package com.stimitom.erasmuspartyapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class VenuesAdapter extends FirestoreRecyclerAdapter<Venue, VenuesAdapter.VenuesViewHolder> {
    private final String TAG = "Adapter";

    public VenuesAdapter(@NonNull FirestoreRecyclerOptions<Venue> options){
        super(options);
    }

    @Override
    public VenuesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View venue = layoutInflater.inflate(R.layout.venue, parent, false);
        return new VenuesViewHolder(venue);
    }

    @Override
    public void onBindViewHolder(VenuesViewHolder venuesViewHolder, int position, @NonNull Venue venue) {
        venuesViewHolder.venueName.setText(venue.getVenueName());
        venuesViewHolder.venuePicture.setImageResource(venue.getImageId());
        venuesViewHolder.venueRating.setText(venue.getRating());
        venuesViewHolder.numberOfAttendees.setText(String.valueOf(venue.getNumberOfAttendees()) +"/n"+ R.string.attendtonight);
    }
    /*************************/
    /**VIEWHOLDER**/

    class VenuesViewHolder extends RecyclerView.ViewHolder {

        public TextView venueName;
        public ImageView venuePicture;
        public TextView venueRating;
        public TextView numberOfAttendees;
        public CardView cardView;

        public VenuesViewHolder(View itemView) {
            super(itemView);
            this.venueName = (TextView) itemView.findViewById(R.id.venue_name);
            this.venuePicture = (ImageView) itemView.findViewById(R.id.venue_picture);
            this.venueRating = (TextView) itemView.findViewById(R.id.venue_rating);
            this.numberOfAttendees = (TextView) itemView.findViewById(R.id.number_of_attendees);
            cardView = (CardView) itemView.findViewById(R.id.card_view_venue);
        }
    }

}


