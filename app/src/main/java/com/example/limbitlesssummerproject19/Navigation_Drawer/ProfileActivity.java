package com.example.limbitlesssummerproject19.Navigation_Drawer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.limbitlesssummerproject19.Login.ForgotPasswordActivity;
import com.example.limbitlesssummerproject19.Login.LoginActivity;
import com.example.limbitlesssummerproject19.MainActivity;
import com.example.limbitlesssummerproject19.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    private EditText mProfileName, mProfileGender, mProfileDOB;
    private TextView mProfileEmail;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private String user_email = user.getEmail();

    private DocumentReference userRef = db.collection("Users").document(user_email);



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mProfileName = findViewById(R.id.profile_name);
        mProfileGender = findViewById(R.id.profile_gender);
        mProfileDOB = findViewById(R.id.profile_DOB);
        mProfileEmail = findViewById(R.id.profile_email);

        loadUser();
    }

    @Override
    public void onStart() {
        super.onStart();

        if(user != null) {
            loadUser();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);
        startActivityForResult(myIntent, 0);
        return true;
    }

    public void updatePassword(View view) {
        startActivity(new Intent(ProfileActivity.this, ForgotPasswordActivity.class));
        ProfileActivity.this.finish();
    }

    public void updateProfile(View view) {
        String user_input_name = mProfileName.getText().toString().trim();
        String user_input_DOB = mProfileDOB.getText().toString().trim();
        String user_input_gender = mProfileGender.getText().toString();

        if(!validForm(user_input_name, user_input_DOB, user_input_gender)){
            return;
        }

        Map<String, Object> userObj = new HashMap<>();

        userObj.put("full_name", user_input_name);
        userObj.put("DOB", user_input_DOB);
        userObj.put("gender", user_input_gender);

        userRef.set(userObj).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Profile update failed!", Toast.LENGTH_LONG).show();
            }
        });


        mProfileName.getText().clear();
        mProfileDOB.getText().clear();
        mProfileGender.getText().clear();

        loadUser();
    }

    private boolean validForm(String name, String DOB, String gender) {

        boolean valid = true;


        // Checking if user entered password
        if(TextUtils.isEmpty(name)){
            mProfileName.setError("Name required!");
            valid = false;
        } else {
            mProfileName.setError(null);
        }

        if(TextUtils.isEmpty(DOB)){
            mProfileDOB.setError("DOB required!");
            valid = false;
        } else {
            mProfileDOB.setError(null);
        }

        if(TextUtils.isEmpty(gender)){
            mProfileGender.setError("Name required!");
            valid = false;
        } else {
            mProfileGender.setError(null);
        }

        return valid;
    }

    private void loadUser() {
        user_email = user.getEmail();
        if(user_email != null) {
            mProfileEmail.setText(user_email);
        }

        userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()) {
                    String userName = documentSnapshot.getString("full_name");
                    String userDOB = documentSnapshot.getString("DOB");
                    String userGender = documentSnapshot.getString("gender");

                    mProfileName.setText(userName);
                    mProfileDOB.setText(userDOB);
                    mProfileGender.setText(userGender);
                } else {

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "No user information!", Toast.LENGTH_LONG).show();
            }
        });
    }

}
