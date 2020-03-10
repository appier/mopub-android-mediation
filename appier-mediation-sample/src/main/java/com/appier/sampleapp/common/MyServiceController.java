package com.appier.sampleapp.common;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.appier.ads.Appier;

import static android.content.Context.BIND_AUTO_CREATE;

public class MyServiceController {
    private Activity mActivity;
    private EventListener mEventListener;
    private MyService mMyService = null;
    private Intent mServiceIntent;
    private ServiceConnection mServiceConnection;

    public MyServiceController(Activity activity) {
        mActivity = activity;
    }

    public MyService getService() {
        return mMyService;
    }

    public void setEventListener(EventListener eventListener) {
        mEventListener = eventListener;
    }

    public void startMyService() {
        mServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder serviceBinder) {
                Appier.log("[Sample App]", "onServiceConnected()");
                mMyService = ((MyService.LocalBinder)serviceBinder).getService();
                mMyService.myMethod();
                if (mEventListener != null) {
                    mEventListener.onServiceStart();
                }
            }

            public void onServiceDisconnected(ComponentName name) {
                Appier.log("[Sample App]", "onServiceDisconnected()", name.getClassName());
            }
        };
        mServiceIntent = new Intent(mActivity, MyService.class);
        mActivity.startService(mServiceIntent);
        mActivity.bindService(mServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
    }

    public void stopMyService() {
        if (mServiceConnection != null) {
            mActivity.unbindService(mServiceConnection);
        }
        if (mServiceIntent != null) {
            mActivity.stopService(mServiceIntent);
        }
        if (mEventListener != null) {
            mEventListener.onServiceStop();
        }
    }

    public interface EventListener {
        void onServiceStart();
        void onServiceStop();
    }
}
