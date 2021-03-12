package com.example.phonefinder;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;


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
        createNotificationChannel("PhoneFinderChannel", "NotifySeniors");
        createNotificationChannel("PhoneFinderChannel_2", "NotifySeniors_2");
        createNotificationChannel("PhoneFinderChannel_3", "NotifySeniors_3");

        Intent intent_1 = new Intent(this, NotificationHelper.class);
        PendingIntent pendingIntent_1 = PendingIntent.getBroadcast(this, 1, intent_1, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager_1 = (AlarmManager) getSystemService(ALARM_SERVICE);


        int notificationCounter= 0;
//        long alarmTime=0;

        Intent intent_2 = new Intent(this, NotificationHelper2.class);
        PendingIntent pendingIntent_2 = PendingIntent.getBroadcast(this, 2, intent_2, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager_2 = (AlarmManager) getSystemService(ALARM_SERVICE);

        Intent intent_3 = new Intent(this, NotificationHelper3.class);
        PendingIntent pendingIntent_3 = PendingIntent.getBroadcast(this, 3, intent_3, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager_3 = (AlarmManager) getSystemService(ALARM_SERVICE);


        long preTime = System.currentTimeMillis();
        long notificationTime1 = preTime + 10*1000;
        long notificationTime2 = preTime + 20*1000;
        long notificationTime3 = preTime + 35*1000;
        long alarmTime = preTime + 60*1000;

        alarmManager_1.set(AlarmManager.RTC_WAKEUP, notificationTime1, pendingIntent_1);

        alarmManager_2.set(AlarmManager.RTC_WAKEUP, notificationTime2, pendingIntent_2);

        alarmManager_3.set(AlarmManager.RTC_WAKEUP, notificationTime3, pendingIntent_3);

        setAlarm(alarmTime);

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


    private void setAlarm(long timeInMillis) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, MyAlarm.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0 , intent, 0);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, timeInMillis, AlarmManager.INTERVAL_FIFTEEN_MINUTES, pendingIntent);
        Toast.makeText(this, "Alarm is set", Toast.LENGTH_SHORT).show();
    }

    private void AlarmStop() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, MyAlarm.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0 , intent, 0);
        alarmManager.cancel(pendingIntent);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createNotificationChannel(String notifyName, String notifyId) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = notifyName;
            String description = "Channel for locating phone";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(notifyId, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

    }

}
