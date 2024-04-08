package com.db.plantlyf;

import static android.graphics.Shader.TileMode.MIRROR;
import static android.graphics.Shader.TileMode.REPEAT;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Intent;
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
import android.widget.Toast;
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
    private String navigation = "LoginOrRegister";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginOrRegisterBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());

        startBgVideo();
        startBtnListeners();

        //startBgVideoExo();

    }

    private void startBtnListeners() {

        binding.loginOrRegisterLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLoginSet();
            }
        });

        binding.loginOrRegisterRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRegisterSet();
            }
        });

        binding.loginRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {showRegisterSet();}
        });

        binding.submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSetProfilePictureSet();
            }
        });

        binding.registerLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {showLoginSet();}
        });

        binding.registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginOrRegister.this, Dashboard.class);
//                Bundle b = ActivityOptions.makeSceneTransitionAnimation(LoginOrRegister.this).toBundle();
                startActivity(intent);
            }
        });

    }

    private void showSetProfilePictureSet(){

        binding.registerContainerLL.animate().alpha(0).setDuration(500).start();

        navigation = "SetProfilePicture";

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                binding.registerContainerLL.setVisibility(View.GONE);
                binding.setProfilePictureContainerLL.setVisibility(View.VISIBLE);
                binding.setProfilePictureContainerLL.animate().alpha(1).setDuration(500).start();
            }
        },700);

    }

    private void showLoginRegisterSet(){

        if(navigation.equals("Login"))
            binding.loginContainerLL.animate().alpha(0).setDuration(500).start();
        else if(navigation.equals("Register"))
            binding.registerContainerLL.animate().alpha(0).setDuration(500).start();

        navigation = "LoginOrRegister";

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                binding.loginContainerLL.setVisibility(View.GONE);
                binding.registerContainerLL.setVisibility(View.GONE);
                binding.loginOrRegisterBtnLL.setVisibility(View.VISIBLE);
                binding.loginOrRegisterAppNameTV.setVisibility(View.VISIBLE);
                binding.loginOrRegisterAppNameTV.animate().alpha(1).setDuration(500).start();
                binding.loginOrRegisterBtnLL.animate().alpha(1).setDuration(500).start();
            }
        },700);

    }

    private void showLoginSet() {

        if(navigation.equals("LoginOrRegister")) {
            binding.loginOrRegisterAppNameTV.animate().alpha(0).setDuration(500).start();
            binding.loginOrRegisterBtnLL.animate().alpha(0).setDuration(500).start();
        }
        else if(navigation.equals("Register"))
            binding.registerContainerLL.animate().alpha(0).setDuration(500).start();

        navigation = "Login";

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                binding.loginOrRegisterBtnLL.setVisibility(View.GONE);
                binding.loginOrRegisterAppNameTV.setVisibility(View.GONE);
                binding.registerContainerLL.setVisibility(View.GONE);
                binding.loginContainerLL.setVisibility(View.VISIBLE);
                binding.loginContainerLL.animate().alpha(1).setDuration(500).start();
            }
        },700);

    }

    private void showRegisterSet() {

        if(navigation.equals("LoginOrRegister")) {
            binding.loginOrRegisterAppNameTV.animate().alpha(0).setDuration(500).start();
            binding.loginOrRegisterBtnLL.animate().alpha(0).setDuration(500).start();
        }
        else if(navigation.equals("Login"))
            binding.loginContainerLL.animate().alpha(0).setDuration(500).start();
        else if(navigation.equals("SetProfilePicture"))
            binding.setProfilePictureContainerLL.animate().alpha(0).setDuration(500).start();

        navigation = "Register";

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                binding.loginOrRegisterBtnLL.setVisibility(View.GONE);
                binding.loginOrRegisterAppNameTV.setVisibility(View.GONE);
                binding.loginContainerLL.setVisibility(View.GONE);
                binding.registerContainerLL.setVisibility(View.VISIBLE);
                binding.registerContainerLL.animate().alpha(1).setDuration(500).start();
            }
        },700);
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


//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                videoView.setVisibility(View.VISIBLE);
//            }
//        },500);


        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                videoView.start();
            }
        });

    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        if(navigation.equals("LoginOrRegister")) {
            super.onBackPressed();
        }
        else if(navigation.equals("SetProfilePicture")){
            showRegisterSet();
        }
        else {
            showLoginRegisterSet();
        }
        //Toast.makeText(this, "Back and nothing else", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {

        if(onResumeFlag)
            startBgVideo();
        else
            onResumeFlag = true;
        super.onResume();
    }

    //    private void startBgVideoExo() {
//
//        ExoPlayer player = new ExoPlayer.Builder(this).build();
//        //binding.LoginOrRegisterBgPV.setPlayer(player);
//
//        MediaItem mediaItem = MediaItem.fromUri("android.resource://" + getPackageName() + "/" + R.raw.plantlyfbganim);
//        player.setMediaItem(mediaItem);
//        player.prepare();
//        player.play();
//
//    }
}