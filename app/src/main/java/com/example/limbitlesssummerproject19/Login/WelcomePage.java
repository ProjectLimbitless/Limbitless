package com.example.limbitlesssummerproject19.Login;


import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.limbitlesssummerproject19.R;

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

    }
}
