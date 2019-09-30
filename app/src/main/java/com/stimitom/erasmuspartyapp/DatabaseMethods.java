package com.stimitom.erasmuspartyapp;

import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.annotation.NonNull;

public class DatabaseMethods {
    private static final String TAG = "DatabaseMethods";
    private static FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static CollectionReference venuesRef = db.collection("venues");

    public static void saveVenueToDatabase(Venue v) {
        Venue venue = v;

        venuesRef.document(venue.getVenueName())
                .set(venue)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.i(TAG, "Venue upload database succesful");
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

//
//    //Retrieves all venues from Database and stores them to given List
//    // NOT NEEDED --> TRANSFERRED TO VENUESACTIVITY
//    public static void loadVenues(final List<Venue> venuesList) {
//        venuesRef.get()
//                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//                    @Override
//                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//                        venuesList.clear();
//                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots ){
//                            Venue venue = documentSnapshot.toObject(Venue.class);
//                            venuesList.add(venue);
//                        }
//
//                    }
//
//                });
//    }
