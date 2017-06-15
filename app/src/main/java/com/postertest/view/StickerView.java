package com.postertest.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.Path.FillType;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;

import com.postertest.listener.OnClipedListener;
import com.postertest.listener.RectHandleClipListener;
import com.postertest.what.Line;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class StickerView extends View implements OnTouchListener {
    boolean a;//
    boolean isDrawing;//正在绘制中
    public int screenWidthPixels;//屏幕宽度，px
    public int screenHeightPixels;//屏幕高度，px
    public Bitmap mBitmap1;
    public Bitmap mBitmap2;
    public RectHandleClipListener mRectHandleClipListener;
    public OnClipedListener mClipedListener;
    public List<PointF> mPointFList;
    public Path mPath;
    public float k;
    public ArrayList<Line> mLineList;

    public Rect mRect;

    public float x;//手指点击的点下去的x
    public float y;//手指点击的点下去的y

    public int p;
    public boolean q;

    public StickerView(Context context) {
        this(context, null, 0);
    }

    public StickerView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public StickerView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        mBitmap1 = null;
        mPointFList = new ArrayList();
        mPath = new Path();
        mLineList = new ArrayList();
        this.p = 0;
        this.a = false;
        isDrawing = false;
        this.q = true;
        getScreenSize();
        setOnTouchListener(this);
        mPath.setFillType(FillType.WINDING);
        Log.e("sticker_view", Thread.currentThread().getName() + "---MAIN");

    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

//        canvas.drawBitmap(mBitmap2, getSrcRect(), getDstRect(), null);
        //调用下，不然获取不到剪裁的图片
        getDstRect();
        canvas.drawBitmap(mBitmap2, 0, 0, null);
        //绘制剪裁的
        if (isDrawing) {
            //绘制剪裁的线条
            Iterator it = mLineList.iterator();
            while (it.hasNext()) {
                Line line = (Line) it.next();
                canvas.drawLine(line.startX, line.startY, line.stopX, line.stopY, getClipPaint());
            }
            //绘制剪裁的线条缩略图
//            drawStickView(canvas);
            isDrawing = false;
        }

    }

    @Override
    protected void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {


        switch (motionEvent.getAction()) {

            case MotionEvent.ACTION_DOWN:

                this.x = motionEvent.getX();
                this.y = motionEvent.getY();

                break;

            case MotionEvent.ACTION_UP:

                if (mLineList.size() >= 2) {
                    this.mRectHandleClipListener.startClip();
                    creatThread();
                    break;
                }
                break;

            case MotionEvent.ACTION_MOVE:

                float x = motionEvent.getX();
                float y = motionEvent.getY();

                if (a(x - this.x, y - this.y)) {
                    isDrawing = true;
                    //把剪切线条连起来
//                    if (this.p % 2 == 0) {
                    Line line = new Line();
                    line.startX = this.x;
                    line.startY = this.y;
                    line.stopX = x;
                    line.stopY = y;
                    mLineList.add(line);
                    invalidate();
//                    }
                    this.p++;
                    this.x = x;
                    this.y = y;
                    break;
                }
                break;

        }
        return true;
    }


    public void drawStickView(Canvas canvas) {

        PointF pointF = new PointF(this.x, this.y);

        PointF pointF2 = new PointF(pointF.x - ((float) mRect.left), pointF.y - ((float) mRect.top));

        Rect rect = new Rect((int) (pointF2.x - 150.0f), (int) (pointF2.y - 150.0f), (int) (pointF2.x + 150.0f), (int) (pointF2.y + 150.0f));

        pointF2 = new PointF(30.0f + 150.0f, 30.0f + 150.0f);

        RectF rectF = new RectF(30.0f, 30.0f, (2.0f * 150.0f) + 30.0f, (2.0f * 150.0f) + 30.0f);

        canvas.save();

        Path path = new Path();

        path.addCircle(pointF2.x, pointF2.y, 150.0f + 5.0f, Direction.CCW);
        canvas.clipPath(path);
        Paint paint = new Paint();
        paint.setColor(-1);
        paint.setStrokeWidth(3.0f);
        paint.setAntiAlias(true);
        canvas.drawCircle(pointF2.x, pointF2.y, 150.0f + 5.0f, paint);
        path.reset();

        path.addCircle(pointF2.x, pointF2.y, 150.0f, Direction.CCW);
        canvas.clipPath(path);
        //轨迹图片的缩放控制
        canvas.scale(1.5f, 1.5f, pointF2.x, pointF2.y);
        canvas.drawBitmap(mBitmap2, rect, rectF, null);

        Paint paint2 = new Paint();
        paint2.setColor(-1);
        canvas.drawCircle(pointF2.x, pointF2.y, 5.0f, paint2);

        float f = this.x - pointF2.x;
        float f2 = this.y - pointF2.y;

        for (Line line : mLineList) {
            canvas.drawLine(line.startX - f, line.startY - f2, line.stopX - f, line.stopY - f2, getClipPaint());
        }

        canvas.restore();
    }

    public boolean a(float f, float f2) {
        return ((float) Math.sqrt((double) ((f * f) + (f2 * f2)))) > 5.0f;
    }

    public void creatThread() {
        new MyThread(this).start();
    }

    public List getAllPoints() {
        Log.i("sticker_view", Thread.currentThread().getName() + "---getAllPoints");
        List arrayList = new ArrayList();
        for (int i = 0; i < mLineList.size(); i++) {
            Line line = (Line) mLineList.get(i);
            PointF pointF = new PointF(line.startX, line.startY);
            PointF pointF2 = new PointF(line.stopX, line.stopY);
            arrayList.add(pointF);
            arrayList.add(pointF2);
        }
        return arrayList;
    }

    //绘制的画笔
    public Paint getClipPaint() {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.YELLOW);
        paint.setAntiAlias(false);
        paint.setStrokeWidth(3.0f);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        return paint;
    }

    public Rect getDstRect() {
        if (mBitmap1 == null) {
            return null;
        }
        int i;
        int i2;
        int i3;
        int width = mBitmap1.getWidth();
        int height = mBitmap1.getHeight();
        if (width < screenWidthPixels) {
            i = (screenWidthPixels - width) / 2;
            i2 = (screenHeightPixels - height) / 2;
            i3 = i + width;
            width += i2;
        } else {
            this.k = (((float) screenWidthPixels) * 1.0f) / ((float) width);
            i = 0;
            i2 = ((int) (((float) screenHeightPixels) - (((float) height) * this.k))) / 2;
            i3 = screenWidthPixels;
            width = ((int) (((float) height) * this.k)) + i2;
        }
        //这样剪裁的不准
//        mRect = new Rect(i, i2, i3, width);

        mRect = new Rect(i, 0, i3, width);
        return mRect;
    }

    public Rect getPathRect() {
        int i = (int) mPointFList.get(0).x;
        int i2 = (int) mPointFList.get(0).y;
        int i3 = (int) mPointFList.get(0).x;
        int i4 = (int) mPointFList.get(0).y;
        int i5 = i;
        i = i2;
        i2 = i3;
        i3 = i4;
        for (PointF pointF : mPointFList) {
            if (pointF.x < ((float) i5)) {
                i5 = (int) pointF.x;
            }
            if (pointF.y < ((float) i)) {
                i = (int) pointF.y;
            }
            if (pointF.x > ((float) i2)) {
                i2 = (int) pointF.x;
            }
            i3 = pointF.y > ((float) i3) ? (int) pointF.y : i3;
        }
        return new Rect(i5 - 10, i - 10, i2 + 10, i3 + 10);
    }

    public void getScreenSize() {
        WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        screenWidthPixels = displayMetrics.widthPixels;
        screenHeightPixels = displayMetrics.heightPixels;
    }

    //得到原图的矩形
    public Rect getSrcRect() {
        return mBitmap1 == null ? null : new Rect(0, 0, mBitmap1.getWidth(), mBitmap1.getHeight());
    }

    //得到绘制的路径
    public Path getmPath() {
        Log.i("sticker_view", Thread.currentThread() + "---getmPath");
        for (PointF pointF : mPointFList) {
            mPath.lineTo(pointF.x, pointF.y);
        }
        return mPath;
    }


    public void setBitmap(Bitmap bitmap) {

        mBitmap1 = bitmap;
        setBitmap2();
        invalidate();
    }


    public void setBitmap2() {
        //图片的宽小于View的宽
        if (mBitmap1.getWidth() < getWidth()) {
            mBitmap2 = Bitmap.createScaledBitmap(mBitmap1, mBitmap1.getWidth(), mBitmap1.getHeight(), false);
            return;
        }
        //图片的宽大于等于View的宽
        int width = mBitmap1.getWidth();
        int height = mBitmap1.getHeight();
        int i = screenWidthPixels;
        mBitmap2 = Bitmap.createScaledBitmap(mBitmap1, i, (int) (((((float) i) * 1.0f) / ((float) width)) * ((float) height)), false);
    }

    public void setOnClipedListener(OnClipedListener listener) {
        mClipedListener = listener;
    }

    public void setmOnHandleClipListener(RectHandleClipListener listener) {
        mRectHandleClipListener = listener;
    }
}
