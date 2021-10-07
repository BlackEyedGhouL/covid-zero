package com.myhealthplusplus.app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.myhealthplusplus.app.Models.Sick_Rows;

import java.util.List;

import okhttp3.internal.Version;

public class Sick_Rows_Adapter extends RecyclerView.Adapter<Sick_Rows_Adapter.Sick_Rows_VH> {

    List<Sick_Rows> sick_rows;

    public Sick_Rows_Adapter(List<Sick_Rows> sick_rows) {
        this.sick_rows = sick_rows;
    }

    @NonNull
    @Override
    public Sick_Rows_VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sick_row, parent, false);
        return new Sick_Rows_VH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Sick_Rows_VH holder, int position) {

        Sick_Rows sickRows = sick_rows.get(position);
        holder.topicTxt.setText(sickRows.getTopic());
        holder.contentTxt.setText(sickRows.getContent());

        boolean isExpandable = sick_rows.get(position).isExpandable();
        holder.expandableLayout.setVisibility(isExpandable ? View.VISIBLE : View.GONE);
    }

    @Override
    public int getItemCount() {
        return sick_rows.size();
    }

    public class Sick_Rows_VH extends RecyclerView.ViewHolder {

        TextView topicTxt, contentTxt;
        LinearLayout linearLayout;
        RelativeLayout expandableLayout;

        public Sick_Rows_VH(@NonNull View itemView) {
            super(itemView);

            topicTxt = itemView.findViewById(R.id.sick_topic);
            contentTxt = itemView.findViewById(R.id.sick_content);
            linearLayout = itemView.findViewById(R.id.sick_linear_layout);
            expandableLayout = itemView.findViewById(R.id.sick_expandable_layout);

            linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Sick_Rows sickRows = sick_rows.get(getAdapterPosition());
                    sickRows.setExpandable(!sickRows.isExpandable());
                    notifyItemChanged(getAdapterPosition());
                }
            });
        }
    }
}
