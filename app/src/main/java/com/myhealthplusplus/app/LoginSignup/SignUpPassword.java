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
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.myhealthplusplus.app.MainActivity;
import com.myhealthplusplus.app.Models.User;
import com.myhealthplusplus.app.R;

import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.RenderScriptBlur;

public class SignUpPassword extends AppCompatActivity {

    ImageView back;
    BlurView blurView;
    TextInputLayout password;
    TextView agree;
    EditText editTextPassword;
    CardView card1, card2, card3, card4;
    private  boolean is8char=false, hasUpper=false, hasNum=false, hasSpecialSymbol =false, isReady=false;
    FirebaseAuth mAuth;
    FirebaseDatabase db;
    DatabaseReference ref;
    private final MainActivity activity = new MainActivity();
    boolean isReadyPassword = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_password);
        getWindow().setStatusBarColor(ContextCompat.getColor(SignUpPassword.this, R.color.dark_black));

        init();
        blurBackground();
        mAuth = FirebaseAuth.getInstance();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        agree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isReady && isReadyPassword) {
                    createUser();
                }
            }
        });

        editTextPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                validatePassword();
                checkRequirements();
                isReady = is8char && hasNum && hasSpecialSymbol && hasUpper;
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    private void createUser() {

        activity.ShowDialog(this);

        String name = getIntent().getStringExtra("NAME");
        String email = getIntent().getStringExtra("EMAIL");
        String pass = password.getEditText().getText().toString();
        String profilePicture = "https://firebasestorage.googleapis.com/v0/b/myhealthplusplus.appspot.com/o/user_profile_pictures%2Fdefault_profile_picture.png?alt=media&token=2d8164a2-c5d1-44f8-b31e-36e1d2af752f";

        mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    String uId = task.getResult().getUser().getUid();
                    FirebaseUser fUser = task.getResult().getUser();
                    addUserToFirebase(name, email, uId, profilePicture, fUser);
                }
                else {
                    activity.DismissDialog();
                    runAlertFail("Sign up", "Please try again later.");
                }
            }
        });
    }

    private void addUserToFirebase(String name, String email, String uId, String profilePicture, FirebaseUser fUser) {
        User user = new User(name, email, uId, profilePicture);
        db = FirebaseDatabase.getInstance();
        ref = db.getReference("users");
        ref.child(uId).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                fUser.sendEmailVerification();
                mAuth.signOut();
                activity.DismissDialog();
                runAlertSuccess("Sign up", "Verification email sent.");
            }
        });
    }

    @SuppressLint("SetTextI18n")
    public void runAlertFail(String topic, String subTopic) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(
                SignUpPassword.this, R.style.BottomSheetDialogTheme
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
                startActivity(new Intent(SignUpPassword.this, SignIn.class));
                finish();
            }
        });
        bottomSheetDialog.setCancelable(true);
        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
    }

    @SuppressLint("SetTextI18n")
    public void runAlertSuccess(String topic, String subTopic) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(
                SignUpPassword.this, R.style.BottomSheetDialogTheme
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
                startActivity(new Intent(SignUpPassword.this, SignIn.class));
            }
        });
        bottomSheetDialog.setCancelable(false);
        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
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
    private void validatePassword() {
        String pass = password.getEditText().getText().toString();

        if (pass.isEmpty()) {
            password.setBoxStrokeColor(Color.parseColor(getString(R.color.red_pie)));
            isReadyPassword = false;
        }else if (pass.contains(" ")) {
            password.setBoxStrokeColor(Color.parseColor(getString(R.color.red_pie)));
            isReadyPassword = false;
        } else {
            password.setBoxStrokeColor(Color.parseColor(getString(R.color.white)));
            isReadyPassword = true;
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