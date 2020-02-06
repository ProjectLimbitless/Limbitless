package com.example.limbitlesssummerproject19;

import android.content.Intent;

public interface LoginActivityMVPManager {

    // sign-in model
    interface signInModel {
        
        void startSignInActivity(View view);

        void requestAuth(int requestCode, Intent data);

        void checkIfUserSignedIn();
    }


    // guest model
    interface guestModel{

        void starGuestActivity( LoginActivityMVPManager.View view );

    }


    // Noting to return to the view, but important to understand MVC
    interface View { }

    // Presenter controls the flow of calls between the model and view
    interface Presenter{

        void guestUserSignInButtonClicked();

        void signInButtonClicked();

        void accessingGoogle(int requestCode, Intent data);

        void updateUI();
    }

}
