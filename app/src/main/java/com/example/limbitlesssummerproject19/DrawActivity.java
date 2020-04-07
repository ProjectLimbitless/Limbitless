package com.example.limbitlesssummerproject19;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.ImageButton;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.concurrent.atomic.AtomicReference;


/**
 * File: DrawActivity.java
 *
 * This activity controls the function of the drawers on the application.
 *
 */
public class DrawActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private MenuItem setUserName;
    private FirebaseAuth mAuth; /** Auth object */
    private ImageButton btnStart;  /** Declaring btnStart as an instance of Button */
    private ImageButton btnGallery;
    private ImageButton btnTutorial;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);

        /** Set toolbar */
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        /** Set drawer layout */
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        /** Display username of logged-in user */
        Menu menu = navigationView.getMenu();
        setUserName = menu.findItem(R.id.nav_user);
        String username = null;
        try {
            mAuth = FirebaseAuth.getInstance();
            FirebaseUser user = mAuth.getCurrentUser();
            username = user.getDisplayName();
        } catch (Exception e){
            username = "Guest";
        }
        setUserName.setTitle(username);

        /** Set drawer toggle */
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);


        /** Camera button */
        btnStart = findViewById(R.id.btn_to_start);
        btnStart.setOnClickListener( new View.OnClickListener() {

            public void onClick(View view){
                /** On Click start button, we go to the start activity */
                Intent intent = new Intent(DrawActivity.this,
                        CameraActivity.class);
                startActivity(intent);
            }
        });

        /** Gallery Button */
        btnGallery = findViewById(R.id.btn_to_gallery);
        btnGallery.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent intent = new Intent(DrawActivity.this,
                        GalleryActivity.class);
                startActivity(intent);

            }
        });

        /** Tutorial Button */
        btnTutorial = findViewById(R.id.btn_to_tutorial);
        btnTutorial.setOnClickListener( new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DrawActivity.this,
                        TutorialActivity.class);
                startActivity(intent);
            }
        });

    }


    /**
     * Function: onBackPressed()
     * Purpose: Closes drawer when "return" pressed
     * Parameters: none
     * Return: none
     */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    /**
     * Function: onCreateOptionsMenu()
     * Purpose: Adds "settings" to action bar
     * Parameters: Menu menu = the menu to access
     * Return: success code of action
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /** Inflate the menu; this adds items to the action bar if it is present. */
        getMenuInflater().inflate(R.menu.drawer, menu);
        return true;
    }


    /**
     * Function: onOptionsItemSelected()
     * Purpose: Handles onClick on action bar
     * Parameters: MenuItem item = the menu item being selected
     * Return: success code of action
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /**
         * Handle action bar item clicks here. The action bar will
         * automatically handle clicks on the Home/Up button, so long
         * as you specify a parent activity in AndroidManifest.xml. */
        int id = item.getItemId();

        /** noinspection SimplifiableIfStatement */
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * Function: onNavigationItemSelected()
     * Purpose: Handles items selected from drawer (such as logout)
     * Parameters: MenuItem item = the menu item being selected
     * Return: success code of action
     */
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        /** Handle navigation view item clicks here. */
        int id = item.getItemId();

        if (id == R.id.nav_logout) {
            /** Sign out user using firebase */
            FirebaseAuth.getInstance().signOut();
            /** Sign out user using google sign out */
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken("479154573546-l7h3fmcrf3qsn9s4ll76q204egpjschb.apps.googleusercontent.com")
                    .build();
            GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(this, gso);
            googleSignInClient.signOut()
                    .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                        }
                    });
            /** Switch back to login page */
            Intent intent = new Intent(DrawActivity.this,
                    LoginActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}