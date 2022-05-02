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

import com.example.propertyproject.chart.ContractorChartActivity;
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
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
//"<b>" + "<font color = 'red'>" + "Address : "  + "</font>" + "</b>"
public class PropertyActivity extends AppCompatActivity {
    private NavigationView navigationView;
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    FloatingActionButton mcreatepropertiesfab;
    private FirebaseAuth firebaseAuth;
    RecyclerView mrecyclerview;
    StaggeredGridLayoutManager staggeredGridLayoutManager;

    FirebaseUser firebaseUser;
    FirebaseFirestore firebaseFirestore;
    private FusedLocationProviderClient fusedLocationProviderClient;
    FirestoreRecyclerAdapter<firebasemodel, PropertyViewHolder> propertyAdapter;
    private com.google.android.gms.location.LocationRequest locationRequest;
    private LocationCallback locationCallback;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_property);
        navigationView = findViewById(R.id.nav_view);
        drawerLayout  = findViewById(R.id.drawerLayout);
        toolbar  = findViewById(R.id.toolbar);
        mcreatepropertiesfab = findViewById(R.id.createpropertiesfab);
        firebaseAuth = FirebaseAuth.getInstance();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        setSupportActionBar(toolbar);

        grantPermissions();

        mcreatepropertiesfab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(PropertyActivity.this, createProperty.class));
            }
        });

        Query query = firebaseFirestore
                .collection("properties")
                .document(firebaseUser.getUid())
                .collection("myProperties")
                .orderBy("eircode", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<firebasemodel> allproperties = new FirestoreRecyclerOptions.Builder<firebasemodel>().setQuery(query, firebasemodel.class).build();

        propertyAdapter=new FirestoreRecyclerAdapter<firebasemodel, PropertyViewHolder>(allproperties) {
            @Override
            protected void onBindViewHolder(@NonNull PropertyViewHolder propertyViewHolder, int i, @NonNull firebasemodel firebasemodel) {
                propertyViewHolder.propertyeircode.setText(Html.fromHtml("<b>" + "Eircode " + "</b>" + firebasemodel.getEircode()));
                propertyViewHolder.propertyaddress.setText(Html.fromHtml("<b>" + "Address " + "</b>" + firebasemodel.getAddress()));
                propertyViewHolder.propertybeds.setText(Html.fromHtml("<b>" + "Beds " + "</b>" +firebasemodel.getBeds()));
                propertyViewHolder.propertyrent.setText(Html.fromHtml("<b>" + "Rent " + "</b>" +firebasemodel.getRent()));
                propertyViewHolder.propertytenantname.setText(Html.fromHtml("<b>" + "Tenant " + "</b>" +firebasemodel.getTenantName()));
                propertyViewHolder.propertytenantemail.setText(Html.fromHtml("<b>" + "Email " + "</b>" +firebasemodel.getTenantEmail()));
                propertyViewHolder.propertytenantphone.setText(Html.fromHtml("<b>" + "Phone " + "</b>" +firebasemodel.getTenantEmail()));
                propertyViewHolder.propertybaths.setText(Html.fromHtml("<b>" + "Address " + "</b>" +firebasemodel.getBaths()));
                Picasso.get().load(firebasemodel.propertyImage).into(propertyViewHolder.imageView);
                propertyViewHolder.menu.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        PopupMenu popupMenu = new PopupMenu(propertyViewHolder.itemView.getContext(),propertyViewHolder.menu);
                        popupMenu.inflate(R.menu.adapter_menu);
                        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem menuItem) {
                                switch (menuItem.getItemId()){
                                    case R.id.delete:
                                        deleteProperty(firebasemodel.getPropertyID());
                                        return true;
                                    case R.id.edit:
                                        Intent intent = new Intent(propertyViewHolder.itemView.getContext(),editProperty.class);
                                        intent.putExtra("propertyID",firebasemodel.getPropertyID());
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
            public PropertyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.properties_layout,parent, false);
                return new PropertyViewHolder(view);
            }
        };

        mrecyclerview=findViewById(R.id.recyclerview);
        mrecyclerview.setHasFixedSize(true);
        staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        mrecyclerview.setLayoutManager(staggeredGridLayoutManager);
        mrecyclerview.setAdapter(propertyAdapter);

        createDrawer();

    }
    private void deleteProperty(String propertyID) {
        firebaseFirestore
                .collection("properties")
                .document(firebaseUser.getUid())
                .collection("myProperties")
                .document(propertyID)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(PropertyActivity.this,"Property Successfully Deleted",
                                Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    public class PropertyViewHolder extends RecyclerView.ViewHolder {
        private ImageView menu;
        private CircleImageView imageView;
        private TextView propertyeircode;
        private TextView propertyaddress;
        private TextView propertybeds;
        private TextView propertybaths;
        private TextView propertyrent;
        private TextView propertytenantname;
        private TextView propertytenantemail;
        private TextView propertytenantphone;
        LinearLayout mproperty;

        public PropertyViewHolder(@NonNull View itemView) {
            super(itemView);
            menu = itemView.findViewById(R.id.menupopbutton);
            propertyeircode = itemView.findViewById(R.id.propertyeircode);
            propertyaddress = itemView.findViewById(R.id.propertyaddress);
            propertybeds = itemView.findViewById(R.id.propertybeds);
            propertybaths = itemView.findViewById(R.id.propertybaths);
            propertyrent = itemView.findViewById(R.id.propertyrent);
            propertytenantname = itemView.findViewById(R.id.propertytenantname);
            propertytenantemail = itemView.findViewById(R.id.propertytenantemail);
            propertytenantphone = itemView.findViewById(R.id.propertytenantphone);
            imageView = itemView.findViewById(R.id.propertyImage);
            mproperty = itemView.findViewById(R.id.property);

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
                        startActivity(new Intent(PropertyActivity.this, WeatherActivity.class));
                    }
                    break;
                    case R.id.openMap : {
                        startActivity(new Intent(PropertyActivity.this, MapsActivity.class));
                    }
                    break;
                    case R.id.covidTracker : {
                        startActivity(new Intent(PropertyActivity.this, C19Tracker.class));
                    }
                    break;
                    case R.id.addContractor : {
                        startActivity(new Intent(PropertyActivity.this, createContractor.class));
                    }
                    break;
                    case R.id.chat : {
                        startActivity(new Intent(PropertyActivity.this, splashMain.class));
                    }
                    break;
                    case R.id.stats : {
                        startActivity(new Intent(PropertyActivity.this, ContractorChartActivity.class));
                    }
                    break;
                    case R.id.rentStats : {
                        startActivity(new Intent(PropertyActivity.this, RentChartActivity.class));
                    }
                    break;
                    case R.id.scheduleJob : {
                        startActivity(new Intent(PropertyActivity.this, JobSchedularActivity.class));
                    }
                    break;
                    case R.id.appointments:  {
                        startActivity(new Intent(PropertyActivity.this, AppointmentsActivity.class));
                    }
                    break;
                    case R.id.logout : {
                        firebaseAuth.signOut();
                        finish();
                        startActivity(new Intent(PropertyActivity.this, MainActivity.class));
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
        propertyAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (propertyAdapter != null) {
            propertyAdapter.startListening();
        }
    }

    private void grantPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED &&  ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
          getCurrentLocation();
        } else {
           if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION)
                   &&ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_COARSE_LOCATION)){
               Toast.makeText(PropertyActivity.this,"Permission Need to show location on map",
                       Toast.LENGTH_SHORT).show();
           } else {
               ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                       Manifest.permission.ACCESS_COARSE_LOCATION},1000);
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
                saveLatitudeLongitude(latitude,longitude);
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
                        if(location != null){
                            Double latitude = location.getLatitude();
                            Double longitude = location.getLongitude();
                            saveLatitudeLongitude(latitude,longitude);
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
                        resolvable.startResolutionForResult(PropertyActivity.this,
                                1000);
                    } catch (IntentSender.SendIntentException sendEx) {
                        // Ignore the error.
                    }
                }
            }
        });

        }

    private void saveLatitudeLongitude(Double latitude, Double Longitude){
        SharedPreferences sharedPreferences = getSharedPreferences("prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong("latitude", latitude.longValue());
        editor.putLong("longitude", Longitude.longValue());
        editor.apply();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
         if(requestCode == 1000){
             if(grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED){
                 getCurrentLocation();
             } else {
                 ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                         Manifest.permission.ACCESS_COARSE_LOCATION},1000);
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
