package com.myhealthplusplus.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.myhealthplusplus.app.LoginSignup.SignIn;

import java.util.Objects;

public class Settings extends AppCompatActivity {

    TextInputLayout current_password, new_password, confirm_password;
    CardView card1, card2, card3, card4;
    EditText editTextPassword;
    TextView btnSave, btnCancel;

    public static String Password;
    private  boolean is8char=false, hasUpper=false, hasnum=false, hasSpecialSymbol =false, isReady=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        setTitle(Html.fromHtml("<font color=\"white\">" + "Settings" + "</font>"));

        CardView editProfile = findViewById(R.id.settings_editProfile_card);
        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Settings.this, ViewProfile.class);
                startActivity(intent);
            }
        });

        CardView logOut = findViewById(R.id.settings_logout_card);
        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Settings.this, SignIn.class);
                startActivity(intent);
            }
        });

        CardView changePassword = findViewById(R.id.settings_editProfile_changePassword_card);
        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(Settings.this);
                LayoutInflater inflater = (LayoutInflater) Settings.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View v = inflater.inflate(R.layout.activity_change_password, findViewById(R.id.changePassword_layout));
                btnSave = v.findViewById(R.id.changePassword_saveBtn);
                btnCancel = v.findViewById(R.id.changePassword_cancelBtn);
                builder.setCancelable(true);
                builder.setView(v);

                final Dialog dialog = builder.create();
                dialog.getWindow().setWindowAnimations(R.style.DialogAnimation);

                card1 = (CardView) v.findViewById(R.id.changePassword_checkCard1);
                card2 = (CardView) v.findViewById(R.id.changePassword_checkCard2);
                card3 = (CardView) v.findViewById(R.id.changePassword_checkCard3);
                card4 = (CardView) v.findViewById(R.id.changePassword_checkCard4);
                current_password = (TextInputLayout) v.findViewById(R.id.changePassword_currentPassword_Layout);
                new_password = (TextInputLayout) v.findViewById(R.id.changePassword_newPassword_layout);
                confirm_password = (TextInputLayout) v.findViewById(R.id.changePassword_confirmNewPassword_layout);
                editTextPassword = (EditText) v.findViewById(R.id.changePassword_newPassword_txt);

                btnSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (!validateCurrentPassword() | !validateNewPassword() | !validateConfirmPassword()) {
                            return;
                        }

                        if(isReady) {
                            Password = new_password.getEditText().getText().toString();
                            dialog.dismiss();
                            Toast.makeText(Settings.this, "Password changed successfully!", Toast.LENGTH_LONG).show();
                        }
                        else{
                            Toast.makeText(Settings.this, "New password invalid", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                editTextPassword.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                        checkRequirements();

                        if(is8char && hasnum && hasSpecialSymbol && hasUpper)
                        {
                            isReady = true;
                        }
                        else{
                            isReady = false;
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                    }
                });

                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });
    }

    @SuppressLint("ResourceType")
    private void checkRequirements() {
        String password = new_password.getEditText().getText().toString();

        if(password.length()>= 8)
        {
            is8char = true;
            card1.setCardBackgroundColor(Color.parseColor(getString(R.color.red_pie)));
        }else{
            is8char = false;
            card1.setCardBackgroundColor(Color.parseColor(getString(R.color.gray)));
        }

        if(password.matches("(.*[0-9].*)"))
        {
            hasnum = true;
            card2.setCardBackgroundColor(Color.parseColor(getString(R.color.red_pie)));
        }else{
            hasUpper = false;
            card2.setCardBackgroundColor(Color.parseColor(getString(R.color.gray)));
        }

        if(password.matches("(.*[A-Z].*)"))
        {
            hasUpper = true;
            card3.setCardBackgroundColor(Color.parseColor(getString(R.color.red_pie)));
        }else{
            hasUpper = false;
            card3.setCardBackgroundColor(Color.parseColor(getString(R.color.gray)));
        }

        if(password.matches("^(?=.*[_.()#*$&@]).*$")){
            hasSpecialSymbol = true;
            card4.setCardBackgroundColor(Color.parseColor(getString(R.color.red_pie)));
        }else{
            hasSpecialSymbol = false;
            card4.setCardBackgroundColor(Color.parseColor(getString(R.color.gray)));
        }
    }

    private boolean validateConfirmPassword() {
        String val = new_password.getEditText().getText().toString();
        String val1 = confirm_password.getEditText().getText().toString();

        if (val.isEmpty()) {
            confirm_password.setError("Field can not be empty");
            return false;
        } else if (!(val.equals(val1))) {
            confirm_password.setError("Password doesn't match with new password");
            return false;
        } else {
            confirm_password.setError(null);
            confirm_password.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validateNewPassword() {
        String newPass = new_password.getEditText().getText().toString();

        if (newPass.isEmpty()) {
            new_password.setError("Field can not be empty");
            return false;
        }else if (newPass.contains(" ")) {
            new_password.setError("No white spaces are allowed!");
            return false;
        } else {
            new_password.setError(null);
            new_password.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validateCurrentPassword() {
        String val = current_password.getEditText().getText().toString();

        if (val.isEmpty()) {
            current_password.setError("Field can not be empty");
            return false;
        } else {
            current_password.setError(null);
            current_password.setErrorEnabled(false);
            return true;
        }
    }
}