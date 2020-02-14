/*
 * Reference: https://developer.android.com/guide/navigation/navigation-swipe-view
 */
package com.appier.sampleapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;

import com.appier.ads.Appier;
import com.google.android.material.tabs.TabLayout;

public class AppierNativeManualIntegrationTabActivity extends AppCompatActivity {
    private TabLayout mTabLayout;
    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;
    private FragmentManager mFragmentManager;
    private MyService mMyService = null;
    private Intent mServiceIntent;
    private ServiceConnection mServiceConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appier_native_manual_integration_tab);

        /*
         * Start service for tab3
         */
        mServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder serviceBinder) {
                Appier.log("[Sample App]", "onServiceConnected()");
                mMyService = ((MyService.LocalBinder)serviceBinder).getService();
                mMyService.myMethod();
                setupTabAndPager();
            }

            public void onServiceDisconnected(ComponentName name) {
                Appier.log("[Sample App]", "onServiceDisconnected()", name.getClassName());
            }
        };
        mServiceIntent = new Intent(AppierNativeManualIntegrationTabActivity.this, MyService.class);
        startService(mServiceIntent);
        bindService(mServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
    }

    public void setupTabAndPager() {
        mPager = findViewById(R.id.viewpager);
        mTabLayout = findViewById(R.id.tabs);
        mFragmentManager = getSupportFragmentManager();
        mPagerAdapter = new PagerAdapter(mFragmentManager, mMyService);

        mPager.setAdapter(mPagerAdapter);

        final TabLayout.OnTabSelectedListener onTabSelectedListener = new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Appier.log("[Sample App]", "onTabSelected()", "position:", tab.getPosition());
                int position = tab.getPosition();
                Fragment fragment = mPagerAdapter.getItem(position);
                ((BaseFragment)fragment).loadAd();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                Appier.log("[Sample App]", "onTabUnselected()", "position:", tab.getPosition());
                int position = tab.getPosition();
                Fragment fragment = mPagerAdapter.getItem(position);
                ((BaseFragment)fragment).clearAd();
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                Appier.log("[Sample App]", "onTabReselected()", "position:", tab.getPosition());
                int position = tab.getPosition();
                Fragment fragment = mPagerAdapter.getItem(position);
                ((BaseFragment)fragment).clearAd();
                ((BaseFragment)fragment).loadAd();
            }
        };
        mTabLayout.addOnTabSelectedListener(onTabSelectedListener);
        mTabLayout.setupWithViewPager(mPager);
        mPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
    }

    @Override
    protected void onDestroy() {
        mPagerAdapter.destroy();
        if (mServiceConnection != null) {
            unbindService(mServiceConnection);
        }
        if (mServiceIntent != null) {
            stopService(mServiceIntent);
        }
        super.onDestroy();
    }
}
