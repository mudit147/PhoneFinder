package com.example.phonefinder;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

//public class MainActivity extends AppCompatActivity implements SensorEventListener {
 @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
 public class MainActivity extends AppCompatActivity
{
    private static final String TAG = "MainActivity";

    private static final int LED_NOTIFICATION_ID= 0; //arbitrary constant


    private final int CAMERA_REQUEST_CODE = 2;
    boolean hasCameraFlash = false;


    public static boolean isAlarmFlash = false;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void startService (View view) {
        Intent intent = new Intent(this, PhoneFinderService.class);
        startService(intent);

    }

    public void stopService (View view) {
        Intent intent = new Intent(this, PhoneFinderService.class);
        stopService(intent);
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
        super.onStop();
        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }
    }
    //checking if the device is locked
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

    private double[] convertFloatsToDoubles(float[] input)
    {
        if (input == null)
            return null;

        double[] output = new double[input.length];

        for (int i = 0; i < input.length; i++)
            output[i] = input[i];

        return output;
    }

    public void RedFlashLight() {
        NotificationManager nm = (NotificationManager) getSystemService( NOTIFICATION_SERVICE);
        Notification notif = new Notification();
        notif.ledARGB = 0xFFff0000;
        notif.flags = Notification.FLAG_SHOW_LIGHTS;
        notif.ledOnMS = 100;
        notif.ledOffMS = 100;
        nm.notify(LED_NOTIFICATION_ID, notif);
    }

    @SuppressLint("NewApi")
    private void flashLight()
    {
    }

    @SuppressLint("NewApi")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void flashLightOn()
    {
        CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);

        try {
            String cameraId = cameraManager.getCameraIdList()[0];
            cameraManager.setTorchMode(cameraId, true);
        } catch (CameraAccessException e) {
        }
    }

    @SuppressLint("NewApi")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void flashLightOff()
    {
        CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            String cameraId = cameraManager.getCameraIdList()[0];
            cameraManager.setTorchMode(cameraId, false);
        } catch (CameraAccessException e) {
        }
    }

    private void askPermission(String permission, int requestCode)
    {
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            // We Dont have permission
            ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);

        } else {
            // We already have permission do what you want
            flashLight();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        switch (requestCode) {
            case CAMERA_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    hasCameraFlash = getPackageManager().
                            hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
                    Toast.makeText(this, "Camera Permission Granted", Toast.LENGTH_LONG).show();
                    flashLight();

                } else {
                    Toast.makeText(this, "Camera Permission Denied", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    private void vibrateThePhone() {
        if (Build.VERSION.SDK_INT >= 26) {
            ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(VibrationEffect.createOneShot(30000, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(50);
        }
    }


}