package com.mopub.mobileads;

import com.appier.ads.common.AdUnitIdentifier;

public class AppierAdUnitIdentifier extends AdUnitIdentifier {
    public AppierAdUnitIdentifier(String adUnitId) {
        super(adUnitId);
    }

    @Override
    public String build() {
        return "mopub_" + getAdUnitId();
    }
}
