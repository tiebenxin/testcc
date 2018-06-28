package com.lensim.fingerchat.hexmeet.ScreenManager;

/**
 * Created by 周哥 on 2016/10/11.
 */

public interface OnInitializedListener extends BaseManagerInterface {
    /**
     * 当服务启动并且数据加载完成时调用
     * 在ui线程调用
     */
    void onInitialized();
}
