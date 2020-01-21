# Appier Mediation for MoPub Android SDK

This is Appier's official android mediation repository for MoPub SDK.

## Prerequisites

- Make sure you are using MoPub android SDK `4.20.0`
- Make sure you have already configured line items on MoPub Web UI
	- `Custom event class` field should be `com.mopub.nativeads.AppierNative`
	- `Custom event data` field should follow the format `{ "zoneId": "<your_zone_id_from_appier>" }`

## Gradle Configuration

Please add jcenter to your repositories, and specify both appier's sdk and mediation to mopub as dependencies:

``` diff
  repositories {
      // ...
+     jcenter()
  }

  dependencies {
      // ...
+     implementation 'com.appier.android:ads-sdk:1.0.0-rc1@aar'
+     implementation 'com.appier.android:mopub-mediation:1.0.0-rc1@aar'
  }
```

## Native Ads Integration

To render appier's native ads via MoPub mediaiton, you need to register `AppierNativeAdRenderer` to your own `MoPubNative`, `MoPubAdAdapter`, or `MoPubRecyclerAdapter` instance:

``` java
import com.mopub.nativeads.AppierNativeAdRenderer;

// ...
AppierNativeAdRenderer appierNativeAdRenderer = new AppierNativeAdRenderer(viewBinder);

// Option 1: Manual Integration
MoPubNative moPubNative = new MoPubNative(...);
moPubNative.registerAdRenderer(appierNativeAdRenderer);

// Option 2: MoPubAdAdapter
MoPubAdAdapter moPubAdAdapter = new MoPubAdAdapter(...)
moPubAdAdapter.registerAdRenderer(appierNativeAdRenderer);

// Option 3: MoPubRecyclerAdapter
MoPubRecyclerAdapter moPubRecyclerAdapter = new MoPubRecyclerAdapter(...);
moPubRecyclerAdapter.registerAdRenderer(appierNativeAdRenderer);
```
