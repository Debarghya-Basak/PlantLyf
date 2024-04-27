package com.db.plantlyf;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.db.plantlyf.Adapter.ManagePlantListRecyclerViewAdapter;
import com.db.plantlyf.AppData.Constants;
import com.db.plantlyf.AppData.Data;
import com.db.plantlyf.Model.PlantDataModel;
import com.db.plantlyf.Utilities.DarkModeStatus;
import com.db.plantlyf.Utilities.DialogBox;
import com.db.plantlyf.databinding.ActivityManagePlantBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.search.SearchBar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ManagePlant extends AppCompatActivity {

    private ActivityManagePlantBinding binding;
    private boolean onResumeFlag = false;
    private ArrayList<PlantDataModel> plantData;
    private ArrayList<String> plantList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityManagePlantBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());

        initializePage();
    }

    private void initializePage() {

        initializeContainerBg();
        startBgVideo();
        fetchDataFromDatabase();
        initializeBtns();

    }

    private void initializeBtns() {


        binding.addPlantManuallyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DialogBox addPlantDialogBox;
                if(DarkModeStatus.isDarkModeEnabled(ManagePlant.this))
                    addPlantDialogBox = new DialogBox(ManagePlant.this, R.layout.manageplant_add_plant_dialogbox, R.drawable.global_functional_dialogbox_bg_dark, true);
                else
                    addPlantDialogBox = new DialogBox(ManagePlant.this, R.layout.manageplant_add_plant_dialogbox, R.drawable.global_functional_dialogbox_bg_light, true);

                Dialog dialog = addPlantDialogBox.getDialog();
                updateAdapter(addPlantDialogBox, dialog, plantList);

                SearchView searchPlantNamesSV = dialog.findViewById(R.id.searchPlantNamesSV);
                searchPlantNamesSV.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        ArrayList<String> updatedPlantList = new ArrayList<>();
                        for(String s : plantList){
                            if(s.toLowerCase().contains(newText.toLowerCase()))
                                updatedPlantList.add(s);
                        }

                        if(newText.isEmpty())
                            updateAdapter(addPlantDialogBox, dialog, plantList);
                        else
                            updateAdapter(addPlantDialogBox, dialog, updatedPlantList);
                        return false;
                    }
                });

                addPlantDialogBox.showDialog();

//                dialog.findViewById(R.id.finalAddPlantManuallyBtn).setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Toast.makeText(ManagePlant.this, "Clicked", Toast.LENGTH_SHORT).show();
//                    }
//                });
            }
        });

    }

    private void updateAdapter(DialogBox addPlantDialogBox, Dialog dialog, ArrayList<String> plantList) {

        ListView plantListView = dialog.findViewById(R.id.plantListDialogLV);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), com.google.android.material.R.layout.support_simple_spinner_dropdown_item, plantList);
        plantListView.setAdapter(adapter);

        plantListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                DialogBox sendingDataDialogBox = new DialogBox(ManagePlant.this, R.layout.global_loading_dialog_box, false);
                FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

                Map<String,Integer> data = new HashMap<>();
                data.put(Constants.DB_PLANTCOUNT, 0);
                firebaseFirestore.collection(Constants.DB_USERDATA)
                        .document(Data.UID)
                        .collection(Constants.DB_PLANTDATA)
                        .document(plantList.get(position))
                        .set(data)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                fetchDataFromDatabase();
                                addPlantDialogBox.dismissDialog();
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

    private void fetchDataFromDatabase() {

        DialogBox dialogBox = new DialogBox(this, R.layout.global_loading_dialog_box,false);
        dialogBox.showDialog();

        plantData = new ArrayList<>();
        plantList = new ArrayList<>();

        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

        firebaseFirestore.collection(Constants.DB_PLANTINFO)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for(DocumentSnapshot data : queryDocumentSnapshots.getDocuments()){
                            Log.d("-PLANTLYF-" , "ManagePlant : Plant Info = " + data.getId());
                            plantList.add(data.getId());
                        }

                        firebaseFirestore.collection(Constants.DB_USERDATA)
                                .document(Data.UID)
                                .collection(Constants.DB_PLANTDATA)
                                .get()
                                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

//                        Log.d("-PLANTLYF-", "ManagePlant = " + queryDocumentSnapshots.getDocuments().get(0).getId());

                                        for(DocumentSnapshot data : queryDocumentSnapshots.getDocuments()){
                                            PlantDataModel dataModel = new PlantDataModel(data.getId(), Integer.parseInt(Objects.requireNonNull(data.get(Constants.DB_PLANTCOUNT)).toString()));
                                            Log.d("-PLANTLYF-", "ManagePlant = Plant Name = " + dataModel.getPlantName());
                                            Log.d("-PLANTLYF-", "ManagePlant = Plant Count = " + dataModel.getPlantCount());

                                            plantData.add(dataModel);
                                        }

                                        if(!plantData.isEmpty())
                                            binding.noPlantsToDisplayTV.setVisibility(View.GONE);
                                        setRecyclerViewAdapter();
                                        dialogBox.dismissDialog();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        dialogBox.dismissDialog();
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });


    }

    private void setRecyclerViewAdapter() {

        ManagePlantListRecyclerViewAdapter adapter = new ManagePlantListRecyclerViewAdapter(this, plantData, binding.noPlantsToDisplayTV);
        binding.plantListRV.setAdapter(adapter);
        binding.plantListRV.setLayoutManager(new LinearLayoutManager(this));

    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void initializeContainerBg() {

        if(DarkModeStatus.isDarkModeEnabled(this))
            binding.containerBgV.setBackground(getDrawable(R.drawable.global_container_bg_dark));
        else
            binding.containerBgV.setBackground(getDrawable(R.drawable.global_container_bg_light));

    }

    private void startBgVideo() {

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        float height = displayMetrics.heightPixels;
        float width = displayMetrics.widthPixels;
        //Toast.makeText(this, dpHeight + "," + dpWidth, Toast.LENGTH_SHORT).show();

        VideoView videoView = binding.dashboardBgVV;
        ViewGroup.LayoutParams layoutParams = videoView.getLayoutParams();
        layoutParams.width = (int)width + ((int)((width * 37.0) / 100.0));
        videoView.setLayoutParams(layoutParams);
        videoView.setVideoPath("android.resource://" + getPackageName() + "/" + R.raw.plantlyfbganim);
        videoView.start();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                videoView.setVisibility(View.VISIBLE);
            }
        },500);


        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                videoView.start();
            }
        });
    }

    @Override
    protected void onResume() {
        if(onResumeFlag)
            startBgVideo();
        else
            onResumeFlag = true;
        super.onResume();
    }
}