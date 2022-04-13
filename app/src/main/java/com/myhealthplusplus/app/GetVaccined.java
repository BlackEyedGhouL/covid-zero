package com.myhealthplusplus.app;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.myhealthplusplus.app.Adapters.VaccinationTokenAdapter;
import com.myhealthplusplus.app.Models.VaccinationToken;

import java.util.ArrayList;

public class GetVaccined extends AppCompatActivity {

    TextInputLayout firstName, lastName, nic, postalCode, nic2;
    ImageView back;
    SwipeRefreshLayout swipeRefreshLayout;
    ArrayList<VaccinationToken> vaccinationTokenArrayList = new ArrayList<>();
    RecyclerView recyclerView;
    FirebaseUser user;
    DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
    private final MainActivity activity = new MainActivity();
    VaccinationTokenAdapter vaccinationTokenAdapter;
    boolean doesNotExistsAlready = false;
    boolean notTakenD1 = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_vaccined);

        getWindow().setStatusBarColor(ContextCompat.getColor(GetVaccined.this, R.color.dark_black));

        back = findViewById(R.id.gv_back);
        recyclerView = findViewById(R.id.get_vaccined_vaccination_tokens_recycler_view);
        firstName = findViewById(R.id.get_vaccined_reg_firstName_txtLayout);
        lastName = findViewById(R.id.get_vaccined_reg_LastName_txtLayout);
        nic = findViewById(R.id.get_vaccined_reg_NIC_txtLayout);
        nic2 = findViewById(R.id.get_vaccined_reg_23_NIC_txtLayout);
        postalCode = findViewById(R.id.get_vaccined_reg_PostalCode_txtLayout);
        swipeRefreshLayout = findViewById(R.id.gv_swipe_ref);

        user = FirebaseAuth.getInstance().getCurrentUser();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        getData();

        int refreshCycleColor = Color.parseColor("#c01722");
        swipeRefreshLayout.setColorSchemeColors(refreshCycleColor);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onRefresh() {
                vaccinationTokenArrayList.clear();
                vaccinationTokenAdapter.notifyDataSetChanged();
                getData();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        Button next2 = findViewById(R.id.get_vaccined_reg_23_Next);
        next2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!validateNIC2() | !notTakenD1) {
                    return;
                }

                checkIfD2orD3(nic2.getEditText().getText().toString());
                nic2.getEditText().setText(null);
            }
        });

        Button locations = findViewById(R.id.get_vaccined_locations);
        locations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GetVaccined.this, VaccineCenters.class);
                startActivity(intent);
            }
        });

        Button next = findViewById(R.id.get_vaccined_reg_Next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!validateFirstName() | !validateLastName() | !validatePostalCode() | !validateNIC() | !doesNotExistsAlready) {
                    return;
                }
                Intent intent = new Intent(GetVaccined.this, GetVaccined_p2.class);
                intent.putExtra("FIRST_NAME", firstName.getEditText().getText().toString());
                intent.putExtra("LAST_NAME", lastName.getEditText().getText().toString());
                intent.putExtra("POSTAL_CODE", postalCode.getEditText().getText().toString());
                intent.putExtra("NIC", nic.getEditText().getText().toString());
                startActivity(intent);

                firstName.getEditText().setText(null);
                lastName.getEditText().setText(null);
                postalCode.getEditText().setText(null);
                nic.getEditText().setText(null);
            }
        });
    }

    public void checkIfIdHasTokensPreviously (String nic) {
        DatabaseReference eventRef = rootRef
                .child("vaccinationTokens").child(user.getUid()).child(nic);
        eventRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() == null) {
                    doesNotExistsAlready = true;
                } else {
                    doesNotExistsAlready = false;
                    Toast.makeText(GetVaccined.this, "Provided NIC has taken the dose 01 previously", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void checkIfHasTakeD1 (String nic) {
        DatabaseReference eventRef = rootRef
                .child("vaccinationTokens").child(user.getUid()).child(nic);
        eventRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    notTakenD1 = true;
                } else {
                    notTakenD1 = false;
                    Toast.makeText(GetVaccined.this, "Provided NIC hasn't taken the dose 01", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void checkIfD2orD3 (String nic) {
        rootRef.child("vaccinationTokens").child(user.getUid()).child(nic).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                boolean dose2 = Boolean.parseBoolean(snapshot
                        .child("dose2")
                        .getValue().toString());

                boolean dose3 = Boolean.parseBoolean(snapshot
                        .child("dose3")
                        .getValue().toString());

                if (dose2 && dose3) {
                    Toast.makeText(GetVaccined.this, "You're already fully vaccinated", Toast.LENGTH_SHORT).show();
                } else if (!dose2 && !dose3) {
                    Intent intent = new Intent(GetVaccined.this, GetVaccined_D2_D3.class);
                    intent.putExtra("NIC", nic);
                    intent.putExtra("DOSE", "2");
                    startActivity(intent);
                } else if (!dose3) {
                    Intent intent = new Intent(GetVaccined.this, GetVaccined_D2_D3.class);
                    intent.putExtra("DOSE", "3");
                    intent.putExtra("NIC", nic);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void getData() {
        activity.ShowDialog(this);

        DatabaseReference eventRef = rootRef
                .child("vaccinationTokens").child(user.getUid());

        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {

                    String dob = ds
                            .child("dateOfBirthT")
                            .getValue().toString();

                    String firstName = ds
                            .child("firstNameT")
                            .getValue().toString();

                    String gender = ds
                            .child("genderT")
                            .getValue().toString();

                    String indigenous = ds
                            .child("indigenousT")
                            .getValue().toString();

                    String lastName = ds
                            .child("lastNameT")
                            .getValue().toString();

                    String phone = ds
                            .child("phoneNumberT")
                            .getValue().toString();

                    boolean used = Boolean.parseBoolean(ds
                            .child("used")
                            .getValue().toString());

                    boolean dose1 = Boolean.parseBoolean(ds
                            .child("dose1")
                            .getValue().toString());

                    boolean dose2 = Boolean.parseBoolean(ds
                            .child("dose2")
                            .getValue().toString());

                    boolean dose3 = Boolean.parseBoolean(ds
                            .child("dose3")
                            .getValue().toString());

                    String validDate = ds
                            .child("validDateT")
                            .getValue().toString();

                    String postal = ds
                            .child("postalCodeT")
                            .getValue().toString();

                    String issuedDate = ds
                            .child("issuedDateT")
                            .getValue().toString();

                    String nic = ds
                            .child("nicT")
                            .getValue().toString();

                    String tokenImageUrl = ds
                            .child("tokenImageUrl")
                            .getValue().toString();

                    vaccinationTokenArrayList.add(new VaccinationToken(issuedDate, validDate, firstName, lastName, postal, nic, phone, gender, dob, indigenous, tokenImageUrl, used, dose1, dose2, dose3));
                }

                vaccinationTokenAdapter = new VaccinationTokenAdapter(vaccinationTokenArrayList, recyclerView, GetVaccined.this);
                recyclerView.setAdapter(vaccinationTokenAdapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        };
        eventRef.addListenerForSingleValueEvent(eventListener);
        activity.DismissDialog();
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
            checkIfIdHasTokensPreviously(val);
            return true;
        }
    }

    private boolean validateNIC2() {
        String val = nic2.getEditText().getText().toString();
        String checkForLetters = "[0-9vV]+";

        if (val.isEmpty()) {
            nic2.setError("Field can not be empty");
            return false;
        }
        else if (!val.matches(checkForLetters)) {
            nic2.setError("Invalid NIC!");
            return false;
        }
        else if (val.length() < 10 || val.length() == 11) {
            nic2.setError("NIC should contain 10 or 12 characters!");
            return false;
        }
        else {
            nic2.setError(null);
            nic2.setErrorEnabled(false);
            checkIfHasTakeD1(val);
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
}