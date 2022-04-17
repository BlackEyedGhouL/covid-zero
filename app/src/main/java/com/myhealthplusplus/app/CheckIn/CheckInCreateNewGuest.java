package com.myhealthplusplus.app.CheckIn;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.myhealthplusplus.app.R;

public class CheckInCreateNewGuest extends AppCompatActivity {

    ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_in_create_new_guest);
        getWindow().setStatusBarColor(ContextCompat.getColor(CheckInCreateNewGuest.this, R.color.dark_black));

        init();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void init() {
        back = findViewById(R.id.check_in_create_guest_back);
    }
}