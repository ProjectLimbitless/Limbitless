package com.example.limbitlesssummerproject19;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.w3c.dom.Text;

public class LoginActivity extends BaseAccountActivity {

    public static final String TAG = "EmailPassword";

    private EditText mEmailField;
    private EditText mPassWordField;
    private TextView mCreateAccount;


    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);

        //Hiding the menu app bar
        getSupportActionBar().hide();

        mEmailField = findViewById(R.id.email_text);
        mPassWordField = findViewById(R.id.password_text);
        mCreateAccount =  findViewById(R.id.create_account_id);

        //Need a prograss bar

        setProgressBar(R.id.progressBar);

        // Initialize firebase auth
        mAuth = FirebaseAuth.getInstance();

        makeTextClickable("New to Lim[b]itless? Create an account.");

    }



    @Override
    public void onStart() {
        super.onStart();

    }



    private void signIn(String email, String password) {
        Log.d(TAG, "signIn:" + email);

        if(!validForm()){
            return;
        }

        showProgressBar();

        mAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if ( task.isSuccessful()){
                            Log.d(TAG, "signInWithEmail:success");
                            Intent intent = new Intent(getApplicationContext(), DrawActivity.class);
                            startActivity(intent);
                            overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);

                        } else{
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(getApplicationContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }

                        hideProgressBar();
                    }
                });
    }


    private boolean validForm() {

        boolean valid = true;

        // Checking if user entered email
        String email = mEmailField.getText().toString();
        if(TextUtils.isEmpty(email)){
            mEmailField.setError("Requires email.");
            valid = false;
        } else{
            mEmailField.setError(null);
        }

        // Checking if user entered password
        String password = mPassWordField.getText().toString();
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
        Intent intent = new Intent (this, DrawActivity.class);
        intent.putExtra(name, password);
        this.startActivity(intent);
        this.overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
    }

    public void logInToLimbitless(View view) {

        signIn(mEmailField.getText().toString(),mPassWordField.getText().toString());

    }

    private void makeTextClickable(String account) {

        SpannableString ss = new SpannableString(account);
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                startActivity(new Intent(LoginActivity.this, CreateAccountActivity.class));
            }
            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
            }
        };
        ss.setSpan(clickableSpan, 21, account.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        mCreateAccount.setText(ss);
        mCreateAccount.setMovementMethod(LinkMovementMethod.getInstance());
        mCreateAccount.setHighlightColor(Color.TRANSPARENT);

    }

}
