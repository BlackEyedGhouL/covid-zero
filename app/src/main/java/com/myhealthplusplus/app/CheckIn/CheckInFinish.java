package com.myhealthplusplus.app.CheckIn;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.myhealthplusplus.app.R;

public class CheckInFinish extends AppCompatActivity {

    TextView done, thanks, location;
    ImageView back;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_in_finish);
        getWindow().setStatusBarColor(ContextCompat.getColor(CheckInFinish.this, R.color.dark_black));

        init();

        thanks.setText("Thanks " + getIntent().getStringExtra("NAME") + ", you've checked in at");
        location.setText(getIntent().getStringExtra("LOCATION"));

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CheckInFinish.this, CheckInHome.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void init() {
        done = findViewById(R.id.check_in_finish_done);
        back = findViewById(R.id.check_in_finish_back);
        thanks = findViewById(R.id.check_in_finish_checking_into);
        location = findViewById(R.id.check_in_finish_location);
    }
}