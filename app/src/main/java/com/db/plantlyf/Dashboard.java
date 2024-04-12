package com.db.plantlyf;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.VideoView;

import com.db.plantlyf.AppData.Data;
import com.db.plantlyf.Utilities.BitmapStringConverter;
import com.db.plantlyf.Utilities.DarkModeStatus;
import com.db.plantlyf.databinding.ActivityDashboardBinding;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class Dashboard extends AppCompatActivity {

    private ActivityDashboardBinding binding;
    private boolean onResumeFlag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDashboardBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });

        initializePage();

    }

    private void initializePage() {
        initializeContainerBg();
        initializeCardIcons();
        showGreetings();
        startBgVideo();
        showProfilePicture();
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void initializeCardIcons(){

        if(DarkModeStatus.isDarkModeEnabled(this)){
            binding.soilIconIV.setBackground(getDrawable(R.drawable.global_soil_icon_dark));
            binding.plantDiseaseIconIV.setBackground(getDrawable(R.drawable.global_plant_disease_icon_dark));
            binding.plantWaterIconIV.setBackground(getDrawable(R.drawable.global_plant_water_icon_dark));
            binding.plantNutritionIconIV.setBackground(getDrawable(R.drawable.global_plant_nutrition_icon_dark));
        }
        else{
            binding.soilIconIV.setBackground(getDrawable(R.drawable.global_soil_icon_light));
            binding.plantDiseaseIconIV.setBackground(getDrawable(R.drawable.global_plant_disease_icon_light));
            binding.plantWaterIconIV.setBackground(getDrawable(R.drawable.global_plant_water_icon_light));
            binding.plantNutritionIconIV.setBackground(getDrawable(R.drawable.global_plant_nutrition_icon_light));
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void showProfilePicture(){
        if(Data.USER_PROFILE_PICTURE.equals("no_profile_picture"))
            binding.profilePicture.setBackground(getDrawable(R.drawable.default_profile_picture));
        else
            binding.profilePicture.setImageBitmap(BitmapStringConverter.stringToBitmap(Data.USER_PROFILE_PICTURE));
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void initializeContainerBg() {

        if(DarkModeStatus.isDarkModeEnabled(this))
            binding.containerBgV.setBackground(getDrawable(R.drawable.global_container_bg_dark));
        else
            binding.containerBgV.setBackground(getDrawable(R.drawable.global_container_bg_light));

    }

    private void showGreetings() {


        String time = "evening";
        String greetingsText = "Good " + time;
        String userFirstName = Data.USER_FULLNAME.substring(0, Data.USER_FULLNAME.indexOf(' '));

        binding.greetingsTV.setText(greetingsText);
        binding.userFirstNameTV.setText(userFirstName);
        binding.hiUserFirstNameTV.setText("Hi " + userFirstName);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                binding.greetingsTV.animate().alpha(0).setDuration(500).start();
                binding.userFirstNameTV.animate().alpha(0).translationXBy(100).setDuration(500).start();

            }
        },2000);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                binding.greetingsTV.setVisibility(View.GONE);
                binding.appNameTV.setVisibility(View.VISIBLE);
                binding.userFirstNameTV.setVisibility(View.GONE);
                binding.hiUserFirstNameTV.setVisibility(View.VISIBLE);
                binding.hiUserFirstNameTV.animate().alpha(1).translationXBy(100).setDuration(500).start();
                binding.appNameTV.animate().alpha(1).setDuration(500).start();
            }
        },2500);

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