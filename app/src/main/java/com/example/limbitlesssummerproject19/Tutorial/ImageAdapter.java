package com.example.limbitlesssummerproject19.Tutorial;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.limbitlesssummerproject19.R;
import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator;

import org.w3c.dom.Text;

public class ImageAdapter extends PagerAdapter{


    private final static String TAG = "ImageAdapter";

    private Context mContext;
    private int [] images;
    private int [] text;
    private int [] title;
    LayoutInflater inflater;



    ImageAdapter(Context context, int [] images, int[] text, int [] title) {

        this.mContext = context;
        this.images = images;
        this.text = text;
        this.title = title;
    }

    @Override
    public int getCount() {

        return images.length;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        Log.i(TAG, "instantiateItem: inflating all the images");
        View itemView =  inflater.inflate(R.layout.viewpager_item,container,false);


        Log.d(TAG, "instantiateItem: setting the images");
        ImageView imageView = itemView.findViewById(R.id.tutorial_image);
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);

        imageView.setImageResource(images[position]);

        WormDotsIndicator indicator = itemView.findViewById(R.id.worm_dots_indicator);

        indicator.setViewPager((ViewPager) container);


        TextView titleView = itemView.findViewById(R.id.text_view_title);
        titleView.setText(title[position]);


        Log.d(TAG, "instantiateItem: setting the labels");
        TextView textView = itemView.findViewById(R.id.text_view);
        textView.setText(text[position]);


        ((ViewPager) container).addView(itemView, null);


        Log.d(TAG, "Image position" + position);

        return itemView;

    }





    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        ((ViewPager) container).removeView((LinearLayout) object);
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }


    @Override
    public int getItemPosition(@NonNull Object object) {
        return super.getItemPosition(object);
    }


}

