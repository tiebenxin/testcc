package com.lensim.fingerchat.fingerchat.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.format.Formatter;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.lensim.fingerchat.fingerchat.R;

/**
 * Created by LY309313 on 2016/12/3.
 */

public class MomeryView extends View {

    private int cx;
    private int cy;
    private int raduis;
    private long[] data;
    private String[] size;
    private String[] categroys;
    private int[] colors;
    private int[] percent;
    private long total;
    private int blockSize;
    private int textSize = 50;
    private int blockTextSize = 20;
    private Paint mCirclePaint;
    private Paint mTextPaint;
    private RectF rectF;
    private RectF bigRectF;
    private int totalArc;
    private int offset;
    private int margin;
    private int padding;
    private Context context;

    public MomeryView(Context context) {
        this(context,null);
    }

    public MomeryView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);

    }

    private void init(Context context, AttributeSet attrs) {
        this.context = context;

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MomeryView);

        textSize = (int) typedArray.getDimension(R.styleable.MomeryView_cache_textSize, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,14,getResources().getDisplayMetrics()));
        raduis = (int) typedArray.getDimension(R.styleable.MomeryView_cache_raduis,TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,100,getResources().getDisplayMetrics()));
        offset = (int) typedArray.getDimension(R.styleable.MomeryView_cache_offset,
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,5,getResources().getDisplayMetrics()));
        margin = (int) typedArray.getDimension(R.styleable.MomeryView_cache_block_margin_top,
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,10,getResources().getDisplayMetrics()));
        padding = (int) typedArray.getDimension(R.styleable.MomeryView_cache_border_padding,
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,10,getResources().getDisplayMetrics()));
        blockSize = (int) typedArray.getDimension(R.styleable.MomeryView_cache_blocksize,
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,25,getResources().getDisplayMetrics()));
        blockTextSize = (int) typedArray.getDimension(R.styleable.MomeryView_cache_block_text_size,
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,12,getResources().getDisplayMetrics()));
        typedArray.recycle();

        mCirclePaint = new Paint();
        mCirclePaint.setAntiAlias(true);
        mCirclePaint.setStyle(Paint.Style.FILL);
        mCirclePaint.setTextSize(blockTextSize);

        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextAlign(Paint.Align.LEFT);
        mTextPaint.setTextSize(textSize);

        colors = new int[3];
        colors[0] = Color.GREEN;
        colors[1] = Color.argb(255,79,220,246);
        colors[2] = Color.argb(255,175,175,175);

        categroys = new String[]{"飞鸽","其他","剩余"};

        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;

        totalArc=0;
        cx = width/2;
        cy = height/5;


        rectF = new RectF(cx-raduis,cy-raduis,cx+raduis,cy+raduis);
        bigRectF = new RectF(cx-raduis - offset,cy-raduis - offset,cx+raduis +offset,cy+raduis +offset);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int w = MeasureSpec.getSize(widthMeasureSpec);
        int h = MeasureSpec.getSize(heightMeasureSpec);
        int width;
        int height;
        if(widthMode == MeasureSpec.EXACTLY){
            width = w;
        }else{
            width = getResources().getDisplayMetrics().widthPixels;
        }
        if(heightMode == MeasureSpec.EXACTLY){
            height = h;
        }else{
            Log.i("MomeryView===textSize:" ,"" + textSize);
            height = getResources().getDisplayMetrics().heightPixels / 5
                    + raduis //半径
                    + margin //距离方块的距离
                    + blockSize  //方块的大小
                    + 100 //多出来的盈余
                    + textSize //文字大小
                    + getPaddingBottom(); //设置的padding

        }
        setMeasuredDimension(MeasureSpec.makeMeasureSpec(width,MeasureSpec.EXACTLY),MeasureSpec.makeMeasureSpec(height,MeasureSpec.EXACTLY));
    }

    public void setData(long total, long[] data){
        long totalSize =0;
        if(percent == null){
            percent = new int[data.length];
        }
        if(size == null){
            size = new String[data.length];
        }
        for(int i=0;i<data.length;i++){

            if(i == data.length-1){
                percent[i] = (int) (100 - totalSize*100/total);
            }else{
                percent[i] = (int) (data[i]*100/total);
            }
            size[i] = Formatter.formatFileSize(context,data[i]);
            totalSize+=data[i];
        }
        if(total != totalSize){
            throw new IllegalArgumentException("total size is not equels data");
        }
        this.total = total;
        this.data = data;
//        for(int i:percent)
//        System.out.println("比例：" + i);
       startAnim();
    }

    private void startAnim() {
        ValueAnimator anim = ValueAnimator.ofInt(0,360);
        anim.setDuration(1500);
        anim.setInterpolator(new AccelerateDecelerateInterpolator());
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                totalArc = (Integer) animation.getAnimatedValue();
                invalidate();
            }
        });
        anim.start();
    }

    public void setData(long[] data){
        if(total == 0){
            return;
        }
        if(data.length != 3){
            throw new IllegalArgumentException("the argument count must be three!!");
        }
        this.data = data;
        long totalSize =0;
        if(percent == null){
            percent = new int[data.length];
        }
        for(int i=0;i<data.length;i++){

            if(i == data.length-1){
                percent[i] = (int) (100 - totalSize*100/total);
            }else{
                percent[i] = (int) (data[i]*100/total);
            }
            totalSize+=data[i];
        }
        if(totalSize != this.total){
            throw new IllegalArgumentException("total size is not equels data");
        }
        startAnim();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(total == 0){
            return ;
        }
        //圆绘制完成,做判断，如果总的角度小于某一个角度，则只绘制那一个角度
        int startArgle = -90;
        if(totalArc < percent[0]*360/100){
            mCirclePaint.setColor(colors[0]);
            canvas.drawArc(bigRectF,startArgle,totalArc,true,mCirclePaint);
        }else if(totalArc < (percent[1] + percent[0])*360/100){
            mCirclePaint.setColor(colors[0]);
            canvas.drawArc(bigRectF,startArgle,percent[0]*360/100,true,mCirclePaint);
            mCirclePaint.setColor(colors[1]);
            startArgle+=percent[0]*360/100;
            canvas.drawArc(rectF, startArgle,totalArc - percent[0]*360/100,true,mCirclePaint);
        }else{
            mCirclePaint.setColor(colors[0]);
            canvas.drawArc(bigRectF,startArgle,percent[0]*360/100,true,mCirclePaint);
            mCirclePaint.setColor(colors[1]);
            startArgle+=percent[0]*360/100;
            canvas.drawArc(rectF,startArgle,percent[1]*360/100,true,mCirclePaint);
            mCirclePaint.setColor(colors[2]);
            startArgle+=percent[1]*360/100;
            canvas.drawArc(rectF,startArgle,totalArc - (100  - percent[2])*360/100,true,mCirclePaint);
        }
//        for(int i = 0;i<percent.length;i++){
//            mCirclePaint.setColor(colors[i]);
//            if(i==0)
//            canvas.drawArc(bigRectF,startArgle,percent[i]*totalArc/100,true,mCirclePaint);
//            else
//                canvas.drawArc(rectF,startArgle,percent[i]*totalArc/100,true,mCirclePaint);
//            startArgle+=percent[i]*totalArc/100;
//        }
        //绘制三个方块
        canvas.save();
        canvas.translate(0,margin);
        int totalWidth = getWidth() - padding*2;

        int top = cy + raduis + margin;
        int centerOffSet = (totalWidth/3 - blockSize - blockTextSize*2)/2;
        for(int i=0;i<3;i++){
            mCirclePaint.setColor(colors[i]);
            canvas.drawRect(padding+totalWidth*i/3 +centerOffSet,top,padding+totalWidth*i/3 + blockSize + centerOffSet,top+blockSize,mCirclePaint);
            canvas.drawText(categroys[i],padding+totalWidth*i/3 +centerOffSet + blockSize + 10,top + (blockSize+blockTextSize) /2,mCirclePaint);
        }
        canvas.save();

        canvas.translate(0,blockSize + 10 + textSize);

        int textTop = cy + raduis + margin;

        for(int i=0;i<3;i++){

            canvas.drawText(size[i],padding+totalWidth*i/3 + centerOffSet,textTop,mTextPaint);
        }
        canvas.restore();
        canvas.restore();

        //绘制
    }
}
