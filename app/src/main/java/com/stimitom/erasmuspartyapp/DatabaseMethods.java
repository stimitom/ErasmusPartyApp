package com.stimitom.erasmuspartyapp;

import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;

public class DatabaseMethods {
    private static final String TAG = "DatabaseMethods";
    private static FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static CollectionReference citiesRef = db.collection("cities");
    private static CollectionReference usersRef = db.collection("users");


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


    public static void addUserToDatabase(String userName, String nationality, String city) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String userId = firebaseUser.getUid();
        String email = firebaseUser.getEmail();

        User user = new User(userId,userName,nationality,email,city);
        usersRef.document(userId)
                .set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "user upload to database successful");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "user upload to database FAILED" + e.toString());
                    }
                });
    }

    public static void updateUserInDatabase(String nationality, String city){
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String userId = firebaseUser.getUid();
        usersRef.document(userId).update("nationality",nationality);
        usersRef.document(userId).update("city",city);
    }

    public static void updateVenuesInDatabase(final String oldNationality, final String oldCity, final String newNationality){
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final String userId = firebaseUser.getUid();
        usersRef.document(userId).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        User user = documentSnapshot.toObject(User.class);
                        List<String> dates = user.getListnames();
                        for (String date: dates) {
                            cleanUpVenueAfterChangedNationality(oldNationality, oldCity, newNationality, date,userId);
                        }
                    }
                });

    }

    public static void cleanUpVenueAfterChangedNationality(final String oldNationality, final String oldCity, final String newNationality, final String date, String userId){
        db.collection(oldCity +"_dates").document(date).collection("day_venues")
                .whereArrayContains("guestList",userId).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot queriedVenue: queryDocumentSnapshots) {
                            Venue venue =  queriedVenue.toObject(Venue.class);
                            String venueName = venue.getVenueName();
                            Map<String, Long> nationalitiesCounterMap = venue.getNationalitiesCounterMap();
                            List<String> nationalitiesList = venue.getNationalitiesList();
                            DocumentReference docRef = db.collection(oldCity+"_dates").document(date).collection("day_venues").document(venueName);


                            //Handle old nationality
                            Long oldCountryCounter = 0L;
                            if (nationalitiesList.contains(oldNationality))oldCountryCounter = nationalitiesCounterMap.get(oldNationality);
                            nationalitiesCounterMap.put(oldNationality,--oldCountryCounter);
                            if (oldCountryCounter == 0){
                                docRef.update("nationalitiesList", FieldValue.arrayRemove(oldNationality));
                            }


                            //Handle new nationality
                            Long newCountryCounter = 1L;
                            if (nationalitiesList.contains(newNationality)){
                                newCountryCounter = nationalitiesCounterMap.get(newNationality);
                                ++newCountryCounter;
                            }
                            nationalitiesCounterMap.put(newNationality,newCountryCounter);
                            docRef.update("nationalitiesList", FieldValue.arrayUnion(newNationality));
                            docRef.update("nationalitiesCounterMap", nationalitiesCounterMap);
                        }
                    }
                });

    }

}

