package com.myhealthplusplus.app.LoginSignup;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.InsetDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.myhealthplusplus.app.MainActivity;
import com.myhealthplusplus.app.R;

import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.RenderScriptBlur;

public class ForgotPassword extends AppCompatActivity {

    ImageView back;
    BlurView blurView;
    FirebaseAuth mAuth;
    TextInputLayout email;
    EditText emailText;
    boolean isReady=false;
    TextView submit;
    private final MainActivity activity = new MainActivity();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        getWindow().setStatusBarColor(ContextCompat.getColor(ForgotPassword.this, R.color.dark_black));

        init();
        blurBackground();
        mAuth = FirebaseAuth.getInstance();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isReady) {
                    activity.ShowDialog(ForgotPassword.this);
                    String tEmail = email.getEditText().getText().toString();
                    mAuth.sendPasswordResetEmail(tEmail).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            activity.DismissDialog();
                            runAlertSuccess("Forgot password", "Password reset email sent.");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            activity.DismissDialog();
                            String msg = e.getMessage();
                            if(msg.equals("There is no user record corresponding to this identifier. The user may have been deleted.")) {
                                runAlertFail("Forgot password", "No user found.");
                            } else if(msg.equals("The email address is badly formatted.")) {
                                runAlertFail("Forgot password", "Invalid email address.");
                            }
                            else{
                                runAlertFail("Forgot password", "Please try again later.");
                            }
                        }
                    });
                }
            }
        });

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
        blurView = findViewById(R.id.fp_blurView);
        back = findViewById(R.id.fp_back);
        email = findViewById(R.id.fp_email_txt_field);
        emailText = findViewById(R.id.fp_email_txt);
        submit = findViewById(R.id.fp_submit);
    }

    @SuppressLint("ResourceType")
    private void validateEmail() {
        String val = email.getEditText().getText().toString();
        String checkForLetters = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        if (val.isEmpty()) {
            email.setBoxStrokeColor(Color.parseColor(getString(R.color.red_pie)));
            isReady = false;
        }
        else if (val.contains(" ")) {
            email.setBoxStrokeColor(Color.parseColor(getString(R.color.red_pie)));
            isReady = false;
        }
        else if (!val.matches(checkForLetters)) {
            email.setBoxStrokeColor(Color.parseColor(getString(R.color.red_pie)));
            isReady = false;
        }
        else {
            email.setBoxStrokeColor(Color.parseColor(getString(R.color.white)));
            isReady = true;
        }
    }

    @SuppressLint("SetTextI18n")
    public void runAlertFail(String topic, String subTopic) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(
                ForgotPassword.this, R.style.BottomSheetDialogTheme
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
                ForgotPassword.this, R.style.BottomSheetDialogTheme
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
                Intent intent = new Intent(ForgotPassword.this, SignIn.class);
                startActivity(intent);
            }
        });
        bottomSheetDialog.setCancelable(false);
        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
    }
}