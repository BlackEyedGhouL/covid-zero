package com.myhealthplusplus.app.CheckIn;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.myhealthplusplus.app.Adapters.GuestsAdapter;
import com.myhealthplusplus.app.MainActivity;
import com.myhealthplusplus.app.Models.Guest;
import com.myhealthplusplus.app.R;

import java.util.ArrayList;
import java.util.List;

public class CheckInAddPeople extends AppCompatActivity {

    ImageView back;
    LinearLayout guestsList;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    Switch listSwitch;
    TextView name, location, checkIn;
    SearchView searchView;
    private final MainActivity activity = new MainActivity();
    FirebaseUser user;
    ArrayList<Guest> guestArrayList = new ArrayList<>();
    GuestsAdapter guestsAdapter;
    DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
    RecyclerView recyclerViewGuests;
    LottieAnimationView lottieAnimationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_in_add_people);
        getWindow().setStatusBarColor(ContextCompat.getColor(CheckInAddPeople.this, R.color.dark_black));

        init();

        guestsList.setVisibility(View.GONE);
        searchView.setInputType(InputType.TYPE_NULL);

        user = FirebaseAuth.getInstance().getCurrentUser();

        activity.ShowDialog(this);

        getUserNameFromDatabase();
        getLocationFromDatabase();
        getGuestsFromDatabase();

        activity.DismissDialog();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        checkIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return true;
            }
        });

        listSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    guestsList.setVisibility(View.VISIBLE);
                    searchView.setInputType(InputType.TYPE_CLASS_TEXT);
                    searchView.setQueryHint("Search guests");
                    lottieAnimationView.setVisibility(View.GONE);
                } else {
                    guestsList.setVisibility(View.GONE);
                    searchView.setQuery("", false);
                    searchView.clearFocus();
                    searchView.setInputType(InputType.TYPE_NULL);
                    searchView.setQueryHint("Check in additional people");
                    lottieAnimationView.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void filter(String newText) {
        List<Guest> filteredList = new ArrayList<>();
        for(Guest guest : guestArrayList) {
            if(guest.getFirstName().toLowerCase().contains(newText.toLowerCase())) {
                filteredList.add(guest);
            }
        }
        guestsAdapter.filterList(filteredList);
    }

    private void getGuestsFromDatabase() {
        DatabaseReference eventRef = rootRef
                .child("frequentGuests").child(user.getUid());

        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {

                    String firstName = ds
                            .child("firstName")
                            .getValue().toString();

                    String email = ds
                            .child("email")
                            .getValue().toString();

                    String addedDate = ds
                            .child("addedDate")
                            .getValue().toString();

                    String lastName = ds
                            .child("lastName")
                            .getValue().toString();

                    String phoneNumber = ds
                            .child("phoneNumber")
                            .getValue().toString();

                    guestArrayList.add(new Guest(addedDate, email, firstName, lastName, phoneNumber));
                }

                guestsAdapter = new GuestsAdapter(guestArrayList, recyclerViewGuests, CheckInAddPeople.this);
                recyclerViewGuests.setAdapter(guestsAdapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        };
        eventRef.addListenerForSingleValueEvent(eventListener);
    }

    private void getLocationFromDatabase() {
        rootRef.child("checkInBusinesses").child(getIntent().getStringExtra("CODE")).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String nameT = snapshot
                        .child("name")
                        .getValue().toString();

                location.setText(nameT);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void getUserNameFromDatabase() {
        rootRef.child("users").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String nameT = snapshot
                        .child("name")
                        .getValue().toString();

                name.setText(getFirstWord(nameT)+", you're checking into");
            }

            private String getFirstWord(String text) {
                int index = text.indexOf(' ');
                if (index > -1) {
                    return text.substring(0, index).trim();
                } else {
                    return text;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void init() {
        back = findViewById(R.id.check_in_add_people_back);
        guestsList = findViewById(R.id.check_in_add_people_frequent_guests_list);
        searchView = findViewById(R.id.check_in_add_people_search);
        listSwitch = findViewById(R.id.check_in_add_people_switch);
        name = findViewById(R.id.check_in_add_people_checking_into);
        location = findViewById(R.id.check_in_add_people_location);
        recyclerViewGuests = findViewById(R.id.check_in_add_people_recycler_view);
        checkIn = findViewById(R.id.check_in_add_people_check_in_now);
        lottieAnimationView = findViewById(R.id.check_in_add_people_lottieAnimationView);
    }
}