package com.appier.sampleapp;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.PixelFormat;
import android.os.Build;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.mopub.nativeads.AdapterHelper;
import com.mopub.nativeads.AppierNativeAdRenderer;
import com.mopub.nativeads.MoPubNative;
import com.mopub.nativeads.MoPubStaticNativeAdRenderer;
import com.mopub.nativeads.NativeAd;
import com.mopub.nativeads.NativeErrorCode;
import com.mopub.nativeads.ViewBinder;

import static android.content.Context.WINDOW_SERVICE;


/**
 * Created by aminography on 11/1/2018.
 */
public class AppierManualIntegrationFloatingWindowFloatViewManager {

    private static boolean mIsFloatViewShowing;

    private Activity mActivity;
    private WindowManager mWindowManager;
    private View mFloatView;
    private WindowManager.LayoutParams mFloatViewLayoutParams;
    private boolean mFloatViewTouchConsumedByMove = false;
    private int mFloatViewLastX;
    private int mFloatViewLastY;
    private int mFloatViewFirstX;
    private int mFloatViewFirstY;

    private View adView;
    private MoPubNative moPubNative;

    @SuppressLint("InflateParams")
    public AppierManualIntegrationFloatingWindowFloatViewManager(Activity activity) {
        mActivity = activity;
        LayoutInflater inflater = LayoutInflater.from(activity);
        mFloatView = inflater.inflate(R.layout.appier_manual_integration_floating_window, null);
        Button buttonDismiss = mFloatView.findViewById(R.id.button_dismiss);
        buttonDismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissFloatView();
            }
        });

        mFloatView.setOnClickListener(mFloatViewOnClickListener);
        mFloatView.setOnTouchListener(mFloatViewOnTouchListener);

        mFloatViewLayoutParams = new WindowManager.LayoutParams();
        mFloatViewLayoutParams.format = PixelFormat.TRANSLUCENT;
        mFloatViewLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

        mFloatViewLayoutParams.type = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
            ? WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            : WindowManager.LayoutParams.TYPE_TOAST;

        mFloatViewLayoutParams.gravity = Gravity.CENTER;
        mFloatViewLayoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mFloatViewLayoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

    }

    public void dismissFloatView() {
        if (mIsFloatViewShowing) {
            mIsFloatViewShowing = false;
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mWindowManager != null) {
                        mWindowManager.removeViewImmediate(mFloatView);
                    }
                    if (adView != null) {
                        LinearLayout mAdContainer = mFloatView.findViewById(R.id.ad_container);
                        mAdContainer.removeView(adView);
                    }
                }
            });
            if (moPubNative != null) {
                moPubNative.destroy();
                moPubNative = null;
            }
        }
    }

    public void showFloatView() {
        if (!mIsFloatViewShowing) {
            mIsFloatViewShowing = true;
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (!mActivity.isFinishing()) {
                        mWindowManager = (WindowManager) mActivity.getSystemService(WINDOW_SERVICE);
                        if (mWindowManager != null) {
                            mWindowManager.addView(mFloatView, mFloatViewLayoutParams);
                            showAd();
                        }
                    }
                }
            });
        }
    }

    public void showAd() {
        final String LOG_TAG = "AppierMediation";

        final NativeAd.MoPubNativeEventListener moPubNativeEventListener;
        MoPubNative.MoPubNativeNetworkListener moPubNativeNetworkListener;

        moPubNativeEventListener = new NativeAd.MoPubNativeEventListener() {
            @Override
            public void onImpression(View view) {
                Log.d(LOG_TAG, "[Sample App] Native ad recorded an impression.");
                // Impress is recorded - do what is needed AFTER the ad is visibly shown here.
            }

            @Override
            public void onClick(View view) {
                Log.d(LOG_TAG, "[Sample App] Native ad recorded a click.");
                // Click tracking.
                dismissFloatView();
            }
        };
        moPubNativeNetworkListener = new MoPubNative.MoPubNativeNetworkListener() {
            @Override
            public void onNativeLoad(final NativeAd nativeAd) {
                Log.d(LOG_TAG, "[Sample App] Native ad has loaded.");

                final AdapterHelper adapterHelper = new AdapterHelper(mActivity, 0, 3); // When standalone, any range will be fine.

                // Retrieve the pre-built ad view that AdapterHelper prepared for us.
                adView = adapterHelper.getAdView(null, null, nativeAd, new ViewBinder.Builder(0).build());

                // Set the native event listeners (onImpression, and onClick).
                nativeAd.setMoPubNativeEventListener(moPubNativeEventListener);

                // Add the ad view to our view hierarchy
                LinearLayout mAdContainer = mFloatView.findViewById(R.id.ad_container);
                mAdContainer.addView(adView);
            }

            @Override
            public void onNativeFail(NativeErrorCode errorCode) {
                Log.d(LOG_TAG, "[Sample App] Native ad failed to load with error: " + errorCode.toString());
            }
        };
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

        moPubNative = new MoPubNative(mActivity, mActivity.getString(R.string.adunit_appier_native_sample_default), moPubNativeNetworkListener);
        moPubNative.registerAdRenderer(appierNativeAdRenderer);
        moPubNative.registerAdRenderer(moPubStaticNativeAdRenderer);
        Log.d(LOG_TAG, "[Sample App] ====== make request ======");
        moPubNative.makeRequest();
    }

    @SuppressWarnings("FieldCanBeLocal")
    private View.OnClickListener mFloatViewOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(mActivity, "Float view is clicked!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    };

    @SuppressWarnings("FieldCanBeLocal")
    private View.OnTouchListener mFloatViewOnTouchListener = new View.OnTouchListener() {

        @SuppressLint("ClickableViewAccessibility")
        @TargetApi(Build.VERSION_CODES.FROYO)
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            WindowManager.LayoutParams prm = mFloatViewLayoutParams;
            int totalDeltaX = mFloatViewLastX - mFloatViewFirstX;
            int totalDeltaY = mFloatViewLastY - mFloatViewFirstY;

            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    mFloatViewLastX = (int) event.getRawX();
                    mFloatViewLastY = (int) event.getRawY();
                    mFloatViewFirstX = mFloatViewLastX;
                    mFloatViewFirstY = mFloatViewLastY;
                    break;
                case MotionEvent.ACTION_UP:
                    break;
                case MotionEvent.ACTION_MOVE:
                    int deltaX = (int) event.getRawX() - mFloatViewLastX;
                    int deltaY = (int) event.getRawY() - mFloatViewLastY;
                    mFloatViewLastX = (int) event.getRawX();
                    mFloatViewLastY = (int) event.getRawY();
                    if (Math.abs(totalDeltaX) >= 5 || Math.abs(totalDeltaY) >= 5) {
                        if (event.getPointerCount() == 1) {
                            prm.x += deltaX;
                            prm.y += deltaY;
                            mFloatViewTouchConsumedByMove = true;
                            if (mWindowManager != null) {
                                mWindowManager.updateViewLayout(mFloatView, prm);
                            }
                        } else {
                            mFloatViewTouchConsumedByMove = false;
                        }
                    } else {
                        mFloatViewTouchConsumedByMove = false;
                    }
                    break;
                default:
                    break;
            }
            return mFloatViewTouchConsumedByMove;
        }
    };

}
