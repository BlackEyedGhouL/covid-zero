package com.myhealthplusplus.app.LoginSignup;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.InsetDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.myhealthplusplus.app.MainActivity;
import com.myhealthplusplus.app.Models.User;
import com.myhealthplusplus.app.R;
import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.RenderScriptBlur;

public class SignIn extends AppCompatActivity {

    private long backPressedTime;
    BlurView blurView;
    TextView signUp, forgotPassword, signIn;
    LinearLayout signInGoogle;
    private GoogleSignInClient mGoogleSignInClient;
    public static final int RC_SIGN_IN = 123;
    private FirebaseAuth mAuth;
    TextInputLayout password, email;
    EditText emailText, passwordText;
    boolean isReady=false, isReadyPassword = false;;
    private final MainActivity activity = new MainActivity();
    DatabaseReference ref = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        getWindow().setStatusBarColor(ContextCompat.getColor(SignIn.this, R.color.dark_black));

        init();
        blurBackground();
        mAuth = FirebaseAuth.getInstance();
        requestGoogleSignIn();

        signInGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

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

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isReady && isReadyPassword) {

                    activity.ShowDialog(SignIn.this);

                    String tEmail = email.getEditText().getText().toString();
                    String tPass = password.getEditText().getText().toString();

                    mAuth.signInWithEmailAndPassword(tEmail, tPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()) {
                                if(!task.getResult().getUser().isEmailVerified()) {
                                    mAuth.signOut();
                                    activity.DismissDialog();
                                    runAlertFail("Sign in", "Account hasn't been verified.");
                                } else {
                                    getUserFromDatabase(tPass);
                                    activity.DismissDialog();
                                    runAlertSuccess("Sign in", "Signed in successfully.");
                                }
                            }
                            else {
                                activity.DismissDialog();
                                String msg = task.getException().getMessage();
                                Log.d(TAG, "onComplete: "+msg);
                                if(msg.equals("The password is invalid or the user does not have a password.")) {
                                    runAlertFail("Sign in", "Incorrect email or password.");
                                } else if(msg.equals("There is no user record corresponding to this identifier. The user may have been deleted.")) {
                                    runAlertFail("Sign in", "No user found.");
                                }
                                else{
                                    runAlertFail("Sign in", "Please try again later.");
                                }
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

        passwordText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                validatePassword();
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    public void getUserFromDatabase(String tPass) {
        SharedPreferences.Editor editor = getApplicationContext()
                .getSharedPreferences("MyPrefs", MODE_PRIVATE)
                .edit();

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference userRef = ref
                .child("users");
        String userUid = firebaseUser.getUid();
        userRef.child(userUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                    editor.putString("fullName", user.getName());
                    editor.putString("userEmail", user.getEmail());
                    editor.putString("userPhoto", user.getProfilePicture());
                    editor.putString("password", tPass);
                    editor.putBoolean("isGoogle", false);
                    editor.apply();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void checkIfUserExists() {
        SharedPreferences.Editor editor = getApplicationContext()
                .getSharedPreferences("MyPrefs", MODE_PRIVATE)
                .edit();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference userRef = ref
                .child("users");
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.hasChild(user.getUid())) {

                    FirebaseDatabase db;
                    DatabaseReference reference;

                    User fUser = new User(user.getDisplayName(), user.getEmail(), user.getUid(), user.getPhotoUrl().toString());
                    db = FirebaseDatabase.getInstance();
                    reference = db.getReference("users");
                    reference.child(user.getUid()).setValue(fUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            editor.putString("fullName", user.getDisplayName());
                            editor.putString("userEmail", user.getEmail());
                            editor.putString("userPhoto", user.getPhotoUrl().toString());
                            editor.putBoolean("isGoogle", true);
                            editor.apply();
                        }
                    });
                } else {
                    getExistingDataFromFirebase(user.getUid());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        userRef.addListenerForSingleValueEvent(valueEventListener);
    }

    private void getExistingDataFromFirebase(String uid) {
        SharedPreferences.Editor editor = getApplicationContext()
                .getSharedPreferences("MyPrefs", MODE_PRIVATE)
                .edit();

        DatabaseReference userRef = ref
                .child("users");
        userRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                editor.putString("fullName", user.getName());
                editor.putString("userEmail", user.getEmail());
                editor.putString("userPhoto", user.getProfilePicture());
                editor.putBoolean("isGoogle", true);
                editor.apply();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());

            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                runAlertFail("Sign in", "Please try again later.");
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            activity.ShowDialog(SignIn.this);
                            checkIfUserExists();
                            activity.DismissDialog();

                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                            Intent intent = new Intent(SignIn.this, MainActivity.class);
                            intent.putExtra("NAME", user.getDisplayName());
                            intent.putExtra("PHOTO", user.getPhotoUrl().toString());
                            intent.putExtra("GOOGLE", true);
                            startActivity(intent);
                        }
                    }
                });
    }

    @SuppressLint("SetTextI18n")
    public void runAlertFail(String topic, String subTopic) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(
                SignIn.this, R.style.BottomSheetDialogTheme
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
                SignIn.this, R.style.BottomSheetDialogTheme
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
                Intent intent = new Intent(SignIn.this, MainActivity.class);
                intent.putExtra("GOOGLE", false);
                startActivity(intent);
            }
        });
        bottomSheetDialog.setCancelable(false);
        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if(user != null) {
            startActivity(new Intent(SignIn.this, MainActivity.class));
            finish();
        }
    }

    private void requestGoogleSignIn() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void signIn() {
        mGoogleSignInClient.signOut();
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
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

    public void init() {
        blurView = findViewById(R.id.sign_in_blurView);
        signUp = findViewById(R.id.sign_in_text_sign_up);
        forgotPassword = findViewById(R.id.sign_in_text_forgot_password);
        signInGoogle = findViewById(R.id.sign_in_google);
        email = findViewById(R.id.sign_in_email_txt_field);
        password = findViewById(R.id.sign_in_password_txt_field);
        signIn = findViewById(R.id.sign_in_continue);
        emailText = findViewById(R.id.sign_in_email_txt);
        passwordText = findViewById(R.id.sign_in_password_txt);
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