package com.bluelinelabs.conductor.rxlifecycle;

import android.os.Bundle;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;

import com.bluelinelabs.conductor.Controller;
import com.trello.rxlifecycle.RxLifecycle;

import rx.Observable;
import rx.subjects.BehaviorSubject;

/**
 * A base {@link Controller} that can be used to expose lifecycle events using RxJava
 */
public abstract class RxController extends Controller implements ControllerLifecycleProvider {

    private final BehaviorSubject<ControllerEvent> mLifecycleSubject;

    public RxController() {
        this(null);
    }

    public RxController(Bundle args) {
        super(args);
        mLifecycleSubject = ControllerLifecycleSubjectHelper.create(this);
    }

    @Override
    @NonNull
    @CheckResult
    public final Observable<ControllerEvent> lifecycle() {
        return mLifecycleSubject.asObservable();
    }

    @Override
    @NonNull
    @CheckResult
    public final <T> Observable.Transformer<T, T> bindUntilEvent(@NonNull ControllerEvent event) {
        return RxLifecycle.bindUntilEvent(mLifecycleSubject, event);
    }

    @Override
    @NonNull
    @CheckResult
    public final <T> Observable.Transformer<T, T> bindToLifecycle() {
        return RxControllerLifecycle.bindController(mLifecycleSubject);
    }

}