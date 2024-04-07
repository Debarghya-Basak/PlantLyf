package com.db.plantlyf;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.VideoView;
import com.db.plantlyf.databinding.ActivityDashboardBinding;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

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

        showGreetings();
        startBgVideo();
    }

    private void showGreetings() {


        String time = "evening";
        String greetingsText = "Good " + time;
        String userFirstName = "Debarghya";

        binding.greetingsTV.setText(greetingsText);
        binding.userFirstNameTV.setText(userFirstName);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                binding.greetingsTV.animate().alpha(0).setDuration(500).start();
                binding.userFirstNameTV.animate().alpha(0).translationXBy(100).setDuration(300).start();

            }
        },2000);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                binding.greetingsTV.setVisibility(View.GONE);
                binding.appNameTV.setVisibility(View.VISIBLE);
                binding.userFirstNameTV.setVisibility(View.GONE);
                binding.hiUserFirstNameTV.setVisibility(View.VISIBLE);
                binding.hiUserFirstNameTV.animate().alpha(1).translationXBy(100).setDuration(300).start();
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