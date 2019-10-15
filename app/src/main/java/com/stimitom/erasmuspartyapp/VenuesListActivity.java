package com.stimitom.erasmuspartyapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class VenuesListActivity extends AppCompatActivity {
    private final String TAG = "VenuesListActivity";
    private Context context = this;
    public static Activity reloader;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static CollectionReference dayVenuesRef;
    private RecyclerView recyclerView;
    private FirebaseUser user;
    DocumentReference userRef;

    private Button popularButton;
    private Button alphabeticButton;
    private Boolean popularSortActive;

    private Button dateButton;
    private String today;
    private String tomorrow;
    private String theDayAfterTomorrow;

    private Boolean todayBool;
    private Boolean tomorrowBool;

    private VenuesAdapter popularAdapter;
    private VenuesAdapter alphabeticAdapter;

    private ShimmerFrameLayout shimmerViewContainer;

    private Boolean  newDayExistsInD = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_venues_list);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        popularButton = (Button) findViewById(R.id.button_popular);
        alphabeticButton = (Button) findViewById(R.id.button_alphabetic);
        dateButton = (Button) findViewById(R.id.button_date);

        shimmerViewContainer = (ShimmerFrameLayout) findViewById(R.id.shimmer_view_container);

        popularButton.setBackgroundResource(R.drawable.button_venues_list_selected);
        alphabeticButton.setBackgroundResource(R.drawable.button_venues_list_not_selected);
        popularButton.setOnClickListener(popularSortListener);
        alphabeticButton.setOnClickListener(alphabeticSortListener);

        setUpDateButton();
       // setUpThreeDaysInDB();
          setUpNewDayInDB();

        reloader = this;
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            setDayVenuesRef();
            checkIfDialogNeeded();
        } else {
            Intent intent = new Intent(context, LoginActivity.class);
            context.startActivity(intent);
        }


        setUpPopularRecyclerView(true, false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "onResume: called" );
        shimmerViewContainer.startShimmerAnimation();
    }

    @Override
    protected void onStart() {
        Log.e(TAG, "onStart: called" );
        super.onStart();
        if (popularSortActive) popularAdapter.startListening();
        else alphabeticAdapter.startListening();
    }


    @Override
    protected void onStop() {
        super.onStop();
        if (popularSortActive) popularAdapter.stopListening();
        else alphabeticAdapter.stopListening();
    }


    /**
     * RecyclerView setups, Sorting
     **/

    private void setUpPopularRecyclerView(Boolean firstSetup, Boolean daySwitch) {
        if (daySwitch) {
            setDayVenuesRef();
            popularAdapter.stopListening();
        }
        Query query = dayVenuesRef.orderBy("numberOfAttendees", Query.Direction.DESCENDING)
                .orderBy("venueName", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<Venue> options = new FirestoreRecyclerOptions.Builder<Venue>()
                .setQuery(query, Venue.class)
                .build();
        popularAdapter = new VenuesAdapter(options, shimmerViewContainer);

        if (firstSetup) {
            recyclerView = (RecyclerView) findViewById(R.id.recycler_view_venues_list);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
        } else {
            if (!daySwitch) {
                alphabeticAdapter.stopListening();
            }
        }
        recyclerView.setAdapter(popularAdapter);
        attachItemClickListenerToAdapter(popularAdapter);

        popularAdapter.startListening();
        popularSortActive = true;
    }

    public void setUpAlphabeticRecyclerView(Boolean daySwitch) {
        if (daySwitch) {
            setDayVenuesRef();
            alphabeticAdapter.stopListening();
        }
        Query query = dayVenuesRef.orderBy("venueName", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<Venue> options = new FirestoreRecyclerOptions.Builder<Venue>()
                .setQuery(query, Venue.class)
                .build();
        alphabeticAdapter = new VenuesAdapter(options,shimmerViewContainer);
        popularAdapter.stopListening();
        recyclerView.setAdapter(alphabeticAdapter);
        attachItemClickListenerToAdapter(alphabeticAdapter);
        alphabeticAdapter.startListening();
        popularSortActive = false;
    }

    public void setDayVenuesRef() {
        if (todayBool) {
            dayVenuesRef = db.collection("dates")
                    .document(today)
                    .collection("day_venues");
        } else if (tomorrowBool) {
            dayVenuesRef = db.collection("dates")
                    .document(tomorrow)
                    .collection("day_venues");
        } else {
            dayVenuesRef = db.collection("dates")
                    .document(theDayAfterTomorrow)
                    .collection("day_venues");
        }
    }

    public void attachItemClickListenerToAdapter(VenuesAdapter adapter) {
        /**Handles the Clicks**/
        adapter.setOnItemClickListener(new VenuesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                Venue clickedVenue = documentSnapshot.toObject(Venue.class);
                Intent intent = new Intent(context, AttendPartyActivity.class);
                intent.putExtra("clickedVenue", clickedVenue);
                String day;
                if (todayBool) {
                    day = today;
                } else if (tomorrowBool) {
                    day = tomorrow;
                } else {
                    day = theDayAfterTomorrow;
                }
                intent.putExtra("dateGiven", day);
                startActivity(intent);
            }

        });
    }




    View.OnClickListener popularSortListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!popularSortActive) {
                popularButton.setBackgroundResource(R.drawable.button_venues_list_selected);
                alphabeticButton.setBackgroundResource(R.drawable.button_venues_list_not_selected);
                setUpPopularRecyclerView(false, false);
            }
        }
    };


    View.OnClickListener alphabeticSortListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (popularSortActive) {
                alphabeticButton.setBackgroundResource(R.drawable.button_venues_list_selected);
                popularButton.setBackgroundResource(R.drawable.button_venues_list_not_selected);
                setUpAlphabeticRecyclerView(false);
            }
        }
    };


    /********************************/
    /*** ACTION BAR METHODS **/

    //Inflates the menu's XML file to the Action Bar
    //and inflates the searchView in the Action bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_venues_list_activity, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(searchListener);
        return true;
    }

    //Search function
    SearchView.OnQueryTextListener searchListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String query) {
            return false;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            Query query = dayVenuesRef.orderBy("venueName", Query.Direction.ASCENDING).startAt(newText.toUpperCase());

            FirestoreRecyclerOptions<Venue> options = new FirestoreRecyclerOptions.Builder<Venue>()
                    .setQuery(query, Venue.class)
                    .build();
            VenuesAdapter searchAdapter = new VenuesAdapter(options,shimmerViewContainer);
            if (newText.trim().isEmpty()) {
                searchAdapter.stopListening();
                if (popularSortActive) {
                    recyclerView.setAdapter(popularAdapter);
                    attachItemClickListenerToAdapter(popularAdapter);
                    popularAdapter.startListening();
                } else {
                    recyclerView.setAdapter(alphabeticAdapter);
                    attachItemClickListenerToAdapter(alphabeticAdapter);
                    alphabeticAdapter.startListening();
                }
                return false;
            } else {
                if (popularSortActive) {
                    popularAdapter.stopListening();
                } else {
                    alphabeticAdapter.stopListening();
                }
                recyclerView.setAdapter(searchAdapter);
                attachItemClickListenerToAdapter(searchAdapter);
                searchAdapter.startListening();
                return false;
            }

        }
    };

    //Checks which item in the Action Bar was clicked and performs its action
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_request_venue:
                Intent intent = new Intent(this, RequestVenueActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_logout:
                AuthUI.getInstance()
                        .signOut(context)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            public void onComplete(@NonNull Task<Void> task) {
                                // user is now signed out
                                startActivity(new Intent(context, RegistrationActivity.class));
                                finish();
                            }
                        });
                return true;

            case R.id.action_about:
                Intent intent1 = new Intent(this,AboutActivity.class);
                startActivity(intent1);
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }


    /**
     * DIALOG
     **/
    public void openDialog() {
        UsernameNationalityDialog dialog = new UsernameNationalityDialog();
        dialog.show(getSupportFragmentManager(), "UsernameNationalityDialog");
    }


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

    public void checkIfDialogNeeded() {
        userRef = db.collection("users").document(getUserId());
        userRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (!documentSnapshot.contains("nationality")) openDialog();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "onFailure: Could not fetch UserData" + e.toString());
                    }
                });
    }

    /**
     * Date Button
     **/

    public void setUpDateButton() {
        today = DatabaseMethods.getDateToday();
        tomorrow = DatabaseMethods.getDateTomorrow();
        theDayAfterTomorrow = DatabaseMethods.getDateTheDayAfterTomorrow();

        dateButton.setText(today);

        todayBool = true;
        tomorrowBool = false;
        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (todayBool) {
                    dateButton.setText(tomorrow);
                    todayBool = false;
                    tomorrowBool = true;

                } else if (tomorrowBool) {
                    dateButton.setText(theDayAfterTomorrow);
                    tomorrowBool = false;
                } else {
                    dateButton.setText(today);
                    todayBool = true;
                }
                if (popularSortActive) setUpPopularRecyclerView(false, true);
                else setUpAlphabeticRecyclerView(true);
            }
        });
    }

    /**
     * DB Method
     **/
    public void setUpThreeDaysInDB() {
        DatabaseMethods.saveDayVenueToDB(today, new Venue("Dzempub", R.drawable.bk_logo, "3/5"));
        DatabaseMethods.saveDayVenueToDB(today, new Venue("Taboo", R.drawable.bk_logo, "2/5"));
        DatabaseMethods.saveDayVenueToDB(today, new Venue("Listas", R.drawable.bk_logo, "1/5"));
        DatabaseMethods.saveDayVenueToDB(today, new Venue("DejaVu", R.drawable.bk_logo, "3/5"));
        DatabaseMethods.saveDayVenueToDB(today, new Venue("Pjazz", R.drawable.bk_logo, "4/5"));
        DatabaseMethods.saveDayVenueToDB(today, new Venue("B20", R.drawable.bk_logo, "3/5"));
        DatabaseMethods.saveDayVenueToDB(today, new Venue("Blue", R.drawable.bk_logo, "3/5"));
        DatabaseMethods.saveDayVenueToDB(today, new Venue("Green", R.drawable.bk_logo, "3/5"));
        DatabaseMethods.saveDayVenueToDB(today, new Venue("Yellow", R.drawable.bk_logo, "3/5"));
        DatabaseMethods.saveDayVenueToDB(today, new Venue("Brown", R.drawable.bk_logo, "2/5"));
        DatabaseMethods.saveDayVenueToDB(today, new Venue("Black", R.drawable.bk_logo, "3/5"));
        DatabaseMethods.saveDayVenueToDB(today, new Venue("Grey", R.drawable.bk_logo, "3/5"));
        DatabaseMethods.saveDayVenueToDB(today, new Venue("White", R.drawable.bk_logo, "4/5"));
        DatabaseMethods.saveDayVenueToDB(today, new Venue("Purple", R.drawable.bk_logo, "3/5"));
        DatabaseMethods.saveDayVenueToDB(today, new Venue("Red", R.drawable.bk_logo, "1/5"));
        DatabaseMethods.saveDayVenueToDB(today, new Venue("Magenta", R.drawable.bk_logo, "5/5"));
        DatabaseMethods.saveDayVenueToDB(today, new Venue("Some Shithole", R.drawable.bk_logo, "5/5"));


        DatabaseMethods.saveDayVenueToDB(tomorrow, new Venue("Dzempub", R.drawable.bk_logo, "3/5"));
        DatabaseMethods.saveDayVenueToDB(tomorrow, new Venue("Taboo", R.drawable.bk_logo, "2/5"));
        DatabaseMethods.saveDayVenueToDB(tomorrow, new Venue("Listas", R.drawable.bk_logo, "1/5"));
        DatabaseMethods.saveDayVenueToDB(tomorrow, new Venue("DejaVu", R.drawable.bk_logo, "3/5"));
        DatabaseMethods.saveDayVenueToDB(tomorrow, new Venue("Pjazz", R.drawable.bk_logo, "4/5"));
        DatabaseMethods.saveDayVenueToDB(tomorrow, new Venue("B20", R.drawable.bk_logo, "3/5"));
        DatabaseMethods.saveDayVenueToDB(tomorrow, new Venue("Blue", R.drawable.bk_logo, "3/5"));
        DatabaseMethods.saveDayVenueToDB(tomorrow, new Venue("Green", R.drawable.bk_logo, "3/5"));
        DatabaseMethods.saveDayVenueToDB(tomorrow, new Venue("Yellow", R.drawable.bk_logo, "3/5"));
        DatabaseMethods.saveDayVenueToDB(tomorrow, new Venue("Brown", R.drawable.bk_logo, "2/5"));
        DatabaseMethods.saveDayVenueToDB(tomorrow, new Venue("Black", R.drawable.bk_logo, "3/5"));
        DatabaseMethods.saveDayVenueToDB(tomorrow, new Venue("Grey", R.drawable.bk_logo, "3/5"));
        DatabaseMethods.saveDayVenueToDB(tomorrow, new Venue("White", R.drawable.bk_logo, "4/5"));
        DatabaseMethods.saveDayVenueToDB(tomorrow, new Venue("Purple", R.drawable.bk_logo, "3/5"));
        DatabaseMethods.saveDayVenueToDB(tomorrow, new Venue("Red", R.drawable.bk_logo, "1/5"));
        DatabaseMethods.saveDayVenueToDB(tomorrow, new Venue("Magenta", R.drawable.bk_logo, "5/5"));
        DatabaseMethods.saveDayVenueToDB(tomorrow, new Venue("Some Shithole", R.drawable.bk_logo, "5/5"));

        DatabaseMethods.saveDayVenueToDB(theDayAfterTomorrow, new Venue("Dzempub", R.drawable.bk_logo, "3/5"));
        DatabaseMethods.saveDayVenueToDB(theDayAfterTomorrow, new Venue("Taboo", R.drawable.bk_logo, "2/5"));
        DatabaseMethods.saveDayVenueToDB(theDayAfterTomorrow, new Venue("Listas", R.drawable.bk_logo, "1/5"));
        DatabaseMethods.saveDayVenueToDB(theDayAfterTomorrow, new Venue("DejaVu", R.drawable.bk_logo, "3/5"));
        DatabaseMethods.saveDayVenueToDB(theDayAfterTomorrow, new Venue("Pjazz", R.drawable.bk_logo, "4/5"));
        DatabaseMethods.saveDayVenueToDB(theDayAfterTomorrow, new Venue("B20", R.drawable.bk_logo, "3/5"));
        DatabaseMethods.saveDayVenueToDB(theDayAfterTomorrow, new Venue("Blue", R.drawable.bk_logo, "3/5"));
        DatabaseMethods.saveDayVenueToDB(theDayAfterTomorrow, new Venue("Green", R.drawable.bk_logo, "3/5"));
        DatabaseMethods.saveDayVenueToDB(theDayAfterTomorrow, new Venue("Yellow", R.drawable.bk_logo, "3/5"));
        DatabaseMethods.saveDayVenueToDB(theDayAfterTomorrow, new Venue("Brown", R.drawable.bk_logo, "2/5"));
        DatabaseMethods.saveDayVenueToDB(theDayAfterTomorrow, new Venue("Black", R.drawable.bk_logo, "3/5"));
        DatabaseMethods.saveDayVenueToDB(theDayAfterTomorrow, new Venue("Grey", R.drawable.bk_logo, "3/5"));
        DatabaseMethods.saveDayVenueToDB(theDayAfterTomorrow, new Venue("White", R.drawable.bk_logo, "4/5"));
        DatabaseMethods.saveDayVenueToDB(theDayAfterTomorrow, new Venue("Purple", R.drawable.bk_logo, "3/5"));
        DatabaseMethods.saveDayVenueToDB(theDayAfterTomorrow, new Venue("Red", R.drawable.bk_logo, "1/5"));
        DatabaseMethods.saveDayVenueToDB(theDayAfterTomorrow, new Venue("Magenta", R.drawable.bk_logo, "5/5"));
        DatabaseMethods.saveDayVenueToDB(theDayAfterTomorrow, new Venue("Some Shithole", R.drawable.bk_logo, "5/5"));




    }

    public void setUpNewDayInDB(){

        db.collection("dates").document(theDayAfterTomorrow).get().
                addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) newDayExistsInD = true;
            }
        });

        if (!newDayExistsInD) {
            DatabaseMethods.saveDayVenueToDB(theDayAfterTomorrow, new Venue("Dzempub", R.drawable.bk_logo, "3/5"));
            DatabaseMethods.saveDayVenueToDB(theDayAfterTomorrow, new Venue("Taboo", R.drawable.bk_logo, "2/5"));
            DatabaseMethods.saveDayVenueToDB(theDayAfterTomorrow, new Venue("Listas", R.drawable.bk_logo, "1/5"));
            DatabaseMethods.saveDayVenueToDB(theDayAfterTomorrow, new Venue("DejaVu", R.drawable.bk_logo, "3/5"));
            DatabaseMethods.saveDayVenueToDB(theDayAfterTomorrow, new Venue("Pjazz", R.drawable.bk_logo, "4/5"));
            DatabaseMethods.saveDayVenueToDB(theDayAfterTomorrow, new Venue("B20", R.drawable.bk_logo, "3/5"));
            DatabaseMethods.saveDayVenueToDB(theDayAfterTomorrow, new Venue("Blue", R.drawable.bk_logo, "3/5"));
            DatabaseMethods.saveDayVenueToDB(theDayAfterTomorrow, new Venue("Green", R.drawable.bk_logo, "3/5"));
            DatabaseMethods.saveDayVenueToDB(theDayAfterTomorrow, new Venue("Yellow", R.drawable.bk_logo, "3/5"));
            DatabaseMethods.saveDayVenueToDB(theDayAfterTomorrow, new Venue("Brown", R.drawable.bk_logo, "2/5"));
            DatabaseMethods.saveDayVenueToDB(theDayAfterTomorrow, new Venue("Black", R.drawable.bk_logo, "3/5"));
            DatabaseMethods.saveDayVenueToDB(theDayAfterTomorrow, new Venue("Grey", R.drawable.bk_logo, "3/5"));
            DatabaseMethods.saveDayVenueToDB(theDayAfterTomorrow, new Venue("White", R.drawable.bk_logo, "4/5"));
            DatabaseMethods.saveDayVenueToDB(theDayAfterTomorrow, new Venue("Purple", R.drawable.bk_logo, "3/5"));
            DatabaseMethods.saveDayVenueToDB(theDayAfterTomorrow, new Venue("Red", R.drawable.bk_logo, "1/5"));
            DatabaseMethods.saveDayVenueToDB(theDayAfterTomorrow, new Venue("Magenta", R.drawable.bk_logo, "5/5"));
            DatabaseMethods.saveDayVenueToDB(theDayAfterTomorrow, new Venue("Some Shithole", R.drawable.bk_logo, "5/5"));
        }
    }

    public static CollectionReference getDayVenuesRef() {
        return dayVenuesRef;
    }
}
