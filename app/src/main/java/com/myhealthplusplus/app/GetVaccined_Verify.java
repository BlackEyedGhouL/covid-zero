package com.myhealthplusplus.app;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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

public class GetVaccined_Verify extends AppCompatActivity {

    ImageView back, qr;
    Button save;
    LinearLayout token;
    String codeText;
    DatabaseReference vaccinationRef;
    FirebaseUser user;
    TextView validDate, issuedDate, firstName, lastName, postalCode, gender, indigenous, dateOfBirth, nic, phoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_vaccined_verify);
        getWindow().setStatusBarColor(ContextCompat.getColor(GetVaccined_Verify.this, R.color.dark_black));

        init();
        user = FirebaseAuth.getInstance().getCurrentUser();

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

                save(bitmap);
            }
        });

        createToken();
        generateQr();
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

        codeText = "validDate: " + validDateT
                + "\nissuedDate: " + issuedDateT
                + "\nfirstName: " + firstNameT
                + "\nlastName: " + lastNameT
                + "\npostalCode: " + postalCodeT
                + "\nnic: " + nicT
                + "\nphoneNumber: " + phoneNumberT
                + "\ngender: " + genderT
                + "\ndateOfBirth: " + dateOfBirthT
                + "\nindigenous: " + indigenousT;

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

        VaccinationToken vaccinationToken = new VaccinationToken(issuedDateT, validDateT, firstNameT, lastNameT, postalCodeT, nicT, phoneNumberT, genderT, dateOfBirthT, indigenousT, false);
        addDataToDatabase(vaccinationToken, nicT);
    }

    private void addDataToDatabase(VaccinationToken vaccinationToken, String nic) {
        vaccinationRef = FirebaseDatabase.getInstance().getReference().child("vaccinationTokens").child(user.getUid());
        vaccinationRef.child(nic).setValue(vaccinationToken);
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
    }
}