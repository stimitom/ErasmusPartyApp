package com.stimitom.erasmuspartyapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
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
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class AttendPartyActivity extends AppCompatActivity {
    private final String TAG = "AttendPartyActivity";
    Context context = this;

    private TextView venueName_TextView;
    private TextView venueRating_TextView;
    private ImageView venuePicture_ImageView;
    private TextView venueNumberOfAttendees_TextView;
    private TextView currentVenueState_TextView;

    private Button attendButton;
    private Boolean ButtonIsRed;

    private Query query;
    private RecyclerView recyclerView;
    private NationalitiesAdapter adapter;
    private FirestoreRecyclerOptions<User> options;

    private FirebaseFirestore db;
    private FirebaseUser firebaseUser;
    DocumentReference userRef;
    DocumentReference dayVenueRef;
    private String venueName;
    private String venueRating;
    private int venueImageId;
    private int venueNumberOfAttendees;
    private List<String> venueGuestList;
    private String currentUserId;

    private String dateGiven;
    private long usersVenueCountNumber;
    private String usersVenueCountName;
    private Boolean containsList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attend_party);
        venueName_TextView = (TextView) findViewById(R.id.venue_name_tv);
        venueRating_TextView = (TextView) findViewById(R.id.venue_rating_tv);
        venuePicture_ImageView = (ImageView) findViewById(R.id.venue_picture_iv);
        attendButton = (Button) findViewById(R.id.attend_button);
        venueNumberOfAttendees_TextView = (TextView) findViewById(R.id.number_of_attendees1);
        currentVenueState_TextView = (TextView) findViewById(R.id.text_view_meet_people_from);

        Intent intent = getIntent();
        final Venue venue = intent.getParcelableExtra("clickedVenue");
        dateGiven = intent.getStringExtra("dateGiven");
        final String venue_name = venue.getVenueName();

        db = FirebaseFirestore.getInstance();
        dayVenueRef = db.collection("dates").document(dateGiven)
                .collection("day_venues")
                .document(venue_name);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();


        // Check current firebaseUser
        if (firebaseUser != null) {
            currentUserId = getUserId();
            userRef = db.collection("users")
                    .document(currentUserId);
        } else {
            Intent intent1 = new Intent(context, LoginActivity.class);
            context.startActivity(intent1);
        }

        readVenueData(venueDataListener);
        attendButton.setOnClickListener(attendButtonListener);
        setUpRecyclerView(venue_name);
    }

    /**
     * Listener
     ***/
    GetDataListener venueDataListener = new GetDataListener() {
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
            setDescriptiveText();
        }
    };


    View.OnClickListener attendButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (containsList) {
                Log.e(TAG, "onClick: Contains List");
                if (usersVenueCountNumber < 3 || ButtonIsRed) {
                    if (!ButtonIsRed) {
                        //Update db day_venue Side
                        dayVenueRef.update("numberOfAttendees", ++venueNumberOfAttendees);
                        addUserToVenueGuestList(currentUserId);

                        //Update db user side
                        addToUserListOfAttendedVenues();
                        makeButtonGreen();
                        venueNumberOfAttendees_TextView.setText(Integer.toString(venueNumberOfAttendees));
                        ButtonIsRed = false;
                    } else {
                        //Update db day_venue side
                        deleteUserFromVenueGuestList(currentUserId);
                        dayVenueRef.update("numberOfAttendees", --venueNumberOfAttendees);

                        // Update db user side
                        deleteFromUserListOfAttendedVenues();
                        makeButtonGreen();
                        venueNumberOfAttendees_TextView.setText(Integer.toString(venueNumberOfAttendees));
                        ButtonIsRed = false;
                    }
                } else {
                    Toast.makeText(context, "Sorry, you can only attend 3 venues per night!", Toast.LENGTH_SHORT).show();
                }

            }else {
                //Update db day_venue Side
                dayVenueRef.update("numberOfAttendees", ++venueNumberOfAttendees);
                addUserToVenueGuestList(currentUserId);

                //Update db user side
                addToUserListOfAttendedVenues();
                makeButtonGreen();
                venueNumberOfAttendees_TextView.setText(Integer.toString(venueNumberOfAttendees));
                ButtonIsRed = false;
            }
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    /***************************/
    /**GET DATA FROM DATABASE**/

    /**
     * UserInfo
     **/
    //Returns String of ID if firebaseUser is logged in
    //null otherwise
    public String getUserId() {
        if (firebaseUser != null) {
            //User is logged in
            Log.d(TAG, "getUserId: " + firebaseUser.getUid());
            return firebaseUser.getUid();
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
                        int listSize = user.getListnames().size();
                        if (documentSnapshot.contains(dateGiven)) {
                            containsList = true;

                            for (int i = 0; i < listSize; i++) {
                                if (user.getListnames().get(i).equals(dateGiven)) {
                                    if (i == 0) {
                                        usersVenueCountNumber = user.getCounterpos0();
                                        usersVenueCountName = "counterpos0";
                                    } else if (i == 1) {
                                        usersVenueCountNumber = user.getCounterpos1();
                                        usersVenueCountName = "counterpos1";
                                    } else {
                                        usersVenueCountNumber = user.getCounterpos2();
                                        usersVenueCountName = "counterpos2";
                                    }
                                }
                            }
                        } else {
                            if (listSize == 3) {
                                cleanUser(user.getListnames());
                                // new date will be added to users datelist  at position 0
                                usersVenueCountName = "counterpos0";
                            } else if (listSize == 2) {
                                // new date will be added to datelist at position 2
                                usersVenueCountName = "counterpos2";
                            } else {
                                // new date will be added to datelist at position 1
                                usersVenueCountName = "counterpos1";
                            }
                            containsList = false;
                            usersVenueCountNumber = 0;
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "onFailure: Could not fetch UserData" + e.toString());
                    }
                });
    }

    public void cleanUser(List<String> listnames) {
        for (String listname : listnames) {
            userRef.update(listname, FieldValue.delete());
            userRef.update("listnames", FieldValue.arrayRemove(listname));
        }
    }

    /**
     * UserInterface Code
     **/

    public void setButtonColorAndText() {

        if (venueGuestList != null) {
            Log.e(TAG, "onCreate: venueGuestList not null");
            if (venueGuestList.contains(currentUserId)) {
                ButtonIsRed = true;
                makeButtonRed();
            } else {
                ButtonIsRed = false;
                makeButtonGreen();
            }
        } else {
            ButtonIsRed = false;
            makeButtonGreen();
        }

    }

    public void makeButtonGreen() {
        attendButton.setText(R.string.attend);
        attendButton.setBackgroundResource(R.drawable.button_green_round);
    }

    public void makeButtonRed() {
        attendButton.setBackgroundResource(R.drawable.button_red_round);
        attendButton.setText(R.string.dontgo);
    }

    public void setDescriptiveText() {
        if (venueNumberOfAttendees == 0) {
            currentVenueState_TextView.setText(R.string.nobody_attends_yet);
        } else currentVenueState_TextView.setText(R.string.nationalities_tonight);
    }

    /**
     * Venue Info
     **/
    public interface GetDataListener {
        void onSuccess(DocumentSnapshot snapshot);
    }

    public void readVenueData(final GetDataListener listener) {
        dayVenueRef.get()
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

    public void addToUserListOfAttendedVenues() {
        //Update Local Variable
        usersVenueCountNumber++;
        //Update Db variables
        userRef.update(dateGiven, FieldValue.arrayUnion(venueName));
        userRef.update(usersVenueCountName, usersVenueCountNumber);
        //Will not copy the givenDate, but add if non existent
        userRef.update("listnames", FieldValue.arrayUnion(dateGiven));

        Log.d(TAG, "addToUserListOfAttendedVenues: Updated");
    }

    public void deleteFromUserListOfAttendedVenues() {
        // Update Local variables
        usersVenueCountNumber--;
        //Update db Variables
        userRef.update(dateGiven, FieldValue.arrayRemove(venueName));
        userRef.update(usersVenueCountName, usersVenueCountNumber);
    }

    /**
     * Venue Side
     **/

    public void addUserToVenueGuestList(String userId) {
        venueGuestList = new ArrayList<String>();
        venueGuestList.add(userId);
        dayVenueRef.update("guestList", venueGuestList);
    }

    public void deleteUserFromVenueGuestList(String userId) {
        venueGuestList.remove(userId);
        dayVenueRef.update("guestList", venueGuestList);
    }

    /**
     * Set Up RecyclerView
     **/

    private void setUpRecyclerView(String venueName) {
        //TODO query should only include those where right array of the dayGiven contains venue Name

        query = db.collection("users").whereArrayContains(dateGiven, venueName);
        options = new FirestoreRecyclerOptions.Builder<User>()
                .setQuery(query, User.class)
                .build();

        adapter = new NationalitiesAdapter(options);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_attend_party);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }
}
