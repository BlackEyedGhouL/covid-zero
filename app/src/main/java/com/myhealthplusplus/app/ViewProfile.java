package com.myhealthplusplus.app;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.InsetDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.myhealthplusplus.app.Status.Data;

import java.util.HashMap;

public class ViewProfile extends AppCompatActivity {

    Spinner spinner;
    CardView profile_color_ring, editProfile;
    CardView editNameCard;
    TextView editName, editEmail;
    TextInputLayout firstName, lastName, password;
    ImageView back, profile_picture, name_next_icon;
    public static String FName, LName, PWord;
    boolean isGoogle = false;
    StorageReference storageReference;
    private final MainActivity activity = new MainActivity();
    FirebaseUser user;
    FirebaseDatabase db;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);
        getWindow().setStatusBarColor(ContextCompat.getColor(ViewProfile.this, R.color.dark_black));

        init();
        user = FirebaseAuth.getInstance().getCurrentUser();
        getDataFromSharedPreferences();
        storageReference = FirebaseStorage.getInstance().getReference();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        editNameCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!isGoogle) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ViewProfile.this);
                    LayoutInflater inflater = (LayoutInflater) ViewProfile.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View v = inflater.inflate(R.layout.edit_profile_name_window_p1, findViewById(R.id.edit_profile_name_window_p1_layout));
                    TextView btnReview = v.findViewById(R.id.editProfileName_review_btn);
                    TextView btnP1Cancel = v.findViewById(R.id.editProfileName_cancel_p1_btn);
                    builder.setCancelable(true);
                    builder.setView(v);

                    final Dialog dialog = builder.create();
                    dialog.getWindow().setWindowAnimations(R.style.DialogAnimation);

                    btnReview.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            firstName = v.findViewById(R.id.editProfileName_firstName_txtLayout);
                            lastName = v.findViewById(R.id.editProfileName_LastName_txtLayout);
                            password = v.findViewById(R.id.editProfileName_password_txtLayout);
                            FName = firstName.getEditText().getText().toString();
                            LName = lastName.getEditText().getText().toString();

                            if (!validateFirstName() | !validateLastName()) {
                                return;
                            }

                            Toast.makeText(ViewProfile.this, "hello", Toast.LENGTH_SHORT).show();

                            AlertDialog.Builder builder1 = new AlertDialog.Builder(ViewProfile.this);
                            LayoutInflater inflater1 = (LayoutInflater) ViewProfile.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                            View v1 = inflater1.inflate(R.layout.edit_profile_name_window_p2, findViewById(R.id.edit_profile_name_window_p2_layout));
                            TextView btnChange = v1.findViewById(R.id.editProfileName_save_btn);
                            TextView btnP2Cancel = v1.findViewById(R.id.editProfileName_cancel_p2_btn);
                            builder1.setCancelable(true);
                            builder1.setView(v1);

                            final Dialog dialog1 = builder1.create();
                            dialog1.getWindow().setWindowAnimations(R.style.DialogAnimation);

                            btnChange.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    PWord = password.getEditText().getText().toString();
                                }
                            });

                            btnP2Cancel.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    dialog1.dismiss();
                                }
                            });

                            dialog1.show();
                        }
                    });

                    btnP1Cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    });

                    dialog.show();
                }
            }
        });

        StatusAdapter adapter = new StatusAdapter(ViewProfile.this, Data.getStatusList());
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if (adapterView.getItemAtPosition(i).equals(0)) {
                    profile_color_ring.setCardBackgroundColor(Color.parseColor("#FFCA28"));

                    if (Build.VERSION.SDK_INT >= 28) {
                        profile_color_ring.setOutlineAmbientShadowColor(Color.parseColor("#FFCA28"));
                        profile_color_ring.setOutlineSpotShadowColor(Color.parseColor("#FFCA28"));
                    }
                } else if (adapterView.getItemAtPosition(i).equals(1)) {
                    profile_color_ring.setCardBackgroundColor(Color.parseColor("#4CAF50"));

                    if (Build.VERSION.SDK_INT >= 28) {
                        profile_color_ring.setOutlineAmbientShadowColor(Color.parseColor("#4CAF50"));
                        profile_color_ring.setOutlineSpotShadowColor(Color.parseColor("#4CAF50"));
                    }
                } else {
                    profile_color_ring.setCardBackgroundColor(Color.parseColor("#F44336"));

                    if (Build.VERSION.SDK_INT >= 28) {
                        profile_color_ring.setOutlineAmbientShadowColor(Color.parseColor("#F44336"));
                        profile_color_ring.setOutlineSpotShadowColor(Color.parseColor("#F44336"));
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent openGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(openGallery, 1000);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1000) {
            if (resultCode == Activity.RESULT_OK) {
                Uri imageUri = data.getData();

                activity.ShowDialog(ViewProfile.this);
                uploadImageToFirebase(imageUri);
            }
        }
    }

    private void uploadImageToFirebase(Uri imageUri) {
        StorageReference fileRef = storageReference.child("user_profile_pictures/" + user.getUid() + "/profile_picture.jpg");
        fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        addDataToFirebase(uri);
                    }
                });
                activity.DismissDialog();
                runAlertSuccess("Change profile picture", "Changed successfully.");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                activity.DismissDialog();
                runAlertFail("Change profile picture", "Please try again later.");
            }
        });
    }

    private void addDataToFirebase(Uri uri) {

        HashMap uHash = new HashMap();
        uHash.put("profilePicture", uri.toString());

        db = FirebaseDatabase.getInstance();
        reference = db.getReference("users");
        reference.child(user.getUid()).updateChildren(uHash).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()) {
                    SharedPreferences.Editor editor = getApplicationContext()
                            .getSharedPreferences("MyPrefs", MODE_PRIVATE)
                            .edit();
                    editor.putString("userPhoto", uri.toString());
                    editor.apply();

                    Glide.with(ViewProfile.this).load(uri).into(profile_picture);
                }
            }
        });


    }

    private void getDataFromSharedPreferences() {
        SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);

        String fullName = preferences.getString("fullName", "");
        String email = preferences.getString("userEmail", "");
        String profilePicture = preferences.getString("userPhoto", "");
        isGoogle = preferences.getBoolean("isGoogle", false);

        editName.setText(fullName);
        editEmail.setText(email);
        Glide.with(this).load(profilePicture).into(profile_picture);

        if(isGoogle) {
            name_next_icon.setVisibility(View.INVISIBLE);
            editNameCard.setClickable(false);
            editNameCard.setFocusable(false);
            editNameCard.setForeground(null);
            editProfile.setVisibility(View.INVISIBLE);
            editProfile.setClickable(false);
            editProfile.setFocusable(false);
            editProfile.setForeground(null);
        }
    }

    private boolean validateLastName() {
        String val = lastName.getEditText().getText().toString();
        String checkForLetters = "[a-zA-Z]+";
        String noWhiteSpace = "\\A\\w{1,20}\\z";

        if (val.isEmpty()) {
            lastName.setError("Field can not be empty");
            return false;
        } else if (!val.matches(noWhiteSpace)) {
            lastName.setError("No white spaces are allowed!");
            return false;
        } else if (!val.matches(checkForLetters)) {
            lastName.setError("Invalid last name!");
            return false;
        } else {
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
        } else if (!val.matches(noWhiteSpace)) {
            firstName.setError("No white spaces are allowed!");
            return false;
        } else if (!val.matches(checkForLetters)) {
            firstName.setError("Invalid first name!");
            return false;
        } else {
            firstName.setError(null);
            firstName.setErrorEnabled(false);
            return true;
        }
    }

    @SuppressLint("SetTextI18n")
    public void runAlertFail(String topic, String subTopic) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(
                ViewProfile.this, R.style.BottomSheetDialogTheme
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
                ViewProfile.this, R.style.BottomSheetDialogTheme
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
            }
        });
        bottomSheetDialog.setCancelable(true);
        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
    }

    private void init() {
        spinner = findViewById(R.id.profile_spinner_status);
        profile_color_ring = findViewById(R.id.profile_color_ring);
        editNameCard = findViewById(R.id.profile_editName_card);
        editName = findViewById(R.id.profile_editName);
        editEmail = findViewById(R.id.profile_editEmail);
        back = findViewById(R.id.profile_back);
        profile_picture = findViewById(R.id.profile_picture);
        name_next_icon = findViewById(R.id.profile_editName_next_icon);
        editProfile = findViewById(R.id.profile_pictureEdit_ring);
    }
}