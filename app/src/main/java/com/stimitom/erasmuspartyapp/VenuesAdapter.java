package com.stimitom.erasmuspartyapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class VenuesAdapter extends FirestoreRecyclerAdapter<Venue, VenuesAdapter.VenuesViewHolder> {
    private OnItemClickListener listener;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private CollectionReference dayVenueRef;
    private String currentUserId = getUserId();
    private ShimmerFrameLayout shimmerFrameLayout;
    private  Context context;

    public VenuesAdapter(@NonNull FirestoreRecyclerOptions<Venue> options, ShimmerFrameLayout shimmerContainer , Context context) {
        super(options);
        this.shimmerFrameLayout = shimmerContainer;
        this.context = context;
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


        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),venue.getImageId());
        Bitmap circularBitmap = ImageConverter.getRoundedCornerBitmap(bitmap, 100);

        venuesViewHolder.venuePicture.setImageBitmap(circularBitmap);


        venuesViewHolder.venueRating.setRating(Float.parseFloat(venue.getRating().replace(",", ".") + "f"));
        venuesViewHolder.numberOfAttendees.setText(""+venue.getNumberOfAttendees());

        //Make going button visible on attended Venues
        if (user != null) {
            dayVenueRef = VenuesListActivity.getDayVenuesRef();
            dayVenueRef.document(venue.getVenueName())
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.contains("guestList")) {
                                List<String> usersAttending = (List<String>) documentSnapshot.get("guestList");
                                if (usersAttending != null) {
                                    if (usersAttending.contains(currentUserId)) {
                                        venuesViewHolder.goingBanner.setVisibility(View.VISIBLE);
                                    }
                                }
                            }
                        }
                    });
        }

    }

    public String getUserId() {
        if (user != null) return user.getUid();
        else return null;
    }

    @Override
    public void onDataChanged() {
        if (shimmerFrameLayout != null) {
            shimmerFrameLayout.stopShimmerAnimation();
            shimmerFrameLayout.setVisibility(View.GONE);
            }
    }


    /*************************/
    /**
     * VIEWHOLDER
     **/

    class VenuesViewHolder extends RecyclerView.ViewHolder {

        private TextView venueName;
        private ImageView venuePicture;
        private RatingBar venueRating;
        private TextView numberOfAttendees;
        private ImageView goingBanner;

        private VenuesViewHolder(View itemView) {
            super(itemView);
            this.venueName = (TextView) itemView.findViewById(R.id.venue_name);
            this.venuePicture = (ImageView) itemView.findViewById(R.id.venue_picture_round);
            this.venueRating = (RatingBar) itemView.findViewById(R.id.venue_rating_bar);
            this.numberOfAttendees = (TextView) itemView.findViewById(R.id.number_of_attendees);
            this.goingBanner = (ImageView) itemView.findViewById(R.id.going_banner);

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


