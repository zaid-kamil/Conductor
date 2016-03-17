package com.bluelinelabs.conductor.demo.controllers;

import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bluelinelabs.conductor.Controller;
import com.bluelinelabs.conductor.demo.R;
import com.bluelinelabs.conductor.demo.controllers.base.RefWatchingController;
import com.bluelinelabs.conductor.support.ControllerPagerAdapter;

import butterknife.Bind;

public class PagerController extends RefWatchingController {

    @Bind(R.id.view_pager) ViewPager mViewPager;
    private ControllerPagerAdapter mPagerAdapter;

    public PagerController() {
        mPagerAdapter = new ControllerPagerAdapter(this) {
            @Override
            public Controller getItem(int position) {
                switch (position) {
                    case 0:
                        return new ChildController("Child #1 (Swipe to see more)", R.color.cyan_300, true);
                    case 1:
                        return new ChildController("Child #2 (Swipe to see more)", R.color.deep_purple_300, true);
                    case 2:
                        return new ChildController("Child #3 (Swipe to see more)", R.color.lime_300, true);
                    default:
                        throw new RuntimeException("Invalid item position: " + position);
                }
            }

            @Override
            public int getCount() {
                return 3;
            }
        };
    }

    @Override
    protected void onBindView(@NonNull View view) {
        super.onBindView(view);
        mViewPager.setAdapter(mPagerAdapter);
    }

    @NonNull
    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        return inflater.inflate(R.layout.controller_pager, container, false);
    }

}
