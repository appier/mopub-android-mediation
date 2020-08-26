package com.mopub.mobileads;

import android.content.Context;

import com.appier.ads.Appier;
import com.appier.ads.AppierPredictor;
import com.appier.ads.AppierError;
import com.appier.ads.AppierPredictCache;
import com.mopub.nativeads.NativeErrorCode;
import com.mopub.nativeads.NativeImageHelper;
import com.mopub.nativeads.RequestParameters;
import static com.mopub.nativeads.NativeImageHelper.preCacheImages;

import java.util.List;

public class AppierPredictHandler implements AppierPredictor.EventListener {
    private Context mContext;

    public AppierPredictHandler(Context context) {
        mContext = context;
    }

    private static String getKeywordTargeting(String zoneId) {
        if (AppierPredictCache.getInstance().getPredictResult(zoneId)) {
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

    @Override
    public void onPredictBidAndGetPrefetchList(final String adUnitId, List<String> prefetchList) {
        Appier.log("[Appier Mediation]", "[Predict Mode]", "successfully predict ad ", adUnitId, ": Bid");
        preCacheImages(mContext, prefetchList, new NativeImageHelper.ImageListener() {
            @Override
            public void onImagesCached() {
                Appier.log("[Appier Mediation]", "[Predict Mode]", "successfully cache images for ad: ", adUnitId);
            }

            @Override
            public void onImagesFailedToCache(NativeErrorCode errorCode) {
                Appier.log("[Appier Mediation]", "[Predict Mode]", "failed to cache images for ad: ", adUnitId);
            }
        });
    }

    @Override
    public void onPredictNoBid(String adUnitId) {
        Appier.log("[Appier Mediation]", "[Predict Mode]", "successfully predict ad ", adUnitId, ": NoBid");
    }

    @Override
    public void onPredictFailed(String adUnitId, AppierError error) {
        Appier.log("[Appier Mediation]", "[Predict Mode]", "predict ad ", adUnitId, " failed: ", error);
    }
}
