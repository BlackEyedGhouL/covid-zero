package com.myhealthplusplus.app.Adapters;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
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

import java.io.File;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class VaccinationTokenAdapter extends RecyclerView.Adapter<VaccinationTokenAdapter.ViewHolder> {

    List<VaccinationToken> vaccinationTokenList;
    RecyclerView recyclerView;
    Context context;
    final View.OnClickListener onClickListener = new MyOnClickListener();

    public VaccinationTokenAdapter(List<VaccinationToken> vaccinationTokenList, RecyclerView recyclerView, Context context) {
        this.vaccinationTokenList = vaccinationTokenList;
        this.recyclerView = recyclerView;
        this.context = context;
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

        holder.validDateText.setText("Valid: "+valid);
        holder.issuedDateText.setText("Issued: "+issued);
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
            String imageUrl = vaccinationTokenList.get(itemPosition).getTokenImageUrl();
            String nic = vaccinationTokenList.get(itemPosition).getNicT();

            DateTimeFormatter userFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG);
            LocalDate issuedDate = LocalDate.parse(issued, userFormatter);
            LocalDate today = LocalDate.now(ZoneId.of("Asia/Dhaka"));
            long weeks = ChronoUnit.WEEKS.between(issuedDate, today);

            if (!isUsed && weeks < 2) {
                if (!imageUrl.isEmpty()) {
                    BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(
                            context, R.style.BottomSheetDialogTheme
                    );
                    View bottomSheetView = LayoutInflater.from(context)
                            .inflate(
                                    R.layout.vaccination_token_bottom_sheet,
                                    bottomSheetDialog.findViewById(R.id.vaccination_token_bottom_sheet)
                            );

                    bottomSheetView.findViewById(R.id.vaccination_token_bottom_sheet_view).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent i = new Intent(Intent.ACTION_VIEW);
                            i.setData(Uri.parse(imageUrl));
                            context.startActivity(i);
                            bottomSheetDialog.dismiss();
                        }
                    });

                    bottomSheetView.findViewById(R.id.vaccination_token_bottom_sheet_save).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            try
                            {
                                DownloadManager downloadManager = null;
                                downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);

                                Uri downloadUri = Uri.parse(imageUrl);

                                DownloadManager.Request request = new DownloadManager.Request(downloadUri);

                                request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI
                                | DownloadManager.Request.NETWORK_MOBILE)
                                        .setAllowedOverRoaming(false)
                                        .setTitle("Vaccination_Digital_Token_"+nic)
                                        .setMimeType("image/jpeg")
                                        .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                                        .setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES, File.separator+"Vaccination_Digital_Token_"+nic+".jpg");

                                downloadManager.enqueue(request);

                                Toast.makeText(context, "Token downloaded", Toast.LENGTH_SHORT).show();
                                bottomSheetDialog.dismiss();
                            }
                            catch (Exception e)
                            {
                                e.printStackTrace();
                                Toast.makeText(context, "Token download failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    bottomSheetDialog.setCancelable(true);
                    bottomSheetDialog.setContentView(bottomSheetView);
                    bottomSheetDialog.show();
                } else {
                    Toast.makeText(context, "Token hasn't been saved previously", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(context, "Not accessible", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
