package com.myhealthplusplus.app.CheckIn;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.myhealthplusplus.app.MainActivity;
import com.myhealthplusplus.app.Models.Guest;
import com.myhealthplusplus.app.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;

public class CheckInCreateNewGuest extends AppCompatActivity {

    ImageView back;
    int random, max, min;
    private final MainActivity activity = new MainActivity();
    DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
    TextInputLayout firstName, lastName, email, phoneNumber;
    TextView save;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_in_create_new_guest);
        getWindow().setStatusBarColor(ContextCompat.getColor(CheckInCreateNewGuest.this, R.color.dark_black));

        init();
        user = FirebaseAuth.getInstance().getCurrentUser();

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                activity.ShowDialog(CheckInCreateNewGuest.this);

                String em = email.getEditText().getText().toString();
                String pn = phoneNumber.getEditText().getText().toString().trim();

                if (!em.isEmpty() && !pn.isEmpty()) {
                    if (!validateFirstName() | !validateLastName() | !validateEmail() | !validatePN()) {
                        return;
                    }
                } else if (!em.isEmpty()) {
                    if (!validateFirstName() | !validateLastName() | !validateEmail()) {
                        return;
                    }
                } else if (!pn.isEmpty()) {
                    if (!validateFirstName() | !validateLastName() | !validatePN()) {
                        return;
                    }
                } else {
                    if (!validateFirstName() | !validateLastName()) {
                        return;
                    }
                }

                Random r = new Random();
                random = r.nextInt(max-min+1)+min;

                @SuppressLint("SimpleDateFormat")
                DateFormat df = new SimpleDateFormat("d MMM yyyy 'at' h:mm a");
                String today = df.format(Calendar.getInstance().getTime());

                try {
                    saveGuest("guest"+random, firstName.getEditText().getText().toString(), lastName.getEditText().getText().toString(), em, pn, today);
                    activity.DismissDialog();
                    Toast.makeText(CheckInCreateNewGuest.this, "Guest saved", Toast.LENGTH_SHORT).show();
                    onBackPressed();
                } catch (Exception e) {
                    activity.DismissDialog();
                    Toast.makeText(CheckInCreateNewGuest.this, "Guest save failed: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void saveGuest(String key, String firstName, String lastName, String email, String phoneNumber, String addedDate) {
        if (!phoneNumber.isEmpty()) {
            phoneNumber = "+94" + phoneNumber;
        }
        Guest guest = new Guest(addedDate, email, firstName, lastName, phoneNumber);
        rootRef = FirebaseDatabase.getInstance().getReference().child("frequentGuests").child(user.getUid());
        rootRef.child(key).setValue(guest);
    }

    private void init() {
        back = findViewById(R.id.check_in_create_guest_back);
        save = findViewById(R.id.check_in_create_guest_save);
        firstName = findViewById(R.id.check_in_create_guest_first_name_txtLayout);
        lastName = findViewById(R.id.check_in_create_guest_last_name_txtLayout);
        email = findViewById(R.id.check_in_create_guest_email_txtLayout);
        phoneNumber = findViewById(R.id.check_in_create_guest_phone_txtLayout);
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

    private boolean validateEmail() {
        String val = email.getEditText().getText().toString();
        String checkForLetters = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        if (val.isEmpty()) {
            email.setError("Field can not be empty");
            return false;
        }
        else if (val.contains(" ")) {
            email.setError("No white spaces are allowed!");
            return false;
        }
        else if (!val.matches(checkForLetters)) {
            email.setError("Invalid last name!");
            return false;
        }
        else {
            email.setError(null);
            email.setErrorEnabled(false);
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