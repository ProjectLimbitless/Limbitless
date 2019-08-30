package com.example.limbitlesssummerproject19;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;



import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AlbumActivity extends AppCompatActivity {

    // Get Firebase storage references
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();
    Context context;


    //GridView albumGallery;
    RecyclerView albumGallery;
    private Button sendbtn;

    private ArrayList<String> album = new ArrayList<String>();
    File sessionFolder;
    String folderName;



    @Override
    protected void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);
        context = getApplicationContext();

        // Set up recycler view
        albumGallery = (RecyclerView) findViewById(R.id.recView);
        albumGallery.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(context,2);
        albumGallery.setLayoutManager(layoutManager);

        sendbtn = (Button)findViewById(R.id.sendDataBtn);

        Intent intent = getIntent();
        folderName = intent.getStringExtra("fileName");

        // Get file paths of images inside selected session folder
        System.out.println("Open file: " + folderName);
        File childFolder = new File(folderName);
        sessionFolder = new File(childFolder.getParent());
        for ( File i : sessionFolder.listFiles() ) {
            album.add(i.getAbsolutePath());
        }

        // Set recycler view adapter
        AlbumAdapter albumAdapter = new AlbumAdapter(this, album);
        albumGallery.setAdapter(albumAdapter);

        // Push data to firebase storage
        sendbtn.setOnClickListener(new View.OnClickListener() {
            List<Task<Void>> myTasks = new ArrayList<>();
            @Override
            public void onClick(View v) {

                sendbtn.setText("Sending...");
                sendbtn.setBackgroundColor(getResources().getColor(R.color.accentColor));


                for ( String f : album ) {
                    final File image = new File(f);
                    Uri uri = Uri.fromFile(new File(f));
                    StorageReference saveRef = storageRef.child("userSessions/" + sessionFolder.getName() + "/" + image.getName());
                    Task upload = saveRef.putFile(uri);
                    myTasks.add(upload);
                }
                // Notify user when all images have been uploaded
                Tasks.whenAll(myTasks).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        sendbtn.setText("Send to Server");
                        sendbtn.setBackgroundColor(getResources().getColor(R.color.originalBtn));
                        Toast.makeText(context, "Images Sent Successfully!",
                                Toast.LENGTH_LONG).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        sendbtn.setText("Send to Server");
                        sendbtn.setBackgroundColor(getResources().getColor(R.color.originalBtn));
                        Toast.makeText(context, "Error. Images not sent.",
                                Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

    }



    /**
     * AlbumAdapter
     * Data provider for recyclerView
     */
    public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.ViewHolder> {

        private final Context mContext;
        private ArrayList<String> images;

        public AlbumAdapter(Context context, ArrayList<String> galleryList) {
            this.images = galleryList;
            this.mContext = context;
        }

        @Override
        public AlbumAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.single_image, viewGroup, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(AlbumAdapter.ViewHolder viewHolder, int i) {
            viewHolder.img.setScaleType(ImageView.ScaleType.CENTER_CROP);


            Bitmap originalImage = BitmapFactory.decodeFile(images.get(i));



            // Fix rotation of image
            Matrix matrix = new Matrix();
            matrix.postRotate(90);



            Bitmap scaledBitmap = Bitmap.createScaledBitmap(originalImage, originalImage.getWidth(),
                    originalImage.getHeight(), true);


            Bitmap rotatedBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
                    scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);

            // Set image
            viewHolder.img.setImageBitmap(rotatedBitmap);
        }

        @Override
        public int getItemCount() {
            return images.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder{
            private ImageView img;
            public ViewHolder(View view) {
                super(view);
                img = (ImageView) view.findViewById(R.id.img);
            }
        }

//        private final Context mContext;
//        private ArrayList<String> images;
//
//        // Constructor
//        public AlbumAdapter(Context context, ArrayList<String> src){
//            this.mContext = context;
//            this.images = src;
//        }
//
//
//        @Override
//        public int getCount(){
//            return images.size();
//        }
//
//        @Override
//        public long getItemId(int position){
//            return 0;
//        }
//
//        @Override
//        public Object getItem(int position){
//            return null;
//        }
//
//        @Override
//        public View getView(int position, View convertView, ViewGroup parent){
//
//            ImageView iv = new ImageView(mContext);
//
//            Bitmap org = BitmapFactory.decodeFile(images.get(position));
//
//            // Fix rotation of image
//            Matrix matrix = new Matrix();
//            matrix.postRotate(90);
//            Bitmap scaledBitmap = Bitmap.createScaledBitmap(org, org.getWidth(), org.getHeight(),
//                    true);
//            Bitmap rotatedBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
//                    scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
//
//            iv.setImageBitmap(rotatedBitmap);
//            return iv;
//        }

    }
}
