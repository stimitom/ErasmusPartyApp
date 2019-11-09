package com.stimitom.erasmuspartyapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class CountriesAdapter extends RecyclerView.Adapter<CountriesAdapter.CountriesViewHolder> {
    private ArrayList<String> nationalities;
    public CountriesAdapter(ArrayList<String> nationalities){
        this.nationalities = nationalities;
    }

    @NonNull
    @Override
    public CountriesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View nationality = layoutInflater.inflate(R.layout.nationality, parent, false);
        return new CountriesViewHolder(nationality);
    }

    @Override
    public void onBindViewHolder(@NonNull CountriesViewHolder holder, int position) {
        String country = nationalities.get(position);
        holder.nationalityTextView.setText(country);
    }

    @Override
    public int getItemCount() {
        return nationalities.size();
    }

    public static class CountriesViewHolder extends RecyclerView.ViewHolder{
        private TextView nationalityTextView;
        private CardView cardView;


        public CountriesViewHolder(@NonNull View itemView) {
            super(itemView);
            this.nationalityTextView = (TextView) itemView.findViewById(R.id.text_view_nationality);
            this.cardView = (CardView) itemView.findViewById(R.id.card_view_nationality);
        }
    }

}

