package com.dima.acceptic.ui;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.dima.acceptic.R;
import com.dima.acceptic.service.RandomService;

/**
 * Created by dima on 03.12.16.
 */

public class NotificationValueListener implements RandomService.ValueListener {
    public static final int ID = 101;
    private Context context;
    private final NotificationManager notificationManager;

    public NotificationValueListener(Context context) {
        this.context = context;
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @Override
    public void onValueReady(Integer integer) {
        showNotification(integer);
    }

    private void showNotification(Integer latestValue) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setAutoCancel(true)
                        .setDefaults(NotificationCompat.DEFAULT_ALL)
                        .setContentTitle("Service")
                        .setContentText(context.getString(R.string.result, latestValue));
        Intent resultIntent = new Intent(context, MainActivity.class);
        resultIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(context, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        notificationManager.notify(ID, mBuilder.build());
    }

    public void cancelNotification() {
        notificationManager.cancel(ID);
    }
}
