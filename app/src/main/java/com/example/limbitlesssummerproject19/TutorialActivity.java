package com.example.limbitlesssummerproject19;

import android.app.ActionBar;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;


import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;


// Creating a fragment instead of an activity
public class TutorialActivity extends FragmentActivity {

    private static final int NUM_PAGES = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        // What the hell is going on with mPager
        ViewPager mPager = findViewById(R.id.pager);
        PagerAdapter pagerAdapter = new ImageAdapter(this);
        mPager.setPageTransformer(true, new ZoomOutPageTransformer());
        mPager.setAdapter(pagerAdapter);
    }

    public boolean onOptionsItemSelected(MenuItem item){
        Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);
        startActivityForResult(myIntent, 0);
        return true;
    }
}
