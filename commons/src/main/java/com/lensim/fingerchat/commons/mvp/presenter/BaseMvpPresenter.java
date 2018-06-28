package com.lensim.fingerchat.commons.mvp.presenter;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;



import com.lensim.fingerchat.commons.mvp.view.BaseMvpView;



/**
 * Created by ll147996 on 2017/12/12.
 * 所有 Presenter 的基类，并不强制实现这些方法，有需要在重写
 */

public class BaseMvpPresenter<V extends BaseMvpView> {

    /**
     * V层view
     */
    protected V mView;

    /**
     * Presenter被创建后调用
     * @param savedState 被意外销毁后重建后的Bundle
     */
    public void onCreatePersenter(@Nullable Bundle savedState) {
        Log.d("mvp","P onCreatePersenter = ");
    }


    /**
     * 绑定View
     */
    public void onAttachMvpView(V mvpView) {
        mView = mvpView;
        Log.d("mvp","P onResume");
    }

    /**
     * 解除绑定View
     */
    public void onDetachMvpView() {
        mView = null;
        Log.d("mvp","P onDetachMvpView = ");
    }

    /**
     * Presenter被销毁时调用
     */
    public void onDestroyPersenter() {
        Log.d("mvp","P onDestroy = ");
    }

    /**
     * 在Presenter意外销毁的时候被调用，它的调用时机和Activity、Fragment、View中的onSaveInstanceState
     * 时机相同
     */
    public void onSaveInstanceState(Bundle outState) {
        Log.d("mvp","P onSaveInstanceState = ");
    }

    /**
     * 获取V层接口View
     * @return 返回当前MvpView
     */
    public V getMvpView() {
        return mView;
    }

    /**
     * 是否已经关联View
     */
    protected boolean isViewAttached() {
        if (mView == null) Log.e("getMvpView1","mView == null");
        return mView != null;
    }

}

