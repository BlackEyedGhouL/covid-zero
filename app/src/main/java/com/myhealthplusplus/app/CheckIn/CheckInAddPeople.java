package com.myhealthplusplus.app.CheckIn;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
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
import com.myhealthplusplus.app.Models.PlacesCheckInUser;
import com.myhealthplusplus.app.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
    List<String> selectedList;
    GuestsAdapter guestsAdapter;
    DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
    RecyclerView recyclerViewGuests;
    DatabaseReference placesRef;
    LottieAnimationView lottieAnimationView;
    String nameIntent, locationIntent;

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
                getData();

                if (listSwitch.isChecked()) {
                    PlacesCheckInUser placesCheckInUser = new PlacesCheckInUser(getIntent().getStringExtra("TIME"), selectedList);
                    setCheckInDataInDatabase(placesCheckInUser);
                    setCheckInBusinessDataInDatabase(placesCheckInUser);
                } else {
                    selectedList.clear();
                    selectedList.add("");
                    PlacesCheckInUser placesCheckInUser = new PlacesCheckInUser(getIntent().getStringExtra("TIME"), selectedList);
                    setCheckInDataInDatabase(placesCheckInUser);
                    setCheckInBusinessDataInDatabase(placesCheckInUser);
                }

                Intent intent = new Intent(CheckInAddPeople.this, CheckInFinish.class);
                intent.putExtra("NAME", nameIntent);
                intent.putExtra("LOCATION", locationIntent);
                startActivity(intent);
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
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    getGuestsFromDatabase();
                    guestsList.setVisibility(View.VISIBLE);
                    searchView.setInputType(InputType.TYPE_CLASS_TEXT);
                    searchView.setQueryHint("Search guests");
                    lottieAnimationView.setVisibility(View.GONE);
                } else {
                    guestArrayList.clear();
                    guestsAdapter.notifyDataSetChanged();
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

    private void setCheckInDataInDatabase(PlacesCheckInUser placesCheckInUser) {
        placesRef = FirebaseDatabase.getInstance().getReference().child("checkInPlacesUsers").child(user.getUid()).child(getIntent().getStringExtra("CODE"));
        placesRef.child(getIntent().getStringExtra("TIME")).setValue(placesCheckInUser);
    }

    private void setCheckInBusinessDataInDatabase(PlacesCheckInUser placesCheckInUser) {
        @SuppressLint("SimpleDateFormat")
        DateFormat df = new SimpleDateFormat("d MMM yyyy");
        String date = df.format(Calendar.getInstance().getTime());
        placesRef = FirebaseDatabase.getInstance().getReference().child("checkInBusinesses").child(getIntent().getStringExtra("CODE")).child("visitors").child(date).child(user.getUid());
        placesRef.child(getIntent().getStringExtra("TIME")).setValue(placesCheckInUser);
    }

    public void getData(){
        selectedList = guestsAdapter.listOfSelectedGuests();
        Log.d("Selected list: ", selectedList.toString()) ;}

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

                    String id = ds
                            .child("id")
                            .getValue().toString();

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

                    guestArrayList.add(new Guest(id, addedDate, email, firstName, lastName, phoneNumber));
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

                locationIntent = nameT;
                location.setText(locationIntent);
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

                nameIntent = getFirstWord(nameT);
                name.setText(nameIntent + ", you're checking into");
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