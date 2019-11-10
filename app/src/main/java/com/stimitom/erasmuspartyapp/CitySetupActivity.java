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
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class CitySetupActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private static final String TAG = "CitySetupActivity";
    private Spinner citySpinner;
    private Spinner nationalitySpinner;
    private Button letsGoButton;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ArrayList<String> cityList;
    private String city;
    private String nationality;
    private Boolean comesFromProfileEdit;
    private String oldNationality;
    private String oldCity;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_setup);

        progressBar = (ProgressBar)findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.INVISIBLE);
        citySpinner = (Spinner) findViewById(R.id.spinner_cities);
        nationalitySpinner = (Spinner) findViewById(R.id.spinner_nationality);
        letsGoButton = (Button) findViewById(R.id.lets_go_button);
        Intent intent = getIntent();
        comesFromProfileEdit = intent.getBooleanExtra("comesFromProfileEdit",false);
        oldCity = intent.getStringExtra("oldCity");
        oldNationality = intent.getStringExtra("oldNationality");


        cityList = new ArrayList<>();
        final ArrayAdapter<String> cityAdapter = new ArrayAdapter<String>(getBaseContext(),R.layout.spinner_item,cityList);
        db.collection("cities")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                            cityList.add(queryDocumentSnapshot.getId());
                        }
                        cityList.add("Select City");
                        cityAdapter.notifyDataSetChanged();
                        citySpinner.setSelection(cityList.size()-1);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "onFailure: could not fetch cities names" + e.toString());
                    }
                });

        citySpinner.setAdapter(cityAdapter);
        citySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                city = cityList.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ArrayAdapter<CharSequence> nationalityAdapter = ArrayAdapter.createFromResource(this, R.array.countries, R.layout.spinner_item);
        nationalitySpinner.setAdapter(nationalityAdapter);
        nationalitySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                nationality = nationalitySpinner.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        letsGoButton.setOnClickListener(letsGoListener);
    }

    View.OnClickListener letsGoListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!city.equals("Select City") && !nationality.equals("Select Nationality")) {
                progressBar.setVisibility(View.VISIBLE);
                String username = getIntent().getStringExtra("username");
                if (!comesFromProfileEdit)DatabaseMethods.addUserToDatabase(username, nationality, city);
                else{
                    DatabaseMethods.updateUserInDatabase(nationality,city);
                    if (oldNationality != null && oldNationality != nationality) DatabaseMethods.updateVenuesInDatabase(getApplicationContext(),oldCity,nationality);
                }
                progressBar.setVisibility(View.INVISIBLE);
                runVenuesListActivity();
            }else {
                Toast.makeText(getApplicationContext(),"Please Select a city and a Nationality.",Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
    }


    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void runVenuesListActivity() {
        Intent intent = new Intent(getApplicationContext(), VenuesListActivity.class);
        intent.putExtra("city",city);
        startActivity(intent);
    }
}
