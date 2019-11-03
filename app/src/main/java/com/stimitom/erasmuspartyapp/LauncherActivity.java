package com.stimitom.erasmuspartyapp;

import android.content.Context;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.os.Handler;

import com.google.firebase.auth.FirebaseAuth;


public class LauncherActivity extends AppCompatActivity {
    String TAG = "LauncherActivity";
    private Context context = LauncherActivity.this;
    private static final int SPLASH_TIME_OUT = 2500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isLoggedIn())runVenuesListActivity();
                else runLoginActivity();
                finish();
            }
        },SPLASH_TIME_OUT);
    }


    public void runLoginActivity(){
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
    }
    public  void runVenuesListActivity(){
        Intent intent = new Intent(context,VenuesListActivity.class);
        context.startActivity(intent);
    }

    public boolean isLoggedIn() {
        //Returns true if a user is Logged in , false otherwise
        return FirebaseAuth.getInstance().getCurrentUser() != null;
    }


}



/*
CODE TO FIND A KEYHASH FOR FACEBOOK LOGIN: (RUN IN ON CREATE)

 try{
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.stimitom.erasmuspartyapp", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));

            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
 */