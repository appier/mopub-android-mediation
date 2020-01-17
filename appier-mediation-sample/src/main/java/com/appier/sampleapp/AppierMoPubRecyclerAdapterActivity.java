// ref: https://stackoverflow.com/questions/40584424/simple-android-recyclerview-example

package com.appier.sampleapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.mopub.nativeads.AppierNativeAdRenderer;
import com.mopub.nativeads.MoPubRecyclerAdapter;
import com.mopub.nativeads.MoPubStaticNativeAdRenderer;
import com.mopub.nativeads.ViewBinder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AppierMoPubRecyclerAdapterActivity extends AppCompatActivity implements MyRecyclerViewAdapter.ItemClickListener {
    private static final String LOG_TAG = "AppierMediation";
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
        setContentView(R.layout.activity_appier_mopubrecycleradapter);

        ArrayList<String> itemArrayList = new ArrayList<String>();
        List<String> itemList = Arrays.asList(items);
        itemArrayList.addAll(itemList);

        recyclerView = (RecyclerView)findViewById(R.id.recycler_view_with_ads);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        MyRecyclerViewAdapter adapter = new MyRecyclerViewAdapter(this, itemArrayList);
        adapter.setClickListener(this);

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

        moPubAdAdapter = new MoPubRecyclerAdapter(this, adapter);
        moPubAdAdapter.registerAdRenderer(appierNativeAdRenderer);
        moPubAdAdapter.registerAdRenderer(moPubStaticNativeAdRenderer);

        recyclerView.setAdapter(moPubAdAdapter);

        moPubAdAdapter.loadAds(getString(R.string.adunit_appier_native_sample_default));
    }

    @Override
    public void onItemClick(View view, int position, long id) {
        Log.d(LOG_TAG, "[Sample App] List item " + items[(int)id]);
    }

    @Override
    protected void onDestroy() {
        moPubAdAdapter.destroy();
        super.onDestroy();
    }
}