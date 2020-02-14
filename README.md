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

Please add jcenter to your repositories, and specify both MoPub’s dependencies and Appier's dependencies.

*MoPub Dependencies:*
``` diff

  repositories {
      // ...
+     jcenter()
  }

  dependencies {
      // MoPub SDK base
+     implementation('com.mopub:mopub-sdk-base:4.20.0@aar') {
+         transitive = true
+         exclude module: 'libAvid-mopub' // To exclude AVID
+         exclude module: 'moat-mobile-app-kit' // To exclude Moat
+     }

      // MoPub SDK for native static (images)
+     implementation('com.mopub:mopub-sdk-native-static:4.20.0@aar') {
+         transitive = true
+         exclude module: 'libAvid-mopub' // To exclude AVID
+         exclude module: 'moat-mobile-app-kit' // To exclude Moat
+     }

      // MoPub SDK for banner
+     implementation('com.mopub:mopub-sdk-banner:4.20.0@aar') {
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
+     jcenter()
  }

  dependencies {
      // ...
+     implementation 'com.appier.android:ads-sdk:1.0.0-rc4'
+     implementation('com.appier.android:mopub-mediation:1.0.0-rc4') {
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
  <base-config cleartextTrafficPermitted="true">
    <trust-anchors>
      <certificates src="system"/>
    </trust-anchors>
  </base-config>
  <debug-overrides>
    <trust-anchors>
      <certificates src="user" />
    </trust-anchors>
  </debug-overrides>
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

## Native Ads Integration

To render appier's native ads via MoPub mediation, you need to register `AppierNativeAdRenderer` to your own `MoPubNative`, `MoPubAdAdapter`, or `MoPubRecyclerAdapter` instance:

``` java
import com.mopub.nativeads.AppierNativeAdRenderer;

// ...
AppierNativeAdRenderer appierNativeAdRenderer = new AppierNativeAdRenderer(viewBinder);

// Option 1: Manual Integration
MoPubNative moPubNative = new MoPubNative(...);
moPubNative.registerAdRenderer(appierNativeAdRenderer);

// Option 2: MoPubAdAdapter
MoPubAdAdapter moPubAdAdapter = new MoPubAdAdapter(...);
moPubAdAdapter.registerAdRenderer(appierNativeAdRenderer);

// Option 3: MoPubRecyclerAdapter
MoPubRecyclerAdapter moPubRecyclerAdapter = new MoPubRecyclerAdapter(...);
moPubRecyclerAdapter.registerAdRenderer(appierNativeAdRenderer);
```

## Banner Ads Integration

To render appier's banner ads via MoPub mediation, you need to specify the width and height of ad unit to load ads with suitable sizes. You can either pass through `localExtras` or `serverExtras`.

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
