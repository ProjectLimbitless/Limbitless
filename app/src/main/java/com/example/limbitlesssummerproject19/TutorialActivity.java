package com.example.limbitlesssummerproject19;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.transition.Slide;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.mancj.slideup.SlideUp;
import com.mancj.slideup.SlideUpBuilder;




// Creating a fragment instead of an activity
public class TutorialActivity extends AppCompatActivity {

    private static final int NUM_PAGES = 5;

    private SlideUp slideUp;
    private View dim;
    private View sliderView;
    private FloatingActionButton fab;
    Toast toast;

    Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        // What the hell is going on with mPager
        ViewPager mPager = findViewById(R.id.pager);
        PagerAdapter pagerAdapter = new ImageAdapter(this);
        mPager.setPageTransformer(true, new ZoomOutPageTransformer());
        mPager.setAdapter(pagerAdapter);
//        button = findViewById(R.id.left_button_tutorial);
//        button.setOnClickListener(this);

        sliderView = findViewById(R.id.slideView);
        sliderView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (toast != null){
                    toast.cancel();
                }
                toast = Toast.makeText(TutorialActivity.this, "click", Toast.LENGTH_SHORT);
                toast.show();
            }
        });
        dim = findViewById(R.id.dim);
        fab = (FloatingActionButton) findViewById(R.id.fab);

        slideUp = new SlideUpBuilder(sliderView)
                .withListeners(new SlideUp.Listener.Events() {
                    @Override
                    public void onSlide(float percent) {
                        dim.setAlpha(1 - (percent / 100));
                        if (fab.isShown() && percent < 100) {
                            fab.hide();
                        }
                    }

                    @Override
                    public void onVisibilityChanged(int visibility) {
                        if (visibility == View.GONE){
                            fab.show();
                        }
                    }
                })
                .withStartGravity(Gravity.BOTTOM)
                .withLoggingEnabled(true)
                .withGesturesEnabled(true)
                .withStartState(SlideUp.State.HIDDEN)
                .withSlideFromOtherView(findViewById(R.id.rootView))
                .build();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                slideUp.show();
            }
        });


    }

    public boolean onOptionsItemSelected(MenuItem item){
        Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);
        startActivityForResult(myIntent, 0);
        return true;
    }

//    @Override
//    public void onClick(View view) {
//
//        Intent intent;
//        if(view.getId() == R.id.left_button_tutorial){
//            intent = new Intent(this, SlideUpViewActivity.class);
//            this.startActivity(intent);
//        }
//
//    }
}
