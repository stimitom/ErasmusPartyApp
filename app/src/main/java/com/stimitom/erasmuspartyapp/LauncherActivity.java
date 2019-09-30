package com.stimitom.erasmuspartyapp;

import android.content.Context;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.facebook.AccessToken;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;


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
             if (isLoggedIn()) runVenuesActivity();
             else runSignInActivity();
        }
    };

    View.OnClickListener startLab1ExtrasActivity = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            runLab1ExtrasActivity();
        }
    };

    public void runVenuesActivity(){
        Intent intent = new Intent(context,VenuesListActivity.class);
        context.startActivity(intent);
    }
    public void runLab1ExtrasActivity(){
        Intent intent = new Intent(context,Lab1ExtrasActivity.class);
        context.startActivity(intent);
    }

    /*********************/
    //Handling Registration
    // Authentication Providers

    private int RQC_SIGN_IN = 1;
    private int RSC_SIGN_IN_OK = 2;

    List<AuthUI.IdpConfig> providers = Arrays.asList(
            new AuthUI.IdpConfig.EmailBuilder().build(),
            new AuthUI.IdpConfig.FacebookBuilder().build()
    );

    public void runSignInActivity(){
        startActivityForResult(
                AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setAvailableProviders(providers)
                    .build(),RQC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RQC_SIGN_IN){
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RSC_SIGN_IN_OK){
                //Successfully signed in
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                //Starts VenueActivitywithFlag
                Intent intent = new Intent(context, VenuesListActivity.class);
                intent.putExtra("sign_up_flag",true);
                context.startActivity(intent);
            }else{
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode()
                Log.e(TAG,"Sign In error: Check response.getError().getErrorCode()");
            }
        }
    }

    public boolean isLoggedIn() {
        boolean loggedIn = false;
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if (accessToken != null) loggedIn = true;
        return loggedIn;
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