package com.example.phonefinder;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import android.os.SystemClock;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;

import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

//TODO: Convert the application to a background service
//TODO: Generate notifications and see if the notifications are missed
//TODO: if 5 notifications are missed, send alarm broadcasts: vibrate phone, flash screen/flashlight, ring the phone

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private NotificationManagerCompat notificationManagerCompat;

    private static final String TAG = "MainActivity";

    Sensor accelerometer;
    public Vibrator vibrator;
    Ringtone ringtone;
    Uri uri;

    private final int CAMERA_REQUEST_CODE = 2;
    boolean hasCameraFlash = false;



    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(MainActivity.this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);

//        notificationManagerCompat = NotificationManagerCompat.from(this);

        hasCameraFlash = getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
        uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        ringtone = RingtoneManager.getRingtone(this, uri);
        vibrator = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);

        if (isDeviceLocked(getApplicationContext())){
            Log.v(TAG, "locked");
            setAlarm(System.currentTimeMillis() + 5000);
            flashLightOn();
//            vibrateThePhone();
//            vibrator.vibrate(5000);
        }
    }

    //accelerometer reading changes
    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void onSensorChanged(SensorEvent sensorEvent) {

        double[] axisReadings = convertFloatsToDoubles(sensorEvent.values.clone());
        double axisReadingNorm = Math.sqrt(axisReadings[0] * axisReadings[0] +
                axisReadings[1] * axisReadings[1] +
                axisReadings[2] * axisReadings[2]);

        axisReadings[0] /= axisReadingNorm;
        axisReadings[1] /= axisReadingNorm;
        axisReadings[2] /= axisReadingNorm;

        int inclination = (int) Math.round(Math.toDegrees(Math.acos(axisReadings[2])));

        boolean isPhoneFlat = false;

        if (inclination < 25 || inclination > 155) {
            isPhoneFlat = true;
//            Log.d(TAG, "FLAT");
            vibrateThePhone();

        } else {
//            Log.d(TAG, "UNFLAT");
        }

        final NotificationHelper notificationHelper = new NotificationHelper(getApplicationContext());
        // check if the device is locked since a certain amount of time and is lying straight on the table

//        if(isDeviceLocked(getApplicationContext()) && isPhoneFlat){
//        int countNotification =0;
//            long startTime = System.currentTimeMillis()/1000;
//                while ((isDeviceLocked(getApplicationContext()) && isPhoneFlat) || countNotification == 3){
//                    long finishTime = System.currentTimeMillis()/1000;
//                    long timeElapsed = finishTime - startTime;
////                    Log.d(TAG, Long.toString(timeElapsed));
//                    long timeElapsedInMinutes = timeElapsed/5;//to be changed to timeElapsed/60
//                    int notificationCounter = 0;
//
//                    //more than 10 seconds
//                    if (timeElapsedInMinutes == 2 || timeElapsedInMinutes > 2){
//                        //push notification
//
//                        //TODO: Generate notifications
//                        notificationHelper.sendHighPriorityNotification("Help me!", "");
//                        Log.d(TAG, "Hey there!");
//                        countNotification++;
//                    }
//                }
//
//            Log.d(TAG, "locked" );
//            //Start timer
//        }
//
//        else{
//            Log.d(TAG, "unlocked");
//        }

//        long startTime = 0;
//        if (isDeviceLocked(getApplicationContext()) && isPhoneFlat) {
////            startTime = System.currentTimeMillis();
//            Handler handler = new Handler(Looper.myLooper());
//            handler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    Log.v(TAG, "hello");
////                    notificationHelper.sendHighPriorityNotification("Help me!", "");
//                    Toast.makeText(MainActivity.this, "help!", Toast.LENGTH_SHORT).show();
//                    flashLightOn();
////                    vibrator.vibrate(50);
//                    vibrateThePhone();
//                }
//            }, 10000);
//        }
//
//        else {
//            flashLightOff();
//
//        }
//        if (isDeviceLocked(getApplicationContext()) && isPhoneFlat){
//            setAlarm(System.currentTimeMillis() + 5000);
//        }



    }

    private void setAlarm(long timeInMillis) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, MyAlarm.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0 , intent, 0);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, timeInMillis, AlarmManager.INTERVAL_FIFTEEN_MINUTES, pendingIntent);
        Toast.makeText(this, "Alarm is set", Toast.LENGTH_SHORT).show();
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


    @SuppressLint("NewApi")
    private void flashLight()
    {
    }

    @SuppressLint("NewApi")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void flashLightOn()
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
            ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(150);
        }
    }


}