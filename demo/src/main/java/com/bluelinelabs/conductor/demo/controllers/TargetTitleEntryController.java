package com.bluelinelabs.conductor.demo.controllers;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.bluelinelabs.conductor.Controller;
import com.bluelinelabs.conductor.demo.R;
import com.bluelinelabs.conductor.demo.controllers.base.RefWatchingController;

import butterknife.Bind;
import butterknife.OnClick;

public class TargetTitleEntryController extends RefWatchingController {

    public interface TargetTitleEntryControllerListener {
        void onTitlePicked(String option);
    }

    @Bind(R.id.edit_text) EditText mEditText;

    public TargetTitleEntryController() { }

    public TargetTitleEntryController(Controller targetController) {
        setTargetController(targetController);
    }

    @NonNull
    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        return inflater.inflate(R.layout.controller_target_title_entry, container, false);
    }

    @Override
    public void onTargetControllerSet(Controller target) {
        super.onTargetControllerSet(target);

        if (!(target instanceof TargetTitleEntryControllerListener)) {
            throw new RuntimeException(getClass().getSimpleName() + " target Controllers must implement the " + TargetTitleEntryControllerListener.class.getSimpleName() + " interface.");
        }
    }

    @OnClick(R.id.btn_use_title) void optionPicked() {
        Controller targetController = getTargetController();
        if (targetController != null) {
            ((TargetTitleEntryControllerListener)targetController).onTitlePicked(mEditText.getText().toString());
            getRouter().popController(this);
        }
    }
}
