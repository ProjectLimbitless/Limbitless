package com.example.limbitlesssummerproject19;
//package com.androstock.galleryapp;

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
import android.widget.ArrayAdapter;
import android.widget.GridView;
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
    //LoadAlbum loadAlbumTast;
    GridView sessionGallery;

    // List containing paths to each session (directory)
    ArrayList<HashMap<String, String>> albumList = new ArrayList<HashMap<String, String>>();

    private String[] sessionThumbnails = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        ContextWrapper cw = new ContextWrapper(getApplicationContext());


        String directoryName = Environment.getExternalStorageDirectory()+File.separator+"ProstheticFolder";
        File countFiles = new File(directoryName);
        File[] files = countFiles.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.isDirectory();
            }
        });
        System.out.println("In gallery activity" + Arrays.toString(files));

        // Get session thumbnails
        for ( File f : files ) {
            System.out.println("curr file " + f.toString());
            System.out.println("contents " + Arrays.toString(f.listFiles()));

        }
        //sessionGallery.setAdapter(new ArrayAdapter<String>(cw, Arrays.toString(files)));
    }

    /** displayAlbum
     *  Display all sessions in the gallery
     *  
     */

    /** displayImages
     *  Display all images in user selected session
     */

    /**
      * saveImage
      * Temporary method to save dummy images to internal storage

    private void saveImage(int id) {

        // FOR INTERNAL STORAGE
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File dir = cw.getDir("Limbitless", Context.MODE_PRIVATE);
        File myImage = new File(dir, "image1");

        // Get the image from drawable resource as drawable object
        Drawable drawable = getDrawable(id);

        // Get the bitmap from drawable object
        Bitmap bitmap = ((BitmapDrawable)drawable).getBitmap();

        OutputStream os = null;

        try {
            os = new FileOutputStream(myImage);
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,os);
            os.flush();
            os.close();
            System.out.println("Image Saved Successfully " + id);
        } catch (Exception e) {
            e.printStackTrace();
        }*/


        /*
        File imageDirectory;
        String storageState = Environment.getExternalStorageState();
        if (storageState.equals(Environment.MEDIA_MOUNTED)) {
            imageDirectory = new File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                    "ProstheticFolder");
            if (!imageDirectory.exists() && !imageDirectory.mkdirs()) {
                imageDirectory = null;
            } else {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy_mm_dd_hh_mm",
                        Locale.getDefault());

                return new File(imageDirectory.getPath() +
                        File.separator + "image_" +
                        dateFormat.format(new Date()) + ".png");
            }
        }
        return null;
    }*/







    /*
     * SAVES THE BITMAP DATA OF THE PICTURE INTO A FILE USING THE PATH FROM openFileForImage
     * FUNCTION. THIS COMPLETES SAVING THE IMAGE INTO THE PHONE.


    private void saveToInternalStorage() {

        File mypath = openFileForImage();
        FileOutputStream fos;

        try {
            fos = new FileOutputStream(mypath);

            Bitmap bmp = BitmapFactory.decodeByteArray(mCameraData, 0,
                    mCameraData.length);
            Matrix matrix = new Matrix();
            if (android.os.Build.VERSION.SDK_INT < 9) {
                matrix.postRotate(90);
            } else {
               // int orientation = determineDisplayOrientation();
                //matrix.postRotate(orientation);
            }
            bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(),
                    bmp.getHeight(), matrix, true);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
            Toast.makeText(getApplicationContext(), "Image was saved!",
                    Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }*/
}
