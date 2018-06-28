package com.lensim.fingerchat.commons.mvp.view;

/**
 * Created by ll147996 on 2017/12/12.
 * 有网络请求显示 Process 的 view 层接口的基类
 */

public interface ProcessMvpView extends BaseMvpView {

    /**
     * 显示进度条
     *
     * @param msg 进度条加载内容
     */
    void showProgress(String msg, boolean canCanceled);

    /**
     * 隐藏进度条
     */
    void dismissProgress();

    /**
     * 显示加载错误
     *
     * @param err 错误内容
     */
    void showErr(String err);

    /**
     * 重新加载
     */
    void reload();

    /**
     * 上拉加载
     */
    void load();

    /**
     * 下拉刷新
     */
    void refresh();
}
