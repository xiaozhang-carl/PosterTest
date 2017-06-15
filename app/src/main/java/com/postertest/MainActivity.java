package com.postertest;

import android.app.Activity;
import android.app.ProgressDialog;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.postertest.databinding.ActivityMainBinding;
import com.postertest.listener.OnClipedListener;
import com.postertest.listener.RectHandleClipListener;
import com.postertest.view.EraserImage;

public class MainActivity extends Activity implements OnClipedListener, RectHandleClipListener {

    private ActivityMainBinding mBinding;

    private ProgressDialog mProgressDialog = null;

    private final int CLIP_FINISH = 2;
    private final int CLIP_BEGIN = 1;
    private final int CLIP_BITMAP = 3;

    //剪裁后的图片
    private Bitmap mBitmap;
    //橡皮擦的图片
    private EraserImage mEraserImage;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case CLIP_BEGIN:
                    mProgressDialog.show();
                    break;
                case CLIP_FINISH:
                    mProgressDialog.dismiss();
                    break;
                case CLIP_BITMAP:
                    //
                    mBitmap = (Bitmap) msg.obj;
                    mBinding.sv.setBitmap(mBitmap);
                    break;
            }
        }
    };


    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getWindow().setFlags(1024, 1024);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        mBinding.setPresenter(new Presenter());
        initView();
        initData();
    }


    private void initView() {
        mBinding.sv.setOnClipedListener(this);
        mBinding.sv.setmOnHandleClipListener(this);
    }

    private void initData() {
        mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.girl2);
        mBinding.sv.setBitmap(mBitmap);
        initDialog();
    }

    private void initDialog() {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setProgressStyle(0);
        mProgressDialog.setMessage("正在处理, 请稍后...");

    }

    @Override
    public void back(Bitmap bitmap) {
        Message message = new Message();
        message.what = CLIP_BITMAP;
        message.obj = bitmap;
        mHandler.sendMessage(message);

    }

    @Override
    public void startClip() {
        //显示dialog
        mHandler.sendEmptyMessage(CLIP_BEGIN);
    }

    @Override
    public void finishClip() {
        //关闭dialog
        mHandler.sendEmptyMessage(CLIP_FINISH);
    }

    //xml里的点击
    public class Presenter {

        //点击叉叉
        public void onClickTooBarBack(View v) {
            finish();
        }

        //保存bitmap
        public void onClickSave(View v) {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.girl2);
            mBinding.sv.setBitmap(bitmap);
        }

        //剪裁bitmap
        public void onClickMatting(View v) {
            mBinding.tvMatting.setTextColor(Color.RED);
            mBinding.tvMattingEraser.setTextColor(Color.WHITE);
            mBinding.ivMatting.setImageResource(R.drawable.ic_matting);
            mBinding.ivMattingEraser.setImageResource(R.drawable.ic_mosaic_eraser_gray);

            if (mEraserImage != null) {
                //获取擦除后的bitmap
                mBitmap = convertViewToBitmap(mEraserImage);
                mBinding.sv.setBitmap(mBitmap);
                //剪裁视图设置为可见
                mBinding.sv.setVisibility(View.VISIBLE);
                //移除橡皮擦的布局，置空
                mBinding.imageLayout.removeView(mEraserImage);
                mEraserImage = null;
            }

        }

        //擦除多余的bitmap
        public void onClickEraser(View v) {
            mBinding.tvMatting.setTextColor(Color.WHITE);
            mBinding.tvMattingEraser.setTextColor(Color.RED);
            mBinding.ivMatting.setImageResource(R.drawable.ic_matting);
            mBinding.ivMattingEraser.setImageResource(R.drawable.ic_mosaic_eraser_red);
            //绘制
            if (mBitmap == null) {
                return;
            }
            //记得每次新建橡皮擦的布局，添加进来
            if (mEraserImage == null) {
                mEraserImage = new EraserImage(MainActivity.this, mBitmap, mBinding.imageLayout.getWidth(), mBinding.imageLayout.getHeight());
                FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                layoutParams.topMargin = 100;
                layoutParams.bottomMargin = 100;
//                mEraserImage.setLayoutParams(layoutParams);
                mBinding.imageLayout.addView(mEraserImage);

            }
            //剪裁视图设置为不可见
            mBinding.sv.setVisibility(View.INVISIBLE);

        }
    }

    //获取橡皮擦的bitmap
    public Bitmap convertViewToBitmap(View view) {
        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, mBinding.imageLayout.getWidth(), mBinding.imageLayout.getHeight());
        view.buildDrawingCache();
        //一定要创建新的bitmap，不然
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
        return bitmap;
    }

    //羽化
    public static Bitmap feather(Bitmap bitmap) {
        return null;

    }

}