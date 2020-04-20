package com.example.limbitlesssummerproject19.Album;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.limbitlesssummerproject19.R;

/**
 * File: FullImageActivity.java
 *
 *
 * FullImageActivity class works in conjunction with Album Activity. Upon pressing a single
 * image inside album activity, a new activity opens up and inflates the window with the image
 * selected. The image cannot be removed from the list, and there is a back button to go back to
 * the album activity
 *
 */
public class FullImageActivity extends AppCompatActivity {

    ImageView imageView;
    String singleImageURL = "";
    String backtrackingURL = "";
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullimage);

        /** single image gets displayed into the user */
        singleImageURL = getIntent().getStringExtra("image_url");

        /** saves the absolute path of the images */
        backtrackingURL = getIntent().getStringExtra("folderName");

        /** displays the image to the user */
        imageView = findViewById(R.id.my_image);

        Glide.with(this)
                .load(singleImageURL)
                .centerCrop()
                .transform(new ImageTransformation(90))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView);

    }


    /**
     * Function: onCreateOptionsMenu()
     * Purpose: Adds "settings" to action bar
     * Parameters: Menu menu = the menu to access
     * Return: success code of action
     */
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.menu_items, menu);

        return true;
    }


    /**
     * Function: onOptionsItemSelected()
     * Purpose: Handles onClick on action bar
     * Parameters: MenuItem item = the menu item being selected
     * Return: success code of action
     */
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            /** Return to the album activity using the previous URL (backtracking steps) */
            case R.id.return_button:

                Intent intent = new Intent(this, AlbumActivity.class);

                intent.putExtra("fileName", backtrackingURL);

                this.startActivity(intent);

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

