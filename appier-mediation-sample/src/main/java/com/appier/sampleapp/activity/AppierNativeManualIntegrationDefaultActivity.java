package com.appier.sampleapp.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.os.Bundle;
import android.view.View;

import com.appier.ads.Appier;
import com.appier.sampleapp.R;
import com.mopub.nativeads.AdapterHelper;
import com.mopub.nativeads.AppierNativeAdRenderer;
import com.mopub.nativeads.FlurryNativeAdRenderer;
import com.mopub.nativeads.FlurryViewBinder;
import com.mopub.nativeads.MoPubNative;
import com.mopub.nativeads.MoPubStaticNativeAdRenderer;
import com.mopub.nativeads.NativeAd;
import com.mopub.nativeads.NativeErrorCode;
import com.mopub.nativeads.ViewBinder;

public class AppierNativeManualIntegrationDefaultActivity extends AppCompatActivity {
    private ConstraintLayout mAdContainer;
    private MoPubNative moPubNative;
    private NativeAd.MoPubNativeEventListener moPubNativeEventListener;
    private MoPubNative.MoPubNativeNetworkListener moPubNativeNetworkListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appier_native_manual_integration_default);

        moPubNativeEventListener = new NativeAd.MoPubNativeEventListener() {
            @Override
            public void onImpression(View view) {
                Appier.log("[Sample App]", "Native ad recorded an impression.");
                // Impress is recorded - do what is needed AFTER the ad is visibly shown here.
            }

            @Override
            public void onClick(View view) {
                Appier.log("[Sample App]", "Native ad recorded a click.");
                // Click tracking.
            }
        };
        moPubNativeNetworkListener = new MoPubNative.MoPubNativeNetworkListener() {
            @Override
            public void onNativeLoad(final NativeAd nativeAd) {
                Appier.log("[Sample App]", "Native ad has loaded.");

                final AdapterHelper adapterHelper = new AdapterHelper(AppierNativeManualIntegrationDefaultActivity.this, 0, 3); // When standalone, any range will be fine.

                // Retrieve the pre-built ad view that AdapterHelper prepared for us.
                View adView = adapterHelper.getAdView(null, null, nativeAd, new ViewBinder.Builder(0).build());

                // Set the native event listeners (onImpression, and onClick).
                nativeAd.setMoPubNativeEventListener(moPubNativeEventListener);

                // Add the ad view to our view hierarchy
                mAdContainer = findViewById(R.id.ad_container);
                mAdContainer.addView(adView);
            }

            @Override
            public void onNativeFail(NativeErrorCode errorCode) {
                Appier.log("[Sample App]", "Native ad failed to load with error:", errorCode.toString());
            }
        };
        ViewBinder viewBinder = new ViewBinder.Builder(R.layout.template_native_ad)
            .mainImageId(R.id.native_main_image)
            .iconImageId(R.id.native_icon_image)
            .titleId(R.id.native_title)
            .textId(R.id.native_text)
            .callToActionId(R.id.native_cta)
            .privacyInformationIconImageId(R.id.native_privacy_information_icon_image)
            .build();
        FlurryViewBinder flurryViewBinder = new FlurryViewBinder(new FlurryViewBinder.Builder(viewBinder));
        FlurryNativeAdRenderer flurryNativeAdRenderer = new FlurryNativeAdRenderer(flurryViewBinder);
        AppierNativeAdRenderer appierNativeAdRenderer = new AppierNativeAdRenderer(viewBinder);
        MoPubStaticNativeAdRenderer moPubStaticNativeAdRenderer = new MoPubStaticNativeAdRenderer(viewBinder);

        moPubNative = new MoPubNative(this, getString(R.string.adunit_appier_native_sample_default), moPubNativeNetworkListener);

        moPubNative.registerAdRenderer(flurryNativeAdRenderer);
        moPubNative.registerAdRenderer(appierNativeAdRenderer);
        moPubNative.registerAdRenderer(moPubStaticNativeAdRenderer);
        Appier.log("[Sample App]", "====== make request ======");
        moPubNative.makeRequest();
    }

    @Override
    protected void onDestroy() {
        if (moPubNative != null) {
            moPubNative.destroy();
            moPubNative = null;
        }
        super.onDestroy();
    }
}
