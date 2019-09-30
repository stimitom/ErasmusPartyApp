package com.stimitom.erasmuspartyapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class AttendPartyActivity extends AppCompatActivity {

    private TextView venueName;
    private TextView venueRating;
    private ImageView venuePicture;
    private TextView numberOfAttendees;
    private Button attendButton;
    private Boolean clicked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attend_party);

        venueName = (TextView) findViewById(R.id.venue_name_tv);
        venueRating = (TextView) findViewById(R.id.venue_rating_tv);
        venuePicture = (ImageView) findViewById(R.id.venue_picture_iv);
        attendButton = (Button) findViewById(R.id.attend_button);
        numberOfAttendees = (TextView) findViewById(R.id.number_of_attendees1);


        final Venue venue = getIntent().getParcelableExtra("clickedVenue");
        final String venue_name = venue.getVenueName();
        final int number_of_attendees = venue.getNumberOfAttendees();
        venueName.setText(venue_name);
        venuePicture.setImageResource(venue.getImageId());
        venueRating.setText(venue.getRating());
        numberOfAttendees.setText(Integer.toString(number_of_attendees));


        attendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clicked == false) {
                    attendButton.setText(R.string.dontgo);
                    attendButton.setBackgroundColor(Color.RED);
                    updateNumberOfAttendees(venue_name, number_of_attendees, true);
                    clicked = true;
                } else {
                    attendButton.setText(R.string.attend);
                    attendButton.setBackgroundColor(Color.GREEN);
                    updateNumberOfAttendees(venue_name, number_of_attendees, false);
                    clicked = false;
                }
            }
        });

    }

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference venuesRef = db.collection("venues");

    //If true increases number of attendees by 1 , else decreases by 1
    public void updateNumberOfAttendees(String venueName, int count, Boolean add) {
        DocumentReference venueInDB = venuesRef.document(venueName);
        if (add) venueInDB.update("numberOfAttendees", ++count);
        else venueInDB.update("numberOfAttendees", --count);
    }
}
