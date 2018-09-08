package com.lens.chatmodel.manager;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.media.AudioManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import com.lens.chatmodel.helper.SettingsManager;
import com.lens.chatmodel.interf.IChatRoomModel;
import com.lens.chatmodel.notification.MessageNotification;
import com.lens.chatmodel.notification.MessageNotificationCreator;
import com.lensim.fingerchat.commons.helper.ContextHelper;
import com.lensim.fingerchat.commons.utils.L;
import com.lensim.fingerchat.data.login.UserInfo;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by LL130386 on 2018/2/8.
 * 通知栏管理类
 */

public class NotifyManager {

    public static final int MESSAGE_NOTIFICATION_ID = 1;
    private static final long VIBRATION_DURATION = 500;


    private static NotifyManager INSTANCE = new NotifyManager();
    NotificationManager notificationManager;
    Application application;
    private MessageNotificationCreator messageNotificationCreator;
    private final List<MessageNotification> messageNotifications;
    private long receiveTime = 0;
    private UserInfo mUserInfo;


    public static NotifyManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new NotifyManager();
        }
        return INSTANCE;
    }


    public NotifyManager() {
        application = ContextHelper.getApplication();
        notificationManager = (NotificationManager) application
            .getSystemService(Context.NOTIFICATION_SERVICE);
        messageNotificationCreator = new MessageNotificationCreator();
        messageNotifications = new ArrayList<>();
    }

    private MessageNotification getMessageNotification(String account, String user) {
        for (MessageNotification messageNotification : messageNotifications) {
            if (messageNotification.equals(account, user)) {
                return messageNotification;
            }
        }
        return null;
    }

    public void onMessageNotification(IChatRoomModel model) {
        mUserInfo = MessageManager.getInstance().getUserInfo();
        if (mUserInfo == null) {
            return;
        }
        MessageNotification notification = getMessageNotification(mUserInfo.getUserid(),
            model.getTo());
        if (notification == null) {
            notification = new MessageNotification(mUserInfo.getUserid(), model.getTo(), null, null,
                0);
        } else {
            messageNotifications.remove(notification);
        }
        notification.addMessage(model.getHint());
        messageNotifications.add(notification);
        updateMessageNotification(model);

    }

    private void updateMessageNotification(IChatRoomModel ticker) {
        Notification messageNotification = messageNotificationCreator
            .notifyMessageNotification(messageNotifications, ticker);
        long time = System.currentTimeMillis();
        if (messageNotification != null && time - receiveTime > 1000) {
            notify(MESSAGE_NOTIFICATION_ID, messageNotification);
            receiveTime = time;
        } else {
            notificationManager.cancel(MESSAGE_NOTIFICATION_ID);
        }
    }

    private void notify(int id, Notification notification) {
        L.i(this.getClass().getName() + "Notification: " + id
            + ", ticker: " + notification.tickerText
            + ", sound: " + notification.sound
            + ", vibro: " + (notification.defaults & Notification.DEFAULT_VIBRATE)
            + ", light: " + (notification.defaults & Notification.DEFAULT_LIGHTS));
        try {
            notificationManager.notify(id, notification);
        } catch (SecurityException e) {
            L.e(this.getClass().getName(), e.getMessage());
        }
    }

    public void addEffects(NotificationCompat.Builder notificationBuilder,
        IChatRoomModel messageItem) {
        if (messageItem == null) {
            return;
        }
        if (MessageManager.getInstance().getFirstNotification() || !SettingsManager
            .eventsFirstOnly()) {
            Uri sound = SettingsManager.eventsSound();
            boolean makeVibration = SettingsManager.eventsVibro();
            NotifyManager.getInstance().setNotificationDefaults(notificationBuilder,
                makeVibration, sound, AudioManager.STREAM_NOTIFICATION);
        }
    }

    public void setNotificationDefaults(NotificationCompat.Builder notificationBuilder,
        boolean vibration, Uri sound, int streamType) {
        notificationBuilder.setSound(sound, streamType);
        notificationBuilder.setDefaults(0);

        int defaults = 0;
        if (vibration) {
            defaults |= Notification.DEFAULT_VIBRATE;
        }

        defaults |= Notification.DEFAULT_LIGHTS;
        notificationBuilder.setDefaults(defaults);
    }

    public void removeMessageNotification(final String account, final String user) {
        MessageNotification messageNotification = getMessageNotification(account, user);
        if (messageNotification == null) {
            return;
        }
        messageNotifications.remove(messageNotification);
        updateMessageNotification(null);
    }

    /*
    * 清空所有notice
    * */
    public void clearNotification() {
        if (messageNotifications != null && messageNotifications.size() > 0) {
            messageNotifications.clear();
            updateMessageNotification(null);
        }
    }


}
