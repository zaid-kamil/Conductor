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

    public <T extends Controller & TargetTitleEntryControllerListener> TargetTitleEntryController(T targetController) {
        super.setTargetController(targetController);
    }

    public TargetTitleEntryController() { }

    @Override
    public void setTargetController(Controller target) {
        throw new RuntimeException(getClass().getSimpleName() + "s can only have their target set through the constructor.");
    }

    @NonNull
    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        return inflater.inflate(R.layout.controller_target_title_entry, container, false);
    }

    @OnClick(R.id.btn_use_title) void optionPicked() {
        Controller targetController = getTargetController();
        if (targetController != null) {
            ((TargetTitleEntryControllerListener)targetController).onTitlePicked(mEditText.getText().toString());
            getRouter().popController(this);
        }
    }
}
