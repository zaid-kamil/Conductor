package com.bluelinelabs.conductor.demo.controllers.base;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;

import com.bluelinelabs.conductor.rxlifecycle.RxController;

import butterknife.ButterKnife;

public abstract class ButterKnifeController extends RxController {

    protected ButterKnifeController() { }
    protected ButterKnifeController(Bundle args) {
        super(args);
    }

    @Override
    protected void onBindView(@NonNull View view) {
        super.onBindView(view);
        ButterKnife.bind(this, view);
    }

    @Override
    protected void onUnbindView(View view) {
        super.onUnbindView(view);
        ButterKnife.unbind(this);
    }

}