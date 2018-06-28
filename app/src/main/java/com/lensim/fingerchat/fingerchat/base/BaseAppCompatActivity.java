package com.lensim.fingerchat.fingerchat.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.lensim.fingerchat.commons.mvp.presenter.BaseMvpPresenter;
import com.lensim.fingerchat.commons.mvp.view.BaseMvpActivity;
import com.lensim.fingerchat.commons.mvp.view.BaseMvpView;

/**
 * Created by zm on 2018/6/4.
 */
public abstract class BaseAppCompatActivity<V extends BaseMvpView, P extends BaseMvpPresenter<V>> extends BaseMvpActivity {
    protected Bundle savedInstanceState;
    protected View mContentView;

    protected abstract int getLayoutResID(); // 获取LayoutID

    protected abstract void initView(); // 初始化控件

    protected abstract void initListener(); // 初始化事件监听

    protected abstract void initData(); // 初始化数据

    public static void startActivity(Context context, Class<?> clazz) {
        startActivity(context, clazz, null);
    }

    public static void startActivity(Context context, Class<?> clazz, Bundle bundle) {
        Intent intent = new Intent();
        intent.setClass(context, clazz);
        if (bundle != null) intent.putExtras(bundle);
        context.startActivity(intent);
    }

    public static void startForResultActivity(Activity context, Class<?> clazz, int requestCode) {
        startForResultActivity(context, clazz, null, requestCode);
    }

    public static void startForResultActivity(Activity activity, Class<?> clazz, Bundle bundle, int requestCode) {
        Intent intent = new Intent();
        intent.setClass(activity, clazz);
        if (bundle != null) intent.putExtras(bundle);
        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.savedInstanceState = savedInstanceState;
        mContentView = getLayoutInflater().inflate(getLayoutResID(), null, false);
        setContentView(mContentView);

        handleBundle(getIntent());
        initView();
        initListener();
        initData();
    }

    /**
     * 发送Intent
     *
     * @param intent
     */
    protected void handleBundle(Intent intent) {

    }
}
