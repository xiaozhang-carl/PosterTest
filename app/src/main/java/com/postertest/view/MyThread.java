package com.postertest.view;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;

class MyThread extends Thread {

    final StickerView mStickerView;

    MyThread(StickerView stickerView) {
        this.mStickerView = stickerView;
    }

    public void run() {
        Log.i("sticker_view", Thread.currentThread().getName() + "---NEW");
        mStickerView.mPointFList = this.mStickerView.getAllPoints();
        this.mStickerView.getmPath();
        Path path = new Path(this.mStickerView.mPath);
        Matrix matrix = new Matrix();
        matrix.postTranslate((float) (-mStickerView.mRect.left), (float) (-mStickerView.mRect.top));
        matrix.postScale(1.0f / mStickerView.k, 1.0f / mStickerView.k);
        path.transform(matrix);
        Rect e = mStickerView.getPathRect();
        int i = -mStickerView.mRect.left;
        int i2 = -mStickerView.mRect.top;
        Rect rect = new Rect(e.left + i, e.top + i2, i + e.right, e.bottom + i2);
        float d = 1.0f / mStickerView.k;
        rect.left = (int) (((float) rect.left) * d);
        rect.top = (int) (((float) rect.top) * d);
        rect.right = (int) (((float) rect.right) * d);
        rect.bottom = (int) (d * ((float) rect.bottom));
        matrix.reset();
        matrix.postTranslate((float) (-rect.left), (float) (-rect.top));
        path.transform(matrix);
        Bitmap createBitmap = Bitmap.createBitmap(rect.width(), rect.height(), Config.ARGB_8888);
        RectF rectF = new RectF(0.0f, 0.0f, (float) rect.width(), (float) rect.height());
        Canvas canvas = new Canvas(createBitmap);
        canvas.clipPath(path);
        canvas.drawBitmap(mStickerView.mBitmap1, rect, rectF, null);
        mStickerView.mRectHandleClipListener.finishClip();
        if (mStickerView.mClipedListener != null) {
            mStickerView.mClipedListener.back(createBitmap);
        }
        mStickerView.a = true;
        mStickerView.mLineList.clear();
        mStickerView.mPath.reset();
        mStickerView.mPointFList.clear();
    }
}
