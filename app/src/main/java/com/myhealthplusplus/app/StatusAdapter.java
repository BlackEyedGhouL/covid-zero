package com.myhealthplusplus.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.myhealthplusplus.app.Status.Status;

import java.util.List;

public class StatusAdapter extends BaseAdapter {

    private Context context;
    private List<Status> statusList;

    public StatusAdapter(Context context, List<Status> statusList) {
        this.context = context;
        this.statusList = statusList;
    }

    @Override
    public int getCount() {
        return statusList != null ? statusList.size() : 0;
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View rootView = LayoutInflater.from(context)
                .inflate(R.layout.item_status, viewGroup, false);

        TextView txtName = rootView.findViewById(R.id.spinner_status_name);
        ImageView image = rootView.findViewById(R.id.spinner_status_image);

        txtName.setText(statusList.get(i).getName());
        image.setImageResource(statusList.get(i).getImage());

        return rootView;
    }
}
