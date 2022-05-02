package com.example.propertyproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.w3c.dom.Document;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class setProfile extends AppCompatActivity {

    private CardView mgetuserimage;
    private ImageView mgetuserimageinimageview;
    private static int PICK_IMAGE = 123;
    private Uri imagepath;

    private EditText mgetusername;

    private android.widget.Button msaveProfile;

    private FirebaseAuth firebaseAuth;
    private String name;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

    private String ImageUriAccessToken;

    private FirebaseFirestore firebaseFirestore;

    private ProgressBar mprogressbarofsaveprofile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_profile);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        firebaseFirestore = FirebaseFirestore.getInstance();

        mgetusername = findViewById(R.id.getusername);
        mgetuserimage = findViewById(R.id.getuserimage);
        mgetuserimageinimageview = findViewById(R.id.getuserimageinimageview);
        msaveProfile = findViewById(R.id.saveProfile);
        mprogressbarofsaveprofile = findViewById(R.id.progressbarofsaveprofile);

        mgetuserimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(intent, PICK_IMAGE);
            }
        });

        msaveProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name = mgetusername.getText().toString();
                if(name.isEmpty())
                {
                    Toast.makeText(getApplicationContext(), "Name is empty",Toast.LENGTH_SHORT).show();
                }
                else if(imagepath==null)
                {
                    Toast.makeText(getApplicationContext(), "Image not selected",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    mprogressbarofsaveprofile.setVisibility(View.VISIBLE);
                    sendDataForNewUser();
                    mprogressbarofsaveprofile.setVisibility(View.INVISIBLE);
                    Intent intent = new Intent(setProfile.this, chatActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });

    }

    private void sendDataForNewUser()
    {
        sendDataToRealtimeDatabase();
    }

    private void sendDataToRealtimeDatabase()
    {
        name = mgetusername.getText().toString();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference(firebaseAuth.getUid());

        userprofile muserprofile = new userprofile(name, firebaseAuth.getUid());
        databaseReference.setValue(muserprofile);
        Toast.makeText(getApplicationContext(), "User profile added succesfully", Toast.LENGTH_SHORT).show();
        sendImageToStorage();
    }

    private void sendImageToStorage()
    {
        StorageReference imageref = storageReference.child("Images").child(firebaseAuth.getUid()).child("Profile Picture");

        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),imagepath);
        }catch (IOException e)
        {
            e.printStackTrace();
        }

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 25, byteArrayOutputStream);
        byte[] data = byteArrayOutputStream.toByteArray();

        UploadTask uploadTask = imageref.putBytes(data);

        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imageref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        ImageUriAccessToken = uri.toString();
                        Toast.makeText(getApplicationContext(), "URI get access", Toast.LENGTH_SHORT).show();
                        sendDataToCloudFirestore();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "URI get failed", Toast.LENGTH_SHORT).show();
                    }
                });
                Toast.makeText(getApplicationContext(), "Image uploaded", Toast.LENGTH_SHORT).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Image upload failed", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void sendDataToCloudFirestore() {
        DocumentReference documentReference = firebaseFirestore.collection("chatusers").document(firebaseAuth.getUid());
        Map<String, Object> chatuserdata = new HashMap<>();
        chatuserdata.put("name", name);
        chatuserdata.put("image", ImageUriAccessToken);
        chatuserdata.put("uid", firebaseAuth.getUid());
        chatuserdata.put("status", "Online");

        documentReference.set(chatuserdata).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(getApplicationContext(), "Data sent to cloud firestore", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
       if(requestCode==PICK_IMAGE && resultCode==RESULT_OK)
       {
           imagepath = data.getData();
           mgetuserimageinimageview.setImageURI(imagepath);
       }

        super.onActivityResult(requestCode, resultCode, data);
    }
}