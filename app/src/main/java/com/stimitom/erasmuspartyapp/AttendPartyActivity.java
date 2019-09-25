package com.stimitom.erasmuspartyapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class AttendPartyActivity extends AppCompatActivity {

    TextView venueName;
    TextView venueRating;
    ImageView venuePicture;
    Button attendButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attend_party);

        venueName = (TextView) findViewById(R.id.venue_name_tv);
        venueRating = (TextView) findViewById(R.id.venue_rating_tv);
        venuePicture = (ImageView) findViewById(R.id.venue_picture_iv);
        attendButton = (Button) findViewById(R.id.attend_button);

        Venue venue = getIntent().getParcelableExtra("clickedVenue");
        venueName.setText(venue.getVenueName());
       venuePicture.setImageResource(venue.getImageId());
        venueRating.setText(venue.getRating());
    }
}
