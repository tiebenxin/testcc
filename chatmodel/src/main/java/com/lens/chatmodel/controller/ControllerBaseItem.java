package com.lens.chatmodel.controller;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.lens.chatmodel.interf.IController;

/**
 * Created by LL130386 on 2017/11/25.
 */

public abstract class ControllerBaseItem<T> implements IController {

    protected Context mContext;
    protected int viewID;
    protected View view;
    private T model;
    private int positionInList;

    public ControllerBaseItem(Context context, int viewId) {
        mContext = context;
        viewID = viewId;
    }

    public View getView() {
        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            view = inflater.inflate(viewID, null);
            loadControls();
            initControls();
        }
        return view;
    }

    @Override
    public void setModel(Object o, int postion) {
        model = (T) o;
        positionInList = postion;
        showData(model);
    }


    public T getModel() {
        return model;
    }

    protected void loadControls() {
    }

    protected void initControls() {
    }

    public void showData(T t) {
    }

}
