package com.myhealthplusplus.app;

import static java.security.AccessController.getContext;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.myhealthplusplus.app.Status.Data;

import java.util.Objects;

public class ViewProfile extends AppCompatActivity {

    Spinner spinner;
    CardView profile_color_ring;
    CardView editNameCard, editUsernameCard, editAddressCard, editGenderCard, editBirthOfDateCard, editEmailCard, editPhoneNumberCard;
    TextView editName, editUsername, editAddress, editGender, editBirthOfDate, editEmail, editPhoneNumber;
    TextInputLayout firstName, lastName, password;

    public static String FName, LName, PWord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        setTitle(Html.fromHtml("<font color=\"white\" >" + "Edit Profile" + "</font>"));

        Init();

        editNameCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(ViewProfile.this);
                LayoutInflater inflater = (LayoutInflater) ViewProfile.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View v = inflater.inflate(R.layout.edit_profile_name_window_p1, findViewById(R.id.edit_profile_name_window_p1_layout));
                TextView btnReview = v.findViewById(R.id.editProfileName_review_btn);
                TextView btnP1Cancel = v.findViewById(R.id.editProfileName_cancel_p1_btn);
                builder.setCancelable(false);
                builder.setView(v);

                final Dialog dialog = builder.create();
                dialog.getWindow().setWindowAnimations(R.style.DialogAnimation);

                btnReview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        firstName = (TextInputLayout) v.findViewById(R.id.editProfileName_firstName_txtLayout);
                        lastName = (TextInputLayout) v.findViewById(R.id.editProfileName_LastName_txtLayout);
                        password = (TextInputLayout) v.findViewById(R.id.editProfileName_password_txtLayout);
                        FName = firstName.getEditText().getText().toString();
                        LName = lastName.getEditText().getText().toString();

                        if (!validateFirstName() | !validateLastName()) {
                            return;
                        }

                        Toast.makeText(ViewProfile.this, "hello", Toast.LENGTH_SHORT).show();

                        AlertDialog.Builder builder1 = new AlertDialog.Builder(ViewProfile.this);
                        LayoutInflater inflater1 = (LayoutInflater) ViewProfile.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        View v1 = inflater1.inflate(R.layout.edit_profile_name_window_p2, findViewById(R.id.edit_profile_name_window_p2_layout));
                        TextView btnChange = v1.findViewById(R.id.editProfileName_save_btn);
                        TextView btnP2Cancel = v1.findViewById(R.id.editProfileName_cancel_p2_btn);
                        builder1.setCancelable(true);
                        builder1.setView(v1);

                        final Dialog dialog1 = builder1.create();
                        dialog1.getWindow().setWindowAnimations(R.style.DialogAnimation);

                        btnChange.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                PWord = password.getEditText().getText().toString();
                            }
                        });

                        btnP2Cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog1.dismiss();
                            }
                        });

                        dialog1.show();
                    }
                });

                btnP1Cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });

        editUsernameCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        editAddressCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        editGenderCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        editBirthOfDateCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        editEmailCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        editPhoneNumberCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        StatusAdapter adapter = new StatusAdapter(ViewProfile.this, Data.getStatusList());
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if (adapterView.getItemAtPosition(i).equals(0)) {
                    profile_color_ring.setCardBackgroundColor(Color.parseColor("#FFCA28"));

                    if (Build.VERSION.SDK_INT >= 28) {
                        profile_color_ring.setOutlineAmbientShadowColor(Color.parseColor("#FFCA28"));
                        profile_color_ring.setOutlineSpotShadowColor(Color.parseColor("#FFCA28"));
                    }
                } else if (adapterView.getItemAtPosition(i).equals(1)) {
                    profile_color_ring.setCardBackgroundColor(Color.parseColor("#4CAF50"));

                    if (Build.VERSION.SDK_INT >= 28) {
                        profile_color_ring.setOutlineAmbientShadowColor(Color.parseColor("#4CAF50"));
                        profile_color_ring.setOutlineSpotShadowColor(Color.parseColor("#4CAF50"));
                    }
                } else {
                    profile_color_ring.setCardBackgroundColor(Color.parseColor("#F44336"));

                    if (Build.VERSION.SDK_INT >= 28) {
                        profile_color_ring.setOutlineAmbientShadowColor(Color.parseColor("#F44336"));
                        profile_color_ring.setOutlineSpotShadowColor(Color.parseColor("#F44336"));
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    private boolean validateLastName() {
        String val = lastName.getEditText().getText().toString();
        String checkForLetters = "[a-zA-Z]+";
        String noWhiteSpace = "\\A\\w{1,20}\\z";

        if (val.isEmpty()) {
            lastName.setError("Field can not be empty");
            return false;
        } else if (!val.matches(noWhiteSpace)) {
            lastName.setError("No white spaces are allowed!");
            return false;
        } else if (!val.matches(checkForLetters)) {
            lastName.setError("Invalid last name!");
            return false;
        } else {
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
        } else if (!val.matches(noWhiteSpace)) {
            firstName.setError("No white spaces are allowed!");
            return false;
        } else if (!val.matches(checkForLetters)) {
            firstName.setError("Invalid first name!");
            return false;
        } else {
            firstName.setError(null);
            firstName.setErrorEnabled(false);
            return true;
        }
    }

    private void Init() {
        spinner = findViewById(R.id.spinner_status);
        profile_color_ring = findViewById(R.id.profile_color_ring);
        editNameCard = findViewById(R.id.profile_editName_card);
        editUsernameCard = findViewById(R.id.profile_editUsername_card);
        editEmailCard = findViewById(R.id.profile_editEmail_card);
        editAddressCard = findViewById(R.id.profile_editAddress_card);
        editPhoneNumberCard = findViewById(R.id.profile_editPhoneNumber_card);
        editBirthOfDateCard = findViewById(R.id.profile_editBirthOfDate_card);
        editGenderCard = findViewById(R.id.profile_editGender_card);
        editName = findViewById(R.id.profile_editName);
        editUsername = findViewById(R.id.profile_editUsername);
        editEmail = findViewById(R.id.profile_editEmail);
        editAddress = findViewById(R.id.profile_editAddress);
        editPhoneNumber = findViewById(R.id.profile_editPhoneNumber);
        editGender = findViewById(R.id.profile_editGender);
        editBirthOfDate = findViewById(R.id.profile_editBirthOfDate);
    }
}