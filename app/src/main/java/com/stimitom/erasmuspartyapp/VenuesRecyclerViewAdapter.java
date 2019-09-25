package com.stimitom.erasmuspartyapp;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;


public class VenuesRecyclerViewAdapter extends RecyclerView.Adapter<VenuesRecyclerViewAdapter.VenuesViewHolder> {

    private List<Venue> venuesList;

    public VenuesRecyclerViewAdapter(List<Venue> venuesList) { this.venuesList = venuesList; }

    @Override
    public VenuesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View venue = layoutInflater.inflate(R.layout.venue, parent, false);
        VenuesViewHolder venuesViewHolder = new VenuesViewHolder(venue);
        return venuesViewHolder;
    }

    @Override
    public void onBindViewHolder(VenuesViewHolder venuesViewHolder, int position) {
        venuesViewHolder.venueName.setText(venuesList.get(position).getVenueName());
        venuesViewHolder.venuePicture.setImageResource(venuesList.get(position).getImageId());
        venuesViewHolder.venueRating.setText(venuesList.get(position).getRating());
    }

    @Override
    public int getItemCount() {
        return venuesList.size();
    }

    public static class VenuesViewHolder extends RecyclerView.ViewHolder {

        public TextView venueName;
        public ImageView venuePicture;
        public TextView venueRating;
        public ConstraintLayout constraintLayout;

        public VenuesViewHolder(View itemView) {
            super(itemView);
            this.venueName = (TextView) itemView.findViewById(R.id.venue_name);
            this.venuePicture = (ImageView) itemView.findViewById(R.id.venue_picture);
            this.venueRating = (TextView) itemView.findViewById(R.id.venue_rating);
            constraintLayout = (ConstraintLayout) itemView.findViewById(R.id.constraint_layout_venue);
        }
    }

}
