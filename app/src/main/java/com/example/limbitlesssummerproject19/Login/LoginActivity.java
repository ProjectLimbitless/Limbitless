package com.example.limbitlesssummerproject19.Login;

import android.content.Intent;

import androidx.annotation.NonNull;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.limbitlesssummerproject19.BaseAccountActivity;
import com.example.limbitlesssummerproject19.MainActivity;
import com.example.limbitlesssummerproject19.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends BaseAccountActivity {

    public static final String TAG = "EmailPassword";

    private EditText mEmailField;
    private EditText mPassWordField;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Hiding the menu app bar
        getSupportActionBar().hide();

        mEmailField = findViewById(R.id.email_text);
        mPassWordField = findViewById(R.id.password_text);

        // Initialize firebase auth
        mAuth = FirebaseAuth.getInstance();

    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if(user != null) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }

    }

    public void logInToLimbitless(View view) {
        String user_input_email = mEmailField.getText().toString().trim();
        String user_input_password = mPassWordField.getText().toString();
        signIn(user_input_email , user_input_password);
        mEmailField.getText().clear();
        mPassWordField.getText().clear();
    }



    private void signIn(String email, String password) {
        Log.d(TAG, "signIn:" + email);

        if(!validForm(email, password)){
            return;
        }

        showProgressBar();

        mAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if ( task.isSuccessful() ){
                            Log.d(TAG, "signInWithEmail:success");
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                            overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                            finish();

                        } else{
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(getApplicationContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }

                        hideProgressBar();
                    }
                });
    }


    private boolean validForm(String email, String password) {

        boolean valid = true;

        // Checking if user entered email
        if(TextUtils.isEmpty(email)){
            mEmailField.setError("Requires email.");
            valid = false;
        } else{
            mEmailField.setError(null);
        }

        // Checking if user entered password
        if(TextUtils.isEmpty(password)){
            mPassWordField.setError("Password required!");
            valid = false;
        } else {
            mPassWordField.setError(null);
        }

        return valid;
    }

    public void signInAsGuest(View view) {
        //Toast.makeText(getApplicationContext(), "Clickable worked",Toast.LENGTH_SHORT).show();
        String password = "Guest";
        String name = "username";
        Intent intent = new Intent (this, MainActivity.class);
        intent.putExtra(name, password);
        this.startActivity(intent);
        this.overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
    }

    public void createAccount(View view) {
        startActivity(new Intent(LoginActivity.this, CreateAccountActivity.class));
    }

    public void forgotPassword(View view) {
        startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
    }
}
