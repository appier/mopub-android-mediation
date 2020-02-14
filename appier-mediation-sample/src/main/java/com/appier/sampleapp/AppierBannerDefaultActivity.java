package com.appier.sampleapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.appier.ads.Appier;
import com.mopub.mobileads.MoPubErrorCode;
import com.mopub.mobileads.MoPubView;

import com.appier.ads.common.AppierDataKeys;

import java.util.HashMap;
import java.util.Map;

public class AppierBannerDefaultActivity extends AppCompatActivity implements MoPubView.BannerAdListener {
    private static int AD_WIDTH = 300;
    private static int AD_HEIGHT = 250;

    private MoPubView moPubView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appier_banner_default);

        Map<String, Object> localExtras = new HashMap<>();
        localExtras.put(AppierDataKeys.AD_WIDTH_LOCAL, AD_WIDTH);
        localExtras.put(AppierDataKeys.AD_HEIGHT_LOCAL, AD_HEIGHT);

        moPubView = findViewById(R.id.ad_container);
        moPubView.setLocalExtras(localExtras);
        moPubView.setBannerAdListener(this);
        moPubView.setAdUnitId(getString(R.string.adunit_appier_banner_sample_default)); // Enter your Ad Unit ID from www.mopub.com
        Appier.log("[Sample App]", "====== make request ======");
        moPubView.loadAd();
    }

    @Override
    protected void onDestroy() {
        if (moPubView != null) {
            moPubView.destroy();
            moPubView = null;
        }
        super.onDestroy();
    }

    // Sent when the banner has successfully retrieved an ad.
    @Override
    public void onBannerLoaded(MoPubView banner) {
        Appier.log("[Sample App]", "onBannerLoaded()");
    }

    // Sent when the banner has failed to retrieve an ad. You can use the MoPubErrorCode value to diagnose the cause of failure.
    @Override
    public void onBannerFailed(MoPubView banner, MoPubErrorCode errorCode) {
        Appier.log("[Sample App]", "onBannerFailed():", errorCode.toString());
    }

    // Sent when the user has tapped on the banner.
    @Override
    public void onBannerClicked(MoPubView banner) {
        Appier.log("[Sample App]", "onBannerClicked()");
    }

    // Sent when the banner has just taken over the screen.
    @Override
    public void onBannerExpanded(MoPubView banner) {
        Appier.log("[Sample App]", "onBannerExpanded()");
    }

    // Sent when an expanded banner has collapsed back to its original size.
    @Override
    public void onBannerCollapsed(MoPubView banner) {
        Appier.log("[Sample App]", "onBannerCollapsed()");
    }
}
