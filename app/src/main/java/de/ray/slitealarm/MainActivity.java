package de.ray.slitealarm;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;


public class MainActivity extends Activity {

    private MainFragment mainFragment;
    private PowerManager.WakeLock wakeLock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD); //relates to IInputConnectionWrapper issue - but not cause of instant close of app
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        if (savedInstanceState == null) {
            mainFragment = new MainFragment();
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.add(R.id.container, mainFragment).commit();
            if (getIntent() != null && getIntent().getIntExtra("STATUS", -1) == Config.START_ALARM) {
                PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
                wakeLock = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP
                        | PowerManager.ON_AFTER_RELEASE | PowerManager.FULL_WAKE_LOCK, "tag");
                wakeLock.acquire();
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (mainFragment == null) {
            mainFragment = new MainFragment();
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.add(R.id.container, mainFragment).commit();
        }
        Log.i("MainActivity", "start alarm");
        mainFragment.startAlarm();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (wakeLock != null && wakeLock.isHeld())
            wakeLock.release();
    }
}
