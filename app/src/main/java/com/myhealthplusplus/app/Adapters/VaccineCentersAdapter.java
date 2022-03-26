package com.myhealthplusplus.app.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.myhealthplusplus.app.Models.VaccineCenter;
import com.myhealthplusplus.app.R;

import java.util.List;
import java.util.Locale;

public class VaccineCentersAdapter extends RecyclerView.Adapter<VaccineCentersAdapter.ViewHolder>{

    Context context;
    List<VaccineCenter> vaccineCenterList;
    RecyclerView recyclerView;
    final View.OnClickListener onClickListener = new MyOnClickListener();

    public VaccineCentersAdapter(Context context, List<VaccineCenter> vaccineCenterList, RecyclerView recyclerView) {
        this.context = context;
        this.vaccineCenterList = vaccineCenterList;
        this.recyclerView = recyclerView;
    }

    @NonNull
    @Override
    public VaccineCentersAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.vaccine_centers_row, parent, false);
        view.setOnClickListener(onClickListener);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VaccineCentersAdapter.ViewHolder holder, int position) {
        VaccineCenter vaccineCenter = vaccineCenterList.get(position);
        holder.rowCenterName.setText(vaccineCenter.getCenter());
        holder.rowDistrict.setText(vaccineCenter.getDistrict());
    }

    @Override
    public int getItemCount() {
        return vaccineCenterList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView rowCenterName;
        TextView rowDistrict;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            rowCenterName = itemView.findViewById(R.id.vcl_center_name);
            rowDistrict = itemView.findViewById(R.id.vcl_district);
        }
    }

    private class MyOnClickListener implements View.OnClickListener {
        @SuppressLint("SetTextI18n")
        @Override
        public void onClick(View view) {
            int itemPosition = recyclerView.getChildLayoutPosition(view);

            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(
                    context, R.style.BottomSheetDialogTheme
            );
            View bottomSheetView = LayoutInflater.from(context.getApplicationContext())
                    .inflate(
                            R.layout.vaccine_centers_details_alert_box,
                            bottomSheetDialog.findViewById(R.id.vcl_alert_box)
                    );

            TextView txtCenter = bottomSheetView.findViewById(R.id.vcl_alert_center_name_txt);
            txtCenter.setText(vaccineCenterList.get(itemPosition).getCenter());
            TextView txtDistrict = bottomSheetView.findViewById(R.id.vcl_alert_district_txt);
            txtDistrict.setText("District : "+vaccineCenterList.get(itemPosition).getDistrict());
            TextView txtPoliceArea = bottomSheetView.findViewById(R.id.vcl_alert_police_area_txt);
            txtPoliceArea.setText("Police Area : "+vaccineCenterList.get(itemPosition).getPolice_area());

            bottomSheetView.findViewById(R.id.vcl_alert_dash).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Double lat = Double.parseDouble(vaccineCenterList.get(itemPosition).getLat());
                    Double lon = Double.parseDouble(vaccineCenterList.get(itemPosition).getLon());
                    String flag = vaccineCenterList.get(itemPosition).getCenter();

                    String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?daddr=%f,%f (%s)", lat, lon, flag);
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                    intent.setPackage("com.google.android.apps.maps");
                    context.startActivity(intent);

                    bottomSheetDialog.dismiss();
                }
            });

            bottomSheetDialog.setCancelable(true);
            bottomSheetDialog.setContentView(bottomSheetView);
            bottomSheetDialog.show();
        }
    }
}
