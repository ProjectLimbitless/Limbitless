package com.example.limbitlesssummerproject19;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * File: LoginActivityView.java
 *
 *
 * This class serves as the view of the login activity.
 *
 */
public class LoginActivityView extends AppCompatActivity implements LoginActivityMVPManager.View,
        View.OnClickListener {

    /** declaring presenter */
    LoginActivityPresenter loginPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        /** find view */
        TextView guestButton = findViewById(R.id.guestLogInButton);
        Button signInButton = findViewById(R.id.signInButton);

        /** set listener */
        guestButton.setOnClickListener( this);
        signInButton.setOnClickListener(this);

        /** set presenter */
        loginPresenter = new LoginActivityPresenter(this);
    }


    /**
     * Function: onClick()
     * Purpose: determines which onButtonClicked function to call (guest or user)
     * Parameters: View view = the view being interacted with
     * Return: none
     */
    @Override
    public void onClick(View view) {

        /** Login into account as guest or as user */
        switch (view.getId()){
            case R.id.guestLogInButton:
                loginPresenter.guestUserSignInButtonClicked();
                break;

            case R.id.signInButton:
                loginPresenter.signInButtonClicked();
                break;
        }
    }


    /**
     * Function: onStart()
     * Purpose: updates the login UI
     * Parameters: none
     * Return: none
     */
    @Override
    public void onStart() {
        super.onStart();

        /** update UI */
        loginPresenter.updateUI();
    }


    /**
     * Function: onActivityResult()
     * Purpose: delegates function call to accessingGoogle depending on the request from the user
     * Parameters: int requestCode = the request code from the user
     *             int resultCode = the result from the request
     *             Intent data = idk what this is
     * Return: none
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        /** Request authentication using FireBase */
        loginPresenter.accessingGoogle(requestCode, data);

    }
}




