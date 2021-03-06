package com.mopub.nativeads;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.appier.ads.Appier;

public class AppierNativeAdRenderer implements MoPubAdRenderer<AppierNative.AppierStaticNativeAd> {
    @NonNull
    private final ViewBinder viewBinder;

    public AppierNativeAdRenderer(@NonNull final ViewBinder viewBinder) {
        this.viewBinder = viewBinder;
    }

    @NonNull
    @Override
    public View createAdView(@NonNull final Context context, @Nullable final ViewGroup parent) {
        Appier.log("[Appier MoPub Mediation]", "AppierNativeAdRenderer.createAdView()");
        return LayoutInflater
            .from(context)
            .inflate(viewBinder.layoutId, parent, false);
    }

    @Override
    public void renderAdView(@NonNull final View view,
                             @NonNull AppierNative.AppierStaticNativeAd appierStaticNativeAd) {
        Appier.log("[Appier MoPub Mediation]", "AppierNativeAdRenderer.renderAdView()");
        TextView tvTitle = view.findViewById(viewBinder.titleId);
        TextView tvText = view.findViewById(viewBinder.textId);
        TextView tvCallToAction = (Button)view.findViewById(viewBinder.callToActionId);
        ImageView imgMain = view.findViewById(viewBinder.mainImageId);
        ImageView imgIcon = view.findViewById(viewBinder.iconImageId);
        ImageView imgPrivacyInfoIcon = view.findViewById(viewBinder.privacyInformationIconImageId);

        if (tvTitle != null)
            NativeRendererHelper.addTextView(tvTitle, appierStaticNativeAd.getTitle());
        if (tvText != null)
            NativeRendererHelper.addTextView(tvText, appierStaticNativeAd.getText());
        if (tvCallToAction != null)
            NativeRendererHelper.addTextView(tvCallToAction, appierStaticNativeAd.getCallToAction());
        if (imgMain != null)
            NativeImageHelper.loadImageView(appierStaticNativeAd.getMainImageUrl(), imgMain);
        if (imgIcon != null)
            NativeImageHelper.loadImageView(appierStaticNativeAd.getIconImageUrl(), imgIcon);
        if (imgPrivacyInfoIcon != null)
            NativeRendererHelper.addPrivacyInformationIcon(
                imgPrivacyInfoIcon,
                appierStaticNativeAd.getPrivacyInformationIconImageUrl(),
                appierStaticNativeAd.getPrivacyInformationIconClickThroughUrl()
            );
    }

    @Override
    public boolean supports(@NonNull final BaseNativeAd nativeAd) {
        boolean isSupport = nativeAd instanceof AppierNative.AppierStaticNativeAd;
        Appier.log("[Appier MoPub Mediation]", "AppierNativeAdRenderer.supports(), isSupport =", isSupport);
        return isSupport;
    }
}
