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
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.db.plantlyf.AiModelHandler.PlantDiseaseClassifier;
import com.db.plantlyf.AiModelHandler.SoilTypeClassifier;
import com.db.plantlyf.AppData.Constants;
import com.db.plantlyf.Utilities.DarkModeStatus;
import com.db.plantlyf.Utilities.DialogBox;
import com.db.plantlyf.databinding.ActivityScanPlantDiseaseBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.Objects;

public class ScanPlantDisease extends AppCompatActivity {

    private ActivityScanPlantDiseaseBinding binding;
    private boolean onResumeFlag = false;
    private final int REQUEST_IMAGE_CAPTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityScanPlantDiseaseBinding.inflate(getLayoutInflater());
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

                binding.textView1.setVisibility(View.VISIBLE);
                binding.recommendedSolutionContainerLL.setVisibility(View.INVISIBLE);
                binding.solutionTV.setText("");
                binding.predictedPlantDiseaseTV.setAlpha(0);
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);

            }
        });
    }

    private void predictPlantDisease(Intent data) throws IOException {

        Bundle extras = data.getExtras();
        Bitmap soilImage = (Bitmap) extras.get("data");
        Bitmap resizedSoilImage = resizeBitmap(soilImage, 200, 200);

        binding.capturePlantDiseasePreviewIV.setImageBitmap(resizedSoilImage);

        PlantDiseaseClassifier plantDiseaseClassifier;

        int inputImageWidth = 256;
        int inputImageHeight = 256;
        String[] labels = {"Apple scab", "Apple Black rot", "Cedar apple rust", "Healthy Apple", "Healthy blueberry", "Healthy Cherry", "Cherry Powdery mildew", "Corn(maize) Cercospora Gray spot", "Corn(maize) Common rust", "Healthy Corn(maize)", "Corn(maize) Northern Leaf Blight", "Grape Black rot", "Grape Esca", "Healthy Grape", "Grape Leaf blight", "Orange Haunglongbing", "Peach Bacterial spot", "Healthy Peach", "Pepper bell Bacterial spot", "Healthy Pepper bell", "Potato Early blight", "Healthy Potato", "Potato Late blight", "Healthy Raspberry", "Healthy Soybean", "Squash Powdery mildew", "Healthy Strawberry", "Strawberry Leaf scorch", "Tomato Bacterial spot", "Tomato Early blight", "Healthy Tomato", "Tomato Late blight", "Tomato Leaf Mold", "Tomato Septoria leaf spot", "Tomato Spider mites", "Tomato Target Spot", "Tomato mosaic virus", "Tomato Yellow Leaf Curl Virus"};
        //String[] labels = {"Apple___Apple_scab", "Apple___Black_rot", "Apple___Cedar_apple_rust", "Apple___healthy", "Blueberry___healthy", "Cherry_(including_sour)___healthy", "Cherry_(including_sour)___Powdery_mildew", "Corn_(maize)___Cercospora_leaf_spot Gray_leaf_spot", "Corn_(maize)___Common_rust_", "Corn_(maize)___healthy", "Corn_(maize)___Northern_Leaf_Blight", "Grape___Black_rot", "Grape___Esca_(Black_Measles)", "Grape___healthy", "Grape___Leaf_blight_(Isariopsis_Leaf_Spot)", "Orange___Haunglongbing_(Citrus_greening)", "Peach___Bacterial_spot", "Peach___healthy", "Pepper,_bell___Bacterial_spot", "Pepper,_bell___healthy", "Potato___Early_blight", "Potato___healthy", "Potato___Late_blight", "Raspberry___healthy", "Soybean___healthy", "Squash___Powdery_mildew", "Strawberry___healthy", "Strawberry___Leaf_scorch", "Tomato___Bacterial_spot", "Tomato___Early_blight", "Tomato___healthy", "Tomato___Late_blight", "Tomato___Leaf_Mold", "Tomato___Septoria_leaf_spot", "Tomato___Spider_mites Two-spotted_spider_mite", "Tomato___Target_Spot", "Tomato___Tomato_mosaic_virus", "Tomato___Tomato_Yellow_Leaf_Curl_Virus"};

        plantDiseaseClassifier = new PlantDiseaseClassifier(this, inputImageWidth, inputImageHeight, labels);

        String predictedLabel = plantDiseaseClassifier.classifyImage(resizedSoilImage);

        DialogBox predictionDialogBox = new DialogBox(this, R.layout.global_prediction_dialog_box);
        predictionDialogBox.showDialog();

        DialogBox downloadingDialogBox = new DialogBox(this, R.layout.global_loading_dialog_box);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                predictionDialogBox.dismissDialog();

                if(predictedLabel.contains("Healthy"))
                    binding.predictedPlantDiseaseTV.setText(predictedLabel);
                else
                    binding.predictedPlantDiseaseTV.setText(predictedLabel + " disease");
                binding.predictedPlantDiseaseTV.animate().alpha(1).start();
            }
        }, 2500);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                downloadingDialogBox.showDialog();
                fetchPlantSolutionDataFromDatabase(predictedLabel, downloadingDialogBox);
            }
        },3000);

    }

    private void fetchPlantSolutionDataFromDatabase(String predictedLabel, DialogBox downloadingDialogBox) {

        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

        firebaseFirestore.collection(Constants.DB_PLANTDISEASEINFO)
                .document(predictedLabel)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String solutionFromDatabase = Objects.requireNonNull(documentSnapshot.get(Constants.DB_SOLUTION)).toString();

                        downloadingDialogBox.dismissDialog();
                        binding.textView1.setVisibility(View.GONE);
                        binding.recommendedSolutionContainerLL.setVisibility(View.VISIBLE);
                        binding.solutionTV.setText(solutionFromDatabase);
                        Log.d("-PLANTLYF-", "ScanPlantDisease: solutionFromDatabase = " + solutionFromDatabase);
                    }
                });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK){

            if(data != null) {
                try {
                    predictPlantDisease(data);
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

        ViewGroup.LayoutParams layoutParams = binding.recommendedSolutionContainerLL.getLayoutParams();
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