package com.lens.chatmodel.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.view.Display;
import android.view.WindowManager;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.load.resource.bitmap.TransformationUtils;
import com.lens.chatmodel.interf.IBooleanListener;
import com.lensim.fingerchat.commons.helper.ContextHelper;
import com.lensim.fingerchat.commons.utils.DensityUtil;
import java.util.HashMap;

/**
 * Created by LL130386 on 2017/9/1.
 */

public class CustomShapeTransformation extends BitmapTransformation {

    private Paint mPaint; // 画笔
    private Context mContext;
    private int mShapeRes; // 形状的drawable资源
    private double scale;
    private boolean isVideo;
    private final int DEFAULT_BIG = DensityUtil.dip2px(ContextHelper.getContext(), 160);
    private final int DEFAULT_SMALL = DensityUtil.dip2px(ContextHelper.getContext(), 120);
    private IBooleanListener mLongImageListener;

    public CustomShapeTransformation(Context context, int shapeRes, boolean isVideo) {
        super(context);
        mContext = context;
        mShapeRes = shapeRes;
        // 实例化Paint对象，并设置Xfermode为SRC_IN
        mPaint = new Paint();
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        this.isVideo = isVideo;
    }

    public CustomShapeTransformation(Context context, int shapeRes, boolean isVideo,
        IBooleanListener longImageListener) {
        super(context);
        mContext = context;
        mShapeRes = shapeRes;
        // 实例化Paint对象，并设置Xfermode为SRC_IN
        mPaint = new Paint();
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        this.isVideo = isVideo;
        mLongImageListener = longImageListener;
    }


    // 复写该方法，完成图片的转换
    @Override
    public Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth,
        int outHeight) {
        // 获取到形状资源的Drawable对象
        Drawable shape = ContextCompat.getDrawable(mContext, mShapeRes);
//        float shapeWidth = shape.getMinimumWidth();// 形状的宽
//        float shapeHeight = shape.getMinimumHeight();// 形状的高

        int width = toTransform.getWidth();// 实际图片的宽
        int height = toTransform.getHeight(); // 实际图片的高

        if (isVideo) {//是视频消息背景图，使用默认尺寸
            if (width < height) {
                width = DEFAULT_SMALL;
                height = DEFAULT_BIG;
            } else {
                width = DEFAULT_BIG;
                height = DEFAULT_SMALL;
            }
        } else {
            if (height != 0) {
                scale = (width * 1.00) / height;
                if (mLongImageListener != null) {
                    if (scale < 0.2) {
                        mLongImageListener.onResult(true);
                    } else {
                        mLongImageListener.onResult(false);
                    }
                }
                if (width >= height) {
                    width = getBitmapWidth();
                    height = (int) (width / scale);
                } else {
                    height = getBitmapHeight();
                    width = (int) (height * scale);
                }
            } else {
                width = 100;
                height = 100;
            }
        }
        //以图片宽高为基准，所以不需要考虑形状宽高
//    if (width > height) { // 如果图片的宽大于高，则以高为基准，以形状的宽高比重新设置宽度
//            width = (int) (height * (shapeWidth / shapeHeight));
//    } else { // 如果图片的宽小于等于高，则以宽为基准，以形状的宽高比重新设置高度度
//           height = (int) (width * (shapeHeight / shapeWidth));
//    }
        // 居中裁剪图片，调用Glide库中TransformationUtils类的centerCrop()方法完成裁剪，保证图片居中且填满
        final Bitmap toReuse = pool.get(width, height,
            toTransform.getConfig() != null ? toTransform.getConfig() : Bitmap.Config.ARGB_8888);
        Bitmap transformed = TransformationUtils.centerCrop(toReuse, toTransform, width, height);
        if (toReuse != null && toReuse != transformed && !pool.put(toReuse)) {
            toReuse.recycle();
        } // 根据算出的宽高新建Bitmap对象并设置到画布上
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(
            bitmap); // 设置形状的大小与图片的大小一致
        shape.setBounds(0, 0, width, height); // 将图片画到画布上
        shape.draw(canvas); // 将裁剪后的图片画得画布上
        canvas.drawBitmap(transformed, 0, 0, mPaint);
        return bitmap;
    }

    @Override
    public String getId() { // 用于缓存的唯一标识符
        return "CustomShapeTransformation" + mShapeRes;
    }

    //isMap 2 非map 3
    public int getBitmapWidth() {
        return getScreenWidth(mContext) / 3;
    }

    public int getBitmapHeight() {
        return getScreenHeight(mContext) / 4;
    }

    // 获取屏幕的宽度
    @SuppressWarnings("deprecation")
    public int getScreenWidth(Context context) {
        WindowManager manager = (WindowManager) context
            .getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        return display.getWidth();
    }

    // 获取屏幕的高度
    @SuppressWarnings("deprecation")
    public int getScreenHeight(Context context) {
        WindowManager manager = (WindowManager) context
            .getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        return display.getHeight();
    }

}
