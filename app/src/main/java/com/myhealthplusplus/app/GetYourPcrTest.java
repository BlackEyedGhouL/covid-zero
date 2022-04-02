package com.myhealthplusplus.app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

public class GetYourPcrTest extends AppCompatActivity {

    TextInputLayout firstName, lastName, nic, postalCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_your_pcr_test);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        setTitle(Html.fromHtml("<font color=\"white\">" + "Vaccinations" + "</font>"));
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.dark_black)));

        firstName = findViewById(R.id.get_your_pcr_test_reg_firstName_txtLayout);
        lastName = findViewById(R.id.get_your_pcr_test_reg_LastName_txtLayout);
        nic = findViewById(R.id.get_your_pcr_test_reg_NIC_txtLayout);
        postalCode = findViewById(R.id.get_your_pcr_test_reg_PostalCode_txtLayout);

        Button locations = findViewById(R.id.get_your_pcr_test_locations);
        locations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GetYourPcrTest.this, AllowPermissionsLocation.class);
                startActivity(intent);
            }
        });

        Button next = findViewById(R.id.get_your_pcr_test_reg_Next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!validateFirstName() | !validateLastName() | !validatePostalCode() | !validateNIC()) {
                    return;
                }
                Intent intent = new Intent(GetYourPcrTest.this, GetVaccined_p2.class);
                startActivity(intent);
            }

            private boolean validateNIC() {
                String val = nic.getEditText().getText().toString();
                String checkForLetters = "[0-9vV]+";

                if (val.isEmpty()) {
                    nic.setError("Field can not be empty");
                    return false;
                }
                else if (!val.matches(checkForLetters)) {
                    nic.setError("Invalid NIC!");
                    return false;
                }
                else if (val.length() < 10 || val.length() == 11) {
                    nic.setError("NIC should contain 10 or 12 characters!");
                    return false;
                }
                else {
                    nic.setError(null);
                    nic.setErrorEnabled(false);
                    return true;
                }
            }

            private boolean validatePostalCode() {
                String val = postalCode.getEditText().getText().toString();
                String checkForLetters = "[0-9]+";

                if (val.isEmpty()) {
                    postalCode.setError("Field can not be empty");
                    return false;
                }
                else if (!val.matches(checkForLetters)) {
                    postalCode.setError("Invalid postal code!");
                    return false;
                }
                else if (val.length() < 5) {
                    postalCode.setError("Postal code should contain 5 characters!");
                    return false;
                }
                else {
                    postalCode.setError(null);
                    postalCode.setErrorEnabled(false);
                    return true;
                }
            }

            private boolean validateLastName() {
                String val = lastName.getEditText().getText().toString();
                String checkForLetters = "[a-zA-Z]+";
                String noWhiteSpace = "\\A\\w{1,20}\\z";

                if (val.isEmpty()) {
                    lastName.setError("Field can not be empty");
                    return false;
                }
                else if (!val.matches(noWhiteSpace)) {
                    lastName.setError("No white spaces are allowed!");
                    return false;
                }
                else if (!val.matches(checkForLetters)) {
                    lastName.setError("Invalid last name!");
                    return false;
                }
                else {
                    lastName.setError(null);
                    lastName.setErrorEnabled(false);
                    return true;
                }
            }

            private boolean validateFirstName() {
                String val = firstName.getEditText().getText().toString();
                String checkForLetters = "[a-zA-Z]+";
                String noWhiteSpace = "\\A\\w{1,20}\\z";

                if (val.isEmpty()) {
                    firstName.setError("Field can not be empty");
                    return false;
                }
                else if (!val.matches(noWhiteSpace)) {
                    firstName.setError("No white spaces are allowed!");
                    return false;
                }
                else if (!val.matches(checkForLetters)) {
                    firstName.setError("Invalid first name!");
                    return false;
                }
                else {
                    firstName.setError(null);
                    firstName.setErrorEnabled(false);
                    return true;
                }
            }
        });
    }
}