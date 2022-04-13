package com.myhealthplusplus.app;

import static android.content.ContentValues.TAG;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.myhealthplusplus.app.Models.VaccinationToken;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.HashMap;

public class GetVaccined_Verify extends AppCompatActivity {

    ImageView back, qr;
    Button save;
    LinearLayout token;
    String codeText, finalNic, finalDose;
    DatabaseReference vaccinationRef;
    FirebaseUser user;
    private final MainActivity activity = new MainActivity();
    StorageReference storageReference;
    TextView validDate, issuedDate, firstName, lastName, postalCode, gender, indigenous, dateOfBirth, nic, phoneNumber, dose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_vaccined_verify);
        getWindow().setStatusBarColor(ContextCompat.getColor(GetVaccined_Verify.this, R.color.dark_black));

        init();
        user = FirebaseAuth.getInstance().getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                token.setDrawingCacheEnabled(true);
                token.buildDrawingCache();
                token.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
                Bitmap bitmap = token.getDrawingCache();

                activity.ShowDialog(GetVaccined_Verify.this);
                save(bitmap);
                activity.DismissDialog();
            }
        });

        activity.ShowDialog(this);

        createToken();
        generateQr();

        activity.DismissDialog();
    }

    private void uploadImageToStorage(Uri imageUri) {
        StorageReference fileRef = storageReference.child("vaccination_tokens/" + user.getUid() + "/token_" + finalNic + finalDose +".jpg");
        fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Log.d(TAG, "onSuccess: Done!");
                        updateDatabaseWithImage(uri);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFail: "+e.getMessage());
            }
        });
    }

    private void save(Bitmap bitmap) {
        String root = Environment.getExternalStorageDirectory().getAbsolutePath();
        File file = new File(root + "/Download");
        String fileName = "vaccination-digital-token.jpg";
        File imgFile = new File(file,fileName);

        if (imgFile.exists()) {
            imgFile.delete();
        }

        try {
            FileOutputStream fileOutputStream = new FileOutputStream(imgFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
            Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();
            token.setDrawingCacheEnabled(false);

            Uri imageUri = Uri.fromFile(imgFile);
            uploadImageToStorage(imageUri);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void generateQr() {
        MultiFormatWriter writer = new MultiFormatWriter();

        try {
            BitMatrix matrix = writer.encode(codeText, BarcodeFormat.QR_CODE, 350, 350);
            BarcodeEncoder encoder = new BarcodeEncoder();
            Bitmap bitmap = encoder.createBitmap(matrix);
            qr.setImageBitmap(bitmap);

        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    private void createToken() {
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Dhaka"));
        LocalDate expireDate = today.plusWeeks(2);
        DateTimeFormatter userFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG);

        String issuedDateT = today.format(userFormatter);
        String validDateT = expireDate.format(userFormatter);
        String firstNameT = getIntent().getStringExtra("FIRST_NAME");
        String lastNameT = getIntent().getStringExtra("LAST_NAME");
        String postalCodeT = getIntent().getStringExtra("POSTAL_CODE");
        String nicT = getIntent().getStringExtra("NIC");
        String phoneNumberT = getIntent().getStringExtra("PHONE");
        String genderT = getIntent().getStringExtra("GENDER");
        String dateOfBirthT = getIntent().getStringExtra("DOB");
        String indigenousT = getIntent().getStringExtra("INDIGENOUS");
        String doseT = getIntent().getStringExtra("DOSE");

        issuedDate.setText(issuedDateT);
        validDate.setText(validDateT);
        firstName.setText(firstNameT);
        lastName.setText(lastNameT);
        postalCode.setText(postalCodeT);
        nic.setText(nicT);
        phoneNumber.setText(phoneNumberT);
        gender.setText(genderT);
        dateOfBirth.setText(dateOfBirthT);
        indigenous.setText(indigenousT);
        dose.setText(doseT);

        finalNic = nicT;

        switch (doseT) {
            case "- DOSE 01 -": {
                doseT = "Dose1";
                finalDose = "_dose1";
                VaccinationToken vaccinationToken = new VaccinationToken(issuedDateT, validDateT, firstNameT, lastNameT, postalCodeT, nicT, phoneNumberT, genderT, dateOfBirthT, indigenousT, "", false, true, false, false);
                addDataToDatabase(vaccinationToken, nicT);
                break;
            }
            case "- DOSE 02 -": {
                doseT = "Dose2";
                finalDose = "_dose2";
                updateDatabaseWithDose(true, "dose2");
                break;
            }
            case "- DOSE 03 -": {
                doseT = "Dose3";
                finalDose = "_dose3";
                updateDatabaseWithDose(true, "dose3");
                break;
            }
        }

        codeText = "validDate: " + validDateT
                + "\nissuedDate: " + issuedDateT
                + "\nfirstName: " + firstNameT
                + "\nlastName: " + lastNameT
                + "\npostalCode: " + postalCodeT
                + "\nnic: " + nicT
                + "\nphoneNumber: " + phoneNumberT
                + "\ngender: " + genderT
                + "\ndateOfBirth: " + dateOfBirthT
                + "\nindigenous: " + indigenousT
                + "\ndose: " + doseT;
    }

    private void addDataToDatabase(VaccinationToken vaccinationToken, String nic) {
        vaccinationRef = FirebaseDatabase.getInstance().getReference().child("vaccinationTokens").child(user.getUid());
        vaccinationRef.child(nic).setValue(vaccinationToken);
    }

    private void updateDatabaseWithImage(Uri uri) {
        HashMap uHash = new HashMap();
        uHash.put("tokenImageUrl", uri.toString());

        vaccinationRef = FirebaseDatabase.getInstance().getReference().child("vaccinationTokens").child(user.getUid());
        vaccinationRef.child(finalNic).updateChildren(uHash).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "onSuccess: Done!");
                }
            }
        });
    }

    private void updateDatabaseWithDose(boolean ds, String ds_text) {
        HashMap uHash = new HashMap();
        uHash.put(ds_text, ds);

        vaccinationRef = FirebaseDatabase.getInstance().getReference().child("vaccinationTokens").child(user.getUid());
        vaccinationRef.child(finalNic).updateChildren(uHash).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "onSuccess: Done!");
                }
            }
        });
    }

    private void init() {
        back = findViewById(R.id.token_back);
        qr = findViewById(R.id.token_qr);
        save = findViewById(R.id.token_download);
        token = findViewById(R.id.token_layout);
        validDate = findViewById(R.id.token_valid_date);
        issuedDate = findViewById(R.id.token_issued_date);
        firstName = findViewById(R.id.token_first_name);
        lastName = findViewById(R.id.token_last_name);
        postalCode = findViewById(R.id.token_postal_code);
        gender = findViewById(R.id.token_gender);
        indigenous = findViewById(R.id.token_indigenous);
        dateOfBirth = findViewById(R.id.token_dob);
        nic = findViewById(R.id.token_nic);
        phoneNumber = findViewById(R.id.token_phone_number);
        dose = findViewById(R.id.token_dose);
    }
}