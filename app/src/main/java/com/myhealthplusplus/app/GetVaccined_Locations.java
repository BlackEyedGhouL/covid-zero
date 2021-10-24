package com.myhealthplusplus.app;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.media.audiofx.Equalizer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Html;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.Objects;

public class GetVaccined_Locations extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    GoogleMap mgooglemap;
    private FusedLocationProviderClient mLocationClient;
    private int GPS_REQUEST_CODE = 9001;
    LatLngBounds srilanka_boundry;

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

    private void gotoLocation(double latitude, double longitude) {
        LatLng latLng = new LatLng(latitude, longitude);

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 7);
        mgooglemap.moveCamera(cameraUpdate);
        mgooglemap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
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