package com.example.propertyproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;

import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationRequest;
import android.os.Bundle;
import android.os.Looper;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.propertyproject.chart.RentChartActivity;
import com.example.propertyproject.jobschedular.JobSchedularActivity;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class contractorActivity extends AppCompatActivity {
    private NavigationView navigationView;
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    FloatingActionButton mcreatecontractorsfab;
    private FirebaseAuth firebaseAuth;
    RecyclerView mrecyclerview;
    StaggeredGridLayoutManager staggeredGridLayoutManager;
    FirebaseUser firebaseUser;
    FirebaseFirestore firebaseFirestore;
    private FusedLocationProviderClient fusedLocationProviderClient;
    FirestoreRecyclerAdapter<firebasemodel1
            , contractorActivity.ContractorViewHolder> contractorAdapter;
    private com.google.android.gms.location.LocationRequest locationRequest;
    private LocationCallback locationCallback;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contractor);
        navigationView = findViewById(R.id.nav_view);
        drawerLayout  = findViewById(R.id.drawerLayout);
        toolbar  = findViewById(R.id.toolbar);
        mcreatecontractorsfab = findViewById(R.id.createcontractorsfab);
        firebaseAuth = FirebaseAuth.getInstance();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        setSupportActionBar(toolbar);

        grantPermissions();

        mcreatecontractorsfab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(contractorActivity.this, createContractor.class));
            }
        });

        Query query = firebaseFirestore.collection("contractors")
                .document(firebaseUser.getUid())
                .collection("myContractors")
                .orderBy("contractorarea", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<firebasemodel1> allproperties = new FirestoreRecyclerOptions.Builder<firebasemodel1>().setQuery(query, firebasemodel1.class).build();

        contractorAdapter = new FirestoreRecyclerAdapter<firebasemodel1, contractorActivity.ContractorViewHolder>(allproperties) {
            @Override
            protected void onBindViewHolder(@NonNull contractorActivity.ContractorViewHolder contractorViewHolder, int i, @NonNull firebasemodel1 firebasemodel1) {
                contractorViewHolder.contractorField.setText(Html.fromHtml("<b>" + "Field " + "</b>" +firebasemodel1.getContractorfield()));
                contractorViewHolder.contractorName.setText(Html.fromHtml("<b>" + "Name " + "</b>" +firebasemodel1.getContractorname()));
                contractorViewHolder.contractorNumber.setText(Html.fromHtml("<b>" + "Number " + "</b>" +firebasemodel1.getContractornumber()));
                contractorViewHolder.contractorRate.setText(Html.fromHtml("<b>" + "Rate " + "</b>" +firebasemodel1.getContractorrate()));
                contractorViewHolder.contractorArea.setText(Html.fromHtml("<b>" + "Area " + "</b>" +firebasemodel1.getContractorarea()));
                contractorViewHolder.menu.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        PopupMenu popupMenu = new PopupMenu(contractorViewHolder.itemView.getContext(),contractorViewHolder.menu);
                        popupMenu.inflate(R.menu.adapter_menu);
                        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem menuItem) {
                                switch (menuItem.getItemId()){
                                    case R.id.delete:
                                        deleteContractor(firebasemodel1.getContractorID());
                                        return true;
                                    case R.id.edit:
                                        Intent intent = new Intent(contractorViewHolder.itemView.getContext(),editContractor.class);
                                        intent.putExtra("contractorID",firebasemodel1.getContractorID());
                                        startActivity(intent);
                                        return true;
                                    default:
                                        return false;
                                }
                            }
                        });
                        popupMenu.show();
                    }
                });
            }

            @NonNull
            @Override
            public contractorActivity.ContractorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contractor_layout, parent, false);
                return new contractorActivity.ContractorViewHolder(view);
            }
        };

        mrecyclerview = findViewById(R.id.recyclerview);
        mrecyclerview.setHasFixedSize(true);
        staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        mrecyclerview.setLayoutManager(staggeredGridLayoutManager);
        mrecyclerview.setAdapter(contractorAdapter);

        createDrawer();
    }

    private void deleteContractor(String contractorID) {
        firebaseFirestore.collection("contractors")
                .document(firebaseUser.getUid())
                .collection("myContractors")
                .document(contractorID)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(contractorActivity.this,"Contractor Successfully Deleted",
                                Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    public class ContractorViewHolder extends RecyclerView.ViewHolder {
        private ImageView menu;
        private TextView contractorField;
        private TextView contractorName;
        private TextView contractorNumber;
        private TextView contractorRate;
        private TextView contractorArea;

        LinearLayout mcontractor;

        public ContractorViewHolder(@NonNull View itemView) {
            super(itemView);
            menu = itemView.findViewById(R.id.menupopbutton);
            contractorField = itemView.findViewById(R.id.contractorField);
            contractorName = itemView.findViewById(R.id.contractorName);
            contractorNumber = itemView.findViewById(R.id.contractorNumber);
            contractorRate = itemView.findViewById(R.id.contractorRate);
            contractorArea = itemView.findViewById(R.id.contractorArea);
            mcontractor = itemView.findViewById(R.id.contractor);

        }
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
                        startActivity(new Intent(contractorActivity.this, WeatherActivity.class));
                    }
                    break;
                    case R.id.openMap : {
                        startActivity(new Intent(contractorActivity.this, MapsActivity.class));
                    }
                    break;
                    case R.id.covidTracker : {
                        startActivity(new Intent(contractorActivity.this, C19Tracker.class));
                    }
                    break;
                    case R.id.addContractor : {
                        startActivity(new Intent(contractorActivity.this, createContractor.class));
                    }
                    break;
                    case R.id.chat : {
                        startActivity(new Intent(contractorActivity.this, splashMain.class));
                    }
                    break;
                    case R.id.stats : {
                        startActivity(new Intent(contractorActivity.this, DataDisplay.class));
                    }
                    break;
                    case R.id.rentStats : {
                        startActivity(new Intent(contractorActivity.this, RentChartActivity.class));
                    }
                    break;
                    case R.id.scheduleJob : {
                        startActivity(new Intent(contractorActivity.this, JobSchedularActivity.class));
                    }
                    case R.id.appointments:  {
                        startActivity(new Intent(contractorActivity.this, AppointmentsActivity.class));
                    }
                    break;
                    case R.id.logout : {
                        firebaseAuth.signOut();
                        finish();
                        startActivity(new Intent(contractorActivity.this, MainActivity.class));
                    }
                    break;
                }
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });

    }


    @Override
    protected void onStart() {
        super.onStart();
        contractorAdapter
                .startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (contractorAdapter != null) { contractorAdapter.startListening(); }
    }

    private void grantPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation();
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    && ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                Toast.makeText(contractorActivity.this, "Permission Need to show location on map",
                        Toast.LENGTH_SHORT).show();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION}, 1000);
            }
        }
    }

    @SuppressLint("MissingPermission")
    private void getCurrentLocation() {
        locationRequest = com.google.android.gms.location.LocationRequest.create();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.QUALITY_HIGH_ACCURACY);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                Double latitude = locationResult.getLocations().get(0).getLatitude();
                Double longitude = locationResult.getLocations().get(0).getLongitude();
                saveLatitudeLongitude(latitude, longitude);
            }

            @Override
            public void onLocationAvailability(@NonNull LocationAvailability locationAvailability) {
                super.onLocationAvailability(locationAvailability);
            }
        };


        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());


        task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            Double latitude = location.getLatitude();
                            Double longitude = location.getLongitude();
                            saveLatitudeLongitude(latitude, longitude);
                        }
                    }
                });
            }
        });

        task.addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    // Location settings are not satisfied, but this can be fixed
                    // by showing the user a dialog.
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(contractorActivity.this,
                                1000);
                    } catch (IntentSender.SendIntentException sendEx) {
                        // Ignore the error.
                    }
                }
            }
        });

    }

    private void saveLatitudeLongitude(Double latitude, Double Longitude) {
        SharedPreferences sharedPreferences = getSharedPreferences("prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong("latitude", latitude.longValue());
        editor.putLong("longitude", Longitude.longValue());
        editor.apply();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1000) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION}, 1000);
//                 ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }

        }
    }

    @Override
    protected void onDestroy() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onDestroy();
            fusedLocationProviderClient.removeLocationUpdates(locationCallback);
        }

    }
}

//    FloatingActionButton mcreatecontractorsfab;
//    private FirebaseAuth firebaseAuth;
//    RecyclerView mrecyclerView;
//    StaggeredGridLayoutManager staggeredGridLayoutManager;
//    FirebaseUser firebaseUser;
//    FirebaseFirestore firebaseFirestore;
//    FirestoreRecyclerAdapter<firebasemodel1, ContractorViewHolder> contractorAdapter;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_contractor);
//
//        mcreatecontractorsfab = findViewById(R.id.createcontractorsfab);
//        firebaseAuth = FirebaseAuth.getInstance();
//
//        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
//        firebaseFirestore = FirebaseFirestore.getInstance();
//
//        getSupportActionBar().setTitle("All Contractors");
//
//        mcreatecontractorsfab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startActivity(new Intent(contractorActivity.this, createContractor.class));
//            }
//        });
//
//        Query query = firebaseFirestore.collection("contractors").document(firebaseUser.getUid()).collection("myContractors").orderBy("contractorfield", Query.Direction.ASCENDING);
//
//        FirestoreRecyclerOptions<firebasemodel1> allcontractors = new FirestoreRecyclerOptions.Builder<firebasemodel1>().setQuery(query, firebasemodel1.class).build();
//
//        contractorAdapter = new FirestoreRecyclerAdapter<firebasemodel1, ContractorViewHolder>(allcontractors) {
//            @Override
//            protected void onBindViewHolder(@NonNull ContractorViewHolder holder, int position, @NonNull firebasemodel1 model) {
//
//            }
//
//            @NonNull
//            @Override
//            public ContractorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//                return null;
//            }
//        };
//
//
//        mrecyclerView=findViewById(R.id.recyclerview);
//        mrecyclerView.setHasFixedSize(false);
//        staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
//        mrecyclerView.setLayoutManager(staggeredGridLayoutManager);
//        mrecyclerView.setAdapter(contractorAdapter);
//
//    }
//
//    public class ContractorViewHolder extends RecyclerView.ViewHolder {
//        private TextView cField;
//        private TextView cName;
//        private TextView cArea;
//        private TextView cNumber;
//        private TextView cRate;
//        LinearLayout mcontractor;
//
//        public ContractorViewHolder(@NonNull View itemView) {
//            super(itemView);
//
//            cField = itemView.findViewById(R.id.contractorField);
//            cName = itemView.findViewById(R.id.contractorName);
//            cArea = itemView.findViewById(R.id.contractorArea);
//            cNumber = itemView.findViewById(R.id.contractorNumber);
//            cRate = itemView.findViewById(R.id.contractorRate);
//            mcontractor = itemView.findViewById(R.id.contractor);
//
//        }
//    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu, menu);
//        return true;
//    }
//
//    @SuppressLint("NonConstantResourceId")
//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        int id = item.getItemId();
//        switch (id) {
//            case R.id.weatherItem:
//                startActivity(new Intent(contractorActivity.this, WeatherActivity.class));
//                return true;
//            case R.id.mapItem:
//                startActivity(new Intent(contractorActivity.this, MapsActivity.class));
//                return true;
//            case R.id.Covidtracker:
//                startActivity(new Intent(contractorActivity.this, C19Tracker.class));
//                return true;
//            case R.id.addContractor:
//                startActivity(new Intent(contractorActivity.this, createContractor.class));
//                return true;
//            case R.id.logout:
//                firebaseAuth.signOut();
//                finish();
//                startActivity(new Intent(contractorActivity.this, MainActivity.class));
//                return true;
//            default: return super.onOptionsItemSelected(item);
//        }
//
//    }
//
//    @Override
//    protected void onStart() {
//        super.onStart();
//        contractorAdapter.startListening();
//    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//        if (contractorAdapter != null) {
//            contractorAdapter.startListening();
//        }
//    }
//}


//    FloatingActionButton mcreatecontractorsfab;
//    private FirebaseAuth firebaseAuth;
//
//    RecyclerView mrecyclerview;
//    StaggeredGridLayoutManager staggeredGridLayoutManager;
//
//    FirebaseUser firebaseUser;
//    FirebaseFirestore firebaseFirestore;
//
//    FirestoreRecyclerAdapter<firebasemodel1, ContractorViewHolder> contractorAdapter;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_contractor);
//
//        mcreatecontractorsfab = findViewById(R.id.createcontractorsfab);
//        firebaseAuth = FirebaseAuth.getInstance();
//
//        firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
//        firebaseFirestore=FirebaseFirestore.getInstance();
//
//        getSupportActionBar().setTitle("All Contractors");
//
//        mcreatecontractorsfab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startActivity(new Intent(contractorActivity.this, createContractor.class));
//            }
//        });
//
//        Query query = firebaseFirestore.collection("contractors").document(firebaseUser.getUid()).collection("myContractors").orderBy("contractorfield", Query.Direction.ASCENDING);
//
//        FirestoreRecyclerOptions<firebasemodel1> allcontractors = new FirestoreRecyclerOptions.Builder<firebasemodel1>().setQuery(query, firebasemodel1.class).build();
//
//        contractorAdapter = new FirestoreRecyclerAdapter<firebasemodel1, ContractorViewHolder>(allcontractors) {
//            @Override
//            protected void onBindViewHolder(@NonNull ContractorViewHolder contractorViewHolder, int i, @NonNull firebasemodel1 firebasemodel1) {
//
//                contractorViewHolder.contractorField.setText(firebasemodel1.getContractorfield());
//                contractorViewHolder.contractorName.setText(firebasemodel1.getContractorname());
//                contractorViewHolder.contractorArea.setText(firebasemodel1.getContractorarea());
//                contractorViewHolder.contractorNumber.setText(firebasemodel1.getContractornumber());
//                contractorViewHolder.contractorRate.setText(firebasemodel1.getContractorrate());
//
//                String conId = contractorAdapter.getSnapshots().getSnapshot(i).getId();
//
//                contractorViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        Intent intent = new Intent(view.getContext(), contractordetails.class);
//                        intent.putExtra("Field", firebasemodel1.getContractorfield());
//                        intent.putExtra("Name", firebasemodel1.getContractorname());
//                        intent.putExtra("Area", firebasemodel1.getContractorarea());
//                        intent.putExtra("Number", firebasemodel1.getContractornumber());
//                        intent.putExtra("Rate", firebasemodel1.getContractorrate());
//                        intent.putExtra("conId", conId);
//                        view.getContext().startActivity(intent);
//                    }
//                });
//            }
//
//            @NonNull
//            @Override
//            public ContractorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.properties_layout,parent,false);
//                return new ContractorViewHolder(view);
//            }
//        };
//
//        mrecyclerview = findViewById(R.id.recyclerView);
//        mrecyclerview.setHasFixedSize(true);
//        staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
//        mrecyclerview.setLayoutManager(staggeredGridLayoutManager);
//        mrecyclerview.setAdapter(contractorAdapter);
//    }
//
//    public class ContractorViewHolder extends RecyclerView.ViewHolder
//    {
//        //same as propertyLayout
//        private TextView contractorField;
//        private TextView contractorName;
//        private TextView contractorArea;
//        private TextView contractorNumber;
//        private TextView contractorRate;
//        LinearLayout mcontractor;
//
//        public ContractorViewHolder(@NonNull View itemView) {
//            super(itemView);
//            contractorField = itemView.findViewById(R.id.contractorField);
//            contractorName = itemView.findViewById(R.id.contractorName);
//            contractorArea = itemView.findViewById(R.id.contractorArea);
//            contractorNumber = itemView.findViewById(R.id.contractorNumber);
//            contractorRate = itemView.findViewById(R.id.contractorRate);
//            mcontractor = itemView.findViewById(R.id.contractor);
//        }
//    }
//
//    @Override
//    protected void onStart() {
//        super.onStart();
//        contractorAdapter.startListening();
//    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//        if(contractorAdapter!=null)
//        {
//            contractorAdapter.stopListening();
//        }
//    }
//}