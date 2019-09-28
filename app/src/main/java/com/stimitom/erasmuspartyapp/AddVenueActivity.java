package com.stimitom.erasmuspartyapp;

import android.content.Context;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;


public class AddVenueActivity extends AppCompatActivity {

    public EditText venueNameEditText;
    private EditText venueRatingEditText;
    private Button addVenueButton;
    private String TAG = "AddVenueActivity";
    private Context context = this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_venue);

        venueNameEditText = (EditText) findViewById(R.id.input_venue_name);
        venueRatingEditText = (EditText) findViewById(R.id.input_venue_rating);
        addVenueButton = (Button) findViewById(R.id.add_venue_button);

        addVenueButton.setOnClickListener(addVenue);
    }

    //TODO update to proper databse method
    //Adds venue to venuesList in Adapter
    //Post: Either finishes this activity (if no correct input)
    // or finishes VenuesActivity and starts it again with updated Data
    View.OnClickListener addVenue = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final String venueNameInput = venueNameEditText.getText().toString();
            final String venueRatingInput = venueRatingEditText.getText().toString();

            if (venueNameInput != null && venueRatingInput != null
                    && venueNameInput.length() != 0 && venueRatingInput.length() != 0) {
                VenuesActivity.reloader.finish();
                ArrayList<Venue> helper = new ArrayList<>();
                helper.addAll(VenuesActivity.getVenues());
                helper.add(new Venue(venueNameInput, R.drawable.bk_logo, venueRatingInput));
                Intent intent = new Intent(context, VenuesActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("updated_venues", helper);
                intent.putExtras(bundle);
                intent.putExtra("added_flag", false);
                intent.putExtra("venue_name", venueNameInput);
                startActivity(intent);
            } else {
                Log.i(TAG, "elseonclick");
                finish();
            }

        }
    };
}
