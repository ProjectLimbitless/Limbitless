package com.example.limbitlesssummerproject19.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.example.limbitlesssummerproject19.R;


public class LoginActivityView extends AppCompatActivity implements LoginActivityMVPManager.View,
        View.OnClickListener {

    // declaring presenter
    LoginActivityPresenter loginPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // find view
        TextView guestButton = findViewById(R.id.guestLogInButton);
        Button signInButton = findViewById(R.id.signInButton);

        // set listener
        guestButton.setOnClickListener( this);
        signInButton.setOnClickListener(this);

        // set presenter
        loginPresenter = new LoginActivityPresenter(this);
    }


    @Override
    public void onClick(View view) {

        // Login into account as guest or as user
        switch (view.getId()){
            case R.id.guestLogInButton:
                loginPresenter.guestUserSignInButtonClicked();
                break;

            case R.id.signInButton:
                loginPresenter.signInButtonClicked();
                break;
        }
    }


    @Override
    public void onStart() {
        super.onStart();

        // update UI
        loginPresenter.updateUI();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Request authentication using FireBase
        loginPresenter.accessingGoogle(requestCode, data);

    }
}




