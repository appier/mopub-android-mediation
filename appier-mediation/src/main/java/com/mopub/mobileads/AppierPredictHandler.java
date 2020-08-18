package com.mopub.mobileads;

import com.appier.ads.AppierAdPredictor;
import com.mopub.nativeads.RequestParameters;

public class AppierPredictHandler {
    private static String getKeywordTargeting(String zoneId) {
        if (AppierAdPredictor.getInstance(null).getPredictResult(zoneId)) {
            return "appier_predict:1";
        }
        return "appier_predict:0";
    }

    public static RequestParameters.Builder setKeywordTargeting(String zoneId) {
        RequestParameters.Builder builder = new RequestParameters.Builder();
        return setKeywordTargeting(zoneId, builder);
    }

    public static RequestParameters.Builder setKeywordTargeting(String zoneId, RequestParameters.Builder builder) {
        builder.keywords(AppierPredictHandler.getKeywordTargeting(zoneId));
        return builder;
    }

    public static void setKeywordTargeting(String zoneId, MoPubView moPubView) {
        moPubView.setKeywords(AppierPredictHandler.getKeywordTargeting(zoneId));
    }

    public static void setKeywordTargeting(String zoneId, MoPubInterstitial moPubInterstitial) {
        moPubInterstitial.setKeywords(AppierPredictHandler.getKeywordTargeting(zoneId));
    }
}
