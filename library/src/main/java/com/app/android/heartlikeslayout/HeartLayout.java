package com.app.android.heartlikeslayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.app.android.hearthikesview.R;

/**
 * Created by kince on 16-8-3.
 */
public class HeartLayout extends RelativeLayout {

    private AbstractPathAnimator mAnimator;

    public HeartLayout(Context context) {
        super(context);
        init(null, 0);
    }

    public HeartLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public HeartLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr);
    }

    private void init(AttributeSet attrs, int defStyleAttr) {
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.HeartLayout, defStyleAttr, 0);
        mAnimator = new PathAnimator(AbstractPathAnimator.Config.fromTypeArray(a));
        a.recycle();
    }

    public AbstractPathAnimator getAnimator() {
        return mAnimator;
    }

    public void setAnimator(AbstractPathAnimator animator) {
        clearAnimation();
        mAnimator = animator;
    }

    public void clearAnimation() {
        for (int i = 0; i < getChildCount(); i++) {
            getChildAt(i).clearAnimation();
        }
        removeAllViews();
    }

    public void addHeart(int color) {
        HeartImageView heartImageView = new HeartImageView(getContext());
        heartImageView.setHeartColor(color);
        mAnimator.start(heartImageView, this);
    }

    public void release(){
        clearAnimation();

    }

}
