package com.stimitom.erasmuspartyapp;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.List;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class VenuesAdapter extends FirestoreRecyclerAdapter<Venue, VenuesAdapter.VenuesViewHolder> {
    private final String TAG = "Adapter";
    private OnItemClickListener listener;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private DocumentReference venueRef;
    private String currentUserId = getUserId();
    public VenuesAdapter(@NonNull FirestoreRecyclerOptions<Venue> options) {
        super(options);
    }

    @Override
    public VenuesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View venue = layoutInflater.inflate(R.layout.venue, parent, false);
        return new VenuesViewHolder(venue);
    }

    @Override
    public void onBindViewHolder(final VenuesViewHolder venuesViewHolder, int position, final Venue venue) {
        venuesViewHolder.goingBanner.setVisibility(View.INVISIBLE);
        venuesViewHolder.venueName.setText(venue.getVenueName());
        venuesViewHolder.venuePicture.setImageResource(venue.getImageId());
        venuesViewHolder.venueRating.setText(venue.getRating());
        venuesViewHolder.numberOfAttendees.setText(""+venue.getNumberOfAttendees());

        //Make going button visible on attended Venues
        if (user != null) {
            venueRef = db.collection("venues").document(venue.getVenueName());
            venueRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.contains("guestList")) {
                        List<String> usersAttending = (List<String>) documentSnapshot.get("guestList");
                        if (usersAttending!= null) {
                            if (usersAttending.contains(currentUserId)) {
                                venuesViewHolder.goingBanner.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e(TAG, "onFailure: retrieveing venue data failed" + e.toString());
                }
            });
        }

    }

    //Returns String of ID if user is logged in
    //null otherwise
    public String getUserId() {
        if (user != null) {
            //User is logged in
            Log.e(TAG, "getUserId: " + user.getUid());
            return user.getUid();
        } else {
            //User not logged in
            return null;
        }
    }


    /*************************/
    /**
     * VIEWHOLDER
     **/

    class VenuesViewHolder extends RecyclerView.ViewHolder {

        public TextView venueName;
        public ImageView venuePicture;
        public TextView venueRating;
        public TextView numberOfAttendees;
        public CardView cardView;
        public ImageView goingBanner;

        public VenuesViewHolder(View itemView) {
            super(itemView);
            this.venueName = (TextView) itemView.findViewById(R.id.venue_name);
            this.venuePicture = (ImageView) itemView.findViewById(R.id.venue_picture);
            this.venueRating = (TextView) itemView.findViewById(R.id.venue_rating);
            this.numberOfAttendees = (TextView) itemView.findViewById(R.id.number_of_attendees);
            this.goingBanner = (ImageView) itemView.findViewById(R.id.going_banner);
            cardView = (CardView) itemView.findViewById(R.id.card_view_venue);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    // if item gets remove but gets clicked during remove animation the following will not be called
                    if (position != RecyclerView.NO_POSITION && listener != null) {
                        listener.onItemClick(getSnapshots().getSnapshot(position), position);
                    }
                }
            });
        }
    }

    //To send itemdata to underlying activity
    public interface OnItemClickListener {
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

}


