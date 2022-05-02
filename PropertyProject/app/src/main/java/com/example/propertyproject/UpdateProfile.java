package com.example.propertyproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
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
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class UpdateProfile extends AppCompatActivity {


    private EditText mgetnewusername;

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;

    private FirebaseFirestore firebaseFirestore;

    private ImageView mgetnewuserimageinimageview;
    private StorageReference storageReference;
    private FirebaseStorage firebaseStorage;

    private String ImageURIaccessToken;

    private androidx.appcompat.widget.Toolbar mtoolbarofupdateprofile;
    private Button mupdateprofilebutton;
    private ImageButton mbackbuttonofupdateprofile;

    ProgressBar mprogressbarofupdateprofile;

    private Uri imagepath;

    Intent intent;

    private static int PICK_IMAGE=123;


    String newname;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);


        mtoolbarofupdateprofile = findViewById(R.id.toolbarofupdateprofile);
        mupdateprofilebutton = findViewById(R.id.updateprofilebutton);
        mgetnewuserimageinimageview = findViewById(R.id.getuserimageinimageview);
        mprogressbarofupdateprofile = findViewById(R.id.progressbarofupdateprofile);
        mgetnewusername = findViewById(R.id.getusername);
        mbackbuttonofupdateprofile = findViewById(R.id.backbuttonofupdateprofile);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        intent = getIntent();

        setSupportActionBar(mtoolbarofupdateprofile);

        mbackbuttonofupdateprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mgetnewusername.setText(intent.getStringExtra("nameofuser"));

        DatabaseReference databaseReference = firebaseDatabase.getReference(firebaseAuth.getUid());

        mupdateprofilebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                newname = mgetnewusername.getText().toString();
                if(newname.isEmpty())
                {
                    Toast.makeText(getApplicationContext(), "Name is empty", Toast.LENGTH_SHORT).show();
                }
                else if(imagepath!=null)
                {
                    mprogressbarofupdateprofile.setVisibility(View.VISIBLE);
                    userprofile muserprofile = new userprofile(newname, firebaseAuth.getUid());
                    databaseReference.setValue(muserprofile);

                    updateimagetostorage();

                    Toast.makeText(getApplicationContext(), "Updated", Toast.LENGTH_SHORT).show();
                    mprogressbarofupdateprofile.setVisibility(View.INVISIBLE);
                    Intent intent = new Intent(UpdateProfile.this, chatActivity.class);
                    startActivity(intent);
                    finish();
                }
                else
                {
                    mprogressbarofupdateprofile.setVisibility(View.VISIBLE);
                    userprofile muserprofile = new userprofile(newname, firebaseAuth.getUid());
                    databaseReference.setValue(muserprofile);
                    updatenameoncloudfirestore();
                    Toast.makeText(getApplicationContext(), "Updated", Toast.LENGTH_SHORT).show();
                    mprogressbarofupdateprofile.setVisibility(View.INVISIBLE);
                    Intent intent = new Intent(UpdateProfile.this, chatActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });

        mgetnewuserimageinimageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(intent,PICK_IMAGE);
            }
        });

        storageReference = firebaseStorage.getReference();
        storageReference.child("Images").child(firebaseAuth.getUid()).child("Profile Picture").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                ImageURIaccessToken = uri.toString();
                Picasso.get().load(uri).into(mgetnewuserimageinimageview);
            }
        });





    }

    private void updatenameoncloudfirestore() {

        DocumentReference documentReference = firebaseFirestore.collection("chatusers").document(firebaseAuth.getUid());
        Map<String, Object> chatuserdata = new HashMap<>();
        chatuserdata.put("name", newname);
        chatuserdata.put("image", ImageURIaccessToken);
        chatuserdata.put("uid", firebaseAuth.getUid());
        chatuserdata.put("status", "Online");

        documentReference.set(chatuserdata).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(getApplicationContext(), "Profile updated succesfully", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateimagetostorage() {

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
                        ImageURIaccessToken = uri.toString();
                        Toast.makeText(getApplicationContext(), "URI get access", Toast.LENGTH_SHORT).show();
                        updatenameoncloudfirestore();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode==PICK_IMAGE && resultCode==RESULT_OK)
        {
            imagepath = data.getData();
            mgetnewuserimageinimageview.setImageURI(imagepath);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onStop() {
        super.onStop();
        DocumentReference documentReference = firebaseFirestore.collection("chatusers").document(firebaseAuth.getUid());
        documentReference.update("status", "Offline").addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void avoid) {
                Toast.makeText(getApplicationContext(), "User offline", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        DocumentReference documentReference = firebaseFirestore.collection("chatusers").document(firebaseAuth.getUid());
        documentReference.update("status", "Online").addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void avoid) {
                Toast.makeText(getApplicationContext(), "User online", Toast.LENGTH_SHORT).show();
            }
        });
    }
}