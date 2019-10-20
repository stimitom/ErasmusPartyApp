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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareButton;
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

    private ImageView venuePicture_ImageView;
    private TextView venueNumberOfAttendees_TextView;
    private TextView currentVenueState_TextView;
    private TextView venueOpeningHours_TextView;
    private ShareButton facebookShareButton;
    private Toolbar myToolbar;
    private TextView myToolbarTitle;
    private RatingBar ratingBar;

    private Button attendButton;
    private Boolean ButtonIsRed;


    private FirebaseFirestore db;
    private FirebaseUser firebaseUser;
    DocumentReference userRef;
    DocumentReference dayVenueRef;

    private Query query;
    private RecyclerView recyclerView;
    private NationalitiesAdapter adapter;
    private FirestoreRecyclerOptions<User> options;

    private String venueName;
    private String venueRating;
    private int venueImageId;
    private int venueNumberOfAttendees;
    private String venueLocation;
    private List<String> venueGuestList;
    private List<String> venueNationsList;
    private List<String> venueOpeningHoursList;
    private ArrayList<String> cleanedNationalities;

    private String dateGivenString;
    private String city;

    private String currentUserId;
    private String currentUserNationality;
    private long usersVenueCountNumber;
    private String usersVenueCountName;
    private Boolean containsList;
    private Map<String, String> usersHashMap;
    private Boolean usersCounterMappingChanged;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attend_party);
        myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        myToolbarTitle = (TextView) myToolbar.findViewById(R.id.toolbar_text_centered);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        venuePicture_ImageView = (ImageView) findViewById(R.id.venue_picture_iv);
        attendButton = (Button) findViewById(R.id.attend_button);
        venueNumberOfAttendees_TextView = (TextView) findViewById(R.id.number_of_attendees1);
        currentVenueState_TextView = (TextView) findViewById(R.id.text_view_meet_people_from);
        venueOpeningHours_TextView = (TextView) findViewById(R.id.opening_hours);
        ratingBar = (RatingBar) findViewById(R.id.rating_bar);

        ShareLinkContent content = new ShareLinkContent.Builder()
                .setContentUrl(Uri.parse("https://developers.facebook.com"))
                .build();
        facebookShareButton = (ShareButton) findViewById(R.id.facebook_share_button);
        facebookShareButton.setShareContent(content);

        Intent intent = getIntent();
        String venue_name = intent.getStringExtra("venueName");
        dateGivenString = intent.getStringExtra("dateGiven");
        city = intent.getStringExtra("city");


        db = FirebaseFirestore.getInstance();
        dayVenueRef = db.collection(city + "_dates").document(dateGivenString)
                .collection("day_venues")
                .document(venue_name);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();


        // Check current firebaseUser
        if (firebaseUser != null) {
            currentUserId = getUserId();
            userRef = db.collection("users")
                    .document(currentUserId);

            userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    currentUserNationality = documentSnapshot.get("nationality").toString();
                }
            });

        } else {
            Intent intent1 = new Intent(context, LoginActivity.class);
            context.startActivity(intent1);
        }

        readVenueData(venueDataListener);
        attendButton.setOnClickListener(attendButtonListener);
        //facebookShareButton.setOnClickListener(facebookShareListener);
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

    /**** UserInterface ****/

    public void setButtonColorAndText() {

        if (venueGuestList != null) {
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
     * Listener
     **/

    GetDataListener venueDataListener = new GetDataListener() {
        @Override
        public void onSuccess(DocumentSnapshot snapshot) {
            Venue venue = snapshot.toObject(Venue.class);
            venueGuestList = new ArrayList<String>();
            venueNationsList = new ArrayList<String>();
            if (venue.getGuestList() != null) {
                venueGuestList.addAll(venue.getGuestList());
                venueNationsList.addAll(venue.getNationalitiesList());
            }
            venueName = venue.getVenueName();
            venueRating = venue.getRating();
            venueImageId = venue.getImageId();
            venueNumberOfAttendees = venue.getNumberOfAttendees();
            venueLocation = venue.getLocation();
            venueOpeningHoursList = venue.getOpeningHours();

            myToolbarTitle.setText(venueName);
            venuePicture_ImageView.setImageResource(venueImageId);
            ratingBar.setRating(Float.parseFloat(venueRating.replace(",", ".l") + "f"));
            venueNumberOfAttendees_TextView.setText(Integer.toString(venueNumberOfAttendees));
            venueOpeningHours_TextView.setText(getFormattedOpeningHours(venueOpeningHoursList));


            //Called here to ensure sequential execution
            getUserData();
            setButtonColorAndText();
            setDescriptiveText();
        }
    };

    public String getFormattedOpeningHours(List<String> venueOpeningHours) {
        String helpArray[] = new String[7];
        //To bring the days in the correct order
        for (String day : venueOpeningHours) {
            switch (day.substring(0, 2)) {
                case "Mo":
                    helpArray[0] = day;
                    break;
                case "Tu":
                    helpArray[1] = day;
                    break;
                case "We":
                    helpArray[2] = day;
                    break;
                case "Th":
                    helpArray[3] = day;
                    break;
                case "Fr":
                    helpArray[4] = day;
                    break;
                case "Sa":
                    helpArray[5] = day;
                    break;
                case "Su":
                    helpArray[6] = day;
                    break;
                default:
                    Log.e(TAG, "onSuccess: Something went wrong with the opening Hours");
                    break;
            }
        }
        StringBuilder builder = new StringBuilder();
        for (String day : helpArray) {
            builder.append(day + "\n");
        }
        return builder.toString();
    }


    View.OnClickListener attendButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (containsList || ButtonIsRed) {
                Log.e(TAG, "onClick: Contains List");
                if (usersVenueCountNumber < 3 || ButtonIsRed) {
                    if (!ButtonIsRed) {
                        //Update db day_venue Side
                        dayVenueRef.update("numberOfAttendees", ++venueNumberOfAttendees);
                        addUserToVenueGuestList();

                        //Update db user side
                        addToUserListOfAttendedVenues();
                        makeButtonRed();
                        venueNumberOfAttendees_TextView.setText(Integer.toString(venueNumberOfAttendees));
                        ButtonIsRed = true;
                    } else {
                        //Update db day_venue side
                        deleteUserFromVenueGuestList();
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
                addUserToVenueGuestList();

                //Update db user side
                addToUserListOfAttendedVenues();
                makeButtonRed();
                venueNumberOfAttendees_TextView.setText(Integer.toString(venueNumberOfAttendees));
                ButtonIsRed = true;
            }
        }
    };


    /**
     * RecyclerView
     **/

    private void setUpRecyclerView(String venueName) {
        query = db.collection("users").whereArrayContains(dateGivenString, venueName);
        options = new FirestoreRecyclerOptions.Builder<User>()
                .setQuery(query, User.class)
                .build();

        adapter = new NationalitiesAdapter(options, dayVenueRef);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_attend_party);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    /**
     * Toolbar
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_attend_party_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_show_venue_on_map:
                Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                intent.putExtra("location", venueLocation);
                intent.putExtra("city", city);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**** GET DATA FROM DATABASE ****/

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
                            Log.e(TAG, "onSuccess: counternumber" + usersVenueCountNumber);
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
        usersHashMap.put(dateGivenString, usersVenueCountName);

        //Clean oldest Date from Listnames
        userRef.update("listnames", FieldValue.arrayRemove(oldestDateString));

    }

    public String getOldestDateString(Map<String, String> counterMapping) {
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
                } catch (ParseException e) {
                    Log.e(TAG, "onCreate: date could not be parsed" + e.toString());
                }
            } else {
                try {
                    checkDate = formatter.parse(dateStringKey);
                } catch (ParseException e) {
                    Log.e(TAG, "onCreate: date could not be parsed" + e.toString());
                }
                if (checkDate.before(oldestDate)) {
                    oldestDate = checkDate;
                }
            }
        }


        String oldestDateString = formatter.format(oldestDate);
        return oldestDateString;
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


    /**** UPDATE DATABASE ****/

    /**
     * User Side
     **/

    public void addToUserListOfAttendedVenues() {
        //Update Local Variable
        usersVenueCountNumber++;
        //Update Db variables
        userRef.update(dateGivenString, FieldValue.arrayUnion(venueName));
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

    public void addUserToVenueGuestList() {
        venueGuestList.add(currentUserId);
        venueNationsList.add(currentUserNationality);
//        updateRecyclerView();
        dayVenueRef.update("guestList", FieldValue.arrayUnion(currentUserId));
        dayVenueRef.update("nationalitiesList", FieldValue.arrayUnion(currentUserNationality));

    }

    public void deleteUserFromVenueGuestList() {
        venueGuestList.remove(currentUserId);
        venueNationsList.remove(currentUserNationality);
//        updateRecyclerView();
        dayVenueRef.update("guestList", FieldValue.arrayRemove(currentUserId));
        dayVenueRef.update("nationalitiesList", FieldValue.arrayRemove(currentUserNationality));

    }


//    private void setUpNationsRecyclerView(ArrayList<String> cleanedList) {
//
//        cleanedNationalities = new ArrayList<String>();
//        for (String nation: venueNationsList) {
//            if (!cleanedNationalities.contains(nation)) cleanedNationalities.add(nation);
//        }
//
//        adapter = new NationsAdapter(cleanedList);
//        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_attend_party);
//        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
//        recyclerView.setAdapter(adapter);
//    }
//
//    private void updateRecyclerView(){
//        if (cleanedNationalities == null) cleanedNationalities = new ArrayList<String>();
//        else cleanedNationalities.clear();
//        for (String nation: venueNationsList) {
//            if (!cleanedNationalities.contains(nation)) cleanedNationalities.add(nation);
//        }
//        adapter.notifyDataSetChanged();
//    }
}
