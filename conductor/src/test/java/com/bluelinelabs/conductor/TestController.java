package com.bluelinelabs.conductor;

import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

public class TestController extends Controller {

    @IdRes public static final int VIEW_ID = 2342;

    public TestController() { }

    @NonNull
    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = new FrameLayout(inflater.getContext());
        view.setId(VIEW_ID);
        return view;
    }

}
