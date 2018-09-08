package com.lens.core.componet.statelayout;

import android.text.TextUtils;

import java.util.UUID;

/**
 * Created by zm on 2018/4/12.
 */

public class StatePresenter implements IState {


    protected String lastFlag;

    private String getRandomFlag() {
        return "" + System.currentTimeMillis() + UUID.randomUUID();
    }

    public String updateFlag() {
        lastFlag = getRandomFlag();
        return lastFlag;
    }

    public boolean checkFlag(String flag) {
        return TextUtils.isEmpty(lastFlag) || lastFlag.equals(flag);
    }

    public static boolean checkFlag(StatePresenter statePresenter, String flag) {
        if(statePresenter == null) {
            return true;
        }
        return TextUtils.isEmpty(statePresenter.lastFlag) || statePresenter.lastFlag.equals(flag);
    }

    @Override
    public void onLoading(String flag) {
        lastFlag = flag;
    }

    @Override
    public void onEmpty(String flag) {

    }

    @Override
    public void onError(String flag) {

    }

    @Override
    public void onSucceed(String flag) {

    }
}
