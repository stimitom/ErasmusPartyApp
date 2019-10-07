package com.stimitom.erasmuspartyapp;

import android.content.Context;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import com.google.firebase.auth.FirebaseAuth;

public class LauncherActivity extends AppCompatActivity {
    String TAG = "LauncherActivity";

    private Button  lab1Button;
    private Button coreAppButton;

    private Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        lab1Button = (Button) findViewById(R.id.button_lab_1);
        coreAppButton = (Button) findViewById(R.id.button_core_app);

        lab1Button.setOnClickListener(startLab1ExtrasActivity);
        coreAppButton.setOnClickListener(startNextActivity);
    }

    //If logged in : Starts VenuesActivity
    //else starts SignInActivity
    View.OnClickListener startNextActivity = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
             if (isLoggedIn()) runVenuesListActivity();
             else runLoginActivity();
        }
    };

    View.OnClickListener startLab1ExtrasActivity = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            runLab1ExtrasActivity();
        }
    };

    public void runVenuesListActivity(){
        Intent intent = new Intent(context,VenuesListActivity.class);
        context.startActivity(intent);
    }
    public void runLab1ExtrasActivity(){
        Intent intent = new Intent(context,Lab1ExtrasActivity.class);
        context.startActivity(intent);
    }

    public void runLoginActivity(){
        Intent intent = new Intent(context, LoginActivity.class);
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