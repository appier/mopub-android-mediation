package com.appier.sampleapp;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class AppierNativeManualIntegrationTabFragment3 extends BaseFragment {
    private MyService mMyService;

    public AppierNativeManualIntegrationTabFragment3(MyService service, @Nullable EventListener eventListener) {
        super(eventListener);
        this.mMyService = service;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_appier_native_manual_integration_tab_fragment_3, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(mMyService);
    }
}
