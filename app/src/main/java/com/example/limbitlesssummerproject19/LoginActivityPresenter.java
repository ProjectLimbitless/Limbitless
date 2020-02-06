package com.example.limbitlesssummerproject19;


import android.content.Intent;

public class LoginActivityPresenter implements LoginActivityMVPManager.Presenter {

    // declaring objects for model-view-presenter
    private LoginActivityMVPManager.guestModel signInAsGuest;
    private LoginActivityMVPManager.signInModel signInAsUser;
    private LoginActivityMVPManager.View view;

    LoginActivityPresenter(LoginActivityMVPManager.View view){

        this.signInAsGuest = new GuestModel();
        this.signInAsUser = new SignInAsUserModel(view);
        this.view = view;

    }

    @Override
    public void guestUserSignInButtonClicked()  { signInAsGuest.starGuestActivity(view); }

    @Override
    public void signInButtonClicked() { signInAsUser.startSignInActivity(view); }

    @Override
    public void accessingGoogle(int codeNum, Intent data) { signInAsUser.requestAuth(codeNum, data); }

    @Override
    public void updateUI() { signInAsUser.checkIfUserSignedIn(); }


}
