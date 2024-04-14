package com.db.plantlyf;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.ViewGroup;
import android.widget.VideoView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.db.plantlyf.AppData.Constants;
import com.db.plantlyf.AppData.Data;
import com.db.plantlyf.Utilities.DarkModeStatus;
import com.db.plantlyf.databinding.ActivitySplashScreenBinding;

@SuppressLint("CustomSplashScreen")
public class SplashScreen extends AppCompatActivity {

    ActivitySplashScreenBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashScreenBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());

        startSplashScreen();
        loadData();

    }

    private void startSplashScreen(){

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        float height = displayMetrics.heightPixels;
        float width = displayMetrics.widthPixels;
        float dpHeight = height / displayMetrics.density;
        float dpWidth = width / displayMetrics.density;
        //Toast.makeText(this, dpHeight + "," + dpWidth, Toast.LENGTH_SHORT).show();

        VideoView videoView = binding.SplashScreenAnimationVV;
        ViewGroup.LayoutParams layoutParams = videoView.getLayoutParams();
        layoutParams.width = (int)width + ((int)((width * 37.0) / 100.0));
        videoView.setLayoutParams(layoutParams);

        if(DarkModeStatus.isDarkModeEnabled(this))
            videoView.setVideoPath("android.resource://" + getPackageName() + "/" + R.raw.plantlyfsplashscreendarkmode);
        else
            videoView.setVideoPath("android.resource://" + getPackageName() + "/" + R.raw.plantlyfsplashscreenlightmode);

        videoView.start();

    }

    private void loadData() {

        Data.ANDROIDSDKVERSION = Build.VERSION.SDK_INT;

        SharedPreferences sharedPreferences = getSharedPreferences(Constants.PLANTLYFSHAREDPREFERENCE, Context.MODE_PRIVATE);

        if(!sharedPreferences.getBoolean(Constants.IS_LOGGED_IN, false))
            startLoginOrRegisterActivity();
        else{
            Data.UID = sharedPreferences.getString(Constants.UID, "");
            Data.USER_FULLNAME = sharedPreferences.getString(Constants.USER_FULLNAME, "");
            Data.USER_EMAIL = sharedPreferences.getString(Constants.USER_EMAIL, "");
            Data.USER_PASSWORD = sharedPreferences.getString(Constants.USER_PASSWORD, "");
            Data.USER_PROFILE_PICTURE = sharedPreferences.getString(Constants.USER_PROFILE_PICTURE, "");

            startDashboardActivity();
        }

    }

    private void startDashboardActivity() {
        Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashScreen.this, Dashboard.class);
                Bundle b = ActivityOptions.makeSceneTransitionAnimation(SplashScreen.this).toBundle();
                startActivity(intent, b);
            }
        }, 2500);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 5000);
    }

    private void startLoginOrRegisterActivity(){
        Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashScreen.this, LoginOrRegister.class);
                Bundle b = ActivityOptions.makeSceneTransitionAnimation(SplashScreen.this).toBundle();
                startActivity(intent, b);
            }
        }, 2500);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 5000);
    }

}