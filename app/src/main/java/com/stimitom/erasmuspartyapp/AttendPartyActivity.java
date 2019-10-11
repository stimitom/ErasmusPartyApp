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
    private TextView currentVenueState;

    private Button attendButton;
    private Boolean ButtonIsRed;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    DocumentReference userRef;
    DocumentReference dayVenueRef;
    private String venueName;
    private String venueRating;
    private int venueImageId;
    private int venueNumberOfAttendees;
    private List<String> venueGuestList;
    private String currentUserId;

    private long usersCurrentVenueCount = 0;
    private List<String> usersCurrentAttendedVenuesList;
    private List<String> usersDateList;
    private String date;
    private String day;

    private String activeList;
    private String activeVenueCount;

    private Query query;
    private RecyclerView recyclerView;
    private NationalitiesAdapter adapter;
    private FirestoreRecyclerOptions<User> options;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attend_party);
        usersCurrentAttendedVenuesList = new ArrayList<String>();

        venueName_TextView = (TextView) findViewById(R.id.venue_name_tv);
        venueRating_TextView = (TextView) findViewById(R.id.venue_rating_tv);
        venuePicture_ImageView = (ImageView) findViewById(R.id.venue_picture_iv);
        attendButton = (Button) findViewById(R.id.attend_button);
        venueNumberOfAttendees_TextView = (TextView) findViewById(R.id.number_of_attendees1);
        currentVenueState = (TextView) findViewById(R.id.text_view_meet_people_from);

        Intent intent = getIntent();
        final Venue venue = intent.getParcelableExtra("clickedVenue");
        date = intent.getStringExtra("date");
        day = intent.getStringExtra("whichDay");

        final String venue_name = venue.getVenueName();


        dayVenueRef = db.collection("dates").document(date)
                .collection("day_venues")
                .document(venue_name);

        // Check current user
        if (user != null) {
            currentUserId = getUserId();
            userRef = db.collection("users")
                    .document(currentUserId);
        } else {
            Intent intent1 = new Intent(context, LoginActivity.class);
            context.startActivity(intent1);
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
                setDescriptiveText();

            }
        });


        attendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (usersCurrentVenueCount < 3 || ButtonIsRed) {
                    if (!ButtonIsRed) {
                        Log.d(TAG, "onClick: update of Venue and User will be performed");
                        // Update db venueSide
                        dayVenueRef.update("numberOfAttendees", ++venueNumberOfAttendees);
                        addUserToVenueGuestList(currentUserId);
                        //update db userSide
                        addToUserListOfAttendedVenues(venueName);
                        makeButtonRed();
                        venueNumberOfAttendees_TextView.setText(Integer.toString(venueNumberOfAttendees));
                        ButtonIsRed = true;
                    } else {
                        //update db venueSide
                        deleteUserFromVenueGuestList(currentUserId);
                        dayVenueRef.update("numberOfAttendees", --venueNumberOfAttendees);
                        //update db userSide
                        deleteFromUserListOfAttendedVenues(venueName);
                        makeButtonGreen();
                        venueNumberOfAttendees_TextView.setText(Integer.toString(venueNumberOfAttendees));
                        ButtonIsRed = false;
                    }
                } else {
                    Toast.makeText(context, "Sorry, you can only attend 3 venues per night!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        setUpRecyclerView(venue_name);
    }

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
                        List<String> usersDateList = new ArrayList<String>();
                        if (user.getDateList() != null) {
                            //random unreachable number
                            int listChooser = 99;
                            for (int i = 0; i < user.getDateList().size(); i++) {
                                if (user.getDateList().get(i).equals(date)) {
                                    listChooser = i;
                                    i = user.getDateList().size();
                                }
                            }
                            if (listChooser != 99) {
                                // that means that date was already in the list

                                switch (listChooser) {
                                    case 0:
                                        activeList = "venuesattendingTD";
                                        activeVenueCount = "venuecountTD";
                                        usersCurrentVenueCount = user.getVenuecountTD();
                                        if (usersCurrentVenueCount != 0) {
                                            usersCurrentAttendedVenuesList.addAll(user.getVenuesattendingTD());
                                        }
                                        break;
                                    case 1:
                                        activeList = "venuesattendingTM";
                                        activeVenueCount = "venuecountTM";
                                        usersCurrentVenueCount = user.getVenuecountTM();
                                        if (usersCurrentVenueCount != 0) {
                                            usersCurrentAttendedVenuesList.addAll(user.getVenuesattendingTM());
                                        }
                                        break;
                                    case 2:
                                        activeList = "venuesattendingTDAT";
                                        activeVenueCount = "venuecountTDAT";
                                        usersCurrentVenueCount = user.getVenuecountTDAT();
                                        if (usersCurrentVenueCount != 0) {
                                            usersCurrentAttendedVenuesList.addAll(user.getVenuesattendingTDAT());
                                        }
                                        break;
                                    default:
                                        activeList = null;
                                        Log.e(TAG, "SWITCH STATEMENT: something went wrong in getUserData");
                                        break;
                                }
                            }else{
                                //date was not in the list
                                if (day.equals("today")){
                                    //TODO A LOT
                                }

                            }
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
            currentVenueState.setText(R.string.nobody_attends_yet);
        } else currentVenueState.setText(R.string.nationalities_tonight);
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

    public void addToUserListOfAttendedVenues(String venueName) {
        //Update Local Variables
        usersCurrentAttendedVenuesList.add(venueName);
        usersCurrentVenueCount++;
        //Update Db variables
        userRef.update(activeList, usersCurrentAttendedVenuesList);
        userRef.update(activeVenueCount, usersCurrentVenueCount);
        Log.d(TAG, "addToUserListOfAttendedVenues: Updated");
    }

    public void deleteFromUserListOfAttendedVenues(String venueName) {
        // Update Local variables
        usersCurrentAttendedVenuesList.remove(venueName);
        usersCurrentVenueCount--;
        //Update db Variables
        userRef.update(activeList, usersCurrentAttendedVenuesList);
        userRef.update(activeVenueCount, usersCurrentVenueCount);
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
        query = db.collection("users").whereArrayContains("venuesattending", venueName);
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


//    private void setUpRecyclerView(String venueName) {
//        nationalitiesAttending = new ArrayList<String>();
//        db.collection("users")
//                .whereArrayContains("venuesattending", venueName)
//                .get()
//                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//                    @Override
//                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//
//                        for (QueryDocumentSnapshot documentSnapshot: queryDocumentSnapshots){
//                            User user = documentSnapshot.toObject(User.class);
//                            String nationality = user.getNationality();
//                            if (!nationalitiesAttending.contains(nationality)){
//                                nationalitiesAttending.add(nationality);
//                            }
//                        }
//                        if (nationalitiesAttending.size() != 0) {
//                            currentVenueState.setText(R.string.nationalities_tonight);
//                            recyclerView = (RecyclerView) findViewById(R.id.recycler_view_attend_party);
//                            adapter = new CountriesAdapter(nationalitiesAttending);
//                            recyclerView.setHasFixedSize(true);
//                            recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
//                            recyclerView.setAdapter(adapter);
//                        }else {
//                            currentVenueState.setText(R.string.nobody_attends_yet);
//
//                        }
//                    }
//                });
//
//    }