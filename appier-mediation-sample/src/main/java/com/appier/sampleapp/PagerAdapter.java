package com.appier.sampleapp;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class PagerAdapter extends FragmentStatePagerAdapter {
    private String[] mTabTitles = new String[]{ "Tab 1", "Tab 2" };

    public PagerAdapter(FragmentManager fm) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                AppierNativeManualIntegrationTabFragment1 tab1 = new AppierNativeManualIntegrationTabFragment1();
                return tab1;
            case 1:
                AppierNativeManualIntegrationTabFragment2 tab2 = new AppierNativeManualIntegrationTabFragment2();
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
}
