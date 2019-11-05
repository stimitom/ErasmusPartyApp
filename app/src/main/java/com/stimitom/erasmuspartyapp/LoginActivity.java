package com.stimitom.erasmuspartyapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.api.LogDescriptor;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private Context context = this;

    private LoginButton facebookLoginButton;
    private CallbackManager callbackManager;
    private FirebaseAuth mAuth;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private ProgressBar progressBar;
    private Button genericLoginButton;
    private Button goToSignUpButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();

        editTextEmail = (EditText) findViewById(R.id.email_edit_text_registration);
        editTextPassword = (EditText) findViewById(R.id.password_edit_text_registration);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar_registration);
        progressBar.setVisibility(View.INVISIBLE);

        facebookLoginButton = (LoginButton) findViewById(R.id.facebook_login_button);
        String [] permissions = {"email", "public_profile"};
        facebookLoginButton.setPermissions(Arrays.asList(permissions));
        genericLoginButton = (Button) findViewById(R.id.generic_login_button);
        genericLoginButton.setOnClickListener(loginListener);
        goToSignUpButton = (Button) findViewById(R.id.not_registered_yet_button);
        goToSignUpButton.setOnClickListener(goToSignUpListener);

        callbackManager = CallbackManager.Factory.create();

        facebookLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                progressBar.setVisibility(View.VISIBLE);
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                Profile profile = Profile.getCurrentProfile();
                String name = "";
                if (profile!= null) {
                     name = profile.getName();
                }
                handleFacebookAccessToken(loginResult.getAccessToken(),name);
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void handleFacebookAccessToken(AccessToken token, final String username) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithCredential:success");
                            runCitySetupActivity(username);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
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
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            runVenuesListActivity();
                        } else {
                            Toast.makeText(context, "No user registered with this email/password. Try again or Sign Up.",Toast.LENGTH_LONG).show();
                            Log.e(TAG, "onComplete: user signIn not successful" + task.getException().getMessage());
                        }
                    }
                });
    }

    private void runVenuesListActivity() {
        Intent intent = new Intent(context, VenuesListActivity.class);
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
            Intent intent = new Intent(context,RegistrationActivity.class);
            context.startActivity(intent);
        }
    };

}
