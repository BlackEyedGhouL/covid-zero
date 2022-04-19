package com.myhealthplusplus.app.CheckIn;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.myhealthplusplus.app.Adapters.FrequentGuestsAdapter;
import com.myhealthplusplus.app.MainActivity;
import com.myhealthplusplus.app.Models.Guest;
import com.myhealthplusplus.app.R;

import java.util.ArrayList;
import java.util.List;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class CheckInFrequentGuests extends AppCompatActivity {

    ImageView back;
    FloatingActionButton add;
    RecyclerView recyclerView;
    FrequentGuestsAdapter frequentGuestsAdapter;
    DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
    SearchView searchView;
    FirebaseUser user;
    SwipeRefreshLayout swipeRefreshLayout;
    private final MainActivity activity = new MainActivity();
    ArrayList<Guest> guestArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_in_frequent_guests);
        getWindow().setStatusBarColor(ContextCompat.getColor(CheckInFrequentGuests.this, R.color.dark_black));

        init();

        user = FirebaseAuth.getInstance().getCurrentUser();

        activity.ShowDialog(this);
        getGuestsFromDatabase();
        activity.DismissDialog();

        int refreshCycleColor = Color.parseColor("#c01722");
        swipeRefreshLayout.setColorSchemeColors(refreshCycleColor);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onRefresh() {
                guestArrayList.clear();
                frequentGuestsAdapter.notifyDataSetChanged();
                activity.ShowDialog(CheckInFrequentGuests.this);
                getGuestsFromDatabase();
                activity.DismissDialog();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CheckInFrequentGuests.this, CheckInCreateNewGuest.class);
                startActivity(intent);
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
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

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            int position = viewHolder.getAdapterPosition();

            switch (direction) {
                case ItemTouchHelper.LEFT:
                    String id = guestArrayList.get(position).getId();
                    guestArrayList.remove(position);
                    deleteGuestFromDatabase(id);
                    frequentGuestsAdapter.notifyItemRemoved(position);
                    break;

                case ItemTouchHelper.RIGHT:
                    String phoneNumber = guestArrayList.get(position).getPhoneNumber();
                    Intent call_intent = new Intent(Intent.ACTION_DIAL);
                    call_intent.setData(Uri.parse("tel:" + phoneNumber));
                    startActivity(call_intent);
                    frequentGuestsAdapter.notifyItemChanged(position);
                    break;
            }
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

            new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addSwipeRightBackgroundColor(ContextCompat.getColor(CheckInFrequentGuests.this, R.color.dark_green))
                    .addSwipeRightActionIcon(R.drawable.call)
                    .addSwipeLeftBackgroundColor(ContextCompat.getColor(CheckInFrequentGuests.this, R.color.dark_red))
                    .addSwipeLeftActionIcon(R.drawable.delete_alert)
                    .create()
                    .decorate();

            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };

    private void deleteGuestFromDatabase(String id) {
        DatabaseReference eventRef = rootRef
                .child("frequentGuests").child(user.getUid()).child(id);
        eventRef.removeValue();
        Toast.makeText(CheckInFrequentGuests.this, "Deleted", Toast.LENGTH_SHORT).show();
    }

    private void filter(String newText) {
        List<Guest> filteredList = new ArrayList<>();
        for(Guest guest : guestArrayList) {
            if(guest.getFirstName().toLowerCase().contains(newText.toLowerCase())) {
                filteredList.add(guest);
            }
        }
        frequentGuestsAdapter.filterList(filteredList);
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

                frequentGuestsAdapter = new FrequentGuestsAdapter(guestArrayList);
                recyclerView.setAdapter(frequentGuestsAdapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        };
        eventRef.addListenerForSingleValueEvent(eventListener);
    }

    private void init() {
        back = findViewById(R.id.check_in_frequent_guests_back);
        add = findViewById(R.id.check_in_frequent_guests_fab);
        recyclerView = findViewById(R.id.check_in_frequent_guests_recycler_view);
        searchView = findViewById(R.id.check_in_frequent_guests_search_view);
        swipeRefreshLayout = findViewById(R.id.check_in_frequent_guests_swipe_ref);
    }
}