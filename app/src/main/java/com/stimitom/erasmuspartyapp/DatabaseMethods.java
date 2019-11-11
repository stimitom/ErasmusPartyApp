package com.stimitom.erasmuspartyapp;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ProgressBar;
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
import com.google.firebase.firestore.WriteBatch;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;

public class DatabaseMethods {
    private static FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static CollectionReference usersRef = db.collection("users");

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


    public static void addUserToDatabase(String userName, String nationality, final String city, final ProgressBar progressBar, final Context context) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String userId = firebaseUser.getUid();
        String email = firebaseUser.getEmail();

        User user = new User(userId,userName,nationality,email,city);
        usersRef.document(userId)
                .set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        progressBar.setVisibility(View.INVISIBLE);
                        runVenuesListActivity(city);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context ,"Something went wrong. Please check your internet connection and try again.",Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public static void updateUserInDatabase(String nationality, String city, ProgressBar progressBar){
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String userId = firebaseUser.getUid();
        usersRef.document(userId).update("nationality",nationality);
        usersRef.document(userId).update("city",city);
        progressBar.setVisibility(View.INVISIBLE);
        runVenuesListActivity(city);
    }

    public static void updateVenuesInDatabase(final Context context, final String oldCity, final String updatedNationality, final ProgressBar progressBar, final String updatedCity){
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final String userId = firebaseUser.getUid();
        usersRef.document(userId).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        User user = documentSnapshot.toObject(User.class);
                        List<String> dates = user.getListnames();
                        for (String date: dates) {
                            cleanUpVenueAfterChangedNationality( oldCity, updatedNationality, date, userId,context);
                        }
                        progressBar.setVisibility(View.INVISIBLE);
                        runVenuesListActivity(updatedCity);
                    }
                });
    }


    public static void cleanUpVenueAfterChangedNationality(final String oldCity, final String updatedNationality, final String date, final String userId, final Context context){
        db.collection(oldCity +"_dates").document(date).collection("day_venues")
                .whereArrayContains("guestList",userId).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        WriteBatch batch = db.batch();
                        for (QueryDocumentSnapshot queriedVenue: queryDocumentSnapshots) {
                            Venue venue =  queriedVenue.toObject(Venue.class);
                            String venueName = venue.getVenueName();
                            Map<String, String> usersNationatlitiesMap = venue.getUsersNationalitiesMap();

                            DocumentReference dayVenueRef = db.collection(oldCity+"_dates").document(date).collection("day_venues").document(venueName);
                            deleteUserFromVenue(userId,usersNationatlitiesMap,dayVenueRef,batch);
                            addUserToVenue(userId,updatedNationality,usersNationatlitiesMap,dayVenueRef,batch);
                        }
                        batch.commit().addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(context,"Something went wrong, please check your internet connection and try again.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
    }


    public static void deleteUserFromVenue(String userId,Map<String,String> usersNationalitiesMap,DocumentReference dayVenueRef, WriteBatch batch){
        usersNationalitiesMap.remove(userId);
        batch.update(dayVenueRef,"usersNationalitiesMap",usersNationalitiesMap);
        batch.update(dayVenueRef,"guestList", FieldValue.arrayRemove(userId));
        batch.update(dayVenueRef,"numberOfAttendees", FieldValue.increment(-1L));
    }

    public static void addUserToVenue(String userId,String updatedNationality,Map<String,String> usersNationatlitiesMap, DocumentReference dayVenueRef, WriteBatch batch){
        usersNationatlitiesMap.put(userId,updatedNationality);
        batch.update(dayVenueRef,"numberOfAttendees", FieldValue.increment(1L));
        batch.update(dayVenueRef,"usersNationalitiesMap",usersNationatlitiesMap);
        batch.update(dayVenueRef,"guestList", FieldValue.arrayUnion(userId));
    }


    public static void runVenuesListActivity(String city) {
        Intent intent = new Intent(CitySetupActivity.citySetupActivity, VenuesListActivity.class);
        intent.putExtra("city",city);
        CitySetupActivity.citySetupActivity.startActivity(intent);
        CitySetupActivity.citySetupActivity.finish();
    }

}

