/*
 * Reference: https://developer.android.com/guide/navigation/navigation-swipe-view
 */
package com.appier.sampleapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.appier.ads.Appier;
import com.google.android.material.tabs.TabLayout;

public class AppierNativeManualIntegrationTabActivity extends AppCompatActivity {
    private TabLayout mTabLayout;
    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;
    private FragmentManager mFragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appier_native_manual_integration_tab);

        mPager = findViewById(R.id.viewpager);
        mTabLayout = findViewById(R.id.tabs);
        mFragmentManager = getSupportFragmentManager();
        mPagerAdapter = new PagerAdapter(mFragmentManager);

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
        super.onDestroy();
    }
}
