package com.stimitom.erasmuspartyapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class CitySetupActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private static final String TAG = "CitySetupActivity";
    private Spinner citySpinner;
    private Spinner nationalitySpinner;
    private Button letsGoButton;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ArrayList<String> cityList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_setup);

        citySpinner = (Spinner)findViewById(R.id.spinner_cities);
        nationalitySpinner = (Spinner)findViewById(R.id.spinner_nationality);
        letsGoButton = (Button)findViewById(R.id.lets_go_button);

        cityList = new ArrayList<>();
        db.collection("cities")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot queryDocumentSnapshot: queryDocumentSnapshots){
                            cityList.add(queryDocumentSnapshot.getId());
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "onFailure: could not fetch cities names" + e.toString() );
                    }
                });


//        ArrayAdapter<String> cityAdapter =  new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,cityList);
//        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        citySpinner.setAdapter(cityAdapter);
//        citySpinner.setOnItemSelectedListener(this);

        ArrayAdapter<CharSequence> nationalityAdapter = ArrayAdapter.createFromResource(this, R.array.countries, android.R.layout.simple_spinner_item);
        nationalityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        nationalitySpinner.setAdapter(nationalityAdapter);
        nationalitySpinner.setOnItemSelectedListener(this);


        letsGoButton.setOnClickListener(letsGoListener);
    }
    View.OnClickListener letsGoListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String city ="Kaunas,LT";
            String nationality = nationalitySpinner.getSelectedItem().toString();
            String username = getIntent().getStringExtra("username");
            Log.e(TAG, "onClick: city " + city + " nationality " + nationality + " username " + username );
            DatabaseMethods.addUserToDatabase(username, nationality, city);
            runVenuesListActivtiy();
        }
    };

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void runVenuesListActivtiy(){
        Intent intent = new Intent(getApplicationContext(), VenuesListActivity.class);
        startActivity(intent);
    }
}
