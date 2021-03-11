package com.example.phonefinder;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.Random;

public class NotificationHelper2 extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        Random random = new Random();
        int m = random.nextInt(9999 - 1000) + 1000;

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "NotifySeniors_2")
                .setSmallIcon(R.drawable.ic_notification_alert)
                .setContentTitle("Hey! Did you find your phone?")
                .setContentText("Click on the notification if you found it")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);

//        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//        builder.setVibrate(new long[] { 100, 300, 300, 300});
//        builder.setLights(Color.RED, 3000, 3000);
//        builder.setSound(soundUri);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(200, builder.build());
    }
}