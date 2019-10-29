package com.example.limbitlesssummerproject19;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ExifInterface;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.style.TextAppearanceSpan;
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
import java.io.Serializable;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;


public class GalleryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ArrayList<Pair<String, String>> sessionFiles = new ArrayList<Pair<String, String>>();
    public File[] files;
    private String directoryName;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //  Sets the content of the gallery activity
        setContentView(R.layout.activity_gallery);

        //  Sets the recycle_view content
        recyclerView = findViewById(R.id.recycle_view);

        //  Creates a grid layout of two columns
        GridLayoutManager gridLayoutManager = new GridLayoutManager(
                GalleryActivity.this, 2);

        // Sets the grid layout into the recycler view
        recyclerView.setLayoutManager(gridLayoutManager);

        // Obtains the directory name
        directoryName = Environment.getExternalStorageDirectory() + File.separator +
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


            /**
             * We are using a comparator to compare files inside the folder. By doing so, we
             * can sort the files based on the last Modified. Last Modified means the newest file
             * saved with a time in milliseconds since *epoch* 1970. Hence, larger
             * the number, the newest a file is. Code below compares the time of files they were
             * created and sorts it from newest to oldest in a descending order. Using
             * history to do the following code. Cool right?
             */
            if (files != null && files.length > 1) {
                Collections.sort(Arrays.asList(files), new Comparator<File>() {
                    public int compare(File o1, File o2) {
                        long lastModifiedO1 = o1.lastModified();
                        long lastModifiedO2 = o2.lastModified();

                        return (lastModifiedO2 < lastModifiedO1) ? -1 :
                                ((lastModifiedO1 > lastModifiedO2) ? 1 : 0);
                    }
                });
            } else {

                Toast.makeText(getApplicationContext(), "No Albums To Display!",
                        Toast.LENGTH_LONG).show();
            }

            // Get session thumbnails ( image at first index of each session )
            for (File f : files) {

                File[] sessionImages = f.listFiles();


                if (sessionImages.length != 0) {

                    Pair newPair = new Pair<>(sessionImages[0].getAbsolutePath(), f.getName());
                    sessionFiles.add(newPair);

                } else {

                    f.delete(); // Delete empty folders
                }
            }

            //  Uses the recycler adapter and glide
            RecyclerAdapter recyclerAdapter = new RecyclerAdapter(
                    GalleryActivity.this,
                    sessionFiles);

            //   Sets images into recycler view
            recyclerView.setAdapter(recyclerAdapter);


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
        public RecyclerAdapter(Context mContext, ArrayList<Pair<String, String>> mImageList) {

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
        public void onBindViewHolder(PlaceViewHolder holder, final int position) {

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

                    Toast.makeText(mContext, "Preparing Session...", Toast.LENGTH_LONG).show();

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
        public class PlaceViewHolder extends RecyclerView.ViewHolder {

            ImageView mImageView;
            TextView sessionTitle;

            public PlaceViewHolder(View itemView) {
                super(itemView);

                mImageView = itemView.findViewById(R.id.single_session_view);
                sessionTitle = itemView.findViewById(R.id.session_title);
            }
        }


    }

    //  Image transformation from horizontal to vertical (used when working with Glide)
    public class ImageTransformation extends BitmapTransformation {

        private Context context;
        private int mOrientation;

        public ImageTransformation(Context context, int orientation) {
            this.context = context;
            this.mOrientation = orientation;
        }

        @Override
        protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
            int newOrientation = getOrientation(mOrientation);
            return TransformationUtils.rotateImageExif(pool, toTransform, newOrientation);
        }

        //  Sets orientation
        private int getOrientation(int orientation) {
            int newOrientation;
            switch (orientation) {
                case 90:
                    newOrientation = ExifInterface.ORIENTATION_ROTATE_90;
                    break;
                // other cases
                default:
                    newOrientation = ExifInterface.ORIENTATION_NORMAL;
                    break;
            }
            return newOrientation;
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

        switch (item.getItemId()) {
            case R.id.return_button:
                startActivity(new Intent(this, MainActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
