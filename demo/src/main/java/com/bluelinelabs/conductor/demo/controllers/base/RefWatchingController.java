package com.bluelinelabs.conductor.demo.controllers.base;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;

import com.bluelinelabs.conductor.demo.DemoApplication;

public abstract class RefWatchingController extends ButterKnifeController {

    protected RefWatchingController() { }
    protected RefWatchingController(Bundle args) {
        super(args);
    }

    @Override
    protected void onDetach(@NonNull View view) {
        super.onDetach(view);

        if (isDestroyed()) {
            DemoApplication.refWatcher.watch(view);
        }
    }

    @Override
    public void onDestroy() {
        if (getView() !=  null) {
            DemoApplication.refWatcher.watch(getView());
        }
        DemoApplication.refWatcher.watch(this);
    }

}
