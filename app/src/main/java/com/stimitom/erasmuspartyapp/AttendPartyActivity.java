package com.stimitom.erasmuspartyapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class AttendPartyActivity extends AppCompatActivity {
    private final String TAG = "AttendPartyActivity";
    Context context = this;

    private TextView venueName_TextView;
    private TextView venueRating_TextView;
    private ImageView venuePicture_ImageView;
    private TextView venueNumberOfAttendees_TextView;

    private Button attendButton;
    private Boolean ButtonIsRed;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    DocumentReference userRef;
    DocumentReference venueRef;
    private String venueName;
    private String venueRating;
    private int venueImageId;
    private int venueNumberOfAttendees;
    private List<String> venueGuestList;
    private String currentUserId;

    private long usersCurrentVenueCount = 0;
    private List<Venue> usersCurrentAttendedVenuesList;

    private Query query;
    private RecyclerView recyclerView;
    private NationalitiesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attend_party);
        usersCurrentAttendedVenuesList = new ArrayList<Venue>();

        venueName_TextView = (TextView) findViewById(R.id.venue_name_tv);
        venueRating_TextView = (TextView) findViewById(R.id.venue_rating_tv);
        venuePicture_ImageView = (ImageView) findViewById(R.id.venue_picture_iv);
        attendButton = (Button) findViewById(R.id.attend_button);
        venueNumberOfAttendees_TextView = (TextView) findViewById(R.id.number_of_attendees1);

        final Venue venue = getIntent().getParcelableExtra("clickedVenue");
        final String venue_name = venue.getVenueName();
        venueRef = db.collection("venues").document(venue_name);

        query = db.collection("users").whereArrayContains("venuesattending",venue_name);

        // Check current user
        if (user != null) {
            currentUserId = getUserId();
            userRef = db.collection("users")
                    .document(currentUserId);
            getVenueData();
        } else {
            Intent intent = new Intent(context,LoginActivity.class);
            context.startActivity(intent);
        }

        readVenueData(new GetDataListener() {
            @Override
            public void onSuccess(DocumentSnapshot snapshot) {
                Venue venue = snapshot.toObject(Venue.class);
                venueGuestList = new ArrayList<String>();
                if (venue.getGuestList() != null) venueGuestList.addAll(venue.getGuestList());
                venueName = venue.getVenueName();
                venueRating = venue.getRating();
                venueImageId = venue.getImageId();
                venueNumberOfAttendees = venue.getNumberOfAttendees();

                venueName_TextView.setText(venueName);
                venuePicture_ImageView.setImageResource(venueImageId);
                venueRating_TextView.setText(venueRating);
                venueNumberOfAttendees_TextView.setText(Integer.toString(venueNumberOfAttendees));

                //Called here to ensure sequential execution
                getUserData();
                setButtonColorAndText();
            }
        });


        attendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (usersCurrentVenueCount < 3 || ButtonIsRed) {
                    if (!ButtonIsRed) {
                        Log.d(TAG, "onClick: update of Venue and User will be performed");
                        // Update db venueSide
                        venueRef.update("numberOfAttendees", ++venueNumberOfAttendees);
                        addUserToVenueGuestList(currentUserId);
                        //update db userSide
                        addToUserListOfAttendedVenues(venue);
                        attendButton.setText(R.string.dontgo);
                        attendButton.setBackgroundColor(Color.RED);
                        venueNumberOfAttendees_TextView.setText(Integer.toString(venueNumberOfAttendees));
                        ButtonIsRed = true;
                    } else {
                        //update db venueSide
                        deleteUserFromVenueGuestList(currentUserId);
                        venueRef.update("numberOfAttendees", --venueNumberOfAttendees);
                        //update db userSide
                        deleteFromUserListOfAttendedVenues(venue);
                        attendButton.setText(R.string.attend);
                        attendButton.setBackgroundColor(Color.GREEN);
                        venueNumberOfAttendees_TextView.setText(Integer.toString(venueNumberOfAttendees));
                        ButtonIsRed = false;
                    }
                } else {
                    Toast.makeText(context, "Sorry, you can only attend 3 venues per night!", Toast.LENGTH_SHORT).show();
                }
            }
        });

      setUpRecyclerView();
    }

    /***************************/
    /**GET DATA FROM DATABASE**/

    /**
     * UserInfo
     **/
    //Returns String of ID if user is logged in
    //null otherwise
    public String getUserId() {
        if (user != null) {
            //User is logged in
            Log.d(TAG, "getUserId: " + user.getUid());
            return user.getUid();
        } else {
            //User not logged in
            return null;
        }
    }

    public void getUserData() {
        userRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        User user = documentSnapshot.toObject(User.class);
                        usersCurrentVenueCount = user.getVenuecount();
                        if (usersCurrentVenueCount != 0) {
                            usersCurrentAttendedVenuesList.addAll(user.getVenuesattending());
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "onFailure: Could not fetch UserData" + e.toString());
            }
        });
    }

    public void setButtonColorAndText() {

        if (venueGuestList != null) {
            Log.e(TAG, "onCreate: venueGuestList not null");
            if (venueGuestList.contains(currentUserId)) {
                ButtonIsRed = true;
                attendButton.setBackgroundColor(Color.RED);
                attendButton.setText(R.string.dontgo);
            } else {
                ButtonIsRed = false;
                attendButton.setText(R.string.attend);
                attendButton.setBackgroundColor(Color.GREEN);
            }
        } else {
            ButtonIsRed = false;
            attendButton.setText(R.string.attend);
            attendButton.setBackgroundColor(Color.GREEN);
        }

    }

    /**
     * Venue Info
     **/

    public void getVenueData() {
        venueRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        Venue venue = documentSnapshot.toObject(Venue.class);
                        venueGuestList = new ArrayList<String>();
                        if (venue.getGuestList() != null)
                            venueGuestList.addAll(venue.getGuestList());
                        venueName = venue.getVenueName();
                        venueRating = venue.getRating();
                        venueImageId = venue.getImageId();
                        venueNumberOfAttendees = venue.getNumberOfAttendees();

                        venueName_TextView.setText(venueName);
                        venuePicture_ImageView.setImageResource(venueImageId);
                        venueRating_TextView.setText(venueRating);
                        venueNumberOfAttendees_TextView.setText(Integer.toString(venueNumberOfAttendees));

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "onFailure: Could not fetch VenueData" + e.toString());
            }
        });

    }

    public interface GetDataListener {
        void onSuccess(DocumentSnapshot snapshot);
    }

    public void readVenueData(final GetDataListener listener) {
        venueRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        listener.onSuccess(documentSnapshot);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "onFailure: Could not fetch VenueData" + e.toString());
                    }
                });
    }

    /***************************/
    /** UPDATE DATABASE **/

    /**
     * User Side
     **/

    public void addToUserListOfAttendedVenues(Venue venue) {
        usersCurrentAttendedVenuesList.add(venue);
        usersCurrentVenueCount++;
        userRef.update("venuesattending", usersCurrentAttendedVenuesList);
        userRef.update("venuecount", usersCurrentVenueCount);
        Log.d(TAG, "addToUserListOfAttendedVenues: Updated");
    }

    public void deleteFromUserListOfAttendedVenues(Venue venue) {
        List<Venue> updatedList = new ArrayList<>();
        updatedList.addAll(usersCurrentAttendedVenuesList);
        int positionOfVenueToBeDeleted = 0;
        for (int i = 0; i < updatedList.size(); i++) {
            if (updatedList.get(i).getVenueName().equals(venue.getVenueName())) {
                positionOfVenueToBeDeleted = i;
                // ListSize can at maximum be 3 so break loop with i = 4;
                i = 4;
            }
        }
        updatedList.remove(positionOfVenueToBeDeleted);
        // Update Local variables
        usersCurrentAttendedVenuesList.clear();
        usersCurrentAttendedVenuesList.addAll(updatedList);
        usersCurrentVenueCount--;
        userRef.update("venuesattending", usersCurrentAttendedVenuesList);
        userRef.update("venuecount", usersCurrentVenueCount);
    }

    /**
     * Venue Side
     **/

    public void addUserToVenueGuestList(String userId) {
        venueGuestList = new ArrayList<String>();
        venueGuestList.add(userId);
        venueRef.update("guestList", venueGuestList);
    }

    public void deleteUserFromVenueGuestList(String userId) {
        venueGuestList.remove(userId);
        venueRef.update("guestList", venueGuestList);
    }

    /**Set Up RecyclerView **/
    private void setUpRecyclerView() {
        FirestoreRecyclerOptions<User> options = new FirestoreRecyclerOptions.Builder<User>()
                .setQuery(query, User.class)
                .build();
        adapter = new NationalitiesAdapter(options);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_attend_party);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }
}
