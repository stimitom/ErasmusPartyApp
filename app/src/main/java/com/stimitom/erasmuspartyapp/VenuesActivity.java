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
import android.widget.Toast;

import java.util.ArrayList;
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


        //******************//
        //Loading a list of venues//

        Intent intent = getIntent();
        //If activity is started normally adds List of Venus
        //If activity is started from AddVenueActivity and a correct Venue was added will create updated list
        if (intent.getBooleanExtra("added_flag", true)) {
            Log.e(TAG, "onCreate: IF");
            venues = new ArrayList<>();

            venues.add(new Venue("Dzempub", R.drawable.bk_logo, "3/5"));
            venues.add(new Venue("Taboo", R.drawable.bk_logo, "2/5"));
            venues.add(new Venue("Listas", R.drawable.bk_logo, "1/5"));
            venues.add(new Venue("DejaVu", R.drawable.bk_logo, "3/5"));
            venues.add(new Venue("Pjazz", R.drawable.bk_logo, "4/5"));
            venues.add(new Venue("B20", R.drawable.bk_logo, "3/5"));
            venues.add(new Venue("Blue", R.drawable.bk_logo, "3/5"));
            venues.add(new Venue("Green", R.drawable.bk_logo, "3/5"));
            venues.add(new Venue("Yellow", R.drawable.bk_logo, "3/5"));
            venues.add(new Venue("Brown", R.drawable.bk_logo, "2/5"));
            venues.add(new Venue("Black", R.drawable.bk_logo, "3/5"));
            venues.add(new Venue("Grey", R.drawable.bk_logo, "3/5"));
            venues.add(new Venue("White", R.drawable.bk_logo, "4/5"));
            venues.add(new Venue("Purple", R.drawable.bk_logo, "3/5"));
            venues.add(new Venue("Red", R.drawable.bk_logo, "1/5"));
            venues.add(new Venue("Magenta", R.drawable.bk_logo, "5/5"));
            venues.add(new Venue("Some Shithole", R.drawable.bk_logo, "5/5"));


        } else {
            Log.e(TAG, "onCreate: else");
            List<Venue> updatedList = this.getIntent().getParcelableArrayListExtra("updated_venues");
            String addedVenue= this.getIntent().getStringExtra("venue_name");

            Toast toast = Toast.makeText(getApplicationContext(),addedVenue + " was added to List",Toast.LENGTH_LONG);
            toast.show();

            venues = new ArrayList<>();
            venues.addAll(updatedList);
        }
        // to improve performance
        //recyclerView.setHasFixedSize(true);

        adapter = new VenuesRecyclerViewAdapter(venues);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        //Handling clicks on Venues
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(context, recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Venue clickedVenue=  venues.get(position);
                Intent intent1 = new Intent(context, AttendPartyActivity.class);
                intent1.putExtra("clickedVenue", clickedVenue);
                startActivity(intent1);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
    }

    public static List<Venue> getVenues() {
        return venues;
    }


    //ACTION BAR METHODS

    //Inflates the menu's XML file to the Action Bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_venues_activity, menu);
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

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

}