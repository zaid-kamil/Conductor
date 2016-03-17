package com.bluelinelabs.conductor.util;

import android.annotation.TargetApi;
import android.os.Build.VERSION_CODES;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;

@TargetApi(VERSION_CODES.LOLLIPOP)
public class ViewUtils {

    public static View findViewWithTransitionName(@NonNull String name, @NonNull View view) {
        if (name.equals(view.getTransitionName())) {
            return view;
        }

        if (view instanceof ViewGroup) {
            View namedView = findViewWithTransitionNameInGroup(name, (ViewGroup)view);

            if (namedView != null) {
                return namedView;
            }
        }

        return null;
    }

    private static View findViewWithTransitionNameInGroup(@NonNull String name, @NonNull ViewGroup viewGroup) {
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View namedView = findViewWithTransitionName(name, viewGroup.getChildAt(i));
            if (namedView != null) {
                return namedView;
            }
        }
        return null;
    }

}
