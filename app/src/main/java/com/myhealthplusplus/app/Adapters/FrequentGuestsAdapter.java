package com.myhealthplusplus.app.Adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.myhealthplusplus.app.Models.Guest;
import com.myhealthplusplus.app.R;

import java.util.List;

public class FrequentGuestsAdapter extends RecyclerView.Adapter<FrequentGuestsAdapter.ViewHolder> {

    List<Guest> guestList;

    public FrequentGuestsAdapter(List<Guest> guestList) {
        this.guestList = guestList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.frequent_guests_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Guest guest = guestList.get(position);
        String firstName = guest.getFirstName();
        String lastName = guest.getLastName();
        String mail = guest.getEmail();
        String phone = guest.getPhoneNumber();

        holder.name.setText(firstName+" "+lastName);

        if (mail.isEmpty()) {
            holder.mail.setText("-");
        } else {
            holder.mail.setText(mail);
        }

        if (phone.isEmpty()) {
            holder.phone.setText("-");
        } else {
            holder.phone.setText(phone);
        }
    }

    @Override
    public int getItemCount() {
        return guestList.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void filterList(List<Guest> filteredList) {
        guestList = filteredList;
        notifyDataSetChanged();
    }

    class  ViewHolder extends RecyclerView.ViewHolder {

        TextView name, mail, phone;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.frequent_guests_row_name);
            mail = itemView.findViewById(R.id.frequent_guests_row_mail);
            phone = itemView.findViewById(R.id.frequent_guests_row_phone);
        }
    }
}
