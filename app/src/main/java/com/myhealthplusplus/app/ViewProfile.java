package com.myhealthplusplus.app;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputLayout;
import com.myhealthplusplus.app.Status.Data;

public class ViewProfile extends AppCompatActivity {

    Spinner spinner;
    CardView profile_color_ring;
    CardView editNameCard, editEmailCard;
    TextView editName, editEmail;
    TextInputLayout firstName, lastName, password;
    ImageView back, profile_picture;
    public static String FName, LName, PWord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);
        getWindow().setStatusBarColor(ContextCompat.getColor(ViewProfile.this, R.color.dark_black));

        init();
        getDataFromSharedPreferences();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        editNameCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(ViewProfile.this);
                LayoutInflater inflater = (LayoutInflater) ViewProfile.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View v = inflater.inflate(R.layout.edit_profile_name_window_p1, findViewById(R.id.edit_profile_name_window_p1_layout));
                TextView btnReview = v.findViewById(R.id.editProfileName_review_btn);
                TextView btnP1Cancel = v.findViewById(R.id.editProfileName_cancel_p1_btn);
                builder.setCancelable(true);
                builder.setView(v);

                final Dialog dialog = builder.create();
                dialog.getWindow().setWindowAnimations(R.style.DialogAnimation);

                btnReview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        firstName = v.findViewById(R.id.editProfileName_firstName_txtLayout);
                        lastName = v.findViewById(R.id.editProfileName_LastName_txtLayout);
                        password = v.findViewById(R.id.editProfileName_password_txtLayout);
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

        editEmailCard.setOnClickListener(new View.OnClickListener() {
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

    private void getDataFromSharedPreferences() {
        SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);

        String fullName = preferences.getString("fullName", "");
        String email = preferences.getString("userEmail", "");
        String profilePicture = preferences.getString("userPhoto", "");

        editName.setText(fullName);
        editEmail.setText(email);

        Glide.with(this).load(profilePicture).into(profile_picture);
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

    private void init() {
        spinner = findViewById(R.id.profile_spinner_status);
        profile_color_ring = findViewById(R.id.profile_color_ring);
        editNameCard = findViewById(R.id.profile_editName_card);
        editEmailCard = findViewById(R.id.profile_editEmail_card);
        editName = findViewById(R.id.profile_editName);
        editEmail = findViewById(R.id.profile_editEmail);
        back = findViewById(R.id.profile_back);
        profile_picture = findViewById(R.id.profile_picture);
    }
}