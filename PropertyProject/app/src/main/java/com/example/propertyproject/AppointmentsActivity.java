package com.example.propertyproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.example.propertyproject.chart.RentChartActivity;
import com.example.propertyproject.jobschedular.JobSchedularActivity;
import com.example.propertyproject.models.AppointmentModel;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

public class AppointmentsActivity extends AppCompatActivity {
    FloatingActionButton addAppointment;
    private FirebaseAuth firebaseAuth;
    RecyclerView mrecyclerview;
    StaggeredGridLayoutManager staggeredGridLayoutManager;
    FirebaseUser firebaseUser;
    FirebaseFirestore firebaseFirestore;
    FirestoreRecyclerAdapter<AppointmentModel, AppointmentsActivity.AppointmentsViewHolder> appointmentsAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointments);
        addAppointment = findViewById(R.id.addAppointment);
        Toolbar toolbar = findViewById(R.id.toolbar);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();

        setSupportActionBar(toolbar);

        addAppointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AppointmentsActivity.this, JobSchedularActivity.class));
            }
        });

        Query query = firebaseFirestore.collection("appoinentments")
                .orderBy("contractor", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<AppointmentModel> appointments =
                new FirestoreRecyclerOptions.Builder<AppointmentModel>().setQuery(query, AppointmentModel.class).build();

        appointmentsAdapter = new FirestoreRecyclerAdapter<AppointmentModel, AppointmentsActivity.AppointmentsViewHolder>(appointments) {
            @Override
            protected void onBindViewHolder(@NonNull AppointmentsActivity.AppointmentsViewHolder appointmentsViewHolder, int i, @NonNull AppointmentModel model) {
                appointmentsViewHolder.address.setText(model.getAddress());
                appointmentsViewHolder.contractor.setText(model.getContractor());
                appointmentsViewHolder.date.setText(model.getDate());
                appointmentsViewHolder.description.setText(model.getDescription());


            }

            @NonNull
            @Override
            public AppointmentsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.appointments_layout, parent, false);
                return new AppointmentsViewHolder(view);
            }
        };

        mrecyclerview = findViewById(R.id.recyclerview);
        mrecyclerview.setHasFixedSize(true);
        staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        mrecyclerview.setLayoutManager(staggeredGridLayoutManager);
        mrecyclerview.setAdapter(appointmentsAdapter);
    }


    public class AppointmentsViewHolder extends RecyclerView.ViewHolder {
        private ImageView menu;
        private TextView address;
        private TextView contractor;
        private TextView date;
        private TextView description;

        LinearLayout mcontractor;

        public AppointmentsViewHolder(@NonNull View itemView) {
            super(itemView);
            menu  = itemView.findViewById(R.id.menupopbutton);
            address = itemView.findViewById(R.id.address);
            contractor = itemView.findViewById(R.id.contractor);
            date = itemView.findViewById(R.id.date);
            description = itemView.findViewById(R.id.description);

        }
    }



    @Override
    protected void onStart() {
        super.onStart();
        appointmentsAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (appointmentsAdapter != null) { appointmentsAdapter.startListening(); }
    }
}