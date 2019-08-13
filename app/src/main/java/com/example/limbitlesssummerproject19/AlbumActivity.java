package com.example.limbitlesssummerproject19;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
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


    GridView albumGallery;
    private Button sendbtn;

    private ArrayList<String> album = new ArrayList<String>();
    File sessionFolder;


    @Override
    protected void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);

        albumGallery = (GridView)findViewById(R.id.albumGridView);
        sendbtn = (Button)findViewById(R.id.sendDataBtn);

        Intent intent = getIntent();
        final String folderName = intent.getStringExtra("fileName");

        // Get file paths of images inside selected session folder
        sessionFolder = new File(folderName);
        for ( File i : sessionFolder.listFiles() ) {
            album.add(i.getAbsolutePath());
        }

        // Set gridView adapter
        AlbumAdapter albumAdapter = new AlbumAdapter(this, album);
        albumGallery.setAdapter(albumAdapter);

        // Push data to firebase storage
        sendbtn.setOnClickListener(new View.OnClickListener() {
            List<Task<Void>> myTasks = new ArrayList<>();
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Sending...",
                        Toast.LENGTH_LONG).show();
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
                        Toast.makeText(getApplicationContext(), "Images Sent Successfully!",
                                Toast.LENGTH_LONG).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Error. Images not sent.",
                                Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

    }

    //System.out.println("Successfully uploaded " + image.getName());


    /**
     * AlbumAdapter
     * Data provider for album gridView
     */
    public class AlbumAdapter extends BaseAdapter {

        private final Context mContext;
        private ArrayList<String> images;

        // Constructor
        public AlbumAdapter(Context context, ArrayList<String> src){
            this.mContext = context;
            this.images = src;
        }


        @Override
        public int getCount(){
            return images.size();
        }

        @Override
        public long getItemId(int position){
            return 0;
        }

        @Override
        public Object getItem(int position){
            return null;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent){

            ImageView iv = new ImageView(mContext);

            Bitmap originalImage = BitmapFactory.decodeFile(images.get(position));


            // Fix rotation of image
            Matrix matrix = new Matrix();
            matrix.postRotate(90);



            Bitmap scaledBitmap = Bitmap.createScaledBitmap(originalImage, originalImage.getWidth(),
                    originalImage.getHeight(), true);


            Bitmap rotatedBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
                    scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);

            iv.setAdjustViewBounds(true);

            iv.setImageBitmap(rotatedBitmap);
            return iv;
        }

    }
}
