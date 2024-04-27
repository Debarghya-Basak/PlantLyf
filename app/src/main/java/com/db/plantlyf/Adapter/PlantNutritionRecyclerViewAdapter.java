package com.db.plantlyf.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.db.plantlyf.AppData.Constants;
import com.db.plantlyf.AppData.Data;
import com.db.plantlyf.Model.PlantDataModel;
import com.db.plantlyf.Model.PlantNutritionModel;
import com.db.plantlyf.R;
import com.db.plantlyf.Utilities.DialogBox;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PlantNutritionRecyclerViewAdapter extends RecyclerView.Adapter<PlantNutritionRecyclerViewAdapter.ViewHolder>{

    private ArrayList<PlantNutritionModel> plantsList;
    private TextView noPlantsToDisplayTV;
    private Context context;

    public PlantNutritionRecyclerViewAdapter(Context context, ArrayList<PlantNutritionModel> plantsList, TextView noPlantsToDisplayTV) {
        this.context = context;
        this.plantsList = plantsList;
        this.noPlantsToDisplayTV = noPlantsToDisplayTV;
    }

    @NonNull
    @Override
    public PlantNutritionRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        View view = LayoutInflater.from(context).inflate(R.layout.plant_nutrition_item_card, parent, false);
        return new PlantNutritionRecyclerViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlantNutritionRecyclerViewAdapter.ViewHolder holder, int position) {

        holder.plantNameTV.setText(plantsList.get(position).getPlantName());
        holder.plantNutritionTv.setText(plantsList.get(position).getPlantNutrition());

    }

    @Override
    public int getItemCount() {
        return plantsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView plantNameTV, plantNutritionTv;

        public ViewHolder(View itemView){
            super(itemView);

            plantNameTV = itemView.findViewById(R.id.plantNameTV);
            plantNutritionTv = itemView.findViewById(R.id.plantNutritionTV);

        }
    }

}
