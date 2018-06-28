package com.lensim.fingerchat.fingerchat.view;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Looper;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import com.lensim.fingerchat.fingerchat.R;
import com.lensim.fingerchat.commons.utils.SPHelper;

/**
 * 作者：周哥 创建自： 2016/6/12.
 * <p/>
 * 描述：
 */
public class CustomTabView extends View {
    private static final String ALPHA_KEY = "alpha_key";
    private Bitmap mIconBitmap;
    private Bitmap mIconClickBitmap;
    private int mColor;
    private int mTextSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics());
    private String mText = "选项";

    private float mAlpha;

    private Paint mPaint;
    //private Bitmap mBitmap;
    //private Canvas mCanvas;

    private Rect mIconRect;
    private Rect mTextRect;
    private Paint mTextPaint;

    public CustomTabView(Context context) {
        this(context, null, 0);
    }

    public CustomTabView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomTabView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    /**
     * 可以自定义的属性
     * 1、tab的显示图标
     * 2、tab的显示颜色
     * 3、文字大小
     * 4、文字内容
     *
     * @param context
     * @param attrs
     */
    private void init(Context context, AttributeSet attrs) {

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomTabView);
        int count = typedArray.getIndexCount();
        for (int i = 0; i < count; i++) {
            int attr = typedArray.getIndex(i);
            switch (attr) {
                case R.styleable.CustomTabView_tabIcon:
                    BitmapDrawable drawable = (BitmapDrawable) typedArray.getDrawable(attr);
                    mIconBitmap = drawable.getBitmap();
                    break;
                case R.styleable.CustomTabView_tabColor:
                    mColor = typedArray.getColor(attr,0x0f0);
                    break;
                case R.styleable.CustomTabView_tabClickIcon:
                    BitmapDrawable clickDrawable = (BitmapDrawable) typedArray.getDrawable(attr);
                    mIconClickBitmap = clickDrawable.getBitmap();
                    break;
                case R.styleable.CustomTabView_tabText:
                    mText = typedArray.getString(attr);
                    break;
                case R.styleable.CustomTabView_tabTextSize:
                    mTextSize = (int) typedArray.getDimension(attr, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                            12, getResources().getDisplayMetrics()));
                    break;
            }
        }
        typedArray.recycle();

        int factor = SPHelper.getInt("font_size", 1);
        if(factor!=1)
        mTextSize += factor*2;
        mTextRect = new Rect();
        mTextPaint = new Paint();
        mTextPaint.setTextSize(mTextSize);//设置字体大小
        mTextPaint.setColor(0x7a7e83);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setDither(true);
        mTextPaint.getTextBounds(mText, 0, mText.length(), mTextRect);//获取文字范围
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);//测量之后就可以拿到尺寸了
        int iconWith = Math.min(getMeasuredWidth()-getPaddingLeft()-getPaddingRight(),
                getMeasuredHeight()-getPaddingTop()-getPaddingBottom()-mTextRect.height());

        int left = getMeasuredWidth()/2-iconWith/2;
        int top = getMeasuredHeight()/2-iconWith/2-mTextRect.height()/2;

        mIconRect = new Rect(left,top,left+iconWith,top+iconWith);

    }

    @Override
    protected void onDraw(Canvas canvas) {

      // canvas.drawBitmap(mIconBitmap, null, mIconRect, null);//不用画笔
        int alpha = (int) Math.ceil(255*mAlpha);
        drawResouceBitmap(canvas,alpha);
        setupTargetBitmap(canvas,alpha);
        drawResouceText(canvas, alpha);
       drawTargetText(canvas, alpha);
       // canvas.drawBitmap(mBitmap,0,0,null);
    }



    private void drawTargetText(Canvas canvas,int alpha) {

        mTextPaint.setColor(mColor);
        mTextPaint.setAlpha(alpha);
        int x = getMeasuredWidth()/2-mTextRect.width()/2;
        int y = mIconRect.bottom + mTextRect.height();
        canvas.drawText(mText, x, y, mTextPaint);

    }

    private void drawResouceText(Canvas canvas, int alpha) {
        mTextPaint.setColor(0x7a7e83);
        int x = getMeasuredWidth()/2-mTextRect.width()/2;
        int y = mIconRect.bottom + mTextRect.height();
        mTextPaint.setAlpha(255-alpha);
        canvas.drawText(mText,x,y,mTextPaint);
    }

    private void drawResouceBitmap(Canvas canvas, int alpha) {
        mPaint = new Paint();
        // mPaint.setColor(mColor);
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setAlpha(255-alpha);
        //  mCanvas.drawBitmap();
        // mCanvas.drawRect(mIconRect, mPaint);
        //以上是在内存中绘制了一个纯色
        //从下面开始绘制icon
        //mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OVER));//能看见目标视图
        //mPaint.setAlpha(255);
        canvas.drawBitmap(mIconBitmap, null, mIconRect, mPaint);
        canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG
                | Paint.FILTER_BITMAP_FLAG));
    }

    private void setupTargetBitmap(Canvas canvas,int alpha) {
       // mBitmap = Bitmap.createBitmap(getMeasuredWidth(),getMeasuredHeight(), Bitmap.Config.ARGB_8888);
       // mCanvas = new Canvas(mIconClickBitmap);
        mPaint = new Paint();
       // mPaint.setColor(mColor);
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setAlpha(alpha);
      //  mCanvas.drawBitmap();
       // mCanvas.drawRect(mIconRect, mPaint);
        //以上是在内存中绘制了一个纯色
        //从下面开始绘制icon
        //mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OVER));//能看见目标视图
        //mPaint.setAlpha(255);
        canvas.drawBitmap(mIconClickBitmap, null, mIconRect, mPaint);
        canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG
                | Paint.FILTER_BITMAP_FLAG));
    }
    public void setIconAlpha(float alpha){
        mAlpha = alpha;
        if(Looper.getMainLooper() == Looper.myLooper()){
            invalidate();
        }else{
            postInvalidate();
        }
    }
    private static final String SUPER_STATUS = "super_status";
    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putFloat(ALPHA_KEY, mAlpha);
        bundle.putParcelable(SUPER_STATUS,super.onSaveInstanceState());
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {

        if(state instanceof Bundle){
            Bundle bundle = (Bundle) state;
            mAlpha = bundle.getFloat(ALPHA_KEY);
            super.onRestoreInstanceState(bundle.getParcelable(SUPER_STATUS));
            return;
        }
        super.onRestoreInstanceState(state);
    }
}


