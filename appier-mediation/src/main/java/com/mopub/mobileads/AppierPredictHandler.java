package com.mopub.mobileads;

import android.content.Context;

import com.appier.ads.Appier;
import com.appier.ads.AppierPredictor;
import com.appier.ads.AppierError;
import com.appier.ads.AppierPredictCache;
import com.mopub.nativeads.NativeErrorCode;
import com.mopub.nativeads.NativeImageHelper;
import static com.mopub.nativeads.NativeImageHelper.preCacheImages;

import java.util.List;

public class AppierPredictHandler implements AppierPredictor.EventListener {
    private Context mContext;

    public AppierPredictHandler(Context context) {
        mContext = context;
    }

    public static String getKeywordTargeting(String adUnitId) {
        StringBuilder keyword = new StringBuilder();
        List<String> result = AppierPredictCache
                .getInstance()
                .getPredictResult(
                        new AppierAdUnitIdentifier(adUnitId)
                );
        if (result != null)
            for (String key: result)
                keyword.append("appier_zone_").append(key).append(":1,");
        else
            keyword.append("appier_predict_ver:1,");
        return keyword.toString();
    }

    @Override
    public void onPredictSuccess(final String adUnitId, List<String> prefetchList) {
        Appier.log("[Appier MoPub Mediation]", "[Predict Mode]", "successfully predict ad:", adUnitId);
        preCacheImages(mContext, prefetchList, new NativeImageHelper.ImageListener() {
            @Override
            public void onImagesCached() {
                Appier.log("[Appier MoPub Mediation]", "[Predict Mode]", "successfully cache images for ad:", adUnitId);
            }

            @Override
            public void onImagesFailedToCache(NativeErrorCode errorCode) {
                Appier.log("[Appier MoPub Mediation]", "[Predict Mode]", "failed to cache images for ad:", adUnitId);
            }
        });
    }

    @Override
    public void onPredictFailed(String adUnitId, AppierError error) {
        Appier.log("[Appier MoPub Mediation]", "[Predict Mode]", "predict ad", adUnitId, " failed:", error);
    }
}
