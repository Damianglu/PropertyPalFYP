package com.example.propertyproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class editContractor extends AppCompatActivity {
    private String contractorID;
    private int selectedPosition;
    String[] fields = { "Plumber", "Electrician", "Carpenter", "Driver"};
    private String selectedField = "";
    private Spinner spinner;
    EditText mcreatecontractorname, mcreatecontractorarea, mcreatecontractorphone, mcreatecontractorrate;
    FloatingActionButton msavecontractor;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseFirestore firebaseFirestore;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_contractor);

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

        Intent intent = getIntent();
        if(intent != null){
            contractorID = intent.getStringExtra("contractorID");
            showContractorInfo();
        }

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Updating Contractor Info ..");
        progressDialog.setCanceledOnTouchOutside(false);


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
                updateContractor();
            }
        });
    }

    private void showContractorInfo() {
       firebaseFirestore
                .collection("contractors")
                .document(firebaseUser.getUid())
                .collection("myContractors")
                .document(contractorID)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()){
                            firebasemodel1  firebasemodel1 = documentSnapshot.toObject(com.example.propertyproject.firebasemodel1.class);
                            mcreatecontractorname.setText(firebasemodel1.getContractorname());
                            mcreatecontractorarea.setText(firebasemodel1.getContractorrate());
                            mcreatecontractorphone.setText(firebasemodel1.getContractornumber());
                            mcreatecontractorrate.setText(firebasemodel1.getContractorrate());
                            spinner.setSelection(Integer.parseInt(firebasemodel1.getPosition()));
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }
    private void updateContractor(){
        String contractorname = mcreatecontractorname.getText().toString();
        String contractorarea = mcreatecontractorarea.getText().toString();
        String contractornumber = mcreatecontractorphone.getText().toString();
        String contractorrate = mcreatecontractorrate.getText().toString();

        if(contractorname.isEmpty() || contractorarea.isEmpty()
                || contractornumber.isEmpty() || contractorrate.isEmpty()) {
            Toast.makeText(getApplicationContext(), "All fields are required",Toast.LENGTH_SHORT).show();
        }
        else
        {
            if(selectedField.equals("")){
                Toast.makeText(editContractor.this,"Please select a field",Toast.LENGTH_SHORT).show();
                return;
            }
            progressDialog.show();
            DocumentReference documentReference = firebaseFirestore
                    .collection("contractors")
                    .document(firebaseUser.getUid())
                    .collection("myContractors")
                    .document(contractorID);
            Map<String , Object> contractor = new HashMap<>();
            contractor.put("contractorfield", selectedField);
            contractor.put("contractorname", contractorname);
            contractor.put("contractorarea", contractorarea);
            contractor.put("contractornumber",contractornumber);
            contractor.put("contractorrate", contractorrate);
            contractor.put("contractorID",contractorID);
            contractor.put("position",String.valueOf(selectedPosition));

            documentReference.update(contractor).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Contractor created succesfully",Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(editContractor.this, contractorActivity.class));
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Contractor not created succesfully",Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(editContractor.this, contractorActivity.class));
                }
            });
        }
    }
}