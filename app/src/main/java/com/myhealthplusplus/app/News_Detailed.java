package com.myhealthplusplus.app;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.myhealthplusplus.app.Models.NewsHeadlines;
import com.squareup.picasso.Picasso;

public class News_Detailed extends AppCompatActivity {

    NewsHeadlines headlines;
    TextView txt_title, txt_author, txt_time, txt_details, txt_url;
    ImageView img_news, back;
    Button read_more;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detailed);
        getWindow().setStatusBarColor(ContextCompat.getColor(News_Detailed.this, R.color.dark_black));

        txt_title = findViewById(R.id.news_detailed_title);
        txt_author = findViewById(R.id.news_detailed_author);
        txt_time = findViewById(R.id.news_detailed_time);
        txt_details = findViewById(R.id.news_detailed_details);
        back = findViewById(R.id.news_detailed_back);
        read_more = findViewById(R.id.news_detailed_read_more);
        img_news = findViewById(R.id.news_detailed_headline_img);

        headlines = (NewsHeadlines) getIntent().getSerializableExtra("data");

        txt_title.setText(headlines.getTitle());
        txt_author.setText(headlines.getAuthor());
        txt_time.setText(headlines.getPublishedAt());
        txt_details.setText(headlines.getDescription());
        Picasso.get().load(headlines.getUrlToImage()).into(img_news);

        read_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = ""+ headlines.getUrl();
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }
}