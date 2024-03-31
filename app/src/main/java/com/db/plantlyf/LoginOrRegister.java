package com.db.plantlyf;

import static android.graphics.Shader.TileMode.MIRROR;
import static android.graphics.Shader.TileMode.REPEAT;

import android.graphics.RenderEffect;
import android.graphics.Shader;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.VideoView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.media3.common.MediaItem;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.exoplayer.SimpleExoPlayer;

import com.db.plantlyf.databinding.ActivityLoginOrRegisterBinding;

public class LoginOrRegister extends AppCompatActivity {

    ActivityLoginOrRegisterBinding binding;
    private boolean onResumeFlag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginOrRegisterBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());

        startBgVideo();
        blurBg();

        //startBgVideoExo();

    }

    private void startBgVideoExo() {

        ExoPlayer player = new ExoPlayer.Builder(this).build();
        //binding.LoginOrRegisterBgPV.setPlayer(player);

        MediaItem mediaItem = MediaItem.fromUri("android.resource://" + getPackageName() + "/" + R.raw.plantlyfbganim);
        player.setMediaItem(mediaItem);
        player.prepare();
        player.play();

    }
    private void blurBg(){




    }


    private void startBgVideo(){

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        float height = displayMetrics.heightPixels;
        float width = displayMetrics.widthPixels;
        //Toast.makeText(this, dpHeight + "," + dpWidth, Toast.LENGTH_SHORT).show();

        VideoView videoView = binding.LoginOrRegisterBgVV;
        ImageView imageView = binding.TempBgImageIV;
        ViewGroup.LayoutParams layoutParams = videoView.getLayoutParams();
        ViewGroup.LayoutParams layoutParams1 = imageView.getLayoutParams();
        layoutParams.width = (int)width + ((int)((width * 37.0) / 100.0));
        layoutParams1.width = (int)width + ((int)((width * 37.0) / 100.0));
        videoView.setLayoutParams(layoutParams);
        imageView.setLayoutParams(layoutParams1);
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