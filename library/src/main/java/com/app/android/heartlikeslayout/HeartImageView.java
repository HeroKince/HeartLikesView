package com.app.android.heartlikeslayout;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by kince on 16-8-3.
 */
public class HeartImageView extends ImageView {

    private Context mContext;

    public HeartImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public HeartImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public HeartImageView(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context){
        mContext = context;
    }

    public void setHeartColor(int color) {
        Bitmap heart = BitmapManager.getInstance().createHeart(mContext,color);
        setImageDrawable(new BitmapDrawable(getResources(), heart));
    }

}
