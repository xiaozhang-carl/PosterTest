package com.postertest.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
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
    private Paint mEraserPaint;
    //路径
    private Path mEraserpath;
    //路径的点
    private float mEraserX;
    private float mEraserY;
    //临时画布
    private Canvas mEraserCanvas;
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
        mEraserPaint = new Paint();
        mEraserPaint.setAlpha(0);
        //设置画笔的痕迹是透明的，从而可以看到背景图片
        mEraserPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        mEraserPaint.setAntiAlias(true);

        mEraserPaint.setDither(true);
        mEraserPaint.setStyle(Paint.Style.STROKE);
        mEraserPaint.setStrokeJoin(Paint.Join.ROUND);
        mEraserPaint.setStrokeCap(Paint.Cap.ROUND);
        mEraserPaint.setStrokeWidth(36);

        mEraserpath = new Path();
        //创建临时透明背景图片
        bitmap_temp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        //创建临时画布
        mEraserCanvas = new Canvas();
        //将画到临时画布上的对象都画到临时图片上
        mEraserCanvas.setBitmap(bitmap_temp);
        //将要擦除的图片缩放至全屏的大小
        //将要擦除的图片画到临时图片上
        mEraserCanvas.drawBitmap(getScaledBitmap(bitmap), 0, 0, null);
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
        mEraserCanvas.drawPath(mEraserpath, mEraserPaint);
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
        mEraserpath.reset();
        mEraserpath.moveTo(f, g);
        mEraserX = f;
        mEraserY = g;
    }

    public void touchMove(float f, float g) {
        float dx = Math.abs(f - mEraserX);
        float dy = Math.abs(g - mEraserY);
        if (dx >= 4 || dy >= 4) {
            mEraserpath.quadTo(mEraserX, mEraserY, (f + mEraserX) / 2, (g + mEraserY) / 2);
            mEraserX = f;
            mEraserY = g;
        }
    }

    public void touchUp(float f, float g) {
        mEraserpath.lineTo(f, g);
    }

}
