package com.lensim.fingerchat.commons.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import com.lensim.fingerchat.commons.mvp.factory.PresenterMvpFactory;
import com.lensim.fingerchat.commons.mvp.factory.PresenterMvpFactoryImpl;
import com.lensim.fingerchat.commons.mvp.presenter.BaseMvpPresenter;
import com.lensim.fingerchat.commons.mvp.proxy.BaseMvpProxy;
import com.lensim.fingerchat.commons.mvp.proxy.PresenterProxyInterface;
import com.lensim.fingerchat.commons.mvp.view.BaseMvpView;


/**
 * Created by ll147996 on 2017/12/12.
 * 使用代理模式来代理 Presenter 的创建、销毁、绑定、解绑以及Presenter的状态保存,
 * 其实就是管理 Presenter 的生命周期
 */
public abstract class BaseMVPFragment<V extends BaseMvpView, P extends BaseMvpPresenter<V>> extends FGFragment implements PresenterProxyInterface<V, P> {

    /**
     * 调用onSaveInstanceState时存入Bundle的key
     */
    private static final String PRESENTER_SAVE_KEY = "presenter_save_key";
    /**
     * 创建被代理对象,传入默认Presenter的工厂
     */
    private BaseMvpProxy<V, P> mProxy = new BaseMvpProxy<>(PresenterMvpFactoryImpl.<V, P>createFactory(getClass()));

    @Override
    @SuppressWarnings("unchecked")
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState != null){
            mProxy.onRestoreInstanceState(savedInstanceState);
        }
        mProxy.onCreate((V) this);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    protected abstract void initView();

    @Override
    @SuppressWarnings("unchecked")
    public void onResume() {
        super.onResume();
        mProxy.onResume((V) this);
        Log.d("perfect-mvp","V onResume");
//        mProxy.onResume((V) this);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mProxy.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBundle(PRESENTER_SAVE_KEY,mProxy.onSaveInstanceState());
    }





    /**
     * 可以实现自己PresenterMvpFactory工厂
     *
     * @param presenterFactory PresenterFactory类型
     */
    @Override
    public void setPresenterFactory(PresenterMvpFactory<V, P> presenterFactory) {
        mProxy.setPresenterFactory(presenterFactory);
    }


    /**
     * 获取创建Presenter的工厂
     *
     * @return PresenterMvpFactory类型
     */
    @Override
    public PresenterMvpFactory<V, P> getPresenterFactory() {
        return mProxy.getPresenterFactory();
    }

    /**
     * 获取Presenter
     * @return P
     */
    @Override
    public P getMvpPresenter() {
        return mProxy.getMvpPresenter();
    }
}
