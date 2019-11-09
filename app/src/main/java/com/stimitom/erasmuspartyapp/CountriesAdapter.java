package com.stimitom.erasmuspartyapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CountriesAdapter extends RecyclerView.Adapter<CountriesAdapter.CountriesViewHolder> {

    private ArrayList<String> nationalities;
    public CountriesAdapter(ArrayList<String> nationalities){
        this.nationalities = nationalities;
    }

    @NonNull
    @Override
    public CountriesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.nationality,parent,false);
        CountriesViewHolder viewHolder = new CountriesViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CountriesViewHolder holder, int position) {
        String country = nationalities.get(position);
        holder.nationalityTextView.setText(country);
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    class CountriesViewHolder extends RecyclerView.ViewHolder{
        private TextView nationalityTextView;

        public CountriesViewHolder(@NonNull View itemView) {
            super(itemView);
            nationalityTextView = (TextView) itemView.findViewById(R.id.text_view_nationality);
        }
    }

}

