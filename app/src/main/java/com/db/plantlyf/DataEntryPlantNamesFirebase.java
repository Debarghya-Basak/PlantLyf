package com.db.plantlyf;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.db.plantlyf.AppData.Constants;
import com.db.plantlyf.Utilities.DialogBox;
import com.db.plantlyf.databinding.ActivityDataEntryPlantNamesFirebaseBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DataEntryPlantNamesFirebase extends AppCompatActivity {

    private ActivityDataEntryPlantNamesFirebaseBinding binding;
    private ArrayList<String> plantNames;
    private DialogBox loadingDialog;
    private FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDataEntryPlantNamesFirebaseBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initializePage();
        fetchDataFromDataBase();
        initializeBtn();
    }

    private void initializePage() {
        loadingDialog = new DialogBox(this, R.layout.global_loading_dialog_box, false);
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    private void initializeBtn() {

        binding.dataEntryPlantNameSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(binding.dataEntryPlantNameET.getText())
                    && !TextUtils.isEmpty(binding.dataEntryPlantNutritionET.getText())
                        && !TextUtils.isEmpty(binding.dataEntryPlantWaterTimingET.getText())){

                    if(!containsPlant(binding.dataEntryPlantNameET.getText().toString())) {
                        loadingDialog.showDialog();

                        Map<String, String> data = new HashMap<>();
                        data.put(Constants.DB_PLANTNUTRITION, binding.dataEntryPlantNutritionET.getText().toString());
                        data.put(Constants.DB_PLANTWATERTIMING, binding.dataEntryPlantWaterTimingET.getText().toString());

                        firebaseFirestore.collection(Constants.DB_PLANTINFO)
                                .document(binding.dataEntryPlantNameET.getText().toString())
                                .set(data)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(DataEntryPlantNamesFirebase.this, "Submitted", Toast.LENGTH_SHORT).show();
                                        plantNames.add(binding.dataEntryPlantNameET.getText().toString());
                                        updateList();
                                        binding.dataEntryPlantWaterTimingET.setText("");
                                        binding.dataEntryPlantNameET.setText("");
                                        binding.dataEntryPlantNutritionET.setText("");
                                        loadingDialog.dismissDialog();


                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        loadingDialog.dismissDialog();
                                    }
                                });
                    }
                    else {
                        Toast.makeText(DataEntryPlantNamesFirebase.this, "Already present", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });



    }

    private void updateList() {
        ArrayAdapter<String> adapter= new ArrayAdapter<String>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, plantNames);
        binding.plantNamesLV.setAdapter(adapter);
    }

    private boolean containsPlant(String plantName) {

        for(String s : plantNames){
            if(s.equalsIgnoreCase(plantName))
                return true;
        }

        return false;

    }

    private void fetchDataFromDataBase() {

        loadingDialog.showDialog();
        plantNames = new ArrayList<>();

        firebaseFirestore.collection(Constants.DB_PLANTINFO)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for(DocumentSnapshot data : queryDocumentSnapshots.getDocuments()) {

                            Log.d("-PLANTLYF-", data.getId());
                            plantNames.add(data.getId());
                        }

                        updateList();
                        loadingDialog.dismissDialog();
                    }
                });


    }
}