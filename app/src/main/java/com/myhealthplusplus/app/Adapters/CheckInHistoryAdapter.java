package com.myhealthplusplus.app.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.myhealthplusplus.app.Models.CheckInHistory;
import com.myhealthplusplus.app.R;

import java.util.List;

public class CheckInHistoryAdapter extends RecyclerView.Adapter<CheckInHistoryAdapter.ViewHolder> {

    List<CheckInHistory> historyList;
    DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String guestName;

    public CheckInHistoryAdapter(List<CheckInHistory> historyList) {
        this.historyList = historyList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.check_in_history_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CheckInHistory checkInHistory = historyList.get(position);
        String location = checkInHistory.getLocation();
        String date = checkInHistory.getDate();

        getLocationFromDatabase(location, holder);
        holder.dateAndGuests.setText(date);
    }

    private void getLocationFromDatabase(String code, ViewHolder holder) {
        rootRef.child("checkInBusinesses").child(code).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String nameT = snapshot
                        .child("name")
                        .getValue().toString();

                holder.location.setText(nameT);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    class  ViewHolder extends RecyclerView.ViewHolder {

        TextView location, dateAndGuests;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            location = itemView.findViewById(R.id.check_in_history_row_location);
            dateAndGuests = itemView.findViewById(R.id.check_in_history_row_date_and_guests);
        }
    }
}
