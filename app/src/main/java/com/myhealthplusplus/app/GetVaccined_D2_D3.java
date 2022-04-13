package com.myhealthplusplus.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class GetVaccined_D2_D3 extends AppCompatActivity {

    ImageView back;
    TextView firstName, lastName, postalCode, gender, indigenous, dateOfBirth, nic, phoneNumber;
    RadioGroup dose;
    Button next;
    String firstNameT, lastNameT, postalCodeT, genderT, indigenousT, dateOfBirthT, nicT, phoneNumberT;
    DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_vaccined_d2_d3);
        getWindow().setStatusBarColor(ContextCompat.getColor(GetVaccined_D2_D3.this, R.color.dark_black));

        init();

        user = FirebaseAuth.getInstance().getCurrentUser();

        getData(getIntent().getStringExtra("NIC"));

        if (getIntent().getStringExtra("DOSE").equals("2")) {
            dose.getChildAt(1).setEnabled(false);
            dose.check(dose.getChildAt(0).getId());
        } else {
            dose.getChildAt(0).setEnabled(false);
            dose.check(dose.getChildAt(1).getId());
        }

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!validateDose()) {
                    return;
                }

                RadioButton doseRadioButton = findViewById(dose.getCheckedRadioButtonId());
                String doseNumber = doseRadioButton.getText().toString();

                if (doseNumber.equals("Dose 2")) {
                    doseNumber = "- DOSE 02 -";
                } else if (doseNumber.equals("Dose 3")) {
                    doseNumber = "- DOSE 03 -";
                }

                Intent intent = new Intent(GetVaccined_D2_D3.this, GetVaccined_Verify.class);
                intent.putExtra("FIRST_NAME", firstNameT);
                intent.putExtra("LAST_NAME", lastNameT);
                intent.putExtra("POSTAL_CODE", postalCodeT);
                intent.putExtra("NIC", nicT);
                intent.putExtra("PHONE", phoneNumberT);
                intent.putExtra("GENDER", genderT);
                intent.putExtra("DOB", dateOfBirthT);
                intent.putExtra("INDIGENOUS", indigenousT);
                intent.putExtra("DOSE", doseNumber);
                startActivity(intent);
            }
        });
    }

    private void getData(String n) {
        rootRef.child("vaccinationTokens").child(user.getUid()).child(n).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot ds) {

                dateOfBirthT = ds
                        .child("dateOfBirthT")
                        .getValue().toString();

                firstNameT = ds
                        .child("firstNameT")
                        .getValue().toString();

                genderT = ds
                        .child("genderT")
                        .getValue().toString();

                indigenousT = ds
                        .child("indigenousT")
                        .getValue().toString();

                lastNameT = ds
                        .child("lastNameT")
                        .getValue().toString();

                phoneNumberT = ds
                        .child("phoneNumberT")
                        .getValue().toString();

                postalCodeT = ds
                        .child("postalCodeT")
                        .getValue().toString();

               nicT = ds
                        .child("nicT")
                        .getValue().toString();

                firstName.setText(firstNameT);
                lastName.setText(lastNameT);
                postalCode.setText(postalCodeT);
                nic.setText(nicT);
                phoneNumber.setText(phoneNumberT);
                gender.setText(genderT);
                dateOfBirth.setText(dateOfBirthT);
                indigenous.setText(indigenousT);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void init() {
        back = findViewById(R.id.gv23_back);
        next = findViewById(R.id.get_vaccined_reg_23_Next2);
        dose = findViewById(R.id.get_vaccined_reg_p2_dose_radio_group);
        firstName = findViewById(R.id.get_vaccined_reg_23_first_name);
        lastName = findViewById(R.id.get_vaccined_reg_23_last_name);
        postalCode = findViewById(R.id.get_vaccined_reg_23_postal_code);
        gender = findViewById(R.id.get_vaccined_reg_23_gender);
        indigenous = findViewById(R.id.get_vaccined_reg_23_indigenous);
        dateOfBirth = findViewById(R.id.get_vaccined_reg_23_dob);
        nic = findViewById(R.id.get_vaccined_reg_23_nic);
        phoneNumber = findViewById(R.id.get_vaccined_reg_23_phone_number);
    }

    private boolean validateDose() {
        if (dose.getCheckedRadioButtonId() == -1) {
            Toast.makeText(GetVaccined_D2_D3.this, "Please select the dose", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }
}