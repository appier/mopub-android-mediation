package com.appier.sampleapp.fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.appier.ads.Appier;
import com.appier.sampleapp.R;
import com.mopub.nativeads.AdapterHelper;
import com.mopub.nativeads.AppierNativeAdRenderer;
import com.mopub.nativeads.MoPubNative;
import com.mopub.nativeads.MoPubStaticNativeAdRenderer;
import com.mopub.nativeads.NativeAd;
import com.mopub.nativeads.NativeErrorCode;
import com.mopub.nativeads.ViewBinder;

public class BaseFragment extends Fragment {
    private LinearLayout mAdContainer;
    private MoPubNative moPubNative;
    private NativeAd.MoPubNativeEventListener moPubNativeEventListener;
    private MoPubNative.MoPubNativeNetworkListener moPubNativeNetworkListener;
    private EventListener mEventListener;
    private View adView;

    public BaseFragment(@Nullable EventListener eventListener) {
        super();
        this.mEventListener = eventListener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_appier_native_manual_integration_tab_fragment_1, container, false);
    }

    public void init(final Context context) {
        final View view = getView();
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

                final AdapterHelper adapterHelper = new AdapterHelper(context, 0, 3); // When standalone, any range will be fine.

                // Retrieve the pre-built ad view that AdapterHelper prepared for us.
                adView = adapterHelper.getAdView(null, null, nativeAd, new ViewBinder.Builder(0).build());

                // Set the native event listeners (onImpression, and onClick).
                nativeAd.setMoPubNativeEventListener(moPubNativeEventListener);

                // Add the ad view to our view hierarchy
                clearAd();
                mAdContainer = view.findViewById(R.id.ad_container);
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
        AppierNativeAdRenderer appierNativeAdRenderer = new AppierNativeAdRenderer(viewBinder);
        MoPubStaticNativeAdRenderer moPubStaticNativeAdRenderer = new MoPubStaticNativeAdRenderer(viewBinder);

        moPubNative = new MoPubNative(context, getString(R.string.adunit_appier_native_sample_default), moPubNativeNetworkListener);

        moPubNative.registerAdRenderer(appierNativeAdRenderer);
        moPubNative.registerAdRenderer(moPubStaticNativeAdRenderer);
    }

    public void loadAd() {
        if (moPubNative != null) {
            Appier.log("[Sample App]", "====== make request ======");
            moPubNative.makeRequest();
        }
    }

    public void clearAd() {
        if (mAdContainer != null && adView != null) {
            mAdContainer.removeView(adView);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mEventListener != null) {
            mEventListener.onReady(this);
        }
    }

    @Override
    public void onDestroyView() {
        if (moPubNative != null) {
            moPubNative.destroy();
        }
        super.onDestroyView();
    }

    public interface EventListener {
        void onReady(BaseFragment fragment);
    }
}
