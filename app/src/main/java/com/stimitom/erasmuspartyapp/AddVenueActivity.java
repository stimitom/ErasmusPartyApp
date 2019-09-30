package com.stimitom.erasmuspartyapp;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;



public class AddVenueActivity extends AppCompatActivity {

    public EditText venueNameEditText;
    private EditText venueRatingEditText;
    private ImageView venuePicture;
    private Button addVenueButton;
    private String TAG = "AddVenueActivity";
    private Context context = this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_venue);

        venueNameEditText = (EditText) findViewById(R.id.input_venue_name);
        venueRatingEditText = (EditText) findViewById(R.id.input_venue_rating);
        venuePicture = (ImageView) findViewById(R.id.input_venue_picture);
        addVenueButton = (Button) findViewById(R.id.add_venue_button);

        addVenueButton.setOnClickListener(addVenue);
    }

    View.OnClickListener addVenue = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final String venueNameInput = venueNameEditText.getText().toString();
            final String venueRatingInput = venueRatingEditText.getText().toString();
            final int venuePictureId = R.drawable.bk_logo;

            if (venueNameInput.trim().isEmpty() || venueRatingInput.trim().isEmpty()) {
                Log.d(TAG, "onClick: no correct inputs provided");
                Toast toast = Toast.makeText(context, "Please provide a venue name and rating", Toast.LENGTH_LONG);
                toast.show();
                return;
            }
            saveVenueToDatabase(new Venue(venueNameInput, venuePictureId, venueRatingInput));
            Toast toast = Toast.makeText(context, "" + venueNameInput + "was added to list", Toast.LENGTH_SHORT);
            toast.show();
        }
    };

    /**
     * Handles database upload
     **/

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference venuesRef = db.collection("venues");

    private void saveVenueToDatabase(Venue v) {
        Venue venue = v;
        venuesRef.document(venue.getVenueName())
                .set(venue)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Venue upload database succesful");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Venue upload to database FAILED");
                        Log.e(TAG, e.toString());
                    }
                });
    }
}
