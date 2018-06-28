package com.lensim.fingerchat.commons.mvp.presenter;


import android.util.Log;
import com.lensim.fingerchat.commons.mvp.view.BaseMvpView;
import io.reactivex.disposables.CompositeDisposable;


/**
 * Created by ll147996 on 2018/2/1.
 * 所有 Presenter 的基类，并不强制实现这些方法，有需要在重写
 */

public class RxMvpPresenter<V extends BaseMvpView> extends BaseMvpPresenter<V> {

    protected CompositeDisposable mCompositeSubscription = new CompositeDisposable();


    /**
     * 取消订阅View
     */
    @Override
    public void onDetachMvpView() {
        mCompositeSubscription.clear();
        Log.d("mvp","P onDetachMvpView = ");
    }

}

