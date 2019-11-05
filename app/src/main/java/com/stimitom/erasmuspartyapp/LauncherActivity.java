package com.stimitom.erasmuspartyapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.os.Handler;
import android.util.Log;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


public class LauncherActivity extends AppCompatActivity {
    String TAG = "LauncherActivity";
    private Context context = LauncherActivity.this;
    private static final int SPLASH_TIME_OUT = 2500;
    private Boolean userIsSet = false;
    public static Activity launcherActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
        launcherActivity = this;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                isLoggedInAndUserIsSet();
            }
        },SPLASH_TIME_OUT);
    }


    public void runLoginActivity(){
        Intent intent = new Intent(context, LoginActivity.class);
        intent.putExtra("comesFromLauncher",true);
        context.startActivity(intent);
    }
    public  void runVenuesListActivity(){
        Intent intent = new Intent(context,VenuesListActivity.class);
        intent.putExtra("comesFromLauncher",true);
        context.startActivity(intent);
    }

    public void isLoggedInAndUserIsSet() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            FirebaseFirestore.getInstance().collection("users").document(user.getUid())
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.get("nationality") != null) {
                                runVenuesListActivity();
                            }else runLoginActivity();
                        }
                    });
        }else runLoginActivity();
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