package com.lens.chatmodel.notification;

import android.net.Uri;
import java.util.Collection;

/**
 * Created by 周哥 on 2016/10/11.
 */

public interface NotificationProvider<T extends NotificationItem> {

    /**
     * 通知列表
     * @return
     */
    Collection<T> getNotifications();

    /**
     * 是否能够清除通知栏
     * @return
     */
    boolean canClearNotifications();

    /**
     * 清除通知栏
     */
    void ClearNotifications();

    /**
     * 通知的声音
     * @return
     */
    Uri getSound();

    /**
     * 通知的音频类型
     * @return
     */
    int getStreamType();

    /**
     * 通知栏的图标
     * @return Resource id with icon for notification bar.
     */
    int getIcon();

}
