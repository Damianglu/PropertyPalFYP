package com.example.propertyproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class editProperty extends AppCompatActivity {
    private Uri imageUri;
    private String propertyID;
    EditText mediteircode, meditaddress, meditbeds, meditbaths, meditrent, medittenantname, medittenantemail, medittenantphone;
    FloatingActionButton msaveproperty;
    private ImageView propertyImage;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    FirebaseUser firebaseUser;
    StorageReference storageReference;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_property);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Updating Property Info ..");
        progressDialog.setCanceledOnTouchOutside(false);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();;
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        Intent intent = getIntent();
        if(intent != null){
            propertyID = intent.getStringExtra("propertyID");
            showPropertyDetails();
        }

        propertyImage = findViewById(R.id.propertyImage);
        mediteircode = findViewById(R.id.editeircode);
        meditaddress = findViewById(R.id.editaddress);
        meditbeds = findViewById(R.id.editbeds);
        meditbaths = findViewById(R.id.editbaths);
        meditrent = findViewById(R.id.editrent);
        medittenantname = findViewById(R.id.edittenantname);
        medittenantemail = findViewById(R.id.edittenantemail);
        medittenantphone = findViewById(R.id.edittenantphone);
        msaveproperty = findViewById(R.id.saveproperty);

        msaveproperty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateProperty();
            }


        });
        // Add new image
        propertyImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNewImage();
            }
        });

    }
    private void addNewImage() {
        CropImage
                .activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(this);
    }
    private void updateProperty() {

        String eircode = mediteircode.getText().toString();
        String address = meditaddress.getText().toString();
        String beds = meditbeds.getText().toString();
        String baths  = meditbaths.getText().toString();
        String rent = meditrent.getText().toString();
        String tenantName = medittenantname.getText().toString();
        String tenantEmail = medittenantemail.getText().toString();
        String tenantPhone = medittenantphone.getText().toString();

        if(eircode.isEmpty() || address.isEmpty() || beds.isEmpty() || baths.isEmpty() || rent.isEmpty()
                || tenantName.isEmpty() || tenantEmail.isEmpty() || tenantPhone.isEmpty()) {
            Toast.makeText(getApplicationContext(), "All fields are required",Toast.LENGTH_SHORT).show();
        }
        else {
            if(imageUri == null){
                Toast.makeText(editProperty.this,"Please select a property image",Toast.LENGTH_SHORT).show();
            } else {
                int rentRange = Integer.parseInt(rent.trim());
                if(rentRange > 3000 || rentRange < 1000){
                    Toast.makeText(editProperty.this,"Rent Should be between 1000 - 3000",Toast.LENGTH_SHORT).show();
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

                                                        documentReference.update(property).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void unused) {
                                                                progressDialog.dismiss();
                                                                Toast.makeText(getApplicationContext(), "Property Updated Successfully",Toast.LENGTH_SHORT).show();
                                                                startActivity(new Intent(editProperty.this, PropertyActivity.class));
                                                            }
                                                        }).addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                progressDialog.dismiss();
                                                                Toast.makeText(getApplicationContext(), "Property not updated",Toast.LENGTH_SHORT).show();
                                                                startActivity(new Intent(editProperty.this, PropertyActivity.class));

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
    private void showPropertyDetails() {
          firebaseFirestore.collection("properties")
                .document(firebaseUser.getUid())
                .collection("myProperties")
                .document(propertyID)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()){
                            firebasemodel firebasemodel = documentSnapshot.toObject(com.example.propertyproject.firebasemodel.class);
                            mediteircode.setText(firebasemodel.eircode);
                            meditaddress.setText(firebasemodel.address);
                            meditbeds.setText(firebasemodel.beds);
                            meditbaths.setText(firebasemodel.baths);
                            meditrent.setText(firebasemodel.rent);
                            medittenantname.setText(firebasemodel.tenantName);
                            medittenantemail.setText(firebasemodel.tenantEmail);
                            medittenantphone.setText(firebasemodel.tenantPhone);

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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                imageUri = result.getUri();
                propertyImage.setImageURI(imageUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}