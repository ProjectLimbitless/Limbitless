package com.example.limbitlesssummerproject19;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;


public class GalleryActivity extends AppCompatActivity {

    GridView sessionGallery;

    // List of file paths and names of each session folder
    private ArrayList<Pair<String, String>> sessionThumbnails = new ArrayList<Pair<String, String>>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        String directoryName = Environment.getExternalStorageDirectory()+File.separator+"ProstheticFolder";
        final File[] files;

        // Open ProstheticFolder directory
        try {
            File countFiles = new File(directoryName);
            files = countFiles.listFiles(new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    return pathname.isDirectory();
                }
            });

            // Get session thumbnails  (image at first index of each session)
            for ( File f : files ) {
                File[] sessionImages = f.listFiles();
                if(sessionImages.length != 0){
                    Pair newPair = new Pair<>(sessionImages[0].getAbsolutePath(), f.getName());
                    sessionThumbnails.add(newPair);
                }
                else{
                    f.delete(); // Delete empty folders
                }
            }

            // Use adapter class as data provider
            sessionGallery = (GridView)findViewById(R.id.galleryGridView);
            final GalleryAdapter galleryAdapter = new GalleryAdapter(
                    this, sessionThumbnails);
            sessionGallery.setAdapter(galleryAdapter);

            // Open session when a thumbnail is clicked
            sessionGallery.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    Toast.makeText(getApplicationContext(), "Opening Session...",
                            Toast.LENGTH_LONG).show();

                    // Open session images in another activity
                    Intent intent = new Intent(getApplicationContext(), AlbumActivity.class);
                    intent.putExtra("fileName", files[position].getAbsolutePath());
                    startActivity(intent);
                }
            });

        } catch (Exception e){
            Toast.makeText(getApplicationContext(), "No Albums To Display!",
                    Toast.LENGTH_LONG).show();
        }

    }


    /**
     * GalleryAdapter
     * Data provider for gallery gridView
     */
    public class GalleryAdapter extends BaseAdapter {

        private final Context mContext;
        private ArrayList<Pair<String, String>> thumbnails;

        // Constructor
        public GalleryAdapter(Context context, ArrayList<Pair<String, String>> src){
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

            if (convertView == null) {
                final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
                convertView = layoutInflater.inflate(R.layout.single_thumbnail, null);
            }

            final ImageView iv = (ImageView)convertView.findViewById(R.id.thumbnail_image);
            final TextView tv = (TextView)convertView.findViewById(R.id.thumbnail_title);

            Bitmap org = BitmapFactory.decodeFile(thumbnails.get(position).first);
            String title = thumbnails.get(position).second;

            // Fix rotation of image
            Matrix matrix = new Matrix();
            matrix.postRotate(90);
            Bitmap scaledBitmap = Bitmap.createScaledBitmap(org, org.getWidth(), org.getHeight(),
                    true);

            // Crop images into square
            int diff = scaledBitmap.getWidth()-scaledBitmap.getHeight();
            int toSubtract = diff/2;
            Bitmap rotatedBitmap = Bitmap.createBitmap(scaledBitmap, toSubtract, 0,
                    scaledBitmap.getHeight(), scaledBitmap.getHeight(), matrix, true);
            // Set image and title
            iv.setImageBitmap(rotatedBitmap);
            tv.setText(title);

            return convertView;

        }
    }


}
