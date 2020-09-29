/**
 * Reference: https://stackoverflow.com/questions/40584424/simple-android-recyclerview-example
 */

package com.appier.sampleapp.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;

import com.appier.ads.Appier;
import com.appier.sampleapp.R;
import com.appier.sampleapp.common.MyRecyclerViewAdapter;
import com.mopub.nativeads.AppierNativeAdRenderer;
import com.mopub.nativeads.MoPubRecyclerAdapter;
import com.mopub.nativeads.MoPubStaticNativeAdRenderer;
import com.mopub.nativeads.ViewBinder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AppierNativeMoPubRecyclerAdapterActivity extends AppCompatActivity implements MyRecyclerViewAdapter.ItemClickListener {
    private RecyclerView recyclerView;
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
    private MoPubRecyclerAdapter moPubAdAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appier_native_mopubrecycleradapter);

        ArrayList<String> itemArrayList = new ArrayList<String>();
        List<String> itemList = Arrays.asList(items);
        itemArrayList.addAll(itemList);

        recyclerView = (RecyclerView)findViewById(R.id.recycler_view_with_ads);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        MyRecyclerViewAdapter adapter = new MyRecyclerViewAdapter(this, itemArrayList);
        adapter.setClickListener(this);

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

        moPubAdAdapter = new MoPubRecyclerAdapter(this, adapter);
        moPubAdAdapter.registerAdRenderer(appierNativeAdRenderer);
        moPubAdAdapter.registerAdRenderer(moPubStaticNativeAdRenderer);

        recyclerView.setAdapter(moPubAdAdapter);

        moPubAdAdapter.loadAds(getString(R.string.adunit_appier_native_sample_default));
    }

    @Override
    public void onItemClick(View view, int position, long id) {
        Appier.log("[Sample App]", "List item", items[(int)id]);
    }

    @Override
    protected void onDestroy() {
        moPubAdAdapter.destroy();
        super.onDestroy();
    }
}
