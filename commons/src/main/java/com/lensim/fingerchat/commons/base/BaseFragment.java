package com.lensim.fingerchat.commons.base;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.View.OnClickListener;
import com.lensim.fingerchat.commons.helper.ContextHelper;
import com.lensim.fingerchat.commons.interf.IEventListener;
import com.lensim.fingerchat.commons.interf.IEventProduct;

/**
 * Created by LL130386 on 2017/11/14.
 */

public abstract class BaseFragment extends Fragment {


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        initData();
    }

    protected abstract void initView();

    protected void initData() {
    }


    public Context getContext() {
        return ContextHelper.getContext();
    }


    protected void notifyActivityResult(int requestCode, int resultCode, Intent data) {

    }

    public void notifyRequestResult(IEventProduct event) {

    }

    public void notifyResumeData() {

    }

    public void setEventListener(IEventListener l) {

    }

    /**
     * 从Fragment跳转到Activity
     */
    public void toActivity(Class<?> clazz) {
        toActivity(clazz, null);
    }

    public void toActivity(Class<?> clazz, Bundle bundle) {
        Intent intent = new Intent(getActivity(), clazz);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
    }

    public static void toActivity(Class<?> clazz, Bundle bundle, Context ctx) {
        Intent intent = new Intent(ctx, clazz);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        ctx.startActivity(intent);
    }


    public void toActivityForResult(Class<?> cls, Bundle bundle, int requestCode) {
        Intent intent = new Intent();
        intent.setClass(getActivity(), cls);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivityForResult(intent, requestCode);
    }


}
