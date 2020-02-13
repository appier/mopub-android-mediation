package com.appier.sampleapp;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.appier.ads.Appier;

public class PagerAdapter extends FragmentStatePagerAdapter {
    private String[] mTabTitles = new String[]{ "Activity Context", "Application Context", "Service Context" };
    private AppierNativeManualIntegrationTabFragment1 tab1;
    private AppierNativeManualIntegrationTabFragment2 tab2;

    private boolean mIsInitialAdLoaded = false;

    public PagerAdapter(FragmentManager fm) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        tab1 = new AppierNativeManualIntegrationTabFragment1(new BaseFragment.EventListener() {
            @Override
            public void onReady(BaseFragment fragment) {
                if (!mIsInitialAdLoaded) {
                    Appier.log("[Sample App]", "First fragment is ready. Start loading ad now");
                    fragment.loadAd();
                    mIsInitialAdLoaded = true;
                }
            }
        });
        tab2 = new AppierNativeManualIntegrationTabFragment2(null);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return tab1;
            case 1:
                return tab2;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mTabTitles.length;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mTabTitles[position];
    }

    public void destroy() {
        tab1.onDestroy();
        tab2.onDestroy();
    }
}
