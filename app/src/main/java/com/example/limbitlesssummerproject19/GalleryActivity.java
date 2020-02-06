package com.example.limbitlesssummerproject19;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.media.ExifInterface;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.load.resource.bitmap.TransformationUtils;
import java.io.File;
import java.io.FileFilter;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;


public class GalleryActivity extends AppCompatActivity {

    private ArrayList<Pair<String, String>> sessionFiles = new ArrayList<>();
    public File[] files;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //  Sets the content of the gallery activity
        setContentView(R.layout.activity_gallery);

        //  Sets the recycle_view content
        RecyclerView adapter = findViewById(R.id.recycle_view);

        //  Creates a grid layout of two columns
        GridLayoutManager gridLayoutManager = new GridLayoutManager(
                GalleryActivity.this, 2);

        // Sets the grid layout into the recycler view
        adapter.setLayoutManager(gridLayoutManager);

        // Obtains the directory name
        String directoryName = Environment.getExternalStorageDirectory() + File.separator +
                "ProstheticFolder";

        // Open ProstheticFolder directory and obtaining the first image of the array to
        // be displayed in the gallery
        try {

            File countFiles = new File(directoryName);

            files = countFiles.listFiles(new FileFilter() {

                @Override
                public boolean accept(File pathname) {
                    return pathname.isDirectory();

                }
            });


            if (files != null && files.length > 1) {
                Arrays.sort(files, new Comparator<File>() {
                    public int compare(File o1, File o2) {
                        long lastModifiedO1 = o1.lastModified();
                        long lastModifiedO2 = o2.lastModified();

                        return lastModifiedO2 < lastModifiedO1 ? -1 : 0;
                    }
                });
            } else {

                Toast.makeText(getApplicationContext(), "No Albums To Display!",
                        Toast.LENGTH_LONG).show();
            }

            // Get session thumbnails ( image at first index of each session )
            for (File file : files) {

                File[] sessionImages = file.listFiles();


                if (sessionImages.length != 0) {

                    Pair<String, String> newPair = new Pair<>(sessionImages[0].getAbsolutePath(), file.getName());
                    sessionFiles.add(newPair);

                } else {

                    file.delete(); // Delete empty folders
                }
            }

            //  Uses the recycler adapter and glide
            RecyclerAdapter recyclerAdapter = new RecyclerAdapter(
                    GalleryActivity.this,
                    sessionFiles);

            //   Sets images into recycler view
            adapter.setAdapter(recyclerAdapter);


        } catch (Exception e) {

            Toast.makeText(getApplicationContext(), "No Albums To Display!",
                    Toast.LENGTH_LONG).show();
        }


    }



    public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.PlaceViewHolder> {

        // Defines member variables
        private Context mContext;
        private ArrayList<Pair<String, String>> mSessionImage;

        //  Creates a constructor for the gallery
        RecyclerAdapter(Context mContext, ArrayList<Pair<String, String>> mImageList) {

            this.mContext = mContext;
            this.mSessionImage = mImageList;
        }


        @NonNull
        @Override
        //  Placing a view into and imageview using a holder ang attaches imageView to the parent
        public PlaceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout
                    .single_image_view_gallery, parent, false);

            return new PlaceViewHolder(view);
        }

        @Override
        public void onBindViewHolder(PlaceViewHolder holder,  final int position) {

            //  Glide is a open source library that allows a bitmap to be uploaded into
            //  an imageView without having out of memory problems. It also allows for smooth
            // scrolling and image transformation.

            Glide.with(holder.mImageView.getContext())
                    .asBitmap()
                    .load(mSessionImage.get(position).first)
                    //.centerCrop()
                    .placeholder(R.drawable.loading_symbol2)
                    .transform(new ImageTransformation(holder.mImageView.getContext(), 90))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.mImageView);


            // Opens the Album activity
            holder.mImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(mContext, AlbumActivity.class);
                    intent.putExtra("fileName", files[position].getAbsolutePath());
                    startActivity(intent);
                    finish();

                }
            });



            //  Sets the text just down below the image view\

            String title = mSessionImage.get(position).second;
            String[] parts = title.split("_"); // Format title string and set title
            String newTitle = parts[0] + "/" + parts[1] + "/" + parts[2] + " " + parts[3]
                    + ":" + parts[4];
            holder.sessionTitle.setText(newTitle);

        }

        @Override
        public int getItemCount() {

            return mSessionImage.size();
        }

        //  View placed on an a place holder and sets ImageView on single_session_view
        class PlaceViewHolder extends RecyclerView.ViewHolder {

            ImageView mImageView;
            TextView sessionTitle;

            PlaceViewHolder(View itemView) {
                super(itemView);

                mImageView = itemView.findViewById(R.id.single_session_view);
                sessionTitle = itemView.findViewById(R.id.session_title);
            }
        }


    }

    //  Image transformation from horizontal to vertical (used when working with Glide)
    public class ImageTransformation extends BitmapTransformation {

        private int mOrientation;

        ImageTransformation(Context context, int orientation) {
            this.mOrientation = orientation;
        }

        @Override
        protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
            int newOrientation = getOrientation(mOrientation);
            return TransformationUtils.rotateImageExif(pool, toTransform, newOrientation);
        }

        //  Sets orientation
        private int getOrientation(int orientation) {

            if (orientation == 90) {
                 return ExifInterface.ORIENTATION_ROTATE_90;
                // other cases
            } else {
                 return ExifInterface.ORIENTATION_NORMAL;
            }
        }


        @Override
        public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {

        }
    }


    //  ConCreateOptionMenu and onOptionItemsSelected create a return button
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_items, menu);



        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.return_button) {
            startActivity(new Intent(this, DrawerActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
