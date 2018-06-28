package com.lensim.fingerchat.commons.mvp.factory;

import com.lensim.fingerchat.commons.mvp.presenter.BaseMvpPresenter;
import com.lensim.fingerchat.commons.mvp.view.BaseMvpView;

/**
 * Created by ll147996 on 2017/12/12.
 * Presenter 工厂接口
 */

public interface PresenterMvpFactory<V extends BaseMvpView,P extends BaseMvpPresenter<V>> {

    /**
     * 创建Presenter的接口方法
     * @return 需要创建的Presenter
     */
    P createMvpPresenter();
}
