package com.appier.sampleapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.appier.ads.Appier;
import com.mopub.nativeads.AppierNativeAdRenderer;
import com.mopub.nativeads.MoPubAdAdapter;
import com.mopub.nativeads.MoPubStaticNativeAdRenderer;
import com.mopub.nativeads.ViewBinder;

public class AppierMoPubAdAdapterActivity extends AppCompatActivity {
    private ListView listView;
    private String[] items = new String[]{
        "Ipsum", "is", "simply", "dummy",
        "text", "of", "the", "printing",
        "and", "typesetting", "industry.", "Lorem",
        "Ipsum", "has", "been", "the",
        "industry's", "standard", "dummy", "text",
        "ever", "since", "the", "1500s,",
        "when", "an", "unknown", "printer",
        "took", "a", "galley", "of",
        "type", "and", "scrambled", "it",
        "to", "make", "a", "type",
        "specimen", "book.", "It", "has",
        "survived", "not", "only", "five",
        "centuries,", "but", "also", "the",
        "leap", "into", "electronic", "typesetting,",
        "remaining", "essentially", "unchanged.", "It",
        "was", "popularised", "in", "the",
        "1960s", "with", "the", "release",
        "of", "Letraset", "sheets",
    };
    private MoPubAdAdapter moPubAdAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appier_mopubadadapter);

        listView = findViewById(R.id.list_view_with_ads);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items);

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

        moPubAdAdapter = new MoPubAdAdapter(this, adapter);
        moPubAdAdapter.registerAdRenderer(appierNativeAdRenderer);
        moPubAdAdapter.registerAdRenderer(moPubStaticNativeAdRenderer);

        listView.setAdapter(moPubAdAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Appier.log("[Sample App]", "List item", items[(int)id]);
            }
        });

        moPubAdAdapter.loadAds(getString(R.string.adunit_appier_native_sample_default));
    }

    @Override
    protected void onDestroy() {
        moPubAdAdapter.destroy();
        super.onDestroy();
    }
}
