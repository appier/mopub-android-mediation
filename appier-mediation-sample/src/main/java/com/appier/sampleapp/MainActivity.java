package com.appier.sampleapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    private View.OnClickListener btnOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Class<?> cls = null;
            switch (v.getId()) {
                case R.id.button_mopub_manual_integration_default:
                    cls = MoPubManualIntegrationDefaultActivity.class;
                    break;
                case R.id.button_mopub_manual_integration_official_sample:
                    cls = MoPubManualIntegrationOfficialSampleActivity.class;
                    break;
                case R.id.button_appier_manual_integration_default:
                    cls = AppierManualIntegrationDefaultActivity.class;
                    break;
                case R.id.button_appier_manual_integration_floating_window:
                    cls = AppierManualIntegrationFloatingWindowActivity.class;
                    break;
                case R.id.button_appier_mopubadadapter:
                    cls = AppierMoPubAdAdapterActivity.class;
                    break;
                case R.id.button_appier_mopubrecycleradapter:
                    cls = AppierMoPubRecyclerAdapterActivity.class;
                    break;
            }
            Intent intent = new Intent(MainActivity.this, cls);
            startActivity(intent);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.button_mopub_manual_integration_default).setOnClickListener(btnOnClickListener);
        findViewById(R.id.button_mopub_manual_integration_official_sample).setOnClickListener(btnOnClickListener);
        findViewById(R.id.button_appier_manual_integration_default).setOnClickListener(btnOnClickListener);
        findViewById(R.id.button_appier_manual_integration_floating_window).setOnClickListener(btnOnClickListener);
        findViewById(R.id.button_appier_mopubadadapter).setOnClickListener(btnOnClickListener);
        findViewById(R.id.button_appier_mopubrecycleradapter).setOnClickListener(btnOnClickListener);
    }
}
