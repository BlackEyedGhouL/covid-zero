package com.myhealthplusplus.app;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class VaccineTokenScan extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vaccine_token_scan);
        getWindow().setStatusBarColor(ContextCompat.getColor(VaccineTokenScan.this, R.color.dark_black));

        init();
    }

    private void init() {}
}