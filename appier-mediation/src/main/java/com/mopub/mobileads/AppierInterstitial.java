package com.mopub.mobileads;

import android.content.Context;

import com.appier.ads.Appier;
import com.appier.ads.AppierError;
import com.appier.ads.AppierInterstitialAd;
import com.appier.ads.common.AppierDataKeys;

import java.util.Map;

public class AppierInterstitial extends CustomEventInterstitial implements AppierInterstitialAd.EventListener {
    private CustomEventInterstitial.CustomEventInterstitialListener mCustomEventInterstitialListener;
    private AppierInterstitialAd mAppierInterstitialAd;

    @Override
    protected void loadInterstitial(Context context,
                                    CustomEventInterstitialListener customEventInterstitialListener,
                                    Map<String, Object> localExtras,
                                    Map<String, String> serverExtras) {
        Appier.log("[Appier MoPub Mediation]", "AppierInterstitial.loadInterstitial()");
        this.mCustomEventInterstitialListener = customEventInterstitialListener;
        if (serverExtras.isEmpty()) {
            mCustomEventInterstitialListener.onInterstitialFailed(MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR);
            return;
        }
        String adUnitId = getAdUnitId(localExtras, serverExtras);
        String zoneId = getZoneId(localExtras, serverExtras);
        int adWidth = getAdWidth(localExtras, serverExtras);
        int adHeight = getAdHeight(localExtras, serverExtras);
        mAppierInterstitialAd = new AppierInterstitialAd(context, new AppierAdUnitIdentifier(adUnitId), this);
        mAppierInterstitialAd.setAdDimension(adWidth, adHeight);
        mAppierInterstitialAd.loadAd(zoneId);
    }

    private String getAdUnitId(final Map<String, Object> localExtras, final Map<String, String> serverExtras) {
        Object adUnitIdLocal = localExtras.get(AppierDataKeys.AD_UNIT_ID_LOCAL);
        String adUnitIdServer = serverExtras.get(AppierDataKeys.AD_UNIT_ID_SERVER);
        if (adUnitIdLocal != null) {
            return adUnitIdLocal.toString();
        }
        return adUnitIdServer;
    }

    private String getZoneId(final Map<String, Object> localExtras, final Map<String, String> serverExtras) {
        Object zoneIdLocal = localExtras.get(AppierDataKeys.ZONE_ID_LOCAL);
        String zoneIdServer = serverExtras.get(AppierDataKeys.ZONE_ID_SERVER);
        if (zoneIdLocal != null) {
            return zoneIdLocal.toString();
        }
        return zoneIdServer;
    }

    /*
     * `DataKeys.AD_WIDTH` and `DataKeys.AD_HEIGHT` comes from mopub web UI.
     * You can configure them through: `Edit ad unit` -> `Advanced options` -> `Safe area fallback`.
     * However, for now, they only supports 320x50 and 728x90.
     * You cannot override these two keys by `moPubView.setLocalExtras(localExtras)`.
     */
    private int getAdWidth(final Map<String, Object> localExtras, final Map<String, String> serverExtras) {
        Object adWidthAppierLocal = localExtras.get(AppierDataKeys.AD_WIDTH_LOCAL);
        String adWidthAppierServer = serverExtras.get(AppierDataKeys.AD_WIDTH_SERVER);
        if (adWidthAppierLocal != null) {
            return (int)adWidthAppierLocal;
        }
        if (adWidthAppierServer != null) {
            return Integer.parseInt(adWidthAppierServer);
        }
        return 0;
    }

    private int getAdHeight(final Map<String, Object> localExtras, final Map<String, String> serverExtras) {
        Object adHeightAppierLocal = localExtras.get(AppierDataKeys.AD_HEIGHT_LOCAL);
        String adHeightAppierServer = serverExtras.get(AppierDataKeys.AD_HEIGHT_SERVER);
        if (adHeightAppierLocal != null) {
            return (int)adHeightAppierLocal;
        }
        if (adHeightAppierServer != null) {
            return Integer.parseInt(adHeightAppierServer);
        }
        return 0;
    }

    @Override
    protected void showInterstitial() {
        Appier.log("[Appier MoPub Mediation]", "AppierInterstitial.showInterstitial()");
        mAppierInterstitialAd.showAd();
    }

    @Override
    protected void onInvalidate() {
        if (mAppierInterstitialAd != null) {
            mAppierInterstitialAd.destroy();
        }
    }

    /*
     * Appier SDK Event
     */
    @Override
    public void onAdLoaded(AppierInterstitialAd appierInterstitialAd) {
        Appier.log("[Appier MoPub Mediation]", "AppierInterstitial.onAdLoaded() (Custom Callback)");
        mCustomEventInterstitialListener.onInterstitialLoaded();
    }

    @Override
    public void onAdNoBid(AppierInterstitialAd appierInterstitialAd) {
        Appier.log("[Appier MoPub Mediation]", "AppierInterstitial.onAdNoBid() (Custom Callback)");
        mCustomEventInterstitialListener.onInterstitialFailed(MoPubErrorCode.NETWORK_NO_FILL);
    }

    @Override
    public void onAdLoadFail(AppierError appierError, AppierInterstitialAd appierInterstitialAd) {
        Appier.log("[Appier MoPub Mediation]", "AppierInterstitial.onAdLoadFail() (Custom Callback)", appierError.toString());
        if (appierError == AppierError.NETWORK_ERROR) {
            mCustomEventInterstitialListener.onInterstitialFailed(MoPubErrorCode.NETWORK_INVALID_STATE);
        } else if (appierError == AppierError.BAD_REQUEST) {
            mCustomEventInterstitialListener.onInterstitialFailed(MoPubErrorCode.NETWORK_NO_FILL);
        } else if (appierError == AppierError.INTERNAL_SERVER_ERROR) {
            mCustomEventInterstitialListener.onInterstitialFailed(MoPubErrorCode.NETWORK_INVALID_STATE);
        }
    }

    @Override
    public void onViewClick(AppierInterstitialAd appierInterstitialAd) {
        mCustomEventInterstitialListener.onInterstitialClicked();
    }

    @Override
    public void onShown(AppierInterstitialAd appierInterstitialAd) {
        mCustomEventInterstitialListener.onInterstitialShown();
    }

    @Override
    public void onShowFail(AppierError appierError, AppierInterstitialAd appierInterstitialAd) {
        if (appierError == AppierError.WEBVIEW_ERROR) {
            Appier.log("  fail to load the url:", mAppierInterstitialAd.getFailingUrl());
        }
        mCustomEventInterstitialListener.onInterstitialFailed(MoPubErrorCode.NETWORK_NO_FILL);
    }

    @Override
    public void onDismiss(AppierInterstitialAd appierInterstitialAd) {
        mCustomEventInterstitialListener.onInterstitialDismissed();
    }
}
