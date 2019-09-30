package com.stimitom.erasmuspartyapp;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import java.util.ArrayList;
import java.util.List;


public class VenuesRecyclerViewAdapter extends RecyclerView.Adapter<VenuesRecyclerViewAdapter.VenuesViewHolder> implements Filterable {
    private final String TAG = "RVAdapter";
    private List<Venue> venuesList;
    private List<Venue> venuesListFull;

    public VenuesRecyclerViewAdapter(List<Venue> venuesList) {
        this.venuesList = venuesList;
        Log.e(TAG, "" + venuesList.size());
        this.venuesListFull = new ArrayList<Venue>(venuesList);
    }

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
        venuesViewHolder.numberOfAttendees.setText(venuesList.get(position).getNumberOfAttendees() + "\npeople attend tonight");
    }

    @Override
    public int getItemCount() {
        return venuesList.size();
    }


    /*************************/
    /**
     * HANDLES THE FILTERING
     **/
    @Override
    public Filter getFilter() {
        return venueFilter;
    }

    private Filter venueFilter = new Filter() {
        //Is run on separate thread by default
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Venue> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(venuesListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                //iterate through whole list to find items that match constraint

                for (Venue venue : venuesListFull) {
                    if (venue.getVenueName().toLowerCase().startsWith(filterPattern)) {
                        filteredList.add(venue);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        //publishes Result to UI thread
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            venuesList.clear();
            venuesList.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

/*************************/
    /**
     * VIEWHOLDER
     **/
    public static class VenuesViewHolder extends RecyclerView.ViewHolder {

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

