package com.example.limbitlesssummerproject19;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.drm.DrmStore;
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

    // Create an array of albums
    private ArrayList<String> album = new ArrayList<>();
    File sessionFolder;
    String folderName;

    // Alert Dialog
    AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Setting back button to main activity
        ActionBar actionBar = getActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);

        //  Sets the activity album content
        setContentView(R.layout.activity_album);

        // Takes the content of the image
        context = getApplicationContext();

        // Set up recycler view
        albumGallery = findViewById(R.id.recView);
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
        AlbumAdapter albumAdapter = new AlbumAdapter(this, album, folderName);
        albumGallery.setAdapter(albumAdapter);

        // Sets the sendButton and deleteButton
        sendButton = findViewById(R.id.sendDataBtn);
        TextView deleteButton = findViewById(R.id.deleteBtn);

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



    public boolean onOptionsItemSelected(MenuItem item){
        Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);
        startActivityForResult(myIntent, 0);
        return true;
    }

}
