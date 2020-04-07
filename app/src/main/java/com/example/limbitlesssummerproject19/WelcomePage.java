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

        /*
        TextView txt = findViewById(R.id.welcome_text_id);

        String Amount="Welcome to LIM[B]ITLESS";
        txt.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 55);
        Spannable span = new SpannableString(Amount);
        span.setSpan(new RelativeSizeSpan(0.8f), 0, 11,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        txt.setText(span);
        */

    }

    public void startButton(View view) {

        Intent intent = new Intent(WelcomePage.this, LoginActivity.class);
        this.startActivity(intent);
        this.overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);


    }
}
