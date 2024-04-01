package com.tradetrack.cryptolist.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;

public class SlideInBottomAnimator extends DefaultItemAnimator {

    private final Interpolator interpolator = new AccelerateDecelerateInterpolator();

    @Override
    public boolean animateAdd(final RecyclerView.ViewHolder holder) {
        final View view = holder.itemView;
        view.setTranslationY(view.getHeight());
        view.setAlpha(0.0f);

        view.post(() -> {
            PropertyValuesHolder translationY = PropertyValuesHolder.ofFloat(View.TRANSLATION_Y, 0);
            PropertyValuesHolder alpha = PropertyValuesHolder.ofFloat(View.ALPHA, 1.0f);

            ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(view, translationY, alpha);
            animator.setDuration(getAddDuration());
            animator.setInterpolator(animator.getInterpolator());
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    dispatchAddStarting(holder);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    dispatchAddFinished(holder);
                }
            });
            animator.start();
        });

        return true;
    }
    
    // Override other necessary methods if needed
}
