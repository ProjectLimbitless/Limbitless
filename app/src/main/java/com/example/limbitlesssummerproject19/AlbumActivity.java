package com.example.limbitlesssummerproject19;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;

public class AlbumActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);

        /*Intent intent = getIntent();

        ArrayList<Bitmap> album  = intent.getExtras().get("images");

        GalleryActivity.GalleryAdapter galleryAdapter = new GalleryActivity.GalleryAdapter(this);

                //Intent intent = new Intent(getApplicationContext(), AlbumActivity.class);
                //intent.putExtra("images", album);
        */
    }
}
