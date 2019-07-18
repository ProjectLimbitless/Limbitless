package com.example.limbitlesssummerproject19;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class GalleryActivity extends AppCompatActivity {

    static final int REQUEST_PERMISSION_KEY = 1;
    GridView sessionGallery;

    // List containing image thumbnails for each session folder
    private ArrayList<Bitmap> sessionThumbnails = new ArrayList<Bitmap>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        ContextWrapper cw = new ContextWrapper(getApplicationContext());

        // Open ProstheticFolder directory
        String directoryName = Environment.getExternalStorageDirectory()+File.separator+"ProstheticFolder";
        File countFiles = new File(directoryName);
        File[] files = countFiles.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.isDirectory();
            }
        });

        // Get session thumbnails  (image at first index of each session)
        for ( File f : files ) {
            File[] insideFile = f.listFiles();
            if(insideFile.length != 0){
                sessionThumbnails.add(BitmapFactory.decodeFile(insideFile[0].getAbsolutePath()));
            }
        }

        // Use adapter class as data provider
        sessionGallery = (GridView)findViewById(R.id.galleryGridView);
        GalleryAdapter galleryAdapter = new GalleryAdapter(this, sessionThumbnails);
        sessionGallery.setAdapter(galleryAdapter);
    }


    /**
     * GalleryAdapter
     * Data provider for gallery gridView
     */
    public class GalleryAdapter extends BaseAdapter {

        private final Context mContext;
        private final ArrayList<Bitmap> thumbnails;

        // Constructor
        public GalleryAdapter(Context context, ArrayList<Bitmap> src){
            this.mContext = context;
            this.thumbnails = src;
        }

        @Override
        public int getCount(){
            return thumbnails.size();
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
            iv.setImageBitmap(thumbnails.get(position));
            return iv;
        }
    }


}
