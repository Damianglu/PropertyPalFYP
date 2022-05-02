package com.example.propertyproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.example.propertyproject.weather.models.Sys;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class createProperty extends AppCompatActivity {

    private Uri imageUri;
    EditText mcreateeircodeofproperty, mcreateaddressofproperty,mcreatepropertybeds, mcreatepropertybaths, mcreatepropertyrent,
    mcreatepropertytenantname, mcreatepropertytenantemail, mcreatepropertytenantphone;
    CircleImageView imageView;
    FloatingActionButton msaveproperty;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseFirestore firebaseFirestore;
    StorageReference storageReference;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_property);

        msaveproperty = findViewById(R.id.saveproperty);
        mcreateeircodeofproperty = findViewById(R.id.createeircodeofproperty);
        mcreateaddressofproperty = findViewById(R.id.createaddressofproperty);
        mcreatepropertybeds = findViewById(R.id.createpropertybeds);
        mcreatepropertybaths = findViewById(R.id.createpropertybaths);
        mcreatepropertyrent =findViewById(R.id.createpropertyrent);
        mcreatepropertytenantname = findViewById(R.id.createpropertytenantname);
        mcreatepropertytenantemail = findViewById(R.id.createpropertytenantemail);
        mcreatepropertytenantphone = findViewById(R.id.createpropertytenantphone);
        imageView = findViewById(R.id.propertyImage);

        Toolbar toolbar = findViewById(R.id.toolbarofcreateproperty);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Saving Property Info ..");
        progressDialog.setCanceledOnTouchOutside(false);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();


        // Add new image
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNewImage();
            }
        });

        msaveproperty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String eircode = mcreateeircodeofproperty.getText().toString();
                String address = mcreateaddressofproperty.getText().toString();
                String beds = mcreatepropertybeds.getText().toString();
                String baths  = mcreatepropertybaths.getText().toString();
                String rent = mcreatepropertyrent.getText().toString();
                String tenantName = mcreatepropertytenantname.getText().toString();
                String tenantEmail = mcreatepropertytenantemail.getText().toString();
                String tenantPhone = mcreatepropertytenantphone.getText().toString();

                if(eircode.isEmpty() || address.isEmpty() || beds.isEmpty() || baths.isEmpty() || rent.isEmpty()
                || tenantName.isEmpty() || tenantEmail.isEmpty() || tenantPhone.isEmpty())
                {
                    Toast.makeText(getApplicationContext(), "All fields are required",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    if(imageUri == null){
                        Toast.makeText(createProperty.this,"Please select a property image",Toast.LENGTH_SHORT).show();
                    } else {
                        int rentRange = Integer.parseInt(rent.trim());
                        if(rentRange > 3000 || rentRange < 1000){
                            Toast.makeText(createProperty.this,"Rent Should be between 1000 - 3000",Toast.LENGTH_SHORT).show();
                        } else {
                            progressDialog.show();
                            storageReference
                                    .child("Images")
                                    .child(String.valueOf(System.currentTimeMillis()))
                                    .putFile(imageUri)
                                    .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                            if(task.isSuccessful()){
                                                task.getResult().getStorage().getDownloadUrl()
                                                        .addOnCompleteListener(new OnCompleteListener<Uri>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Uri> task) {
                                                                // Push Image + Data into firebase database
                                                                String propertyID = String.valueOf(Calendar.getInstance().getTimeInMillis());
                                                                DocumentReference documentReference = firebaseFirestore
                                                                        .collection("properties")
                                                                        .document(firebaseUser.getUid())
                                                                        .collection("myProperties")
                                                                        .document(propertyID);
                                                                Map<String , Object> property = new HashMap<>();
                                                                property.put("propertyImage", task.getResult().toString());
                                                                property.put("eircode", eircode);
                                                                property.put("address", address);
                                                                property.put("beds", beds);
                                                                property.put("baths", baths);
                                                                property.put("rent", rent);
                                                                property.put("tenantName", tenantName);
                                                                property.put("tenantEmail", tenantEmail);
                                                                property.put("tenantPhone", tenantPhone);
                                                                property.put("propertyID",propertyID);

                                                                documentReference.set(property).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void unused) {
                                                                        progressDialog.dismiss();
                                                                        Toast.makeText(getApplicationContext(), "Property created succesfully",Toast.LENGTH_SHORT).show();
                                                                        startActivity(new Intent(createProperty.this, PropertyActivity.class));
                                                                    }
                                                                }).addOnFailureListener(new OnFailureListener() {
                                                                    @Override
                                                                    public void onFailure(@NonNull Exception e) {
                                                                        progressDialog.dismiss();
                                                                        Toast.makeText(getApplicationContext(), "Property not created succesfully",Toast.LENGTH_SHORT).show();
                                                                        startActivity(new Intent(createProperty.this, PropertyActivity.class));

                                                                    }
                                                                });
                                                            }
                                                        })
                                                        .addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {

                                                            }
                                                        });
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


                    }
                }
            }
        });
    }

    private void addNewImage() {
        CropImage
                .activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(this);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

       if(item.getItemId()==android.R.id.home)
       {
           onBackPressed();
       }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                imageUri = result.getUri();
                imageView.setImageURI(imageUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}