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

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.ViewHolder> {

    private final Context mContext;
    private ArrayList<String> images;
    private String folderName;

    // Creates a viewHolder for the images inside the albums
    class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;

        ViewHolder(View view) {
            super(view);
            imageView =  view.findViewById(R.id.img);
        }
    }

    AlbumAdapter(Context context, ArrayList<String> galleryList, String folderName) {
        this.folderName = folderName;
        this.images = galleryList;
        this.mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        System.out.println("in view holder");
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.single_image, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        Glide.with(viewHolder.imageView.getContext())
                .asBitmap()
                .load(images.get(position))
                //.centerCrop()
                .placeholder(R.drawable.loading_symbol2)
                .transform(new ImageTransformation(
                        90))
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


    @Override
    public int getItemCount() { return images.size(); }

}
