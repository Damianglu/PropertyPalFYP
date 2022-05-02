package com.example.propertyproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.propertyproject.chart.ContractorChartActivity;
import com.example.propertyproject.chart.RentChartActivity;
import com.example.propertyproject.jobschedular.JobSchedularActivity;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class HomePage extends AppCompatActivity {
    private NavigationView navigationView;
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private FirebaseAuth firebaseAuth;
    Button mpropertyHome, mcontractorHome, mrentStatsHome, mcontractorStatsHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        navigationView = findViewById(R.id.nav_view);
        drawerLayout  = findViewById(R.id.my_drawer_layout);
        toolbar  = findViewById(R.id.toolbar);
        mpropertyHome = findViewById(R.id.propertyHome);
        mcontractorHome = findViewById(R.id.contractorHome);
        mrentStatsHome = findViewById(R.id.rentStatsHome);
        mcontractorStatsHome = findViewById(R.id.contractorStatsHome);

        setSupportActionBar(toolbar);

        firebaseAuth = FirebaseAuth.getInstance();

        mpropertyHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomePage.this, PropertyActivity.class));
            }
        });

        mcontractorHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomePage.this, contractorActivity.class));
            }
        });

        mrentStatsHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomePage.this, RentChartActivity.class));
            }
        });

        mcontractorStatsHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomePage.this, ContractorChartActivity.class));
            }
        });

        createDrawer();

    }

    private void createDrawer(){
        ActionBarDrawerToggle actionBarDrawerToggle  = new ActionBarDrawerToggle(
                this,drawerLayout,toolbar,R.string.openDrawer,R.string.closeDrawer);

        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.weather : {
                        startActivity(new Intent(HomePage.this, WeatherActivity.class));
                    }
                    break;
                    case R.id.openMap : {
                        startActivity(new Intent(HomePage.this, MapsActivity.class));
                    }
                    break;
                    case R.id.covidTracker: {
                        startActivity(new Intent(HomePage.this, C19Tracker.class));
                    }
                    break;
                    case R.id.addContractor : {
                        startActivity(new Intent(HomePage.this, createContractor.class));
                    }
                    break;
                    case R.id.chat : {
                        startActivity(new Intent(HomePage.this, splashMain.class));
                    }
                    break;
                    case R.id.scheduleJob : {
                        startActivity(new Intent(HomePage.this, JobSchedularActivity.class));
                    }
                    break;
                    case R.id.appointments : {
                        startActivity(new Intent(HomePage.this, AppointmentsActivity.class));
                    }
                    break;
                    case R.id.logout : {
                        firebaseAuth.signOut();
                        finish();
                        startActivity(new Intent(HomePage.this, MainActivity.class));
                    }
                    break;
                }
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });

    }

    @Override
    protected void onDestroy() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onDestroy();
        }
    }
}
