# Appier Mediation for MoPub Android SDK

This is Appier's official Android mediation repository for MoPub SDK.

## Prerequisites

- Make sure you are using MoPub Android SDK version `5.14.0` to `5.15.0`
- Make sure your app's API level meets MoPub Android SDK requirement
- Make sure you have already configured line items on MoPub Web UI
	- `Custom event class` field should be one of Appier's predefined class names
		- `com.mopub.nativeads.AppierNative` for native ads
		- `com.mopub.mobileads.AppierBanner` for banner ads
		- `com.mopub.mobileads.AppierInterstitial` for interstitial ads
		- `com.mopub.mobileads.AppierVideo` for video ads
		- `com.mopub.mobileads.AppierRewardedVideo` for rewarded video ads
	- `Custom event data` field should follow the format `{ "adUnitId": "<your_ad_unit_id_from_mopub>", "zoneId": "<your_zone_id_from_appier>" }`

## Gradle Configuration

Please add `jcenter` and `mavenCentral` to your repositories, and specify both MoPub’s dependencies and Appier's dependencies.

*MoPub Dependencies:*
``` diff

  repositories {
      // ...
+     jcenter()
  }

  dependencies {
      // MoPub SDK base
+     implementation('com.mopub:mopub-sdk-base:5.15.0@aar') {
+         transitive = true
+         exclude module: 'libAvid-mopub' // To exclude AVID
+         exclude module: 'moat-mobile-app-kit' // To exclude Moat
+     }

      // MoPub SDK for native static (images)
+     implementation('com.mopub:mopub-sdk-native-static:5.15.0@aar') {
+         transitive = true
+         exclude module: 'libAvid-mopub' // To exclude AVID
+         exclude module: 'moat-mobile-app-kit' // To exclude Moat
+     }

      // MoPub SDK for banner
+     implementation('com.mopub:mopub-sdk-banner:5.15.0@aar') {
+         transitive = true
+         exclude module: 'libAvid-mopub' // To exclude AVID
+         exclude module: 'moat-mobile-app-kit' // To exclude Moat
+     }

      // MoPub SDK for fullscreen ad
+     implementation('com.mopub:mopub-sdk-fullscreen:5.15.0@aar') {
+         transitive = true
+         exclude module: 'libAvid-mopub' // To exclude AVID
+         exclude module: 'moat-mobile-app-kit' // To exclude Moat
+     }
  }
```

*Appier Dependencies:*
``` diff
  repositories {
      // ...
+     mavenCentral()
  }

  dependencies {
      // ...
+     implementation 'com.appier.android:ads-sdk:2.0.0'
+     implementation('com.appier.android:mopub-mediation:2.0.0') {
+         transitive = true
+         exclude module: 'libAvid-mopub' // To exclude AVID
+         exclude module: 'moat-mobile-app-kit' // To exclude Moat
      }
  }
```

## Manifest Configuration

To prevent your app from crashing, following are the recommended manifest configurations.

``` xml
<manifest ...>
  <uses-permission android:name="android.permission.INTERNET" />
  <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />

  <!--  Required for displaying floating window  -->
  <uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW" />

  <application
    ...
    android:networkSecurityConfig="@xml/network_security_config">

    <uses-library android:name="org.apache.http.legacy" android:required="false" />

    <!-- MoPub's consent dialog -->
    <activity android:name="com.mopub.common.privacy.ConsentDialogActivity" android:configChanges="keyboardHidden|orientation|screenSize"/>

    <!-- All ad formats -->
    <activity android:name="com.mopub.common.MoPubBrowser" android:configChanges="keyboardHidden|orientation|screenSize"/>

    <!-- Google Play Services-->
    <meta-data android:name="com.google.android.gms.version" android:value="@integer/google_play_services_version" />
  </application>
</manifest>
```

You also need to add `xml/network_security_config.xml` into resources.

``` xml
<?xml version="1.0" encoding="utf-8"?>
<network-security-config>
  <base-config cleartextTrafficPermitted="true" />
</network-security-config>
```

## GDPR Consent (Recommended)

In consent to GDPR, we strongly suggest sending the consent status to our SDK via `Appier.setGDPRApplies()` so that we will not track users personal information. Without this configuration, Appier will not apply GDPR by default. Note that this will impact Advertising performance thus impacting Revenue.

``` java
import com.appier.ads.Appier;

public class MainActivity extends AppCompatActivity {
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    // ...
    Appier.setGDPRApplies(true);
  }
}
```

## Ad Format Integration

### Native Ads Integration

To render Appier's native ads via MoPub mediation, you need to register `AppierNativeAdRenderer` to your own `MoPubNative` instance and set up the ad unit id of ad unit to load ad. You can either pass through `localExtras` or `serverExtras`:

``` java
import com.appier.ads.common.AppierDataKeys;
import com.mopub.nativeads.AppierNativeAdRenderer;

// ...
Map<String, Object> localExtras = new HashMap<>();
localExtras.put(AppierDataKeys.AD_UNIT_ID_LOCAL, "<your_mopub_ad_unit_id>");
AppierNativeAdRenderer appierNativeAdRenderer = new AppierNativeAdRenderer(viewBinder);

MoPubNative nativeAd = new MoPubNative(...);
nativeAd.registerAdRenderer(appierNativeAdRenderer);
nativeAd.setLocalExtras(localExtras);
nativeAd.makeRequest();
```

### Banner Ads Integration

To render Appier's banner ads via MoPub mediation, you need to set up the ad unit id of ad unit and specify the width and height of ad unit to load ads with suitable sizes. You can either pass through `localExtras` or `serverExtras`.

``` java
import com.appier.ads.common.AppierDataKeys;
import com.mopub.mobileads.MoPubView;

// ...
Map<String, Object> localExtras = new HashMap<>();
localExtras.put(AppierDataKeys.AD_UNIT_ID_LOCAL, "<your_mopub_ad_unit_id>");
localExtras.put(AppierDataKeys.AD_WIDTH_LOCAL, 300);
localExtras.put(AppierDataKeys.AD_HEIGHT_LOCAL, 250);
MoPubView bannerAd = findViewById(R.id.my_sample_banner_ad);
bannerAd.setLocalExtras(localExtras);
bannerAd.setAdUnitId("<your_mopub_ad_unit_id>");
bannerAd.loadAd();
```

You also need to define the view dimension so the ads will not be cropped.

``` xml
<com.mopub.mobileads.MoPubView
  ...
  android:id="@+id/my_sample_banner_ad"
  android:layout_width="300dp"
  android:layout_height="250dp" />
```

### Interstitial Ads Integration

To render Appier's interstitial ads via MoPub mediation, you need to set up the ad unit id of ad unit and specify the width and height of the ad unit to load ads with suitable sizes. You can either pass through `localExtras` or `serverExtras`.

``` java
import com.appier.ads.common.AppierDataKeys;
import com.mopub.mobileads.MoPubInterstitial;

// ...
Map<String, Object> localExtras = new HashMap<>();
localExtras.put(AppierDataKeys.AD_UNIT_ID_LOCAL, "<your_mopub_ad_unit_id>");
localExtras.put(AppierDataKeys.AD_WIDTH_LOCAL, 320);
localExtras.put(AppierDataKeys.AD_HEIGHT_LOCAL, 480);
MoPubInterstitial interstitialAd = new MoPubInterstitial(...);
interstitialAd.setLocalExtras(localExtras);

// load Ad
interstitialAd.load();

// show Ad
interstitialAd.show();
```

### Interstitial Video Ads Integration

To render Appier's interstitial video ads via MoPub mediation, you need to set up the ad unit id of the ad unit. By default, the ad would show in the same orientation when the ad is loaded. You can specify the orientation of the ad unit based on your requirement.

``` java
import com.appier.ads.common.AppierDataKeys;
import com.mopub.mobileads.MoPubInterstitial;

// ...
Map<String, Object> localExtras = new HashMap<>();
localExtras.put(AppierDataKeys.AD_UNIT_ID_LOCAL, "<your_mopub_ad_unit_id>");
localExtras.put(AppierDataKeys.AD_ORIENTATION_LOCAL, Configuration.ORIENTATION_LANDSCAPE);
MoPubInterstitial videoAd = new MoPubInterstitial(...);
videoAd.setLocalExtras(localExtras);

// load Ad
videoAd.load();

// show Ad
videoAd.show();
```

### Rewarded Video Ads Integration

To render Appier's rewarded video ads via MoPub mediation, you need to set up the ad unit id of the ad unit. By default, the ad would show in the same orientation when the ad is loaded. You can specify the orientation of the ad unit based on your requirement.

``` java
import com.appier.ads.common.AppierDataKeys;
import com.mopub.common.MediationSettings;
import com.mopub.mobileads.AppierMediationSettings;
import com.mopub.mobileads.MoPubRewardedVideos;

// ...
Map<String, Object> localExtras = new HashMap<>();
localExtras.put(AppierDataKeys.AD_UNIT_ID_LOCAL, "<your_mopub_ad_unit_id>");
localExtras.put(AppierDataKeys.AD_ORIENTATION_LOCAL, Configuration.ORIENTATION_LANDSCAPE);

MediationSettings mediationSettings = new AppierMediationSettings().withLocalExtras(localExtras);

// load Ad
MoPubRewardedVideos.loadRewardedVideo("<your_mopub_ad_unit_id>", mediationSettings);

// show Ad
if (MoPubRewardedVideos.hasRewardedVideo("<your_mopub_ad_unit_id>")) {
  	MoPubRewardedVideos.showRewardedVideo("<your_mopub_ad_unit_id>");
}
```

## Predict Ads
Predict mode provides a function to do the Ad response prediction before real MoPub line items are triggered. It is recommended to do the prediction at the previous activity/user view before rendering ads.

Refer to [pmp-android-example](https://github.com/appier/pmp-android-sample) for sample integrations.

### Set keyword targeting for your line items
Before add prediction code into your android app project, You should add keywords for your line item. For details, you could contact our support.

### Before Ads triggered
We recommend to do the prediction at the previous activity/user view before rendering ads.
``` java
import com.appier.ads.AppierPredictor;
import com.mopub.mobileads.AppierPredictHandler;
import com.mopub.mobileads.AppierAdUnitIdentifier;

public class MainActivity extends AppCompatActivity {
   @Override
   protected void onCreate(Bundle savedInstanceState) {
       // ...
       AppierPredictor predictor = new AppierPredictor(
           getContext(),
           new AppierPredictHandler(getContext())
       );
        // Predict by the ad unit id. It is recommended to do the prediction
        // at the previous activity/user view before rendering ads.
       predictor.predictAd(new AppierAdUnitIdentifier("<your_mopub_ad_unit_id>"));
}
```

### Ads Integration
you should integrate the prediction result when trigger MoPub ad unit.

#### Native Ads
``` java
import com.appier.ads.common.AppierDataKeys;
import com.mopub.nativeads.AppierNativeAdRenderer;
import com.mopub.mobileads.AppierPredictHandler;

// ...
AppierNativeAdRenderer appierNativeAdRenderer = new AppierNativeAdRenderer(viewBinder);

// Set Local Extras
Map<String, Object> localExtras = new HashMap();
localExtras.put(AppierDataKeys.AD_UNIT_ID_LOCAL, "<your_mopub_ad_unit_id>");
moPubNative.setLocalExtras(localExtras);

// Required for predict mode.
RequestParameters parameters = new RequestParameters.Builder()
    .keywords(
        AppierPredictHandler.getKeywordTargeting("<your_mopub_ad_unit_id>")
    ).build();

// Integration with predict mode. Pass RequestParameters to makeRequest() 
// function
moPubNative.makeRequest(parameters);
```

#### Banner Ads
```java
import com.appier.ads.common.AppierDataKeys;
import com.mopub.mobileads.MoPubView;
import com.mopub.mobileads.AppierPredictHandler;

// ...
Map<String, Object> localExtras = new HashMap<>();
localExtras.put(AppierDataKeys.AD_WIDTH_LOCAL, 300);
localExtras.put(AppierDataKeys.AD_HEIGHT_LOCAL, 250);

// Integration with predict mode. Set your MoPub ad unit id into localExtras and use `AppierPredictHandler` to get the keywords provided by prediction.
localExtras.put(AppierDataKeys.AD_UNIT_ID_LOCAL, "<your_mopub_ad_unit_id>");
moPubView.setKeywords(AppierPredictHandler.getKeywordTargeting("<your_mopub_ad_unit_id>"));

moPubView.setLocalExtras(localExtras);
moPubView.setAdUnitId("<your_mopub_ad_unit_id>");
moPubView.loadAd();
```

#### Interstitial Ads
```java
import com.appier.ads.common.AppierDataKeys;
import com.mopub.mobileads.MoPubInterstitial;
import com.mopub.mobileads.AppierPredictHandler;

// ...
Map<String, Object> localExtras = new HashMap<>();
localExtras.put(AppierDataKeys.AD_WIDTH_LOCAL, 320);
localExtras.put(AppierDataKeys.AD_HEIGHT_LOCAL, 480);

// Integration with predict mode. Set your MoPub ad unit id into localExtras and use `AppierPredictHandler` to get the keywords provided by prediction.
localExtras.put(AppierDataKeys.AD_UNIT_ID_LOCAL, "<your_mopub_ad_unit_id>");
moPubInterstitial.setKeywords(AppierPredictHandler.getKeywordTargeting("<your_mopub_ad_unit_id>"));

moPubInterstitial.setLocalExtras(localExtras);
moPubInterstitial.setAdUnitId("<your_mopub_ad_unit_id>");
moPubInterstitial.load();
```
