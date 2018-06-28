package com.lens.chatmodel.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import com.lens.chatmodel.R;
import com.lens.chatmodel.interf.OnSwitchListener;

/**
 * Created by LL130386 on 2018/3/14.
 */

public class SwitchButton extends RelativeLayout {

    public static final int OPEN = 1;
    public static final int CLOSE = 0;

    private int defaultStatus;

    private int currentStatus;
    private OnSwitchListener onSwitchListener;

    private TypedArray typedArray;

    private RelativeLayout relativeLayout;
    private View view;

    public SwitchButton(Context context) {
        super(context);
    }

    public SwitchButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.layout_switch, this);
        typedArray = context.obtainStyledAttributes(attrs, R.styleable.SwitchButton);
        initView();
        init();
    }

    /**
     * 初始化控件
     */
    private void initView() {
        relativeLayout = (RelativeLayout) findViewById(R.id.relative_layout_bg);
        view = findViewById(R.id.view_scroll);
    }

    /**
     * 初始化操作
     */
    private void init() {
        defaultStatus = typedArray.getInt(R.styleable.SwitchButton_defaultStatus, 1);
        if (defaultStatus == 0) {
            relativeLayout.setBackgroundResource(R.drawable.bg_switch_bottom);
            view.setBackgroundResource(R.drawable.bg_switch_top);
            setViewLocation(view, RelativeLayout.ALIGN_PARENT_LEFT);
            currentStatus = CLOSE;
        } else if (defaultStatus == 1) {
            relativeLayout.setBackgroundResource(R.drawable.bg_switch_bottom_open);
            view.setBackgroundResource(R.drawable.bg_switch_top_open);
            setViewLocation(view, RelativeLayout.ALIGN_PARENT_RIGHT);
            currentStatus = OPEN;
        }
    }

    private void setViewLocation(View view, int location) {
        LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
        if (location == RelativeLayout.ALIGN_PARENT_LEFT) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                layoutParams.removeRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            }
        } else if (location == RelativeLayout.ALIGN_PARENT_RIGHT) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                layoutParams.removeRule(RelativeLayout.ALIGN_PARENT_LEFT);
            }
        }
        layoutParams.addRule(location);
        view.setLayoutParams(layoutParams);
    }

    /**
     * 获取当前的状态
     */
    public int getCurrentStatus() {
        return currentStatus;
    }

    public boolean isChecked() {
        if (currentStatus == OPEN) {
            return true;
        } else {
            return false;
        }
    }

    public void setChecked(boolean isChecked) {
        changeStatus(!isChecked);
    }

    /**
     * 设置状态变化监听
     */
    public void setOnSwitchListener(OnSwitchListener onSwitchListener) {
        this.onSwitchListener = onSwitchListener;
    }

    /**
     * 关闭按钮
     */
    private void closeButton() {
        relativeLayout.setBackgroundResource(R.drawable.bg_switch_bottom);
        view.setBackgroundResource(R.drawable.bg_switch_top);
        setViewLocation(view, RelativeLayout.ALIGN_PARENT_LEFT);
        currentStatus = CLOSE;
    }

    /**
     * 打开按钮
     */
    private void openButton() {
        relativeLayout.setBackgroundResource(R.drawable.bg_switch_bottom_open);
        view.setBackgroundResource(R.drawable.bg_switch_top_open);
        setViewLocation(view, RelativeLayout.ALIGN_PARENT_RIGHT);
        currentStatus = OPEN;
    }

    /**
     * 改变状态
     */
    private void changeStatus(boolean isChecked) {
        if (isChecked) {
            closeButton();
        } else if (!isChecked) {
            openButton();
        }
        if (onSwitchListener != null) {
            onSwitchListener.onSwitchChange();  //调用监听改变时候处理逻辑的函数
        }
    }

    /**
     * 触摸事件
     * 触摸一下，改变按钮的状态
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                /**
                 * 改变状态
                 */
                changeStatus(isChecked());
                break;
            default:
                break;
        }
        return super.onTouchEvent(event);
    }

}
