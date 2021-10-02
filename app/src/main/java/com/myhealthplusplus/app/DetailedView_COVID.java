package com.myhealthplusplus.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
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
    }

    private void FetchData() {

        if (isConnected(this)) {

        activity.ShowDialog(this);

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        String apiUrl = "https://disease.sh/v3/covid-19/all";
        Button btn1 = findViewById(R.id.detailed_btnSriLanka);
        Button btn2 = findViewById(R.id.detailed_btnGlobal);
        LinearLayout d_linear_countries = findViewById(R.id.detailed_affectedCountries_linear);
        TextView activeNew = findViewById(R.id.detailed_active_new_txt);

        if(i==1){
            d_linear_countries.setVisibility(View.GONE);

            if( btn1.getSolidColor() != getResources().getColor(R.color.fav_red)){
                btn1.setBackgroundResource(R.color.fav_red);
                btn2.setBackgroundResource(R.color.light_black);
            }

            apiUrl = "https://disease.sh/v3/covid-19/countries/lk";
        }
        else{
            d_linear_countries.setVisibility(View.VISIBLE);

            if( btn1.getSolidColor() != getResources().getColor(R.color.fav_red)){
                btn2.setBackgroundResource(R.color.fav_red);
                btn1.setBackgroundResource(R.color.light_black);
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
                builder.setCancelable(false);

                View view = LayoutInflater.from(DetailedView_COVID.this).inflate(R.layout.activity_no_connection, findViewById(R.id.no_connection_layout));
                view.findViewById(R.id.try_again).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!isConnected(DetailedView_COVID.this)) {
                            showInternetDialog();
                        } else {
                            startActivity(new Intent(getApplicationContext(), DetailedView_COVID.class));
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
}