package com.lens.chatmodel.notification;

import android.app.Application;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;
import com.lens.chatmodel.R;
import com.lensim.fingerchat.commons.helper.ContextHelper;


public class SystemNotificationCreator {

    private static int UNIQUE_REQUEST_CODE = 0;
    private final Application application;


    public SystemNotificationCreator() {
        application = ContextHelper.getApplication();
    }

    public android.app.Notification notifyCircleNotification(SystemNotification circleNotification) {


        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(application);
        notificationBuilder.setContentTitle("飞鸽提醒");
        notificationBuilder.setContentText(getText(circleNotification));
        notificationBuilder.setTicker(getText(circleNotification));
        notificationBuilder.setSmallIcon(getSmallIcon());
        notificationBuilder.setLargeIcon(BitmapFactory.decodeResource(application.getResources(),R.drawable.ic_logo));
        notificationBuilder.setWhen(circleNotification.getTimestamp().getTime());
        notificationBuilder.setColor(Color.BLUE);

        notificationBuilder.setContentIntent(getIntent());

        notificationBuilder.setCategory(NotificationCompat.CATEGORY_MESSAGE);
        notificationBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);

        //NotificationManager.addEffects(notificationBuilder);
        notificationBuilder.setDefaults(NotificationCompat.DEFAULT_ALL);

        return notificationBuilder.build();
    }

    private CharSequence getText(SystemNotification message) {
          return message.getMessage();
    }

    private int getSmallIcon() {
        return R.drawable.ic_stat_chat;
    }


    private PendingIntent getIntent() {
        Intent backIntent = null;

//            backIntent = new Intent(application, LoginActivity.class);

        return PendingIntent.getActivity(application, UNIQUE_REQUEST_CODE++,
                backIntent, PendingIntent.FLAG_ONE_SHOT);
    }

}
