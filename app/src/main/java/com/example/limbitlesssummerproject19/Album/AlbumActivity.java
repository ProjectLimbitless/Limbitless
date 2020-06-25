package com.example.limbitlesssummerproject19.Album;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.limbitlesssummerproject19.MainActivity;
import com.example.limbitlesssummerproject19.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import com.example.limbitlesssummerproject19.Album.CopyUtil;

/**
 * File: AlbumActivity.java
 *
 * This activity controls the function of the application when the user views photo albums.?
 *
 */
public class AlbumActivity extends AppCompatActivity {

    FirebaseStorage storage = FirebaseStorage.getInstance(); /**  Firebase storage references */
    StorageReference storageRef = storage.getReference();

    Context context;
    private FirebaseAuth mAuth; /** Auth object */
    RecyclerView albumGallery; /** RecyclerView declaration */
    private Button sendButton;
    private TextView deleteButton;
    private ArrayList<String> album = new ArrayList<>(); /** Create an array of albums */

    File sessionFolder;
    String folderName;
    AlertDialog.Builder builder; /** Alert Dialog */

    ImageButton sendAlbumButton, starAlbumButton, deleteAlbumButton;

    List<Task<Void>> myTasks = new ArrayList<>();

    String sessionName;
    File session;
    String directoryName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        /** Sets the activity album content */
        setContentView(R.layout.activity_album);

        /** Setting back button to main activity */
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        /** Takes the content of the image */
        context = getApplicationContext();


        sendAlbumButton = findViewById(R.id.send_album);
        starAlbumButton = findViewById(R.id.star_album);
        deleteAlbumButton = findViewById(R.id.delete_album);

        sendAlbumButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendAlbum();
            }
        });

        starAlbumButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    starAlbum();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        deleteAlbumButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteAlbum();
            }
        });


        /** Set up recycler view */
        albumGallery = (RecyclerView) findViewById(R.id.recView);
        albumGallery.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(context, 3);
        albumGallery.setLayoutManager(layoutManager);


        /** We looked through all the images inside the internal storage */
        Intent intent = getIntent();

        if(intent.getStringExtra("starredFlag") != null) {
            starAlbumButton.setVisibility(View.INVISIBLE);
        }

        folderName = intent.getStringExtra("fileName");


        /** Get file paths of images inside selected session folder */
        sessionFolder = new File(folderName);
        for ( File i : sessionFolder.listFiles() ) {
            album.add(i.getAbsolutePath());
        }


        /** Set recycler view adapter */
        AlbumAdapter albumAdapter = new AlbumAdapter(this, album, folderName);
        albumGallery.setAdapter(albumAdapter);

        /** Instantiate alert dialog builder */
        builder = new AlertDialog.Builder(this);


    }

    /**
     * Function: onCreateOptionsMenu()
     * Purpose: not sure what the purpose of this is
     * Parameters: Menu menu = the menu to inflate
     * Return: none
     */
    /*public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_items, menu);
        return true;
    }*/

    /** Push data to firebase storage */
    public void sendAlbum() {

        // Create SendToFirebase class !
        sendButton.setText("Sending...");

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
                    /** Do whatever */
                    myTasks.add(upload);
                }
            }).start();
        }


        /** Notify user when all images have been uploaded */
        Tasks.whenAll(myTasks).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                sendButton.setText("Done!");
                Toast.makeText(context, "Images Sent Successfully!",
                        Toast.LENGTH_LONG).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "Error. Images not sent.",
                        Toast.LENGTH_LONG).show();
            }
        });
    }


    /** Delete session from storage */
    public void deleteAlbum(){
        /** Chain together various setter methods to set the dialog characteristics */
        builder.setMessage(R.string.dialog_message)
                .setTitle(R.string.dialog_title);

        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                /** User clicked OK button */
                /** Delete all images in folder */
                for (String s : album){
                    File f = new File(s);
                    System.out.println(f.delete());
                }
                /** Delete folder itself */
                sessionFolder.delete();
                Intent backToGallery = new Intent(AlbumActivity.this,
                        GalleryActivity.class);
                startActivity(backToGallery);
                finish();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                /** User cancelled the dialog */
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void starAlbum() throws IOException {
        sessionFolder = new File(folderName);
        createDirectory();
        CopyUtil.copyDirectory(sessionFolder, session);
        Toast.makeText(context, "Album is starred now!", Toast.LENGTH_LONG).show();
    }

    public void  createDirectory(){

        directoryName = Environment.getExternalStorageDirectory() +
                File.separator + "StarredProstheticFolder";
        File directory = new File( directoryName );

        if( !directory.exists() ) {
            directory.mkdirs();
        }

        // "yyyy_mm_dd_hh_mm"//
        // Get current date
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM_dd_yyyy_hh_mm",
                Locale.getDefault());
        sessionName = directoryName + File.separator + dateFormat.format(new Date());

        // Save photos
        session = new File(sessionName);
        if(!session.exists()) {
            session.mkdirs();
        }
    }



    /**
     * Function: onOptionsItemSelected()
     * Purpose: not sure what this does
     * Parameters: MenuItem item = the item on the menu? :(
     * Return: none
     */
    public boolean onOptionsItemSelected(MenuItem item) {
/*
        switch (item.getItemId()) {
            case R.id.return_button:
                startActivity(new Intent(this, GalleryActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }*/
        Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);
        startActivityForResult(myIntent, 0);
        return true;
    }
}
