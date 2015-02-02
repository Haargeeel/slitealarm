package de.ray.slitealarm;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by ray on 29/01/15.
 */
public class MainFragment extends Fragment{

    private TextView tv_time;
    private Handler handler;
    private Calendar cal;
    java.util.Date noteTS;
    String time, date;

    public MainFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setUpViews();
        startClock();
    }

    private void startClock() {
        Thread t = new Thread() {

            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(1000);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                updateTime();
                                Log.i("hallo", "hallo");
                            }
                        });
                    }
                } catch (InterruptedException e) {
                }
            }
        };

        t.start();
    }

    private void updateTime() {
        noteTS = Calendar.getInstance().getTime();

        time = "hh:mm"; // 12:00
        tv_time.setText(DateFormat.format(time, noteTS));
    }

    private void setUpViews() {
        tv_time = (TextView) getActivity().findViewById(R.id.time);
    }


}
