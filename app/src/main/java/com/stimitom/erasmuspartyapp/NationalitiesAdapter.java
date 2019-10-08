package com.stimitom.erasmuspartyapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class NationalitiesAdapter extends FirestoreRecyclerAdapter<User, NationalitiesAdapter.NationalitiesViewHolder> {
    private static final String TAG = "NationalitiesAdapter";
    public NationalitiesAdapter(FirestoreRecyclerOptions<User> options){
        super(options);
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
        holder.nationality.setText(user.getNationality());

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
