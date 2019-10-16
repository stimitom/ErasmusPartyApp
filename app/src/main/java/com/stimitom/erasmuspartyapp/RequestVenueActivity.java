package com.stimitom.erasmuspartyapp;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.pkmmte.view.CircularImageView;

import java.net.URL;


public class RequestVenueActivity extends AppCompatActivity {

    public EditText venueNameEditText;
    private EditText venueRatingEditText;
    private ImageView venuePicture;
    private Button requestVenueButton;
    private String TAG = "RequestVenueActivity";
    private Context context = this;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_venue);

        venueNameEditText = (EditText) findViewById(R.id.input_venue_name);
        venueRatingEditText = (EditText) findViewById(R.id.input_venue_rating);
        venuePicture = (CircularImageView) findViewById(R.id.input_venue_picture_round);
        requestVenueButton = (Button) findViewById(R.id.add_venue_button);

        venuePicture.setImageResource(R.drawable.bk_logo);

        requestVenueButton.setOnClickListener(addVenue);
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
            saveRequestToDatabase(new Venue(venueNameInput, venuePictureId, venueRatingInput));
            Toast.makeText(context, "Your request of " + venueNameInput + " will be checked and added shortly.", Toast.LENGTH_SHORT).show();
        }
    };

    /**
     * Handles database upload
     **/

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference venueRequests = db.collection("venuerequests");

    private void saveRequestToDatabase(Venue venue) {
        venueRequests.document(venue.getVenueName())
                .set(venue)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Venue request was saved to db succesfully");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Venue request upload to DB failed" + e.toString());
                    }
                });
    }

//    public static Bitmap getFacebookProfilePicture(String userID) throws Exception {
//        URL imageURL = new URL("https://graph.facebook.com/" + userID + "/picture?type=large");
//        Bitmap bitmap = BitmapFactory.decodeStream(imageURL.openConnection().getInputStream());
//
//        return bitmap;
//    }
//
//    Bitmap bitmap;
//
//    View.OnClickListener faceButtonListener = new View.OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            bitmap = getFacebookProfilePicture()
//        }
//    }
//
//    }
}
