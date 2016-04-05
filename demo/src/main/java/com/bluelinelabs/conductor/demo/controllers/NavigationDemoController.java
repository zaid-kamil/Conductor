package com.bluelinelabs.conductor.demo.controllers;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler;
import com.bluelinelabs.conductor.demo.R;
import com.bluelinelabs.conductor.demo.controllers.base.BaseController;
import com.bluelinelabs.conductor.demo.util.BundleBuilder;
import com.bluelinelabs.conductor.demo.util.ColorUtil;

import butterknife.Bind;
import butterknife.OnClick;

public class NavigationDemoController extends BaseController {

    public static final String TAG_UP_TRANSACTION = "NavigationDemoController.up";

    private static final String KEY_INDEX = "NavigationDemoController.index";

    @Bind(R.id.tv_title) TextView mTvTitle;

    private int mIndex;

    public NavigationDemoController(int index) {
        this(new BundleBuilder(new Bundle())
                .putInt(KEY_INDEX, index)
                .build());
    }

    public NavigationDemoController(Bundle args) {
        super(args);
        mIndex = args.getInt(KEY_INDEX);
    }

    @NonNull
    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        return inflater.inflate(R.layout.controller_navigation_demo, container, false);
    }

    @Override
    protected void onViewBound(@NonNull View view) {
        super.onViewBound(view);

        view.setBackgroundColor(ColorUtil.getMaterialColor(getResources(), mIndex));
        mTvTitle.setText(getResources().getString(R.string.navigation_title, mIndex));
    }

    @Override
    protected String getTitle() {
        return "Navigation Demos";
    }

    @OnClick(R.id.btn_next) void onNextClicked() {
        getRouter().pushController(RouterTransaction.builder(new NavigationDemoController(mIndex + 1))
                .pushChangeHandler(new HorizontalChangeHandler())
                .popChangeHandler(new HorizontalChangeHandler())
                .build()
        );
    }

    @OnClick(R.id.btn_up) void onUpClicked() {
        getRouter().popToTag(TAG_UP_TRANSACTION);
    }

    @OnClick(R.id.btn_pop_to_root) void onPopToRootClicked() {
        getRouter().popToRoot();
    }
}
