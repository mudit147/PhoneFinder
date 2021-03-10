package com.example.phonefinder;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

//class extending the Broadcast Receiver
public class MyAlarm extends BroadcastReceiver {
    //the method will be fired when the alarm is triggered
//    Context mContext;
//    private final int CAMERA_REQUEST_CODE = 2;
//    boolean hasCameraFlash = false;
//
//    public MyAlarm(Context mContext) {
//        this.mContext = mContext;
//    }

//    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onReceive(Context context, Intent intent) {
        MediaPlayer mediaPlayer = MediaPlayer.create(context, Settings.System.DEFAULT_RINGTONE_URI);
        mediaPlayer.start();
        Log.d("Alarm!!", "Alarm just fired");
//        flashLightOn();
        Vibrator v=(Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(5000);
    }


//    @SuppressLint("NewApi")
//    private void flashLight()
//    {
//    }
//
//    @SuppressLint("NewApi")
//    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
//    private void flashLightOn()
//    {
//        CameraManager cameraManager = (CameraManager) mContext.getSystemService(Context.CAMERA_SERVICE);
//
//        try {
//            String cameraId = cameraManager.getCameraIdList()[0];
//            cameraManager.setTorchMode(cameraId, true);
//        } catch (CameraAccessException e) {
//        }
//    }
//
//    @SuppressLint("NewApi")
//    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
//    private void flashLightOff()
//    {
//        CameraManager cameraManager = (CameraManager) mContext.getSystemService(Context.CAMERA_SERVICE);
//        try {
//            String cameraId = cameraManager.getCameraIdList()[0];
//            cameraManager.setTorchMode(cameraId, false);
//        } catch (CameraAccessException e) {
//        }
//    }
//
//
//
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
//    {
//        switch (requestCode) {
//            case CAMERA_REQUEST_CODE:
//                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    hasCameraFlash = mContext.getPackageManager().
//                            hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
//                    Toast.makeText(this.mContext, "Camera Permission Granted", Toast.LENGTH_LONG).show();
//                    flashLight();
//
//                } else {
//                    Toast.makeText(this.mContext, "Camera Permission Denied", Toast.LENGTH_LONG).show();
//                }
//                break;
//        }
//    }

}