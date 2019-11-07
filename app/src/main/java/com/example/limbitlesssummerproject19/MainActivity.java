package com.example.limbitlesssummerproject19;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;




/**
 * Main Activity is the first window that opens in the device. This window has two buttons: Start
 * and Tutorial. However, Tutorial has not yet been implemented, only Start button.
 *
 */


public class MainActivity extends AppCompatActivity {
    private ImageButton btnStart;                        //Declaring btnStart as an instance of Button
    private ImageButton btnGallery;
    private ImageButton btnTutorial;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /**
         * Display username of logged-in user

        String username = getIntent().getStringExtra("username");*/


        /**
         *  This is the continuation of the code.
         */
        //Create a buttons and adds a listener to it
        btnStart = findViewById(R.id.btn_to_start);
        btnStart.setOnClickListener( new View.OnClickListener() {

            //Upon clicking the button, a new window will open called StartActivity

            public void onClick(View view){

                //On Click start button, we go to the start activity
                Intent intent = new Intent(MainActivity.this,
                        CameraActivity.class);
                startActivity(intent);
            }
        });



        btnGallery = findViewById(R.id.btn_to_gallery);
        btnGallery.setOnClickListener(new View.OnClickListener() {

            //
            // On click, the button opens Gallery Activity. (Gallery Activity is still on work)
            //

            public void onClick(View view) {

                Toast.makeText(getApplicationContext(), "Preparing Gallery...",
                        Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(MainActivity.this,
                        GalleryActivity.class);
                startActivity(intent);

            }
        });

        /*
         * Creates a Burst Mode button and a listener is added.
         */

       btnTutorial = findViewById(R.id.btn_to_tutorial);
       btnTutorial.setOnClickListener( new View.OnClickListener(){
            /*
             * Upon click, the button opens CameraActivity.
             */

            @Override
            public void onClick(View v) {

                Toast.makeText(getApplicationContext(),"Tutorial Not Accessible",
                        Toast.LENGTH_SHORT).show();

            }
        });

    }

}