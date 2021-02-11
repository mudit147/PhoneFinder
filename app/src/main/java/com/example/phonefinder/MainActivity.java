package com.example.phonefinder;

import android.app.KeyguardManager;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.Vibrator;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;

import android.view.View;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private static final String TAG = "MainActivity";

    private SensorManager sensorManager;
    Sensor accelerometer;

    TextView xValue, yValue, zValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        xValue = (TextView) findViewById(R.id.xValue);
        yValue = (TextView) findViewById(R.id.yValue);
        zValue = (TextView) findViewById(R.id.zValue);

        Log.d(TAG, "onCreate: Registered Sensor Services");
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(MainActivity.this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        Log.d(TAG, "onCreate: Registered accelerometer Listener");

    }

    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    public void onSensorChanged(SensorEvent sensorEvent){

        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        xValue.setText("xValue: " + sensorEvent.values[0]);
        yValue.setText("yValue: " + sensorEvent.values[1]);
        zValue.setText("zValue: " + sensorEvent.values[2]);

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
        }

        // check if the device is locked since a certain amount of time and is lying straight on the table

        // long finishTime = System.currentTimeMillis();

        if(isDeviceLocked(getApplicationContext()) && isPhoneFlat){
            long startTime = System.currentTimeMillis()/1000;
            while (isDeviceLocked(getApplicationContext()) && isPhoneFlat){
                long finishTime = System.currentTimeMillis()/1000;
                long timeElapsed = finishTime - startTime;
                Log.d(TAG, Long.toString(timeElapsed));
                long timeElapsedInMinutes = timeElapsed/5;
                int notificationCounter=0;
                if (timeElapsedInMinutes > 1){
                    //push notification
                    Log.d(TAG, "Sup?");

                }
            }

            Log.d(TAG, "locked" );
            //Start timer
        }
        else {
            Log.d(TAG, "unlocked");
        }
        //send some notifications and see if the notifications are missed

        //if 5 notifications are missed find the phone for the seniors

    }

    public static boolean isDeviceLocked(Context context) {
        boolean isLocked = false;

        // First we check the locked state
        KeyguardManager keyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        boolean inKeyguardRestrictedInputMode = keyguardManager.isKeyguardLocked();

        if (inKeyguardRestrictedInputMode) {
            isLocked = true;

        } else {
            // If password is not set in the settings, the inKeyguardRestrictedInputMode() returns false,
            // so we need to check if screen on for this case
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
}