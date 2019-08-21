package com.lens.core.componet.mvp.presenter;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.lens.core.componet.mvp.view.BaseMvpView;

/**
 * Created by zm on 2018/3/7.
 * 所有Presenter的基类，并不强制实现这些方法，有需要在重写
 */
public class BaseMvpPresenter<V extends BaseMvpView> {

    /**
     * V层view
     */
    private V mView;

    /**
     * Presenter被创建后调用
     *
     * @param savedState 被意外销毁后重建后的Bundle
     */
    public void onCreatePresenter(@Nullable Bundle savedState) {

    }

    /**
     * 绑定View
     */
    public void onAttachMvpView(V mvpView) {
        mView = mvpView;
    }

    /**
     * 解除绑定View
     */
    public void onDetachMvpView() {
        mView = null;
    }

    /**
     * Presenter被销毁时调用
     */
    public void onDestroyPresenter() {

    }

    /**
     * 在Presenter意外销毁的时候被调用，它的调用时机和Activity、Fragment、View中的onSaveInstanceState
     * 时机相同
     *
     * @param outState
     */
    public void onSaveInstanceState(@Nullable Bundle outState) {

    }

    /**
     * 获取V层接口View
     *
     * @return 返回当前MvpView
     */
    public V getMvpView() {
        return mView;
    }
}
