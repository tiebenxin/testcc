package com.lensim.fingerchat.commons.base;

import com.lensim.fingerchat.commons.dialog.CommenProgressDialog;
import com.lensim.fingerchat.commons.mvp.view.ProcessMvpView;


/**
 * date on 2017/12/20
 * author ll147996
 * describe 对有网络请求显示 Process 的做统一处理
 */

public abstract class FGFragment extends BaseFragment implements ProcessMvpView,
    NetworkRequestListener {

    private CommenProgressDialog mProgressDialog;

    @Override
    public void showProgress(String message, boolean canCanceled) {
        if (!getActivity().isFinishing() && mProgressDialog != null && mProgressDialog
            .isShowing()) {
            return;
        }
        mProgressDialog = new CommenProgressDialog(getActivity(), com.lensim.fingerchat.commons.R.style.LoadingDialog, message);
        mProgressDialog.setCanceledOnTouchOutside(canCanceled);
        mProgressDialog.show();

    }

    @Override
    public void dismissProgress() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void showErr(String err) {

    }

    @Override
    public void reload() {

    }

    @Override
    public void load() {

    }

    @Override
    public void refresh() {

    }


    //没有网络
    @Override
    public void noNetwork() {

    }

    //开始请求
    @Override
    public void start(boolean isShowProgress) {
        if (isShowProgress) {
            showProgress("正在加载", false);
        }
    }

    //请求结束
    @Override
    public void end() {
        dismissProgress();
    }

    //网络中断，超时
    @Override
    public void interruptedNetwork() {

    }

}
