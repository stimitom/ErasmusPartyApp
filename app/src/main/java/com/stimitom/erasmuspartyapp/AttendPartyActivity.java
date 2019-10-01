package com.stimitom.erasmuspartyapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class AttendPartyActivity extends AppCompatActivity {

    private TextView venueName;
    private TextView venueRating;
    private ImageView venuePicture;
    private TextView numberOfAttendeesView;
    private Button attendButton;
    private Boolean clicked = false;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private final String TAG = "AttendPartyActivity";
    DocumentReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attend_party);

        venueName = (TextView) findViewById(R.id.venue_name_tv);
        venueRating = (TextView) findViewById(R.id.venue_rating_tv);
        venuePicture = (ImageView) findViewById(R.id.venue_picture_iv);
        attendButton = (Button) findViewById(R.id.attend_button);
        numberOfAttendeesView = (TextView) findViewById(R.id.number_of_attendees1);


        final Venue venue = getIntent().getParcelableExtra("clickedVenue");
        final String venue_name = venue.getVenueName();
        venueName.setText(venue_name);
        venuePicture.setImageResource(venue.getImageId());
        venueRating.setText(venue.getRating());
        numberOfAttendeesView.setText(Integer.toString(venue.getNumberOfAttendees()));
        if (user != null){
             userRef = db.collection("users")
                    .document(getUserId());
        }else{
            //TODO send to login
        }

        attendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clicked == false) {
                    venue.increaseNumberOfAttendees();
                    int updatedCount = venue.getNumberOfAttendees();
                    numberOfAttendeesView.setText(Integer.toString(updatedCount));
                    attendButton.setText(R.string.dontgo);
                    attendButton.setBackgroundColor(Color.RED);
                    updateNumberOfAttendees(venue_name, updatedCount);
                    addToListOfVenuesAttended(venue);
                    clicked = true;
                } else {
                    venue.decreaseNumberOfAttendees();
                    int updatedCount = venue.getNumberOfAttendees();
                    numberOfAttendeesView.setText(Integer.toString(updatedCount));
                    attendButton.setText(R.string.attend);
                    attendButton.setBackgroundColor(Color.GREEN);
                    updateNumberOfAttendees(venue_name, updatedCount);
                    deleteFromListOfVenuesAttended(venue);
                    clicked = false;
                }
            }
        });
    }



    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference venuesRef = db.collection("venues");


    public void updateNumberOfAttendees(String venueName, int count) {
        DocumentReference venueInDB = venuesRef.document(venueName);
        venueInDB.update("numberOfAttendees", count);
    }
    public void addToListOfVenuesAttended(final Venue attendedVenue){
        //Clicked Venue gets Added to List, updates Counter
        userRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        User currentUser = documentSnapshot.toObject(User.class);
                        if (currentUser.getVenuecount() <= 3){
                            List<Venue> attendedVenuesList = new ArrayList<Venue>();
                            attendedVenuesList.add(attendedVenue);
                            currentUser.setVenuesattending(attendedVenuesList);
                            currentUser.setVenuecount(currentUser.getVenuecount()+1);
                        }else {
                            // user selected more than 3 venues for a night
                            Toast.makeText(getApplicationContext(),"You can go to more than 3 venues per night!", Toast.LENGTH_LONG);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: Could not update list of venues attended" + e.toString());
            }
        });
    }

    public void deleteFromListOfVenuesAttended(final Venue v){
        //Deletes venue v from list in user, updates Counter
        userRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                            User currentUser = documentSnapshot.toObject(User.class);
                            List<Venue> updateList = new ArrayList<>(currentUser.getVenuesattending());
                            updateList.remove(v);
                            currentUser.setVenuesattending(updateList);
                            currentUser.setVenuecount(currentUser.getVenuecount()-1);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: Could not update list of venues attended" + e.toString());
            }
        });
    }

    /**UserInfo**/

    //Returns String of ID if user is logged in
    //null otherwise
    public String getUserId(){
        if (user!= null){
            //User is logged in
            return user.getUid();
        }else {
            //User not logged in
            return null;
        }
    }
}
