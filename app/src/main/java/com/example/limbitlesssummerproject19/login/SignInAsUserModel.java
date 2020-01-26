package com.example.limbitlesssummerproject19.login;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.widget.Toast;
import com.example.limbitlesssummerproject19.camera.DrawerActivity;
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


public class SignInAsUserModel implements LoginActivityMVPManager.signInModel {

    private static final int RC_SIGN_IN = 9001;
    private String tokenID = "479154573546-l7h3fmcrf3qsn9s4ll76q204egpjschb.apps.googleusercontent.com";
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuthentication = FirebaseAuth.getInstance();
    private LoginActivityMVPManager.View view;


    // initializing LoginActivityView "this" in view
    public SignInAsUserModel(LoginActivityMVPManager.View view) { this.view = view; }


    @Override
    public void startSignInActivity(LoginActivityMVPManager.View view) {

        // Builds Google client
        GoogleSignInOptions googleSignInOptions = requestSignInOptions();
        mGoogleSignInClient = GoogleSignIn.getClient((Context) view, googleSignInOptions);
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        ((Activity) view).startActivityForResult(signInIntent,RC_SIGN_IN);

    }

    // requestingSignInOptions from google for login
    private GoogleSignInOptions requestSignInOptions() {

        GoogleSignInOptions googleSignInOptions =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(tokenID)
                        .build();

        return googleSignInOptions;
    }

    @Override
    public void requestAuth(int requestCode, Intent data) {

        // Creating an account with Google and authenticating with Firebase
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

    @Override
    public void checkIfUserSignedIn() {

        FirebaseUser currentUser = mAuthentication.getCurrentUser();
        if(currentUser != null)  updateUIAfterAuthentication();

    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {

        System.out.println("firebaseAuthWithGoogle:" + account.getId());
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        System.out.println("credential:" + credential);

        mAuthentication.signInWithCredential(credential)
                .addOnCompleteListener(((Activity) view), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        // updates UI if task is successful, otherwise returns failure
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

    private void updateUIAfterAuthentication() {

        FirebaseUser user = mAuthentication.getCurrentUser();
        System.out.println("Current user: "+ user.getDisplayName());
        Intent intent = new Intent(((Context) view), DrawerActivity.class);
        intent.putExtra("username", user.getDisplayName());
        ((Context) view).startActivity(intent);
    }
}
