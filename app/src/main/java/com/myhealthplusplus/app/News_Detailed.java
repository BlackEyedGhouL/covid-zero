package com.myhealthplusplus.app;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Html;
import android.widget.ImageView;
import android.widget.TextView;

import com.myhealthplusplus.app.Models.NewsHeadlines;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class News_Detailed extends AppCompatActivity {

    NewsHeadlines headlines;
    TextView txt_title, txt_author, txt_time, txt_details, txt_url;
    ImageView img_news;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detailed);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        setTitle(Html.fromHtml("<font color=\"white\">" + "News" + "</font>"));

        txt_title = findViewById(R.id.news_detailed_title);
        txt_author = findViewById(R.id.news_detailed_author);
        txt_time = findViewById(R.id.news_detailed_time);
        txt_details = findViewById(R.id.news_detailed_details);
        txt_url = findViewById(R.id.news_detailed_url);
        img_news = findViewById(R.id.news_detailed_headline_img);

        headlines = (NewsHeadlines) getIntent().getSerializableExtra("data");

        txt_title.setText(headlines.getTitle());
        txt_author.setText(headlines.getAuthor());
        txt_time.setText(headlines.getPublishedAt());
        txt_details.setText(headlines.getDescription());
        txt_url.setText(headlines.getUrl());
        Picasso.get().load(headlines.getUrlToImage()).into(img_news);
    }
}