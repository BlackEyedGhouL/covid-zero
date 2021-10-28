package com.myhealthplusplus.app;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.audiofx.Equalizer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class GetVaccined_Locations extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    GoogleMap mgooglemap;
    private FusedLocationProviderClient mLocationClient;
    private int GPS_REQUEST_CODE = 9001;
    LatLngBounds srilanka_boundry;

    private MainActivity activity = new MainActivity();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_vaccined_locations);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        setTitle(Html.fromHtml("<font color=\"white\">" + "Vaccination Centers" + "</font>"));

        double bottomBoundry = 5.707286989912513;
        double leftBoundry = 79.49138290060732;
        double topBoundry = 10.037499991480718;
        double rightBoundry = 82.06118060471636;

        srilanka_boundry = new LatLngBounds(
                new LatLng(bottomBoundry, leftBoundry),
                new LatLng(topBoundry, rightBoundry)
        );

        initMap();

        mLocationClient = new FusedLocationProviderClient(this);
    }

    private void initMap() {
            if(isGPSenable()) {
                SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                supportMapFragment.getMapAsync(this);
            }
    }

    private boolean isGPSenable(){
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        boolean providerEnable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if(providerEnable){
            return true;
        }
        else{
            AlertDialog alertDialog = new AlertDialog.Builder(this ,
                    R.style.AlertDialog)
                    .setTitle("GPS Required")
                    .setMessage("Enable GPS otherwise location tracking won't work.")
                    .setPositiveButton("Settings", ((dialogInterface, i) -> {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(intent, GPS_REQUEST_CODE);
                    }))
                    .setNegativeButton("Cancel", ((dialogInterface, i) -> {
                        Intent intent = new Intent(this, GetVaccined.class);
                        startActivity(intent);
                    }))
                    .setCancelable(false)
                    .show();
        }
        return false;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

        mgooglemap = googleMap;
        mgooglemap.setMyLocationEnabled(true);
        mgooglemap.setLatLngBoundsForCameraTarget(srilanka_boundry);
        mgooglemap.setMinZoomPreference(7.7f);
        LatLng latLng = new LatLng(7.618352550486964, 80.67400124702566);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 7);
        mgooglemap.moveCamera(cameraUpdate);

        activity.ShowDialog(this);

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference usersRef = rootRef
                .child("vaccineCenters");

        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot ds : dataSnapshot.getChildren()) {

                    String center_Display = ds
                            .child("center")
                            .getValue().toString();

                    String district_Display = ds
                            .child("district")
                            .getValue().toString();

                    String latitude_Display = ds
                            .child("latitude")
                            .getValue().toString();

                    String longitude_Display = ds
                            .child("longitude")
                            .getValue().toString();

                    String policeArea_Display = ds
                            .child("policeArea")
                            .getValue().toString();

                    double latitude = Double.parseDouble(latitude_Display);
                    double longitude = Double.parseDouble(longitude_Display);

                    // map.clear();

                    googleMap.addMarker(new MarkerOptions()
                            .position(new LatLng(latitude, longitude))
                            .title(center_Display + ", " + policeArea_Display + ", " + district_Display));

                    Log.d("New Marker Added: ", center_Display);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };

        usersRef.addListenerForSingleValueEvent(eventListener);

        activity.DismissDialog();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode== GPS_REQUEST_CODE){
            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

            boolean providerEnable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            if(providerEnable){
                finish();
                startActivity(getIntent());
            }
            else {
                Intent intent = new Intent(this, GetVaccined.class);
                startActivity(intent);
            }
        }
    }

}