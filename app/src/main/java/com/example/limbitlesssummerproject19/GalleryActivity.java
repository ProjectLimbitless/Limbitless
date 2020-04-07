package com.example.limbitlesssummerproject19;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.MenuItem;
import java.io.File;
import java.util.ArrayList;

/**
 * File: GalleryActivity.java
 *
 *
 * This activity controls the gallery function of the app.
 *
 */
public class GalleryActivity extends AppCompatActivity {

    /** Instantiating InternalStorage class */
    InternalStorage internalStorage = new InternalStorage();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        /** Setting back button to main activity */
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);


        /** Creating a list of file from internal storage */
        File[] files = internalStorage.getFilesFromInternalStorage( this );
        ArrayList<Pair<String, String>> sessionFiles = internalStorage.getListFromFiles(this, files);


        /** Creates a grid layout of two columns to display the images */
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);


        /** Uses RecyclerView to inflate the images in the position and recycle the child view */
        RecyclerView adapter = findViewById(R.id.recycle_view);
        adapter.setLayoutManager(gridLayoutManager);
        GalleryAdapter recyclerAdapter = new GalleryAdapter(this, sessionFiles, files);
        adapter.setAdapter(recyclerAdapter);

    }

    /**
     * Function: onOptionsItemSelected()
     * Purpose: Handles onClick on action bar
     * Parameters: MenuItem item = the menu item being selected
     * Return: success code of action
     */
    public boolean onOptionsItemSelected(MenuItem item){
        Intent myIntent = new Intent(getApplicationContext(), DrawActivity.class);
        startActivityForResult(myIntent, 0);
        return true;
    }

}
