package com.mopub.mobileads;

import android.content.Context;

import com.appier.ads.Appier;
import com.appier.ads.AppierBaseAd;
import com.appier.ads.AppierError;
import com.appier.ads.AppierInterstitialAd;

public class AppierInterstitial extends AppierBase {

    @Override
    AppierBaseAd getAppierAd(Context context, AppierAdUnitIdentifier adUnitIdentifier) {
        return new AppierInterstitialAd(context, adUnitIdentifier, new AppierInterstitialListener());
    }

    @Override
    void setAppierAdForLoad() {
        ((AppierInterstitialAd) appierBaseAd).setAdDimension(adWidth, adHeight);
    }

    @Override
    protected void show() {
        Appier.log("[Appier MoPub Mediation]", "AppierInterstitial.show() (Custom Callback)");
        ((AppierInterstitialAd) appierBaseAd).showAd();
    }

    private class AppierInterstitialListener implements AppierInterstitialAd.EventListener {

        @Override
        public void onAdLoaded(AppierInterstitialAd appierInterstitialAd) {
            Appier.log("[Appier MoPub Mediation]", "AppierInterstitialListener.onAdLoaded() (Custom Callback)");
            mLoadListener.onAdLoaded();
        }

        @Override
        public void onAdNoBid(AppierInterstitialAd appierInterstitialAd) {
            Appier.log("[Appier MoPub Mediation]", "AppierInterstitialListener.onAdNoBid() (Custom Callback)");
            mLoadListener.onAdLoadFailed(MoPubErrorCode.NO_FILL);
        }

        @Override
        public void onAdLoadFail(AppierError appierError, AppierInterstitialAd appierInterstitialAd) {
            Appier.log("[Appier MoPub Mediation]", "AppierInterstitialListener.onAdLoadFail() (Custom Callback)");
            if (appierError == AppierError.NETWORK_ERROR) {
                mLoadListener.onAdLoadFailed(MoPubErrorCode.NETWORK_INVALID_STATE);
            } else if (appierError == AppierError.BAD_REQUEST) {
                mLoadListener.onAdLoadFailed(MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR);
            } else if (appierError == AppierError.INTERNAL_SERVER_ERROR) {
                mLoadListener.onAdLoadFailed(MoPubErrorCode.INTERNAL_ERROR);
            } else if (appierError == AppierError.WEBVIEW_ERROR) {
                Appier.log("[Appier MoPub Mediation]", "Fail to load the url:", appierInterstitialAd.getFailingUrl());
                mLoadListener.onAdLoadFailed(MoPubErrorCode.HTML_LOAD_ERROR);
            } else {
                mLoadListener.onAdLoadFailed(MoPubErrorCode.UNSPECIFIED);
            }
        }

        @Override
        public void onViewClick(AppierInterstitialAd appierInterstitialAd) {
            Appier.log("[Appier MoPub Mediation]", "AppierInterstitialListener.onViewClick() (Custom Callback)");
            mInteractionListener.onAdClicked();
        }

        @Override
        public void onShown(AppierInterstitialAd appierInterstitialAd) {
            Appier.log("[Appier MoPub Mediation]", "AppierInterstitialListener.onShown() (Custom Callback)");
            mInteractionListener.onAdShown();
        }

        @Override
        public void onShowFail(AppierError appierError, AppierInterstitialAd appierInterstitialAd) {
            Appier.log("[Appier MoPub Mediation]", "AppierInterstitialListener.onShowFail() (Custom Callback)");
            if (appierError == AppierError.WEBVIEW_ERROR) {
                Appier.log("[Appier MoPub Mediation]", "Fail to load the url:", appierInterstitialAd.getFailingUrl());
            }
            mInteractionListener.onAdFailed(MoPubErrorCode.NETWORK_NO_FILL);
        }

        @Override
        public void onDismiss(AppierInterstitialAd appierInterstitialAd) {
            Appier.log("[Appier MoPub Mediation]", "AppierInterstitialListener.onDismiss() (Custom Callback)");
            mInteractionListener.onAdDismissed();
        }
    }
}
