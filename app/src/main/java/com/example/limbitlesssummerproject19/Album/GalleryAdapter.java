package com.example.limbitlesssummerproject19.Album;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.limbitlesssummerproject19.R;

import java.io.File;
import java.util.ArrayList;

/**
 * File: GalleryAdapter.java
 *
 * (Used in GalleryActivty!!!!!)
 *
 * This class is the data provider for recyclerView
 *
 */
public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder> {

    /** Defines member variables */
    private Context mContext;
    private ArrayList<Pair<String, String>> mSessionImage;
    private File[] files;

    /** Creates a constructor for the gallery */
    GalleryAdapter(Context mContext, ArrayList<Pair<String, String>> mImageList, File[] files) {

        this.mContext = mContext;
        this.mSessionImage = mImageList;
        this.files = files;
    }


    /** View placed on an a place holder and sets ImageView on single_session_view */
    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView mImageView;
        TextView sessionTitle;

        ViewHolder(View itemView) {
            super(itemView);

            this.mImageView = itemView.findViewById(R.id.single_session_view);
            this.sessionTitle = itemView.findViewById(R.id.session_title);
        }
    }


    /**
     * Function: onCreateViewHolder()
     * Purpose: Placing a view into an imageview using a holder ang attaches imageView to the parent
     * Parameters: ViewGroup parent = WHAT IS THIS
     *             int viewType = WHAT IS THIS
     * Return: ViewHolder = whaT IS THIS
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout
                .single_image_view_gallery, parent, false);

        return new ViewHolder(view);
    }


    /**
     * Function: onBindViewHolder()
     * Purpose: binds the viewHolder to a position on the screen?
     * Parameters: ViewHolder holder = WHAT IS THIS
     *             final int position = WHAT IS THIS
     * Return: none
     */
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        /**
         * Glide is a open source library that allows a bitmap to be uploaded into
         * an imageView without having out of memory problems. It also allows for smooth
         * scrolling and image transformation.
         */
        Glide.with(holder.mImageView.getContext())
                .asBitmap()
                .load(mSessionImage.get(position).first)
                .placeholder(R.drawable.loading_symbol2)
                .transform(new ImageTransformation( 90))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.mImageView);


        /** Opens the Album activity */
        holder.mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(mContext, AlbumActivity.class);
                intent.putExtra("fileName", files[position].getAbsolutePath());
                mContext.startActivity(intent);
            }
        });

        /** Sets the text just down below the image view */

        String title = mSessionImage.get(position).second;
        String[] parts = title.split("_"); // Format title string and set title
        String newTitle = parts[0] + "/" + parts[1] + "/" + parts[2] + " " + parts[3]
                + ":" + parts[4];
        holder.sessionTitle.setText(newTitle);

    }

    /**
     * Function: getItemCount()
     * Purpose: get count of images in session
     * Parameters: none
     * Return: number of images in session
     */
    @Override
    public int getItemCount() {

        return mSessionImage.size();
    }

}