package com.myhealthplusplus.app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

import java.util.Calendar;
import java.util.Objects;

public class GetVaccined_p2 extends AppCompatActivity {

    RadioGroup gender_radioGroup, indigenous_radiogroup;
    DatePicker datePicker;
    TextInputLayout phoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_vaccined_p2);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        setTitle(Html.fromHtml("<font color=\"white\">" + "Vaccinations" + "</font>"));
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.dark_black)));

        gender_radioGroup = findViewById(R.id.get_vaccined_reg_p2_gender_radio_group);
        indigenous_radiogroup = findViewById(R.id.get_vaccined_reg_p2_Indigenous_radio_group);
        datePicker = findViewById(R.id.age_picker);
        phoneNumber = findViewById(R.id.get_vaccined_reg_p2_PN_txtLayout);

        Button next = findViewById(R.id.get_vaccined_reg_p2_Next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!validatePN() | !validateGender() | !validateAge() | !validateIndigenous()) {
                    return;
                }
                Intent intent = new Intent(GetVaccined_p2.this, GetVaccined_Verify.class);
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