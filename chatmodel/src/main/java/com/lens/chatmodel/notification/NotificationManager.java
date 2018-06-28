
package com.lens.chatmodel.notification;

import android.app.Application;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Handler;
import android.os.PowerManager;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import com.lens.chatmodel.R;
import com.lens.chatmodel.helper.SettingsManager;
import com.lens.chatmodel.interf.IChatRoomModel;
import com.lens.chatmodel.interf.OnCloseListener;
import com.lens.chatmodel.interf.OnInitializedListener;
import com.lens.chatmodel.interf.OnLoadListener;
import com.lens.chatmodel.manager.MessageManager;
import com.lensim.fingerchat.commons.helper.ContextHelper;
import com.lensim.fingerchat.commons.utils.L;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class NotificationManager implements OnInitializedListener,
    OnCloseListener, OnLoadListener, Runnable {

    public static final int PERSISTENT_NOTIFICATION_ID = 1;
    public static final int MESSAGE_NOTIFICATION_ID = 2;
    public static final int CURRENT_CHAT_MESSAGE_NOTIFICATION_ID = 3;
    public static final int CIRCLE_NOTIFICATION_ID = 4;
    public static final int SYSTEM_NOTIFICATION_ID = 5;
    private static final int BASE_NOTIFICATION_PROVIDER_ID = 0x10;

    private static final long VIBRATION_DURATION = 500;

    private long receiveTime = 0;
    private final static NotificationManager instance = new NotificationManager();

//    static {
//        instance = new NotificationManager();
//        MyApplication.getInstance().addManager(instance);
//    }

    private final Application application;
    private final android.app.NotificationManager notificationManager;
    //  private final PendingIntent clearNotifications;
    private final Handler handler;
    /**
     * 自定义震动
     */
    private final Runnable startVibration;

    /**
     * 停止震动
     */
    private final Runnable stopVibration;

    /**
     * 消息提供者
     */
    private final List<NotificationProvider<? extends NotificationItem>> providers;


    private final List<MessageNotification> messageNotifications;
    private NotificationCompat.Builder persistentNotificationBuilder;
    private MessageNotificationCreator messageNotificationCreator;
    private CircleNotificationCreator circleNotificationCreator;
    private int persistentNotificationColor;
    // private final PowerManager.WakeLock wakeLock;
    private final SystemNotificationCreator systemNotificationCreator;

    private NotificationManager() {
        this.application = ContextHelper.getApplication();

        notificationManager = (android.app.NotificationManager)
            application.getSystemService(Context.NOTIFICATION_SERVICE);

        PowerManager pm = (PowerManager) application.getSystemService(Context.POWER_SERVICE);
        //  wakeLock = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "bright");

        handler = new Handler();
        providers = new ArrayList<>();
        messageNotifications = new ArrayList<>();
//    clearNotifications = PendingIntent.getActivity(
//        application, 0, ClearNotifications.createIntent(application), 0);

        stopVibration = new Runnable() {
            @Override
            public void run() {
                handler.removeCallbacks(startVibration);
                handler.removeCallbacks(stopVibration);
                ((Vibrator) NotificationManager.this.application.
                    getSystemService(Context.VIBRATOR_SERVICE)).cancel();
            }
        };

        startVibration = new Runnable() {
            @Override
            public void run() {
                handler.removeCallbacks(startVibration);
                handler.removeCallbacks(stopVibration);
                ((Vibrator) NotificationManager.this.application
                    .getSystemService(Context.VIBRATOR_SERVICE)).cancel();
                ((Vibrator) NotificationManager.this.application
                    .getSystemService(Context.VIBRATOR_SERVICE))
                    .vibrate(VIBRATION_DURATION);
                handler.postDelayed(stopVibration, VIBRATION_DURATION);
            }
        };

        initPersistentNotification();

        messageNotificationCreator = new MessageNotificationCreator();
        circleNotificationCreator = new CircleNotificationCreator();
        systemNotificationCreator = new SystemNotificationCreator();
        persistentNotificationColor = application.getResources().getColor(R.color.primary);
    }

    public static NotificationManager getInstance() {
        return instance;
    }

    public static void addEffects(NotificationCompat.Builder notificationBuilder,
        IChatRoomModel messageItem) {
        if (messageItem == null) {
            return;
        }
        if (MessageManager.getInstance().getFirstNotification() || !SettingsManager
            .eventsFirstOnly()) {
            Uri sound = SettingsManager.eventsSound();
            boolean makeVibration = SettingsManager.eventsVibro();
            NotificationManager.getInstance().setNotificationDefaults(notificationBuilder,
                makeVibration, sound, AudioManager.STREAM_NOTIFICATION);
        }
    }

    private void initPersistentNotification() {
        L.i(NotificationManager.class.getSimpleName() + " initPersistentNotification");
        if (persistentNotificationBuilder == null) {
            persistentNotificationBuilder = new NotificationCompat.Builder(application);
        }
        persistentNotificationBuilder.setContentTitle("飞鸽互联");
//    persistentNotificationBuilder.setContentText(LensImUtil.getUserNick() + "登陆中");
        persistentNotificationBuilder.setWhen(System.currentTimeMillis());
        persistentNotificationBuilder.setTicker("飞鸽在后台运行");
        persistentNotificationBuilder.setAutoCancel(true);

//        persistentNotificationBuilder.setContentTitle(application.getString(R.string.app_name));
//        persistentNotificationBuilder.setDeleteIntent(clearNotifications);
//        persistentNotificationBuilder.setOngoing(false);
//        persistentNotificationBuilder.setWhen(System.currentTimeMillis());
//        persistentNotificationBuilder.setCategory(NotificationCompat.CATEGORY_SERVICE);
//        persistentNotificationBuilder.setPriority(NotificationCompat.PRIORITY_LOW);
//        persistentNotificationBuilder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

    }

    @Override
    public void onLoad() {
        L.i("加载所有收到的通知信息");
//        final Collection<MessageNotification> messageNotifications = new ArrayList<>();
//        Cursor cursor = NotificationTable.getInstance().list();
//        try {
//            if (cursor.moveToFirst()) {
//                do {
//                    messageNotifications.add(new MessageNotification(
//                            NotificationTable.getUserId(cursor),
//                            NotificationTable.getUser(cursor),
//                            NotificationTable.getText(cursor),
//                            NotificationTable.getTimeStamp(cursor),
//                            NotificationTable.getCount(cursor)));
//                } while (cursor.moveToNext());
//            }
//        } finally {
//            cursor.close();
//        }
//        Application.getInstance().runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                onLoaded(messageNotifications);
//            }
//        });
    }

    private void onLoaded(Collection<MessageNotification> messageNotifications) {
        this.messageNotifications.addAll(messageNotifications);
        for (MessageNotification messageNotification : messageNotifications) {
//      MessageManager.getInstance().openChat(
//          messageNotification.getUserId(),
//          messageNotification.getUser());
        }
    }

    @Override
    public void onInitialized() {
        //  application.addUIListener(OnAccountChangedListener.class, this);
        updateMessageNotification(null);
    }

    /**
     * 注册消息提供者
     */
    public void registerNotificationProvider(
        NotificationProvider<? extends NotificationItem> provider) {
        providers.add(provider);
    }

    /**
     * 更新指定消息提供者的消息
     */
    public <T extends NotificationItem> void updateNotifications(
        NotificationProvider<T> provider, T notify) {
        int id = providers.indexOf(provider);

        if (id == -1) {
            throw new IllegalStateException(
                "registerNotificationProvider() must be called from onLoaded() method.");
        }

        id += BASE_NOTIFICATION_PROVIDER_ID;
        Iterator<? extends NotificationItem> iterator = provider.getNotifications().iterator();

        if (!iterator.hasNext()) {
            notificationManager.cancel(id);
            return;
        }

        NotificationItem top;
        String ticker;

        if (notify == null) {
            top = iterator.next();
            ticker = null;
        } else {
            top = notify;
            ticker = top.getTitle();
        }

        Intent intent = top.getIntent();

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(
            application);

        notificationBuilder.setSmallIcon(provider.getIcon());
        notificationBuilder.setTicker(ticker);

        if (!provider.canClearNotifications()) {
            notificationBuilder.setOngoing(true);
        }

        notificationBuilder.setContentTitle(top.getTitle());
        notificationBuilder.setContentText(top.getText());

        notificationBuilder.setContentIntent(PendingIntent.getActivity(application, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT));

        if (ticker != null) {
            setNotificationDefaults(notificationBuilder, SettingsManager.eventsVibro(),
                provider.getSound(), provider.getStreamType());
        }

//    notificationBuilder.setDeleteIntent(clearNotifications);

        //notificationBuilder.setColor(ColorManager.getInstance().getAccountPainter().getDefaultMainColor());
        notificationBuilder.setColor(Color.BLUE);
        notify(id, notificationBuilder.build());
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

    public void startVibration() {
        handler.post(startVibration);
    }


    private void updateMessageNotification(IChatRoomModel ticker) {
        // updatePersistentNotification();

        Notification messageNotification = messageNotificationCreator
            .notifyMessageNotification(messageNotifications, ticker);
        long time = System.currentTimeMillis();
        if (messageNotification != null && time - receiveTime > 1000) {
            L.i(NotificationManager.class.getSimpleName() + " updateMessageNotification"
                + " notify");
            notify(MESSAGE_NOTIFICATION_ID, messageNotification);
            receiveTime = time;
        } else {
            L.i(NotificationManager.class.getSimpleName() + " updateMessageNotification"
                + " cancel");
            notificationManager.cancel(MESSAGE_NOTIFICATION_ID);
//      notificationManager.cancel(PERSISTENT_NOTIFICATION_ID);
        }
    }

    public void updateCircleNotification(CircleNotification notification) {
        Notification circleNotification = circleNotificationCreator
            .notifyCircleNotification(notification);
        if (circleNotification != null) {
            notify(SYSTEM_NOTIFICATION_ID, circleNotification);
        } else {
            notificationManager.cancel(SYSTEM_NOTIFICATION_ID);
        }
    }

    public void updateSystemNotification(SystemNotification notification) {
        Notification circleNotification = systemNotificationCreator
            .notifyCircleNotification(notification);
        if (circleNotification != null) {
            notify(CIRCLE_NOTIFICATION_ID, circleNotification);
            L.i(NotificationManager.class.getSimpleName() + " updateSystemNotification"
                + " notify");

        } else {
            L.i(NotificationManager.class.getSimpleName() + " updateSystemNotification"
                + " cancel");
            notificationManager.cancel(CIRCLE_NOTIFICATION_ID);
//      notificationManager.cancel(PERSISTENT_NOTIFICATION_ID);

        }
    }


    private void notify(int id, Notification notification) {
        L.i(this.getClass().getName() + "Notification: " + id
            + ", ticker: " + notification.tickerText
            + ", sound: " + notification.sound
            + ", vibro: " + (notification.defaults & Notification.DEFAULT_VIBRATE)
            + ", light: " + (notification.defaults & Notification.DEFAULT_LIGHTS));
        try {

            //wakeLock.acquire();
            notificationManager.notify(id, notification);
//            handler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    wakeLock.release();
//                }
//            },5000);
        } catch (SecurityException e) {
            L.e(this.getClass().getName(), e.getMessage());
        }
    }

    private MessageNotification getMessageNotification(String account, String user) {
        for (MessageNotification messageNotification : messageNotifications) {
            if (messageNotification.equals(account, user)) {
                return messageNotification;
            }
        }
        return null;
    }

    public void onMessageNotification(IChatRoomModel messageItem) {
//    MessageNotification messageNotification = getMessageNotification(
//        messageItem.getChat().getUserId(), messageItem.getChat().getUser());
//    if (messageNotification == null) {
//      messageNotification = new MessageNotification(
//          messageItem.getChat().getUserId(), messageItem.getChat().getUser(), null, null, 0);
//    } else {
//      messageNotifications.remove(messageNotification);
//    }
//    messageNotification.addMessage(messageItem.getHint());
//    messageNotifications.add(messageNotification);

        updateMessageNotification(messageItem);
    }

    public void onCurrentChatMessageNotification(IChatRoomModel messageItem) {
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(
            application);
        addEffects(notificationBuilder, messageItem);
        notify(CURRENT_CHAT_MESSAGE_NOTIFICATION_ID, notificationBuilder.build());
    }


    public void onMessageNotification() {
        updateMessageNotification(null);
    }

    public int getNotificationMessageCount(String account, String user) {
        MessageNotification messageNotification = getMessageNotification(
            account, user);
        if (messageNotification == null) {
            return 0;
        }
        return messageNotification.getCount();
    }

    public void removeMessageNotification(final String account, final String user) {
        MessageNotification messageNotification = getMessageNotification(account, user);
        if (messageNotification == null) {
            return;
        }
        messageNotifications.remove(messageNotification);
//        Application.getInstance().runInBackground(new Runnable() {
//            @Override
//            public void run() {
//                NotificationTable.getInstance().remove(account, user);
//            }
//        });
        updateMessageNotification(null);
    }


    public void onClearNotifications() {
        for (NotificationProvider<? extends NotificationItem> provider : providers) {
            if (provider.canClearNotifications()) {
                provider.ClearNotifications();
            }
        }
        messageNotifications.clear();
//        Application.getInstance().runInBackground(new Runnable() {
//            @Override
//            public void run() {
//                NotificationTable.getInstance().clear();
//            }
//        });
        updateMessageNotification(null);
    }


    @Override
    public void run() {
        handler.removeCallbacks(this);
        updateMessageNotification(null);
    }

    public Notification getPersistentNotification() {
        L.i(NotificationManager.class.getSimpleName() + " getPersistentNotification");
        return persistentNotificationBuilder.build();
    }

    @Override
    public void onClose() {
        notificationManager.cancelAll();
    }
}
