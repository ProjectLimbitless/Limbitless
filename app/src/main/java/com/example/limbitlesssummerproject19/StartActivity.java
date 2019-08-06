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




    }
}
