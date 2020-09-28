package com.mopub.nativeads;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.appier.ads.Appier;
import com.appier.ads.AppierNativeAd;
import com.appier.ads.AppierError;
import com.appier.ads.common.AppierDataKeys;
import com.appier.ads.common.BrowserUtil;
import com.mopub.mobileads.AppierAdUnitIdentifier;

import org.json.JSONException;

import java.util.Map;

import static com.mopub.nativeads.NativeImageHelper.preCacheImages;

public class AppierNative extends CustomEventNative {
    @Override
    protected void loadNativeAd(@NonNull final Context context,
                                @NonNull final CustomEventNativeListener customEventNativeListener,
                                @NonNull final Map<String, Object> localExtras,
                                @NonNull final Map<String, String> serverExtras) {
        Appier.log("[Appier Mediation]", "AppierNative.loadNativeAd()");

        if (serverExtras.isEmpty()) {
            customEventNativeListener.onNativeAdFailed(NativeErrorCode.NATIVE_ADAPTER_CONFIGURATION_ERROR);
            return;
        }
        String adUnitId = getAdUnitId(localExtras, serverExtras);
        String zoneId = getZoneId(localExtras, serverExtras);
        AppierStaticNativeAd appierStaticNativeAd = new AppierStaticNativeAd(
            context,
            new AppierAdUnitIdentifier(adUnitId),
            new ImpressionTracker(context),
            new NativeClickHandler(context),
            customEventNativeListener
        );
        // load native ad from appier sdk here
        appierStaticNativeAd.loadAd(zoneId);
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
        private Handler mHandler;
        private BrowserUtil mBrowserUtil;

        public AppierStaticNativeAd(
            @NonNull final Context context,
            @NonNull final AppierAdUnitIdentifier adUnitId,
            @NonNull final ImpressionTracker impressionTracker,
            @NonNull final NativeClickHandler nativeClickHandler,
            @NonNull final CustomEventNativeListener customEventNativeListener
        ) {
            this.mContext = context;
            this.impressionTracker = impressionTracker;
            this.nativeClickHandler = nativeClickHandler;
            this.customEventNativeListener = customEventNativeListener;

            this.mAppierNativeAd = new AppierNativeAd(mContext, adUnitId, AppierStaticNativeAd.this);
            this.mHandler = new Handler(Looper.getMainLooper());
            this.mBrowserUtil = new BrowserUtil(mContext);
        }

        // Lifecycle Handlers
        @Override
        public void prepare(@NonNull final View view) {
            Appier.log("[Appier Mediation]", "AppierNative.AppierStaticNativeAd.prepare()");
            impressionTracker.addView(view, this);
            nativeClickHandler.setOnClickListener(view, this);
        }

        @Override
        public void clear(@NonNull final View view) {
            Appier.log("[Appier Mediation]", "AppierNative.AppierStaticNativeAd.clear()");
            impressionTracker.removeView(view);
            nativeClickHandler.clearOnClickListener(view);
        }

        @Override
        public void destroy() {
            Appier.log("[Appier Mediation]", "AppierNative.AppierStaticNativeAd.destroy()");
            impressionTracker.destroy();
            mAppierNativeAd.destroy();
            super.destroy();
        }

        // Event Handlers
        @Override
        public void recordImpression(@NonNull final View view) {
            Appier.log("[Appier Mediation]", "AppierNative.AppierStaticNativeAd.recordImpression()");
            mAppierNativeAd.makeImpressionTrackingRequest();
        }

        @Override
        public void handleClick(@Nullable final View view) {
            Appier.log("[Appier Mediation]", "AppierNative.AppierStaticNativeAd.handleClick()");
            /*
             * FYI:
             * For native, MoPub provides helper function to open url with MoPub's in-app browser:
             * `nativeClickHandler.openClickDestinationUrl(getClickDestinationUrl(), view);`
             */
            boolean isOpened = mBrowserUtil.tryToOpenUrl(getClickDestinationUrl());
            if (isOpened) {
                notifyAdClicked();
            }
        }

        public void loadAd(String zoneId) {
            mAppierNativeAd.setZoneId(zoneId);
            mAppierNativeAd.loadAdWithExternalCache();
        }

        // Appier SDK Event
        @Override
        public void onAdLoaded(final AppierNativeAd appierNativeAd) {
            Appier.log("[Appier Mediation]", "AppierNative.AppierStaticNativeAd.onAdLoaded() (Custom Callback)");
            this.mAppierNativeAd = appierNativeAd;
            this.mHandler.post(new Runnable() {
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
            Appier.log("[Appier Mediation]", "AppierNative.AppierStaticNativeAd.onAdLoadFail() (Custom Callback)", appierError.toString());
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
            Appier.log("[Appier Mediation]", "AppierNative.AppierStaticNativeAd.onAdNoBid() (Custom Callback)");
            customEventNativeListener.onNativeAdFailed(NativeErrorCode.NETWORK_NO_FILL);
        }

        @Override
        public void onImpressionRecorded(AppierNativeAd appierNativeAd) {
            Appier.log("[Appier Mediation]", "AppierNative.AppierStaticNativeAd.onImpressionRecorded() (Custom Callback)");
            notifyAdImpressed();
        }

        @Override
        public void onImpressionRecordFail(AppierError appierError, AppierNativeAd appierNativeAd) {
            Appier.log("[Appier Mediation]", "AppierNative.AppierStaticNativeAd.onImpressionRecordFail() (Custom Callback)");
        }

        /*
         * MoPub mediation uses MoPubAdRenderer instead of AppierNativeAd's render functions.
         * So following events will never be invoked.
         */
        @Override
        public void onAdShown(AppierNativeAd appierNativeAd) {}
        @Override
        public void onAdClick(AppierNativeAd appierNativeAd) {}
        @Override
        public void onAdClickFail(AppierError appierError, AppierNativeAd appierNativeAd) {}
    }
}
