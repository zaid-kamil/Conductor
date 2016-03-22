package com.bluelinelabs.conductor.demo.controllers;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bluelinelabs.conductor.ChildControllerTransaction;
import com.bluelinelabs.conductor.Controller;
import com.bluelinelabs.conductor.ControllerChangeHandler;
import com.bluelinelabs.conductor.ControllerTransaction.ControllerChangeType;
import com.bluelinelabs.conductor.changehandler.FadeChangeHandler;
import com.bluelinelabs.conductor.demo.R;
import com.bluelinelabs.conductor.demo.controllers.base.RefWatchingController;
import com.bluelinelabs.conductor.demo.util.ColorUtil;

import java.util.List;

public class ParentController extends RefWatchingController {

    private static final int NUMBER_OF_CHILDREN = 5;
    private boolean mFinishing;

    @NonNull
    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        return inflater.inflate(R.layout.controller_parent, container, false);
    }

    @Override
    protected void onChangeEnded(@NonNull ControllerChangeHandler changeHandler, @NonNull ControllerChangeType changeType) {
        super.onChangeEnded(changeHandler, changeType);

        if (changeType == ControllerChangeType.PUSH_ENTER) {
            addChild(0);
        }
    }

    private void addChild(final int index) {
        String tag = "child_tag" + index;

        if (getChildController(tag) == null) {
            int frameId = getResources().getIdentifier("child_content_" + (index + 1), "id", getActivity().getPackageName());

            ChildController childController = new ChildController("Child Controller #" + index, ColorUtil.getMaterialColor(getResources(), index), false);
            addChildController(ChildControllerTransaction.builder(childController, frameId)
                    .pushChangeHandler(new FadeChangeHandler())
                    .popChangeHandler(new FadeChangeHandler())
                    .tag(tag)
                    .build());

            childController.addLifecycleListener(new LifecycleListener() {
                @Override
                public void onChangeEnd(@NonNull Controller controller, @NonNull ControllerChangeHandler changeHandler, @NonNull ControllerChangeType changeType) {
                    if (changeType == ControllerChangeType.PUSH_ENTER && index < NUMBER_OF_CHILDREN - 1) {
                        addChild(index + 1);
                    } else if (changeType == ControllerChangeType.POP_EXIT) {
                        if (index > 0) {
                            removeChild(index - 1);
                        } else {
                            getRouter().popController(ParentController.this);
                        }
                    }
                }
            });
        }
    }

    private void removeChild(int index) {
        List<Controller> childControllers = getChildControllers();
        removeChildController(childControllers.get(index));
    }

    @Override
    public boolean handleBack() {
        if (getChildControllers().size() == NUMBER_OF_CHILDREN && !mFinishing) {
            mFinishing = true;
            removeChild(getChildControllers().size() - 1);
        }
        return true;
    }
}
