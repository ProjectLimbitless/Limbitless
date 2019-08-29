package com.example.limbitlesssummerproject19;

import android.content.Context;
import android.os.Bundle;

import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


//import com.squareup.picasso.Picasso;


import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;

public class GalleryActivityTest extends AppCompatActivity {


    private RecyclerView recycleView;
    private ArrayList<Pair<String, String>> sessionThumbnails = new ArrayList<Pair<String, String>>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery_activity_image_grid);

        recycleView = findViewById(R.id.recycle_view);
        StaggeredGridLayoutManager staggeredGridLayoutManager =
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recycleView.setLayoutManager(staggeredGridLayoutManager);


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
                    sessionThumbnails.add(newPair);
                } else {
                    f.delete(); // Delete empty folders
                }
            }


            //Need to do something here for on click listener perhaps

        } catch (Exception e){
            Toast.makeText(getApplicationContext(), "No Albums To Display!",
                    Toast.LENGTH_LONG).show();
        }

        ImageGridAdapter imageGridAdapter = new ImageGridAdapter(this, sessionThumbnails);
        recycleView.setAdapter(imageGridAdapter);


        // Need to create a list or some sort of code that takes the first image of the internal
        // storage inside the device.


    }

    public class ImageGridAdapter extends RecyclerView.Adapter<ImageGridAdapter.GridItemViewHolder> {

        private ArrayList<Pair<String, String>> thumbnails;
        private final Context context;


        public class GridItemViewHolder extends RecyclerView.ViewHolder {

            SquareImageView siv;

            public GridItemViewHolder(View view){
                super(view);
                siv = view.findViewById(R.id.single_image_view);
            }
        }

        public ImageGridAdapter( Context context, ArrayList<Pair< String, String >> imageList){
            this.context = context;
            this.thumbnails = imageList;
        }

        @NonNull
        @Override
        public GridItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

            View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(
                    R.layout.single_thumbnail, null, false);

            return new GridItemViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull GridItemViewHolder holder, int position) {

            final String path = thumbnails.get(position).first;

           /* Picasso.get()
                    .load(path)
                    .resize(250, 250)
                    .centerCrop()
                    .into(holder.siv);

            holder.siv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //handle click event on image
                }
            });*/

        }

        @Override
        public int getItemCount() {
            return thumbnails.size();
        }


    }


    public class SquareImageView extends AppCompatImageView {

        public SquareImageView(Context context){
            super(context);
        }

        public SquareImageView(Context context, AttributeSet attributeSet){
            super(context, attributeSet);
        }

        public SquareImageView(Context context, AttributeSet attributeSet, int defStyleAttribute){
            super(context, attributeSet, defStyleAttribute);
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
            super.onMeasure( widthMeasureSpec , widthMeasureSpec );

            int width = getMeasuredWidth();

            setMeasuredDimension( width, width );
        }

    }
}

