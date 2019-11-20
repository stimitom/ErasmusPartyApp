package com.stimitom.erasmuspartyapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;

public class LoginActivity extends AppCompatActivity {
    private Context context = this;
    private LoginButton facebookLoginButton;
    private CallbackManager callbackManager;
    private FirebaseAuth mAuth;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private ProgressBar progressBar;
    private Button genericLoginButton;
    private Button goToSignUpButton;
    private Button resetPasswordButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        if(getIntent().getBooleanExtra("comesFromLauncher",true))LauncherActivity.launcherActivity.finish();

        mAuth = FirebaseAuth.getInstance();

        editTextEmail = (EditText) findViewById(R.id.email_edit_text_registration);
        editTextPassword = (EditText) findViewById(R.id.password_edit_text_registration);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar_registration);
        progressBar.setVisibility(View.INVISIBLE);

        facebookLoginButton = (LoginButton) findViewById(R.id.facebook_login_button);
        String[] permissions = {"email", "public_profile"};
        facebookLoginButton.setPermissions(Arrays.asList(permissions));
        genericLoginButton = (Button) findViewById(R.id.generic_login_button);
        genericLoginButton.setOnClickListener(loginListener);
        goToSignUpButton = (Button) findViewById(R.id.not_registered_yet_button);
        goToSignUpButton.setOnClickListener(goToSignUpListener);
        resetPasswordButton = (Button) findViewById(R.id.forgot_password);
        resetPasswordButton.setOnClickListener(resetPasswordListener);

        callbackManager = CallbackManager.Factory.create();

        facebookLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                progressBar.setVisibility(View.VISIBLE);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(context, "Something went wrong, please check your internet connection and try again", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void handleFacebookAccessToken(AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Profile profile = Profile.getCurrentProfile();
                            String name = "";
                            if (profile != null) {
                                name = profile.getName();
                            }
                            runNextActivity(name);
                        } else {
                            // If sign in fails, display a message to the user
                            Toast.makeText(LoginActivity.this, "Authentication failed. Please check your internet connection and try again.", Toast.LENGTH_SHORT).show();
                        }
                        progressBar.setVisibility(View.GONE);
                    }
                });
    }

    View.OnClickListener loginListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            userLogin();
        }
    };

    private void userLogin() {
        progressBar.setVisibility(View.VISIBLE);
        final String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

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

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            runNextActivity(null);
                        } else {
                            Toast.makeText(context, "No user registered with this email/password. Try again or Sign Up.", Toast.LENGTH_LONG).show();
                        }
                        progressBar.setVisibility(View.GONE);
                    }
                });
    }

    //runs VenuesList if User is properly registered else CitySetup
    private void runNextActivity(final String username) {
        //Checks if user is already registered
        String userid = FirebaseAuth.getInstance().getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(userid).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.contains("nationality") && documentSnapshot.get("nationality")!= null) {
                            runVenuesListActivity(documentSnapshot.get("city").toString());
                            finish();
                        }else {
                            runCitySetupActivity(username);
                            finish();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "Something went wrong, please try again later", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void runVenuesListActivity(String city) {
        Intent intent = new Intent(context, VenuesListActivity.class);
        intent.putExtra("city",city);
        context.startActivity(intent);
    }

    private void runCitySetupActivity(String username) {
        Intent intent = new Intent(context, CitySetupActivity.class);
        intent.putExtra("username", username);
        context.startActivity(intent);
    }

    View.OnClickListener goToSignUpListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(context, RegistrationActivity.class);
            context.startActivity(intent);
        }
    };

    View.OnClickListener resetPasswordListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ResetPasswordDialog dialog = new ResetPasswordDialog();
            dialog.show(getSupportFragmentManager(),"ResetPasswordDialog");
        }
    };

    public Context getContext() {
        return context;
    }
}
