package com.example.propertyproject.chart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.propertyproject.R;
import com.example.propertyproject.models.ContractorModel;
import com.example.propertyproject.models.RentModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

import java.util.ArrayList;
import java.util.Objects;

public class RentChartActivity extends AppCompatActivity {

    private TextView firstTv, secondTv , thirdTv , fourthTv, fifthTv , sixthTv, seventhTv , eighthTv;
    private  ArrayList<RentModel> firstRange,secondRange,thirdRange,fourthRange,fifthRange,sixthRange,seventhRange,
    eighthRange;
    private PieChart pieChart;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rent_chart);

        pieChart = findViewById(R.id.chart);
        firstTv = findViewById(R.id.firstTv);
        secondTv = findViewById(R.id.secondTv);
        thirdTv = findViewById(R.id.thirdTv);
        fourthTv = findViewById(R.id.fourthTv);
        fifthTv = findViewById(R.id.fifthTv);
        sixthTv  = findViewById(R.id.sixthTv);
        seventhTv = findViewById(R.id.seventhTv);
        eighthTv = findViewById(R.id.eighthTv);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        firstRange = new ArrayList<>();
        secondRange = new ArrayList<>();
        thirdRange = new ArrayList<>();
        fourthRange = new ArrayList<>();
        fifthRange = new ArrayList<>();
        sixthRange = new ArrayList<>();
        seventhRange = new ArrayList<>();
        eighthRange = new ArrayList<>();

        getAllProperties();
    }

    private void getAllProperties(){
        firebaseFirestore
                .collection("properties")
                .document(Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid())
                .collection("myProperties")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for(DocumentSnapshot documentSnapshot : task.getResult()){
                                RentModel rentModel = documentSnapshot.toObject(RentModel.class);
                                int  rentRate = Integer.parseInt(rentModel.getRent().trim());
                                if(rentRate >= 1000 && rentRate <= 1250){
                                    firstRange.add(rentModel);
                                    firstTv.setText(String.valueOf(firstRange.size()));
                                }
                                if(rentRate >= 1250 && rentRate <= 1500){
                                    secondRange.add(rentModel);
                                    secondTv.setText(String.valueOf(secondRange.size()));
                                }
                                if(rentRate >= 1500 && rentRate <= 1750){
                                    thirdRange.add(rentModel);
                                    thirdTv.setText(String.valueOf(thirdRange.size()));
                                }
                                if(rentRate >= 1750 && rentRate <= 2000){
                                    fourthRange.add(rentModel);
                                    fourthTv.setText(String.valueOf(fourthRange.size()));
                                }
                                if(rentRate >= 2000 && rentRate <= 2250){
                                    fifthRange.add(rentModel);
                                    fifthTv.setText(String.valueOf(fifthRange.size()));
                                }
                                if(rentRate >= 2250 && rentRate <= 2500){
                                    sixthRange.add(rentModel);
                                    sixthTv.setText(String.valueOf(sixthRange.size()));
                                }
                                if(rentRate >= 2500 && rentRate <= 2750){
                                    seventhRange.add(rentModel);
                                    seventhTv.setText(String.valueOf(seventhRange.size()));
                                }
                                if(rentRate >= 2750 && rentRate <= 3000){
                                    eighthRange.add(rentModel);
                                    eighthTv.setText(String.valueOf(eighthRange.size()));
                                }
                            }

                            // PUSHING DATA INTO CHART
                            if(firstRange.size() > 0){
                                pieChart.addPieSlice(new PieModel("Range 1",firstRange.size(), 0xFF680068));
                            }
                            if(secondRange.size() > 0){
                                pieChart.addPieSlice(new PieModel("Range 2",secondRange.size(),  0xFF3300aa));
                            }
                            if(thirdRange.size() > 0){
                                pieChart.addPieSlice(new PieModel("Range 3",thirdRange.size(), 0xFF66bbff));
                            }
                            if(fourthRange.size() > 0){
                                pieChart.addPieSlice(new PieModel("Range 4",fourthRange.size(), 0xFF33aa33));
                            }

                            //-----------------------------------
                            if(fifthRange.size() > 0){
                                pieChart.addPieSlice(new PieModel("Range 5",fifthRange.size(), 0xFFFFA500));
                            }
                            if(sixthRange.size() > 0){
                                pieChart.addPieSlice(new PieModel("Range 6",sixthRange.size(),  0xFFFF0000));
                            }
                            if(seventhRange.size() > 0){
                                pieChart.addPieSlice(new PieModel("Range 7",seventhRange.size(), 0xFF000000));
                            }
                            if(eighthRange.size() > 0){
                                pieChart.addPieSlice(new PieModel("Range 8",eighthRange.size(), 0xFFFFFF00));
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

    }
}