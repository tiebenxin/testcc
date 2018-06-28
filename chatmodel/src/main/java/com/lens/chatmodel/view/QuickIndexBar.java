package com.lens.chatmodel.view;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.lens.chatmodel.R;
/**
 * Created by xhdl0002 on 2018/1/12.
 */
public class QuickIndexBar extends View {

    private float mTextSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 13, getResources().getDisplayMetrics());
    private float mCellHeight;
    private int mTouchIndex = -1;//用于记录当前触摸的索引值
    private int totalSize;
    //暴露一个字母的监听
    public interface OnLetterUpdateListener {
        void onLetterUpdate(String letter);

        void onLetterCancel();
    }

    private OnLetterUpdateListener mListener;

    public void setOnLetterUpdateListener(OnLetterUpdateListener listener) {
        mListener = listener;
    }


    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //获取单元格的宽度和高度
        mCellHeight = getMeasuredHeight() * 1.0f / sections.length;
    }
    private Paint paint;
    private TextView tv_title;
    private float height;
    private RelativeLayout.LayoutParams layoutParams;

    public QuickIndexBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private String[] sections;

    private void init() {
        sections = new String[]{"星", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L",
            "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "#"};
        totalSize = sections.length;
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.parseColor("#8C8C8C"));
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(mTextSize);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float center = getWidth() / 2;
        height = getHeight() / sections.length;
        for (int i = sections.length - 1; i > -1; i--) {
            canvas.drawText(sections[i], center, height * (i + 1), paint);
        }
    }

    private int sectionForPoint(float y) {
        int index = (int) (y / height);
        if (index < 0) {
            index = 0;
        }
        if (index > sections.length - 1) {
            index = sections.length - 1;
        }
        return index;
    }

    private void setHeaderTextAndscroll(MotionEvent event) {
        String headerString = sections[sectionForPoint(event.getY())];
        if (tv_title != null) {
            tv_title.setText(headerString);
            scrollHead(sectionForPoint(event.getY()));
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int index = -1;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                if (tv_title == null) {
                    tv_title = ((View) getParent()).findViewById(R.id.group_Letter);
                }
                index = (int) (event.getY() / mCellHeight);//   y值/每个单元格的高度 = 当前单元格的索引
                if (index >= 0 && index < sections.length) {
                    if (index != mTouchIndex) {
                        if (mListener != null) {
                            mListener.onLetterUpdate(sections[index]);
                            mTouchIndex = index;
                        }
                    }
                }
                setHeaderTextAndscroll(event);
                tv_title.setVisibility(View.VISIBLE);
                setBackgroundResource(R.drawable.sidebar_background_pressed);
                return true;
            }
            case MotionEvent.ACTION_MOVE: {
                index = (int) (event.getY() / mCellHeight);
                if (index >= 0 && index < sections.length) {
                    if (index != mTouchIndex) {
                        if (mListener != null) {
                            mListener.onLetterUpdate(sections[index]);
                            mTouchIndex = index;
                        }
                    }
                }
                setHeaderTextAndscroll(event);
                return true;
            }
            case MotionEvent.ACTION_UP:
                mTouchIndex = -1;
                if (mListener != null) {
                    mListener.onLetterCancel();
                }
                tv_title.setVisibility(View.INVISIBLE);
                setBackgroundColor(Color.TRANSPARENT);
                return true;
            case MotionEvent.ACTION_CANCEL:
                tv_title.setVisibility(View.INVISIBLE);
                setBackgroundColor(Color.TRANSPARENT);
                return true;
        }
        return super.onTouchEvent(event);
    }

    private void scrollHead(int position) {
        int h;
        if (position > totalSize - 4 && position <= totalSize - 1) {//气泡有高低，如果到到最底部，会压缩变形
            position = totalSize - 4;
            h = (int) (height * (position - 1) + height / 2);
        } else {
            h = (int) (height * (position - 1) + height / 2);
        }
        int w = (int) getX() - 2 * getMeasuredWidth();
        if (layoutParams != null) {
            layoutParams.topMargin = h;
            layoutParams.leftMargin = w;
            tv_title.setLayoutParams(layoutParams);
        }

    }

    public void setLayoutParams(RelativeLayout.LayoutParams params) {
        layoutParams = params;
    }
}
