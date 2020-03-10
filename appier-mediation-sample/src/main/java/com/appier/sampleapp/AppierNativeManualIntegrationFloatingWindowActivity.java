package com.appier.sampleapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.appier.sampleapp.common.FloatViewManager;
import com.appier.ads.Appier;
import com.mopub.nativeads.AdapterHelper;
import com.mopub.nativeads.AppierNativeAdRenderer;
import com.mopub.nativeads.MoPubNative;
import com.mopub.nativeads.MoPubStaticNativeAdRenderer;
import com.mopub.nativeads.NativeAd;
import com.mopub.nativeads.NativeErrorCode;
import com.mopub.nativeads.ViewBinder;

public class AppierNativeManualIntegrationFloatingWindowActivity extends AppCompatActivity {

    private FloatViewManager mFloatViewManager;
    private LinearLayout mAdContainer;
    private View mAdView;
    private MoPubNative moPubNative;
    private NativeAd.MoPubNativeEventListener moPubNativeEventListener;
    private MoPubNative.MoPubNativeNetworkListener moPubNativeNetworkListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appier_native_manual_integration_floating_window);

        findViewById(R.id.button_open_floating_window).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFloatViewManager.openWithPermissionCheck();
            }
        });

        this.mFloatViewManager = new FloatViewManager(this, new FloatViewManager.OnFloatViewEventListener() {
            @Override
            public void onOpen(LinearLayout contentContainer) {
                mAdContainer = contentContainer;
                loadAd(AppierNativeManualIntegrationFloatingWindowActivity.this);
            }

            @Override
            public void onDrawOverlayPermissionResult(boolean isPermissionGranted) {
                if (isPermissionGranted) {
                    mFloatViewManager.open();
                }
            }

            @Override
            public void onClose(LinearLayout contentContainer) {
                if (mAdView != null) {
                    contentContainer.removeView(mAdView);
                }
                if (moPubNative != null) {
                    moPubNative.destroy();
                }
            }
        });
    }

    // After user grants overlay permission
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        this.mFloatViewManager.handleActivityResult(requestCode, resultCode, data);
    }

    private void loadAd(final Context context) {
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
                mFloatViewManager.close();
            }
        };
        moPubNativeNetworkListener = new MoPubNative.MoPubNativeNetworkListener() {
            @Override
            public void onNativeLoad(final NativeAd nativeAd) {
                Appier.log("[Sample App]", "Native ad has loaded.");

                // Set the native event listeners (onImpression, and onClick).
                nativeAd.setMoPubNativeEventListener(moPubNativeEventListener);

                if (mAdContainer != null) {
                    final AdapterHelper adapterHelper = new AdapterHelper(context, 0, 3); // When standalone, any range will be fine.

                    // Retrieve the pre-built ad view that AdapterHelper prepared for us.
                    mAdView = adapterHelper.getAdView(null, null, nativeAd, new ViewBinder.Builder(0).build());

                    // Add the ad view to our view hierarchy
                    mAdContainer.addView(mAdView);
                }
            }

            @Override
            public void onNativeFail(NativeErrorCode errorCode) {
                Appier.log("[Sample App]", "Native ad failed to load with error:", errorCode.toString());
            }
        };
        ViewBinder viewBinder = new ViewBinder.Builder(R.layout.native_ad)
            .mainImageId(R.id.native_main_image)
            .iconImageId(R.id.native_icon_image)
            .titleId(R.id.native_title)
            .textId(R.id.native_text)
            .callToActionId(R.id.native_cta)
            .privacyInformationIconImageId(R.id.native_privacy_information_icon_image)
            .build();
        AppierNativeAdRenderer appierNativeAdRenderer = new AppierNativeAdRenderer(viewBinder);
        MoPubStaticNativeAdRenderer moPubStaticNativeAdRenderer = new MoPubStaticNativeAdRenderer(viewBinder);

        moPubNative = new MoPubNative(context, getString(R.string.adunit_appier_native_sample_default), moPubNativeNetworkListener);
        moPubNative.registerAdRenderer(appierNativeAdRenderer);
        moPubNative.registerAdRenderer(moPubStaticNativeAdRenderer);
        Appier.log("[Sample App]", "====== make request ======");
        moPubNative.makeRequest();
    }
}
