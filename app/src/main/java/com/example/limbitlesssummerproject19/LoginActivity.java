package com.example.limbitlesssummerproject19;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
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


public class LoginActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 9001;


    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Sign-In Button Listener
        findViewById(R.id.btn_sign_in).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("479154573546-l7h3fmcrf3qsn9s4ll76q204egpjschb.apps.googleusercontent.com")
                .requestEmail()
                .build();


        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
    }


    // [START on_start_check_user]
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null) {
            changeActivity();
        }
    }
    // [END on_start_check_user]



    // [START onactivityresult]
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                System.out.println("Google sign in failed: " + e);

            }
        }
    }
    // [END onactivityresult]



    // [START auth_with_google]
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        //System.out.println("firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                changeActivity();
                            }

                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(getApplicationContext(), "Authentication Failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }
    // [END auth_with_google]



    // [START signin]
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    // [END signin]



    // [START mainActivity]
    private void changeActivity(){
        FirebaseUser user = mAuth.getCurrentUser();
        System.out.println("Current user: "+ user.getDisplayName());
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("username", user.getDisplayName());
        startActivity(intent);
    }
    // [END mainActivity]


}




