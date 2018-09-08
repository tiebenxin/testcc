package com.lensim.fingerchat.fingerchat.ui.main;

import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import com.lensim.fingerchat.commons.base.BaseFragment;

/**
 * Created by LL130386 on 2018/8/8.
 */

public class PageModel {
    BaseFragment fragment; // 内容
    @StringRes
    int titleRes; // 标题
    @DrawableRes
    int iconRes; // 默认图标

    public PageModel(BaseFragment fragment, int titleRes, int iconRes) {
        this.fragment = fragment;
        this.titleRes = titleRes;
        this.iconRes = iconRes;
    }

    public BaseFragment getFragment() {
        return fragment;
    }

    public void setFragment(BaseFragment fragment) {
        this.fragment = fragment;
    }

    public int getTitleRes() {
        return titleRes;
    }

    public void setTitleRes(int titleRes) {
        this.titleRes = titleRes;
    }

    public int getIconRes() {
        return iconRes;
    }

    public void setIconRes(int iconRes) {
        this.iconRes = iconRes;
    }
}
