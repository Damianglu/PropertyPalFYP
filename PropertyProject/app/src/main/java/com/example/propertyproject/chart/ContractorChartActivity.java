package com.example.propertyproject.chart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.example.propertyproject.R;
import com.example.propertyproject.models.ContractorModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

import java.util.ArrayList;

public class ContractorChartActivity extends AppCompatActivity {
    private TextView tvPlumber,tvElectrician,tvCarpenter,tvDriver;
    private ArrayList<ContractorModel> plumbersList;
    private ArrayList<ContractorModel> electricianList;
    private ArrayList<ContractorModel> carpenterList;
    private ArrayList<ContractorModel> driverList;
    private PieChart pieChart;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseFirestore firebaseFirestore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contractor_chart);

        pieChart = findViewById(R.id.chart);
        tvPlumber = findViewById(R.id.tvPlumber);
        tvElectrician = findViewById(R.id.tvElectrician);
        tvCarpenter  = findViewById(R.id.tvCarpenter);
        tvDriver = findViewById(R.id.tvDriver);
        pieChart.startAnimation();

        plumbersList = new ArrayList<>();
        electricianList = new ArrayList<>();
        carpenterList = new ArrayList<>();
        driverList = new ArrayList<>();

        //database
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        getAllContractors();
    }

    private void getAllContractors(){
        firebaseFirestore
                .collection("contractors")
                .document(firebaseUser.getUid())
                .collection("myContractors")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for(QueryDocumentSnapshot snapshot : task.getResult()){
                                ContractorModel model = snapshot.toObject(ContractorModel.class);
                                if(model.getContractorfield().equals("Plumber")){
                                    plumbersList.add(model);
                                    tvPlumber.setText(String.valueOf(plumbersList.size()));

                                }
                                if(model.getContractorfield().equals("Electrician")){
                                    electricianList.add(model);
                                    tvElectrician.setText(String.valueOf(electricianList.size()));
                                }
                                if(model.getContractorfield().equals("Carpenter")){
                                    carpenterList.add(model);
                                    tvCarpenter.setText(String.valueOf(carpenterList.size()));
                                }
                                if(model.getContractorfield().equals("Driver")){
                                    driverList.add(model);
                                    tvDriver.setText(String.valueOf(driverList.size()));
                                }


                            }
                            if(plumbersList.size() > 0){
                                pieChart.addPieSlice(new PieModel("Plumber",plumbersList.size(), 0xFF00FF00));
                            }
                            if(electricianList.size() > 0){
                                pieChart.addPieSlice(new PieModel("Electrician",electricianList.size(),  0xFFFF0000));
                            }
                            if(carpenterList.size() > 0){
                                pieChart.addPieSlice(new PieModel("Carpenter",carpenterList.size(), 0xFF0000FF));
                            }
                            if(driverList.size() > 0){
                                pieChart.addPieSlice(new PieModel("Driver",driverList.size(), 0xFFFFFF00));
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
