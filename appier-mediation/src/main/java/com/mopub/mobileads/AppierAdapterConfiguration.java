package com.mopub.mobileads;

import com.appier.ads.Appier;
import com.mopub.nativeads.BuildConfig;

public class AppierAdapterConfiguration {
    public String getAdapterVersion() {
        return BuildConfig.VERSION_NAME;
    }

    public String getNetworkSdkVersion() {
        return Appier.getVersionName();
    }
}
