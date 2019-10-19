package com.stimitom.erasmuspartyapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class NationsAdapter extends RecyclerView.Adapter<NationsAdapter.NationsViewHolder> {

    private ArrayList<String> nationalities;
    public NationsAdapter(ArrayList<String> nationalities){
        this.nationalities = nationalities;
    }

    @NonNull
    @Override
    public NationsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.nationality,parent,false);
        NationsViewHolder viewHolder = new NationsViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull NationsViewHolder holder, int position) {
        String nationality = nationalities.get(position);
        holder.nationalityTextView.setText(nationality);
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    class NationsViewHolder extends RecyclerView.ViewHolder{
        private TextView nationalityTextView;
        private CardView cardView;

        public NationsViewHolder(@NonNull View itemView) {
            super(itemView);
            nationalityTextView = (TextView) itemView.findViewById(R.id.text_view_nationality);
            cardView = (CardView) itemView.findViewById(R.id.card_view_nationality);
        }
    }

}

