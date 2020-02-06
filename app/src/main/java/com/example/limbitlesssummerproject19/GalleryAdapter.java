package com.example.limbitlesssummerproject19;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.media.ExifInterface;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.load.resource.bitmap.TransformationUtils;
import java.io.File;
import java.security.MessageDigest;
import java.util.ArrayList;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder> {

    // Defines member variables
    private Context mContext;
    private ArrayList<Pair<String, String>> mSessionImage;
    private File[] files;

    //  Creates a constructor for the gallery
    GalleryAdapter(Context mContext, ArrayList<Pair<String, String>> mImageList, File[] files) {

        this.mContext = mContext;
        this.mSessionImage = mImageList;
        this.files = files;
    }


    //  View placed on an a place holder and sets ImageView on single_session_view
    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView mImageView;
        TextView sessionTitle;

        ViewHolder(View itemView) {
            super(itemView);

            this.mImageView = itemView.findViewById(R.id.single_session_view);
            this.sessionTitle = itemView.findViewById(R.id.session_title);
        }
    }


    @NonNull
    @Override
    //  Placing a view into and imageview using a holder ang attaches imageView to the parent
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout
                .single_image_view_gallery, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        //  Glide is a open source library that allows a bitmap to be uploaded into
        //  an imageView without having out of memory problems. It also allows for smooth
        // scrolling and image transformation.

        Glide.with(holder.mImageView.getContext())
                .asBitmap()
                .load(mSessionImage.get(position).first)
                //.centerCrop()
                .placeholder(R.drawable.loading_symbol2)
                .transform(new ImageTransformation( 90))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.mImageView);


        // Opens the Album activity
        holder.mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(mContext, AlbumActivity.class);
                intent.putExtra("fileName", files[position].getAbsolutePath());
                mContext.startActivity(intent);
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

}