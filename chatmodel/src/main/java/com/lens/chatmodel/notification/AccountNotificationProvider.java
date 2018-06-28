package com.lens.chatmodel.notification;

/**
 * Created by 周哥 on 2016/10/11.
 */

public interface AccountNotificationProvider<T extends AccountNotificationItem> extends NotificationProvider<T> {

    void clearAccountNotifications(String account);
}
