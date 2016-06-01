package de.ray.slitealarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmReceiver extends BroadcastReceiver {

    Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        startAlarm();
    }

    private void startAlarm() {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra("STATUS", Config.START_ALARM);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

}
