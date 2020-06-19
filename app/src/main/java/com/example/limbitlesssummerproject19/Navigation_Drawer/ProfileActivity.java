package com.example.limbitlesssummerproject19.Navigation_Drawer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.example.limbitlesssummerproject19.Login.LoginActivity;
import com.example.limbitlesssummerproject19.MainActivity;
import com.example.limbitlesssummerproject19.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileActivity extends AppCompatActivity {

    private EditText mProfileName, mProfileGender, mProfileDOB, mProfileEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mProfileName = findViewById(R.id.profile_name);
        mProfileGender = findViewById(R.id.profile_gender);
        mProfileDOB = findViewById(R.id.profile_DOB);
        mProfileEmail = findViewById(R.id.profile_email);

    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if(user != null) {

        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);
        startActivityForResult(myIntent, 0);
        return true;
    }

    public void updatePassword(View view) {

    }

    public void updateProfile(View view) {

    }
}
