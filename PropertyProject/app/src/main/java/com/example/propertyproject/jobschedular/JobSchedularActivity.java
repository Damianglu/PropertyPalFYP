package com.example.propertyproject.jobschedular;

import androidx.activity.ComponentActivity;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.propertyproject.PropertyActivity;
import com.example.propertyproject.R;
import com.example.propertyproject.firebasemodel;
import com.example.propertyproject.models.ContractorModel;
import com.example.propertyproject.models.JobModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.function.Consumer;

public class JobSchedularActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    private ArrayList<JobModel> jobModelArrayList;
    private ProgressDialog progressDialog;
    private String selectedContractor = "";
    private String selectedAddress = "";
    private Spinner addressSpinner,contractorSpinner;
    private EditText dateEdit,descriptionEdit;
    private Button scheduleJob;
    private ArrayList<String> contractorsList;
    private ArrayList<String> addressList;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_schedular);

        addressSpinner = findViewById(R.id.addressSpinner);
        contractorSpinner = findViewById(R.id.contactorSpinner);
        dateEdit = findViewById(R.id.dateEdit);
        descriptionEdit = findViewById(R.id.descriptionEdit);
        scheduleJob = findViewById(R.id.scheduleJob);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        contractorsList = new ArrayList<>();
        addressList = new ArrayList<>();
        jobModelArrayList = new ArrayList<>();


        getContractors();
        getProperties();
        getAppointments();

        contractorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedContractor = contractorsList.get(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        addressSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedAddress = addressList.get(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        dateEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSelectedDate();
            }
        });
        scheduleJob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String date = dateEdit.getText().toString();
                String description = descriptionEdit.getText().toString();

                if(TextUtils.isEmpty(date)){
                    Toast.makeText(JobSchedularActivity.this,"Please select a date",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(description)){
                    Toast.makeText(JobSchedularActivity.this,"Please add description",Toast.LENGTH_SHORT).show();
                    return;
                }

                if(selectedAddress.equals("")){
                    Toast.makeText(JobSchedularActivity.this,"Please select address",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(selectedContractor.equals("")){
                    Toast.makeText(JobSchedularActivity.this,"Please select contractor",Toast.LENGTH_SHORT).show();
                    return;
                }

                for (int i = 0; i < jobModelArrayList.size(); i++) {
                    if(jobModelArrayList.get(i).getContractor().trim().equals(selectedContractor.trim())
                            &&  jobModelArrayList.get(i).getDate().trim().equals(date.trim())){
                        Toast.makeText(JobSchedularActivity.this,jobModelArrayList.get(i).getContractor()
                                + " is unavailable for this date",Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                progressDialog.show();
                HashMap<String,String> map = new HashMap<>();
                map.put("address",selectedAddress);
                map.put("contractor",selectedContractor);
                map.put("date", date);
                map.put("description",description);

                firebaseFirestore
                        .collection("appoinentments")
                        .document()
                        .set(map)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(JobSchedularActivity.this,"Success",Toast.LENGTH_SHORT).show();
                                    dateEdit.setText("");
                                    descriptionEdit.setText("");
                                    progressDialog.dismiss();
                                    Intent intent = new Intent(JobSchedularActivity.this, PropertyActivity.class);
                                    startActivity(intent);
                                    finish();
                                }

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                            }
                        });
            }
        });


    }

    private void getContractors(){
        firebaseFirestore
        .collection("contractors")
        .document(firebaseAuth.getCurrentUser().getUid())
        .collection("myContractors")
        .get()
        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
               if(task.isSuccessful()){
                   for(DocumentSnapshot documentSnapshot : task.getResult()){
                       ContractorModel contractorModel = documentSnapshot.toObject(ContractorModel.class);
                       String contractorName = contractorModel.getContractorname();
                       contractorsList.add(contractorName);
                       ArrayAdapter arrayAdapter = new ArrayAdapter(JobSchedularActivity.this,android.R.layout.simple_spinner_item,contractorsList.toArray());
                       arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                       contractorSpinner.setAdapter(arrayAdapter);
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
    private void getProperties(){
       firebaseFirestore
       .collection("properties")
       .document(firebaseAuth.getCurrentUser().getUid())
       .collection("myProperties")
       .get()
       .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
           @Override
           public void onComplete(@NonNull Task<QuerySnapshot> task) {
               if(task.isSuccessful()){
                   for(DocumentSnapshot snapshot : task.getResult()){
                       firebasemodel firebasemodel = snapshot.toObject(com.example.propertyproject.firebasemodel.class);
                       addressList.add(firebasemodel.getAddress());
                       ArrayAdapter arrayAdapter = new ArrayAdapter(JobSchedularActivity.this,android.R.layout.simple_spinner_item,addressList.toArray());
                       arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                       addressSpinner.setAdapter(arrayAdapter);
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
    private void getSelectedDate(){
        com.example.propertyproject.jobschedular.DatePicker datePicker = new com.example.propertyproject.jobschedular.DatePicker();
        datePicker.show(getSupportFragmentManager(),"date picker");
    }
    private void getAppointments(){
        firebaseFirestore
                .collection("appoinentments")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                       if(task.isSuccessful()){
                           for(DocumentSnapshot snapshot : task.getResult()){
                               JobModel jobModel = snapshot.toObject(JobModel.class);
                               jobModelArrayList.add(jobModel);
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

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar mCalendar = Calendar.getInstance();
        mCalendar.set(Calendar.YEAR, year);
        mCalendar.set(Calendar.MONTH, month);
        mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        String selectedDate = DateFormat.getDateInstance(DateFormat.FULL).format(mCalendar.getTime());
        dateEdit.setText(selectedDate);
    }

}