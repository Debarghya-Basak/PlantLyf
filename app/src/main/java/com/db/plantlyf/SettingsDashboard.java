package com.db.plantlyf;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.db.plantlyf.AppData.Constants;
import com.db.plantlyf.AppData.Data;
import com.db.plantlyf.Utilities.BitmapStringConverter;
import com.db.plantlyf.Utilities.DialogBox;
import com.db.plantlyf.databinding.ActivitySettingsDashboardBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SettingsDashboard extends AppCompatActivity {

    private ActivitySettingsDashboardBinding binding;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private final int PICK_IMAGE_REQUEST = 1;
    private final int PERMISSION_READ_EXTERNAL_STORAGE = 100;
    private final int PERMISSION_READ_MEDIA_IMAGES = 101;
    private DialogBox loadingDialogBox;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsDashboardBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());

        initializePage();

    }

    private void initializePage() {

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        loadingDialogBox = new DialogBox(this, R.layout.global_loading_dialog_box, false);

        binding.showEmailTV.setText(Data.USER_EMAIL);
        binding.fullNameET.setText(Data.USER_FULLNAME);
        if(!Data.USER_PROFILE_PICTURE.equals(Constants.NO_PROFILE_PICTURE))
            binding.userProfilePictureCIV.setImageBitmap(BitmapStringConverter.stringToBitmap(Data.USER_PROFILE_PICTURE));


        initializeBtns();
    }

    private void initializeBtns() {

        binding.logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });

        binding.removeProfilePictureMC.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onClick(View v) {
                Map<String, Object> data = new HashMap<>();
                data.put("full_name", Data.USER_FULLNAME);
                data.put("email", Data.USER_EMAIL);
                data.put("profile_picture", Constants.NO_PROFILE_PICTURE);

                loadingDialogBox.showDialog();
                firebaseFirestore.collection(Constants.DB_USERDATA)
                        .document(Data.UID)
                        .set(data)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Data.USER_PROFILE_PICTURE = Constants.NO_PROFILE_PICTURE;

                                SharedPreferences sharedPreferences = getSharedPreferences(Constants.PLANTLYFSHAREDPREFERENCE, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();

                                editor.putString(Constants.USER_PROFILE_PICTURE, Data.USER_PROFILE_PICTURE);

                                editor.apply();

                                Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.default_profile_picture);
                                binding.userProfilePictureCIV.setImageBitmap(icon);

                                Toast.makeText(SettingsDashboard.this, "Updated Successfully", Toast.LENGTH_SHORT).show();
                                loadingDialogBox.dismissDialog();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(SettingsDashboard.this, "Failed to update", Toast.LENGTH_SHORT).show();
                                loadingDialogBox.dismissDialog();

                            }
                        });

            }
        });

        binding.editProfilePictureMC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setProfilePicture();
            }
        });

        binding.saveFullNameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!TextUtils.isEmpty(binding.fullNameET.getText())){
                    if(!invalidFullName()){
                        Map<String, Object> data = new HashMap<>();
                        data.put("full_name", binding.fullNameET.getText().toString());
                        data.put("email", Data.USER_EMAIL);
                        data.put("profile_picture", Data.USER_PROFILE_PICTURE);

                        loadingDialogBox.showDialog();
                        firebaseFirestore.collection(Constants.DB_USERDATA)
                                .document(Data.UID)
                                .set(data)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Data.USER_FULLNAME = binding.fullNameET.getText().toString();

                                        SharedPreferences sharedPreferences = getSharedPreferences(Constants.PLANTLYFSHAREDPREFERENCE, Context.MODE_PRIVATE);
                                        SharedPreferences.Editor editor = sharedPreferences.edit();

                                        editor.putString(Constants.USER_FULLNAME, Data.USER_FULLNAME);

                                        editor.apply();

                                        Toast.makeText(SettingsDashboard.this, "Updated Successfully", Toast.LENGTH_SHORT).show();
                                        loadingDialogBox.dismissDialog();

                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(SettingsDashboard.this, "Failed to update", Toast.LENGTH_SHORT).show();
                                        loadingDialogBox.dismissDialog();

                                    }
                                });
                    }
                }
                else{
                    Toast.makeText(SettingsDashboard.this, "Please enter a valid full name", Toast.LENGTH_SHORT).show();
                }

            }
        });

        binding.sendPWResetLinkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(binding.currentPasswordET.getText())){

                    if(binding.currentPasswordET.getText().toString().equals(Data.USER_PASSWORD)){

                        loadingDialogBox.showDialog();
                        firebaseAuth.sendPasswordResetEmail(Data.USER_EMAIL)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        logout();
                                        Toast.makeText(SettingsDashboard.this, "Password reset link sent", Toast.LENGTH_LONG).show();

                                        loadingDialogBox.dismissDialog();

                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(SettingsDashboard.this, "Failed to send Password reset link", Toast.LENGTH_LONG).show();

                                        loadingDialogBox.dismissDialog();

                                    }
                                });
                    }
                    else{
                        Toast.makeText(SettingsDashboard.this, "Not correct password", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });

    }

    private void setProfilePicture() {
        checkPerm();
    }

    private void openImagePicker(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
//        getImage.launch(intent);
    }

//    private void launchImageCropper(Uri uri) {
//
//        Intent intent = new Intent(LoginOrRegister.this, CropImage.class);
//        intent.putExtra("ImageUri", uri.toString());
//        startActivity(intent);
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            Uri selectedImageUri = data.getData();

            try {

                Bitmap photo = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                String temp = BitmapStringConverter.bitmapToString(photo);

                Map<String, Object> editData = new HashMap<>();
                editData.put("full_name", Data.USER_FULLNAME);
                editData.put("email", Data.USER_EMAIL);
                editData.put("profile_picture", temp);

                loadingDialogBox.showDialog();

                firebaseFirestore.collection(Constants.DB_USERDATA)
                        .document(Data.UID)
                        .set(editData)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                binding.userProfilePictureCIV.setImageBitmap(photo);
                                Data.USER_PROFILE_PICTURE = BitmapStringConverter.bitmapToString(photo);

                                SharedPreferences sharedPreferences = getSharedPreferences(Constants.PLANTLYFSHAREDPREFERENCE, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();

                                editor.putString(Constants.USER_PROFILE_PICTURE, Data.USER_PROFILE_PICTURE);

                                editor.apply();

                                Toast.makeText(SettingsDashboard.this, "Updated Successfully", Toast.LENGTH_SHORT).show();
                                loadingDialogBox.dismissDialog();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(SettingsDashboard.this, "Failed to update", Toast.LENGTH_SHORT).show();
                                loadingDialogBox.dismissDialog();
                            }
                        });

            } catch (IOException e) {
                Log.d("PLANTLYF : ", "Image picker : " + e.getLocalizedMessage());
            }

        }
    }

    private void checkPerm(){
        // Check for permission
        if(Data.ANDROIDSDKVERSION <= 32) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_READ_EXTERNAL_STORAGE);
            else
                openImagePicker();
        }
        else{
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED)
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_MEDIA_IMAGES}, PERMISSION_READ_MEDIA_IMAGES);
            else
                openImagePicker();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_READ_EXTERNAL_STORAGE
                || requestCode == PERMISSION_READ_MEDIA_IMAGES) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                openImagePicker();
            else
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();

        }
    }

    private boolean invalidFullName() {
        return false;
    }

    private void logout() {

        SharedPreferences sharedPreferences = getSharedPreferences(Constants.PLANTLYFSHAREDPREFERENCE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.clear();

        editor.apply();

        Data.UID = "";
        Data.USER_EMAIL = "";
        Data.USER_PASSWORD = "";
        Data.USER_FULLNAME = "";
        Data.USER_PROFILE_PICTURE = "no_profile_picture";

        Toast.makeText(SettingsDashboard.this, "User Logged Out", Toast.LENGTH_SHORT).show();

        startActivity(new Intent(SettingsDashboard.this, LoginOrRegister.class));

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 3000);

    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 500);
        startActivity(new Intent(SettingsDashboard.this, Dashboard.class));
    }
}