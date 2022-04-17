package com.myhealthplusplus.app.CheckIn;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
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
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.myhealthplusplus.app.MainActivity;
import com.myhealthplusplus.app.R;

import java.util.HashMap;

public class CheckInHome extends AppCompatActivity {

    ImageView back, menu;
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

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopup(view);
            }
        });

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
                    checkMyPermission();
                }
            }
        });
    }

    private void checkMyPermission() {

        Dexter.withContext(this).withPermission(Manifest.permission.CAMERA).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                Intent intent = new Intent(CheckInHome.this, CheckInScanner.class);
                startActivity(intent);
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", CheckInHome.this.getPackageName(), "");
                intent.setData(uri);
                startActivity(intent);
            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest();
            }
        }).check();
    }

    @SuppressLint("RestrictedApi")
    private void showPopup(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                int itemId = menuItem.getItemId();

                if (itemId == R.id.check_in_history) {
                    Intent intent = new Intent(CheckInHome.this, CheckInHistory.class);
                    startActivity(intent);
                    return true;
                } else if (itemId == R.id.check_in_guests) {
                    Intent intent = new Intent(CheckInHome.this, CheckInAddPeople.class);
                    startActivity(intent);
                    return true;
                }
                return false;
            }
        });
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.check_in_menu, popup.getMenu());
        if(popup.getMenu() instanceof MenuBuilder){
            MenuBuilder m = (MenuBuilder) popup.getMenu();
            m.setOptionalIconsVisible(true);
        }
        popup.show();
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
        menu = findViewById(R.id.check_in_home_menu);
    }
}