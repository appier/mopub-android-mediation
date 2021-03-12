package com.mopub.mobileads;

import android.content.Context;

import androidx.annotation.NonNull;

import com.appier.ads.Appier;
import com.appier.ads.AppierBaseAd;
import com.appier.ads.AppierError;
import com.appier.ads.VastVideoAd;
import com.mopub.common.MoPubReward;

import java.util.Map;

public class AppierRewardedVideo extends AppierVideo {
    @Override
    AppierBaseAd getAppierAd(Context context, AppierAdUnitIdentifier adUnitIdentifier) {
        return new VastVideoAd(context, adUnitIdentifier, new AppierRewardedVideoListener());
    }

    @Override
    protected Map<String, String> getRewardedExtras(@NonNull AdData adData) {
        AppierMediationSettings mediationSettings = null;
        String adUnitId = adData.getAdUnit();

        if (adUnitId != null) {
            mediationSettings = MoPubRewardedVideoManager.getInstanceMediationSettings(AppierMediationSettings.class, adUnitId);
        }

        if (mediationSettings == null) {
            mediationSettings = MoPubRewardedVideoManager.getGlobalMediationSettings(AppierMediationSettings.class);
        }

        return (mediationSettings != null) ? mediationSettings.getLocalExtras() : null;
    }

    private class AppierRewardedVideoListener implements VastVideoAd.EventListener {

        @Override
        public void onAdLoaded(VastVideoAd vastVideoAd) {
            Appier.log("[Appier MoPub Mediation]", "AppierRewardedVideoListener.onAdLoaded() (Custom Callback)");
            mLoadListener.onAdLoaded();
        }

        @Override
        public void onAdNoBid(VastVideoAd vastVideoAd) {
            Appier.log("[Appier MoPub Mediation]", "AppierRewardedVideoListener.onAdNoBid() (Custom Callback)");
            mLoadListener.onAdLoadFailed(MoPubErrorCode.NO_FILL);
        }

        @Override
        public void onAdLoadFail(AppierError appierError, VastVideoAd vastVideoAd) {
            Appier.log("[Appier MoPub Mediation]", "AppierRewardedVideoListener.onAdLoadFail() (Custom Callback)");
            if (appierError == AppierError.NETWORK_ERROR) {
                mLoadListener.onAdLoadFailed(MoPubErrorCode.NETWORK_INVALID_STATE);
            } else if (appierError == AppierError.BAD_REQUEST) {
                mLoadListener.onAdLoadFailed(MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR);
            } else if (appierError == AppierError.INTERNAL_SERVER_ERROR) {
                mLoadListener.onAdLoadFailed(MoPubErrorCode.INTERNAL_ERROR);
            } else {
                mLoadListener.onAdLoadFailed(MoPubErrorCode.UNSPECIFIED);
            }
        }

        @Override
        public void onViewClick(VastVideoAd vastVideoAd) {
            Appier.log("[Appier MoPub Mediation]", "AppierRewardedVideoListener.onViewClick() (Custom Callback)");
            mInteractionListener.onAdClicked();
        }

        @Override
        public void onShown(VastVideoAd vastVideoAd) {
            Appier.log("[Appier MoPub Mediation]", "AppierRewardedVideoListener.onShown() (Custom Callback)");
            mInteractionListener.onAdShown();
        }

        @Override
        public void onAdVideoComplete(VastVideoAd vastVideoAd) {
            Appier.log("[Appier MoPub Mediation]", "AppierRewardedVideoListener.onAdVideoComplete() (Custom Callback)");
            MoPubReward moPubReward = MoPubReward.success(
                    MoPubReward.NO_REWARD_LABEL,
                    MoPubReward.DEFAULT_REWARD_AMOUNT
            );
            Appier.log("[Appier MoPub Mediation]", "Get reward [", moPubReward.getLabel(), "], amount [", moPubReward.getAmount(), "]");
            mInteractionListener.onAdComplete(moPubReward);
        }

        @Override
        public void onShowFail(AppierError appierError, VastVideoAd vastVideoAd) {
            Appier.log("[Appier MoPub Mediation]", "AppierRewardedVideoListener.onShowFail() (Custom Callback)");
            mInteractionListener.onAdFailed(MoPubErrorCode.NETWORK_NO_FILL);
        }

        @Override
        public void onDismiss(VastVideoAd vastVideoAd) {
            Appier.log("[Appier MoPub Mediation]", "AppierRewardedVideoListener.onDismiss() (Custom Callback)");
            mInteractionListener.onAdDismissed();
        }
    }
}
