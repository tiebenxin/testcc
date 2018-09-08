package com.lens.core.componet.mvp.factory;

import com.lens.core.componet.mvp.presenter.BaseMvpPresenter;
import com.lens.core.componet.mvp.view.BaseMvpView;

/**
 * Created by zm on 2018/3/6.
 * Presenter工厂接口
 */
public interface PresenterMvpFactory<V extends BaseMvpView,P extends BaseMvpPresenter<V>> {

    /**
     * 创建Presenter的接口方法
     * @return 需要创建的Presenter
     */
    P createMvpPresenter();
}
