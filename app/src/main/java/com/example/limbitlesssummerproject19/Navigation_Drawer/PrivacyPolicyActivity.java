package com.example.limbitlesssummerproject19.Navigation_Drawer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.limbitlesssummerproject19.MainActivity;
import com.example.limbitlesssummerproject19.R;

import org.w3c.dom.Text;

public class PrivacyPolicyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);
        startActivityForResult(myIntent, 0);
        overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
        return true;
    }
}
