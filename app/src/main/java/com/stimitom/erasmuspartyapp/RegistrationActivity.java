package com.stimitom.erasmuspartyapp;

import androidx.appcompat.app.AppCompatActivity;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.util.Arrays;

public class RegistrationActivity extends AppCompatActivity {
    String TAG = "RegistrationActivity";
    LoginButton loginButton;
    CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        isLoggedIn();

        callbackManager = CallbackManager.Factory.create();

        loginButton = (LoginButton) findViewById(R.id.fb_login_button);
        loginButton.setPermissions(Arrays.asList("public_profile","user_hometown"));
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.e(TAG, "Login onSuccess executed");
                loginButton.setVisibility(View.INVISIBLE);
                runVenuesActivity();
                finish();
            }

            @Override
            public void onCancel() {
                Log.e(TAG, "Login OnCancel executed");
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                Log.e(TAG, "Login onError executed");
                Log.e(TAG, exception.toString());
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void runVenuesActivity() {
        Intent intent = new Intent(this, VenuesActivity.class);
        this.startActivity(intent);
    }
    public static boolean isLoggedIn() {
        boolean loggedIn = false;
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if (accessToken != null) loggedIn = true;
        return loggedIn;
    }
}