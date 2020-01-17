package com.mopub.mobileads;

import android.content.Context;
import android.util.Log;

import com.appier.ads.AppierBannerAd;
import com.appier.ads.AppierError;
import com.appier.ads.common.AppierDataKeys;

import com.mopub.common.DataKeys;

import java.util.Map;

public class AppierBanner extends CustomEventBanner implements AppierBannerAd.EventListener {
    private static final String LOG_TAG = "AppierMediation";

    private CustomEventBannerListener mCustomEventBannerListener;
    private AppierBannerAd mAppierBannerAd;

    @Override
    protected void loadBanner(final Context context,
                              final CustomEventBannerListener customEventBannerListener,
                              final Map<String, Object> localExtras,
                              final Map<String, String> serverExtras) {
        Log.d(LOG_TAG, "[Appier Mediation] AppierBanner.loadBanner()");
        this.mCustomEventBannerListener = customEventBannerListener;
        if (serverExtras.isEmpty()) {
            mCustomEventBannerListener.onBannerFailed(MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR);
            return;
        }
        String zoneId = getZoneId(localExtras, serverExtras);
        int adWidth = getAdWidth(localExtras, serverExtras);
        int adHeight = getAdHeight(localExtras, serverExtras);
        mAppierBannerAd = new AppierBannerAd(context, this);
        mAppierBannerAd.setAdDimension(adWidth, adHeight);
        mAppierBannerAd.loadAd(zoneId);
    }

    @Override
    protected void onInvalidate() {
        if (mAppierBannerAd != null) {
            mAppierBannerAd.destroy();
            mAppierBannerAd = null;
        }
    }

    private String getZoneId(final Map<String, Object> localExtras, final Map<String, String> serverExtras) {
        Object zoneIdLocal = localExtras.get(AppierDataKeys.ZONE_ID_LOCAL);
        String zoneIdServer = serverExtras.get(AppierDataKeys.ZONE_ID_SERVER);
        if (zoneIdLocal != null) {
            return zoneIdLocal.toString();
        }
        if (zoneIdServer != null) {
            return zoneIdServer;
        }
        return null;
    }

    /**
     * `DataKeys.AD_WIDTH` and `DataKeys.AD_HEIGHT` comes from mopub web UI.
     * You can configure them through: `Edit ad unit` -> `Advanced options` -> `Safe area fallback`.
     * However, for now, they only supports 320x50 and 728x90.
     * You cannot override these two keys by `moPubView.setLocalExtras(localExtras)`.
     */
    private int getAdWidth(final Map<String, Object> localExtras, final Map<String, String> serverExtras) {
        Object adWidthAppierLocal = localExtras.get(AppierDataKeys.AD_WIDTH_LOCAL);
        String adWidthAppierServer = serverExtras.get(AppierDataKeys.AD_WIDTH_SERVER);
        int adWidthMopub = (int)localExtras.get(DataKeys.AD_WIDTH);
        if (adWidthAppierLocal != null) {
            return (int)adWidthAppierLocal;
        }
        if (adWidthAppierServer != null) {
            return Integer.parseInt(adWidthAppierServer);
        }
        return adWidthMopub;
    }

    private int getAdHeight(final Map<String, Object> localExtras, final Map<String, String> serverExtras) {
        Object adHeightAppierLocal = localExtras.get(AppierDataKeys.AD_HEIGHT_LOCAL);
        String adHeightAppierServer = serverExtras.get(AppierDataKeys.AD_HEIGHT_SERVER);
        int adHeightMopub = (int)localExtras.get(DataKeys.AD_HEIGHT);
        if (adHeightAppierLocal != null) {
            return (int)adHeightAppierLocal;
        }
        if (adHeightAppierServer != null) {
            return Integer.parseInt(adHeightAppierServer);
        }
        return adHeightMopub;
    }

    /**
     * Appier SDK Event
     */
    @Override
    public void onAdLoaded(AppierBannerAd appierBannerAd) {
        Log.d(LOG_TAG, "[Appier Mediation] AppierBanner.onAdLoaded() (Custom Callback)");
        mCustomEventBannerListener.onBannerLoaded(appierBannerAd.getView());
    }

    @Override
    public void onAdNoBid(AppierBannerAd appierBannerAd) {
        Log.d(LOG_TAG, "[Appier Mediation] AppierBanner.onAdNoBid() (Custom Callback)");
        mCustomEventBannerListener.onBannerFailed(MoPubErrorCode.NETWORK_NO_FILL);
    }

    @Override
    public void onAdLoadFail(AppierError appierError, AppierBannerAd appierBannerAd) {
        Log.d(LOG_TAG, "[Appier Mediation] AppierBanner.onAdLoadFail() (Custom Callback) " + appierError.toString());
        if (appierError == AppierError.NETWORK_ERROR) {
            mCustomEventBannerListener.onBannerFailed(MoPubErrorCode.NETWORK_INVALID_STATE);
        } else if (appierError == AppierError.BAD_REQUEST) {
            mCustomEventBannerListener.onBannerFailed(MoPubErrorCode.NETWORK_NO_FILL);
        } else if (appierError == AppierError.INTERNAL_SERVER_ERROR) {
            mCustomEventBannerListener.onBannerFailed(MoPubErrorCode.NETWORK_INVALID_STATE);
        }
    }
}
