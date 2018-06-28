package com.lensim.fingerchat.fingerchat.ui.settings;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;

import com.lensim.fingerchat.fingerchat.R;

/**
 * Created by LY305512 on 2017/12/26.
 */

public class FontAdjustView extends View {
    private static final String TAG = "FontAdjustView";
    private final Paint linePaint;
    private final Paint textPaint;
    private final Paint circlePaint;
    private int lineColor;
    //private int lineWidth;
    private int lineHeight;
    private FontBlock fontBlock;
    private int dx;
    private int preX;
    private int preY;
    private int paddingLeft;
    private int paddingRight;
    private int spaceV;
    private int textStartSize;
    private OnFontSelectListener onFontSelectListener;
    private int raiseSize;
    private int lineWidth;
    private int sizeIndex = 8;
    //  private final int defaultPos;
    // private final int lineWidth1;


    public FontAdjustView(Context context, AttributeSet attrs) {
        super(context, attrs);


        fontBlock = new FontBlock();

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.FontAdjustView);
//        int count = typedArray.getIndexCount();
//        for(int i=0;i<count;i++){
//            int attr =typedArray.getIndex(i);
//            switch (attr){
//                case R.styleable.FontAdustView_line_color:
        lineColor = typedArray.getColor(R.styleable.FontAdjustView_line_color,Color.GRAY);
        //  break;
        //  case R.styleable.FontAdustView_circle_raduis:
        fontBlock.raduis = (int) typedArray.getDimension(R.styleable.FontAdjustView_circle_raduis,25);
//                    break;
//                case R.styleable.FontAdustView_circle_color:
        fontBlock.color = typedArray.getColor(R.styleable.FontAdjustView_circle_color,Color.BLUE);
//                    break;
//                case R.styleable.FontAdustView_horizantol_padding:
        paddingLeft = paddingRight = (int) typedArray.getDimension(R.styleable.FontAdjustView_horizantol_padding,50);
//                    break;
//                case R.styleable.FontAdustView_start_size:
        textStartSize = typedArray.getInt(R.styleable.FontAdjustView_start_size,14);
//                    break;
//                case R.styleable.FontAdustView_raise_size:
        raiseSize = typedArray.getInt(R.styleable.FontAdjustView_raise_size,2);
//                    break;
//                case R.styleable.FontAdustView_space_v:
        spaceV = (int) typedArray.getDimension(R.styleable.FontAdjustView_space_v,50);
//                    break;
//                case R.styleable.FontAdustView_lineWidth:
        lineWidth = (int) typedArray.getDimension(R.styleable.FontAdjustView_lineWidth,3);

        fontBlock.pos = typedArray.getInt(R.styleable.FontAdjustView_default_position,1);
//                    break;
//            }
//        }
        typedArray.recycle();


        linePaint = new Paint();
        linePaint.setColor(lineColor);
        linePaint.setAntiAlias(true);
        linePaint.setStrokeWidth(lineWidth);


        textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setAntiAlias(true);

        circlePaint = new Paint();
        circlePaint.setColor(fontBlock.color);
        circlePaint.setAntiAlias(true);
        circlePaint.setStrokeWidth(1);
        circlePaint.setStyle(Paint.Style.FILL_AND_STROKE);



//        paddingLeft = 50;
//        paddingRight = 50;
//        spaceV = 50;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //   final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int width  = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        if(heightMode != MeasureSpec.EXACTLY){
            int textHeight = px2sp(textStartSize + 4*raiseSize);
            height = (getPaddingTop() + textHeight + spaceV)*2;
        }
        setMeasuredDimension(width,height);
//            TextView
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //  lineWidth = getWidth();
        lineHeight = 20;
        int startX = paddingLeft;
        int startY = getHeight() / 2;
        int endX =  getWidth() - paddingRight;
        int endY = getHeight() / 2;
        //横线
        canvas.drawLine(startX,startY,endX,endY,linePaint);
        //开始的刻度
        for(int i=0;i< sizeIndex ;i++){
            if(i > 0){
                canvas.save();
                canvas.translate(( getWidth()-(paddingLeft + paddingRight))/(sizeIndex -1),0);
                canvas.drawLine(startX,startY-lineHeight,startX,startY+lineHeight,linePaint);
            }else{
                canvas.drawLine(startX,startY-lineHeight,startX,startY+lineHeight,linePaint);
            }

        }
        canvas.restoreToCount(1);

        fontBlock.width = ( getWidth()-(paddingLeft + paddingRight)) / (sizeIndex -1);
        fontBlock.cy = startY;
        if(fontBlock.cx ==0){
            fontBlock.cx = fontBlock.pos*fontBlock.width+paddingLeft;
        }else
            fontBlock.cx = fontBlock.cx + dx;
        canvas.drawCircle(fontBlock.cx,fontBlock.cy,fontBlock.raduis,circlePaint);

        // Log.i(TAG,"圆点位置:x==" + fontBlock.cx);

        canvas.save();
        canvas.translate(0,-spaceV);
        textPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,textStartSize,getResources().getDisplayMetrics()));
        canvas.drawText("A",startX,startY,textPaint);
        textPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,textStartSize + raiseSize,getResources().getDisplayMetrics()));
        canvas.drawText("标准",paddingLeft+fontBlock.width,startY,textPaint);
        textPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,textStartSize + 4*raiseSize,getResources().getDisplayMetrics()));
        canvas.drawText("A",endX,startY,textPaint);
        canvas.restore();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                //判断落点位置
                preX = (int) event.getX();
                preY = (int) event.getY();
                int distanseX = Math.abs(preX - fontBlock.cx);
                int distanseY = Math.abs(preY -fontBlock.cy);
                int r = (int) Math.sqrt(Math.pow(distanseX,2) + Math.pow(distanseY,2));
                if(r > fontBlock.raduis){
                    for (int i = 0;i<sizeIndex;i++){
                        int left =i*fontBlock.width + paddingLeft-fontBlock.raduis;
                        int top = fontBlock.cy-fontBlock.raduis;
                        int right = i*fontBlock.width + paddingRight + fontBlock.raduis;
                        int bottom = fontBlock.cy+fontBlock.raduis;
                        Rect rect = new Rect(left,top,right,bottom);
                        if(rect.contains(preX,preY)){
                            //
                            if(onFontSelectListener != null){
                                //int size = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,textStartSize + i*raiseSize,getResources().getDisplayMetrics());
                                onFontSelectListener.onFontSelect(i,px2sp(textStartSize + i*raiseSize));
                            }
                            fontBlock.pos= i;
                            anim(fontBlock.cx,i*fontBlock.width + paddingLeft);
                        }
                    }
                    return false;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                //改变dx的大小
                int x = (int) event.getX();

                for (int i = 0;i<sizeIndex;i++){
                    int left =i*fontBlock.width + paddingLeft-fontBlock.raduis;
                    int top = fontBlock.cy-fontBlock.raduis;
                    int right = i*fontBlock.width + paddingLeft + fontBlock.raduis;
                    int bottom = fontBlock.cy+fontBlock.raduis;
                    Rect rect = new Rect(left,top,right,bottom);
                    if(rect.contains(x,fontBlock.cy)){
                        //
                        if(onFontSelectListener != null){
                            // int size = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,textStartSize + i*raiseSize,getResources().getDisplayMetrics());
                            onFontSelectListener.onFontSelect(i,px2sp(textStartSize + i*raiseSize));
                        }
                        fontBlock.pos= i;
                        // anim(fontBlock.cx,i*fontBlock.width + 50);
                    }
                }

                if((x-paddingLeft)%fontBlock.width==0){
                    Log.i(TAG,"触发");
                    int i = (x-paddingLeft)/fontBlock.width;
                    if(onFontSelectListener != null){


                        Log.i(TAG,"i等于多少:" + i);
                        // int size = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,textStartSize + i*raiseSize,getResources().getDisplayMetrics());
                        onFontSelectListener.onFontSelect(i,px2sp(textStartSize + i*raiseSize));
                    }
                    fontBlock.pos= i;
                }
                dx = x - preX;
                if(fontBlock.cx + dx <= paddingLeft || fontBlock.cx + dx > getWidth() - paddingRight){
                    dx = 0;
                }

                preX = x;
                // Log.i(TAG,dx + "");
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                //判断滑块的位置，完成接下来的滑动,分为5个阶段

                int upX = (int) event.getX();
                dx = 0;
                for(int i=0;i<sizeIndex;i++){
                    int pos = i;
                    if(upX<(paddingLeft+i*fontBlock.width) && upX>paddingLeft){
                        int s1 = paddingLeft+(i-1)*fontBlock.width;
                        int e1 = paddingLeft+i*fontBlock.width;
                        int offset = upX - s1;
                        if(offset < fontBlock.width/2){
                            pos = i-1;
                            anim(upX,s1);
                        }else if(offset >= fontBlock.width/2){
                            anim(upX,e1);
                        }
                        if(onFontSelectListener != null){
                            //   int i = (x-paddingLeft)/fontBlock.width;
                            // int size = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,textStartSize + pos*raiseSize,getResources().getDisplayMetrics());
                            onFontSelectListener.onFontSelect(pos,px2sp(textStartSize + pos*raiseSize));
                        }
                        fontBlock.pos = pos;
                        break;

                    }

                }
                break;

        }
        return true;

    }

    private void anim(int i, int end) {
        ValueAnimator animator = ValueAnimator.ofInt(i,end);

        // ObjectAnimator animator = ObjectAnimator.ofInt(fontBlock,"cx",i,end);
        // int duration = Math.abs(end - i);
        animator.setDuration(200);
        animator.setInterpolator(new AccelerateInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Integer value = (Integer) animation.getAnimatedValue();
                fontBlock.setCx(value);
                invalidate();
            }
        });
        animator.start();
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
    }

    public void setPosition(int pos) {
        fontBlock.pos = pos;
        fontBlock.cx = 0;
        invalidate();
    }

    public class FontBlock{
        int cx;
        int cy;
        int raduis;
        int width;
        int color;
        int pos;

        public void setCx(int cx){
            this.cx = cx;
        }
    }

    public int getSize(){
        return px2sp(textStartSize + fontBlock.pos*raiseSize);
    }

    //非标准度量尺寸转变为标准度量尺寸 (Android系统中的标准尺寸是px, 即像素)
    private int px2sp(int px){
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,px,getResources().getDisplayMetrics());
    }

    public void setOnFontSelectListener(OnFontSelectListener onFontSelectListener) {
        this.onFontSelectListener = onFontSelectListener;
    }

    public interface OnFontSelectListener{
        void onFontSelect(int pos,int size);
    }
}
