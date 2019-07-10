package com.example.limbitlesssummerproject19;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

/**
 * Start Activity is opened when the Start button is pressed. Here, two new buttons are showed:
 * Gallery button which opens the gallery activity, and Camera button which opens native camera
 * inside the phone.
 */

public class StartActivity extends AppCompatActivity {

    private Button btnGallery, btnCamera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        /*
         *  Creates gallery button and a listener is added
         */

        btnGallery = (Button) findViewById(R.id.btn_to_gallery);
        btnGallery.setOnClickListener(new View.OnClickListener() {

            /*
             * On click, the button opens Gallery Activity. (Gallery Activity is still on work)
             */

            public void onClick(View view) {
                Intent intent = new Intent(StartActivity.this,
                        GalleryActivity.class);
                startActivity(intent);
            }
        });

        /*
         * Creates a Burst Mode button and a listener is added.
         */

        btnCamera = (Button) findViewById(R.id.CameraButton);
        btnCamera.setOnClickListener( new View.OnClickListener(){
            /*
             * Upon click, the button opens CameraActivity.
             */
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StartActivity.this,
                        CameraActivity.class);
                startActivity(intent);
            }
        });




    }
}
