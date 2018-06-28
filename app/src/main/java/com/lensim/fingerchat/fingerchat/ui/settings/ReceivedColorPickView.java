package com.lensim.fingerchat.fingerchat.ui.settings;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;

import com.lensim.fingerchat.fingerchat.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LY305512 on 2017/12/26.
 */

public class ReceivedColorPickView extends View {
    private static final String TAG = "ReceivedColorPickView";
    /**颜色块集合**/
    private List<RectF> rectFs;

    /**起始的半圆**/
    private RectF startRect;
    /**结束的半圆**/
    private RectF endRect;
    //private Path block;
    /**颜色条画笔**/
    private Paint paint;
    /**颜色条的宽度**/
    private int colorBarwidth;
    private int totalWidth;
    /**颜色条的高度**/
    private int colorBarHeight;
    /**颜色条的起始位置**/
    private Point startPoint;

    private Point blockCenter;
    /**颜色集合**/
    private int[] colors = {
            Color.parseColor("#FFAE1616"),Color.parseColor("#000000"),Color.parseColor("#FFD79F12"),
            Color.parseColor("#FF1A832A"),Color.parseColor("#FF20BDC2"), Color.parseColor("#FF500883"),
            Color.parseColor("#FFA9206E")
    };
    /**颜色块画笔**/
    private Paint blockPaint;
    /**滑块**/
    private RectF block;
    /**滑块宽度**/
    private int blockWidth;
    /**滑块高度**/
    private int blockheight;
    /**滑块所在的位置**/
    private int blockindex;

    private Point circlePoint;
    /**要绘制的文字**/
    private String text;

    private int raduis;

    private boolean drawText;

    private ValueAnimator animator;
    private final int screenWidth;
    private OnReceivedColorPickListenr onReceivedColorPickListenr;

    public ReceivedColorPickView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        DisplayMetrics displaymetrics = new DisplayMetrics();
        ((WindowManager) context.getSystemService(
                Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(
                displaymetrics);
        screenWidth = displaymetrics.widthPixels;

        Log.d(TAG,"屏幕宽度:" + screenWidth);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ColorPickView);
        //用户设置的总的宽度
        colorBarHeight = (int) typedArray.getDimension(R.styleable.ColorPickView_bar_height,26);
        blockindex = typedArray.getInt(R.styleable.ColorPickView_block_index,1);
        typedArray.recycle();

        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);

        blockPaint = new Paint();
        blockPaint.setAntiAlias(true);
        blockPaint.setStyle(Paint.Style.FILL);
        blockPaint.setTextSize(30);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        calColorBar();
        //取控件的宽高，颜色条应该定位在控件的正中间，宽度值取总宽度的60%
    }

    private void calColorBar() {
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();

        Log.d(TAG,"宽度:" + width + "高度:" + height);


        if(rectFs == null && width!=0 && height!=0){


            totalWidth = (int) (width * 0.7);
            startPoint = new Point((int) (width*0.2),height/2 - colorBarHeight/2);

            colorBarwidth = totalWidth / colors.length;


            blockheight = colorBarHeight * 3;
            blockWidth = blockheight / 3;
            circlePoint = new Point((int) (width*0.2) / 2 ,height/2);
            raduis = blockheight /2;


            rectFs = new ArrayList<>();
            //知道
            RectF rectF1 = new RectF(startPoint.x+ this.colorBarHeight /2,startPoint.y,startPoint.x + colorBarwidth,startPoint.y+ this.colorBarHeight);
            rectFs.add(rectF1);
            int length = colors.length-1;
            for (int i = 1; i < length; i++) {
                RectF rectF = new RectF(startPoint.x + i* colorBarwidth,startPoint.y,startPoint.x+(i+1)* colorBarwidth,startPoint.y+ this.colorBarHeight);
                rectFs.add(rectF);
            }

            RectF rectF2 = new RectF(startPoint.x + length* colorBarwidth,startPoint.y,startPoint.x+colors.length* colorBarwidth - this.colorBarHeight /2,startPoint.y+ this.colorBarHeight);
            rectFs.add(rectF2);


            startRect = new RectF(startPoint.x,startPoint.y,startPoint.x+ this.colorBarHeight,startPoint.y+ this.colorBarHeight);
            endRect = new RectF(startPoint.x+colors.length* colorBarwidth - this.colorBarHeight,startPoint.y,startPoint.x+colors.length* colorBarwidth,startPoint.y+ this.colorBarHeight);


//            blockindex = 1;
            RectF rectF = rectFs.get(blockindex);
            blockCenter = new Point((int)rectF.centerX(),(int)rectF.centerY());

            block = new RectF(blockCenter.x- blockWidth / 2,blockCenter.y - blockheight/2,blockCenter.x + blockWidth / 2,blockCenter.y + blockheight/2);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        canvas.drawLine(100,100,500,500,paint);
//        canvas.drawRect(300,400,500,450,paint);
        paint.setColor(colors[0]);

        for (int i = 0; i < colors.length; i++) {
            paint.setColor(colors[i]);
            if(i==0){
                canvas.drawArc(startRect,90,180,true,paint);
            }
            if(i==colors.length-1){
                canvas.drawArc(endRect,-90,180,true,paint);
            }
            canvas.drawRect(rectFs.get(i),paint);
        }

        blockPaint.setColor(colors[blockindex]);
        blockPaint.setStyle(Paint.Style.FILL);
        canvas.drawRoundRect(block,10,10,blockPaint);
        if(drawText){
            float textWidth = blockPaint.measureText(text);
            Paint.FontMetrics fm = blockPaint.getFontMetrics();

            float textCenterVerticalBaselineY = circlePoint.y - fm.descent + (fm.descent - fm.ascent) / 2;
            float textstart =circlePoint.x - textWidth /2;
            canvas.drawText(text,textstart,textCenterVerticalBaselineY,blockPaint);
        }else
            canvas.drawCircle(circlePoint.x,circlePoint.y,raduis,blockPaint);

        blockPaint.setColor(Color.WHITE);
        blockPaint.setStyle(Paint.Style.STROKE);
        blockPaint.setStrokeWidth(4);
        canvas.drawRoundRect(block,10,10,blockPaint);
    }

    // private int startX;

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                Log.d(TAG,"按下事件");
                if(animator!=null && animator.isRunning()){
                    animator.cancel();
                }
                //按下时需要判断动画是否开在执行，如果在执行就需要停止
                int startX = (int) event.getX();
                blockCenter.x = startX;
                //需要将滑块移动到对应的位置
                if(startX < startPoint.x){
                    blockCenter.x = startPoint.x;

                }else if(startX > (startPoint.x+colors.length* colorBarwidth)){
                    blockCenter.x = startPoint.x+colors.length* colorBarwidth;
                }
                block.left = blockCenter.x- blockWidth / 2;
                block.right = blockCenter.x + blockWidth / 2;
                // block = new RectF(startX,80,startX+blockWidth,145);
                invalidate();


                break;
            case MotionEvent.ACTION_MOVE:
                //  Log.d(TAG,"滑动事件");

                int moveX = (int) event.getX();


                blockCenter.x = moveX;
                if(moveX < startPoint.x){
                    blockCenter.x = startPoint.x;

                }else if(moveX > (startPoint.x+colors.length* colorBarwidth)){
                    blockCenter.x = startPoint.x+colors.length* colorBarwidth;
                }
                block.left = blockCenter.x- blockWidth / 2;
                block.right = blockCenter.x + blockWidth / 2;
                // block = new RectF(startX,80,startX+blockWidth,145);

                for (int i = 0; i < rectFs.size(); i++) {
                    RectF rectF = rectFs.get(i);
                    if(rectF.contains(block.centerX(),block.centerY())){
                        //在这个
                        blockindex = i;
                        Log.d(TAG,"位置:" + i);
                    }
                }

                // startX = moveX;
                invalidate();
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                //根据索引位置，让滑块回到中间
                startAnim();
                if(onReceivedColorPickListenr!=null){
                    onReceivedColorPickListenr.onReceivedColorPick(colors[blockindex]);
                }
                break;

        }

        return true;

    }

    private void startAnim() {
        RectF rectF = rectFs.get(blockindex);
        int endx = (int)rectF.centerX();
        if(blockindex ==0){
            endx = (int)rectF.centerX() - colorBarHeight / 2;
        }else if(blockindex == colors.length-1){
            endx = (int)rectF.centerX() + colorBarHeight / 2;
        }
        animator = ValueAnimator.ofInt((int)block.centerX(),endx);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                blockCenter.x = (Integer) animation.getAnimatedValue();
                // blockCenter.x = animatedValue;
                block.left = blockCenter.x- blockWidth / 2;
                block.right = blockCenter.x + blockWidth / 2;

                invalidate();

            }
        });
        animator.setDuration(200);
        animator.setInterpolator(new AccelerateInterpolator());
        animator.start();

    }

    public void setText(String text) {
        this.text = text;
        drawText = true;
    }

    public void setColors(int[] colors) {
        this.colors = colors;
        invalidate();
    }

    public void setBlockindex(int blockindex) {
        this.blockindex = blockindex;
        invalidate();
    }

    public void setSelectedColor(int color){
        int size = colors.length;
        for (int i = 0; i < size; i++) {
            if (colors[i] == color){
                setBlockindex(i);
            }
        }
    }

    public int  getCurrColor(){
        return colors[blockindex];
    }

    public void setOnReceivedColorPickListenr(OnReceivedColorPickListenr onReceivedColorPickListenr) {
        this.onReceivedColorPickListenr = onReceivedColorPickListenr;
    }


    public interface OnReceivedColorPickListenr{
        void onReceivedColorPick(int color);
    }
}
