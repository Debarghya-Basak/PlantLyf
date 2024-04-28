package com.db.plantlyf;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.db.plantlyf.Adapter.PlantListRecyclerViewAdapter;
import com.db.plantlyf.AiModelHandler.SoilTypeClassifier;
import com.db.plantlyf.AppData.Constants;
import com.db.plantlyf.AppData.Data;
import com.db.plantlyf.Model.PlantDataModel;
import com.db.plantlyf.Utilities.DarkModeStatus;
import com.db.plantlyf.Utilities.DialogBox;
import com.db.plantlyf.databinding.ActivityScanSoilBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.units.qual.A;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ScanSoil extends AppCompatActivity {

    private ActivityScanSoilBinding binding;
    private boolean onResumeFlag = false;
    private final int REQUEST_IMAGE_CAPTURE = 1;
    private ArrayList<PlantDataModel> plantsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityScanSoilBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());

        initializePage();
    }

    private void initializePage() {
        initializeButton();
        initializeContainerBg();
        initializeRecommendedPlantListLL();
        startBgVideo();
    }

    private void initializeButton(){
        binding.captureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                plantsList = new ArrayList<>();
                PlantListRecyclerViewAdapter adapter = new PlantListRecyclerViewAdapter(ScanSoil.this, plantsList);
                binding.recommendedPlantListContainerLL.setAdapter(adapter);
                binding.recommendedPlantListContainerLL.setLayoutManager(new LinearLayoutManager(ScanSoil.this));
                binding.noPlantsToDisplayTV.setVisibility(View.VISIBLE);

                binding.predictedSoilTypeTV.setAlpha(0);
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);

            }
        });
    }

    private void predictSoilType(Intent data) throws IOException {

        Bundle extras = data.getExtras();
        Bitmap soilImage = (Bitmap) extras.get("data");
        Bitmap resizedSoilImage = resizeBitmap(soilImage, 200, 200);

        binding.captureSoilPreviewIV.setImageBitmap(resizedSoilImage);

        SoilTypeClassifier soilTypeClassifier;

        int inputImageWidth = 256;
        int inputImageHeight = 256;
        String[] labels = {"Alluvial", "Black", "Clay", "Red"};

        soilTypeClassifier = new SoilTypeClassifier(this, inputImageWidth, inputImageHeight, labels);

        String predictedLabel = soilTypeClassifier.classifyImage(resizedSoilImage);

        DialogBox predictionDialogBox = new DialogBox(this, R.layout.global_prediction_dialog_box,false);
        predictionDialogBox.showDialog();

        DialogBox downloadingDialogBox = new DialogBox(this, R.layout.global_loading_dialog_box,false);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                predictionDialogBox.dismissDialog();
                binding.predictedSoilTypeTV.setText("It is " + predictedLabel + " soil");
                binding.predictedSoilTypeTV.animate().alpha(1).start();

            }
        }, 2500);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                downloadingDialogBox.showDialog();
                fetchPlantNamesFromDatabase(predictedLabel, downloadingDialogBox);
            }
        }, 3000);



    }

    private void fetchPlantNamesFromDatabase(String predictedLabel, DialogBox downloadingDialogBox) {

        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
//        ArrayList<PlantDataModel> plantData = new ArrayList<>();
        Map<String, Integer> plantDataMap = new HashMap<>();

        firebaseFirestore.collection(Constants.DB_SOILINFO)
                .document(predictedLabel.toLowerCase())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

//                        downloadingDialogBox.dismissDialog();
                        Log.d("PLANTLYF", "ScanSoil : Plant list Array(" +predictedLabel+ ") = " + documentSnapshot.get(Constants.DB_PLANTS));

                        firebaseFirestore.collection(Constants.DB_USERDATA)
                                .document(Data.UID)
                                .collection(Constants.DB_PLANTDATA)
                                .get()
                                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                                        for(DocumentSnapshot data : queryDocumentSnapshots.getDocuments()){
                                            PlantDataModel dataModel = new PlantDataModel(data.getId(), Integer.parseInt(Objects.requireNonNull(data.get(Constants.DB_PLANTCOUNT)).toString()));
                                            Log.d("-PLANTLYF-", "ScanSoil = Plant Name = " + dataModel.getPlantName());
                                            Log.d("-PLANTLYF-", "ScanSoil = Plant Count = " + dataModel.getPlantCount());

                                            plantDataMap.put(dataModel.getPlantName(), Integer.valueOf(dataModel.getPlantCount()+""));
                                        }

                                        setPlantListAdapter(plantDataMap, Objects.requireNonNull(documentSnapshot.get(Constants.DB_PLANTS)).toString());
                                        downloadingDialogBox.dismissDialog();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        downloadingDialogBox.dismissDialog();
                                    }
                                });

                    }
                });



    }

    private void setPlantListAdapter(Map<String, Integer> plantDataMap, String plants) {

        plantsList = convertToArrayList(plants);

        for(PlantDataModel dataModel : plantsList)
            if(plantDataMap.containsKey(dataModel.getPlantName()))
                dataModel.setPlantCount(plantDataMap.get(dataModel.getPlantName()).intValue());


        PlantListRecyclerViewAdapter adapter = new PlantListRecyclerViewAdapter(this, plantsList);
        binding.recommendedPlantListContainerLL.setAdapter(adapter);
        binding.recommendedPlantListContainerLL.setLayoutManager(new LinearLayoutManager(this));

        binding.noPlantsToDisplayTV.setVisibility(View.GONE);

    }

    private ArrayList<PlantDataModel> convertToArrayList(String plants) {

        String newStr = plants.replace("[", "").replace("]","");
        newStr += ",";

        int index = 0;
        ArrayList<PlantDataModel> plantsList = new ArrayList<>();
        while(newStr.contains(",")){

            String plantName = "";
            if(index > 0)
                plantName = newStr.substring(1, newStr.indexOf(','));
            else if(index == 0)
                plantName = newStr.substring(0, newStr.indexOf(','));


            plantsList.add(new PlantDataModel(plantName, 0));
            index++;

            newStr = newStr.substring(newStr.indexOf(','));
            if(newStr.length() > 1)
                newStr = newStr.substring(1);
            else
                break;
        }

        return plantsList;

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK){

            if(data != null) {
                try {
                    predictSoilType(data);
                } catch (IOException e) {}
            }
            else
                Toast.makeText(this, "No Image Captured", Toast.LENGTH_SHORT).show();

        }
    }

    private Bitmap resizeBitmap(Bitmap soilImage, int width, int height) {
        return Bitmap.createScaledBitmap(soilImage, convertDpToPixels(width), convertDpToPixels(height), false);
    }

    private int convertDpToPixels(int dp) {
        return (int)(dp * Resources.getSystem().getDisplayMetrics().density);

    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void initializeRecommendedPlantListLL() {

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        float height = displayMetrics.heightPixels;
        float width = displayMetrics.widthPixels;

        ViewGroup.LayoutParams layoutParams = binding.recommendedPlantListContainerLL.getLayoutParams();
        layoutParams.height = (int)((height * 35f)/100f);

        if(DarkModeStatus.isDarkModeEnabled(this))
            binding.captureImageIV.setBackground(getDrawable(R.drawable.global_capture_icon_dark));
        else
            binding.captureImageIV.setBackground(getDrawable(R.drawable.global_capture_icon_light));
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