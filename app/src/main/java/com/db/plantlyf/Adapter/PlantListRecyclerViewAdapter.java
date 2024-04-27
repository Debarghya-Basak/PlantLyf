package com.db.plantlyf.Adapter;

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

public class PlantListRecyclerViewAdapter extends RecyclerView.Adapter<PlantListRecyclerViewAdapter.ViewHolder> {

    private ArrayList<PlantDataModel> plantsList;
    private Context context;

    public PlantListRecyclerViewAdapter(Context context, ArrayList<PlantDataModel> plantsList) {
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

        holder.plantNameTV.setText(plantsList.get(position).getPlantName());
        holder.showPlantCountTv.setText(""+plantsList.get(position).getPlantCount());
        holder.addPlantCountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                plantsList.get(position).setPlantCount(plantsList.get(position).getPlantCount() + 1);
                holder.showPlantCountTv.setText(""+plantsList.get(position).getPlantCount());
            }
        });

        holder.subtractPlantCountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(plantsList.get(position).getPlantCount() - 1 >= 0) {
                    plantsList.get(position).setPlantCount(plantsList.get(position).getPlantCount() - 1);
                    holder.showPlantCountTv.setText("" + plantsList.get(position).getPlantCount());
                }
            }
        });

        holder.savePlantBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DialogBox sendingDataDialogBox = new DialogBox(context, R.layout.global_loading_dialog_box,false);
                sendingDataDialogBox.showDialog();
                FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

                Map<String,Integer> data = new HashMap<>();
                data.put(Constants.DB_PLANTCOUNT, plantsList.get(position).getPlantCount());
                firebaseFirestore.collection(Constants.DB_USERDATA)
                        .document(Data.UID)
                        .collection(Constants.DB_PLANTDATA)
                        .document(plantsList.get(position).getPlantName())
                        .set(data)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                sendingDataDialogBox.dismissDialog();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                sendingDataDialogBox.dismissDialog();
                            }
                        });
            }
        });

    }

    @Override
    public int getItemCount() {
        return plantsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView plantNameTV, showPlantCountTv;
        MaterialCardView addPlantCountBtn, subtractPlantCountBtn;
        MaterialButton savePlantBtn;

        public ViewHolder(View itemView){
            super(itemView);

            plantNameTV = itemView.findViewById(R.id.plantNameTV);
            showPlantCountTv = itemView.findViewById(R.id.showPlantCountTV);
            addPlantCountBtn = itemView.findViewById(R.id.addPlantCountBtn);
            subtractPlantCountBtn = itemView.findViewById(R.id.subtractPlantCountBtn);
            savePlantBtn = itemView.findViewById(R.id.savePlantBtn);

        }
    }
}
