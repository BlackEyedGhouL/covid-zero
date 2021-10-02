package com.myhealthplusplus.app;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class News_CustomViewHolder extends RecyclerView.ViewHolder {

    TextView text_title, text_source;
    ImageView img_headline;
    CardView cardView;

    public News_CustomViewHolder(@NonNull View itemView) {
        super(itemView);

        text_title = itemView.findViewById(R.id.news_title);
        text_source = itemView.findViewById(R.id.news_source);
        img_headline = itemView.findViewById(R.id.news_headline_img);
        cardView = itemView.findViewById(R.id.news_headlines_card);

    }
}
