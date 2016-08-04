package com.app.android.heartlikeslayout;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;

import com.app.android.hearthikesview.R;

import java.util.HashMap;

/**
 * Created by kince on 16-8-3.
 */
public class BitmapManager {

    private static final Paint sPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
    private int mHeartResId = R.drawable.heart;
    private int mHeartBorderResId = R.drawable.heart_border;
    private static Bitmap sHeart;
    private static Bitmap sHeartBorder;
    private static final Canvas sCanvas = new Canvas();
    private HashMap<Integer, Bitmap> mBitmapHashMap = new HashMap<Integer, Bitmap>();

    private static BitmapManager instance = null;

    private BitmapManager() {
    }

    public static BitmapManager getInstance() {
        synchronized (BitmapManager.class) {
            if (instance == null) {
                instance = new BitmapManager();
            }
        }
        return instance;
    }

    public Bitmap createHeart(Context context, int color) {
        Bitmap tempBitmap = mBitmapHashMap.get(color);
        if (tempBitmap != null) {
            return tempBitmap;
        }
        if (sHeart == null) {
            sHeart = BitmapFactory.decodeResource(context.getResources(), mHeartResId);
        }
        if (sHeartBorder == null) {
            sHeartBorder = BitmapFactory.decodeResource(context.getResources(), mHeartBorderResId);
        }
        Bitmap heart = sHeart;
        Bitmap heartBorder = sHeartBorder;
        Bitmap bm = createBitmapSafely(heartBorder.getWidth(), heartBorder.getHeight());
        if (bm == null) {
            return null;
        }
        Canvas canvas = sCanvas;
        canvas.setBitmap(bm);
        Paint p = sPaint;
        canvas.drawBitmap(heartBorder, 0, 0, p);
        p.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_ATOP));
        float dx = (heartBorder.getWidth() - heart.getWidth()) / 2f;
        float dy = (heartBorder.getHeight() - heart.getHeight()) / 2f;
        canvas.drawBitmap(heart, dx, dy, p);
        p.setColorFilter(null);
        canvas.setBitmap(null);
        mBitmapHashMap.put(color, bm);
        return bm;
    }

    private static Bitmap createBitmapSafely(int width, int height) {
        try {
            return Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError error) {
            error.printStackTrace();
        }
        return null;
    }

    public void release(){

    }

}
