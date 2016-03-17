package com.bluelinelabs.conductor.rxlifecycle;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;

import rx.Observable;

/**
 * Interface used for RxController. Can also be used if writing your own Controller component without subclassing RxController.
 */
public interface ControllerLifecycleProvider {

    /**
     * @return An observable that will have all {@link com.bluelinelabs.conductor.Controller} lifecycle events
     */
    @NonNull
    @CheckResult
    Observable<ControllerEvent> lifecycle();

    /**
     * Will bind the source until a specific {@link ControllerEvent} occurs.
     *
     * @param event The {@link ControllerEvent} that should cause onComplete to be called
     * @return A {@link rx.Observable.Transformer} that will call onComplete when the event occurs.
     */
    @NonNull
    @CheckResult
    <T> Observable.Transformer<T, T> bindUntilEvent(@NonNull ControllerEvent event);

    /**
     * Will bind the source until the next reasonable {@link ControllerEvent} occurs.
     * @return A {@link rx.Observable.Transformer} that will call onComplete when the event occurs.
     */
    @NonNull
    @CheckResult
    <T> Observable.Transformer<T, T> bindToLifecycle();

}
