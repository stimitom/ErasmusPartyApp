package com.stimitom.erasmuspartyapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.Toast;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.OnCompleteListener;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;


public class VenuesListActivity extends AppCompatActivity {
    private Context context = this;
    public static Activity downShutter;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static CollectionReference dayVenuesRef;
    private RecyclerView recyclerView;
    private FirebaseUser user;
    private Button popularButton;
    private Button alphabeticButton;
    private Boolean popularSortActive;

    private Button dateButton;
    private String today;
    private String tomorrow;
    private String theDayAfterTomorrow;
    private String city;

    private Boolean todayBool;
    private Boolean tomorrowBool;

    private VenuesAdapter popularAdapter;
    private VenuesAdapter alphabeticAdapter;

    private ShimmerFrameLayout shimmerViewContainer;

    private static final int ERROR_DIALOG_REQUEST = 90001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_venues_list);
        downShutter = this;
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        isLoggedInAndUserIsSet();
        Intent intentComing = getIntent();
        if (intentComing.getBooleanExtra("comesFromLauncher", false))
            LauncherActivity.launcherActivity.finish();
        city = intentComing.getStringExtra("city");

        popularButton = (Button) findViewById(R.id.button_popular);
        alphabeticButton = (Button) findViewById(R.id.button_alphabetic);
        dateButton = (Button) findViewById(R.id.button_date);
        shimmerViewContainer = (ShimmerFrameLayout) findViewById(R.id.shimmer_view_container);
        popularButton.setBackgroundResource(R.drawable.button_venues_list_selected);
        alphabeticButton.setBackgroundResource(R.drawable.button_venues_list_not_selected);
        popularButton.setOnClickListener(popularSortListener);
        alphabeticButton.setOnClickListener(alphabeticSortListener);


        setUpDateButton();
        setDayVenuesRef();
        setUpPopularRecyclerView(true, false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        shimmerViewContainer.startShimmerAnimation();
    }

    @Override
    protected void onStart() {
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
        alphabeticAdapter = new VenuesAdapter(options, shimmerViewContainer);
        popularAdapter.stopListening();
        recyclerView.setAdapter(alphabeticAdapter);
        attachItemClickListenerToAdapter(alphabeticAdapter);
        alphabeticAdapter.startListening();
        popularSortActive = false;
    }

    public void setDayVenuesRef() {
        if (todayBool) {
            dayVenuesRef = db.collection(city + "_dates")
                    .document(today)
                    .collection("day_venues");
        } else if (tomorrowBool) {
            dayVenuesRef = db.collection(city + "_dates")
                    .document(tomorrow)
                    .collection("day_venues");
        } else {
            dayVenuesRef = db.collection(city + "_dates")
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
                intent.putExtra("venueName", clickedVenue.getVenueName());
                String day;
                if (todayBool) {
                    day = today;

                } else if (tomorrowBool) {
                    day = tomorrow;
                } else {
                    day = theDayAfterTomorrow;
                }
                intent.putExtra("dateGiven", day);
                intent.putExtra("dayText", beautifyDateString(day));
                intent.putExtra("city", city);
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
            VenuesAdapter searchAdapter = new VenuesAdapter(options, shimmerViewContainer);
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
                Intent intent = new Intent(this, AddGooglePlaceActivity.class);
                intent.putExtra("city", city);
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
                Intent intent1 = new Intent(this, AboutActivity.class);
                startActivity(intent1);
                return true;
            case R.id.action_maps:
                if (isServicesOk()) {
                    Intent intent2 = new Intent(this, MapsActivity.class);
                    intent2.putExtra("city", city);
                    startActivity(intent2);
                }
                return true;
            case R.id.action_profile:
                Intent intent2 = new Intent(this, ProfileActivity.class);
                startActivity(intent2);
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }


    public boolean isServicesOk() {
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);

        if (available == ConnectionResult.SUCCESS) {
            //Everything okay
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            //An resolveable error occured
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            //Nothing can be done, maps cannot be used
            Toast.makeText(this, "Your phone does not fulfill the requirements for this function.", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    /**** UserInfo ****/

    public void isLoggedInAndUserIsSet() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            FirebaseFirestore.getInstance().collection("users").document(user.getUid())
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.get("nationality") == null) runCitySetupActivity();
                        }
                    });

        } else runLoginActivity();
    }

    public void runLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    public void runCitySetupActivity() {
        Intent intent = new Intent(this, CitySetupActivity.class);
        startActivity(intent);
    }


    /**** Date Button ****/

    public void setUpDateButton() {
        today = DatabaseMethods.getDateToday();
        tomorrow = DatabaseMethods.getDateTomorrow();
        theDayAfterTomorrow = DatabaseMethods.getDateTheDayAfterTomorrow();
        dateButton.setText(beautifyDateString(today));
        todayBool = true;
        tomorrowBool = false;
        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (todayBool) {
                    dateButton.setText(beautifyDateString(tomorrow));
                    todayBool = false;
                    tomorrowBool = true;

                } else if (tomorrowBool) {
                    dateButton.setText(beautifyDateString(theDayAfterTomorrow));
                    dateButton.setCompoundDrawablesRelativeWithIntrinsicBounds(getApplicationContext().getResources().getDrawable(R.drawable.ic_arrow_left_24dp), null, getResources().getDrawable(R.drawable.ic_arrow_right_primary_light_24dp), null);
                    tomorrowBool = false;
                } else {
                    dateButton.setText(beautifyDateString(today));
                    dateButton.setCompoundDrawablesRelativeWithIntrinsicBounds(getApplicationContext().getResources().getDrawable(R.drawable.ic_arrow_left_primary_light_24dp), null, getResources().getDrawable(R.drawable.ic_arrow_right_24dp), null);
                    todayBool = true;
                }
                shimmerViewContainer.setVisibility(View.VISIBLE);
                shimmerViewContainer.startShimmerAnimation();
                if (popularSortActive) setUpPopularRecyclerView(false, true);
                else setUpAlphabeticRecyclerView(true);
            }
        });
    }

    public String beautifyDateString(String date) {
        String day = date.substring(0, 2);
        String month = date.substring(3, 5);
        String year = date.substring(6);
        switch (month) {
            case "01":
                month = "Jan";
                break;
            case "02":
                month = "Feb";
                break;
            case "03":
                month = "Mar";
                break;
            case "04":
                month = "Apr";
                break;
            case "05":
                month = "May";
                break;
            case "06":
                month = "Jun";
                break;
            case "07":
                month = "Jul";
                break;
            case "08":
                month = "Aug";
                break;
            case "09":
                month = "Sep";
                break;
            case "10":
                month = "Oct";
                break;
            case "11":
                month = "Nov";
                break;
            case "12":
                month = "Dec";
                break;
        }
        return month + " " + day + " " + year;
    }

    public static CollectionReference getDayVenuesRef() {
        return dayVenuesRef;
    }
}


