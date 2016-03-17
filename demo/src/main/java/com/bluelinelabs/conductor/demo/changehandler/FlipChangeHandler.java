package com.bluelinelabs.conductor.demo.changehandler;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.support.annotation.NonNull;
import android.util.Property;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.bluelinelabs.conductor.changehandler.AnimatorChangeHandler;

public class FlipChangeHandler extends AnimatorChangeHandler {

    private static final long DEFAULT_ANIMATION_DURATION = 300;

    public enum FlipDirection {
        LEFT(-180, 180, View.ROTATION_Y),
        RIGHT(180, -180, View.ROTATION_Y),
        UP(-180, 180, View.ROTATION_X),
        DOWN(180, -180, View.ROTATION_X);

        final int inStartRotation;
        final int outEndRotation;
        final Property<View, Float> property;

        FlipDirection(int inStartRotation, int outEndRotation, Property<View, Float> property) {
            this.inStartRotation = inStartRotation;
            this.outEndRotation = outEndRotation;
            this.property = property;
        }
    }

    private final long mAnimationDuration;
    private final FlipDirection mFlipDirection;

    public FlipChangeHandler() {
        this(FlipDirection.RIGHT);
    }

    public FlipChangeHandler(FlipDirection flipDirection) {
        this(flipDirection, DEFAULT_ANIMATION_DURATION);
    }

    public FlipChangeHandler(long animationDuration) {
        this(FlipDirection.RIGHT, animationDuration);
    }

    public FlipChangeHandler(FlipDirection flipDirection, long animationDuration) {
        mFlipDirection = flipDirection;
        mAnimationDuration = animationDuration;
    }

    @Override
    protected Animator getAnimator(@NonNull ViewGroup container, View from, View to, boolean isPush, boolean toAddedToContainer) {
        AnimatorSet animatorSet = new AnimatorSet();

        if (to != null) {
            to.setAlpha(0);

            ObjectAnimator rotation = ObjectAnimator.ofFloat(to, mFlipDirection.property, mFlipDirection.inStartRotation, 0).setDuration(mAnimationDuration);
            rotation.setInterpolator(new AccelerateDecelerateInterpolator());
            animatorSet.play(rotation);

            Animator alpha = ObjectAnimator.ofFloat(to, View.ALPHA, 1).setDuration(mAnimationDuration / 2);
            alpha.setStartDelay(mAnimationDuration / 3);
            animatorSet.play(alpha);
        }

        if (from != null) {
            ObjectAnimator rotation = ObjectAnimator.ofFloat(from, mFlipDirection.property, 0, mFlipDirection.outEndRotation).setDuration(mAnimationDuration);
            rotation.setInterpolator(new AccelerateDecelerateInterpolator());
            animatorSet.play(rotation);

            Animator alpha = ObjectAnimator.ofFloat(from, View.ALPHA, 0).setDuration(mAnimationDuration / 2);
            alpha.setStartDelay(mAnimationDuration / 3);
            animatorSet.play(alpha);
        }

        return animatorSet;
    }

    @Override
    protected void resetFromView(@NonNull View from) {
        from.setAlpha(1);

        switch (mFlipDirection) {
            case LEFT:
            case RIGHT:
                from.setRotationY(0);
                break;
            case UP:
            case DOWN:
                from.setRotationX(0);
                break;
        }
    }
}
