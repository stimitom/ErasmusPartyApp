package com.stimitom.erasmuspartyapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.OpeningHours;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class AddGooglePlaceActivity extends AppCompatActivity {
    private static final String TAG = "AddGooglePlaceActivity";
    private Button addPlaceButton;
    private String city;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_google_place);

        // Initialize the SDK
        Places.initialize(getApplicationContext(), "AIzaSyBfPPMyE2lIlREv58jF9G6wvXf9L_z2DQ8", new Locale("en"));
        // Create a new Places client instance
        PlacesClient placesClient = Places.createClient(this);
        // Initialize the AutocompleteSupportFragment.
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.google_autocomplete_fragment);
        // Specify the types of place data to return.

        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS,
                Place.Field.LAT_LNG, Place.Field.RATING, Place.Field.OPENING_HOURS, Place.Field.TYPES));

        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(placeSelectionListener);
        addPlaceButton = (Button) findViewById(R.id.add_place_button);
        addPlaceButton.setVisibility(View.INVISIBLE);

        //City
        city = getIntent().getStringExtra("city");
    }

    PlaceSelectionListener placeSelectionListener = new PlaceSelectionListener() {
        @Override
        public void onPlaceSelected(Place place) {

            //Name
            String name = place.getName();
            Log.d(TAG, "onPlaceSelected: Name " + name);

            //Types
            List<String> typesList = new ArrayList<String>();
            String type = "";
            if (place.getTypes() != null) {
                List<Place.Type> types = place.getTypes();
                for (Place.Type t : types) {
                    typesList.add(t.toString());
                    Log.d(TAG, "onPlaceSelected: Type: " + t.toString());
                }
                if (typesList.contains("BAR") || typesList.contains("NIGHT_CLUB")) {
                    if (typesList.contains("BAR") && !typesList.contains("NIGHT_CLUB"))
                        type = "BAR";
                    else if (typesList.contains("NIGHT_CLUB") && !typesList.contains("BAR"))
                        type = "NIGHT_CLUB";
                    else type = "BAR|NIGHT_CLUB";
                } else {
                    Toast.makeText(getApplicationContext(), "Sorry, " + name + " is not a bar or a nightclub, please find another place.", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), "Sorry, " + name + " is not a bar or a nightclub, please find another place.", Toast.LENGTH_LONG).show();
            }

            //Opening hours
            ArrayList<String> openingHoursList = new ArrayList<String>();
            if (place.getOpeningHours() != null) {
                OpeningHours openingHours = place.getOpeningHours();
                for (String element : openingHours.getWeekdayText()) {
                    openingHoursList.add(element);
                    Log.d(TAG, "onPlaceSelected: Opening Hours Week " + element);
                }
            }

            //Rating
            Double ratingDouble = 0.0;
            if (place.getRating() != null) ratingDouble = place.getRating();
            DecimalFormat df = new DecimalFormat("#.##");
            df.setRoundingMode(RoundingMode.CEILING);
            String rating = df.format(ratingDouble);
            Log.d(TAG, "onPlaceSelected: Rating " + rating);

            //Address
            String address = "";
            if (place.getAddress() != null) address = place.getAddress();
            Log.d(TAG, "onPlaceSelected: Address " + address);

            //Location LATLNG
            String location = "";
            String cleanedLocation = "";
            if (place.getLatLng() != null) {
                location = place.getLatLng().toString();
                cleanedLocation = location.substring(10, location.length() - 1);
            }
            Log.d(TAG, "onPlaceSelected: LATLNG: " + location);
            Log.d(TAG, "onPlaceSelected: Cleaned LATLNG " + cleanedLocation);


            //PROCEED ADDING TO VENUES LIST
            Venue venue = new Venue(name, rating, address, cleanedLocation, openingHoursList, type);
            startAddVenuesProcedure(venue);
        }

        @Override
        public void onError(Status status) {
            // TODO: Handle the error.
            Log.i(TAG, "An error occurred: " + status);
        }
    };

    public void startAddVenuesProcedure(final Venue venue) {
        addPlaceButton.setVisibility(View.VISIBLE);
        addPlaceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addVenueToCityDB(city, venue);
            }
        });
    }

    /**
     * Database Method
     **/

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference citiesRef = db.collection("cities");

    public void addVenueToCityDB(String city, Venue venue) {
        citiesRef.document(city).collection("venues")
                .document(venue.getVenueName())
                .set(venue)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Venue upload database succesful");
                        Toast.makeText(getApplicationContext(), "Your request will be checked and added to the list. Thank You!", Toast.LENGTH_SHORT).show();
                        addPlaceButton.setVisibility(View.INVISIBLE);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "venue upload to database FAILED " + e.toString());
                        Toast.makeText(getApplicationContext(), "Sorry something went wrong Try again later.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
