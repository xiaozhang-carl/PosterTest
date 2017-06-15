package com.postertest.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

/**
 * Created by zhanghongqiang on 2017/6/15.
 * http://blog.sina.com.cn/s/blog_9968251c01014u6z.html
 * ToDo:
 */

public class EraserImage extends View {

    private Context context;
    //画笔
    private Paint paint;
    //路径
    private Path path;
    //路径的点
    private float mX;
    private float mY;
    //临时画布
    private Canvas can;
    //临时画布中的背景图片
    private Bitmap bitmap_temp;

    public int screenWidthPixels;//屏幕宽度，px
    public int screenHeightPixels;//屏幕高度，px


    public EraserImage(Context context) {
        super(context);
    }

    public EraserImage(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public EraserImage(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public EraserImage(Context context, Bitmap bitmap, int width, int height) {
        super(context);
        init(context, bitmap, width, height);
    }


    private void init(Context context, Bitmap bitmap, int width, int height) {

        this.context = context;
        getScreenSize();
        paint = new Paint();
        paint.setAlpha(0);
        //设置画笔的痕迹是透明的，从而可以看到背景图片
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        paint.setAntiAlias(true);

        paint.setDither(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(36);

        path = new Path();
        //创建临时透明背景图片
        bitmap_temp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        //创建临时画布
        can = new Canvas();
        //将画到临时画布上的对象都画到临时图片上
        can.setBitmap(bitmap_temp);
        //将要擦除的图片缩放至全屏的大小
        //将要擦除的图片画到临时图片上
        can.drawBitmap(getScaledBitmap(bitmap), 0, 0, null);
    }

    //得到原图的矩形
    public Rect getSrcRect(Bitmap bitmap) {
        return bitmap == null ? null : new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
    }


    public Rect getDstRect(Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }
        int i;
        int i2;
        int i3;
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        if (width < screenWidthPixels) {
            i = (screenWidthPixels - width) / 2;
            i2 = (screenHeightPixels - height) / 2;
            i3 = i + width;
            width += i2;
        } else {
            float k = (((float) screenWidthPixels) * 1.0f) / ((float) width);
            i = 0;
            i2 = ((int) (((float) screenHeightPixels) - (((float) height) * k))) / 2;
            i3 = screenWidthPixels;
            width = ((int) (((float) height) * k)) + i2;
        }
        Rect mRect = new Rect(i, i2, i3, width);
        return mRect;
    }

    public void getScreenSize() {
        WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        screenWidthPixels = displayMetrics.widthPixels;
        screenHeightPixels = displayMetrics.heightPixels;
    }

    //缩放bitmap
    public Bitmap getScaledBitmap(Bitmap bitmap) {
        //图片的宽小于View的宽
        if (bitmap.getWidth() < getWidth()) {
            return Bitmap.createScaledBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight(), false);
        }
        //图片的宽大于等于View的宽
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int i = screenWidthPixels;
        return Bitmap.createScaledBitmap(bitmap, i, (int) (((((float) i) * 1.0f) / ((float) width)) * ((float) height)), false);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        //将画布上的临时图片显示在屏幕上
        canvas.drawBitmap(bitmap_temp, 0, 0, null);
        //将path画在临时图片上
        can.drawPath(path, paint);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // TODO Auto-generated method stub
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touchDown(event.getX(), event.getY());
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                touchMove(event.getX(), event.getY());
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                touchUp(event.getX(), event.getY());
                invalidate();
                break;
        }
        return true;
    }


    public void touchDown(float f, float g) {
        path.reset();
        path.moveTo(f, g);
        mX = f;
        mY = g;
    }

    public void touchMove(float f, float g) {
        float dx = Math.abs(f - mX);
        float dy = Math.abs(g - mY);
        if (dx >= 4 || dy >= 4) {
            path.quadTo(mX, mY, (f + mX) / 2, (g + mY) / 2);
            mX = f;
            mY = g;
        }
    }

    public void touchUp(float f, float g) {
        path.lineTo(f, g);
    }

}
