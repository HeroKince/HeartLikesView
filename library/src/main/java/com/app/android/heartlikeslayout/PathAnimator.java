package com.app.android.heartlikeslayout;

import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;

import java.util.concurrent.atomic.AtomicInteger;

public class PathAnimator extends AbstractPathAnimator {
    private final AtomicInteger mCounter = new AtomicInteger(0);
    private Handler mHandler;

    public PathAnimator(Config config) {
        super(config);
        mHandler = new Handler(Looper.getMainLooper());
    }

    @Override
    public void start(final View child, final ViewGroup parent) {
        parent.addView(child, new ViewGroup.LayoutParams(mConfig.heartWidth, mConfig.heartHeight));
        FloatAnimation anim = new FloatAnimation(createPath(mCounter, parent, 2), randomRotation(), parent, child);
        anim.setDuration(mConfig.animDuration);
        anim.setInterpolator(new LinearInterpolator());
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationEnd(Animation animation) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        parent.removeView(child);
                    }
                });
                mCounter.decrementAndGet();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationStart(Animation animation) {
                mCounter.incrementAndGet();
            }
        });
        anim.setInterpolator(new LinearInterpolator());
        child.startAnimation(anim);
    }

}

