package com.myhealthplusplus.app.CheckIn;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.myhealthplusplus.app.MainActivity;
import com.myhealthplusplus.app.R;

import java.util.HashMap;

public class CheckInHome extends AppCompatActivity {

    ImageView back;
    FirebaseUser user;
    String phoneNumber;
    DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
    TextView name, phone, email, addProof, checkInNow;
    private final MainActivity activity = new MainActivity();
    boolean isPhoneNumberEmpty = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_in_home);
        getWindow().setStatusBarColor(ContextCompat.getColor(CheckInHome.this, R.color.dark_black));

        init();

        user = FirebaseAuth.getInstance().getCurrentUser();

        checkIfPhoneNumberIsThere();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        checkInNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isPhoneNumberEmpty) {
                    addPhoneNumber();
                } else {

                }
            }
        });
    }

    private void checkIfPhoneNumberIsThere() {

        activity.ShowDialog(this);

        rootRef.child("users").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String nameT = snapshot
                        .child("name")
                        .getValue().toString();

                String emailT = snapshot
                        .child("email")
                        .getValue().toString();

                phoneNumber = snapshot
                        .child("phoneNumber")
                        .getValue().toString();

                name.setText(nameT);
                email.setText(emailT);

                activity.DismissDialog();

                if (phoneNumber.isEmpty()) {
                    addPhoneNumber();
                } else {
                    isPhoneNumberEmpty = false;
                    phone.setText(phoneNumber);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void addPhoneNumber() {
        TextInputLayout phoneNumberTextLayout;

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(
                CheckInHome.this, R.style.BottomSheetDialogTheme
        );
        View bottomSheetView = LayoutInflater.from(CheckInHome.this)
                .inflate(
                        R.layout.add_phone_number_bottom_sheet,
                        bottomSheetDialog.findViewById(R.id.check_in_add_phone_number_bottom_sheet)
                );

        phoneNumberTextLayout = bottomSheetView.findViewById(R.id.check_in_add_phone_number_txt_layout);

        bottomSheetDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                if (isPhoneNumberEmpty) {
                    Toast.makeText(CheckInHome.this, "You won't be able to check in without entering your phone number", Toast.LENGTH_SHORT).show();
                    checkInNow.setText("Add Phone Number");
                }
            }
        });

        bottomSheetView.findViewById(R.id.check_in_add_phone_number).setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {

                if (!validatePhoneNumber()) {
                    return;
                }

                activity.ShowDialog(CheckInHome.this);
                String phoneN = "+94"+phoneNumberTextLayout.getEditText().getText().toString().trim();
                phone.setText(phoneN);
                updatePhoneNumberInDatabase(phoneN);

                bottomSheetDialog.dismiss();
            }

            private void updatePhoneNumberInDatabase(String phoneN) {
                DatabaseReference phoneRef;
                HashMap uHash = new HashMap();
                uHash.put("phoneNumber", phoneN);

                phoneRef = FirebaseDatabase.getInstance().getReference().child("users");
                phoneRef.child(user.getUid()).updateChildren(uHash).addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        Log.d(TAG, "onSuccess: Done!");
                        isPhoneNumberEmpty = false;
                        checkInNow.setText("Check In Now");
                        activity.DismissDialog();
                    }
                });
            }

            private boolean validatePhoneNumber() {
                String val = phoneNumberTextLayout.getEditText().getText().toString();
                String checkForLetters = "[0-9]+";
                if (val.isEmpty()) {
                    phoneNumberTextLayout.setError("Field can not be empty");
                    return false;
                }
                else if (!val.matches(checkForLetters)) {
                    phoneNumberTextLayout.setError("Invalid phone number!");
                    return false;
                }
                else if (val.length() < 9) {
                    phoneNumberTextLayout.setError("Phone number should contain 9 characters!");
                    return false;
                }
                else {
                    phoneNumberTextLayout.setError(null);
                    phoneNumberTextLayout.setErrorEnabled(false);
                    return true;
                }
            }
        });

        bottomSheetDialog.setCancelable(true);
        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
    }

    private void init() {
        back = findViewById(R.id.check_in_home_back);
        name = findViewById(R.id.check_in_home_name);
        phone = findViewById(R.id.check_in_home_phone_number);
        email = findViewById(R.id.check_in_home_email);
        addProof = findViewById(R.id.check_in_home_add_proof);
        checkInNow = findViewById(R.id.check_in_home_check_in_now);
    }
}