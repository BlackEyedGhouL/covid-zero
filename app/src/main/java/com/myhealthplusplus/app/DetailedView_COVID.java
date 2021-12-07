package com.myhealthplusplus.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.SweepGradient;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.DefaultAxisValueFormatter;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Objects;

public class DetailedView_COVID extends AppCompatActivity {

    private TextView tv_confirmed, tv_confirmed_new, tv_active, tv_active_new, tv_recovered, tv_recovered_new, tv_death,
            tv_death_new, tv_tests, tv_countries;

    String str_confirmed, str_confirmed_new, str_active, str_recovered, str_recovered_new,
            str_death, str_death_new, str_tests, str_countries;

    private SwipeRefreshLayout swipeRefreshLayout;

    private PieChart pieChart;

    private int int_active_new = 0;

    Button d_Global, d_Srilanka;

    private BarChart barChart_newCases;

    int i = 1;

    private MainActivity activity = new MainActivity();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_view_covid);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        setTitle(Html.fromHtml("<font color=\"white\">" + "Analytics" + "</font>"));

        Init();
        FetchData();

        d_Global = findViewById(R.id.detailed_btnGlobal);
        d_Global.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                i=2;
                Init();
                FetchData();
            }
        });

        d_Srilanka = findViewById(R.id.detailed_btnSriLanka);
        d_Srilanka.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                i=1;
                Init();
                FetchData();
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                int refreshCycleColor = Color.parseColor("#c01722");
                swipeRefreshLayout.setColorSchemeColors(refreshCycleColor);
                FetchData();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        BarDataSet barDataSet1 = new BarDataSet(dataValues1(), "New cases");
        barDataSet1.setValueTextColor(Color.WHITE);
        barDataSet1.setColor(getResources().getColor(R.color.red_pie));
        BarData barData1 = new BarData();
        barData1.addDataSet(barDataSet1);

        barChart_newCases.setData(barData1);
        XAxis xAxis = barChart_newCases.getXAxis();
        xAxis.setValueFormatter(new MyXAxisValueFormatter());

        YAxis yAxis = barChart_newCases.getAxisLeft();
        yAxis.setTextColor(Color.WHITE);

        barDataSet1.setValueFormatter(new MyValueFormatter());
        //Paint mPaint = barChart_newCases.getRenderer().getPaintRender(); mPaint.setShader(new
          //      SweepGradient(550,750,Color.parseColor("#007afe"),Color.parseColor("#F6404F")));

        barChart_newCases.getAxisLeft().setAxisMinValue(0f);
        barChart_newCases.getAxisLeft().setAxisMaxValue(1000f);
        barChart_newCases.getXAxis().setDrawGridLines(false);
        barChart_newCases.setTouchEnabled(false);
        barChart_newCases.getAxisRight().setEnabled(false);
        barChart_newCases.getLegend().setEnabled(false);
        barChart_newCases.setDoubleTapToZoomEnabled(false);
        barChart_newCases.setFitBars(true);
        barChart_newCases.setScaleEnabled(false);
        barChart_newCases.setClickable(false);
        barChart_newCases.setDrawBarShadow(false);
        barChart_newCases.setDrawGridBackground(false);
        barChart_newCases.getAxisRight().setEnabled(false);
        barChart_newCases.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        Description description = new Description();
        description.setText("");
        barChart_newCases.setDescription(description);
        barChart_newCases.invalidate();
    }

    public class MyXAxisValueFormatter implements IAxisValueFormatter {

        @Override
        public String getFormattedValue(float v, AxisBase axisBase) {
            axisBase.setLabelCount(7);
            axisBase.setTextColor(Color.WHITE);
            return "Nov " + Math.round(v);
        }
    }

    public class MyValueFormatter implements IValueFormatter {

        private DecimalFormat mFormat;

        public MyValueFormatter() {
            mFormat = new DecimalFormat("###,###,##0");
        }

        @Override
        public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
            // write your logic here
            return mFormat.format(value);
        }
    }

    private ArrayList<BarEntry> dataValues1() {
        ArrayList<BarEntry> dataV = new ArrayList<>();
        dataV.add(new BarEntry(10, 693));
        dataV.add(new BarEntry(11, 715));
        dataV.add(new BarEntry(12, 723));
        dataV.add(new BarEntry(13, 716));
        dataV.add(new BarEntry(14, 697));
        dataV.add(new BarEntry(15, 732));
        dataV.add(new BarEntry(16, 720));
        return dataV;
    }

    private void FetchData() {

        if (isConnected(this)) {

        activity.ShowDialog(this);

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        String apiUrl = "https://disease.sh/v3/covid-19/all";
        Button btn1 = findViewById(R.id.detailed_btnSriLanka);
        Button btn2 = findViewById(R.id.detailed_btnGlobal);
        View d_Srilanka_view = findViewById(R.id.detailed_btnSriLanka_view);
        View d_Global_view = findViewById(R.id.detailed_btnGlobal_view);
        CardView d_card_countries = findViewById(R.id.detailed_affectedCountries_card_txt);
        TextView activeNew = findViewById(R.id.detailed_active_new_txt);

        if(i==1){
            d_card_countries.setVisibility(View.GONE);

            if( btn1.getCurrentHintTextColor() != getResources().getColor(R.color.white)){
                btn1.setTextColor(getResources().getColor(R.color.white));
                btn2.setTextColor(getResources().getColor(R.color.gray));
                d_Srilanka_view.setBackgroundResource(R.color.red_pie);
                d_Global_view.setBackgroundResource(R.color.lightest_black);
            }

            apiUrl = "https://disease.sh/v3/covid-19/countries/lk";
        }
        else{
            d_card_countries.setVisibility(View.VISIBLE);

            if( btn1.getCurrentHintTextColor() != getResources().getColor(R.color.white)){
                btn2.setTextColor(getResources().getColor(R.color.white));
                btn1.setTextColor(getResources().getColor(R.color.gray));
                d_Global_view.setBackgroundResource(R.color.red_pie);
                d_Srilanka_view.setBackgroundResource(R.color.lightest_black);
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

    private void Init() {

        tv_confirmed = findViewById(R.id.detailed_confirmed_num_txt);
        tv_confirmed_new = findViewById(R.id.detailed_confirmed_new_txt);
        tv_active = findViewById(R.id.detailed_active_num_txt);
        tv_active_new = findViewById(R.id.detailed_active_new_txt);
        tv_recovered = findViewById(R.id.detailed_recovered_num_txt);
        tv_recovered_new = findViewById(R.id.detailed_recovered_new_txt);
        tv_death = findViewById(R.id.detailed_deaths_num_txt);
        tv_death_new = findViewById(R.id.detailed_deaths_new_txt);
        tv_tests = findViewById(R.id.detailed_sample_num_txt);
        barChart_newCases = findViewById(R.id.detailed_new_cases_BarChart);

        if(i==1){
        }
        else{
            tv_countries = findViewById(R.id.detailed_affectedCountries_num_txt);
        }

        pieChart = findViewById(R.id.detailed_piechart);
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