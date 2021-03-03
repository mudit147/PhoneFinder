package com.example.phonefinder;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
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
import android.os.PowerManager;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;

import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

import static com.example.phonefinder.PhoneFinderApplication.CHANNEL_1_ID;

//TODO: Convert the application to a background service
//TODO: Generate notifications and see if the notifications are missed
//TODO: if 5 notifications are missed, send alarm broadcasts: vibrate phone, flash screen/flashlight, ring the phone

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private NotificationManagerCompat notificationManagerCompat;

    private static final String TAG = "MainActivity";

    Sensor accelerometer;
    private Vibrator vibrator;
    Ringtone ringtone;
    Uri uri;

    private final int CAMERA_REQUEST_CODE = 2;
    boolean hasCameraFlash = false;
    private boolean isFlashOn = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(MainActivity.this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        notificationManagerCompat = NotificationManagerCompat.from(this);

        hasCameraFlash = getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
        uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        ringtone = RingtoneManager.getRingtone(this, uri);
//        AlarmManager alarmManager= (AlarmManager) getSystemService(ALARM_SERVICE);
    }

    //accelerometer reading changes
    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void onSensorChanged(SensorEvent sensorEvent){

        double[] axisReadings = convertFloatsToDoubles(sensorEvent.values.clone());
        double axisReadingNorm = Math.sqrt(axisReadings[0] * axisReadings[0] +
                axisReadings[1] * axisReadings[1] +
                axisReadings[2] * axisReadings[2]);

        axisReadings[0] /= axisReadingNorm;
        axisReadings[1] /= axisReadingNorm;
        axisReadings[2] /= axisReadingNorm;

        int inclination = (int) Math.round(Math.toDegrees(Math.acos(axisReadings[2])));

        boolean isPhoneFlat = false;

        if (inclination < 25 || inclination > 155){
            isPhoneFlat = true;
            Log.d(TAG, "FLAT");
            flashLightOn();
            vibrateThePhone();
        }
        else{
            Log.d(TAG, "UNFLAT");
        }

        // check if the device is locked since a certain amount of time and is lying straight on the table

        // long finishTime = System.currentTimeMillis();
//        AlarmManager alarmManager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
//        PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(), 0, )

        if(isDeviceLocked(getApplicationContext()) && isPhoneFlat){
        int countNotification =0;
            long startTime = System.currentTimeMillis()/1000;
                while ((isDeviceLocked(getApplicationContext()) && isPhoneFlat) || countNotification == 3){
                    long finishTime = System.currentTimeMillis()/1000;
                    long timeElapsed = finishTime - startTime;
//                    Log.d(TAG, Long.toString(timeElapsed));
                    long timeElapsedInMinutes = timeElapsed/5;//to be changed to timeElapsed/60
                    int notificationCounter = 0;

                    //more than 10 seconds
                    if (timeElapsedInMinutes == 2 || timeElapsedInMinutes > 2){
                        //push notification

                        //TODO: Generate notifications
                        Log.d(TAG, "Hey there!");

                        countNotification++;
                    }
                }

            Log.d(TAG, "locked" );
            //Start timer
        }

        else{
            Log.d(TAG, "unlocked");
        }
    }

    //generate notifications on channels
    public void sendOnChannel1(View v){
        String title = "Hey! Are you there?";
        String message = "Tap Here";

        Uri uri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_1_ID)
                .setSmallIcon(R.drawable.ic_notification_alert)
                .setContentTitle(title)
                .setContentText(message)
                .setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 })
                .setLights(Color.RED, 3000,3000)
                .setSound(uri)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .build();
        notificationManagerCompat.notify(1, notification);
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
            ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(VibrationEffect.createOneShot(150, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(150);
        }
    }


}