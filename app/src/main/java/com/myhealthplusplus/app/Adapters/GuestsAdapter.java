package com.myhealthplusplus.app.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.myhealthplusplus.app.Models.Guest;
import com.myhealthplusplus.app.R;

import java.util.List;

public class GuestsAdapter extends RecyclerView.Adapter<GuestsAdapter.ViewHolder> {

    List<Guest> guestList;
    RecyclerView recyclerView;
    Context context;
    final View.OnClickListener onClickListener = new MyOnClickListener();

    public GuestsAdapter(List<Guest> guestList, RecyclerView recyclerView, Context context) {
        this.guestList = guestList;
        this.recyclerView = recyclerView;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.guest_row, parent, false);
        view.setOnClickListener(onClickListener);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Guest guest = guestList.get(position);
        String firstName = guest.getFirstName();
        String lastName = guest.getLastName();

        holder.name.setText(firstName+" "+lastName);
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

        CheckBox name;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.guest_row_name);
        }
    }

    private class MyOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            int itemPosition = recyclerView.getChildLayoutPosition(view);


        }
    }
}
