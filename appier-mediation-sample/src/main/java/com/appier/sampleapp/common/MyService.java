/*
 * Reference: https://xnfood.com.tw/android-service/
 */

package com.appier.sampleapp.common;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.appier.ads.Appier;

public class MyService extends Service {
    public class LocalBinder extends Binder {
        MyService getService() {
            return  MyService.this;
        }
    }

    private LocalBinder mLocBin = new LocalBinder();

    @Override
    public IBinder onBind(Intent arg0) {
        return mLocBin;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void myMethod() {
        Appier.log("[Sample Service]", "myMethod()");
    }
}
