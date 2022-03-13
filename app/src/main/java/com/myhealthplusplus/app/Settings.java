package com.myhealthplusplus.app;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.InsetDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.myhealthplusplus.app.LoginSignup.ForgotPassword;
import com.myhealthplusplus.app.LoginSignup.SignIn;

public class Settings extends AppCompatActivity {

    TextInputLayout current_password, new_password, confirm_password, deletePassword;
    CardView card1, card2, card3, card4, changePassword;
    EditText editTextNewPassword, textDeletePassword, editTextCurrentPassword, editTextConfirmPassword;
    TextView btnSave, btnCancel, btnCancelDelete, btnConfirmDelete, deleteForgot;
    ImageView back;
    FirebaseAuth mAuth;
    private final MainActivity activity = new MainActivity();
    public static String Password;
    String pass = "";
    private  boolean is8char=false, hasUpper=false, hasNum=false, hasSpecialSymbol =false, isReady=false, isDeleteReady=false, isGoogle=false, isCurrentPasswordReady = false, isNewPasswordReady = false, isConfirmPasswordReady = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getWindow().setStatusBarColor(ContextCompat.getColor(Settings.this, R.color.dark_black));

        mAuth = FirebaseAuth.getInstance();
        isGoogle = false;

        changePassword = findViewById(R.id.settings_editProfile_changePassword_card);

        getDataFromSharedPreferences();

        back = findViewById(R.id.settings_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        CardView editProfile = findViewById(R.id.settings_editProfile_card);
        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Settings.this, ViewProfile.class);
                startActivity(intent);
            }
        });

        CardView logOut = findViewById(R.id.settings_sign_out_card);
        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                Intent intent = new Intent(Settings.this, SignIn.class);
                startActivity(intent);
            }
        });

        CardView deleteProfile = findViewById(R.id.settings_deleteAccount_card);
        deleteProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(Settings.this);
                LayoutInflater inflater = (LayoutInflater) Settings.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View v = inflater.inflate(R.layout.activity_delete_account, findViewById(R.id.delete_profile_layout));
                btnConfirmDelete = v.findViewById(R.id.deleteProfile_confirm_btn);
                btnCancelDelete = v.findViewById(R.id.deleteProfile_cancel_btn);
                deletePassword = v.findViewById(R.id.deleteProfile_currentPassword_Layout);
                textDeletePassword = v.findViewById(R.id.deleteProfile_currentPassword_txt);
                deleteForgot = v.findViewById(R.id.deleteProfile_forgotPassword);
                builder.setCancelable(true);
                builder.setView(v);

                final Dialog dialog = builder.create();
                dialog.getWindow().setWindowAnimations(R.style.DialogAnimation);

                if (isGoogle) {
                    deleteForgot.setVisibility(View.GONE);
                    deletePassword.setVisibility(View.GONE);
                }

                deleteForgot.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mAuth.signOut();
                        dialog.dismiss();
                        Intent intent = new Intent(Settings.this, ForgotPassword.class);
                        finishAffinity();
                        startActivity(intent);
                    }
                });

                textDeletePassword.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        validateDeletePassword();
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                    }
                });

                btnConfirmDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (isGoogle) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            user.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    mAuth.signOut();
                                    dialog.dismiss();

                                    String userUid = user.getUid();

                                    activity.ShowDialog(Settings.this);
                                    deleteFromDatabase(userUid);
                                    activity.DismissDialog();

                                    runAlertSuccess("Delete account", "Account deleted.");
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    dialog.dismiss();
                                    runAlertFail("Delete account", "Please try again later.");
                                }
                            });
                        } else {
                            String p = deletePassword.getEditText().getText().toString();
                            if(isDeleteReady )
                            {
                                if (!p.equals(pass)) {
                                    dialog.dismiss();
                                    runAlertFail("Delete account", "Incorrect password.");
                                } else {
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    user.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            mAuth.signOut();
                                            dialog.dismiss();

                                            String userUid = user.getUid();

                                            activity.ShowDialog(Settings.this);
                                            deleteFromDatabase(userUid);
                                            activity.DismissDialog();

                                            runAlertSuccess("Delete account", "Account deleted.");
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            dialog.dismiss();
                                            runAlertFail("Delete account", "Please try again later.");
                                        }
                                    });
                                }
                            }
                        }
                    }
                });

                btnCancelDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });

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

                card1 = v.findViewById(R.id.changePassword_checkCard1);
                card2 = v.findViewById(R.id.changePassword_checkCard2);
                card3 = v.findViewById(R.id.changePassword_checkCard3);
                card4 = v.findViewById(R.id.changePassword_checkCard4);
                current_password = v.findViewById(R.id.changePassword_currentPassword_Layout);
                new_password = v.findViewById(R.id.changePassword_newPassword_layout);
                confirm_password = v.findViewById(R.id.changePassword_confirmNewPassword_layout);
                editTextNewPassword = v.findViewById(R.id.changePassword_newPassword_txt);
                editTextCurrentPassword = v.findViewById(R.id.changePassword_currentPassword_txt);
                editTextConfirmPassword = v.findViewById(R.id.changePassword_confirmNewPassword_txt);

                btnSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if(isReady && isCurrentPasswordReady && isNewPasswordReady && isConfirmPasswordReady) {

                            Password = new_password.getEditText().getText().toString();

                            if (!Password.equals(pass)) {
                                    dialog.dismiss();
                                    runAlertFail("Change Password", "Incorrect password.");
                                } else {

                                FirebaseUser user = mAuth.getCurrentUser();
                                user.updatePassword(Password).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        mAuth.signOut();
                                        dialog.dismiss();

                                        runAlertSuccess("Change Password", "Password changed.");
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        dialog.dismiss();
                                        runAlertFail("Change Password", "Please try again later.");
                                    }
                                });
                            }
                        }
                    }
                });

                editTextNewPassword.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        validateNewPassword();
                        checkRequirements();
                        isReady = is8char && hasNum && hasSpecialSymbol && hasUpper;
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                    }
                });

                editTextCurrentPassword.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        validateCurrentPassword();
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });

                editTextConfirmPassword.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        validateConfirmPassword();
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

    private void getDataFromSharedPreferences() {
        SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        isGoogle = preferences.getBoolean("isGoogle", false);

        if(isGoogle) {
            changePassword.setVisibility(View.INVISIBLE);
            changePassword.setClickable(false);
            changePassword.setFocusable(false);
            changePassword.setForeground(null);
        } else {pass = preferences.getString("password", "");
            Log.d(TAG, "getDataFromSharedPreferences: "+pass);
        }
    }

    private void deleteFromDatabase(String userUid) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users");
        ref.child(userUid).removeValue();
    }

    @SuppressLint("SetTextI18n")
    public void runAlertFail(String topic, String subTopic) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(
                Settings.this, R.style.BottomSheetDialogTheme
        );
        View bottomSheetView = LayoutInflater.from(getApplicationContext())
                .inflate(
                        R.layout.fail_alert_box,
                        findViewById(R.id.cm_fail_alert_box)
                );

        ColorDrawable back = new ColorDrawable(Color.TRANSPARENT);
        InsetDrawable inset = new InsetDrawable(back, 20);
        bottomSheetDialog.getWindow().setBackgroundDrawable(inset);

        TextView t = bottomSheetView.findViewById(R.id.cm_fail);
        t.setText(topic);

        TextView subT = bottomSheetView.findViewById(R.id.cm_fail_subT);
        subT.setText(subTopic);

        bottomSheetView.findViewById(R.id.cm_close_fail).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.dismiss();
            }
        });
        bottomSheetDialog.setCancelable(true);
        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
    }

    @SuppressLint("SetTextI18n")
    public void runAlertSuccess(String topic, String subTopic) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(
                Settings.this, R.style.BottomSheetDialogTheme
        );
        View bottomSheetView = LayoutInflater.from(getApplicationContext())
                .inflate(
                        R.layout.success_alert_box,
                        findViewById(R.id.cm_success_alert_box)
                );

        ColorDrawable back = new ColorDrawable(Color.TRANSPARENT);
        InsetDrawable inset = new InsetDrawable(back, 20);
        bottomSheetDialog.getWindow().setBackgroundDrawable(inset);

        TextView t = bottomSheetView.findViewById(R.id.cm_success);
        t.setText(topic);

        TextView subT = bottomSheetView.findViewById(R.id.cm_success_subT);
        subT.setText(subTopic);

        bottomSheetView.findViewById(R.id.cm_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.dismiss();
                Intent intent = new Intent(Settings.this, SignIn.class);
                startActivity(intent);
            }
        });
        bottomSheetDialog.setCancelable(false);
        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
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
            hasNum = true;
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

    @SuppressLint("ResourceType")
    private void validateConfirmPassword() {
        String val = new_password.getEditText().getText().toString();
        String val1 = confirm_password.getEditText().getText().toString();

        if (val.isEmpty()) {
            confirm_password.setBoxStrokeColor(Color.parseColor(getString(R.color.red_pie)));
            isConfirmPasswordReady = false;
        } else if (!(val.equals(val1))) {
            confirm_password.setBoxStrokeColor(Color.parseColor(getString(R.color.red_pie)));
            isConfirmPasswordReady = false;
        } else {
            confirm_password.setBoxStrokeColor(Color.parseColor(getString(R.color.white)));
            isConfirmPasswordReady = true;
        }
    }

    @SuppressLint("ResourceType")
    private void validateNewPassword() {
        String newPass = new_password.getEditText().getText().toString();

        if (newPass.isEmpty()) {
            new_password.setBoxStrokeColor(Color.parseColor(getString(R.color.red_pie)));
            isNewPasswordReady = false;
        }else if (newPass.contains(" ")) {
            new_password.setBoxStrokeColor(Color.parseColor(getString(R.color.red_pie)));
            isNewPasswordReady = false;
        } else {
            new_password.setBoxStrokeColor(Color.parseColor(getString(R.color.white)));
            isNewPasswordReady = true;
        }
    }

    @SuppressLint("ResourceType")
    private void validateCurrentPassword() {
        String val = current_password.getEditText().getText().toString();

        if (val.isEmpty()) {
            current_password.setBoxStrokeColor(Color.parseColor(getString(R.color.red_pie)));
            isCurrentPasswordReady = false;
        } else {
            current_password.setBoxStrokeColor(Color.parseColor(getString(R.color.white)));
            isCurrentPasswordReady = true;
        }
    }

    @SuppressLint("ResourceType")
    private void validateDeletePassword() {
        String pass = deletePassword.getEditText().getText().toString();

        if (pass.isEmpty()) {
            deletePassword.setBoxStrokeColor(Color.parseColor(getString(R.color.red_pie)));
            isDeleteReady = false;
        }else if (pass.contains(" ")) {
            deletePassword.setBoxStrokeColor(Color.parseColor(getString(R.color.red_pie)));
            isDeleteReady = false;
        } else {
            deletePassword.setBoxStrokeColor(Color.parseColor(getString(R.color.white)));
            isDeleteReady = true;
        }
    }
}