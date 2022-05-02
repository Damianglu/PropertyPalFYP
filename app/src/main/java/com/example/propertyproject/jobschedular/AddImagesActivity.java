package com.example.propertyproject.jobschedular;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.propertyproject.AppointmentsActivity;
import com.example.propertyproject.R;
import com.example.propertyproject.adapters.ImagesAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AddImagesActivity extends AppCompatActivity {
    private FirebaseFirestore firebaseFirestore;
    private StorageReference storageReference;
    private int upload_count = 0;
    private List<String> uriList;
    private List<Uri> imagesList;
    private ImagesAdapter imagesAdapter;
    private ProgressDialog progressDialog;
    private RecyclerView imagesRecyclerView;
    private Button btn;
    private FloatingActionButton fab;
    private String documentId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_images);
        imagesRecyclerView  = findViewById(R.id.imagesRecyclerView);
        btn = findViewById(R.id.btn);
        fab = findViewById(R.id.fab);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading images..");
        progressDialog.setCanceledOnTouchOutside(false);

        imagesRecyclerView.setLayoutManager(new GridLayoutManager(this,2));
        imagesRecyclerView.setHasFixedSize(true);

        imagesList = new ArrayList<>();
        uriList  = new ArrayList<>();

        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        Intent intent = getIntent();
        if(intent != null){
            documentId = intent.getStringExtra("documentId");
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.activity().start(AddImagesActivity.this);
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.show();
                if (!imagesList.isEmpty()) {
                    HashMap<String, Object> map = new HashMap<>();
                    for (upload_count = 0; upload_count < imagesList.size(); upload_count++) {
                        Uri imageUri = imagesList.get(upload_count);
                        Task<UploadTask.TaskSnapshot> task = storageReference
                                .child("Images")
                                .child(String.valueOf(System.currentTimeMillis()))
                                .putFile(imageUri);

                        task.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        uriList.add(String.valueOf(uri));
                                        map.put("list", uriList);
                                        if (uriList.size() == imagesList.size()) {
                                            firebaseFirestore
                                                    .collection("appoinentments")
                                                    .document(documentId)
                                                    .update(map)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                uriList.clear();
                                                                imagesList.clear();
                                                                Toast.makeText(AddImagesActivity.this, "Images Successfully Uploaded", Toast.LENGTH_SHORT).show();
                                                                progressDialog.dismiss();
                                                                finish();
                                                                Intent intent1 = new Intent(AddImagesActivity.this,AppointmentsActivity.class);
                                                                startActivity(intent1);

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
                                });
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
                else {
                    progressDialog.dismiss();
                    Toast.makeText(AddImagesActivity.this,"Please pick an image",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(imagesList.size() < 4){
            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == RESULT_OK) {
                    Uri resultUri = result.getUri();
                    imagesList.add(resultUri);
                    imagesAdapter = new ImagesAdapter(imagesList);
                    imagesRecyclerView.setAdapter(imagesAdapter);
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Exception error = result.getError();
                }
            }
        } else {
            Toast.makeText(this, "4 Images MAX", Toast.LENGTH_SHORT).show();
        }
    }
}