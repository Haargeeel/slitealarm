package de.ray.slitealarm;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.PowerManager;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

public class MainFragment extends Fragment{

    private MediaPlayer mediaPlayer;
    private TextView tv_time, tv_alarm, tv_alarm_cancel, tv_sleep, tv_end;
    private TimePicker timePicker;
    private Button bt_ok, bt_cancel;
    private LinearLayout ll_setTime, ll_sleep, ll_alarm;
    private Thread t;
    java.util.Date noteTS;
    private String time;

    public MainFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onStart();
        startClock();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setUpViews();
        if (getActivity().getIntent() != null
                && getActivity().getIntent().getIntExtra("STATUS", -1) == Config.START_ALARM) {
            startAlarm();
        }
    }

    private void startClock() {
        t = new Thread() {
            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(1000);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                updateTime();
                            }
                        });
                    }
                } catch (InterruptedException e) {
                    Log.e("Clock interrupted", "Clock interrupted");
                }
            }
        };

        t.start();
    }

    private void updateTime() {
        noteTS = Calendar.getInstance().getTime();
        time = "HH:mm";
        tv_time.setText(DateFormat.format(time, noteTS));
    }

    private void setUpViews() {
        ll_setTime = (LinearLayout) getActivity().findViewById(R.id.set_time_layout);
        tv_time = (TextView) getActivity().findViewById(R.id.time);
        Typeface tf = Typeface.createFromAsset(this.getActivity().getAssets(), "Ubuntu-L.ttf");
        tv_time.setTypeface(tf);
        tv_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ll_setTime.setVisibility(View.VISIBLE);
                Intent stopIntent = new Intent(getActivity(), AlarmService.class);
                getActivity().stopService(stopIntent);
            }
        });

        ll_alarm = (LinearLayout) getActivity().findViewById(R.id.alarm_layout);

        tv_alarm = (TextView) getActivity().findViewById(R.id.alarm_time);
        tv_alarm.setTypeface(tf);

        tv_alarm_cancel = (TextView) getActivity().findViewById(R.id.alarm_cancel);
        tv_alarm_cancel.setTypeface(tf);
        tv_alarm_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AlarmService.class);
                intent.putExtra("STATUS", Config.CANCEL);
                getActivity().startService(intent);
                ll_alarm.setVisibility(View.GONE);
            }
        });

        timePicker = (TimePicker) getActivity().findViewById(R.id.timepicker);
        timePicker.setIs24HourView(true);

        bt_cancel = (Button) getActivity().findViewById(R.id.set_time_cancel);
        bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ll_setTime.setVisibility(View.GONE);
            }
        });
        bt_ok = (Button) getActivity().findViewById(R.id.set_time_ok);
        bt_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ll_setTime.setVisibility(View.GONE);
                setAlarm();
            }
        });
        ll_sleep = (LinearLayout) getActivity().findViewById(R.id.sleep_layout);
        tv_sleep = (TextView) getActivity().findViewById(R.id.sleep_button);
        tv_sleep.setTypeface(tf);
        tv_sleep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSleep();
            }
        });
        tv_end = (TextView) getActivity().findViewById(R.id.sleep_end_button);
        tv_end.setTypeface(tf);
        tv_end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AlarmService.class);
                intent.putExtra("STATUS", Config.CANCEL);
                getActivity().startService(intent);
                ll_sleep.setVisibility(View.GONE);
                mediaPlayer.stop();
            }
        });
    }

    private void setSleep() {
        Intent intent = new Intent(getActivity(), AlarmService.class);
        intent.putExtra("STATUS", Config.SLEEP);
        getActivity().startService(intent);
        ll_sleep.setVisibility(View.GONE);
        mediaPlayer.stop();
    }

    public void setAlarm() {
        Intent serviceIntent = new Intent(getActivity(), AlarmService.class);
        serviceIntent.putExtra("STATUS", Config.SET_ALARM);
        serviceIntent.putExtra("hour", timePicker.getCurrentHour());
        serviceIntent.putExtra("minute", timePicker.getCurrentMinute());
        getActivity().startService(serviceIntent);

        int hour = timePicker.getCurrentHour();
        int minute = timePicker.getCurrentMinute();
        if (minute < 10)
            tv_alarm.setText(hour + ":0" + minute);
        else
            tv_alarm.setText(hour + ":" + minute);

        ll_alarm.setVisibility(View.VISIBLE);
    }

    public void startSong() {
        mediaPlayer = MediaPlayer.create(getActivity(), R.raw.test);
        mediaPlayer.start();
    }

    public void startAlarm() {
        Log.i("MAINFRAGMENT", "start Alarm");
        startSong();
        showSleepDialog();
    }

    private void showSleepDialog() {
        ll_alarm.setVisibility(View.INVISIBLE);
        ll_sleep.setVisibility(View.VISIBLE);
    }

    @Override
    public void onPause() {
        super.onPause();
        t.interrupt();
    }
}
