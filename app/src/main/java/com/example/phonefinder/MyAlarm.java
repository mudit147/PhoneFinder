package com.example.phonefinder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Vibrator;
import android.provider.Settings;
import android.util.Log;

//class extending the Broadcast Receiver
public class MyAlarm extends BroadcastReceiver {
    //the method will be fired when the alarm is triggered

    @Override
    public void onReceive(Context context, Intent intent) {
        MediaPlayer mediaPlayer = MediaPlayer.create(context, Settings.System.DEFAULT_RINGTONE_URI);
        mediaPlayer.start();
        Log.d("Alarm!!", "Alarm just fired");

        Vibrator v=(Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(5000);
    }


}