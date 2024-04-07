package com.db.plantlyf;

import android.app.ActivityOptions;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.ViewGroup;
import android.widget.VideoView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.db.plantlyf.databinding.ActivitySplashScreenBinding;

public class SplashScreen extends AppCompatActivity {

    ActivitySplashScreenBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashScreenBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());
        //        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
        //            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
        //            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
        //            return insets;
        //        });

        startSplashScreen();
        startLoginOrRegisterActivity();

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

        if(isDarkModeEnabled())
            videoView.setVideoPath("android.resource://" + getPackageName() + "/" + R.raw.plantlyfsplashscreendarkmode);
        else
            videoView.setVideoPath("android.resource://" + getPackageName() + "/" + R.raw.plantlyfsplashscreendarkmode);

        videoView.start();

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
        }, 3000);
    }

    private boolean isDarkModeEnabled() {

        int currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;

        switch (currentNightMode) {
            case Configuration.UI_MODE_NIGHT_YES:
                return true;
            case Configuration.UI_MODE_NIGHT_NO:
            case Configuration.UI_MODE_NIGHT_UNDEFINED:
                return false;
        }

        return false;
    }
}