package com.myhealthplusplus.app.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.myhealthplusplus.app.Models.VaccinationToken;
import com.myhealthplusplus.app.R;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class VaccinationTokenAdapter extends RecyclerView.Adapter<VaccinationTokenAdapter.ViewHolder> {

    List<VaccinationToken> vaccinationTokenList;
    RecyclerView recyclerView;
    final View.OnClickListener onClickListener = new MyOnClickListener();

    public VaccinationTokenAdapter(List<VaccinationToken> vaccinationTokenList, RecyclerView recyclerView) {
        this.vaccinationTokenList = vaccinationTokenList;
        this.recyclerView = recyclerView;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.vaccination_token_row, parent, false);
        view.setOnClickListener(onClickListener);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        VaccinationToken vaccinationToken = vaccinationTokenList.get(position);
        String valid = vaccinationToken.getValidDateT();
        String issued = vaccinationToken.getIssuedDateT();
        String nic = vaccinationToken.getNicT();
        boolean isUsed = vaccinationToken.isUsed();
        DateTimeFormatter userFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG);
        LocalDate issuedDate = LocalDate.parse(issued, userFormatter);

        holder.validDateText.setText("Valid until: "+valid);
        holder.issuedDateText.setText("Issued date: "+issued);
        holder.nicText.setText("Nic: "+nic);

        LocalDate today = LocalDate.now(ZoneId.of("Asia/Dhaka"));
        long weeks = ChronoUnit.WEEKS.between(issuedDate, today);

        if (isUsed) {
            holder.gradient.setBackgroundResource(R.drawable.vaccination_token_fade_gradient_top_red);
        } else if (weeks >= 2) {
            holder.gradient.setBackgroundResource(R.drawable.vaccination_token_fade_gradient_top_yellow);
        } else {
            holder.gradient.setBackgroundResource(R.drawable.vaccination_token_fade_gradient_top_green);
        }
    }

    @Override
    public int getItemCount() {
        return vaccinationTokenList.size();
    }

    class  ViewHolder extends RecyclerView.ViewHolder {

        TextView validDateText, issuedDateText, nicText;
        View gradient;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            validDateText = itemView.findViewById(R.id.vaccination_token_row_expired_date);
            issuedDateText = itemView.findViewById(R.id.vaccination_token_row_issued_date);
            nicText = itemView.findViewById(R.id.vaccination_token_row_nic);
            gradient = itemView.findViewById(R.id.vaccination_token_row_expired_gradient);
        }
    }

    private class MyOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            int itemPosition = recyclerView.getChildLayoutPosition(view);

            boolean isUsed = vaccinationTokenList.get(itemPosition).isUsed();
            String issued = vaccinationTokenList.get(itemPosition).getIssuedDateT();

            DateTimeFormatter userFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG);
            LocalDate issuedDate = LocalDate.parse(issued, userFormatter);
            LocalDate today = LocalDate.now(ZoneId.of("Asia/Dhaka"));
            long weeks = ChronoUnit.WEEKS.between(issuedDate, today);

            if (!isUsed && weeks < 2) {
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(
                        recyclerView.getContext(), R.style.BottomSheetDialogTheme
                );
                View bottomSheetView = LayoutInflater.from(recyclerView.getContext())
                        .inflate(
                                R.layout.vaccination_token_bottom_sheet,
                                bottomSheetDialog.findViewById(R.id.vaccination_token_bottom_sheet)
                        );

                bottomSheetView.findViewById(R.id.vaccination_token_bottom_sheet_view).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        bottomSheetDialog.dismiss();
                    }
                });

                bottomSheetView.findViewById(R.id.vaccination_token_bottom_sheet_save).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        bottomSheetDialog.dismiss();
                    }
                });

                bottomSheetDialog.setCancelable(true);
                bottomSheetDialog.setContentView(bottomSheetView);
                bottomSheetDialog.show();
            } else {
                Toast.makeText(recyclerView.getContext(), "Not accessible", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
