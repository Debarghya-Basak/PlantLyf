package com.db.plantlyf.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.db.plantlyf.Model.PlantNutritionWaterModel;
import com.db.plantlyf.R;

import java.util.ArrayList;

public class PlantNutritionWaterRecyclerViewAdapter extends RecyclerView.Adapter<PlantNutritionWaterRecyclerViewAdapter.ViewHolder>{

    private ArrayList<PlantNutritionWaterModel> plantsList;
    private Context context;

    public PlantNutritionWaterRecyclerViewAdapter(Context context, ArrayList<PlantNutritionWaterModel> plantsList) {
        this.context = context;
        this.plantsList = plantsList;
    }

    @NonNull
    @Override
    public PlantNutritionWaterRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        View view = LayoutInflater.from(context).inflate(R.layout.plant_nutrition_water_item_card, parent, false);
        return new PlantNutritionWaterRecyclerViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlantNutritionWaterRecyclerViewAdapter.ViewHolder holder, int position) {

        holder.plantNameTV.setText(plantsList.get(position).getPlantName());
        holder.plantNutritionWaterTv.setText(plantsList.get(position).getPlantNutritionWater());

    }

    @Override
    public int getItemCount() {
        return plantsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView plantNameTV, plantNutritionWaterTv;

        public ViewHolder(View itemView){
            super(itemView);

            plantNameTV = itemView.findViewById(R.id.plantNameTV);
            plantNutritionWaterTv = itemView.findViewById(R.id.plantNutritionWaterTV);

        }
    }

}
