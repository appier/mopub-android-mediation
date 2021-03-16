package com.mopub.mobileads;

import android.content.Context;

import com.appier.ads.Appier;
import com.appier.ads.AppierBaseAd;
import com.appier.ads.AppierError;
import com.appier.ads.VideoAd;

public class AppierVideo extends AppierBase {
    @Override
    AppierBaseAd getAppierAd(Context context, AppierAdUnitIdentifier adUnitIdentifier) {
        return new VideoAd(context, adUnitIdentifier, new AppierVideoListener());
    }

    @Override
    void setAppierAdForLoad() {}

    @Override
    protected boolean adParametersAreValid() {
        return adUnitIdIsValid() && zoneIdIsValid();
    }

    @Override
    protected void show() {
        ((VideoAd) appierBaseAd).showAd();
    }

    private class AppierVideoListener implements VideoAd.EventListener {

        @Override
        public void onAdLoaded(VideoAd videoAd) {
            Appier.log("[Appier MoPub Mediation]", "AppierVideoListener.onAdLoaded() (Custom Callback)");
            mLoadListener.onAdLoaded();
        }

        @Override
        public void onAdNoBid(VideoAd videoAd) {
            Appier.log("[Appier MoPub Mediation]", "AppierVideoListener.onAdNoBid() (Custom Callback)");
            mLoadListener.onAdLoadFailed(MoPubErrorCode.NO_FILL);
        }

        @Override
        public void onAdLoadFail(AppierError appierError, VideoAd videoAd) {
            Appier.log("[Appier MoPub Mediation]", "AppierVideoListener.onAdLoadFail() (Custom Callback)");
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
        public void onViewClick(VideoAd videoAd) {
            Appier.log("[Appier MoPub Mediation]", "AppierVideoListener.onViewClick() (Custom Callback)");
            mInteractionListener.onAdClicked();
        }

        @Override
        public void onViewClickFail(AppierError appierError, VideoAd videoAd) {
            Appier.log("[Appier MoPub Mediation]", "AppierVideoListener.onViewClick() (Custom Callback)");
        }

        @Override
        public void onShown(VideoAd videoAd) {
            Appier.log("[Appier MoPub Mediation]", "AppierVideoListener.onShown() (Custom Callback)");
            mInteractionListener.onAdShown();
        }

        @Override
        public void onAdVideoComplete(VideoAd videoAd) {
            Appier.log("[Appier MoPub Mediation]", "AppierVideoListener.onAdVideoComplete() (Custom Callback)");
        }

        @Override
        public void onShowFail(AppierError appierError, VideoAd videoAd) {
            Appier.log("[Appier MoPub Mediation]", "AppierVideoListener.onShowFail() (Custom Callback)");
            mInteractionListener.onAdFailed(MoPubErrorCode.NETWORK_NO_FILL);
        }

        @Override
        public void onDismiss(VideoAd videoAd) {
            Appier.log("[Appier MoPub Mediation]", "AppierVideoListener.onDismiss() (Custom Callback)");
            mInteractionListener.onAdDismissed();
        }
    }
}
