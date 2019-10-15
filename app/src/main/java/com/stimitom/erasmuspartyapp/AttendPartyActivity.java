package com.stimitom.erasmuspartyapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareButton;
import com.facebook.share.widget.ShareDialog;
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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AttendPartyActivity extends AppCompatActivity {
    private final String TAG = "AttendPartyActivity";
    Context context = this;

    private TextView venueName_TextView;
    private TextView venueRating_TextView;
    private ImageView venuePicture_ImageView;
    private TextView venueNumberOfAttendees_TextView;
    private TextView currentVenueState_TextView;
    private ShareButton facebookShareButton;

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

    private String dateGivenString;
    private Date dateGivenDate;
    private long usersVenueCountNumber;
    private String usersVenueCountName;
    private Boolean containsList;
    private Map<String, String> usersHashMap;
    private Boolean usersCounterMappingChanged;


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

        ShareLinkContent content = new ShareLinkContent.Builder()
                .setContentUrl(Uri.parse("https://developers.facebook.com"))
                .build();
        facebookShareButton = (ShareButton) findViewById(R.id.facebook_share_button);
        facebookShareButton.setShareContent(content);

        Intent intent = getIntent();
        final Venue venue = intent.getParcelableExtra("clickedVenue");
        dateGivenString = intent.getStringExtra("dateGiven");

        final String venue_name = venue.getVenueName();

        db = FirebaseFirestore.getInstance();
        dayVenueRef = db.collection("dates").document(dateGivenString)
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
        //facebookShareButton.setOnClickListener(facebookShareListener);
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
            if (containsList || ButtonIsRed) {
                Log.e(TAG, "onClick: Contains List");
                if (usersVenueCountNumber < 3 || ButtonIsRed) {
                    if (!ButtonIsRed) {
                        //Update db day_venue Side
                        dayVenueRef.update("numberOfAttendees", ++venueNumberOfAttendees);
                        addUserToVenueGuestList(currentUserId);

                        //Update db user side
                        addToUserListOfAttendedVenues();
                        makeButtonRed();
                        venueNumberOfAttendees_TextView.setText(Integer.toString(venueNumberOfAttendees));
                        ButtonIsRed = true;
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

            } else {
                //Update db day_venue Side
                dayVenueRef.update("numberOfAttendees", ++venueNumberOfAttendees);
                addUserToVenueGuestList(currentUserId);

                //Update db user side
                addToUserListOfAttendedVenues();
                makeButtonRed();
                venueNumberOfAttendees_TextView.setText(Integer.toString(venueNumberOfAttendees));
                ButtonIsRed = true;
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

    //sets containsList, usersVenueCountName and usersVenueCountNumber depending on state of user
    public void getUserData() {
        userRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        User user = documentSnapshot.toObject(User.class);
                        usersHashMap = new HashMap<>();
                        int listSize = user.getListnames().size();
                        Log.e(TAG, "onSuccess: List size : " + listSize);

                        //Check if user already has list for givenDate and adjust user accordingly
                        if (documentSnapshot.contains(dateGivenString)) {
                            usersCounterMappingChanged = false;
                            containsList = true;
                            usersVenueCountName = user.getCountermapping().get(dateGivenString);
                            switch (usersVenueCountName) {
                                case "counterpos0":
                                    usersVenueCountNumber = user.getCounterpos0();
                                    break;
                                case "counterpos1":
                                    usersVenueCountNumber = user.getCounterpos1();
                                    break;
                                case "counterpos2":
                                    usersVenueCountNumber = user.getCounterpos2();
                                    break;
                                default:
                                    Log.e(TAG, "onSuccess: no correct counter found... Should not happen!");
                                    break;
                            }
                        } else {
                            Log.e(TAG, "onSuccess: switchStatement in else reached");
                            usersCounterMappingChanged = true;
                            // new List wil be initialized,a counter needs to be set
                            switch (listSize) {
                                case 3:
                                    cleanUser(user.getCountermapping());
                                    break;
                                case 2:
                                    usersVenueCountName = "counterpos2";
                                    usersHashMap.putAll(user.getCountermapping());
                                    usersHashMap.put(dateGivenString, usersVenueCountName);
                                    break;
                                case 1:
                                    usersVenueCountName = "counterpos1";
                                    usersHashMap.putAll(user.getCountermapping());
                                    usersHashMap.put(dateGivenString, usersVenueCountName);
                                    break;
                                default:
                                    usersVenueCountName = "counterpos0";
                                    usersHashMap.put(dateGivenString, usersVenueCountName);
                                    break;
                            }
                            Log.e(TAG, "onSuccess: counterName: " + usersVenueCountName);

                            containsList = false;
                            usersVenueCountNumber = 0;
                            Log.e(TAG, "onSuccess: counternumber" + usersVenueCountNumber );
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

    public void cleanUser(Map<String, String> counterMapping) {
        String oldestDateString = getOldestDateString(counterMapping);

        //Find the oldest countername in the hashmap
        String oldestCounter = usersHashMap.get(oldestDateString);

        //remove the oldest key-value pair from the hashmap
        usersHashMap.remove(oldestDateString);

        //set the venuecountname to the oldest one which is now free
        usersVenueCountName = oldestCounter;

        //add the new date to the hashmap
        usersHashMap.put(dateGivenString,usersVenueCountName);

        //Clean oldest Date from Listnames
        userRef.update("listnames", FieldValue.arrayRemove(oldestDateString));

    }

    public String getOldestDateString(Map<String,String> counterMapping){
        //Finds and cleans the oldest counter and deletes it from map
        //returns String of the oldest COunter that can be used again
        usersHashMap.putAll(counterMapping);

        Date oldestDate = null;
        Date checkDate = null;
        DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        for (String dateStringKey : usersHashMap.keySet()) {
            if (oldestDate == null) {
                try {
                    oldestDate = formatter.parse(dateStringKey);
                    Log.e(TAG, "cleanUser: oldestDate start:"  +oldestDate );
                } catch (ParseException e) {
                    Log.e(TAG, "onCreate: date could not be parsed" + e.toString());
                }
            } else {
                try {
                    checkDate = formatter.parse(dateStringKey);
                    Log.e(TAG, "cleanUser: checkDAte" + checkDate);
                } catch (ParseException e) {
                    Log.e(TAG, "onCreate: date could not be parsed" + e.toString());
                }
                if (checkDate.before(oldestDate)) {
                    oldestDate = checkDate;
                    Log.e(TAG, "cleanUser: if checkdate round1 before oldest , oldest: " +oldestDate );
                }
            }
        }


        String oldestDateString = formatter.format(oldestDate);
        return oldestDateString;
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
        userRef.update(dateGivenString, FieldValue.arrayUnion(venueName));
        Log.e(TAG, "addToUserListOfAttendedVenues: usersVenueCountName" +usersVenueCountName );
        userRef.update(usersVenueCountName, usersVenueCountNumber);
        userRef.update("listnames", FieldValue.arrayUnion(dateGivenString));
        if (usersCounterMappingChanged) userRef.update("countermapping", usersHashMap);

        Log.d(TAG, "addToUserListOfAttendedVenues: Updated");
    }

    public void deleteFromUserListOfAttendedVenues() {
        // Update Local variables
        usersVenueCountNumber--;
        //Update db Variables
        userRef.update(dateGivenString, FieldValue.arrayRemove(venueName));
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

        query = db.collection("users").whereArrayContains(dateGivenString, venueName);
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
