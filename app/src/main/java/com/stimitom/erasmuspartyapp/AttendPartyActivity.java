package com.stimitom.erasmuspartyapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

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
    private ImageView shareButton_ImageView;
    private Toolbar myToolbar;
    private TextView myToolbarTitle;
    private RatingBar ratingBar;
    private ProgressBar progressBar;
    private TextView dateTextView;

    private Button attendButton;
    private Boolean buttonIsClicked;


    private FirebaseFirestore db;
    private FirebaseUser firebaseUser;
    DocumentReference userRef;
    DocumentReference dayVenueRef;

    private RecyclerView recyclerView;
      private CountriesAdapter adapter;

    private String venueName;
    private String venueRating;
    private int venueImageId;
    private int venueNumberOfAttendees;
    private String venueLocation;
    private List<String> venueGuestList;
    private List<String> venueOpeningHoursList;
    private ArrayList<String> venueNationalitiesList;
    private Map<String,String> venueUsersNationalitiesMap;


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
        shareButton_ImageView = (ImageView)findViewById(R.id.share_button);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.INVISIBLE);
        dateTextView = (TextView)findViewById(R.id.date_text_view);

        Intent intent = getIntent();
        String venue_name = intent.getStringExtra("venueName");
        dateGivenString = intent.getStringExtra("dateGiven");
        String dayText = intent.getStringExtra("dayText");
        city = intent.getStringExtra("city");

        dateTextView.setText(dayText);

        db = FirebaseFirestore.getInstance();
        dayVenueRef = db.collection(city + "_dates").document(dateGivenString)
                .collection("day_venues")
                .document(venue_name);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        // Check current firebaseUser
        if (firebaseUser != null) {
            currentUserId = firebaseUser.getUid();
            userRef = db.collection("users").document(currentUserId);
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

        venueNationalitiesList = new ArrayList<String>();
        readVenueData(venueDataListener);
        attendButton.setOnClickListener(attendButtonListener);
        setUpRecyclerView(venue_name);
    }


    /**** UserInterface ****/

    public void setButtonColorAndText() {

        if (venueGuestList != null) {
            if (venueGuestList.contains(currentUserId)) {
                buttonIsClicked = true;
                makeButtonClicked();
            } else {
                buttonIsClicked = false;
                makeButtonUnclicked();
            }
        } else {
            buttonIsClicked = false;
            makeButtonUnclicked();
        }

    }

    public void makeButtonUnclicked() {
        attendButton.setText(R.string.attend);
        attendButton.setTextColor(getResources().getColor(R.color.colorGreenButton));
        attendButton.setBackgroundResource(R.drawable.button_inverted_green_round);
    }

    public void makeButtonClicked() {
        attendButton.setText(R.string.dontgo);
        attendButton.setTextColor(getResources().getColor(R.color.colorWhite));
        attendButton.setBackgroundResource(R.drawable.button_green_round);
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
            venueUsersNationalitiesMap = new HashMap<String,String>();
            venueUsersNationalitiesMap.putAll(venue.getUsersNationalitiesMap());

            if (venue.getGuestList().size() != 0) {
                venueGuestList.addAll(venue.getGuestList());
                for (String nationality : venueUsersNationalitiesMap.values()) {
                    if (!venueNationalitiesList.contains(nationality)) venueNationalitiesList.add(nationality);
                }
                adapter.notifyDataSetChanged();
            }

            venueName = venue.getVenueName();
            venueRating = venue.getRating();
            venueImageId = venue.getImageId();
            venueNumberOfAttendees = venue.getNumberOfAttendees();
            venueLocation = venue.getLocation();
            venueOpeningHoursList = venue.getOpeningHours();

            myToolbarTitle.setText(venueName);
            venuePicture_ImageView.setImageResource(venueImageId);
            ratingBar.setRating(Float.parseFloat(venueRating.replace(",", ".") + "f"));
            venueNumberOfAttendees_TextView.setText(Integer.toString(venueNumberOfAttendees));
            venueOpeningHours_TextView.setText(getFormattedOpeningHours(venueOpeningHoursList));
            shareButton_ImageView.setOnClickListener(shareButtonListener);

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
            progressBar.setVisibility(View.VISIBLE);
            if (containsList || buttonIsClicked) {
                Log.e(TAG, "onClick: Contains List");
                if (usersVenueCountNumber < 3 || buttonIsClicked) {
                    if (!buttonIsClicked) {
                        //Update db day_venue Side
                        WriteBatch addBatch = db.batch();
                        ++venueNumberOfAttendees;
                        addUserToVenueGuestList(addBatch);

                        //Update db user side
                        addToUserListOfAttendedVenues(addBatch);
                        makeButtonClicked();
                        setDescriptiveText();

                        //Write Batch to db
                        addBatch.commit().addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e(TAG, "onFailure: " + e.toString());
                                Toast.makeText(context,"Sorry this operation did not work.Please try again later.", Toast.LENGTH_SHORT);
                            }
                        });

                        venueNumberOfAttendees_TextView.setText(Integer.toString(venueNumberOfAttendees));
                        buttonIsClicked = true;
                    } else {
                        WriteBatch deleteBatch = db.batch();
                        //Update db day_venue side
                        deleteUserFromVenueGuestList(deleteBatch);
                        --venueNumberOfAttendees;

                        // Update db user side
                        deleteFromUserListOfAttendedVenues(deleteBatch);
                        makeButtonUnclicked();
                        setDescriptiveText();

                        //WriteBatchToDb
                        deleteBatch.commit().addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e(TAG, "onFailure: " + e.toString());
                                Toast.makeText(context,"Sorry this operation did not work.Please try again later.", Toast.LENGTH_SHORT);
                            }
                        });

                        venueNumberOfAttendees_TextView.setText(Integer.toString(venueNumberOfAttendees));
                        buttonIsClicked = false;
                    }
                } else {
                    Toast.makeText(context, "Sorry, you can only attend 3 venues per night!", Toast.LENGTH_SHORT).show();
                }

            } else {
                WriteBatch addBatch = db.batch();
                //Update db day_venue Side
                ++venueNumberOfAttendees;
                addUserToVenueGuestList(addBatch);

                //Update db user side
                addToUserListOfAttendedVenues(addBatch);
                makeButtonClicked();
                setDescriptiveText();

                //Write batch to db
                addBatch.commit().addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "onFailure: " + e.toString());
                        Toast.makeText(context,"Sorry something went wrong.Please try again later.", Toast.LENGTH_SHORT);
                    }
                });

                venueNumberOfAttendees_TextView.setText(Integer.toString(venueNumberOfAttendees));
                buttonIsClicked = true;
            }
            progressBar.setVisibility(View.INVISIBLE);
        }
    };

    View.OnClickListener shareButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, "Hey! Come join me at " + venueName + " tonight!");
            sendIntent.putExtra(Intent.EXTRA_SUBJECT, "com.stimitom.erasmuspartyapp");
            sendIntent.setType("text/plain");
            startActivity(Intent.createChooser(sendIntent,null));
        }
    };


    /**
     * RecyclerView
     **/

    private void setUpRecyclerView(String venueName) {
      adapter = new CountriesAdapter(venueNationalitiesList);
      recyclerView = (RecyclerView) findViewById(R.id.recycler_view_attend_party);
      recyclerView.setLayoutManager(new LinearLayoutManager(this));
      recyclerView.setAdapter(adapter);
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


    public void addToUserListOfAttendedVenues(WriteBatch batch) {
        //Update Local Variable
        usersVenueCountNumber++;
        //Update Db variables
        batch.update(userRef,dateGivenString, FieldValue.arrayUnion(venueName));
        batch.update(userRef,usersVenueCountName, usersVenueCountNumber);
        batch.update(userRef,"listnames", FieldValue.arrayUnion(dateGivenString));
        if (usersCounterMappingChanged) batch.update(userRef,"countermapping", usersHashMap);

    }

    public void deleteFromUserListOfAttendedVenues(WriteBatch batch) {
        // Update Local variables
        usersVenueCountNumber--;
        //Update db Variables
        batch.update(userRef,dateGivenString, FieldValue.arrayRemove(venueName));
        batch.update(userRef,usersVenueCountName, usersVenueCountNumber);
    }

    /**
     * Venue Side
     **/

    public void addUserToVenueGuestList(WriteBatch batch) {
        venueGuestList.add(currentUserId);
        venueUsersNationalitiesMap.put(currentUserId,currentUserNationality);
        if (!venueNationalitiesList.contains(currentUserNationality)) {
            venueNationalitiesList.add(currentUserNationality);
            adapter.notifyDataSetChanged();
        }

        batch.update(dayVenueRef,"numberOfAttendees", FieldValue.increment(1L));
        batch.update(dayVenueRef,"usersNationalitiesMap",venueUsersNationalitiesMap);
        batch.update(dayVenueRef,"guestList", FieldValue.arrayUnion(currentUserId));

    }

    public void deleteUserFromVenueGuestList(WriteBatch batch) {
        venueGuestList.remove(currentUserId);
        venueUsersNationalitiesMap.remove(currentUserId);
        venueNationalitiesList.clear();
        venueNationalitiesList.addAll(venueUsersNationalitiesMap.values());
        adapter.notifyDataSetChanged();

        batch.update(dayVenueRef,"usersNationalitiesMap",venueUsersNationalitiesMap);
        batch.update(dayVenueRef,"guestList", FieldValue.arrayRemove(currentUserId));
        batch.update(dayVenueRef,"numberOfAttendees", FieldValue.increment(-1L));
    }

}
