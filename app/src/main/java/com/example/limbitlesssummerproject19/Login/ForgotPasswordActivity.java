package com.example.limbitlesssummerproject19.Login;

import android.content.Intent;
import android.os.Bundle;

import com.example.limbitlesssummerproject19.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText mEmailText;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        mEmailText = findViewById(R.id.forgot_email);

        mAuth = FirebaseAuth.getInstance();
    }


    public void resetPassword(View view) {
        String user_input_email = mEmailText.getText().toString().trim();
        sendEmailLink(user_input_email);
    }

    private void sendEmailLink(String email) {
        if(!validForm(email)){
            return;
        }

        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Reset link sent to email.",
                            Toast.LENGTH_SHORT).show();
                    openLoginPage();
                }

                else {
                    Toast.makeText(getApplicationContext(), "Authentication failed.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean validForm(String email) {
        boolean valid = true;

        // Checking if user entered email
        if(TextUtils.isEmpty(email)){
            mEmailText.setError("Requires email.");
            valid = false;
        } else{
            mEmailText.setError(null);
        }

        return valid;
    }

    public void createAccount(View view) {
        startActivity(new Intent(ForgotPasswordActivity.this, CreateAccountActivity.class));
        finish();
    }

    public void openLoginPage() {
        startActivity(new Intent(ForgotPasswordActivity.this, LoginActivity.class));
        finish();
    }

}
