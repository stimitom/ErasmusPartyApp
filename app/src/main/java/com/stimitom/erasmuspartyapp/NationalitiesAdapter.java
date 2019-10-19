package com.stimitom.erasmuspartyapp;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class NationalitiesAdapter extends FirestoreRecyclerAdapter<User, NationalitiesAdapter.NationalitiesViewHolder> {
    private static final String TAG = "NationalitiesAdapter";
    private ArrayList<String> alreadyMentioned;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference dayVenueRef;

    public NationalitiesAdapter(FirestoreRecyclerOptions<User> options, DocumentReference dayVenueRef){
        super(options);
        this.alreadyMentioned = new ArrayList<String>();
        this.dayVenueRef = dayVenueRef;
//        dayVenueRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//            @Override
//            public void onSuccess(DocumentSnapshot documentSnapshot) {
//                Venue venue = documentSnapshot.toObject(Venue.class);
//                if (venue.getNationalitiesList().size() != 0) {
//                    alreadyMentioned.addAll(venue.getNationalitiesList());
//                }
//            }
//        });

    }

    @NonNull
    @Override
    public NationalitiesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View nationality = layoutInflater.inflate(R.layout.nationality, parent, false);
        return new NationalitiesViewHolder(nationality);
    }

    @Override
    protected void onBindViewHolder(@NonNull NationalitiesViewHolder holder, int position, @NonNull User user) {
        if (!alreadyMentioned.contains(user.getNationality())) {
            holder.nationality.setText(user.getNationality());
            alreadyMentioned.add(user.getNationality());
            Log.e(TAG, "onBindViewHolder: " + user.getNationality() +   " added to already mentioned list" );
        }
    }

    class NationalitiesViewHolder extends RecyclerView.ViewHolder{

        private TextView nationality;
        private CardView cardView;


        public NationalitiesViewHolder(View itemView){
            super(itemView);
            this.nationality = (TextView) itemView.findViewById(R.id.text_view_nationality);
            cardView = (CardView) itemView.findViewById(R.id.card_view_nationality);
        }
    }

}
