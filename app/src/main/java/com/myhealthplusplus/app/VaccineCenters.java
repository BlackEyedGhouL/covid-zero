package com.myhealthplusplus.app;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.myhealthplusplus.app.Fragments.FragmentVaccineCentersList;

public class VaccineCenters extends AppCompatActivity {

    Button list, map;
    View list_view, map_view;
    ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vaccine_centers);
        getWindow().setStatusBarColor(ContextCompat.getColor(VaccineCenters.this, R.color.dark_black));

        init();

        if(savedInstanceState == null) {
            getSupportFragmentManager().
                    beginTransaction().replace(R.id.vc_frameLayout, new FragmentVaccineCentersList()).commit();
        }

        list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                map.setTextColor(getResources().getColor(R.color.gray));
                list.setTextColor(getResources().getColor(R.color.white));
                map_view.setBackgroundResource(R.color.dark_black);
                list_view.setBackgroundResource(R.color.white);

                replaceFragment(new FragmentVaccineCentersList());
            }
        });

        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                list.setTextColor(getResources().getColor(R.color.gray));
//                map.setTextColor(getResources().getColor(R.color.white));
//                list_view.setBackgroundResource(R.color.dark_black);
//                map_view.setBackgroundResource(R.color.white);
//
//                replaceFragment(new StationMapFragment());
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.vc_frameLayout, fragment);
        fragmentTransaction.commit();
    }

    public void init() {
        back = findViewById(R.id.vc_back);
        list = findViewById(R.id.vc_list);
        map = findViewById(R.id.vc_map);
        list_view = findViewById(R.id.vc_list_view);
        map_view = findViewById(R.id.vc_map_view);
    }
}