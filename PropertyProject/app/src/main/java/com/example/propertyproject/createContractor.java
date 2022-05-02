package com.example.propertyproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class createContractor extends AppCompatActivity {
    String[] fields = { "Plumber", "Electrician", "Carpenter", "Driver"};
    private int selectedPosition;
    private String selectedField = "";
    private Spinner spinner;
    EditText mcreatecontractorname, mcreatecontractorarea, mcreatecontractorphone, mcreatecontractorrate;
    FloatingActionButton msavecontractor;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseFirestore firebaseFirestore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_contractor);
        //save button
        msavecontractor = findViewById(R.id.savecontractor);
        //fields
        spinner = findViewById(R.id.spinner);
        mcreatecontractorname = findViewById(R.id.createcontractorname);
        mcreatecontractorarea = findViewById(R.id.createcontractorarea);
        mcreatecontractorphone = findViewById(R.id.createcontractorphone);
        mcreatecontractorrate = findViewById(R.id.createcontractorrate);
        //database
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();


        ArrayAdapter arrayAdapter = new ArrayAdapter(this,android.R.layout.simple_spinner_item,fields);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedField = fields[i];
                selectedPosition = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        msavecontractor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String contractorname = mcreatecontractorname.getText().toString();
                String contractorarea = mcreatecontractorarea.getText().toString();
                String contractornumber = mcreatecontractorphone.getText().toString();
                String contractorrate = mcreatecontractorrate.getText().toString();

                if(contractorname.isEmpty() || contractorarea.isEmpty()
                || contractornumber.isEmpty() || contractorrate.isEmpty())
                {
                    Toast.makeText(getApplicationContext(), "All fields are required",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    if(selectedField.equals("")){
                        Toast.makeText(createContractor.this,"Please select a field",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    String uniqueID = String.valueOf(Calendar.getInstance().getTimeInMillis());
                    DocumentReference documentReference = firebaseFirestore
                            .collection("contractors")
                            .document(firebaseUser.getUid())
                            .collection("myContractors")
                            .document(uniqueID);
                    Map<String , Object> contractor = new HashMap<>();
                    contractor.put("contractorfield", selectedField);
                    contractor.put("contractorname", contractorname);
                    contractor.put("contractorarea", contractorarea);
                    contractor.put("contractornumber",contractornumber);
                    contractor.put("contractorrate", contractorrate);
                    contractor.put("contractorID",uniqueID);
                    contractor.put("position",String.valueOf(selectedPosition));

                    documentReference.set(contractor).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(getApplicationContext(), "Contractor created succesfully",Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(createContractor.this, contractorActivity.class));
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "Contractor not created succesfully",Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(createContractor.this, contractorActivity.class));
                        }
                    });
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }


        return super.onOptionsItemSelected(item);
    }
}