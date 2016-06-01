package de.ray.slitealarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import java.util.Calendar;

public class AlarmService extends Service{

    private AlarmManager alarmManager;
    private PendingIntent alarmIntent;
    private int hour, minute;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        Log.i("extras", String.valueOf(intent.getIntExtra("hour", 0)));
        Log.i("SERVICE","start Command");
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent receiverIntent = new Intent(this, AlarmReceiver.class);
        alarmIntent = PendingIntent.getBroadcast(this, 0, receiverIntent, 0);

        if (intent == null)
            return START_STICKY;

        if (intent.getIntExtra("STATUS", -1) == Config.SET_ALARM) {
            Log.i("SERVICE", "set alarm");
            this.hour = intent.getIntExtra("hour", 0);
            this.minute = intent.getIntExtra("minute", 0);
            setAlarm();
        }

        if (intent.getIntExtra("STATUS", -1) == Config.SLEEP) {
            Log.i("SERVICE", "sleep");
            setSleep();
        }

        if (intent.getIntExtra("STATUS", -1) == Config.CANCEL) {
            Log.i("SERVICE", "cancel");
            setCancel();
        }

        return START_STICKY;
    }

    private void setCancel() {
        alarmManager.cancel(alarmIntent);
    }

    private void setSleep() {
        alarmManager.cancel(alarmIntent);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + Config.SNOOZE_TIME, alarmIntent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        alarmManager.cancel(alarmIntent);
    }

    public void setAlarm() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmIntent);
    }

}
