package com.mopub.mobileads;

import android.content.Context;
import android.view.View;

import androidx.annotation.Nullable;

import com.appier.ads.Appier;
import com.appier.ads.AppierBannerAd;
import com.appier.ads.AppierBaseAd;
import com.appier.ads.AppierError;


public class AppierBanner extends AppierBase {

    @Override
    AppierBaseAd getAppierAd(Context context, AppierAdUnitIdentifier adUnitIdentifier) {
        return new AppierBannerAd(context, adUnitIdentifier, new AppierBannerListener());
    }

    @Override
    void setAppierAdForLoad() {
        ((AppierBannerAd) appierBaseAd).setAdDimension(adWidth, adHeight);
    }

    @Nullable
    @Override
    protected View getAdView() {
        return ((AppierBannerAd) appierBaseAd).getView();
    }

    private class AppierBannerListener implements AppierBannerAd.EventListener {

        @Override
        public void onAdLoaded(AppierBannerAd appierBannerAd) {
            Appier.log("[Appier MoPub Mediation]", "AppierBannerListener.onAdLoaded() (Custom Callback)");
            mLoadListener.onAdLoaded();
        }

        @Override
        public void onAdNoBid(AppierBannerAd appierBannerAd) {
            Appier.log("[Appier MoPub Mediation]", "AppierBannerListener.onAdNoBid() (Custom Callback)");
            mLoadListener.onAdLoadFailed(MoPubErrorCode.NO_FILL);
        }

        @Override
        public void onAdLoadFail(AppierError appierError, AppierBannerAd appierBannerAd) {
            Appier.log("[Appier MoPub Mediation]", "AppierBannerListener.onAdLoadFail() (Custom Callback)", appierError.toString());
            if (appierError == AppierError.NETWORK_ERROR) {
                mLoadListener.onAdLoadFailed(MoPubErrorCode.NETWORK_INVALID_STATE);
            } else if (appierError == AppierError.BAD_REQUEST) {
                mLoadListener.onAdLoadFailed(MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR);
            } else if (appierError == AppierError.INTERNAL_SERVER_ERROR) {
                mLoadListener.onAdLoadFailed(MoPubErrorCode.INTERNAL_ERROR);
            } else if (appierError == AppierError.WEBVIEW_ERROR) {
                Appier.log("[Appier MoPub Mediation]", "Fail to load the url:", appierBannerAd.getFailingUrl());
                mLoadListener.onAdLoadFailed(MoPubErrorCode.HTML_LOAD_ERROR);
            } else {
                mLoadListener.onAdLoadFailed(MoPubErrorCode.UNSPECIFIED);
            }
        }

        @Override
        public void onViewClick(AppierBannerAd appierBannerAd) {
            Appier.log("[Appier MoPub Mediation]", "AppierBannerListener.onViewClick() (Custom Callback)");
            mInteractionListener.onAdClicked();
        }
    }
}
