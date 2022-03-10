package com.myhealthplusplus.app.LoginSignup;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.myhealthplusplus.app.R;
import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.RenderScriptBlur;

public class SignUp extends AppCompatActivity {

    ImageView back;
    BlurView blurView;
    FirebaseAuth mAuth;
    TextView next;
    TextInputLayout name, email;
    EditText emailText, nameText;
    boolean isEmailReady = false, isNameReady = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        getWindow().setStatusBarColor(ContextCompat.getColor(SignUp.this, R.color.dark_black));

        init();
        blurBackground();

        mAuth = FirebaseAuth.getInstance();

        emailText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                validateEmail();
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        nameText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                validateName();
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isEmailReady && isNameReady) {
                    String n = name.getEditText().getText().toString();
                    String e = email.getEditText().getText().toString();

                    Intent intent = new Intent(SignUp.this, SignUpPassword.class);
                    intent.putExtra("NAME", n);
                    intent.putExtra("EMAIL", e);
                    startActivity(intent);
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

    @SuppressLint("ResourceType")
    private void validateEmail() {
        String val = email.getEditText().getText().toString();
        String checkForLetters = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        if (val.isEmpty()) {
            email.setBoxStrokeColor(Color.parseColor(getString(R.color.red_pie)));
            isEmailReady = false;
        }
        else if (val.contains(" ")) {
            email.setBoxStrokeColor(Color.parseColor(getString(R.color.red_pie)));
            isEmailReady = false;
        }
        else if (!val.matches(checkForLetters)) {
            email.setBoxStrokeColor(Color.parseColor(getString(R.color.red_pie)));
            isEmailReady = false;
        }
        else {
            email.setBoxStrokeColor(Color.parseColor(getString(R.color.white)));
            isEmailReady = true;
        }
    }

    @SuppressLint("ResourceType")
    private void validateName() {
        String val = name.getEditText().getText().toString();
        String checkForLetters = "[a-zA-Z ]+";

        if (val.isEmpty()) {
            name.setBoxStrokeColor(Color.parseColor(getString(R.color.red_pie)));
            isNameReady = false;
        }
        else if (!val.matches(checkForLetters)) {
            name.setBoxStrokeColor(Color.parseColor(getString(R.color.red_pie)));
            isNameReady = false;
        }
        else {
            name.setBoxStrokeColor(Color.parseColor(getString(R.color.white)));
            isNameReady = true;
        }
    }

    private void blurBackground() {
        float radius = 25f;

        View decorView = getWindow().getDecorView();
        ViewGroup rootView = decorView.findViewById(android.R.id.content);
        Drawable windowBackground = decorView.getBackground();

        blurView.setupWith(rootView)
                .setFrameClearDrawable(windowBackground)
                .setBlurAlgorithm(new RenderScriptBlur(this))
                .setBlurRadius(radius)
                .setBlurAutoUpdate(true)
                .setHasFixedTransformationMatrix(true);

        blurView.setOutlineProvider(ViewOutlineProvider.BACKGROUND);
        blurView.setClipToOutline(true);
    }

    public void init() {
        blurView = findViewById(R.id.sign_up_blurView);
        back = findViewById(R.id.sign_up_back);
        next = findViewById(R.id.sign_up_continue);
        email = findViewById(R.id.sign_up_email_txt_field);
        name = findViewById(R.id.sign_up_name_txt_field);
        emailText = findViewById(R.id.sign_up_email_txt);
        nameText = findViewById(R.id.sign_up_name_txt);
    }
}