package com.example.limbitlesssummerproject19;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.io.File;
import java.util.ArrayList;

public class AlbumActivity extends AppCompatActivity {

    GridView albumGallery;
    private ArrayList<String> album = new ArrayList<String>();


    @Override
    protected void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);

        albumGallery = (GridView)findViewById(R.id.albumGridView);

        Intent intent = getIntent();
        String fileName = intent.getStringExtra("fileName");

        // Get file paths of images inside selected session
        File openFile = new File(fileName);
        for ( File i : openFile.listFiles() ) {
            album.add(i.getAbsolutePath());
        }

        AlbumAdapter albumAdapter = new AlbumAdapter(this, album);
        albumGallery.setAdapter(albumAdapter);

    }


    /**
     * AlbumAdapter
     * Data provider for album gridView
     */
    public class AlbumAdapter extends BaseAdapter {

        private final Context mContext;
        private ArrayList<String> images;

        // Constructor
        public AlbumAdapter(Context context, ArrayList<String> src){
            this.mContext = context;
            this.images = src;
        }


        @Override
        public int getCount(){
            return images.size();
        }

        @Override
        public long getItemId(int position){
            return 0;
        }

        @Override
        public Object getItem(int position){
            return null;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent){

            ImageView iv = new ImageView(mContext);

            Bitmap org = BitmapFactory.decodeFile(images.get(position));

            // Fix rotation of image
            Matrix matrix = new Matrix();
            matrix.postRotate(90);
            Bitmap scaledBitmap = Bitmap.createScaledBitmap(org, org.getWidth(), org.getHeight(),
                    true);
            Bitmap rotatedBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
                    scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);

            iv.setImageBitmap(rotatedBitmap);
            return iv;
        }

    }
}
