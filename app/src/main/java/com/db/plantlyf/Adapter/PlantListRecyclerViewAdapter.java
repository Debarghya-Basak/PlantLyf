package com.db.plantlyf.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.db.plantlyf.R;

import java.util.ArrayList;

public class PlantListRecyclerViewAdapter extends RecyclerView.Adapter<PlantListRecyclerViewAdapter.ViewHolder> {


    private ArrayList<String> plantsList;
    private Context context;

    public PlantListRecyclerViewAdapter(Context context, ArrayList<String> plantsList) {
        this.context = context;
        this.plantsList = plantsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        View view = LayoutInflater.from(context).inflate(R.layout.scansoil_plant_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.plantNameTV.setText(plantsList.get(position));

    }

    @Override
    public int getItemCount() {
        return plantsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView plantNameTV;

        public ViewHolder(View itemView){
            super(itemView);

            plantNameTV = itemView.findViewById(R.id.plantNameTV);

        }
    }
}
