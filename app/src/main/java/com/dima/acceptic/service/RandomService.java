package com.dima.acceptic.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.dima.acceptic.ui.NotificationValueListener;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public class RandomService extends Service {

    public static final int PERIOD = 10;
    private Subscription subscription;
    private Integer latestValue;
    private final IBinder mBinder = new LocalBinder();
    private Random random = new Random(Integer.MAX_VALUE);
    private NotificationValueListener defaultListener;

    private ValueListener valueListener;

    public RandomService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        defaultListener = new NotificationValueListener(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        runComputing();
        return START_STICKY;
    }

    private void runComputing() {
        if (subscription != null && !subscription.isUnsubscribed()) {
            return;
        }
        subscription = Observable.interval(0, PERIOD, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        latestValue = random.nextInt();
                        notifyListeners();
                    }
                });
    }

    public void setValueListener(ValueListener valueListener) {
        runComputing();
        this.valueListener = valueListener;
        defaultListener.cancelNotification();
        if (latestValue != null && valueListener != null) {
            notifyListeners();
        }
    }

    private void notifyListeners() {
        if (valueListener != null) {
            valueListener.onValueReady(latestValue);
        } else {
            defaultListener.onValueReady(latestValue);
        }
    }

    @Override
    public void onDestroy() {
        if (subscription != null) {
            subscription.unsubscribe();
        }
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class LocalBinder extends Binder {
        public RandomService getService() {
            return RandomService.this;
        }
    }

    public interface ValueListener {
        void onValueReady(Integer integer);
    }
}
