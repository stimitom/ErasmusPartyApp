package com.stimitom.erasmuspartyapp;

import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;

public class DatabaseMethods {
    private static final String TAG = "DatabaseMethods";
    private static FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static CollectionReference datesRef = db.collection("dates");
    private static CollectionReference citiesRef = db.collection("cities");


    public static void addVenueToDate(Venue venue, String date, String city){
        db.collection(city+"_dates").document(date)
                .collection("day_venues")
                .document(venue.getVenueName())
                .set(venue)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Day_venue upload database successful");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Day_venue upload to database FAILED" + e.toString());
                    }
                });
    }

    public static void addCityToDB(String city) {
        Map<String, Object> cityDummy = new HashMap<>();
        cityDummy.put("exists", true);
        citiesRef.document(city).set(cityDummy);
    }

    public static void addExistenceDummyToDate(String city, String date){
        Map<String, Object> dateDummy = new HashMap<>();
        dateDummy.put("exists", true);
        db.collection(city+"_dates").document(date)
                .set(dateDummy)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Dummy added to date");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Dummy could NOT be added to date.");
                    }
                });
    }


    public static String getDateToday() {
        Date date;
        Format formatter = new SimpleDateFormat("dd-MM-yyyy");
        Calendar calendar = Calendar.getInstance();

        date = calendar.getTime();
        return formatter.format(date);
    }

    public static String getDateTomorrow() {
        Date date;
        Format formatter = new SimpleDateFormat("dd-MM-yyyy");
        Calendar calendar = Calendar.getInstance();

        calendar.add(Calendar.DATE, 1);
        date = calendar.getTime();
        return formatter.format(date);
    }

    public static String getDateTheDayAfterTomorrow() {
        Date date;
        Format formatter = new SimpleDateFormat("dd-MM-yyyy");
        Calendar calendar = Calendar.getInstance();

        calendar.add(Calendar.DATE, 2);
        date = calendar.getTime();
        return formatter.format(date);
    }

    public static String getDateInThreeDays() {
        Date date;
        Format formatter = new SimpleDateFormat("dd-MM-yyyy");
        Calendar calendar = Calendar.getInstance();

        calendar.add(Calendar.DATE, 3);
        date = calendar.getTime();
        return formatter.format(date);
    }

}

