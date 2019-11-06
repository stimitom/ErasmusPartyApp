package com.stimitom.erasmuspartyapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileActivity extends AppCompatActivity {
    private static final String TAG = "ProfileActivity";
    private TextView userNameTextView;
    private TextView nationalityTextView;
    private TextView cityTextView;
    private Button editButton;
    private Button resetPasswordButton;
    private String emailAddress;
    private String username;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        userNameTextView = (TextView) findViewById(R.id.text_view_username);
        nationalityTextView = (TextView) findViewById(R.id.text_view_chosen_nationality);
        cityTextView = (TextView) findViewById(R.id.text_view_chosen_city);
        editButton = (Button) findViewById(R.id.edit_button);
        editButton.setVisibility(View.INVISIBLE);
        resetPasswordButton = (Button) findViewById(R.id.reset_password_button);
        resetPasswordButton.setVisibility(View.INVISIBLE);

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            FirebaseFirestore.getInstance().collection("users").document(firebaseUser.getUid())
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            User user = documentSnapshot.toObject(User.class);
                            username = user.getUsername();
                            String usernameForTextView = getResources().getString(R.string.profile_username) + " " + username;
                            String nationality = getResources().getString(R.string.profile_nationality) + " " + user.getNationality();
                            String city = getResources().getString(R.string.profile_city) + " " + user.getCity();
                            userNameTextView.setText(usernameForTextView);
                            nationalityTextView.setText(nationality);
                            cityTextView.setText(city);
                            emailAddress = user.getEmail();
                            resetPasswordButton.setVisibility(View.VISIBLE);
                            editButton.setVisibility(View.VISIBLE);
                        }
                    });

        } else {
            runLoginActivity();
        }

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runCitySetupActivity(username);
            }
        });

        resetPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final FirebaseAuth auth = FirebaseAuth.getInstance();
                auth.sendPasswordResetEmail(emailAddress)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d(TAG, "Reset Email sent.");
                                    Toast.makeText(getApplicationContext(), "Reset Email Sent", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }


    public void runLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    public void runCitySetupActivity(String username) {
        Intent intent = new Intent(this, CitySetupActivity.class);
        intent.putExtra("username",username);
        startActivity(intent);
        finish();
    }
}
