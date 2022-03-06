package com.myhealthplusplus.app.LoginSignup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.myhealthplusplus.app.R;
import com.myhealthplusplus.app.Settings;

import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.RenderScriptBlur;

public class SignUpPassword extends AppCompatActivity {

    ImageView back;
    BlurView blurView;
    TextInputLayout password;
    TextView agree;
    EditText editTextPassword;
    CardView card1, card2, card3, card4;
    public static String passwordTextFinal;
    private  boolean is8char=false, hasUpper=false, hasNum=false, hasSpecialSymbol =false, isReady=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_password);
        getWindow().setStatusBarColor(ContextCompat.getColor(SignUpPassword.this, R.color.dark_black));

        init();
        blurBackground();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        agree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!validatePassword()) {
                    return;
                }

                if(isReady) {
                    passwordTextFinal = password.getEditText().getText().toString();
                    Toast.makeText(SignUpPassword.this, "Password valid", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(SignUpPassword.this, "Password invalid", Toast.LENGTH_SHORT).show();
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

                isReady = is8char && hasNum && hasSpecialSymbol && hasUpper;
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
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

    @SuppressLint("ResourceType")
    private boolean validatePassword() {
        String pass = password.getEditText().getText().toString();

        if (pass.isEmpty()) {
            password.setBoxStrokeColor(Color.parseColor(getString(R.color.red_pie)));
            Toast.makeText(SignUpPassword.this, "Password can not be empty.", Toast.LENGTH_SHORT).show();
            return false;
        }else if (pass.contains(" ")) {
            password.setBoxStrokeColor(Color.parseColor(getString(R.color.red_pie)));
            Toast.makeText(SignUpPassword.this, "No white spaces are allowed.", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            password.setBoxStrokeColor(Color.parseColor(getString(R.color.white)));
            return true;
        }
    }

    @SuppressLint("ResourceType")
    private void checkRequirements() {
        String pass = password.getEditText().getText().toString();

        if(pass.length()>= 8)
        {
            is8char = true;
            card1.setCardBackgroundColor(Color.parseColor(getString(R.color.red_pie)));
        }else{
            is8char = false;
            card1.setCardBackgroundColor(Color.parseColor(getString(R.color.gray)));
        }

        if(pass.matches("(.*[0-9].*)"))
        {
            hasNum = true;
            card2.setCardBackgroundColor(Color.parseColor(getString(R.color.red_pie)));
        }else{
            hasUpper = false;
            card2.setCardBackgroundColor(Color.parseColor(getString(R.color.gray)));
        }

        if(pass.matches("(.*[A-Z].*)"))
        {
            hasUpper = true;
            card3.setCardBackgroundColor(Color.parseColor(getString(R.color.red_pie)));
        }else{
            hasUpper = false;
            card3.setCardBackgroundColor(Color.parseColor(getString(R.color.gray)));
        }

        if(pass.matches("^(?=.*[_.()#*$&@]).*$")){
            hasSpecialSymbol = true;
            card4.setCardBackgroundColor(Color.parseColor(getString(R.color.red_pie)));
        }else{
            hasSpecialSymbol = false;
            card4.setCardBackgroundColor(Color.parseColor(getString(R.color.gray)));
        }
    }

    public void init() {
        blurView = findViewById(R.id.sign_up_p_blurView);
        back = findViewById(R.id.sign_up_p_back);
        agree = findViewById(R.id.sign_up_p_continue);
        password = findViewById(R.id.sign_up_p_password_txt_field);
        editTextPassword = findViewById(R.id.sign_up_p_password_txt);
        card1 = findViewById(R.id.sign_up_p_changePassword_checkCard1);
        card2 = findViewById(R.id.sign_up_p_changePassword_checkCard2);
        card3 = findViewById(R.id.sign_up_p_changePassword_checkCard3);
        card4 = findViewById(R.id.sign_up_p_changePassword_checkCard4);
    }
}