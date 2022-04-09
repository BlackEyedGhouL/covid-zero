package com.myhealthplusplus.app;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.textfield.TextInputLayout;

import java.util.Calendar;

public class GetVaccined_p2 extends AppCompatActivity {

    RadioGroup gender_radioGroup, indigenous_radiogroup;
    DatePicker datePicker;
    TextInputLayout phoneNumber;
    ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_vaccined_p2);

        getWindow().setStatusBarColor(ContextCompat.getColor(GetVaccined_p2.this, R.color.dark_black));

        gender_radioGroup = findViewById(R.id.get_vaccined_reg_p2_gender_radio_group);
        indigenous_radiogroup = findViewById(R.id.get_vaccined_reg_p2_Indigenous_radio_group);
        datePicker = findViewById(R.id.get_vaccined_p2_age_picker);
        phoneNumber = findViewById(R.id.get_vaccined_reg_p2_PN_txtLayout);
        back = findViewById(R.id.get_vaccined_p2_back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        Button next = findViewById(R.id.get_vaccined_reg_p2_Next);
        next.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onClick(View view) {
                if (!validatePN() | !validateGender() | !validateAge() | !validateIndigenous()) {
                    return;
                }

                RadioButton genderRadioButton = findViewById(gender_radioGroup.getCheckedRadioButtonId());
                String gender = genderRadioButton.getText().toString();

                @SuppressLint("DefaultLocale") String strDate = String.format("%02d-%02d-%d", datePicker.getMonth() + 1, datePicker.getDayOfMonth(), datePicker.getYear());;

                RadioButton indigenousRadioButton = findViewById(indigenous_radiogroup.getCheckedRadioButtonId());
                String indigenous = indigenousRadioButton.getText().toString();

                Intent intent = new Intent(GetVaccined_p2.this, GetVaccined_Verify.class);
                intent.putExtra("FIRST_NAME", getIntent().getStringExtra("FIRST_NAME"));
                intent.putExtra("LAST_NAME", getIntent().getStringExtra("LAST_NAME"));
                intent.putExtra("POSTAL_CODE", getIntent().getStringExtra("POSTAL_CODE"));
                intent.putExtra("NIC", getIntent().getStringExtra("NIC"));
                intent.putExtra("PHONE", "+94"+phoneNumber.getEditText().getText().toString().trim());
                intent.putExtra("GENDER", gender);
                intent.putExtra("DOB", strDate);
                intent.putExtra("INDIGENOUS", indigenous);
                startActivity(intent);
            }

            private boolean validatePN() {
                String val = phoneNumber.getEditText().getText().toString();
                String checkForLetters = "[0-9]+";
                if (val.isEmpty()) {
                    phoneNumber.setError("Field can not be empty");
                    return false;
                }
                else if (!val.matches(checkForLetters)) {
                    phoneNumber.setError("Invalid phone number!");
                    return false;
                }
                else if (val.length() < 9) {
                    phoneNumber.setError("Phone number should contain 9 characters!");
                    return false;
                }
                else {
                    phoneNumber.setError(null);
                    phoneNumber.setErrorEnabled(false);
                    return true;
                }
            }

            private boolean validateIndigenous() {
                if (indigenous_radiogroup.getCheckedRadioButtonId() == -1) {
                    Toast.makeText(GetVaccined_p2.this, "Please select you're indigenous or not", Toast.LENGTH_SHORT).show();
                    return false;
                } else {
                    return true;
                }
            }

            private boolean validateAge() {
                int currentYear = Calendar.getInstance().get(Calendar.YEAR);
                int userAge = datePicker.getYear();
                int isAgeValid = currentYear - userAge;

                if (isAgeValid < 19) {
                    Toast.makeText(GetVaccined_p2.this, "You are not eligible to apply", Toast.LENGTH_SHORT).show();
                    return false;
                } else
                    return true;
            }

            private boolean validateGender() {
                if (gender_radioGroup.getCheckedRadioButtonId() == -1) {
                    Toast.makeText(GetVaccined_p2.this, "Please select gender", Toast.LENGTH_SHORT).show();
                    return false;
                } else {
                    return true;
                }
            }
        });
    }
}