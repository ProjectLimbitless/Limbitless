package com.example.limbitlesssummerproject19;


import android.content.Intent;

/**
 * File: LoginActivityPresenter.java
 *
 *
 * Class to adapt information from the model to the view.
 *
 */
public class LoginActivityPresenter implements LoginActivityMVPManager.Presenter {

    /** declaring objects for model-view-presenter */
    private LoginActivityMVPManager.guestModel signInAsGuest;
    private LoginActivityMVPManager.signInModel signInAsUser;
    private LoginActivityMVPManager.View view;

    LoginActivityPresenter(LoginActivityMVPManager.View view){

        this.signInAsGuest = new GuestModel();
        this.signInAsUser = new SignInAsUserModel(view);
        this.view = view;

    }

    /**
     * Function: guestUserSignInButtonClicked()
     * Purpose: starts guest activity when the guest user sign in button is clicked
     * Parameters: none
     * Return: none
     */
    @Override
    public void guestUserSignInButtonClicked()  { signInAsGuest.starGuestActivity(view); }


    /**
     * Function: signInButtonClicked()
     * Purpose: starts sign in activity when the sign in button is clicked
     * Parameters: none
     * Return: none
     */
    @Override
    public void signInButtonClicked() { signInAsUser.startSignInActivity(view); }

    /**
     * Function: accessingGoogle()
     * Purpose: access google.?
     * Parameters: int codeNum = the specific code to access a user's google account
     *             Intent data = the intent passed into requestAuth()
     * Return: none
     */
    @Override
    public void accessingGoogle(int codeNum, Intent data) { signInAsUser.requestAuth(codeNum, data); }

    /**
     * Function: updateUI()
     * Purpose: update the UI for the login activity if a user is signed in
     * Parameters: none
     * Return: none
     */
    @Override
    public void updateUI() { signInAsUser.checkIfUserSignedIn(); }


}
