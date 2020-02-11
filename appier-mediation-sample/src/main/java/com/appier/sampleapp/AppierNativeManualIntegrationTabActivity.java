/**
 * Reference: https://developer.android.com/guide/navigation/navigation-swipe-view
 */
package com.appier.sampleapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.util.Log;

import com.google.android.material.tabs.TabLayout;

public class AppierNativeManualIntegrationTabActivity extends AppCompatActivity {
    private static final String LOG_TAG = "AppierMediation";

    private TabLayout mTablayout;
    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;
    private FragmentManager mFragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appier_native_manual_integration_tab);

        mPager = findViewById(R.id.viewpager);
        mTablayout = findViewById(R.id.tabs);
        mFragmentManager = getSupportFragmentManager();
        mPagerAdapter = new PagerAdapter(mFragmentManager);

        mPager.setAdapter(mPagerAdapter);
        mTablayout.setupWithViewPager(mPager);

        mPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTablayout));
        mTablayout.addOnTabSelectedListener((new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Log.d(LOG_TAG, "[Sample App] onTabSelected(), position: " + tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                Log.d(LOG_TAG, "[Sample App] onTabUnselected(), position: " + tab.getPosition());
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                Log.d(LOG_TAG, "[Sample App] onTabReselected(), position: " + tab.getPosition());
            }
        }));
    }
}
