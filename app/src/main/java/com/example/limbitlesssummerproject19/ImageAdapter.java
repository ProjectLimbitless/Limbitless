package com.example.limbitlesssummerproject19;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class ImageAdapter extends PagerAdapter {


    private int imagePosition;
    private Context mContext;
    private int[] imageArray = new int [] {R.drawable.ic_360_leg,R.drawable.image2, R.drawable.image3,R.drawable.image4};

    ImageAdapter(Context context) {
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return imageArray.length;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        imagePosition = position;
        ImageView imageView = new ImageView(mContext);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setImageResource(imageArray[position]);
        container.addView(imageView,0);

        return imageView;

    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((ImageView) object);
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    public int getImagePosition(){
        return imagePosition;
    }


}

