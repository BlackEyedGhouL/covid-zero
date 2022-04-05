package com.myhealthplusplus.app;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.myhealthplusplus.app.Models.DeathDate;
import com.myhealthplusplus.app.Models.NewCasesDate;
import com.myhealthplusplus.app.Models.RecoveredDate;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;

public class DetailedView_COVID extends AppCompatActivity {

    private TextView tv_confirmed, tv_confirmed_new, tv_active, tv_active_new, tv_recovered, tv_recovered_new, tv_death,
            tv_death_new, tv_tests, tv_countries;
    String str_confirmed, str_confirmed_new, str_active, str_recovered, str_recovered_new,
            str_death, str_death_new, str_tests, str_countries;
    private SwipeRefreshLayout swipeRefreshLayout;
    private PieChart pieChart;
    private int int_active_new = 0;
    Button d_Global, d_sri_lanka;
    int i = 1;
    private final MainActivity activity = new MainActivity();
    ImageView back;
    DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();

    LineChart lineChartNewCases;
    LineDataSet newCasesLineDataSet = new LineDataSet(null, null);
    ArrayList<ILineDataSet> iNewCasesLineDataSet = new ArrayList<>();
    LineData newCasesLineData;

    LineChart lineChartDeaths;
    LineDataSet deathsLineDataSet = new LineDataSet(null, null);
    ArrayList<ILineDataSet> iDeathsLineDataSet = new ArrayList<>();
    LineData deathLineData;

    LineChart lineChartRecovered;
    LineDataSet recoveredLineDataSet = new LineDataSet(null, null);
    ArrayList<ILineDataSet> iRecoveredLineDataSet = new ArrayList<>();
    LineData recoveredLineData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_view_covid);
        getWindow().setStatusBarColor(ContextCompat.getColor(DetailedView_COVID.this, R.color.dark_black));

        init();
        FetchData();


        d_Global.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                i=2;
                init();
                FetchData();
            }
        });


        d_sri_lanka.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                i=1;
                init();
                FetchData();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        int refreshCycleColor = Color.parseColor("#c01722");
        swipeRefreshLayout.setColorSchemeColors(refreshCycleColor);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                FetchData();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        get_data_new_cases();
        get_data_deaths();
        get_data_recovered();
    }

    private void get_data_new_cases() {
        DatabaseReference eventRef = rootRef
                .child("chartData")
                .child("new_cases");

        Query query = eventRef.limitToLast(7);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                ArrayList<Entry> newCasesDateList = new ArrayList<>();

                for(DataSnapshot ds : snapshot.getChildren()) {

                    NewCasesDate newCasesDate = ds.getValue(NewCasesDate.class);
                    assert newCasesDate != null;
                    newCasesDateList.add(new Entry(newCasesDate.getDate(), newCasesDate.getValue()));
                }
                new_cases(newCasesDateList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void get_data_deaths() {
        DatabaseReference eventRef = rootRef
                .child("chartData")
                .child("deaths");

        Query query = eventRef.limitToLast(7);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                ArrayList<Entry> deathsDateList = new ArrayList<>();

                for(DataSnapshot ds : snapshot.getChildren()) {

                    DeathDate deathDate = ds.getValue(DeathDate.class);
                    assert deathDate != null;
                    deathsDateList.add(new Entry(deathDate.getDate(), deathDate.getValue()));
                }
                deaths(deathsDateList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void get_data_recovered() {
        DatabaseReference eventRef = rootRef
                .child("chartData")
                .child("recovered");

        Query query = eventRef.limitToLast(7);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                ArrayList<Entry> recoveredDateList = new ArrayList<>();

                for(DataSnapshot ds : snapshot.getChildren()) {

                    RecoveredDate recoveredDate = ds.getValue(RecoveredDate.class);
                    assert recoveredDate != null;
                    recoveredDateList.add(new Entry(recoveredDate.getDate(), recoveredDate.getValue()));
                }
                recovered(recoveredDateList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void deaths(ArrayList<Entry> dataValues) {
        deathsLineDataSet.setValues(dataValues);
        deathsLineDataSet.setLabel("Deaths");
        iDeathsLineDataSet.clear();
        iDeathsLineDataSet.add(deathsLineDataSet);
        deathLineData = new LineData(iDeathsLineDataSet);
        lineChartDeaths.clear();
        lineChartDeaths.setData(deathLineData);
        lineChartDeaths.invalidate();
        deathsLineDataSet.setValueTextColor(Color.WHITE);
        deathsLineDataSet.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);
        deathsLineDataSet.setDrawFilled(true);
        deathsLineDataSet.setDrawCircles(true);
        deathsLineDataSet.setDrawCircleHole(false);
        deathsLineDataSet.setLineWidth(2f);
        deathsLineDataSet.setCircleColor(Color.WHITE);

        Drawable drawable = ContextCompat.getDrawable(this, R.drawable.red_gradient);
        deathsLineDataSet.setFillDrawable(drawable);

        deathsLineDataSet.setColor(getResources().getColor(R.color.red_pie));
        LineData barData1 = new LineData();
        barData1.addDataSet(deathsLineDataSet);

        lineChartDeaths.setData(barData1);
        XAxis xAxis = lineChartDeaths.getXAxis();
        xAxis.setLabelCount(7);
        xAxis.setTextColor(Color.WHITE);
        YAxis yAxis = lineChartDeaths.getAxisLeft();
        yAxis.setTextColor(Color.WHITE);
        lineChartDeaths.getXAxis().setDrawGridLines(false);
        lineChartDeaths.getXAxis().setDrawLabels(true);
        lineChartDeaths.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        lineChartDeaths.getXAxis().setLabelCount(7, true);
        lineChartDeaths.setTouchEnabled(false);
        lineChartDeaths.getLegend().setEnabled(false);
        lineChartDeaths.setDoubleTapToZoomEnabled(false);
        lineChartDeaths.setScaleEnabled(false);
        lineChartDeaths.setClickable(false);
        lineChartDeaths.setDrawGridBackground(false);
        lineChartDeaths.getAxisRight().setEnabled(false);
        Description description = new Description();
        description.setText("");
        lineChartDeaths.setDescription(description);
        lineChartDeaths.animateY(1000);
    }

    public void recovered(ArrayList<Entry> dataValues) {
        recoveredLineDataSet.setValues(dataValues);
        recoveredLineDataSet.setLabel("Recovered");
        iRecoveredLineDataSet.clear();
        iRecoveredLineDataSet.add(recoveredLineDataSet);
        recoveredLineData = new LineData(iRecoveredLineDataSet);
        lineChartRecovered.clear();
        lineChartRecovered.setData(recoveredLineData);
        lineChartRecovered.invalidate();
        recoveredLineDataSet.setValueTextColor(Color.WHITE);
        recoveredLineDataSet.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);
        recoveredLineDataSet.setDrawFilled(true);
        recoveredLineDataSet.setDrawCircles(true);
        recoveredLineDataSet.setDrawCircleHole(false);
        recoveredLineDataSet.setLineWidth(2f);
        recoveredLineDataSet.setCircleColor(Color.WHITE);

        Drawable drawable = ContextCompat.getDrawable(this, R.drawable.green_gradient);
        recoveredLineDataSet.setFillDrawable(drawable);

        recoveredLineDataSet.setColor(getResources().getColor(R.color.green_pie));
        LineData barData1 = new LineData();
        barData1.addDataSet(recoveredLineDataSet);

        lineChartRecovered.setData(barData1);
        XAxis xAxis = lineChartRecovered.getXAxis();
        xAxis.setLabelCount(7);
        xAxis.setTextColor(Color.WHITE);
        YAxis yAxis = lineChartRecovered.getAxisLeft();
        yAxis.setTextColor(Color.WHITE);
        lineChartRecovered.getXAxis().setDrawGridLines(false);
        lineChartRecovered.getXAxis().setDrawLabels(true);
        lineChartRecovered.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        lineChartRecovered.getXAxis().setLabelCount(7, true);
        lineChartRecovered.setTouchEnabled(false);
        lineChartRecovered.getLegend().setEnabled(false);
        lineChartRecovered.setDoubleTapToZoomEnabled(false);
        lineChartRecovered.setScaleEnabled(false);
        lineChartRecovered.setClickable(false);
        lineChartRecovered.setDrawGridBackground(false);
        lineChartRecovered.getAxisRight().setEnabled(false);
        Description description = new Description();
        description.setText("");
        lineChartRecovered.setDescription(description);
        lineChartRecovered.animateY(1000);
    }

    public void new_cases(ArrayList<Entry> dataValues) {
        newCasesLineDataSet.setValues(dataValues);
        newCasesLineDataSet.setLabel("New Cases");
        iNewCasesLineDataSet.clear();
        iNewCasesLineDataSet.add(newCasesLineDataSet);
        newCasesLineData = new LineData(iNewCasesLineDataSet);
        lineChartNewCases.clear();
        lineChartNewCases.setData(newCasesLineData);
        lineChartNewCases.invalidate();
        newCasesLineDataSet.setValueTextColor(Color.WHITE);
        newCasesLineDataSet.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);
        newCasesLineDataSet.setDrawFilled(true);
        newCasesLineDataSet.setDrawCircles(true);
        newCasesLineDataSet.setDrawCircleHole(false);
        newCasesLineDataSet.setLineWidth(2f);
        newCasesLineDataSet.setCircleColor(Color.WHITE);

        Drawable drawable = ContextCompat.getDrawable(this, R.drawable.blue_gradient);
        newCasesLineDataSet.setFillDrawable(drawable);

        newCasesLineDataSet.setColor(getResources().getColor(R.color.blue_pie));
        LineData barData1 = new LineData();
        barData1.addDataSet(newCasesLineDataSet);

        lineChartNewCases.setData(barData1);
        XAxis xAxis = lineChartNewCases.getXAxis();
        xAxis.setLabelCount(7);
        xAxis.setTextColor(Color.WHITE);
        YAxis yAxis = lineChartNewCases.getAxisLeft();
        yAxis.setTextColor(Color.WHITE);
        lineChartNewCases.getXAxis().setDrawGridLines(false);
        lineChartNewCases.getXAxis().setDrawLabels(true);
        lineChartNewCases.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        lineChartNewCases.getXAxis().setLabelCount(7, true);
        lineChartNewCases.setTouchEnabled(false);
        lineChartNewCases.getLegend().setEnabled(false);
        lineChartNewCases.setDoubleTapToZoomEnabled(false);
        lineChartNewCases.setScaleEnabled(false);
        lineChartNewCases.setClickable(false);
        lineChartNewCases.setDrawGridBackground(false);
        lineChartNewCases.getAxisRight().setEnabled(false);
        Description description = new Description();
        description.setText("");
        lineChartNewCases.setDescription(description);
        lineChartNewCases.animateY(1000);
    }

    private void FetchData() {

        if (isConnected(this)) {

        activity.ShowDialog(this);

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        String apiUrl = "https://disease.sh/v3/covid-19/all";
        Button btn1 = findViewById(R.id.detailed_btn_sri_lanka);
        Button btn2 = findViewById(R.id.detailed_btnGlobal);
        View d_sri_lanka_view = findViewById(R.id.detailed_btn_sri_lanka_view);
        View d_Global_view = findViewById(R.id.detailed_btnGlobal_view);
        CardView d_card_countries = findViewById(R.id.detailed_affectedCountries_card_txt);
        TextView activeNew = findViewById(R.id.detailed_active_new_txt);

        if(i==1){
            d_card_countries.setVisibility(View.GONE);

            if( btn1.getCurrentHintTextColor() != getResources().getColor(R.color.white)){
                btn1.setTextColor(getResources().getColor(R.color.white));
                btn2.setTextColor(getResources().getColor(R.color.gray));
                d_sri_lanka_view.setBackgroundResource(R.color.red_pie);
                d_Global_view.setBackgroundResource(R.color.gray);
            }

            apiUrl = "https://disease.sh/v3/covid-19/countries/lk";
        }
        else{
            d_card_countries.setVisibility(View.VISIBLE);

            if( btn1.getCurrentHintTextColor() != getResources().getColor(R.color.white)){
                btn2.setTextColor(getResources().getColor(R.color.white));
                btn1.setTextColor(getResources().getColor(R.color.gray));
                d_Global_view.setBackgroundResource(R.color.red_pie);
                d_sri_lanka_view.setBackgroundResource(R.color.gray);
            }

            apiUrl = "https://disease.sh/v3/covid-19/all";
        }

        pieChart.clearChart();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                apiUrl,
                null,

                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            str_confirmed = response.getString("cases");
                            str_confirmed_new = response.getString("todayCases");
                            str_active = response.getString("active");
                            str_recovered = response.getString("recovered");
                            str_recovered_new = response.getString("todayRecovered");
                            str_death = response.getString("deaths");
                            str_death_new = response.getString("todayDeaths");
                            str_tests = response.getString("tests");

                            if(i==1){
                            }
                            else{
                                str_countries = response.getString("affectedCountries");
                            }

                            Handler delay = new Handler();
                            delay.postDelayed(new Runnable() {
                                @SuppressLint("SetTextI18n")
                                @Override
                                public void run() {

                                    tv_confirmed.setText(NumberFormat.getInstance().format(Integer.parseInt(str_confirmed)));
                                    tv_confirmed_new.setText("+"+NumberFormat.getInstance().format(Integer.parseInt(str_confirmed_new)));

                                    tv_active.setText(NumberFormat.getInstance().format(Integer.parseInt(str_active)));

                                    int_active_new = Integer.parseInt(str_confirmed_new)
                                            - (Integer.parseInt(str_recovered_new) + Integer.parseInt(str_death_new));

                                    if(int_active_new < 0 ){
                                        activeNew.setVisibility(View.INVISIBLE);
                                        tv_active_new.setText("0");
                                    }
                                    else{
                                        activeNew.setVisibility(View.VISIBLE);
                                        tv_active_new.setText("+"+NumberFormat.getInstance().format(int_active_new));
                                    }

                                    tv_recovered.setText(NumberFormat.getInstance().format(Integer.parseInt(str_recovered)));
                                    tv_recovered_new.setText("+"+NumberFormat.getInstance().format(Integer.parseInt(str_recovered_new)));

                                    tv_death.setText(NumberFormat.getInstance().format(Integer.parseInt(str_death)));
                                    tv_death_new.setText("+"+NumberFormat.getInstance().format(Integer.parseInt(str_death_new)));

                                    tv_tests.setText(NumberFormat.getInstance().format(Long.parseLong(str_tests)));

                                    if(i==1){
                                    }
                                    else{
                                        tv_countries.setText(NumberFormat.getInstance().format(Integer.parseInt(str_countries)));
                                    }

                                    pieChart.addPieSlice(new PieModel("Active", Integer.parseInt(str_active), Color.parseColor("#007afe")));
                                    pieChart.addPieSlice(new PieModel("Recovered", Integer.parseInt(str_recovered), Color.parseColor("#08a045")));
                                    pieChart.addPieSlice(new PieModel("Deceased", Integer.parseInt(str_death), Color.parseColor("#F6404F")));

                                    pieChart.startAnimation();

                                    activity.DismissDialog();

                                }
                            }, 1000);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                }
        );
      requestQueue.add(jsonObjectRequest);
        }
        else{
            showInternetDialog();
        }
    }

    private void init() {
        tv_confirmed = findViewById(R.id.detailed_confirmed_num_txt);
        tv_confirmed_new = findViewById(R.id.detailed_confirmed_new_txt);
        tv_active = findViewById(R.id.detailed_active_num_txt);
        tv_active_new = findViewById(R.id.detailed_active_new_txt);
        tv_recovered = findViewById(R.id.detailed_recovered_num_txt);
        tv_recovered_new = findViewById(R.id.detailed_recovered_new_txt);
        tv_death = findViewById(R.id.detailed_deaths_num_txt);
        tv_death_new = findViewById(R.id.detailed_deaths_new_txt);
        tv_tests = findViewById(R.id.detailed_sample_num_txt);
        d_sri_lanka = findViewById(R.id.detailed_btn_sri_lanka);
        lineChartNewCases = findViewById(R.id.detailed_new_cases_BarChart);
        lineChartDeaths = findViewById(R.id.detailed_new_deaths_BarChart);
        lineChartRecovered = findViewById(R.id.detailed_new_recovered_BarChart);
        d_Global = findViewById(R.id.detailed_btnGlobal);

        back = findViewById(R.id.d_back);

        if(i==1){
        }
        else{
            tv_countries = findViewById(R.id.detailed_affectedCountries_num_txt);
        }

        pieChart = findViewById(R.id.detailed_pie_chart);
        swipeRefreshLayout = findViewById(R.id.detailed_refresh);
    }

    private boolean isConnected(DetailedView_COVID detailedViewCovid) {
        ConnectivityManager connectivityManager = (ConnectivityManager) detailedViewCovid.getSystemService(Context.CONNECTIVITY_SERVICE);

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
                AlertDialog.Builder builder = new AlertDialog.Builder(DetailedView_COVID.this);
                View view = LayoutInflater.from(DetailedView_COVID.this).inflate(R.layout.activity_no_connection, findViewById(R.id.no_connection_layout));
                builder.setCancelable(false);
                builder.setView(view);

                AlertDialog alertDialog = builder.create();
                alertDialog.getWindow().setWindowAnimations(R.style.DialogAnimation);

                view.findViewById(R.id.try_again).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!isConnected(DetailedView_COVID.this)) {
                            alertDialog.dismiss();
                            showInternetDialog();
                        } else {
                            startActivity(new Intent(getApplicationContext(), DetailedView_COVID.class));
                            finish();
                        }
                    }
                });

                alertDialog.show();

                activity.DismissDialog();
            }
        },1000);
    }
}