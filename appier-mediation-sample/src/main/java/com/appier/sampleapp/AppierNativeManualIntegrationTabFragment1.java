package com.appier.sampleapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.mopub.nativeads.AdapterHelper;
import com.mopub.nativeads.AppierNativeAdRenderer;
import com.mopub.nativeads.MoPubNative;
import com.mopub.nativeads.MoPubStaticNativeAdRenderer;
import com.mopub.nativeads.NativeAd;
import com.mopub.nativeads.NativeErrorCode;
import com.mopub.nativeads.ViewBinder;

public class AppierNativeManualIntegrationTabFragment1 extends Fragment {
    private static final String LOG_TAG = "AppierMediation";

    private LinearLayout mAdContainer;
    private MoPubNative moPubNative;
    private NativeAd.MoPubNativeEventListener moPubNativeEventListener;
    private MoPubNative.MoPubNativeNetworkListener moPubNativeNetworkListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_appier_native_manual_integration_tab_fragment_1, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        moPubNativeEventListener = new NativeAd.MoPubNativeEventListener() {
            @Override
            public void onImpression(View view) {
                Log.d(LOG_TAG, "[Sample App] Native ad recorded an impression.");
                // Impress is recorded - do what is needed AFTER the ad is visibly shown here.
            }

            @Override
            public void onClick(View view) {
                Log.d(LOG_TAG, "[Sample App] Native ad recorded a click.");
                // Click tracking.
            }
        };
        moPubNativeNetworkListener = new MoPubNative.MoPubNativeNetworkListener() {
            @Override
            public void onNativeLoad(final NativeAd nativeAd) {
                Log.d(LOG_TAG, "[Sample App] Native ad has loaded.");

                final AdapterHelper adapterHelper = new AdapterHelper(getActivity(), 0, 3); // When standalone, any range will be fine.

                // Retrieve the pre-built ad view that AdapterHelper prepared for us.
                View adView = adapterHelper.getAdView(null, null, nativeAd, new ViewBinder.Builder(0).build());

                // Set the native event listeners (onImpression, and onClick).
                nativeAd.setMoPubNativeEventListener(moPubNativeEventListener);

                // Add the ad view to our view hierarchy
                mAdContainer = view.findViewById(R.id.ad_container);
                mAdContainer.addView(adView);
            }

            @Override
            public void onNativeFail(NativeErrorCode errorCode) {
                Log.d(LOG_TAG, "[Sample App] Native ad failed to load with error: " + errorCode.toString());
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

        moPubNative = new MoPubNative(getActivity(), getString(R.string.adunit_appier_native_sample_default), moPubNativeNetworkListener);

        moPubNative.registerAdRenderer(appierNativeAdRenderer);
        moPubNative.registerAdRenderer(moPubStaticNativeAdRenderer);
        Log.d(LOG_TAG, "[Sample App] ====== make request ======");
        moPubNative.makeRequest();
    }

    @Override
    public void onDestroyView() {
        if (moPubNative != null) {
            moPubNative.destroy();
        }
        super.onDestroyView();
    }
}
