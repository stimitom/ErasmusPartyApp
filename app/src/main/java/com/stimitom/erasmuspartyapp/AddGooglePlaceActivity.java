package com.stimitom.erasmuspartyapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AddGooglePlaceActivity extends AppCompatActivity {
    private static final String TAG = "AddGooglePlaceActivity";
    private Button addPlaceButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_google_place);
        // Initialize the SDK
        Places.initialize(getApplicationContext(), "AIzaSyBfPPMyE2lIlREv58jF9G6wvXf9L_z2DQ8");
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

    }

    PlaceSelectionListener placeSelectionListener = new PlaceSelectionListener() {
        @Override
        public void onPlaceSelected(Place place) {
            // TODO: Get info about the selected place.
            String name = place.getName().toString();
            List<Place.Type> types = place.getTypes();
            List<String> typeStringList = new ArrayList<String>();
            Log.d(TAG, "onPlaceSelected: Type 0  " + typeStringList.get(0));
            for (Place.Type type : types) {
                typeStringList.add(type.toString());
            }
            if (typeStringList.contains("BAR") || typeStringList.contains("NIGHT_CLUB")) {
                Log.d(TAG, "onPlaceSelected: Name " + place.getName() );
                Log.d(TAG, "onPlaceSelected: Rating " + place.getRating());
                Log.d(TAG, "onPlaceSelected: Address " + place.getAddress());
                Log.d(TAG, "onPlaceSelected: Opening HOurs " + place.getOpeningHours());

                //PROCEED ADDING TO VENUES LIST
                startAddVenuesProcedure(place);
            } else {
                Toast.makeText(getApplicationContext(), "Sorry, " + name + " is not a bar or a nightclub, please find another place.", Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onError(Status status) {
            // TODO: Handle the error.
            Log.i(TAG, "An error occurred: " + status);
        }
    };

    public void startAddVenuesProcedure(Place place){
        addPlaceButton.setVisibility(View.VISIBLE);
        //TODO ADJUST VENUE CLASS; START ADDING TO DB; ADJUST ATTEND PARTY XML, ADJUST RATING, CREATE LOGOS FOR NIGHTCLUB,BAR

    }

}
