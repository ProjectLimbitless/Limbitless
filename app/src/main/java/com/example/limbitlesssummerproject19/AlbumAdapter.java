package com.example.limbitlesssummerproject19;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;

/**
 * File: AlbumAdapter.java
 *
 * (Used in AlbumActivty!!!!!)
 *
 * This class is the data provider for recyclerView
 *
 */
public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.ViewHolder>{

    private final Context mContext; /** Should be AlbumActivity context*/
    private ArrayList<String> images; /** list of images */
    private String folderName; /** name of folder to store images*/

    /** Creates a viewHolder for the images inside the albums */
    class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;

        ViewHolder(View view) {

            super(view);
            imageView =  view.findViewById(R.id.img);

        }

    }

    /**
     * Function: Constructor
     * Purpose: create an AlbumAdapter object
     * Parameters: Context context = context of the activity creating this object
     *             ArrayList<String> galleryList = the gallery list
     *             String foldName = name of the folder (album)
     * Return: none
     */
    public AlbumAdapter(Context context, ArrayList<String> galleryList, String foldName) {

        this.images = galleryList;
        this.mContext = context;
        this.folderName = foldName;

    }


    /**
     * Function: onCreateViewHolder()
     * Purpose: creates a new view holder (ViewHolder is defined at bottom of file)
     * Parameters: ViewGroup viewGroup = WHAT IS THIS
     *             int i = WHAT IS THIS
     * Return: none
     */
    @Override
    public AlbumAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.single_image, viewGroup, false);

        return new ViewHolder(view);
    }


    /**
     * Function: onBindViewHolder()
     * Purpose: I wish I knew what this does
     * Parameters: AlbumAdapter.ViewHolder viewHolder = what is this
     *             final int position = what is this
     * Return:
     */
    @Override
    public void onBindViewHolder(AlbumAdapter.ViewHolder viewHolder, final int position) {

        Glide.with(viewHolder.imageView.getContext())
                .asBitmap()
                .load(images.get(position))
                .placeholder(R.drawable.loading_symbol2)
                .transform(new ImageTransformation(90))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(viewHolder.imageView);


        viewHolder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(mContext, FullImageActivity.class);
                intent.putExtra("image_url",images.get(position));
                intent.putExtra("folderName",folderName);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            }
        });

    }


    /**
     * Function: getItemCount()
     * Purpose: get the item count?
     * Parameters: none
     * Return: none
     */
    @Override
    public int getItemCount() {

        return images.size();
    }


}
