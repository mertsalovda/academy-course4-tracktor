package com.elegion.tracktor.service;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.elegion.tracktor.R;
import com.elegion.tracktor.event.GetRouteEvent;
import com.elegion.tracktor.event.StopBtnClickedEvent;
import com.elegion.tracktor.event.StopTrackEvent;
import com.elegion.tracktor.event.UpdateRouteEvent;
import com.elegion.tracktor.event.UpdateTimerEvent;
import com.elegion.tracktor.service.helpers.NotificationHelper;
import com.elegion.tracktor.service.helpers.TrackHelper;
import com.elegion.tracktor.util.StringUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class CounterService extends Service {

    public static final String CHANNEL_ID = "counter_service";
    public static final String CHANNEL_NAME = "Counter Service";
    public static final int NOTIFICATION_ID = 101;
    public static final int REQUEST_CODE_LAUNCH = 0;
    public static final int REQUEST_CODE_STOP = 1;

    private Disposable mTimerDisposable;

    private long mShutDownDuration;

    private NotificationHelper mNotificationHelper;
    private TrackHelper mTrackHelper;

    @Override
    public void onCreate() {
        super.onCreate();
        EventBus.getDefault().register(this);
        mNotificationHelper = new NotificationHelper(this);
        mTrackHelper = new TrackHelper(this);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PERMISSION_GRANTED) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                mNotificationHelper.createNotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_NONE);
            }

            Notification notification = mNotificationHelper.buildNotification();
            startForeground(NOTIFICATION_ID, notification);

            startTimer();

            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
            mShutDownDuration = Long.valueOf(preferences.getString(getString(R.string.pref_key_shutdown), "-1"));

        } else {
            Toast.makeText(this, R.string.permissions_denied, Toast.LENGTH_SHORT).show();
        }

    }

    private void startTimer() {
        mTimerDisposable = Observable.interval(1, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(CounterService.this::onTimerUpdate);
    }

    private void onTimerUpdate(long totalSeconds) {
        EventBus.getDefault().post(new UpdateTimerEvent(totalSeconds, mTrackHelper.getDistance()));
        mNotificationHelper.notify(NOTIFICATION_ID, totalSeconds, mTrackHelper.getDistance(), REQUEST_CODE_LAUNCH);

        if (mShutDownDuration != -1 && totalSeconds == mShutDownDuration) {
            EventBus.getDefault().post(new StopBtnClickedEvent());
            //configure btns state
            //from notification
        }

    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().post(new StopTrackEvent(mTrackHelper.getRoute()));

        mTrackHelper.destroy();
        mTimerDisposable.dispose();

        stopForeground(true);

        EventBus.getDefault().unregister(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetRoute(GetRouteEvent event) {
        EventBus.getDefault().post(new UpdateRouteEvent(mTrackHelper.getRoute(), mTrackHelper.getDistance()));
    }

}
