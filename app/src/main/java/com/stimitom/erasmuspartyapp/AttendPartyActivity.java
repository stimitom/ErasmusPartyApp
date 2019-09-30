package com.stimitom.erasmuspartyapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class AttendPartyActivity extends AppCompatActivity {

    TextView venueName;
    TextView venueRating;
    ImageView venuePicture;
    Button attendButton;
    Boolean clicked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attend_party);

        venueName = (TextView) findViewById(R.id.venue_name_tv);
        venueRating = (TextView) findViewById(R.id.venue_rating_tv);
        venuePicture = (ImageView) findViewById(R.id.venue_picture_iv);
        attendButton = (Button) findViewById(R.id.attend_button);

        attendButton.setText(R.string.attend);

        final Venue venue = getIntent().getParcelableExtra("clickedVenue");
        venueName.setText(venue.getVenueName());
        venuePicture.setImageResource(venue.getImageId());
        venueRating.setText(venue.getRating());

        attendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clicked==false) {
                    attendButton.setText(R.string.dontgo);
                    attendButton.setBackgroundColor(Color.RED);
                    changeNumberOfAttendees(venue, true);
                    clicked = true;
                }else{
                    attendButton.setText(R.string.attend);
                    attendButton.setBackgroundColor(Color.GREEN);
                    changeNumberOfAttendees(venue,false);
                    clicked = false;
                }
            }
        });

    }

    //If true increases number of attendees by 1 , else decreases by 1
    public void changeNumberOfAttendees(Venue venue ,Boolean add){
        if (add){ venue.increaseNumberOfAttendees();}
        else venue.decreaseNumberOfAttendees();
    }
}
