package com.myhealthplusplus.app;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.myhealthplusplus.app.Models.NewsApiResponse;
import com.myhealthplusplus.app.Models.NewsHeadlines;

import java.util.List;
import java.util.Objects;

public class News_Main extends AppCompatActivity implements News_SelectListener {

    RecyclerView recyclerView;
    News_CustomAdapter adapter;
    private MainActivity activity = new MainActivity();
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_news);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        setTitle(Html.fromHtml("<font color=\"white\">" + "News" + "</font>"));

        run();

        swipeRefreshLayout = findViewById(R.id.news_refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                int refreshCycleColor = Color.parseColor("#c01722");
                swipeRefreshLayout.setColorSchemeColors(refreshCycleColor);

                run();

                swipeRefreshLayout.setRefreshing(false);
            }
        });

    }

    private void run(){
        if (isConnected(this)) {
        activity.ShowDialog(this);
        News_RequestManager manager = new News_RequestManager(News_Main.this);
        manager.getNewsHeadlines(listener, "publishedAt");
        }
        else{
            showInternetDialog();
        }
    }

    private final News_OnFetchDataListener<NewsApiResponse> listener = new News_OnFetchDataListener<NewsApiResponse>() {
        @Override
        public void onFetchData(List<NewsHeadlines> list, String message) {
            showNews(list);
            activity.DismissDialog();
        }

        @Override
        public void onError(String message) {

        }
    };

    private void showNews(List<NewsHeadlines> list) {
        recyclerView = findViewById(R.id.news_recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        adapter = new News_CustomAdapter(this, list, this);
        recyclerView.setAdapter(adapter);
    }

    private boolean isConnected(News_Main news_main) {
        ConnectivityManager connectivityManager = (ConnectivityManager) news_main.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo wifiConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        return (wifiConn != null && wifiConn.isConnected()) || (mobileConn != null && mobileConn.isConnected());
    }

    private void showInternetDialog() {

        activity.ShowDialog(this);

        Handler delay = new Handler();
        delay.postDelayed(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(News_Main.this);
                builder.setCancelable(false);

                View view = LayoutInflater.from(News_Main.this).inflate(R.layout.activity_no_connection, findViewById(R.id.no_connection_layout));
                view.findViewById(R.id.try_again).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!isConnected(News_Main.this)) {
                            showInternetDialog();
                        } else {
                            startActivity(new Intent(getApplicationContext(), News_Main.class));
                            finish();
                        }
                    }
                });

                builder.setView(view);

                AlertDialog alertDialog = builder.create();

                alertDialog.show();

                activity.DismissDialog();
            }
        },1000);
    }

    @Override
    public void OnNewsClicked(NewsHeadlines headlines) {
        startActivity(new Intent(News_Main.this, News_Detailed.class)
        .putExtra("data", headlines));

    }
}