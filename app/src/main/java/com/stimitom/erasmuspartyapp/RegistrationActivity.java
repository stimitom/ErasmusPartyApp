package com.stimitom.erasmuspartyapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;

public class RegistrationActivity extends AppCompatActivity {

    private static final String TAG = "RegistrationActivity";
    private Context context = this;

    private EditText editTextName;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private EditText editTextRepeatPassword;
    private Button signUpButton;
    private ProgressBar progressBar;
    private Button goToLoginButton;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        editTextName = (EditText) findViewById(R.id.username_edit_text_registration);
        editTextEmail = (EditText) findViewById(R.id.email_edit_text_registration);
        editTextPassword = (EditText) findViewById(R.id.password_edit_text_registration);
        editTextRepeatPassword = (EditText) findViewById(R.id.repeat_password_edit_text_registration);
        signUpButton = (Button) findViewById(R.id.sign_up_button);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar_registration);
        goToLoginButton = (Button) findViewById(R.id.already_registered_button);
        mAuth = FirebaseAuth.getInstance();

        progressBar.setVisibility(View.INVISIBLE);
        signUpButton.setOnClickListener(signUpListener);
        goToLoginButton.setOnClickListener(goToLogInListener);

    }


    View.OnClickListener signUpListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            registerUser();
        }
    };

    private void registerUser() {
        progressBar.setVisibility(View.VISIBLE);
        final String name = editTextName.getText().toString().trim();
        final String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String repeatedPassword = editTextRepeatPassword.getText().toString().trim();

        if (name.isEmpty()) {
            editTextName.setError("username required");
            editTextName.requestFocus();
            progressBar.setVisibility(View.GONE);
            return;
        }

        if (email.isEmpty()) {
            editTextEmail.setError("email required");
            editTextEmail.requestFocus();
            progressBar.setVisibility(View.GONE);
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("Enter a valid email");
            editTextEmail.requestFocus();
            progressBar.setVisibility(View.GONE);
            return;
        }

        if (password.isEmpty()) {
            editTextPassword.setError("password required");
            editTextPassword.requestFocus();
            progressBar.setVisibility(View.GONE);
            return;
        }

        if (password.length() < 6) {
            editTextPassword.setError("password should be at least 6 characters long");
            editTextPassword.requestFocus();
            progressBar.setVisibility(View.GONE);
            return;
        }
        if (!repeatedPassword.equals(password)) {
            editTextRepeatPassword.setError("Passwords do not match. Type again, please!");
            editTextRepeatPassword.requestFocus();
            progressBar.setVisibility(View.GONE);
            return;
        }


        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            runCitySetupActivity(name);
                        } else {
                            Log.e(TAG, "onComplete: user upload to db was not succesful" + task.getException().getMessage());
                            // If email address is already registered
                            if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                Toast.makeText(getApplicationContext(), "Your email address is already registered, please Login.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });

    }

    View.OnClickListener goToLogInListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(context, LoginActivity.class);
            context.startActivity(intent);
        }
    };

    private void runCitySetupActivity(String username) {
        Intent intent = new Intent(context, CitySetupActivity.class);
        intent.putExtra("username",username);
        intent.putExtra("comesFromProfileEdit", false);
        context.startActivity(intent);
        finish();
    }

}
