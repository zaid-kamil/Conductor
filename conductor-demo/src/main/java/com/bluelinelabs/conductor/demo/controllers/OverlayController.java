package com.bluelinelabs.conductor.demo.controllers;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bluelinelabs.conductor.demo.R;
import com.bluelinelabs.conductor.demo.controllers.base.RefWatchingController;

import butterknife.Bind;

public class OverlayController extends RefWatchingController {

    @Bind(R.id.text_view) TextView mTextView;

    @NonNull
    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        return inflater.inflate(R.layout.controller_overlay, container, false);
    }

    @Override
    public void onBindView(@NonNull View view) {
        super.onBindView(view);
        mTextView.setText("I'm an Overlay");
    }

}
