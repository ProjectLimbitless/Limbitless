package com.example.limbitlesssummerproject19;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class CreateAccountActivity extends BaseAccountActivity implements View.OnClickListener {
    private EditText mEmailText;
    private EditText mPasswordText;
    private EditText mConfirmPasswordText;

    private FirebaseAuth mAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        mEmailText = findViewById(R.id.email_text);
        mPasswordText = findViewById(R.id.password_text);
        mConfirmPasswordText = findViewById(R.id.confirm_password_text);

        mAuth = FirebaseAuth.getInstance();

        if(mAuth.getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }
    }

    @Override
    public void onClick(View view) {

    }

    public void registerUser(View view) {
        String email = mEmailText.getText().toString().trim();
        String password = mPasswordText.getText().toString().trim();
        String confirmPassword = mConfirmPasswordText.getText().toString().trim();

        if(!formValidation(email, password, confirmPassword)) {
            return;
        }

        if(!comparePasswords(password, confirmPassword)) {
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    goToMainMenuSignedIn();
                }
                else {
                    Toast.makeText(getApplicationContext(), "Registration Failed.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean comparePasswords(String password, String confirmPassword) {
        if(password.equals(confirmPassword))
            return true;

        Toast.makeText(getApplicationContext(), "Please make sure your passwords match up.", Toast.LENGTH_SHORT).show();

        return false;
    }

    private boolean formValidation(String email, String password, String confirmPassword) {
        boolean valid = true;

        // Checking if user entered email
        if (TextUtils.isEmpty(email)) {
            mEmailText.setError("Requires email.");
            valid = false;
        } else {
            mEmailText.setError(null);
        }

        // Checking if user entered password
        if (TextUtils.isEmpty(password)) {
            mPasswordText.setError("Password required!");
            valid = false;
        } else {
            mPasswordText.setError(null);
        }

        if (TextUtils.isEmpty(confirmPassword)) {
            mConfirmPasswordText.setError("Confirm Password required!");
            valid = false;
        } else {
            mConfirmPasswordText.setError(null);
        }

        return valid;
    }

    private void goToMainMenuSignedIn() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);

    }
}
