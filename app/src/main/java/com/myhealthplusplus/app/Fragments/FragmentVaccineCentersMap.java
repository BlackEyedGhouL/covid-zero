package com.myhealthplusplus.app.Fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.github.captain_miao.optroundcardview.OptRoundCardView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
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
import com.myhealthplusplus.app.Adapters.CustomInfoWindowAdapter;
import com.myhealthplusplus.app.GetVaccined;
import com.myhealthplusplus.app.Models.VaccineCenter;
import com.myhealthplusplus.app.R;
import com.myhealthplusplus.app.SQLite.DatabaseAdapter;
import com.myhealthplusplus.app.SQLite.PreCreateDB;

import java.util.ArrayList;
import java.util.List;

public class FragmentVaccineCentersMap extends Fragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    View rootView, fragmentMap;
    Context mContext;
    GoogleMap mMap;
    FloatingActionButton myLocation, drop;
    TextView allow, notNow, title, subTitle;
    OptRoundCardView card;
    ConstraintLayout layout;
    Animation open, close;
    private final int GPS_REQUEST_CODE = 9001;
    List<VaccineCenter> vaccineCenterList = new ArrayList<>();
    DatabaseAdapter databaseAdapter;
    private FusedLocationProviderClient mLocationClient;
    Boolean isAllFabsVisible;
    LatLngBounds sriLanka_boundary;
    DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();

    public FragmentVaccineCentersMap() {
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @SuppressLint({"ClickableViewAccessibility", "VisibleForTests"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.activity_fragment_vaccine_centers_map, container, false);

        double bottom_boundary = 5.774112558779486;
        double left_boundary = 79.51377588147243;
        double top_boundary = 10.090428766513725;
        double right_boundary = 82.06510282696583;

        sriLanka_boundary = new LatLngBounds(
                new LatLng(bottom_boundary, left_boundary),
                new LatLng(top_boundary, right_boundary)
        );

        init();

        fragmentMap.setVisibility(View.GONE);
        drop.setVisibility(View.GONE);
        myLocation.setVisibility(View.GONE);

        drop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isAllFabsVisible) {
                    myLocation.setVisibility(View.VISIBLE);
                    myLocation.setClickable(true);
                    drop.startAnimation(open);
                    isAllFabsVisible = true;
                } else {
                    myLocation.setVisibility(View.GONE);
                    myLocation.setClickable(false);
                    drop.startAnimation(close);
                    isAllFabsVisible = false;
                }
            }
        });

        myLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myLocation.setVisibility(View.GONE);
                myLocation.setClickable(false);
                drop.startAnimation(close);
                isAllFabsVisible = false;
                AnimateToDeviceLocation();
            }
        });

        myLocation.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(mContext, "My Location", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        allow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkMyPermission();
            }
        });

        notNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, GetVaccined.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        checkMyPermission();

        PreCreateDB.copyDB(mContext);
        databaseAdapter = new DatabaseAdapter(mContext);
        vaccineCenterList = databaseAdapter.getAllCenters();

        return rootView;
    }

    private void setLastUpdated() {
        DatabaseReference lastUpdateRef = rootRef
                .child("lastUpdated");

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String lastUpdated = snapshot
                        .child("vaccineCenters")
                        .getValue().toString();

                Log.d("Last Updated: ", lastUpdated);

                Snackbar.make(layout, "Last Updated: " + lastUpdated, BaseTransientBottomBar.LENGTH_INDEFINITE)
                        .setAction("Hide", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                            }
                        })
                        .setActionTextColor(getResources().getColor(R.color.red_pie))
                        .setBackgroundTint(getResources().getColor(R.color.light_black))
                        .setTextColor(getResources().getColor(R.color.white))
                        .show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        lastUpdateRef.addListenerForSingleValueEvent(valueEventListener);
    }

    private void getMarkerData() {
        mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(mContext));

        for (int i = 0; i < vaccineCenterList.size(); i++) {

            String center = vaccineCenterList.get(i).getCenter();
            String district = vaccineCenterList.get(i).getDistrict();
            String police_area = vaccineCenterList.get(i).getPolice_area();
            double lat = Double.parseDouble(vaccineCenterList.get(i).getLat());
            double lon = Double.parseDouble(vaccineCenterList.get(i).getLon());

            LatLng latLng = new LatLng(lat, lon);
            customMarker(latLng, center, district, police_area);
        }
    }

    private void customMarker(LatLng latLng, String center, String district, String police_area) {
        MarkerOptions options = new MarkerOptions()
                .position(latLng)
                .title(center)
                .snippet("District: " + district + "\nPolice Area: " + police_area)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.vaccine_center_marker));

        mMap.addMarker(options);
    }

    private void AnimateToDeviceLocation() {
        mLocationClient = LocationServices.getFusedLocationProviderClient(mContext);

        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        final Task location = mLocationClient.getLastLocation();
        location.addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()) {
                    Location currentLocation = (Location) task.getResult();
                    LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f));
                } else {
                    Toast.makeText(mContext, "Unable to get current location.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.getUiSettings().setCompassEnabled(false);
        mMap.setLatLngBoundsForCameraTarget(sriLanka_boundary);
        mMap.setMinZoomPreference(7.7f);
        LatLng latLng = new LatLng(6.898410441777559, 79.87451160758005);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 12);
        mMap.moveCamera(cameraUpdate);

        getMarkerData();
        setLastUpdated();
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

    private void init() {
        fragmentMap = rootView.findViewById(R.id.vcm_map);
        myLocation = rootView.findViewById(R.id.vcm_floatingActionButton_my_location);
        drop = rootView.findViewById(R.id.vcm_floatingActionButton_drop);
        open = AnimationUtils.loadAnimation(mContext, R.anim.rotate_open_anim);
        close = AnimationUtils.loadAnimation(mContext, R.anim.rotate_close_anim);
        allow = rootView.findViewById(R.id.vcm_allow_access);
        notNow = rootView.findViewById(R.id.vcm_allow_access_notNow);
        card = rootView.findViewById(R.id.vcm_card);
        title = rootView.findViewById(R.id.vcm_title);
        subTitle = rootView.findViewById(R.id.vcm_subTitle);
        layout = rootView.findViewById(R.id.vcm_constraint_layout);
    }

    private void checkMyPermission() {
        Dexter.withContext(mContext).withPermission(Manifest.permission.ACCESS_FINE_LOCATION).withListener(new PermissionListener() {
            @SuppressLint("VisibleForTests")
            @Override
            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                card.setVisibility(View.GONE);
                title.setVisibility(View.GONE);
                subTitle.setVisibility(View.GONE);
                allow.setVisibility(View.GONE);
                notNow.setVisibility(View.GONE);
                layout.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                fragmentMap.setVisibility(View.VISIBLE);
                drop.setVisibility(View.VISIBLE);
                isAllFabsVisible = false;
                initMap();
                mLocationClient = new FusedLocationProviderClient(mContext);
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", mContext.getPackageName(), "");
                intent.setData(uri);
                startActivity(intent);
            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest();
            }
        }).check();
    }

    private void initMap() {
        if (isGPSEnable()) {
            SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                    .findFragmentById(R.id.vcm_map);

            assert mapFragment != null;
            mapFragment.getMapAsync(FragmentVaccineCentersMap.this);
    }}

    private boolean isGPSEnable() {
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(mContext.LOCATION_SERVICE);

        boolean providerEnable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (providerEnable) {
            return true;
        } else {
            AlertDialog alertDialog = new AlertDialog.Builder(mContext,
                    R.style.AlertDialog)
                    .setTitle("GPS Required")
                    .setMessage("Enable GPS otherwise location tracking won't work.")
                    .setPositiveButton("Settings", ((dialogInterface, i) -> {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(intent, GPS_REQUEST_CODE);
                    }))
                    .setNegativeButton("Cancel", ((dialogInterface, i) -> {
                        Intent intent = new Intent(mContext, GetVaccined.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }))
                    .setCancelable(false)
                    .show();
        }
        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GPS_REQUEST_CODE) {
            LocationManager locationManager = (LocationManager) getActivity().getSystemService(mContext.LOCATION_SERVICE);

            boolean providerEnable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            if (providerEnable) {
                Fragment frag = new FragmentVaccineCentersMap();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.vcm_constraint_layout, frag).commit();
            } else {
                isGPSEnable();
            }
        }
    }
}