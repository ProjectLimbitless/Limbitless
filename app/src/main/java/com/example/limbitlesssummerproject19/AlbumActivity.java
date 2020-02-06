package com.example.limbitlesssummerproject19;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.load.resource.bitmap.TransformationUtils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.File;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;


public class AlbumActivity extends AppCompatActivity {

    //  Firebase storage references
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();
    Context context;

    // Auth object
    private FirebaseAuth mAuth;

    //  RecyclerView declarations
    RecyclerView albumGallery;
    private Button sendButton;
    private TextView deleteButton;

    // Create an array of albums
    private ArrayList<String> album = new ArrayList<String>();
    File sessionFolder;
    String folderName;

    // Alert Dialog
    AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Setting back button to main activity
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //  Sets the activity album content
        setContentView(R.layout.activity_album);

        // Takes the content of the image
        context = getApplicationContext();

        // Set up recycler view
        albumGallery = (RecyclerView) findViewById(R.id.recView);
        albumGallery.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(context, 2);
        albumGallery.setLayoutManager(layoutManager);

        // We looked through all the images inside the internal storage
        Intent intent = getIntent();
        folderName = intent.getStringExtra("fileName");

        // Get file paths of images inside selected session folder
        sessionFolder = new File(folderName);
        for ( File i : sessionFolder.listFiles() ) {
            album.add(i.getAbsolutePath());

        }

        // Set recycler view adapter
        AlbumAdapter albumAdapter = new AlbumAdapter(this, album);
        albumGallery.setAdapter(albumAdapter);

        // Sets the sendButton and deleteButton
        sendButton = (Button) findViewById(R.id.sendDataBtn);
        deleteButton = (TextView) findViewById(R.id.deleteBtn);

        // Push data to firebase storage
        sendButton.setOnClickListener(new View.OnClickListener() {
            List<Task<Void>> myTasks = new ArrayList<>();

            @Override
            public void onClick(View v) {
                sendButton.setText("Sending...");
                //sendButton.setBackgroundColor(getResources().getColor(R.color.accentColor));

                for (String f : album) {

                    final File image = new File(f);

                    Uri uri = Uri.fromFile(new File(f));

                    mAuth = FirebaseAuth.getInstance();
                    FirebaseUser user = mAuth.getCurrentUser();
                    String username = user.getDisplayName();

                    StorageReference saveRef = storageRef.child("userSessions/" + username + "/"+
                                sessionFolder.getName() + "/" + image.getName());

                    final Task upload = saveRef.putFile(uri);

                    new Thread(new Runnable() {
                        public void run() {
                            //Do whatever
                            myTasks.add(upload);
                        }
                    }).start();
                    //myTasks.add(upload);
                }
                // Notify user when all images have been uploaded
                Tasks.whenAll(myTasks).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        sendButton.setText("Done!");
                        //sendButton.setBackgroundColor(getResources().getColor(R.color.sendColor));
                        Toast.makeText(context, "Images Sent Successfully!",
                                Toast.LENGTH_LONG).show();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //sendButton.setText("ent to Server");
                        //sendButton.setBackgroundColor(getResources().getColor(R.color.sendColor));
                        Toast.makeText(context, "Error. Images not sent.",
                                Toast.LENGTH_LONG).show();
                    }
                });
            }
        });


        // Instantiate alert dialog builder
        builder = new AlertDialog.Builder(this);

        // Delete session from storage
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Chain together various setter methods to set the dialog characteristics
                builder.setMessage(R.string.dialog_message)
                        .setTitle(R.string.dialog_title);

                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK button
                        // Delete all images in folder
                        for (String s : album){
                            File f = new File(s);
                            System.out.println(f.delete());
                        }
                        // Delete folder itself
                        sessionFolder.delete();
                        Intent backToGallery = new Intent(AlbumActivity.this,
                                GalleryActivity.class);
                        startActivity(backToGallery);
                    }
                });
                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
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
            System.out.println("in view holder");

            View view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.single_image, viewGroup, false);

            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(AlbumAdapter.ViewHolder viewHolder, final int position) {

            Glide.with(viewHolder.imageView.getContext())
                    .asBitmap()
                    .load(images.get(position))
                    //.centerCrop()
                    .placeholder(R.drawable.loading_symbol2)
                    .transform(new ImageTransformation(viewHolder.imageView.getContext(),
                            90))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(viewHolder.imageView);


            viewHolder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent= new Intent(context, FullImageActivity.class);
                    intent.putExtra("image_url",images.get(position));
                    intent.putExtra("folderName",folderName);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            });

        }


        @Override
        public int getItemCount() {

            return images.size();
        }

        // Creates a viewHolder for the images inside the albums
        public class ViewHolder extends RecyclerView.ViewHolder {

            private ImageView imageView;

            public ViewHolder(View view) {

                super(view);
                imageView = (ImageView) view.findViewById(R.id.img);

            }


        }

    }

    //  Image transformation from horizontal to vertical (used when working with Glide)
    public class ImageTransformation extends BitmapTransformation {

        private Context context;
        private int mOrientation;

        public ImageTransformation(Context context, int orientation) {
            this.context = context;
            this.mOrientation = orientation;
        }

        @Override
        protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
            int newOrientation = getOrientation(mOrientation);
            return TransformationUtils.rotateImageExif(pool, toTransform, newOrientation);
        }

        //  Sets orientation of the images
        private int getOrientation(int orientation) {
            int newOrientation;
            switch (orientation) {
                case 90:
                    newOrientation = ExifInterface.ORIENTATION_ROTATE_90;
                    break;
                // other cases
                default:
                    newOrientation = ExifInterface.ORIENTATION_NORMAL;
                    break;
            }
            return newOrientation;
        }


        @Override
        public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {

        }
    }




    public boolean onOptionsItemSelected(MenuItem item){
        Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);
        startActivityForResult(myIntent, 0);
        return true;
    }

}
