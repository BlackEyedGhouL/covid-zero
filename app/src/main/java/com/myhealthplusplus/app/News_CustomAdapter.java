package com.myhealthplusplus.app;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.myhealthplusplus.app.Models.NewsHeadlines;
import com.squareup.picasso.Picasso;

import java.util.List;

public class News_CustomAdapter extends RecyclerView.Adapter<News_CustomViewHolder> {

    private Context context;
    private List<NewsHeadlines> headlines;
    private News_SelectListener listener;

    public News_CustomAdapter(Context context, List<NewsHeadlines> headlines, News_SelectListener listener) {
        this.context = context;
        this.headlines = headlines;
        this.listener = listener;
    }

    @NonNull
    @Override
    public News_CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new News_CustomViewHolder(LayoutInflater.from(context).inflate(R.layout.news_headlines, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull News_CustomViewHolder holder, @SuppressLint("RecyclerView") int position) {

        holder.text_title.setText(headlines.get(position).getTitle());
        holder.text_source.setText(headlines.get(position).getSource().getName());

        if(headlines.get(position).getUrlToImage()!=null){
            Picasso.get().load(headlines.get(position).getUrlToImage()).into(holder.img_headline);
        }

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.OnNewsClicked(headlines.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return headlines.size();
    }
}
