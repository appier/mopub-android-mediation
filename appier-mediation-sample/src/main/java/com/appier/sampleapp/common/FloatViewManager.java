package com.appier.sampleapp.common;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;

import com.appier.sampleapp.R;

import java.util.Timer;
import java.util.TimerTask;

import static android.content.Context.WINDOW_SERVICE;

public class FloatViewManager {
    private static final int REQUEST_CODE_DRAW_OVERLAY_PERMISSION = 5;
    private static boolean mIsFloatViewShowing;

    public static WindowManager.LayoutParams getLayoutParams() {
        WindowManager.LayoutParams floatViewLayoutParams = new WindowManager.LayoutParams();
        floatViewLayoutParams.format = PixelFormat.TRANSLUCENT;
        floatViewLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

        /*
         * Floating window requires different permissions in different API levels.
         * Be careful about setting correct permission.
         * Otherwise, the app will crash with WindowManager$BadTokenException: Unable to add window -- token null is not valid; is your activity running?
         *
         * References:
         *   - https://blog.csdn.net/ltym2014/article/details/78860620
         *   - https://blog.csdn.net/ajgjagdasnbd/article/details/77982472
         */
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            // <= Android 4.3 (API 18)
            floatViewLayoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        } else if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.N) {
            // Android 4.4 (API 19) to 7.0 (API 24)
            floatViewLayoutParams.type = WindowManager.LayoutParams.TYPE_TOAST;
        } else if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.N_MR1) {
            // Android 7.1 (API 25)
            floatViewLayoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        } else {
            // >= Android 8.0 (API 26)
            floatViewLayoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        }

        floatViewLayoutParams.gravity = Gravity.CENTER;
        floatViewLayoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        floatViewLayoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        return floatViewLayoutParams;
    }

    /**
     * @return isPermissionGranted
     * This permission cannot be trusted on Android 8.0 and some devices of Android 8.1.
     * Please refer to the comments inside the implementation for more details.
     */
    public static boolean canDrawOverlays(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            /*
             * < Android 6.0 (API 23)
             * ======================
             * The permission is automatically granted for older APIs
             */
            return true;
        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            /*
             * Android 6.0 (API 23) to 7.1 (API 25)
             * ====================================
             * `Settings.canDrawOverlays` is introduced since android M.
             */
            return Settings.canDrawOverlays(context);
        } else if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.O_MR1) {
            /*
             * Android 8.0 (API 26) to 8.1 (API 27)
             * ====================================
             * On 8.0, `Settings.canDrawOverlays` returns true but only when you wait for 5 to 15 seconds. [^1]
             * On 8.1 on some devices, we still can get false even if the permission was just received. [^2]
             *
             * To workaround, we firstly use `AppOpsManager`. [^3]
             * If it doesn't work, we can fall back to add an invisible overlay.
             * If an exception is thrown, the permission isn't granted. [^4][^5]
             *
             * References:
             *   [1]: https://stackoverflow.com/a/49328532
             *   [2]: https://stackoverflow.com/a/55581534
             *   [3]: https://stackoverflow.com/a/51249708
             *   [4]: https://stackoverflow.com/a/46174872
             *   [5]: https://stackoverflow.com/questions/46173460/why-in-android-8-method-settings-candrawoverlays-returns-false-when-user-has
             */
            if (Settings.canDrawOverlays(context)) {
                return true;
            }

            AppOpsManager appOpsMgr = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
            int mode = appOpsMgr.checkOpNoThrow(AppOpsManager.OPSTR_SYSTEM_ALERT_WINDOW, android.os.Process.myUid(), context.getPackageName());
            switch (mode) {
                case AppOpsManager.MODE_ALLOWED: // when granted and reopen the app
                    return true;
                case AppOpsManager.MODE_ERRORED: // when the application does not have this permission
                    return false;
                case AppOpsManager.MODE_IGNORED: // returned from permission request prompt to the application
                    /*
                     * For this case, the permission may or may not be granted.
                     */
                    break;
                case AppOpsManager.MODE_DEFAULT:
                    break;
            }

            try {
                WindowManager mgr = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

                // getSystemService might return null
                if (mgr == null) {
                    return false;
                }
                View viewToAdd = new View(context);
                WindowManager.LayoutParams params = FloatViewManager.getLayoutParams();
                viewToAdd.setLayoutParams(params);
                mgr.addView(viewToAdd, params);
                mgr.removeView(viewToAdd);
                /*
                 * If the permission is not granted and user ignores the permission request prompt directly, there will be no exception.
                 * In such special case (Android O & MODE_IGNORED & no exception thrown),
                 * floating window totally works and bypass granting both `SYSTEM_ALERT_WINDOW` and `ACTION_MANAGE_OVERLAY_PERMISSION` permissions.
                 */
                return true;
            } catch (WindowManager.BadTokenException e) {}
            return false;
        } else {
            /*
             * > Android 8.1 (API 27)
             */
            return Settings.canDrawOverlays(context);
        }
    }

    private Activity mActivity;
    private OnFloatViewEventListener mEventListener;
    private View mFloatView;
    private LinearLayout mContentContainer;
    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mFloatViewLayoutParams;
    private boolean mFloatViewTouchConsumedByMove = false;
    private int mFloatViewLastX;
    private int mFloatViewLastY;
    private int mFloatViewFirstX;
    private int mFloatViewFirstY;

    @SuppressLint("InflateParams")
    public FloatViewManager(Activity activity, OnFloatViewEventListener eventListener) {
        this.mActivity = activity;
        this.mEventListener = eventListener;
        this.mFloatView = LayoutInflater
                .from(activity)
                .inflate(R.layout.template_floating_window, null);
        this.mContentContainer = this.mFloatView.findViewById(R.id.content_container);
        this.mFloatViewLayoutParams = FloatViewManager.getLayoutParams();
        this.initializeEventBinding();
    }

    private void initializeEventBinding() {
        Button buttonDismiss = this.mFloatView.findViewById(R.id.button_dismiss);
        buttonDismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                close();
            }
        });
        this.mFloatView.setOnTouchListener(this.mFloatViewOnTouchListener);
    }

    public void requestDrawOverlayPermission() {
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + mActivity.getPackageName()));
        mActivity.startActivityForResult(intent, REQUEST_CODE_DRAW_OVERLAY_PERMISSION);
    }

    public void handleActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_DRAW_OVERLAY_PERMISSION: {
                mEventListener.onDrawOverlayPermissionResult(FloatViewManager.canDrawOverlays(mActivity));
            }
        }
    }

    public void open() {
        if (!mIsFloatViewShowing) {
            mIsFloatViewShowing = true;
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (!mActivity.isFinishing()) {
                        mWindowManager = (WindowManager) mActivity.getSystemService(WINDOW_SERVICE);
                        if (mWindowManager != null) {
                            mWindowManager.addView(mFloatView, mFloatViewLayoutParams);
                            mEventListener.onOpen(mContentContainer);
                        }
                    }
                }
            });
        }
    }

    public void openWithPermissionCheck() {
        if (FloatViewManager.canDrawOverlays(mActivity)) {
            try {
                open();
            } catch (Exception e) {
                requestDrawOverlayPermission();
            }
        } else {
            requestDrawOverlayPermission();
        }
    }

    public void close() {
        close(0);
    }

    public void close(int closeDelay) {
        if (mIsFloatViewShowing) {
            mIsFloatViewShowing = false;
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (mWindowManager != null) {
                                mWindowManager.removeViewImmediate(mFloatView);
                            }
                            mEventListener.onClose(mContentContainer);
                        }
                    });
                }
            }, closeDelay);
        }
    }

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

    public interface OnFloatViewEventListener {
        void onOpen(LinearLayout contentContainer);
        void onDrawOverlayPermissionResult(boolean isPermissionGranted);
        void onClose(LinearLayout contentContainer);
    }
}
