package com.lensim.fingerchat.components.widget;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Rect;
import android.support.design.widget.FloatingActionButton;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import com.lensim.fingerchat.commons.utils.TDevice;
import com.lensim.fingerchat.components.R;


/**
 * Created by LY309313 on 2016/12/22.
 */

public class FloatMenu extends FrameLayout {


    private FloatingActionButton mFloatButton;
    private LinearLayout mFirstMenu;
    private LinearLayout mSecondMenu;
    private boolean isOpen;
    private ValueAnimator animClose;
    private ValueAnimator animStart;
    private View mbgView;
    private OnMenuSelectListener onMenuSelectListener;

    public FloatMenu(Context context) {
        super(context);
        init(context);
    }

    public FloatMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.float_menu, this);
        mFloatButton = (FloatingActionButton) findViewById(R.id.floatbutton);
        mFirstMenu = (LinearLayout) findViewById(R.id.fristMunu);
        mSecondMenu = (LinearLayout) findViewById(R.id.secondMunu);
        mbgView = findViewById(R.id.bgView);
        mFirstMenu.setScaleX(0);
        mFirstMenu.setScaleY(0);
        mSecondMenu.setScaleX(0);
        mSecondMenu.setScaleY(0);
        mFirstMenu.setVisibility(GONE);
        mSecondMenu.setVisibility(GONE);
        isOpen = false;
        initListener();
    }

    private void initListener() {

        mFirstMenu.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onMenuSelectListener != null) {
                    onMenuSelectListener.onSelect(0);
                }
            }
        });
        mSecondMenu.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onMenuSelectListener != null) {
                    onMenuSelectListener.onSelect(1);
                }
            }
        });
        mbgView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                isOpen = false;
                animClose();
            }
        });

        mFloatButton.setOnTouchListener(new OnTouchListener() {
            int lastX, lastY;
            int dotx, doty;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                int ea = event.getAction();
                Log.i("TAG", "Touch:" + ea);
                switch (ea) {
                    case MotionEvent.ACTION_DOWN:
                        if (isOpen) {
//                            closeMenu();
//                            isOpen = false;
                            return false;
                        }
                        lastX = (int) event.getRawX();// 获取触摸事件触摸位置的原始X坐标
                        lastY = (int) event.getRawY();
                        dotx = lastX;
                        doty = lastY;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        int dx = (int) event.getRawX() - lastX;
                        int dy = (int) event.getRawY() - lastY;
                        int l = v.getLeft() + dx;
                        int b = v.getBottom() + dy;
                        int r = v.getRight() + dx;
                        int t = v.getTop() + dy;
                        // 下面判断移动是否超出屏幕
                        if (l < 0) {
                            l = 0;
                            r = l + v.getWidth();
                        }
                        if (t < 0) {
                            t = 0;
                            b = t + v.getHeight();
                        }
                        if (r > TDevice.getScreenWidth()) {
                            r = ((int) TDevice.getScreenWidth());
                            l = r - v.getWidth();
                        }
                        if (b > TDevice.getScreenHeight() - TDevice.dpToPixel(150)) {
                            b = (int) ((TDevice.getScreenHeight()) - TDevice.dpToPixel(150));
                            t = b - v.getHeight();
                        }
                        v.layout(l, t, r, b);
                        lastX = (int) event.getRawX();
                        lastY = (int) event.getRawY();
                   /* Toast.makeText(DraftButtonActivity.this,
                            "当前位置：" + l + "," + t + "," + r + "," + b,
                            Toast.LENGTH_SHORT).show();  */
                        v.postInvalidate();
                        break;
                    case MotionEvent.ACTION_UP:
                        int currx = (int) event.getRawX();
                        int curry = (int) event.getRawY();
                        Rect rect = new Rect(currx - 5, curry - 5, currx + 5, curry + 5);

                        if (rect.contains(dotx, doty)) {
                            boolean needOpen = !isOpen;
                            if (needOpen) {
                                mbgView.setVisibility(VISIBLE);
                                mFirstMenu.setVisibility(VISIBLE);
                                mSecondMenu.setVisibility(VISIBLE);
                                openMenu();
                            } else {
                                closeMenu();
                            }
                            isOpen = needOpen;
                        }
                        break;
                }

                return true;
            }

        });
    }

    public void closeMenu() {
        isOpen = false;
        animClose();
    }

    private void animClose() {
        if (animStart != null && animStart.isRunning()) {
            return;
        }
        animClose = ValueAnimator.ofInt(0, 100);
        animClose = ValueAnimator.ofFloat(0, 100);
        animClose.setDuration(300);
        animClose.setInterpolator(new AccelerateInterpolator());
        animClose.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Float value = (Float) animation.getAnimatedValue();
                mbgView.setAlpha(0.7f - 0.7f * value / 100f);
                mFirstMenu.setScaleX(1 - value / 100f);
                mFirstMenu.setScaleY(1 - value / 100f);
                mSecondMenu.setScaleX(1 - value / 100f);
                mSecondMenu.setScaleY(1 - value / 100f);
            }
        });
        animClose.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mbgView.setVisibility(GONE);
                mFirstMenu.setVisibility(GONE);
                mSecondMenu.setVisibility(GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animClose.start();
    }

    public void openMenu() {
        if (animClose != null && animClose.isRunning()) {
            return;
        }
        animStart = ValueAnimator.ofFloat(0, 100);
        animStart.setDuration(300);
        animStart.setInterpolator(new AccelerateInterpolator());
        animStart.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Float value = (Float) animation.getAnimatedValue();
                mbgView.setAlpha(0.7f * value / 100f);
                mFirstMenu.setScaleX(value / 100f);
                mFirstMenu.setScaleY(value / 100f);
                mSecondMenu.setScaleX(value / 100f);
                mSecondMenu.setScaleY(value / 100f);
            }
        });

        animStart.start();
    }


    public void setOnMenuSelectListener(OnMenuSelectListener onMenuSelectListener) {
        this.onMenuSelectListener = onMenuSelectListener;
    }

    public interface OnMenuSelectListener {

        void onSelect(int position);

    }
}
