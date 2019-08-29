package com.example.limbitlesssummerproject19;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;

import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.Placeholder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.GridLayoutManager;

import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


//import com.squareup.picasso.Picasso;


import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;

public class GalleryActivityTest extends AppCompatActivity {


    private RecyclerView recycleView;
    private ArrayList<Pair<String, String>> sessionFiles = new ArrayList<Pair<String, String>>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery_activity_test);

        recycleView = findViewById(R.id.recycle_view);
       GridLayoutManager gridLayoutManager =
                new GridLayoutManager(GalleryActivityTest.this, 2);
        recycleView.setLayoutManager(gridLayoutManager);


        String directoryName = Environment.getExternalStorageDirectory() +
                File.separator+"ProstheticFolder";

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
            for (File f : files) {
                File[] sessionImages = f.listFiles();
                if (sessionImages.length != 0) {
                    Pair newPair = new Pair<>(sessionImages[0].getAbsolutePath(), f.getName());
                    sessionFiles.add(newPair);
                } else {
                    f.delete(); // Delete empty folders
                }
            }


            RecyclerAdapter recyclerAdapter = new RecyclerAdapter(GalleryActivityTest.this, sessionFiles);
            recycleView.setAdapter(recyclerAdapter);


            //Need to do something here for on click listener perhaps

        } catch (Exception e){
            Toast.makeText(getApplicationContext(), "No Albums To Display!",
                    Toast.LENGTH_LONG).show();
        }


    }

    public static class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.PlaceViewHolder> {


        private Context mContext;
        private ArrayList<Pair<String,String>> mSessionImage;

        //  Creating a constructor for the gallery
        public RecyclerAdapter(Context mContext, ArrayList<Pair<String,String>> mImageList){

            this.mContext = mContext;
            this.mSessionImage = mImageList;
        }


        @NonNull
        @Override
        //  Placing a view into and imageview using a holder
        public PlaceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.gallery_single_image_view_test,
                    parent, false);

            return new PlaceViewHolder(view);
        }

        @Override
        public void onBindViewHolder(PlaceViewHolder holder, int position) {

            /*
             *  Decoding the image from the mSessionImage and placing it into the imageView
             *  This is important to do since the image has to placed well
             *
             */

            Bitmap originalImage = BitmapFactory.decodeFile(mSessionImage.get(position).first);

            // Fix rotation of image
            Matrix matrix = new Matrix();
            matrix.postRotate(90);
            Bitmap scaledBitmap = Bitmap.createScaledBitmap(originalImage, originalImage.getWidth(), originalImage.getHeight(),
                    true);

            // Crop images into square
            int diff = scaledBitmap.getWidth()-scaledBitmap.getHeight();
            int toSubtract = diff/2;
            Bitmap rotatedBitmap = Bitmap.createBitmap(scaledBitmap, toSubtract, 0,
                    scaledBitmap.getHeight(), scaledBitmap.getHeight(), matrix, true);


            //  Glide is a open source library that allows a bitmap to be uploaded into
            //  an imageView without having OOM problems. It also allows for smooth scrolling
            Glide.with(holder.mImageView.getContext()).load(bitMapToByte(rotatedBitmap)).into(holder.mImageView);


            // This is a of the to the imageView - opens the Album activity
            holder.mImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Toast.makeText(null, "Works!",Toast.LENGTH_SHORT).show();

                }
            });


            /*
             *  Setting the text just down below the image view
             *
             */
            String title = mSessionImage.get(position).second;
            // Format title string and set title
            String[] parts = title.split("_");
            String newTitle = parts[0]+"/"+parts[1]+"/"+parts[2]+" "+parts[3]+":"+parts[4];
            holder.sessionTitle.setText(newTitle);

        }

        // Changes the rotated image into a byteArray
        public byte[] bitMapToByte(Bitmap bitmap){
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            return byteArray;
        }

        @Override
        public int getItemCount() {
            return mSessionImage.size();
        }

        //  The view is placed on an a place holder sets and ImageView on single_session_view
        public static class PlaceViewHolder extends RecyclerView.ViewHolder {

            ImageView mImageView;
            TextView sessionTitle;

            public PlaceViewHolder(View itemView) {
                super(itemView);

                mImageView = itemView.findViewById(R.id.single_session_view);
                sessionTitle = itemView.findViewById(R.id.session_title);
            }
        }


    }

}

