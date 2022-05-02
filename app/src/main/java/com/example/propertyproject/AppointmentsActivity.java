package com.example.propertyproject;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.propertyproject.adapters.AppointmentsAdapter;
import com.example.propertyproject.adapters.ImagesAdapter;
import com.example.propertyproject.jobschedular.AddImagesActivity;
import com.example.propertyproject.jobschedular.JobSchedularActivity;
import com.example.propertyproject.models.AppointmentModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.MetadataChanges;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AppointmentsActivity extends AppCompatActivity {
    StorageReference storageReference;
    List<String> stringList;
    private AppointmentsAdapter appointmentsAdapter;
    private List<AppointmentModel> appointmentModelList;
    private List<List<String>> listList;
    FloatingActionButton addAppointment;
    private FirebaseAuth firebaseAuth;
    RecyclerView mrecyclerview;
    StaggeredGridLayoutManager staggeredGridLayoutManager;
    FirebaseUser firebaseUser;
    FirebaseFirestore firebaseFirestore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointments);
        addAppointment = findViewById(R.id.addAppointment);
        Toolbar toolbar = findViewById(R.id.toolbar);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        setSupportActionBar(toolbar);
        appointmentModelList = new ArrayList<>();
        listList = new ArrayList<>();

        addAppointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AppointmentsActivity.this, JobSchedularActivity.class));
            }
        });

        mrecyclerview = findViewById(R.id.recyclerview);
        mrecyclerview.setHasFixedSize(true);
        staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        mrecyclerview.setLayoutManager(staggeredGridLayoutManager);
        getAllAppointments();

    }

    private void getAllAppointments() {
        Query query = firebaseFirestore
                .collection("appoinentments")
                .orderBy("contractor", Query.Direction.ASCENDING);
        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                for(DocumentSnapshot documentSnapshot : value.getDocuments()){
                    AppointmentModel appointmentModel = new AppointmentModel();
                    String address = documentSnapshot.getString("address").toString();
                    String contractor = documentSnapshot.getString("contractor").toString();
                    String date = documentSnapshot.getString("date").toString();
                    String description = documentSnapshot.getString("description").toString();
                    String documentId = documentSnapshot.getString("documentId").toString();
                    stringList = (List<String>) documentSnapshot.get("list");
                    listList.add(stringList);
                    appointmentModel.setAddress(address);
                    appointmentModel.setContractor(contractor);
                    appointmentModel.setDate(date);
                    appointmentModel.setDescription(description);
                    appointmentModel.setDocumentId(documentId);
                    appointmentModelList.add(appointmentModel);
                    appointmentsAdapter  = new AppointmentsAdapter(appointmentModelList,listList);
                    mrecyclerview.setAdapter(appointmentsAdapter);
                    appointmentsAdapter.getImages(new AppointmentsAdapter.onItemClick() {
                        @Override
                        public void addImages(String documentId) {
                            // Add New Images
                            Intent intent = new Intent(AppointmentsActivity.this, AddImagesActivity.class);
                            intent.putExtra("documentId",documentId);
                            startActivity(intent);
                            finish();
                        }
                    });;
                }
            }
        });
    }

}