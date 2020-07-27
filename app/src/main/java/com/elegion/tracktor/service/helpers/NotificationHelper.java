package com.elegion.tracktor.service.helpers;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import com.elegion.tracktor.R;
import com.elegion.tracktor.service.CounterService;
import com.elegion.tracktor.ui.map.MainActivity;
import com.elegion.tracktor.util.StringUtil;

public class NotificationHelper {

    private NotificationCompat.Builder mNotificationBuilder;
    private NotificationManager mNotificationManager;
    private Service mService;
    private String mChanelID;

    public NotificationHelper(Service service) {
        mService = service;
        init();
    }

    private void init() {
        mNotificationManager = (NotificationManager) mService.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public Notification buildNotification() {
        return buildNotification("", "", CounterService.REQUEST_CODE_LAUNCH);
    }

    public Notification buildNotification(String time, String distance, int requestCode) {
        if (mNotificationBuilder == null) {
            configureNotificationBuilder(requestCode);
        }

        String message = mService.getString(R.string.notify_info, time, distance);

        return mNotificationBuilder
                .setContentText(message)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .build();
    }

    private void configureNotificationBuilder(int requestCode) {
        Intent notificationIntent = new Intent(mService, MainActivity.class);
        notificationIntent.setAction(Intent.ACTION_MAIN);
        notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP
                | Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent contentIntent = PendingIntent.getActivity(
                mService, requestCode, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent stopIntent = new Intent(mService, MainActivity.class);
        stopIntent.setAction(Intent.ACTION_MAIN);
        stopIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        stopIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP
                | Intent.FLAG_ACTIVITY_NEW_TASK);
        stopIntent.putExtra("ACTION", "STOP");
        PendingIntent stopPendingIntent = PendingIntent.getActivity(
                mService, CounterService.REQUEST_CODE_STOP, stopIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        mNotificationBuilder = new NotificationCompat.Builder(mService, mChanelID)
                .setContentIntent(contentIntent)
                .setOngoing(true)
                .setSmallIcon(R.drawable.ic_my_location_white_24dp)
                .setWhen(System.currentTimeMillis())
                .setContentTitle(mService.getString(R.string.route_active))
                .setVibrate(new long[]{0})
                .setColor(ContextCompat.getColor(mService, R.color.colorAccent))
                .addAction(R.drawable.ic_baseline_stop_24, mService.getString(R.string.stop), stopPendingIntent);
    }

    @RequiresApi(Build.VERSION_CODES.O)
    public void createNotificationChannel(String channelId, String channelName, int importanceNone) {
        mChanelID = channelId;
        if (mNotificationManager != null && mNotificationManager.getNotificationChannel(channelId) == null) {
            NotificationChannel chan = new NotificationChannel(channelId, channelName, importanceNone);
            chan.setLightColor(Color.BLUE);
            chan.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            mNotificationManager.createNotificationChannel(chan);
        }
    }

    public void notify(int notificationId, long totalSeconds, double distance, int requestCode) {
        Notification notification
                = buildNotification(
                StringUtil.getTimeText(totalSeconds),
                StringUtil.getDistanceText(distance),
                requestCode);
        mNotificationManager.notify(notificationId, notification);
    }
}
