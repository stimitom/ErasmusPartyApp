package com.stimitom.erasmuspartyapp;

import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import androidx.annotation.NonNull;

public class DatabaseMethods {
    private static final String TAG = "DatabaseMethods";
    private static FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static CollectionReference venuesRef = db.collection("venues");
    private static CollectionReference datesRef = db.collection("dates");


    public static void saveVenueToDatabase(Venue venue) {
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
                        Log.e(TAG, "Venue upload to database FAILED" + e.toString());
                    }
                });
    }

    public static void saveDayVenueToDB(String date, Venue venue){
        datesRef.document(date).collection("day_venues")
                .document(venue.getVenueName())
                .set(venue)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.i(TAG, "Day_venue upload database succesful");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Day_venue upload to database FAILED"  + e.toString());
                    }
                });
    }

    public static String getDateToday(){
        Date date;
        Format formatter = new SimpleDateFormat("dd-MM-yyyy");
        Calendar calendar = Calendar.getInstance();

        date = calendar.getTime();
        return formatter.format(date);
    }

    public static String getDateTomorrow(){
        Date date;
        Format formatter = new SimpleDateFormat("dd-MM-yyyy");
        Calendar calendar = Calendar.getInstance();

        calendar.add(Calendar.DATE, 1);
        date = calendar.getTime();
        return formatter.format(date);
    }

    public static String getDateTheDayAfterTomorrow(){
        Date date;
        Format formatter = new SimpleDateFormat("dd-MM-yyyy");
        Calendar calendar = Calendar.getInstance();

        calendar.add(Calendar.DATE, 2);
        date = calendar.getTime();
        return formatter.format(date);
    }

    public static String getDateInThreeDays(){
        Date date;
        Format formatter = new SimpleDateFormat("dd-MM-yyyy");
        Calendar calendar = Calendar.getInstance();

        calendar.add(Calendar.DATE, 3);
        date = calendar.getTime();
        return formatter.format(date);
    }
}

