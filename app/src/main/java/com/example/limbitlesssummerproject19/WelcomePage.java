package com.example.limbitlesssummerproject19;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

public class WelcomePage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_page);

        getSupportActionBar().hide();

    }

    public void startButton(View view) {

        Intent intent = new Intent(WelcomePage.this, LoginActivity.class);
        this.startActivity(intent);
        this.overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
        finish();

    }
}
