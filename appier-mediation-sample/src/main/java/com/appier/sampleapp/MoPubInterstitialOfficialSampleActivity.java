package com.appier.sampleapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.appier.ads.Appier;
import com.mopub.mobileads.MoPubErrorCode;
import com.mopub.mobileads.MoPubInterstitial;

public class MoPubInterstitialOfficialSampleActivity extends AppCompatActivity implements MoPubInterstitial.InterstitialAdListener {
    private MoPubInterstitial mInterstitial;
    private boolean isAdLoading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mopub_interstitial_official_sample);

        findViewById(R.id.button_show_ad_if_ready).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAd();
            }
        });

        mInterstitial = new MoPubInterstitial(this, getString(R.string.adunit_mopub_interstitial_official_sample));
        mInterstitial.setInterstitialAdListener(this);
        loadAd();
    }

    @Override
    protected void onDestroy() {
        if (mInterstitial != null) {
            mInterstitial.destroy();
        }
        super.onDestroy();
    }

    void loadAd() {
        if (!isAdLoading) {
            isAdLoading = true;
            mInterstitial.load();
        }
    }

    // Defined by your application, indicating that you're ready to show an interstitial ad.
    void showAd() {
        Appier.log("[Sample App] showAd()", "mInterstitial.isReady() =", mInterstitial.isReady());
        if (mInterstitial.isReady()) {
            mInterstitial.show();
        } else {
            // Caching is likely already in progress if `isReady()` is false.
            // Avoid calling `load()` here and instead rely on the callbacks as suggested below.
            Toast.makeText(this, "Ad is not ready, please retry again later", Toast.LENGTH_SHORT).show();
            loadAd();
        }
    }

    // InterstitialAdListener methods
    @Override
    public void onInterstitialLoaded(MoPubInterstitial interstitial) {
        // The interstitial has been cached and is ready to be shown.
        Appier.log("[Sample App]", "onInterstitialLoaded()");
        isAdLoading = false;
    }

    @Override
    public void onInterstitialFailed(MoPubInterstitial interstitial, MoPubErrorCode errorCode) {
        // The interstitial has failed to load. Inspect errorCode for additional information.
        Appier.log("[Sample App]", "onInterstitialFailed()");
        isAdLoading = false;
    }

    @Override
    public void onInterstitialShown(MoPubInterstitial interstitial) {
        // The interstitial has been shown. Pause / save state accordingly.
        Appier.log("[Sample App]", "onInterstitialShown()");
    }

    @Override
    public void onInterstitialClicked(MoPubInterstitial interstitial) {
        Appier.log("[Sample App]", "onInterstitialClicked()");
    }

    @Override
    public void onInterstitialDismissed(MoPubInterstitial interstitial) {
        // The interstitial has being dismissed. Resume / load state accordingly.
        Appier.log("[Sample App]", "onInterstitialDismissed()");
        loadAd();
    }
}
