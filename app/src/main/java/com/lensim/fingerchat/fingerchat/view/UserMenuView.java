package com.lensim.fingerchat.fingerchat.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import com.lensim.fingerchat.fingerchat.R;
import com.lensim.fingerchat.fingerchat.databinding.ViewUserMenuBinding;

/**
 * Created by zm on 2018/5/24.
 */
public class UserMenuView extends FrameLayout{
    private ViewUserMenuBinding binding;

    private String title; // 标题
    private Drawable icon; // 图标
    private int titleSize; // 标题大小
    private int titleColor; // 标题颜色

    public UserMenuView(@NonNull Context context) {
        this(context, null, 0);
    }

    public UserMenuView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public UserMenuView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        // 获取自定义属性
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.UserMenuView);
        icon = ta.getDrawable(R.styleable.UserMenuView_left_icon);
        title = ta.getString(R.styleable.UserMenuView_title);
        titleSize = ta.getInteger(R.styleable.UserMenuView_title_size, 18);
        titleColor = ta.getInteger(R.styleable.UserMenuView_title_color, R.color.black_33);
        ta.recycle();

        init();
    }

    private void init() {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        binding = DataBindingUtil.inflate(inflater, R.layout.view_user_menu, this, true);

        // 图标
        if (icon != null) binding.ivLeft.setImageDrawable(icon);
        // 标题
        binding.tvTitle.setText(title);
        // 标题字体大小
        binding.tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, titleSize);
        // 标题字体颜色
        binding.tvTitle.setTextColor(ContextCompat.getColor(getContext(), titleColor));
    }

    /**
     * 设置左边图标
     * @param icon
     */
    public void setLeftIcon(@DrawableRes int icon) {
        binding.ivLeft.setImageResource(icon);
    }

    /**
     * 设置标题
     * @param title
     */
    public void setTitle(String title) {
        binding.tvTitle.setText(TextUtils.isEmpty(title) ? "" : title);
    }
}
