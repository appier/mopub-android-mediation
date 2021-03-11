package com.mopub.mobileads;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.appier.ads.Appier;
import com.appier.ads.AppierBaseAd;
import com.appier.ads.common.AppierDataKeys;
import com.mopub.common.LifecycleListener;

import java.util.Map;

abstract class AppierBase extends BaseAd {

    protected AppierBaseAd appierBaseAd;
    protected String adUnitId = null;
    protected String zoneId = null;
    protected int adWidth = -1;
    protected int adHeight = -1;

    abstract AppierBaseAd getAppierAd(Context context, AppierAdUnitIdentifier adUnitIdentifier);
    abstract void setAppierAdForLoad();

    @Override
    protected void onInvalidate() {
        if (appierBaseAd != null) {
            appierBaseAd.setBaseAdEventListener(null);
            appierBaseAd.destroy();
            appierBaseAd = null;
        }
    }

    @Nullable
    @Override
    protected LifecycleListener getLifecycleListener() {
        return null;
    }

    @NonNull
    @Override
    protected String getAdNetworkId() {
        return adUnitId == null ? "" : adUnitId;
    }

    @Override
    protected boolean checkAndInitializeSdk(@NonNull Activity launcherActivity, @NonNull AdData adData) throws Exception {
        return false;
    }
    
    protected Map<String, String> getRewardedExtras(@NonNull AdData adData) {
        return null;
    }

    @Override
    protected void load(@NonNull Context context, @NonNull AdData adData) throws Exception {
        Appier.log("[Appier MoPub Mediation]", "AppierAd.load()");
        Map<String, String> extras = adData.getExtras();
        Map<String, String> rewardedExtras = getRewardedExtras(adData);

        setAdUnitId(extras, rewardedExtras);
        setZoneId(extras, rewardedExtras);
        setAdWidth(extras, rewardedExtras);
        setAdHeight(extras, rewardedExtras);

        if (!adParametersAreValid()) {
            mLoadListener.onAdLoadFailed(MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR);
            return;
        }
        appierBaseAd = getAppierAd(context, new AppierAdUnitIdentifier(adUnitId));
        setAppierAdForLoad();
        appierBaseAd.loadAd(zoneId);
    }


    protected boolean adParametersAreValid() {
        return adUnitIdIsValid() && zoneIdIsValid() && adWidthIsValid() && adHeightIsValid();
    }

    private void setAdUnitId(Map<String, String> extras, Map<String, String> rewardedExtras) {
        if (extras != null && extras.containsKey(AppierDataKeys.AD_UNIT_ID_SERVER)) {
            adUnitId = extras.get(AppierDataKeys.AD_UNIT_ID_SERVER);
        } else if (extras != null && extras.containsKey(AppierDataKeys.AD_UNIT_ID_LOCAL)) {
            adUnitId = extras.get(AppierDataKeys.AD_UNIT_ID_LOCAL);
        } else if (rewardedExtras != null && rewardedExtras.containsKey(AppierDataKeys.AD_UNIT_ID_LOCAL)) {
            adUnitId = rewardedExtras.get(AppierDataKeys.AD_UNIT_ID_LOCAL);
        }
    }
    
    protected boolean adUnitIdIsValid() {
        if (adUnitId == null) {
            Appier.log("[Appier MoPub Mediation]", "Ad Unit Id is empty, please add parameter in `localExtras` or `serverExtras`");
            return false;
        }
        return true;
    }

    private void setZoneId(Map<String, String> extras, Map<String, String> rewardedExtras) {
        if (extras != null && extras.containsKey(AppierDataKeys.ZONE_ID_SERVER)) {
            zoneId = extras.get(AppierDataKeys.ZONE_ID_SERVER);
        } else if (extras != null && extras.containsKey(AppierDataKeys.ZONE_ID_LOCAL)) {
            zoneId = extras.get(AppierDataKeys.ZONE_ID_LOCAL);
        } else if (rewardedExtras != null && rewardedExtras.containsKey(AppierDataKeys.ZONE_ID_LOCAL)) {
            zoneId = rewardedExtras.get(AppierDataKeys.ZONE_ID_LOCAL);
        }
    }

    protected boolean zoneIdIsValid() {
        if (zoneId == null) {
            Appier.log("[Appier MoPub Mediation]", "Zone Id is empty, please add parameter in `localExtras` or `serverExtras`");
            return false;
        }
        return true;
    }

    /*
     * `DataKeys.AD_WIDTH` and `DataKeys.AD_HEIGHT` comes from mopub web UI.
     * You can configure them through: `Edit ad unit` -> `Advanced options` -> `Safe area fallback`.
     * However, for now, they only supports 320x50 and 728x90.
     * You cannot override these two keys by `moPubView.setLocalExtras(localExtras)`.
     */
    private void setAdWidth(Map<String, String> extras, Map<String, String> rewardedExtras) {
        if (extras != null && extras.containsKey(AppierDataKeys.AD_WIDTH_SERVER)) {
            adWidth = Integer.parseInt(extras.get(AppierDataKeys.AD_WIDTH_SERVER));
        } else if (extras != null && extras.containsKey(AppierDataKeys.AD_WIDTH_LOCAL)) {
            adWidth = Integer.parseInt(extras.get(AppierDataKeys.AD_WIDTH_LOCAL));
        } else if (rewardedExtras != null && rewardedExtras.containsKey(AppierDataKeys.AD_WIDTH_LOCAL)) {
            adWidth = Integer.parseInt(rewardedExtras.get(AppierDataKeys.AD_WIDTH_LOCAL));
        }
    }

    protected boolean adWidthIsValid() {
        if (adWidth == -1) {
            Appier.log("[Appier MoPub Mediation]", "Ad width is empty, please add parameter in `localExtras` or `serverExtras`");
            return false;
        }
        return true;
    }

    private void setAdHeight(Map<String, String> extras, Map<String, String> rewardedExtras) {
        if (extras != null && extras.containsKey(AppierDataKeys.AD_HEIGHT_SERVER)) {
            adHeight = Integer.parseInt(extras.get(AppierDataKeys.AD_HEIGHT_SERVER));
        } else if (extras != null && extras.containsKey(AppierDataKeys.AD_HEIGHT_LOCAL)) {
            adHeight = Integer.parseInt(extras.get(AppierDataKeys.AD_HEIGHT_LOCAL));
        } else if (rewardedExtras != null && rewardedExtras.containsKey(AppierDataKeys.AD_HEIGHT_LOCAL)) {
            adHeight = Integer.parseInt(rewardedExtras.get(AppierDataKeys.AD_HEIGHT_LOCAL));
        }
    }

    protected boolean adHeightIsValid() {
        if (adHeight == -1) {
            Appier.log("[Appier MoPub Mediation]", "Ad height is empty, please add parameter in `localExtras` or `serverExtras`");
            return false;
        }

        return true;
    }
}
