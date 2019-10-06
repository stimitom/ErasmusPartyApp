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
import android.widget.SearchView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
    private CollectionReference venuesRef = db.collection("venues");
    private RecyclerView recyclerView;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    DocumentReference userRef;

    private VenuesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_venues_list);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        reloader = this;

        checkIfDialogNeeded();



            DatabaseMethods.saveVenueToDatabase(new Venue("Dzempub", R.drawable.bk_logo, "3/5"));
            DatabaseMethods.saveVenueToDatabase(new Venue("Taboo", R.drawable.bk_logo, "2/5"));
            DatabaseMethods.saveVenueToDatabase(new Venue("Listas", R.drawable.bk_logo, "1/5"));
            DatabaseMethods.saveVenueToDatabase(new Venue("DejaVu", R.drawable.bk_logo, "3/5"));
            DatabaseMethods.saveVenueToDatabase(new Venue("Pjazz", R.drawable.bk_logo, "4/5"));
            DatabaseMethods.saveVenueToDatabase(new Venue("B20", R.drawable.bk_logo, "3/5"));
            DatabaseMethods.saveVenueToDatabase(new Venue("Blue", R.drawable.bk_logo, "3/5"));
            DatabaseMethods.saveVenueToDatabase(new Venue("Green", R.drawable.bk_logo, "3/5"));
            DatabaseMethods.saveVenueToDatabase(new Venue("Yellow", R.drawable.bk_logo, "3/5"));
            DatabaseMethods.saveVenueToDatabase(new Venue("Brown", R.drawable.bk_logo, "2/5"));
            DatabaseMethods.saveVenueToDatabase(new Venue("Black", R.drawable.bk_logo, "3/5"));
            DatabaseMethods.saveVenueToDatabase(new Venue("Grey", R.drawable.bk_logo, "3/5"));
            DatabaseMethods.saveVenueToDatabase(new Venue("White", R.drawable.bk_logo, "4/5"));
            DatabaseMethods.saveVenueToDatabase(new Venue("Purple", R.drawable.bk_logo, "3/5"));
            DatabaseMethods.saveVenueToDatabase(new Venue("Red", R.drawable.bk_logo, "1/5"));
            DatabaseMethods.saveVenueToDatabase(new Venue("Magenta", R.drawable.bk_logo, "5/5"));
            DatabaseMethods.saveVenueToDatabase(new Venue("Some Shithole", R.drawable.bk_logo, "5/5"));

        setUpRecyclerView();
    }

    private void setUpRecyclerView() {
        Query query = venuesRef.orderBy("numberOfAttendees", Query.Direction.DESCENDING)
                .orderBy("venueName", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<Venue> options = new FirestoreRecyclerOptions.Builder<Venue>()
                .setQuery(query, Venue.class)
                .build();
        adapter = new VenuesAdapter(options);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_venues_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        attachItemClickListenerToAdapter(adapter);

    }

    public void attachItemClickListenerToAdapter(VenuesAdapter adapter) {
        /**Handles the Clicks**/
        adapter.setOnItemClickListener(new VenuesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                Venue clickedVenue = documentSnapshot.toObject(Venue.class);
                Intent intent = new Intent(context, AttendPartyActivity.class);
                intent.putExtra("clickedVenue", clickedVenue);
                startActivity(intent);
            }
        });
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

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Query query = venuesRef.orderBy("venueName", Query.Direction.ASCENDING).startAt(newText.toUpperCase());

                FirestoreRecyclerOptions<Venue> options = new FirestoreRecyclerOptions.Builder<Venue>()
                        .setQuery(query, Venue.class)
                        .build();
                VenuesAdapter searchAdapter = new VenuesAdapter(options);
                if (newText.trim().isEmpty()) {
                    searchAdapter.stopListening();
                    recyclerView.setAdapter(adapter);
                    attachItemClickListenerToAdapter(adapter);
                    adapter.startListening();
                    return false;
                } else {
                    adapter.stopListening();
                    recyclerView.setAdapter(searchAdapter);
                    attachItemClickListenerToAdapter(searchAdapter);
                    searchAdapter.startListening();
                    return false;
                }

            }
        });
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "onResume: isCalled");
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
                        User user = documentSnapshot.toObject(User.class);
                        if (user.getUsername() == null){
                            openDialog();
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


}
