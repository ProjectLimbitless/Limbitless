package com.example.limbitlesssummerproject19.Album;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Pair;
import android.view.MenuItem;
import com.example.limbitlesssummerproject19.MainActivity;
import com.example.limbitlesssummerproject19.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

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

    /**
     * Instantiating InternalStorage class
     */
    //InternalStorage internalStorage = new InternalStorage();

    private FragmentAllAlbums allAlbumsFragment;
    private FragmentStarredAlbums starredAlbumsFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        // Setting back button to main activity
        ActionBar actionBar = getSupportActionBar();
        //assert actionBar != null;
        //actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Gallery");


        ViewPager2 viewPager2 = findViewById(R.id.viewpage);
        viewPager2.setAdapter(new GalleryPagerAdapter(this));

        TabLayout tabLayout = findViewById(R.id.tablayout);
        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tabLayout, viewPager2, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                switch(position) {
                    case 0: {
                        tab.setText("All");
                        break;
                    }
                    case 1: {
                        tab.setText("Starred");
                        break;
                    }
                }
            }
        });
        tabLayoutMediator.attach();


    }


    /**
     * Function: onOptionsItemSelected()
     * Purpose: Handles onClick on action bar
     * Parameters: MenuItem item = the menu item being selected
     * Return: success code of action
     */
    /*
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);
        startActivityForResult(myIntent, 0);
        return true;
    }*/
}
