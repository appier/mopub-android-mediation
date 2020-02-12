package com.mopub.nativeads;

import android.content.Context;
import android.util.Log;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.appier.ads.AppierNativeAd;
import com.appier.ads.AppierError;
import com.appier.ads.common.AppierDataKeys;

import org.json.JSONException;

import java.util.Map;

import static com.mopub.nativeads.NativeImageHelper.preCacheImages;

public class AppierNative extends CustomEventNative {
    private static final String LOG_TAG = "AppierMediation";

    public AppierNative() {
        Log.d(LOG_TAG, "[Appier Mediation] AppierNative.constructor()");
    }

    @Override
    protected void loadNativeAd(@NonNull final Context context,
                                @NonNull final CustomEventNativeListener customEventNativeListener,
                                @NonNull final Map<String, Object> localExtras,
                                @NonNull final Map<String, String> serverExtras) {
        Log.d(LOG_TAG, "[Appier Mediation] AppierNative.loadNativeAd()");

        if (serverExtras.isEmpty()) {
            customEventNativeListener.onNativeAdFailed(NativeErrorCode.NATIVE_ADAPTER_CONFIGURATION_ERROR);
            return;
        }
        String zoneId = getZoneId(localExtras, serverExtras);
        AppierStaticNativeAd appierStaticNativeAd = new AppierStaticNativeAd(
            context,
            new ImpressionTracker(context),
            new NativeClickHandler(context),
            customEventNativeListener
        );
        // load native ad from appier sdk here
        appierStaticNativeAd.loadAd(zoneId);
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

    static class AppierStaticNativeAd extends StaticNativeAd implements AppierNativeAd.EventListener {
        @NonNull
        private final Context mContext;
        @NonNull
        private final ImpressionTracker impressionTracker;
        @NonNull
        private final NativeClickHandler nativeClickHandler;
        @NonNull
        private final CustomEventNativeListener customEventNativeListener;
        @NonNull
        private AppierNativeAd mAppierNativeAd;

        public AppierStaticNativeAd(
            @NonNull final Context context,
            @NonNull final ImpressionTracker impressionTracker,
            @NonNull final NativeClickHandler nativeClickHandler,
            @NonNull final CustomEventNativeListener customEventNativeListener
        ) {
            Log.d(LOG_TAG, "[Appier Mediation] AppierNative.AppierStaticNativeAd.constructor()");
            this.mContext = context;
            this.impressionTracker = impressionTracker;
            this.nativeClickHandler = nativeClickHandler;
            this.customEventNativeListener = customEventNativeListener;
            this.mAppierNativeAd = new AppierNativeAd(mContext,AppierStaticNativeAd.this);
        }

        // Lifecycle Handlers
        @Override
        public void prepare(@NonNull final View view) {
            Log.d(LOG_TAG, "[Appier Mediation] AppierNative.AppierStaticNativeAd.prepare()");
            impressionTracker.addView(view, this);
            nativeClickHandler.setOnClickListener(view, this);
        }

        @Override
        public void clear(@NonNull final View view) {
            Log.d(LOG_TAG, "[Appier Mediation] AppierNative.AppierStaticNativeAd.clear()");
            impressionTracker.removeView(view);
            nativeClickHandler.clearOnClickListener(view);
        }

        @Override
        public void destroy() {
            Log.d(LOG_TAG, "[Appier Mediation] AppierNative.AppierStaticNativeAd.destroy()");
            impressionTracker.destroy();
            super.destroy();
        }

        // Event Handlers
        @Override
        public void recordImpression(@NonNull final View view) {
            Log.d(LOG_TAG, "[Appier Mediation] AppierNative.AppierStaticNativeAd.recordImpression()");
            try {
                mAppierNativeAd.makeImpressionTrackingRequest();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void handleClick(@Nullable final View view) {
            Log.d(LOG_TAG, "[Appier Mediation] AppierNative.AppierStaticNativeAd.handleClick()");
            notifyAdClicked();
            nativeClickHandler.openClickDestinationUrl(getClickDestinationUrl(), view);
        }

        public void loadAd(String zoneId) {
            mAppierNativeAd.loadAd(zoneId);
        }

        // Appier SDK Event
        @Override
        public void onAdLoaded(final AppierNativeAd appierNativeAd) {
            Log.d(LOG_TAG, "[Appier Mediation] AppierNative.AppierStaticNativeAd.onAdLoaded() (Custom Callback)");
            this.mAppierNativeAd = appierNativeAd;
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    try {
                        setTitle(appierNativeAd.getTitle());
                        setText(appierNativeAd.getText());
                        setIconImageUrl(appierNativeAd.getIconImageUrl());
                        setMainImageUrl(appierNativeAd.getMainImageUrl());
                        setCallToAction(appierNativeAd.getCallToActionText());
                        setClickDestinationUrl(appierNativeAd.getClickDestinationUrl());
                        setPrivacyInformationIconImageUrl(appierNativeAd.getPrivacyInformationIconImageUrl());
                        setPrivacyInformationIconClickThroughUrl(appierNativeAd.getPrivacyInformationIconClickThroughUrl());

                        // to suppress warning:
                        // D/MoPub: Image was not loaded immediately into your ad view. You should call preCacheImages as part of your custom event loading process.
                        preCacheImages(mContext, appierNativeAd.getCacheableImageUrls(), new NativeImageHelper.ImageListener() {
                            @Override
                            public void onImagesCached() {
                                customEventNativeListener.onNativeAdLoaded(AppierStaticNativeAd.this);
                            }

                            @Override
                            public void onImagesFailedToCache(final NativeErrorCode errorCode) {
                                customEventNativeListener.onNativeAdFailed(errorCode);
                            }
                        });
                    } catch (JSONException e) {
                        customEventNativeListener.onNativeAdFailed(NativeErrorCode.INVALID_RESPONSE);
                    }
                }
            });
        }

        @Override
        public void onAdLoadFail(AppierError appierError, AppierNativeAd appierNativeAd) {
            Log.d(LOG_TAG, "[Appier Mediation] AppierNative.AppierStaticNativeAd.onAdLoadFail() (Custom Callback) " + appierError.toString());
            if (appierError == AppierError.NETWORK_ERROR) {
                customEventNativeListener.onNativeAdFailed(NativeErrorCode.NETWORK_INVALID_STATE);
            } else if (appierError == AppierError.BAD_REQUEST) {
                customEventNativeListener.onNativeAdFailed(NativeErrorCode.NETWORK_INVALID_REQUEST);
            } else if (appierError == AppierError.INTERNAL_SERVER_ERROR) {
                customEventNativeListener.onNativeAdFailed(NativeErrorCode.NETWORK_INVALID_STATE);
            }
        }

        @Override
        public void onAdNoBid(AppierNativeAd appierNativeAd) {
            Log.d(LOG_TAG, "[Appier Mediation] AppierNative.AppierStaticNativeAd.onAdNoBid() (Custom Callback)");
            customEventNativeListener.onNativeAdFailed(NativeErrorCode.NETWORK_NO_FILL);
        }

        @Override
        public void onImpressionRecorded(AppierNativeAd appierNativeAd) {
            Log.d(LOG_TAG, "[Appier Mediation] AppierNative.AppierStaticNativeAd.onImpressionRecorded() (Custom Callback)");
            notifyAdImpressed();
        }

        @Override
        public void onImpressionRecordFail(AppierError responseCode, AppierNativeAd appierNativeAd) {
            Log.d(LOG_TAG, "[Appier Mediation] AppierNative.AppierStaticNativeAd.onImpressionRecordFail() (Custom Callback)");
        }
    }
}
