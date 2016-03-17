package com.bluelinelabs.conductor.demo.controllers;

import android.graphics.PorterDuff.Mode;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bluelinelabs.conductor.ChildControllerTransaction;
import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.changehandler.FadeChangeHandler;
import com.bluelinelabs.conductor.demo.R;
import com.bluelinelabs.conductor.demo.controllers.base.RefWatchingController;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HomeController extends RefWatchingController {

    public enum HomeDemoModel {
        NAVIGATION("Navigation Demos", R.color.red_300),
        TRANSITIONS("Transition Demos", R.color.blue_grey_300),
        OVERLAY("Overlay Controller", R.color.purple_300),
        CHILD_CONTROLLERS("Child Controllers", R.color.orange_300),
        VIEW_PAGER("ViewPager", R.color.green_300),
        TARGET_CONTROLLER("Target Controller", R.color.pink_300),
        DRAG_DISMISS("Drag Dismiss", R.color.lime_300),
        RX_LIFECYCLE("Rx Lifecycle", R.color.teal_300);

        String title;
        @ColorRes int color;

        HomeDemoModel(String title, @ColorRes int color) {
            this.title = title;
            this.color = color;
        }
    }

    @Bind(R.id.recycler_view) RecyclerView mRecyclerView;

    @NonNull
    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        return inflater.inflate(R.layout.controller_home, container, false);
    }

    @Override
    protected void onBindView(@NonNull View view) {
        super.onBindView(view);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        mRecyclerView.setAdapter(new HomeAdapter(LayoutInflater.from(view.getContext()), HomeDemoModel.values()));
    }

    void onModelRowClick(HomeDemoModel model) {
        switch (model) {
            case NAVIGATION:
                getRouter().pushController(RouterTransaction.builder(new NavigationDemoController(0))
                        .pushChangeHandler(new FadeChangeHandler())
                        .popChangeHandler(new FadeChangeHandler())
                        .tag(NavigationDemoController.TAG_UP_TRANSACTION)
                        .build());
                break;
            case TRANSITIONS:
                getRouter().pushController(TransitionDemoController.getRouterTransaction(0, this));
                break;
            case TARGET_CONTROLLER:
                getRouter().pushController(RouterTransaction.builder(new TargetDisplayController())
                        .pushChangeHandler(new FadeChangeHandler())
                        .popChangeHandler(new FadeChangeHandler())
                        .build());
                break;
            case VIEW_PAGER:
                getRouter().pushController(RouterTransaction.builder(new PagerController())
                        .pushChangeHandler(new FadeChangeHandler())
                        .popChangeHandler(new FadeChangeHandler())
                        .build());
                break;
            case CHILD_CONTROLLERS:
                getRouter().pushController(RouterTransaction.builder(new ParentController())
                        .pushChangeHandler(new FadeChangeHandler())
                        .popChangeHandler(new FadeChangeHandler())
                        .build());
                break;
            case OVERLAY:
                addChildController(ChildControllerTransaction.builder(new OverlayController(), R.id.home_root)
                        .pushChangeHandler(new FadeChangeHandler())
                        .popChangeHandler(new FadeChangeHandler())
                        .addToLocalBackstack(true)
                        .build());
                break;
            case DRAG_DISMISS:
                getRouter().pushController(RouterTransaction.builder(new DragDismissController())
                        .pushChangeHandler(new FadeChangeHandler(false))
                        .popChangeHandler(new FadeChangeHandler())
                        .build());
                break;
            case RX_LIFECYCLE:
                getRouter().pushController(RouterTransaction.builder(new RxLifecycleController())
                        .pushChangeHandler(new FadeChangeHandler())
                        .popChangeHandler(new FadeChangeHandler())
                        .build());
                break;
        }
    }

    class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.ViewHolder> {

        private final LayoutInflater mInflater;
        private final HomeDemoModel[] mItems;

        public HomeAdapter(LayoutInflater inflater, HomeDemoModel[] items) {
            mInflater = inflater;
            mItems = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(mInflater.inflate(R.layout.row_home, parent, false));
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.bind(mItems[position]);
        }

        @Override
        public int getItemCount() {
            return mItems.length;
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            @Bind(R.id.tv_title) TextView mTvTitle;
            @Bind(R.id.img_dot) ImageView mImgDot;
            private HomeDemoModel mModel;

            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }

            void bind(HomeDemoModel item) {
                mModel = item;
                mTvTitle.setText(item.title);
                mImgDot.getDrawable().setColorFilter(ContextCompat.getColor(getActivity(), item.color), Mode.SRC_ATOP);
            }

            @OnClick(R.id.row_root)
            void onRowClick() {
                onModelRowClick(mModel);
            }

        }
    }

}
