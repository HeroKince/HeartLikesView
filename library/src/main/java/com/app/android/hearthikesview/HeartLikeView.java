package com.app.android.hearthikesview;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by kince on 16-8-2.
 */
public class HeartLikeView extends View {

    private Paint mPaint;

    private Bitmap mBitmap;// 当前显示的图像

    private List<PathObj> list = new ArrayList<>();// 路径

    private Random mRandom = new Random();
    private boolean mFlag;//
    private long mStartTime = 0;

    private Context mContext;

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(android.os.Message msg) {
            int what = msg.what;
            switch (what) {
                case 0:
                    list.add(new PathObj(mBitmap));
                    mFlag = true;
                    this.post(mRunnable);
                    break;

                default:
                    break;
            }
        }
    };

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            invalidate();
        }
    };

    public HeartLikeView(Context context) {
        super(context);
        init(context);
    }

    public HeartLikeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public HeartLikeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public HeartLikeView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mFlag) {
            release();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (list == null) {
            return;
        }
        if (mFlag) {
            drawView(canvas);
            this.post(mRunnable);
        }
    }

    private void init(Context context) {
        mContext = context;

        mPaint = new Paint();
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));

        mBitmap = createHeart();
    }

    public boolean isRunning() {
        return mFlag;
    }

    /**
     * 绘图图像
     */
    public void drawView(Canvas canvas) {
        try {
            drawPath(canvas);
        } catch (Exception e) {
        }
    }

    private int randomColor() {
        return Color.rgb(mRandom.nextInt(255), mRandom.nextInt(255), mRandom.nextInt(255));
    }

    public Bitmap createHeart() {
        Bitmap sHeart = BitmapFactory.decodeResource(getResources(), R.drawable.heart);
        Bitmap sHeartBorder = BitmapFactory.decodeResource(getResources(), R.drawable.heart_border);

        Bitmap heart = sHeart;
        Bitmap heartBorder = sHeartBorder;
        Bitmap bm = createBitmapSafely(heartBorder.getWidth(), heartBorder.getHeight());
        if (bm == null) {
            return null;
        }
        Canvas canvas = new Canvas();
        canvas.setBitmap(bm);
        Paint p = new Paint();
        canvas.drawBitmap(heartBorder, 0, 0, p);
        p.setColorFilter(new PorterDuffColorFilter(randomColor(), PorterDuff.Mode.SRC_ATOP));
        float dx = (heartBorder.getWidth() - heart.getWidth()) / 2f;
        float dy = (heartBorder.getHeight() - heart.getHeight()) / 2f;
        canvas.drawBitmap(heart, dx, dy, p);
        p.setColorFilter(null);
        canvas.setBitmap(null);
        return bm;
    }

    /**
     * 绘制贝赛尔曲线
     *
     * @param canvas 主画布
     */
    public void drawPath(Canvas canvas) {
        if (list.size() <= 0) {
            mFlag = false;
        }
        for (int i = 0; i < list.size(); i++) {
            try {
                PathObj obj = list.get(i);
                if (obj.getAlpha() <= 0) {
                    list.remove(i);
                    i--;
                    continue;
                }
                Rect src = obj.getSrcRect();
                Rect dst = obj.getDstRect();
                if (dst == null) {
                    list.remove(i);
                    i--;
                    continue;
                }
                canvas.drawBitmap(obj.getBitmap(), src, dst, obj.getPaint());
            } catch (Exception e) {
                list.remove(i);
                i--;
            }
        }
    }

    /**
     * 点赞
     */
    public void showHeartView() {
        if (System.currentTimeMillis() - mStartTime > 50) {
            // 禁止同时显示多个赞
            mStartTime = System.currentTimeMillis();
            list.add(new PathObj(mBitmap));
            mFlag = true;
            this.post(mRunnable);
        }
    }

    public void stop() {
        this.removeCallbacks(mRunnable);
        mFlag = false;
    }

    public void release() {
        stop();
        mContext = null;
        if (mBitmap != null && !mBitmap.isRecycled()) {
            mBitmap.recycle();
        }
    }

    public void add(int count) {
        for (int i = 0; i < count; i++) {
            Message msg = Message.obtain();
            msg.what = 0;
            mHandler.sendMessageDelayed(msg, 80 * i);
        }
    }

    private static Bitmap createBitmapSafely(int width, int height) {
        try {
            return Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444);
        } catch (OutOfMemoryError error) {
            error.printStackTrace();
        }
        return null;
    }

    /**
     * 随机路径
     */
    public class PathObj {
        private Paint paint;

        public Path path;
        public PathMeasure pathMeasure;

        public float speed;// 速度
        private final float acceleratedSpeed = 0.05f;// 加速度，目前恒定
        private final float speedMax = 6;

        public int curPos;
        public int length;// 路径总长度
        private float[] p = new float[2];// 坐标点，很重要

        private int time;// 执行的次数
        private int scaleTime = 20;// 放大

        private Rect src;
        private Rect dst;

        private Bitmap bitmap;

        private int bitmapWidth;// 图片宽度
        private int bitmapHeight;// 图片高度

        private int bitmapWidthDst;// 最终放大宽度
        private int bitmapHeightDst;// 最终放大高度

        private int alpha = 255;// 透明度范围 0-255
        private final int alphaOffset = 5;// 透明度偏移量

        /**
         * 初始化路径
         *
         * @param bitmap 绘制的图片
         */
        public PathObj(Bitmap bitmap) {
            this.bitmap = bitmap;
            this.bitmapWidth = bitmap.getWidth();
            this.bitmapHeight = bitmap.getHeight();

            bitmapWidthDst = bitmapWidth;// + bitmapWidth/4;
            bitmapHeightDst = bitmapHeight;// + bitmapHeight/4;

            src = new Rect(0, 0, bitmapWidth, bitmapHeight);
            dst = new Rect(0, 0, bitmapWidthDst / 2, bitmapHeightDst / 2);

            paint = new Paint();
            paint.setAntiAlias(true);

            path = new Path();
            pathMeasure = new PathMeasure();

            int factor = 2;
            int initX = (int) mContext.getResources().getDimension(R.dimen.heart_anim_init_x);
            int initY = (int) mContext.getResources().getDimension(R.dimen.heart_anim_init_y);
            int xRand = (int) mContext.getResources().getDimension(R.dimen.heart_anim_bezier_x_rand);
            int animLengthRand = (int) mContext.getResources().getDimension(R.dimen.heart_anim_length_rand);
            int bezierFactor = 6;
            int animLength = (int) mContext.getResources().getDimension(R.dimen.heart_anim_length);
            int xPointFactor = (int) mContext.getResources().getDimension(R.dimen.heart_anim_x_point_factor);

            int x = mRandom.nextInt(xRand);
            int x2 = mRandom.nextInt(xRand);

            int y = getHeight() - initY;
            int y2 = animLength * factor + mRandom.nextInt(animLengthRand);

            factor = y2 / bezierFactor;

            x = xPointFactor + x;
            x2 = xPointFactor + x2;

            int y3 = y - y2;
            y2 = y - y2 / 2;

            path.moveTo(initX, y);
            path.cubicTo(initX, y - factor, x, y2 + factor, x, y2);
            path.moveTo(x, y2);
            path.cubicTo(x, y2 - factor, x2, y3 + factor, x2, y3);

            pathMeasure.setPath(path, false);
            length = (int) pathMeasure.getLength();
            speed = mRandom.nextInt(1) + 1f;
        }

        /**
         * 获取bitmap，用来canvas
         *
         * @return
         */
        public Bitmap getBitmap() {
            return bitmap;
        }

        /**
         * 画笔
         *
         * @return
         */
        public Paint getPaint() {
            return paint;
        }

        /**
         * 获取起始Rect
         *
         * @return
         */
        public Rect getSrcRect() {
            return src;
        }

        /**
         * 获取目标Rect，根据当前点坐标计算Rect，已经加入放大/淡出动画
         *
         * @return
         */
        public Rect getDstRect() {
            curPos += speed;
            if (time < scaleTime) {
                speed = 3;
            } else {
                if (speed <= speedMax) {
                    speed += acceleratedSpeed;
                }
            }

            if (curPos > length) {
                curPos = length;
                return null;
            }

            pathMeasure.getPosTan(curPos, p, null);

            if (time < scaleTime) {
                // 放大动画
                float s = (float) time / scaleTime;
                dst.left = (int) (p[0] - bitmapWidthDst / 4 * s);
                dst.right = (int) (p[0] + bitmapWidthDst / 4 * s);
                dst.top = (int) ((p[1] - bitmapHeightDst / 2 * s));
                dst.bottom = (int) (p[1]);
            } else {
                dst.left = (int) (p[0] - bitmapWidthDst / 4);
                dst.right = (int) (p[0] + bitmapWidthDst / 4);
                dst.top = (int) (p[1] - bitmapHeightDst / 2);
                dst.bottom = (int) (p[1]);
            }
            time++;
            alpha();
            return dst;
        }

        private int alpha() {
            int offset = length - curPos;
            if (offset < length / 1.5) {
                alpha -= alphaOffset;
                if (alpha < 0) {
                    alpha = 0;
                }
                paint.setAlpha(alpha);
            } else if (offset <= 10) {
                alpha = 0;
                paint.setAlpha(alpha);
            }
            return 0;
        }

        public int getAlpha() {
            return alpha;
        }

        /**
         * 获取当前执行次数
         *
         * @return
         */
        public int getTime() {
            return time;
        }
    }

}
