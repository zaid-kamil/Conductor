package com.bluelinelabs.conductor.demo.controllers.base;

import android.os.Bundle;

import com.bluelinelabs.conductor.demo.DemoApplication;

public abstract class RefWatchingController extends ButterKnifeController {

    protected RefWatchingController() { }
    protected RefWatchingController(Bundle args) {
        super(args);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        DemoApplication.refWatcher.watch(this);
    }

}
