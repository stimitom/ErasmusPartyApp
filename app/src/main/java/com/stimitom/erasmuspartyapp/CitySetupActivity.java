package com.stimitom.erasmuspartyapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
                            cityList.add(queryDocumentSnapshot.toString());
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "onFailure:could not fetch city data" +e.toString() );
                    }
                });


        ArrayAdapter<String> cityAdapter =  new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,cityList);
        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        citySpinner.setAdapter(cityAdapter);
        citySpinner.setOnItemSelectedListener(this);



        ArrayAdapter<CharSequence> nationalityAdapter = ArrayAdapter.createFromResource(this, R.array.countries, android.R.layout.simple_spinner_item);
        nationalityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        nationalitySpinner.setAdapter(nationalityAdapter);
        nationalitySpinner.setOnItemSelectedListener(this);


        letsGoButton.setOnClickListener(letsGoListener);
    }
//TODO A LOOOOOOOT
    View.OnClickListener letsGoListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            String nationality = nationalitySpinner.getSelectedItem().toString();

            db.collection("cities").document();
        }
    };

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private CollectionReference usersRef =  db.collection("users");

    public void saveUserToDatabase(String inputCity, String inputNationality) {
        DocumentReference usersRef = db.collection("cities").document(inputCity);

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();

//        User user = new User(userId,inputNationality,userEmail);
//        usersRef.document(userId)
//                .set(user)
//                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void aVoid) {
//                        Log.i(TAG, "user upload to database succesful");
//                        Toast.makeText(getApplicationContext(), "Have Fun tonight!", Toast.LENGTH_SHORT).show();
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Log.e(TAG, "user upload to database FAILED" + e.toString());
//                    }
//                });
    }
}
