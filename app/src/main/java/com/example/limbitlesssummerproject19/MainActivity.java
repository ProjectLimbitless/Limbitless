package com.example.limbitlesssummerproject19;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.google.android.material.navigation.NavigationView;


/**
 * UPDATE: Functionality moved to DrawActivity! This activity is essentially never executed
 * Main Activity is the first window that opens in the device.
 */


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Intent intent = null;
    public static final String TAG = "MainActicity: ";

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    Toolbar toolbar;
    NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // initializing variables
        ImageButton btnStart = findViewById(R.id.btn_to_start);
        ImageButton btnGallery = findViewById(R.id.btn_to_gallery);
        ImageButton btnTutorial = findViewById(R.id.btn_to_tutorial);

        // set listener
        btnStart.setOnClickListener(this);
        btnGallery.setOnClickListener(this);
        btnTutorial.setOnClickListener(this);

        drawerLayout = findViewById(R.id.drawer_id);
        toolbar = findViewById(R.id.toolbar);
        navigationView = findViewById(R.id.navigation_view);

        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onClick(View view) {



        switch (view.getId()) {
            case R.id.btn_to_start:
                Log.d(TAG, "btn start called");


                intent = new Intent(MainActivity.this, CameraActivity.class);
                this.startActivity(intent);
                break;

            case R.id.btn_to_gallery:

                Log.d(TAG, "btn gallery called");

                intent = new Intent(MainActivity.this, GalleryActivity.class);
                this.startActivity(intent);
                break;

            case R.id.btn_to_tutorial:

                Log.d(TAG, "btn tutorial called");

                intent = new Intent(MainActivity.this, TutorialActivity.class);
                this.startActivity(intent);
                break;
        }

    }

    @Override
    public void onBackPressed() {
        return;
    }
}