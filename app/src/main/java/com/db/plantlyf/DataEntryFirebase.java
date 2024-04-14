package com.db.plantlyf;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.db.plantlyf.Adapter.DataEntryItemRecyclerViewAdapter;
import com.db.plantlyf.databinding.ActivityDataEntryFirebaseBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

public class DataEntryFirebase extends AppCompatActivity {

    ActivityDataEntryFirebaseBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityDataEntryFirebaseBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

        String[] labels = {"Apple scab", "Apple Black rot", "Cedar apple rust", "Healthy Apple", "Healthy blueberry", "Healthy Cherry", "Cherry Powdery mildew", "Corn(maize) Cercospora Gray spot", "Corn(maize) Common rust", "Healthy Corn(maize)", "Corn(maize) Northern Leaf Blight", "Grape Black rot", "Grape Esca", "Healthy Grape", "Grape Leaf blight", "Orange Haunglongbing", "Peach Bacterial spot", "Healthy Peach", "Pepper bell Bacterial spot", "Healthy Pepper bell", "Potato Early blight", "Healthy Potato", "Potato Late blight", "Healthy Raspberry", "Healthy Soybean", "Squash Powdery mildew", "Healthy Strawberry", "Strawberry Leaf scorch", "Tomato Bacterial spot", "Tomato Early blight", "Healthy Tomato", "Tomato Late blight", "Tomato Leaf Mold", "Tomato Septoria leaf spot", "Tomato Spider mites", "Tomato Target Spot", "Tomato mosaic virus", "Tomato Yellow Leaf Curl Virus"};

        ArrayList<String> diseaseList = new ArrayList<>(Arrays.asList(labels));

        firebaseFirestore.collection("plantDiseaseInfo")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        ArrayList<DocumentSnapshot> documentSnapshot = new ArrayList<DocumentSnapshot>(queryDocumentSnapshots.getDocuments());
                        if(!documentSnapshot.isEmpty()) {
                            for (int i = 0;i<documentSnapshot.size();i++){
                                Log.d("PLANTLYF", "DataEntryFirebase = " + documentSnapshot.get(i).getId());

                                Log.d("PLANTLYF", "DataEntryFirebase = " + diseaseList.contains(documentSnapshot.get(i).getId()));
                                diseaseList.remove(documentSnapshot.get(i).getId());

                            }
                        }

                        DataEntryItemRecyclerViewAdapter adapter = new DataEntryItemRecyclerViewAdapter(DataEntryFirebase.this, diseaseList);
                        binding.dataList.setAdapter(adapter);
                        binding.dataList.setLayoutManager(new LinearLayoutManager(DataEntryFirebase.this));

                        //Log.d("PLANTLYF", "DataEntryFirebase = " + documentSnapshot.get(0));
                    }
                });



    }
}