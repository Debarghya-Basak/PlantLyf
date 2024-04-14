package com.db.plantlyf.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.db.plantlyf.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DataEntryItemRecyclerViewAdapter extends RecyclerView.Adapter<DataEntryItemRecyclerViewAdapter.ViewHolder> {


    private ArrayList<String> diseaseList;
    private Context context;
    private FirebaseFirestore firebaseFirestore;

    public DataEntryItemRecyclerViewAdapter(Context context, ArrayList<String> diseaseList) {
        this.context = context;
        this.diseaseList = diseaseList;
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        View view = LayoutInflater.from(context).inflate(R.layout.dataentry_item_cards, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        holder.diseaseNameTV.setText(diseaseList.get(position));
        holder.submitSolutionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(holder.solutionET.getText())){

                    String solution = holder.solutionET.getText().toString();

                    Map<String, String> data = new HashMap<>();
                    data.put("solution", solution);

                    firebaseFirestore.collection("plantDiseaseInfo")
                            .document(diseaseList.get(position))
                            .set(data)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @SuppressLint("NotifyDataSetChanged")
                                @Override
                                public void onSuccess(Void unused) {
                                    diseaseList.remove(position);
                                    notifyDataSetChanged();
                                }
                            });

                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return diseaseList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView diseaseNameTV;
        TextInputEditText solutionET;
        MaterialButton submitSolutionBtn;

        public ViewHolder(View itemView){
            super(itemView);

            diseaseNameTV = itemView.findViewById(R.id.diseaseNameTv);
            solutionET = itemView.findViewById(R.id.solutionET);
            submitSolutionBtn = itemView.findViewById(R.id.submitSolutionBtn);

        }
    }
}
