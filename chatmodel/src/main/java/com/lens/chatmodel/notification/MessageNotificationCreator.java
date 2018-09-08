package com.lens.chatmodel.notification;

import android.app.Application;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.StyleSpan;
import com.fingerchat.proto.message.Muc.MucItem;
import com.lens.chatmodel.ChatEnum.EChatType;
import com.lens.chatmodel.ChatEnum.ESureType;
import com.lens.chatmodel.R;
import com.lens.chatmodel.bean.UserBean;
import com.lens.chatmodel.bean.message.RecentMessage;
import com.lens.chatmodel.db.MucInfo;
import com.lens.chatmodel.db.ProviderChat;
import com.lens.chatmodel.db.ProviderUser;
import com.lens.chatmodel.helper.SettingsManager;
import com.lens.chatmodel.interf.IChatRoomModel;
import com.lens.chatmodel.manager.NotifyManager;
import com.lens.chatmodel.ui.message.ChatActivity;
import com.lensim.fingerchat.commons.helper.ContextHelper;
import com.lensim.fingerchat.commons.router.ActivityPath;
import com.lensim.fingerchat.commons.router.ActivitysRouter;
import com.lensim.fingerchat.commons.utils.StringUtils;
import java.util.List;

public class MessageNotificationCreator {

    private static int UNIQUE_REQUEST_CODE = 0;
    private final Application application;
    private List<MessageNotification> messageNotifications;

    public MessageNotificationCreator() {
        application = ContextHelper.getApplication();
    }

    public android.app.Notification notifyMessageNotification(
        List<MessageNotification> messageNotifications,
        IChatRoomModel messageItem) {
        this.messageNotifications = messageNotifications;

        if (messageNotifications.isEmpty()) {
            return null;
        }

        int messageCount = 0;

        for (MessageNotification messageNotification : messageNotifications) {
            messageCount += messageNotification.getCount();
        }

        MessageNotification message = messageNotifications.get(messageNotifications.size() - 1);

        boolean showText = SettingsManager.eventsShowText();
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(
            application);
        notificationBuilder.setContentTitle(getTitle(message, messageCount, messageItem));
        notificationBuilder.setContentText(getText(message, showText, messageItem));

        notificationBuilder.setTicker(getText(message, showText, messageItem));

        notificationBuilder.setSmallIcon(getSmallIcon());
        notificationBuilder
            .setLargeIcon(
                BitmapFactory.decodeResource(application.getResources(), R.drawable.ic_logo));
        notificationBuilder.setWhen(message.getTimestamp().getTime());
        notificationBuilder.setColor(Color.BLUE);
        notificationBuilder.setStyle(getStyle(message, messageCount, showText, messageItem));

        notificationBuilder.setContentIntent(getIntent(message));

        notificationBuilder.setCategory(NotificationCompat.CATEGORY_MESSAGE);
        notificationBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);

        NotifyManager.getInstance().addEffects(notificationBuilder, messageItem);

        return notificationBuilder.build();
    }

    private CharSequence getTitle(MessageNotification message, int messageCount,
        IChatRoomModel model) {
        if (isFromOneContact()) {
            return getSingleContactTitle(message, messageCount, model);
        } else {
            return getMultiContactTitle(messageCount);
        }
    }

    private CharSequence getSingleContactTitle(MessageNotification message, int messageCount,
        IChatRoomModel model) {
        if (messageCount > 1) {
            return application.getString(R.string.chat_messages_from_contact,
                messageCount, getContactName(message, model));
        } else {
            return getContactName(message, model);
        }
    }

    private String getContactName(MessageNotification message, IChatRoomModel model) {
        if (model != null) {
            if (model.isGroupChat()) {
                String groupName = model.getGroupName();
                if (TextUtils.isEmpty(groupName)) {
                    groupName = model.getTo();
                }
                return groupName;
            } else {
                return !TextUtils.isEmpty(model.getNick()) ? model.getNick() : model.getTo();
            }
        } else {
            return message.getUser();
        }
    }

    private CharSequence getMultiContactTitle(int messageCount) {
        String messageText = getTextForMessages(messageCount);
        String contactText = application.getString(R.string.chat_contact_quantity_1);
        return application.getString(R.string.chat_status,
            messageCount, messageText, messageNotifications.size(), contactText);
    }

    private String getTextForMessages(int messageCount) {
        //return "联系人";
        return StringUtils.getQuantityString(
            application.getResources(), R.array.chat_message_quantity, messageCount);
    }

    private CharSequence getText(MessageNotification message, boolean showText,
        IChatRoomModel model) {
        if (isFromOneContact()) {
            if (showText) {
                return message.getText();
            } else {
                return null;
            }
        } else {
            return getContactNameAndMessage(message, showText, model);
        }
    }

    private int getSmallIcon() {
        return R.drawable.ic_stat_chat;
    }

//    private android.graphics.Bitmap getLargeIcon(MessageNotification message) {
//        if (isFromOneContact()) {
//            if (MUCManager.getInstance().hasRoom(message.getUserId(), message.getUser())) {
//                return AvatarManager.getInstance().getRoomBitmap(message.getUser());
//            } else {
//                return AvatarManager.getInstance().getUserBitmap(message.getUser());
//            }
//        }
//        return null;
//    }

    private boolean isFromOneContact() {
        if (messageNotifications != null && messageNotifications.size() > 0) {
            return messageNotifications.size() == 1;
        } else {
            return false;
        }
    }

    private NotificationCompat.Style getStyle(MessageNotification message, int messageCount,
        boolean showText, IChatRoomModel model) {

        String text = "";

        if (isFromOneContact()) {
            NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();

            bigTextStyle.setBigContentTitle(getSingleContactTitle(message, messageCount, model));
            if (showText) {
                bigTextStyle.bigText(message.getText());
            }

            bigTextStyle.setSummaryText(text);

            return bigTextStyle;
        } else {
            return getInboxStyle(messageCount, text, model);
        }
    }

    private NotificationCompat.Style getInboxStyle(int messageCount, String accountName,
        IChatRoomModel model) {
        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();

        inboxStyle.setBigContentTitle(getMultiContactTitle(messageCount));

        for (int i = 1; i <= messageNotifications.size(); i++) {
            MessageNotification messageNotification = messageNotifications
                .get(messageNotifications.size() - i);

            boolean showTextForThisContact
                = SettingsManager.eventsShowText();
            inboxStyle
                .addLine(
                    getContactNameAndMessage(messageNotification, showTextForThisContact, model));
        }

        inboxStyle.setSummaryText(accountName);

        return inboxStyle;
    }

    private Spannable getContactNameAndMessage(MessageNotification messageNotification,
        boolean showText, IChatRoomModel model) {
        String userName = getContactName(messageNotification, model);

        Spannable spannableString;
        if (showText) {
            String contactAndMessage = application.getString(
                R.string.chat_contact_and_message, userName, messageNotification.getText());
            spannableString = new SpannableString(contactAndMessage);

        } else {
            spannableString = new SpannableString(userName);
        }

        spannableString.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, userName.length(),
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableString;
    }

    private PendingIntent getIntent(MessageNotification message) {
        Intent backIntent = null;

        if (isFromOneContact()) {
            UserBean bean = (UserBean) ProviderUser
                .selectRosterSingle(ContextHelper.getContext(), message.getUser());
            if (bean != null) {//私聊
                backIntent = ChatActivity
                    .createChatIntent(application, bean);
            } else {//群聊
                MucItem mucItem = MucInfo
                    .selectMucInfoSingle(ContextHelper.getContext(), message.getUser());
                int topFlag = ProviderChat
                    .getTopFlag(ContextHelper.getContext(), message.getUser());
                if (mucItem != null) {
                    int bgId = 0;
                    int disturb = 0;
                    if (mucItem.getPConfig() != null) {
                        if (!TextUtils.isEmpty(mucItem.getPConfig().getChatBg())) {
                            bgId = Integer.parseInt(mucItem.getPConfig().getChatBg());
                        }
                        if (mucItem.getPConfig().getNoDisturb() > 0) {
                            disturb = mucItem.getPConfig().getNoDisturb();
                        }
                    }
                    backIntent = ChatActivity
                        .createChatIntent(application, mucItem.getMucid(), mucItem.getMucname(),
                            EChatType.GROUP.ordinal(), bgId, disturb, topFlag);
                } else {
                    backIntent = ActivitysRouter.getInstance().invoke(application,
                        ActivityPath.ACTIVITY_MAIN_PATH);
                    backIntent.putExtra("page",0);
                    backIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                }
            }
        } else {
            backIntent = ActivitysRouter.getInstance().invoke(application,
                ActivityPath.ACTIVITY_MAIN_PATH);
            backIntent.putExtra("page",0);
            backIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }

        return PendingIntent.getActivity(application, UNIQUE_REQUEST_CODE++,
            backIntent, PendingIntent.FLAG_ONE_SHOT);
    }

}
