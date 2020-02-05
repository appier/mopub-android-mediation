# Appier Mediation for MoPub Android SDK

This is Appier's official android mediation repository for MoPub SDK.

## Prerequisites

- Make sure you are using MoPub android SDK `4.20.0`
- Make sure your app's API level >= 18
- Make sure you have already configured line items on MoPub Web UI
	- `Custom event class` field should be one of Appier's predefined class names
		- `com.mopub.nativeads.AppierNative` for native ads
		- `com.mopub.mobileads.AppierBanner` for banner ads
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
+     implementation 'com.appier.android:ads-sdk:1.0.0-rc3@aar'
+     implementation 'com.appier.android:mopub-mediation:1.0.0-rc3@aar'
  }
```

## SDK Initialization

You can pass GDPR consent status to Appier SDK via `Appier.setGDPRApplies()`.
Without this configuration, Appier will not apply GDPR by default.

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

## Banner Ads Integration

To render appier's banner ads via MoPub mediaiton, you need to specify the width and height of ad unit to load ads with suitable sizes. You can either pass through `localExtras` or `serverExtras`.

``` java
import com.appier.ads.common.AppierDataKeys;
import com.mopub.mobileads.MoPubView;

// ...
Map<String, Object> localExtras = new HashMap<>();
localExtras.put(AppierDataKeys.AD_WIDTH_LOCAL, 300);
localExtras.put(AppierDataKeys.AD_HEIGHT_LOCAL, 250);
MoPubView moPubView = findViewById(R.id.my_sample_banner_ad);
moPubView.setLocalExtras(localExtras);
```

You also need to define the view dimension so the ads will not be cropped.

``` xml
<com.mopub.mobileads.MoPubView
  ...
  android:id="@+id/my_sample_banner_ad"
  android:layout_width="300dp"
  android:layout_height="250dp" />
```
