package com.appier.sampleapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;

public class AppierNativeManualIntegrationFloatingWindowActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_DRAW_OVERLAY_PERMISSION = 5;
    private AppierNativeManualIntegrationFloatingWindowFloatViewManager mFloatViewManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appier_native_manual_integration_floating_window);

        mFloatViewManager = new AppierNativeManualIntegrationFloatingWindowFloatViewManager(AppierNativeManualIntegrationFloatingWindowActivity.this);

        findViewById(R.id.button_open_floating_window).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkDrawOverlayPermission()) {
                    mFloatViewManager.showFloatView();
                }
            }
        });
    }

    // After user grants overlay permission
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_DRAW_OVERLAY_PERMISSION) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Settings.canDrawOverlays(this)) {
                mFloatViewManager.showFloatView();
            }
        }
    }

    private boolean checkDrawOverlayPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, REQUEST_CODE_DRAW_OVERLAY_PERMISSION);
            return false;
        } else {
            return true;
        }
    }
}
