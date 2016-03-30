package com.bluelinelabs.conductor.changehandler;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;

import com.bluelinelabs.conductor.ControllerChangeHandler;

/**
 * A {@link ControllerChangeHandler} that will instantly swap Views with no animations or transitions.
 */
public class SimpleSwapChangeHandler extends ControllerChangeHandler {

    private static final String KEY_REMOVES_FROM_ON_PUSH = "SimpleSwapChangeHandler.removesFromViewOnPush";

    private boolean mRemovesFromViewOnPush;

    public SimpleSwapChangeHandler() {
        this(true);
    }

    public SimpleSwapChangeHandler(boolean removesFromViewOnPush) {
        mRemovesFromViewOnPush = removesFromViewOnPush;
    }

    @Override
    public void saveToBundle(@NonNull Bundle bundle) {
        super.saveToBundle(bundle);
        bundle.putBoolean(KEY_REMOVES_FROM_ON_PUSH, mRemovesFromViewOnPush);
    }

    @Override
    public void restoreFromBundle(@NonNull Bundle bundle) {
        super.restoreFromBundle(bundle);
        mRemovesFromViewOnPush = bundle.getBoolean(KEY_REMOVES_FROM_ON_PUSH);
    }

    @Override
    public void performChange(@NonNull ViewGroup container, View from, View to, boolean isPush, @NonNull final ControllerChangeCompletedListener changeListener) {
        if (from != null && (!isPush || mRemovesFromViewOnPush)) {
            container.removeView(from);
        }

        if (to != null && to.getParent() == null) {
            container.addView(to);
        }

        changeListener.onChangeCompleted();
    }

}