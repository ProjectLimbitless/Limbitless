package com.example.limbitlesssummerproject19;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.limbitlesssummerproject19.Album.GalleryActivity;
import com.example.limbitlesssummerproject19.Camera.CameraActivity;
import com.example.limbitlesssummerproject19.Login.LoginActivity;
import com.example.limbitlesssummerproject19.Tutorial.TutorialActivity;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.sql.SQLOutput;


/**
 * UPDATE: Functionality moved to DrawActivity! This activity is essentially never executed
 * Main Activity is the first window that opens in the device.
 */


public class MainActivity extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {

    Intent intent = null;
    public static final String TAG = "MainActicity: ";

    FirebaseAuth mAuth;

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    Toolbar toolbar;
    NavigationView navigationView;

    //header content
    private View headerView;
    private TextView mUserEmail;

    //drawer content
    private MenuItem mProfileItem;
    private MenuItem mLogoutItem;

    private int profile_index = 0;
    private int logout_index = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

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

        headerView = navigationView.getHeaderView(0);
        mUserEmail = headerView.findViewById(R.id.email_text_view);

        navigationView.setNavigationItemSelectedListener(this);


        /*navigationView.getMenu().findItem(R.id.drawer_logout).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                userLogOut();
                return false;
            }
        });*/
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
        if(drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }

        return;
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user = mAuth.getCurrentUser();

        if(user == null) {
            mUserEmail.setText("Guest User");
        } else {
            String user_email = user.getEmail();
            mUserEmail.setText(user_email);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()) {
            case R.id.drawer_logout:
                userLogOut();
                break;

            case R.id.drawer_profile:
                startProfileActivity();
                break;
        }

        closeDrawer();

        return true;
    }

    private void startProfileActivity() {
        startActivity(new Intent(MainActivity.this, ProfileActivity.class));
        //finish();
        overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
    }

    private void userLogOut() {
        mAuth.signOut();
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
        finish();
        overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
    }

    private void closeDrawer() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }
}