package com.example.limbitlesssummerproject19;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

/**
 * Main Activity is the first window that opens in the device. This window has two buttons: Start
 * and Tutorial. However, Tutorial has not yet been implemented, only Start button.
 *
 */


public class MainActivity extends AppCompatActivity {
    private Button btnStart;                        //Declaring btnStart as an instance of Button

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Create a buttons and adds a listener to it

        btnStart = (Button) findViewById(R.id.btn_to_start);
        btnStart.setOnClickListener( new View.OnClickListener() {

            //Upon clicking the button, a new window will open called StartActivity

            public void onClick(View view){

                //On Click start button, we go to the start activity
                Intent intent = new Intent(MainActivity.this,
                        StartActivity.class);
                startActivity(intent);
            }
        });

    }
}