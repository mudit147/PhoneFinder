package com.example.phonefinder;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.Random;

public class NotificationHelper3 extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Random random = new Random();
        int m = random.nextInt(9999 - 1000) + 1000;

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "NotifySeniors_3")
                .setSmallIcon(R.drawable.ic_notification_alert)
                .setContentTitle("Hey! This is the last notification Reminder. Did you find your phone?")
                .setContentText("Click on the notification if you found it")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(300, builder.build());
    }
}
