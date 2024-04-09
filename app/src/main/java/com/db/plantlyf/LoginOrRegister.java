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
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.media3.common.MediaItem;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.exoplayer.SimpleExoPlayer;

import com.db.plantlyf.AppData.Data;
import com.db.plantlyf.databinding.ActivityLoginOrRegisterBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class LoginOrRegister extends AppCompatActivity {

    private ActivityLoginOrRegisterBinding binding;
    private boolean onResumeFlag = false;
    private String navigation = "LoginOrRegister";
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

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

        binding.loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userLoginAuth();
                Toast.makeText(LoginOrRegister.this, "LOGIN", Toast.LENGTH_SHORT).show();
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
                userRegisterAuth();
            }
        });

        binding.registerLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {showLoginSet();}
        });

        binding.registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userRegisterProfilePictureAuth();
                Toast.makeText(LoginOrRegister.this, "REGISTER PP", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(LoginOrRegister.this, Dashboard.class);
//                Bundle b = ActivityOptions.makeSceneTransitionAnimation(LoginOrRegister.this).toBundle();
                startActivity(intent);
            }
        });

    }

    private void initializeFirebaseAuth() {
        firebaseAuth = FirebaseAuth.getInstance();
    }

    private void initializeFirebaseFirestore(){
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    private void userRegisterAuth() {
        initializeFirebaseAuth();

        String userFullName = String.valueOf(binding.registerNameET.getText());
        String userEmail = String.valueOf(binding.registerEmailET.getText());
        String userPassword = String.valueOf(binding.registerPasswordET.getText());
        String userConfirmPassword = String.valueOf(binding.registerConfirmpwET.getText());

        if(invalidUserName(userFullName)){
            Toast.makeText(this, "Please enter full name", Toast.LENGTH_SHORT).show();
        }
        else if(invalidUserEmail(userEmail)){
            Toast.makeText(this, "Please enter correct email", Toast.LENGTH_SHORT).show();
        }
        else if(invalidUserPassword(userPassword)){
            Toast.makeText(this, "Password is invalid", Toast.LENGTH_SHORT).show();
        }
        else if(!userPassword.equals(userConfirmPassword)){
            Toast.makeText(this, "Confirm password does not match", Toast.LENGTH_SHORT).show();
        }
        else{
            firebaseAuth.createUserWithEmailAndPassword(userEmail, userPassword)
                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            Log.d("Firebase Authentication : ", authResult.getUser().getUid());

                            Data.UID = authResult.getUser().getUid();
                            Data.USERFULLNAME = userFullName;
                            Data.USEREMAIL = userEmail;
                            Data.USERPASSWORD = userPassword;

                            Map<String, Object> data = new HashMap<>();
                            data.put("fullName", Data.USERFULLNAME);
                            data.put("email", Data.USEREMAIL);

                            initializeFirebaseFirestore();
                            firebaseFirestore.collection("userData")
                                            .document(Data.UID).set(data)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    Toast.makeText(LoginOrRegister.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                                                    showSetProfilePictureSet();
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(LoginOrRegister.this, "Registration failed", Toast.LENGTH_SHORT).show();
                                                    firebaseAuth.getCurrentUser().delete();
                                                }
                                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(LoginOrRegister.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private boolean invalidUserPassword(String userPassword) {
        return false;
    }

    private boolean invalidUserEmail(String userEmail) {
        return false;
    }

    private boolean invalidUserName(String userFullName) {
        return false;
    }

    private void userRegisterProfilePictureAuth() {
    }

    private void userLoginAuth() {
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
                binding.setProfilePictureContainerLL.setVisibility(View.GONE);
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