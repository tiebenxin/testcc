package com.lensim.fingerchat.components.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.lensim.fingerchat.components.R;


/**
 * Created by LY309313 on 2016/11/19.
 */

public class CircleProgress extends View {

    private int raduis;
    private int strikeWidth;
    private int strikeColor;
    private int CircleColor;
    private int progress;
    private Paint strikePaint;
    private Paint circlePaint;
    private RectF oval;

    public CircleProgress(Context context) {
        super(context);
        init(context,null);
    }

    public CircleProgress(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);
    }

    public CircleProgress(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context,attrs);
    }


    private void init(Context context, AttributeSet attrs){
        initProgress();

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CircleProgress);
        try {

            raduis = a.getDimensionPixelSize(R.styleable.CircleProgress_raduis,100);
            strikeWidth = a.getDimensionPixelSize(R.styleable.CircleProgress_strikeWidth,2);
            strikeColor = a.getColor(R.styleable.CircleProgress_strikeColor, Color.BLACK);
            CircleColor = a.getColor(R.styleable.CircleProgress_color, Color.BLUE);

        }finally {
            a.recycle();
        }


        strikePaint = new Paint();
        strikePaint.setColor(strikeColor);
        strikePaint.setStyle(Paint.Style.STROKE);
        strikePaint.setStrokeWidth(strikeWidth);
        strikePaint.setAntiAlias(true);

        circlePaint = new Paint();
        circlePaint.setColor(CircleColor);
        circlePaint.setStyle(Paint.Style.FILL);
        circlePaint.setAntiAlias(true);

    }

    private void initProgress() {
        raduis = 100;
        strikeWidth = 2;
        strikeColor = Color.BLACK;
        CircleColor = Color.BLUE;
        progress = 0;
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        float xpad = (float)(getPaddingLeft()+getPaddingRight());
        float ypad = (float)(getPaddingBottom()+ getPaddingTop());

        float wwd = (float)w - xpad;
        float hhd = (float)h - ypad;

        oval = new RectF(getPaddingLeft(), getPaddingTop(), getPaddingLeft()+wwd, getPaddingTop()+hhd);
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawArc(oval,0,360,true,strikePaint);
       // canvas.drawCircle(0,0,raduis,strikePaint);
        canvas.drawArc(oval,0,progress*3.6f,true,circlePaint);

    }

    public void setPercent(int percent) {
        this.progress = percent;
        invalidate();
        requestLayout();
    }
}
