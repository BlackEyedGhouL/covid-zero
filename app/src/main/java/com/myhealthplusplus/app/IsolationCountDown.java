package com.myhealthplusplus.app;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.airbnb.lottie.LottieAnimationView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class IsolationCountDown extends AppCompatActivity {

    long START_TIME_IN_MILLIS = 1209600000;
    Button si_start, si_stop;
    ProgressBar pb;
    LottieAnimationView lav;
    TextView time_text, more_info, days_togo, until;
    CountDownTimer cdt;
    boolean mTimerRunning = false;
    long mTimeLeftInMillis;
    long mEndTime;
    String mEndDate;
    ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_isolation_count_down);
        getWindow().setStatusBarColor(ContextCompat.getColor(IsolationCountDown.this, R.color.dark_black));

        pb = findViewById(R.id.pb_si);
        si_start = findViewById(R.id.si_start);
        si_stop = findViewById(R.id.si_stop);
        lav = findViewById(R.id.si_lottieAnimationView);
        time_text = findViewById(R.id.si_remaining_days);
        days_togo = findViewById(R.id.si_days_togo);
        until = findViewById(R.id.si_untilTime);
        more_info = findViewById(R.id.si_more_info);
        back = findViewById(R.id.si_back);

        createNotificationChannel();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

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
                Log.d(TAG, "onStart: click - " + mTimerRunning);
                if (mTimerRunning) {
                    pauseTimer();
                } else {
                    mTimeLeftInMillis = START_TIME_IN_MILLIS;
                    startTimer();
                }
            }
        });

        si_stop.setOnClickListener(new View.OnClickListener() {
            @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
            @Override
            public void onClick(View view) {
                pauseTimer();
            }
        });
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void startTimer() {
        si_start.setVisibility(View.GONE);
        si_stop.setVisibility(View.VISIBLE);
        lav.setAnimation(R.raw.red_rounds);
        lav.loop(true);
        lav.playAnimation();
        pb.setProgressDrawable(getResources().getDrawable(R.drawable.pb_si_circle_red));

        Calendar end_calendar = Calendar.getInstance();
        end_calendar.add(Calendar.DATE, 14);

        @SuppressLint("SimpleDateFormat") SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
        @SuppressLint("SimpleDateFormat") SimpleDateFormat format2 = new SimpleDateFormat("HH:mm");
        mEndDate = "Until " + format1.format(end_calendar.getTime()) + " at " + format2.format(end_calendar.getTime());
        until.setText(mEndDate);

        mEndTime = System.currentTimeMillis() + mTimeLeftInMillis;

        cdt = new CountDownTimer(mTimeLeftInMillis, 1000) {
            @SuppressLint("SetTextI18n")
            @Override
            public void onTick(long millisUntilFinished) {

                mTimeLeftInMillis = millisUntilFinished;

                long days = TimeUnit.MILLISECONDS.toDays(millisUntilFinished);
                millisUntilFinished -= TimeUnit.DAYS.toMillis(days);

                long hours = TimeUnit.MILLISECONDS.toHours(millisUntilFinished);
                millisUntilFinished -= TimeUnit.HOURS.toMillis(hours);

                long minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished);
                millisUntilFinished -= TimeUnit.MINUTES.toMillis(minutes);

                long seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished);

                time_text.setText(days + "");
                Log.d(TAG, "onTick: " + "Sec " + seconds + " minutes " + minutes + " hours " + hours + " days " + days);

                if (time_text.getText().toString().equals("13")) {
                    pb.setProgress(1);
                }
                if (time_text.getText().toString().equals("12")) {
                    pb.setProgress(2);
                }
                if (time_text.getText().toString().equals("11")) {
                    pb.setProgress(3);
                }
                if (time_text.getText().toString().equals("10")) {
                    pb.setProgress(4);
                }
                if (time_text.getText().toString().equals("9")) {
                    pb.setProgress(5);
                }
                if (time_text.getText().toString().equals("8")) {
                    pb.setProgress(6);
                }
                if (time_text.getText().toString().equals("7")) {
                    pb.setProgress(7);
                }
                if (time_text.getText().toString().equals("6")) {
                    pb.setProgress(8);
                }
                if (time_text.getText().toString().equals("5")) {
                    pb.setProgress(9);
                }
                if (time_text.getText().toString().equals("4")) {
                    pb.setProgress(10);
                }
                if (time_text.getText().toString().equals("3")) {
                    pb.setProgress(11);
                }
                if (time_text.getText().toString().equals("2")) {
                    pb.setProgress(12);
                }
                if (time_text.getText().toString().equals("1")) {
                    days_togo.setText("Day to go");
                    pb.setProgress(13);
                }
                if (time_text.getText().toString().equals("0")) {
                    days_togo.setText("Days to go");
                    pb.setProgress(14);
                }
            }

            @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
            @Override
            public void onFinish() {
                mTimerRunning = false;
                si_stop.setVisibility(View.GONE);
                si_start.setVisibility(View.VISIBLE);
                lav.setAnimation(R.raw.green_rounds);
                lav.loop(true);
                lav.playAnimation();
                pb.setProgress(0);
                pb.setProgressDrawable(getResources().getDrawable(R.drawable.pb_si_circle_green));
                time_text.setText("14");
                until.setText("Until 14 days from now");

                Intent intent = new Intent(IsolationCountDown.this, IsolationOverReminder.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(IsolationCountDown.this, 0, intent, 0);

                AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

                alarmManager.set(AlarmManager.RTC_WAKEUP,
                        mEndTime, pendingIntent);
            }
        }.start();

        mTimerRunning = true;
    }

    @SuppressLint({"UseCompatLoadingForDrawables", "SetTextI18n"})
    private void pauseTimer() {
        cdt.cancel();
        mTimeLeftInMillis = START_TIME_IN_MILLIS;
        mTimerRunning = false;
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

    @SuppressLint({"UseCompatLoadingForDrawables", "SetTextI18n"})
    @Override
    protected void onStop() {
        super.onStop();

        SharedPreferences prefs = getSharedPreferences("cdt", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putLong("millisLeft", mTimeLeftInMillis);
        editor.putBoolean("timerRunning", mTimerRunning);
        editor.putLong("endTime", mEndTime);
        editor.putString("endDate", mEndDate);

        editor.apply();
        if (cdt != null) {
            cdt.cancel();
        }
    }

    @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
    @Override
    protected void onStart() {
        super.onStart();

        SharedPreferences prefs = getSharedPreferences("cdt", MODE_PRIVATE);

        mTimeLeftInMillis = prefs.getLong("millisLeft", START_TIME_IN_MILLIS);
        mTimerRunning = prefs.getBoolean("timerRunning", false);
        mEndDate = prefs.getString("endDate", "");

        Log.d(TAG, "onStart: is timer running - " + mTimerRunning);

        if (mTimerRunning) {
            mEndTime = prefs.getLong("endTime", 0);
            mTimeLeftInMillis = mEndTime - System.currentTimeMillis();

            if (mTimeLeftInMillis < 0) {
                mTimeLeftInMillis = 0;
                mTimerRunning = false;
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

                Intent intent = new Intent(IsolationCountDown.this, IsolationOverReminder.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(IsolationCountDown.this, 0, intent, 0);

                AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

                alarmManager.set(AlarmManager.RTC_WAKEUP,
                        mEndTime, pendingIntent);
            } else {
                startTimer();
            }
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Isolation Countdown";
            String description = "Congrats! you've successfully completed your isolation time period.";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("notifyIsolation", name, importance);
            channel.setDescription(description);

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }
}