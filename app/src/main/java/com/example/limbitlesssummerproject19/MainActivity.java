package com.example.limbitlesssummerproject19;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;


/**
 * UPDATE: Functionality moved to DrawerActivity! This activity is essentially never executed
 * Main Activity is the first window that opens in the device.
 */


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

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
        btnTutorial.setOnClickListener( this);

    }

    @Override
    public void onClick(View view) {

        Intent intent;

        switch (view.getId()){
            case R.id.btn_to_start:

                intent = new Intent(MainActivity.this, CameraActivity.class);
                this.startActivity(intent);
                break;

            case R.id.btn_to_gallery:

                Toast.makeText(getApplicationContext(), "Preparing Gallery...",
                        Toast.LENGTH_SHORT).show();
                intent = new Intent(MainActivity.this, GalleryActivity.class);
                this.startActivity(intent);
                break;

            case R.id.btn_to_tutorial:

                Toast.makeText(getApplicationContext(),"Tutorial Not Accessible",
                        Toast.LENGTH_SHORT).show();
                break;
        }
    }
}