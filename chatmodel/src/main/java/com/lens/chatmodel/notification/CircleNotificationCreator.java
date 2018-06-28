package com.lens.chatmodel.notification;

import android.app.Application;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;
import com.lens.chatmodel.R;
import com.lensim.fingerchat.commons.helper.ContextHelper;

public class CircleNotificationCreator {

    private static int UNIQUE_REQUEST_CODE = 0;
    private final Application application;


    public CircleNotificationCreator() {
        application = ContextHelper.getApplication();
    }

    public android.app.Notification notifyCircleNotification(CircleNotification circleNotification) {

        if (circleNotification.getCount() <= 0) {
            return null;
        }


        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(application);
        notificationBuilder.setContentTitle("朋友圈更新");
        notificationBuilder.setContentText(getText(circleNotification));
       // notificationBuilder.setSubText(text);

        notificationBuilder.setTicker(getText(circleNotification));

        notificationBuilder.setSmallIcon(getSmallIcon());
       // notificationBuilder.setLargeIcon(getLargeIcon(message));
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

    private CharSequence getText(CircleNotification message) {
          return application.getString(R.string.circle_update,message.getCount());
    }

    private int getSmallIcon() {
        return R.drawable.ic_stat_chat;
    }


    private PendingIntent getIntent() {
        Intent backIntent = null;

//            backIntent = new Intent(application, LookupCommentActivity.class);

        return PendingIntent.getActivity(application, UNIQUE_REQUEST_CODE++,
                backIntent, PendingIntent.FLAG_ONE_SHOT);
    }

}
