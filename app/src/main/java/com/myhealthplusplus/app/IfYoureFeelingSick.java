package com.myhealthplusplus.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;

import com.myhealthplusplus.app.Models.Sick_Info;
import com.myhealthplusplus.app.Models.Sick_Rows;
import com.myhealthplusplus.app.SQLite.DatabaseAdapter;
import com.myhealthplusplus.app.SQLite.PreCreateDB;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class IfYoureFeelingSick extends AppCompatActivity {

    RecyclerView recyclerView;
    List<Sick_Rows> sick_rowsList = new ArrayList<>();
    List<Sick_Info> sick_infoList = new ArrayList<>();
    ImageView back;
    DatabaseAdapter databaseAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_if_youre_feeling_sick);
        getWindow().setStatusBarColor(ContextCompat.getColor(IfYoureFeelingSick.this, R.color.dark_black));

        init();

        PreCreateDB.copyDB(this);
        databaseAdapter = new DatabaseAdapter(this);
        sick_infoList = databaseAdapter.getAllSickInfo();

        setData();
        setRecyclerView();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void setRecyclerView() {
        Sick_Rows_Adapter adapter = new Sick_Rows_Adapter(sick_rowsList);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
    }

    private void setData() {
        for (int i = 0; i < sick_infoList.size(); i++) {
            sick_rowsList.add(new Sick_Rows(sick_infoList.get(i).getTopic(), sick_infoList.get(i).getSub_topic()));
        }
    }

    public void init() {
        back = findViewById(R.id.sick_back);
        recyclerView = findViewById(R.id.sick_recyclerView);
    }
}