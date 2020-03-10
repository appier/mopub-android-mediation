package com.appier.sampleapp.fragment;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.appier.sampleapp.R;

public class AppierNativeManualIntegrationTabFragment1 extends BaseFragment {
    public AppierNativeManualIntegrationTabFragment1(@Nullable EventListener eventListener) {
        super(eventListener);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_appier_native_manual_integration_tab_fragment_1, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(getActivity());
    }
}
