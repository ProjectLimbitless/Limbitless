package com.example.limbitlesssummerproject19;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

/**
 * File: SignInAsUserModel.java
 *
 *
 * Model class for a user with an account.
 *
 */
public class SignInAsUserModel implements LoginActivityMVPManager.signInModel {

    private static final int RC_SIGN_IN = 9001;
    private String tokenID = "479154573546-l7h3fmcrf3qsn9s4ll76q204egpjschb.apps.googleusercontent.com";
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuthentication = FirebaseAuth.getInstance();
    private LoginActivityMVPManager.View view;


    /** initializing LoginActivityView "this" in view */
    SignInAsUserModel(LoginActivityMVPManager.View view) { this.view = view; }


    /**
     * Function: startSignInActivity()
     * Purpose: starts user sign in activity when the user sign in button is clicked
     * Parameters: LoginActivityMVPManager.View view = what is this?
     * Return: none
     */
    @Override
    public void startSignInActivity(LoginActivityMVPManager.View view) {

        /** Builds Google client */
        GoogleSignInOptions googleSignInOptions = requestSignInOptions();
        mGoogleSignInClient = GoogleSignIn.getClient((Context) view, googleSignInOptions);
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        ((Activity) view).startActivityForResult(signInIntent,RC_SIGN_IN);

    }


    /**
     * Function: requestSignInOptions()
     * Purpose: requestingSignInOptions from google for login
     * Parameters: none
     * Return: Google Object for sign in options
     */
    private GoogleSignInOptions requestSignInOptions() {

        GoogleSignInOptions.Builder builder = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN);
        builder.requestIdToken(tokenID);
        return builder.build();

    }


    /**
     * Function: requestAuth()
     * Purpose: request authentication with Firebase
     * Parameters: int requestCode = the request code
     *             Intent data = ???
     * Return: none
     */
    @Override
    public void requestAuth(int requestCode, Intent data) {

        /** Creating an account with Google and authenticating with Firebase */
        if (requestCode == RC_SIGN_IN) {

            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                System.out.println("Google sign in failed: " + e);
            }
        }

    }


    /**
     * Function: checkIfUserSignedIn()
     * Purpose: checks if the user is signed in already
     * Parameters: none
     * Return: none
     */
    @Override
    public void checkIfUserSignedIn() {

        FirebaseUser currentUser = mAuthentication.getCurrentUser();
        if(currentUser != null)  updateUIAfterAuthentication();

    }


    /**
     * Function: firebaseAuthWithGoogle()
     * Purpose: access firebase with google account
     * Parameters: GoogleSignInAccount account = the account to authenticate
     * Return: none
     */
    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {

        System.out.println("firebaseAuthWithGoogle:" + account.getId());
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        System.out.println("credential:" + credential);

        mAuthentication.signInWithCredential(credential)
                .addOnCompleteListener(((Activity) view), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        /** updates UI if task is successful, otherwise returns failure */
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuthentication.getCurrentUser();
                            if (user != null)  updateUIAfterAuthentication();

                        } else {
                            Toast.makeText(((Activity) view).getApplication(),
                                    "Authentication Failed.",
                                    Toast.LENGTH_SHORT).show();
                            System.out.println(mAuthentication.getCurrentUser());
                        }

                    }
                });
    }


    /**
     * Function: updateUIAfterAuthentication()
     * Purpose: updates the UI after the user account gets authenticated
     * Parameters: none
     * Return: none
     */
    private void updateUIAfterAuthentication() {

        FirebaseUser user = mAuthentication.getCurrentUser();
        System.out.println("Current user: "+ user.getDisplayName());
        Intent intent = new Intent(((Context) view), DrawerActivity.class);
        intent.putExtra("username", user.getDisplayName());
        ((Context) view).startActivity(intent);
    }
}
