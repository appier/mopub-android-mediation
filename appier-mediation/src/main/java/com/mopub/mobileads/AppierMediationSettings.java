package com.mopub.mobileads;

import androidx.annotation.Nullable;

import com.mopub.common.MediationSettings;

import java.util.Map;

public class AppierMediationSettings implements MediationSettings {
    @Nullable
    private Map<String, String> localExtras;

    public AppierMediationSettings() {}

    public AppierMediationSettings withLocalExtras(Map<String, String> localExtras) {
        this.localExtras = localExtras;
        return this;
    }

    @Nullable
    public Map<String, String> getLocalExtras() {
        return localExtras;
    }
}
