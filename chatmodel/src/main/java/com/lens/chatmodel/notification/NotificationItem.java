package com.lens.chatmodel.notification;

import android.content.Intent;

/**
 * Created by 周哥 on 2016/10/11.
 */

public interface NotificationItem {
    /**
     * 点击通知跳转的Intent
     * @return
     */
    Intent getIntent();

    String getTitle();

    String getText();
}
