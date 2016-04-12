package com.bluelinelabs.conductor.changehandler;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;

import com.bluelinelabs.conductor.ControllerChangeHandler;
import com.bluelinelabs.conductor.internal.ClassUtils;

/**
 * A base {@link ControllerChangeHandler} that facilitates using {@link android.transition.Transition}s to replace Controller Views.
 * If the target device is running on a version of Android that doesn't support transitions, a fallback {@link ControllerChangeHandler} will be used.
 */
public class TransitionChangeHandlerCompat extends ControllerChangeHandler {

    private static final String KEY_TRANSITION_HANDLER_CLASS = "TransitionChangeHandlerCompat.transitionChangeHandler.class";
    private static final String KEY_FALLBACK_HANDLER_CLASS = "TransitionChangeHandlerCompat.fallbackChangeHandler.class";
    private static final String KEY_TRANSITION_HANDLER_STATE = "TransitionChangeHandlerCompat.transitionChangeHandler.state";
    private static final String KEY_FALLBACK_HANDLER_STATE = "TransitionChangeHandlerCompat.fallbackChangeHandler.state";

    private TransitionChangeHandler mTransitionChangeHandler;
    private ControllerChangeHandler mFallbackChangeHandler;

    public TransitionChangeHandlerCompat() { }

    /**
     * Constructor that takes a {@link TransitionChangeHandler} for use with compatible devices, as well as a fallback
     * {@link ControllerChangeHandler} for use with older devices.
     *
     * @param transitionChangeHandler The change handler that will be used on API 21 and above
     * @param fallbackChangeHandler The change handler that will be used on APIs below 21
     */
    public TransitionChangeHandlerCompat(TransitionChangeHandler transitionChangeHandler, ControllerChangeHandler fallbackChangeHandler) {
        mTransitionChangeHandler = transitionChangeHandler;
        mFallbackChangeHandler = fallbackChangeHandler;
    }

    @Override
    public void performChange(@NonNull final ViewGroup container, View from, View to, boolean isPush, @NonNull final ControllerChangeCompletedListener changeListener) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mTransitionChangeHandler.performChange(container, from, to, isPush, changeListener);
        } else {
            mFallbackChangeHandler.performChange(container, from, to, isPush, changeListener);
        }
    }

    @Override
    public void saveToBundle(@NonNull Bundle bundle) {
        super.saveToBundle(bundle);

        bundle.putString(KEY_TRANSITION_HANDLER_CLASS, mTransitionChangeHandler.getClass().getName());
        bundle.putString(KEY_FALLBACK_HANDLER_CLASS, mFallbackChangeHandler.getClass().getName());

        Bundle transitionBundle = new Bundle();
        mTransitionChangeHandler.saveToBundle(transitionBundle);
        bundle.putBundle(KEY_TRANSITION_HANDLER_STATE, transitionBundle);

        Bundle fallbackBundle = new Bundle();
        mFallbackChangeHandler.saveToBundle(fallbackBundle);
        bundle.putBundle(KEY_FALLBACK_HANDLER_STATE, fallbackBundle);
    }

    @Override
    public void restoreFromBundle(@NonNull Bundle bundle) {
        super.restoreFromBundle(bundle);

        String transitionClassName = bundle.getString(KEY_TRANSITION_HANDLER_CLASS);
        mTransitionChangeHandler = ClassUtils.newInstance(transitionClassName);
        //noinspection ConstantConditions
        mTransitionChangeHandler.restoreFromBundle(bundle.getBundle(KEY_TRANSITION_HANDLER_STATE));

        String fallbackClassName = bundle.getString(KEY_FALLBACK_HANDLER_CLASS);
        mFallbackChangeHandler = ClassUtils.newInstance(fallbackClassName);
        //noinspection ConstantConditions
        mFallbackChangeHandler.restoreFromBundle(bundle.getBundle(KEY_FALLBACK_HANDLER_STATE));
    }

}
