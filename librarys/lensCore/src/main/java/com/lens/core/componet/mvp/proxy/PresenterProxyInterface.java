package com.lens.core.componet.mvp.proxy;

import com.lens.core.componet.mvp.factory.PresenterMvpFactory;
import com.lens.core.componet.mvp.presenter.BaseMvpPresenter;
import com.lens.core.componet.mvp.view.BaseMvpView;

/**
 * Created by zm on 2018/3/7.
 * 代理接口
 */
public interface PresenterProxyInterface<V extends BaseMvpView, P extends BaseMvpPresenter<V>> {

    /**
     * 设置创建Presenter的工厂
     *
     * @param presenterFactory PresenterFactory类型
     */
    void setPresenterFactory(PresenterMvpFactory<V, P> presenterFactory);

    /**
     * 获取Presenter的工厂类
     *
     * @return 返回PresenterMvpFactory类型
     */
    PresenterMvpFactory<V, P> getPresenterFactory();

    /**
     * 获取创建的Presenter
     *
     * @return 指定类型的Presenter
     */
    P getMvpPresenter();
}
