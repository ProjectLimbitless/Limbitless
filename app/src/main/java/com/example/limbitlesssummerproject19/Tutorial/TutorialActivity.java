package com.example.limbitlesssummerproject19.Tutorial;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.AppComponentFactory;
import android.content.Intent;

import com.example.limbitlesssummerproject19.MainActivity;
import com.example.limbitlesssummerproject19.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import android.provider.CalendarContract;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;
import android.widget.Toast;

import com.mancj.slideup.SlideUp;
import com.mancj.slideup.SlideUpBuilder;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;
import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator;


// Creating a fragment instead of an activity
public class TutorialActivity extends AppCompatActivity {

    public static final String TAG = ".TutorialActivity";


    PagerAdapter pageAdapter;
    ViewPager pager;

    ArgbEvaluator argbEvaluator = new ArgbEvaluator();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        int[] imageArray = new int [] {R.drawable.frame01,R.drawable.frame02, R.drawable.frame03,R.drawable.frame04, R.drawable.frame05};
        int[] textArray = new int[] { R.string.text_frame_0, R.string.text_frame_1,R.string.text_frame_2,R.string.text_frame_3,R.string.text_frame_4};
        int [] titlesArray = new int [] { R.string.step_1,R.string.step_2,R.string.step_3,R.string.step_4,R.string.step_5};
        final Integer [] colors = {getResources().getColor(R.color.color1),
                getResources().getColor(R.color.color2),
                getResources().getColor(R.color.color3),
                getResources().getColor(R.color.color4),
                getResources().getColor(R.color.color5)
        };

        // What the hell is going on with

        Log.d(TAG, "onCreate: calling the pager2");
        pager = findViewById(R.id.pager);

        pageAdapter = new ImageAdapter(this, imageArray, textArray, titlesArray);
        pager.setPageTransformer(true, new ZoomOutPageTransformer());
        pager.setAdapter(pageAdapter);






//        pager.setOnPageChangeListener( new ViewPager.OnPageChangeListener(){
//
//
//            @Override
//            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//                if(position < (pageAdapter.getCount() -1) && position <(colors.length -1)){
//                    pager.setBackgroundColor((Integer) argbEvaluator.evaluate(
//                            positionOffset,
//                            colors[position],
//                            colors[position + 1]
//                    ));
//                } else {
//                    pager.setBackgroundColor(colors[colors.length -1 ]);
//                }
//
//            }
//
//            @Override
//            public void onPageSelected(int position) {
//
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int state) {
//
//            }
//        });



    }


    /**
     * Function: onOptionsItemSelected()
     * Purpose: Handles onClick on action bar
     * Parameters: MenuItem item = the menu item being selected
     * Return: success code of action
     */
    public boolean onOptionsItemSelected(MenuItem item){
        Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);
        startActivityForResult(myIntent, 0);
        return true;
    }



}
