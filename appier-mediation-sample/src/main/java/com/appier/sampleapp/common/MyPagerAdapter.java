package com.appier.sampleapp.common;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.appier.sampleapp.AppierNativeManualIntegrationTabFragment1;
import com.appier.sampleapp.AppierNativeManualIntegrationTabFragment2;
import com.appier.sampleapp.AppierNativeManualIntegrationTabFragment3;
import com.appier.sampleapp.common.MyService;

public class MyPagerAdapter extends FragmentStatePagerAdapter {
    private String[] mTabTitles = new String[]{ "Activity Context", "Application Context", "Service Context" };
    private AppierNativeManualIntegrationTabFragment1 tab1;
    private AppierNativeManualIntegrationTabFragment2 tab2;
    private AppierNativeManualIntegrationTabFragment3 tab3;

    public MyPagerAdapter(FragmentManager fm, MyService mMyService) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        tab1 = new AppierNativeManualIntegrationTabFragment1(null);
        tab2 = new AppierNativeManualIntegrationTabFragment2(null);
        tab3 = new AppierNativeManualIntegrationTabFragment3(mMyService, null);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return tab1;
            case 1:
                return tab2;
            case 2:
                return tab3;
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
        tab3.onDestroy();
    }
}
