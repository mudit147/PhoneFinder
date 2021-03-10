package com.example.phonefinder;

import android.app.AlarmManager;
import android.app.KeyguardManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.security.Provider;


public class PhoneFinderService extends Service {
    private static final String TAG = "MainActivity";
    private final int CAMERA_REQUEST_CODE = 2;
    boolean hasCameraFlash = false;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(PhoneFinderService.this, "Service Started", Toast.LENGTH_LONG).show();
//        setAlarm(System.currentTimeMillis() + 2000);
        hasCameraFlash = getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
//        MyAlarm myAlarm = new MyAlarm(this);
        createNotificationChannel();

        Intent intent_1 = new Intent(this, NotificationHelper.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent_1, 0);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        int notificationCounter= 0;
        long alarmTime=0;

        while (isDeviceLocked(getApplicationContext()) && notificationCounter < 3) {
            Log.v(TAG, "sup bro?");
            long currentTime = System.currentTimeMillis();
            long notificationOneTime = currentTime + 5000;
            long notificationTwoTime = notificationOneTime + 5000;
            long notificationThreeTime = notificationTwoTime + 5000;
            alarmTime = notificationOneTime + 5000;

//            setAlarm(System.currentTimeMillis() + 5000);
//            vibrateThePhone();
            Log.v(TAG, "locked");
            Log.v(TAG, "counter0 =" + notificationCounter);

            alarmManager.set(AlarmManager.RTC_WAKEUP, notificationOneTime, pendingIntent);
            notificationCounter++;
            Log.v(TAG, "counter1 =" + notificationCounter + ", time:" + notificationOneTime);

            alarmManager.set(AlarmManager.RTC_WAKEUP, notificationTwoTime, pendingIntent);
            notificationCounter++;
            Log.v(TAG, "counter2 =" + notificationCounter + ", time:" + notificationTwoTime);

            alarmManager.set(AlarmManager.RTC_WAKEUP, notificationThreeTime, pendingIntent);
            notificationCounter++;
            Log.v(TAG, "counter3 =" + notificationCounter + ", time:" + notificationThreeTime);
        }

        if(!isDeviceLocked(getApplicationContext())){
            Log.v(TAG, "counter_unlocked =" + notificationCounter);
            notificationCounter = 0;
        }
        if(notificationCounter == 3 && System.currentTimeMillis() == alarmTime) {
            Log.v(TAG, "alarm time:" + alarmTime);
            Log.v(TAG, "counter_alarm =" + notificationCounter);
            setAlarm(System.currentTimeMillis() + 5000);
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Toast.makeText(PhoneFinderService.this, "Service Destroyed", Toast.LENGTH_LONG).show();
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static boolean isDeviceLocked(Context context) {
        boolean isLocked = false;

        KeyguardManager keyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        boolean inKeyguardRestrictedInputMode = keyguardManager.isKeyguardLocked();

        if (inKeyguardRestrictedInputMode) {
            isLocked = true;

        } else {

            PowerManager powerManager = (PowerManager)context.getSystemService(Context.POWER_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
                isLocked = !powerManager.isInteractive();
            } else {
                isLocked = !powerManager.isScreenOn();
            }
        }
        return isLocked;
    }

    private void setAlarm(long timeInMillis) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, MyAlarm.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0 , intent, 0);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, timeInMillis, AlarmManager.INTERVAL_FIFTEEN_MINUTES, pendingIntent);
        Toast.makeText(this, "Alarm is set", Toast.LENGTH_SHORT).show();
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "PhoneFinderChannel";
            String description = "Channel for locating phone";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("NotifySeniors", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

    }
}
