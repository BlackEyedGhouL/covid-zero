package com.myhealthplusplus.app.LoginSignup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.myhealthplusplus.app.IfYoureFeelingSick;
import com.myhealthplusplus.app.MainActivity;
import com.myhealthplusplus.app.R;

import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.RenderScriptBlur;

public class SignIn extends AppCompatActivity {

    private long backPressedTime;
    BlurView blurView;
    TextView signUp, forgotPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        getWindow().setStatusBarColor(ContextCompat.getColor(SignIn.this, R.color.dark_black));

        init();
        blurBackground();

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignIn.this, SignUp.class);
                startActivity(intent);
            }
        });

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignIn.this, ForgotPassword.class);
                startActivity(intent);
            }
        });
    }

    private void blurBackground() {
        float radius = 25f;

        View decorView = getWindow().getDecorView();
        ViewGroup rootView = (ViewGroup) decorView.findViewById(android.R.id.content);
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
        blurView = findViewById(R.id.sign_in_blurView);
        signUp = findViewById(R.id.sign_in_text_sign_up);
        forgotPassword = findViewById(R.id.sign_in_text_forgot_password);
    }

    @Override
    public void onBackPressed() {
        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            finishAffinity();
        } else {
            Toast.makeText(getBaseContext(), "Press back again to exit", Toast.LENGTH_SHORT).show();
        }
        backPressedTime = System.currentTimeMillis();
    }
}