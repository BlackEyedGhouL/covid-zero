package com.myhealthplusplus.app.CheckIn;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.myhealthplusplus.app.Adapters.CheckInHistoryAdapter;
import com.myhealthplusplus.app.MainActivity;
import com.myhealthplusplus.app.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CheckInHistory extends AppCompatActivity {

    ImageView back;
    DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
    FirebaseUser user;
    TextView todayEmpty, yesterdayEmpty, olderEmpty;
    private final MainActivity activity = new MainActivity();
    ArrayList<com.myhealthplusplus.app.Models.CheckInHistory> historyListToday = new ArrayList<>();
    ArrayList<com.myhealthplusplus.app.Models.CheckInHistory> historyListYesterday = new ArrayList<>();
    ArrayList<com.myhealthplusplus.app.Models.CheckInHistory> historyListOlder = new ArrayList<>();
    CheckInHistoryAdapter checkInHistoryAdapterToday, checkInHistoryAdapterYesterday, checkInHistoryAdapterOlder;
    RecyclerView recyclerViewToday, recyclerViewYesterday, recyclerViewOlder;
    List<String> guestsList;
    SwipeRefreshLayout swipeRefreshLayout;
    Date today, yesterday;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_in_history);
        getWindow().setStatusBarColor(ContextCompat.getColor(CheckInHistory.this, R.color.dark_black));

        init();

        user = FirebaseAuth.getInstance().getCurrentUser();
        guestsList = new ArrayList<>();

        today = Calendar.getInstance().getTime();
        yesterday = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(yesterday);
        c.add(Calendar.DATE, -1);
        yesterday = c.getTime();

        activity.ShowDialog(this);
        getCheckInHistoryFromDatabase(today, yesterday);
        activity.DismissDialog();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        int refreshCycleColor = Color.parseColor("#c01722");
        swipeRefreshLayout.setColorSchemeColors(refreshCycleColor);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onRefresh() {
                todayEmpty.setVisibility(View.VISIBLE);
                yesterdayEmpty.setVisibility(View.VISIBLE);
                olderEmpty.setVisibility(View.VISIBLE);
                historyListToday.clear();
                historyListYesterday.clear();
                historyListOlder.clear();
                checkInHistoryAdapterToday.notifyDataSetChanged();
                checkInHistoryAdapterYesterday.notifyDataSetChanged();
                checkInHistoryAdapterOlder.notifyDataSetChanged();
                activity.ShowDialog(CheckInHistory.this);
                getCheckInHistoryFromDatabase(today, yesterday);
                activity.DismissDialog();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    @SuppressLint("SimpleDateFormat")
    private void getCheckInHistoryFromDatabase(Date t, Date y) {
        DatabaseReference eventRef = rootRef
                .child("checkInHistory").child(user.getUid());

        DateFormat df1 = new SimpleDateFormat("d MMM yyyy 'at' h:mm a");
        DateFormat df2 = new SimpleDateFormat("d MMM yyyy");

        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {

                    String date = ds
                            .child("date")
                            .getValue().toString();

                    String location = ds
                            .child("location")
                            .getValue().toString();

                    try {
                        Date formattedDate = df1.parse(date);
                        String formattedToday = df2.format(t);
                        String formattedYesterday = df2.format(y);
                        String formattedDate2 = df2.format(formattedDate);

                        if (formattedDate2.equals(formattedToday)) {
                            historyListToday.add(new com.myhealthplusplus.app.Models.CheckInHistory(date, location, guestsList));
                        } else if (formattedDate2.equals(formattedYesterday)) {
                            historyListYesterday.add(new com.myhealthplusplus.app.Models.CheckInHistory(date, location, guestsList));
                        } else {
                            historyListOlder.add(new com.myhealthplusplus.app.Models.CheckInHistory(date, location, guestsList));
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }

                if (!historyListToday.isEmpty()) {
                    todayEmpty.setVisibility(View.GONE);
                }

                if (!historyListYesterday.isEmpty()) {
                    yesterdayEmpty.setVisibility(View.GONE);
                }

                if (!historyListOlder.isEmpty()) {
                    olderEmpty.setVisibility(View.GONE);
                }

                checkInHistoryAdapterToday = new CheckInHistoryAdapter(historyListToday);
                checkInHistoryAdapterYesterday = new CheckInHistoryAdapter(historyListYesterday);
                checkInHistoryAdapterOlder = new CheckInHistoryAdapter(historyListOlder);

                recyclerViewToday.setAdapter(checkInHistoryAdapterToday);
                recyclerViewYesterday.setAdapter(checkInHistoryAdapterYesterday);
                recyclerViewOlder.setAdapter(checkInHistoryAdapterOlder);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        };
        eventRef.addListenerForSingleValueEvent(eventListener);
    }

    private void init() {
        back = findViewById(R.id.check_in_history_back);
        recyclerViewToday = findViewById(R.id.check_in_history_today_recycler_view);
        recyclerViewYesterday = findViewById(R.id.check_in_history_yesterday_recycler_view);
        recyclerViewOlder = findViewById(R.id.check_in_history_older_recycler_view);
        swipeRefreshLayout = findViewById(R.id.check_in_history_swipe_ref);
        todayEmpty = findViewById(R.id.check_in_history_today_empty);
        yesterdayEmpty = findViewById(R.id.check_in_history_yesterday_empty);
        olderEmpty = findViewById(R.id.check_in_history_older_empty);
    }
}