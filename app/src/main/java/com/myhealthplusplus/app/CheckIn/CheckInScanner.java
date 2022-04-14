package com.myhealthplusplus.app.CheckIn;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.myhealthplusplus.app.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import eu.livotov.labs.android.camview.ScannerLiveView;
import eu.livotov.labs.android.camview.scanner.decoder.zxing.ZXDecoder;

public class CheckInScanner extends AppCompatActivity {

    ScannerLiveView scannerLiveView;
    ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_in_scanner);
        getWindow().setStatusBarColor(ContextCompat.getColor(CheckInScanner.this, R.color.dark_black));

        init();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        scannerLiveView.setScannerViewEventListener(new ScannerLiveView.ScannerViewEventListener() {
            @Override
            public void onScannerStarted(ScannerLiveView scanner) { }

            @Override
            public void onScannerStopped(ScannerLiveView scanner) { }

            @Override
            public void onScannerError(Throwable err) {
                Toast.makeText(CheckInScanner.this, "Scanning error ...", Toast.LENGTH_SHORT).show();
                onBackPressed();
            }

            @Override
            public void onCodeScanned(String data) {

                scannerLiveView.stopScanner();

                @SuppressLint("SimpleDateFormat")
                DateFormat df = new SimpleDateFormat("d MMM yyyy 'at' h:mm a");
                String time = df.format(Calendar.getInstance().getTime());

                Intent intent = new Intent(CheckInScanner.this, CheckInAddPeople.class);
                intent.putExtra("CODE", data);
                intent.putExtra("TIME", time);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onPause() {
        scannerLiveView.stopScanner();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        ZXDecoder decoder = new ZXDecoder();
        decoder.setScanAreaPercent(0.8);
        scannerLiveView.setDecoder(decoder);
        scannerLiveView.startScanner();
    }

    private void init() {
        back = findViewById(R.id.check_in_scanner_back);
        scannerLiveView = findViewById(R.id.check_in_scanner_cam_view);
    }
}