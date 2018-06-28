package com.lensim.fingerchat.hexmeet.ScreenManager;

/**
 * Created by 周哥 on 2016/10/11.
 */

public interface OnCloseListener extends BaseManagerInterface {
    /**
     * 当服务停止时调用（ui线程）
     */
    void onClose();
}
