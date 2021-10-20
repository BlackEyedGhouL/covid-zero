package com.myhealthplusplus.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class IsolationCountDown extends AppCompatActivity {

    Button si_start, si_stop;
    ProgressBar pb;
    LottieAnimationView lav;
    TextView time_text, more_info, days_togo, until;
    CountDownTimer cdt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_isolation_count_down);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        setTitle(Html.fromHtml("<font color=\"white\">" + "Self-isolation" + "</font>"));

        pb = findViewById(R.id.pb_si);
        si_start = findViewById(R.id.si_start);
        si_stop = findViewById(R.id.si_stop);
        lav = findViewById(R.id.si_lottieAnimationView);
        time_text = findViewById(R.id.si_remaining_days);
        days_togo = findViewById(R.id.si_days_togo);
        until = findViewById(R.id.si_untilTime);
        more_info = findViewById(R.id.si_more_info);

        more_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "https://www.nhs.uk/conditions/coronavirus-covid-19/self-isolation-and-treatment/when-to-self-isolate-and-what-to-do/";

                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });

        pb.setProgress(0);

        si_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                si_start.setVisibility(View.GONE);
                si_stop.setVisibility(View.VISIBLE);
                lav.setAnimation(R.raw.red_rounds);
                lav.loop(true);
                lav.playAnimation();
                pb.setProgressDrawable(getResources().getDrawable(R.drawable.pb_si_circle_red));

                Calendar start_calendar = Calendar.getInstance();
                Calendar end_calendar = Calendar.getInstance();
                end_calendar.add(Calendar.DATE, 14);

                SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat format2 = new SimpleDateFormat("HH:mm");
                until.setText("Until " + format1.format(end_calendar.getTime()) + " at " + format2.format(end_calendar.getTime()));

                long start_millis = start_calendar.getTimeInMillis();
                long end_millis = end_calendar.getTimeInMillis();
                long total_millis = (end_millis - start_millis);

                cdt = new CountDownTimer(total_millis, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        long days = TimeUnit.MILLISECONDS.toDays(millisUntilFinished);
                        millisUntilFinished -= TimeUnit.DAYS.toMillis(days);

                        long hours = TimeUnit.MILLISECONDS.toHours(millisUntilFinished);
                        millisUntilFinished -= TimeUnit.HOURS.toMillis(hours);

                        long minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished);
                        millisUntilFinished -= TimeUnit.MINUTES.toMillis(minutes);

                        long seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished);

                        time_text.setText(days+"");

                        if(time_text.getText().toString().equals("13")){pb.setProgress(1);}
                        if(time_text.getText().toString().equals("12")){pb.setProgress(2);}
                        if(time_text.getText().toString().equals("11")){pb.setProgress(3);}
                        if(time_text.getText().toString().equals("10")){pb.setProgress(4);}
                        if(time_text.getText().toString().equals("9")){pb.setProgress(5);}
                        if(time_text.getText().toString().equals("8")){pb.setProgress(6);}
                        if(time_text.getText().toString().equals("7")){pb.setProgress(7);}
                        if(time_text.getText().toString().equals("6")){pb.setProgress(8);}
                        if(time_text.getText().toString().equals("5")){pb.setProgress(9);}
                        if(time_text.getText().toString().equals("4")){pb.setProgress(10);}
                        if(time_text.getText().toString().equals("3")){pb.setProgress(11);}
                        if(time_text.getText().toString().equals("2")){pb.setProgress(12);}
                        if(time_text.getText().toString().equals("1")){days_togo.setText("Day to go"); pb.setProgress(13);}
                        if(time_text.getText().toString().equals("0")){days_togo.setText("Days to go"); pb.setProgress(14);}
                    }

                    @Override
                    public void onFinish() {
                        si_stop.setVisibility(View.GONE);
                        si_start.setVisibility(View.VISIBLE);
                        lav.setAnimation(R.raw.green_rounds);
                        lav.loop(true);
                        lav.playAnimation();
                        pb.setProgress(0);
                        pb.setProgressDrawable(getResources().getDrawable(R.drawable.pb_si_circle_green));
                        time_text.setText("14");
                        until.setText("Until 14 days from now");
                    }
                };
                cdt.start();
            }
        });

        si_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cdt.cancel();
                si_stop.setVisibility(View.GONE);
                si_start.setVisibility(View.VISIBLE);
                lav.setAnimation(R.raw.green_rounds);
                lav.loop(true);
                lav.playAnimation();
                pb.setProgress(0);
                days_togo.setText("Days to go");
                until.setText("Until 14 days from now");
                pb.setProgressDrawable(getResources().getDrawable(R.drawable.pb_si_circle_green));
                time_text.setText("14");
            }
        });

    }
}