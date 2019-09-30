package com.stimitom.erasmuspartyapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class VenuesActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private static List<Venue> venues;
    private VenuesRecyclerViewAdapter adapter;
    private String TAG = "VenuesActivity";
    public static Activity reloader;
    private Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_venues);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_venues);
        reloader = this;

        /*******************/
        /**Loading a list of venues**/
        /**IF loads Venues from database normally
         * else is executed after adding venue
         */
        Intent intent = getIntent();
        if (intent.getBooleanExtra("added_flag", true)) {
            Log.e(TAG, "on create : IF");
            venues = new ArrayList<Venue>();
            loadVenues();
        } else {
            Log.e(TAG, "onCreate: else");
            String addedVenue = this.getIntent().getStringExtra("venue_name");

            Toast toast = Toast.makeText(getApplicationContext(), addedVenue + " was added to List", Toast.LENGTH_LONG);
            toast.show();
            loadVenues();
        }

        //TODO Ask if user already defined a username in if-condition
        //   if (intent.getBooleanExtra("sign_up_flag",false))
        //Open Dialog for username and nationality input
        // openDialog();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "on Resume is run");
    }

    /*****************************/
    /**
     * Handles the fetching of venues from database
     * and initializes recyclerview and adapter
     */
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference venuesRef = db.collection("venues");

    public void loadVenues() {
        venuesRef.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        venues.clear();
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            Venue venue = documentSnapshot.toObject(Venue.class);
                            venues.add(venue);
                        }
                        // Is called here because fetching of data from DB must be finished
                        buildRecyclerView();
                        Log.e(TAG, "onSuccessLoadVenues: finished adding to venues");
                    }

                });

    }

    public void buildRecyclerView() {
        adapter = new VenuesRecyclerViewAdapter(venues);
        layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        handleItemClicks();
    }

    public void handleItemClicks() {
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(context, recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Venue clickedVenue = venues.get(position);
                Intent intent1 = new Intent(context, AttendPartyActivity.class);
                intent1.putExtra("clickedVenue", clickedVenue);
                startActivity(intent1);
            }

            @Override
            public void onLongClick(View view, int position) {
            }
        }));
    }

    /********************************/
    /*** ACTION BAR METHODS **/

    //Inflates the menu's XML file to the Action Bar
    //and inflates the searchView in the Action bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_venues_activity, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });
        return true;
    }

    //Checks which item in the Action Bar was clicked and performs its action
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_venue:
                // User chose the "add_venue" item, show the app action_add_venue UI
                Intent intent = new Intent(this, AddVenueActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_sort_alphabetically:
                sortVenuesAlphabetically();
                adapter.notifyDataSetChanged();
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    //Sorts venues alphabetically by venue's name
    public void sortVenuesAlphabetically() {
        Collections.sort(venues, new Comparator<Venue>() {
            @Override
            public int compare(Venue lhs, Venue rhs) {
                return lhs.getVenueName().compareTo(rhs.getVenueName());
            }
        });
    }

    /**********************************/
    /**
     * Handles the dialog and databaseUpload of Inputs
     */
    public void openDialog() {
        UsernameNationalityDialog dialog = new UsernameNationalityDialog();
        dialog.show(getSupportFragmentManager(), "UsernameNationalityDialog");
    }


}

/*
//            DatabaseMethods.saveVenueToDatabase(new Venue("Dzempub", R.drawable.bk_logo, "3/5"));
//            DatabaseMethods.saveVenueToDatabase(new Venue("Taboo", R.drawable.bk_logo, "2/5"));
//            DatabaseMethods.saveVenueToDatabase(new Venue("Listas", R.drawable.bk_logo, "1/5"));
//            DatabaseMethods.saveVenueToDatabase(new Venue("DejaVu", R.drawable.bk_logo, "3/5"));
//            DatabaseMethods.saveVenueToDatabase(new Venue("Pjazz", R.drawable.bk_logo, "4/5"));
//            DatabaseMethods.saveVenueToDatabase(new Venue("B20", R.drawable.bk_logo, "3/5"));
//            DatabaseMethods.saveVenueToDatabase(new Venue("Blue", R.drawable.bk_logo, "3/5"));
//            DatabaseMethods.saveVenueToDatabase(new Venue("Green", R.drawable.bk_logo, "3/5"));
//            DatabaseMethods.saveVenueToDatabase(new Venue("Yellow", R.drawable.bk_logo, "3/5"));
//            DatabaseMethods.saveVenueToDatabase(new Venue("Brown", R.drawable.bk_logo, "2/5"));
//            DatabaseMethods.saveVenueToDatabase(new Venue("Black", R.drawable.bk_logo, "3/5"));
//            DatabaseMethods.saveVenueToDatabase(new Venue("Grey", R.drawable.bk_logo, "3/5"));
//            DatabaseMethods.saveVenueToDatabase(new Venue("White", R.drawable.bk_logo, "4/5"));
//            DatabaseMethods.saveVenueToDatabase(new Venue("Purple", R.drawable.bk_logo, "3/5"));
//            DatabaseMethods.saveVenueToDatabase(new Venue("Red", R.drawable.bk_logo, "1/5"));
//            DatabaseMethods.saveVenueToDatabase(new Venue("Magenta", R.drawable.bk_logo, "5/5"));
//            DatabaseMethods.saveVenueToDatabase(new Venue("Some Shithole", R.drawable.bk_logo, "5/5"));
 */