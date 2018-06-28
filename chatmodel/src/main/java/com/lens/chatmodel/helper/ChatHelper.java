package com.lens.chatmodel.helper;

import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.widget.ImageView;
import com.fingerchat.proto.message.BaseChat.MessageType;
import com.fingerchat.proto.message.Muc;
import com.fingerchat.proto.message.Muc.MOption;
import com.fingerchat.proto.message.Muc.MucAction;
import com.fingerchat.proto.message.Muc.MucItem;
import com.fingerchat.proto.message.Muc.Role;
import com.fingerchat.proto.message.MucChat;
import com.lens.chatmodel.ChatEnum.EActionType;
import com.lens.chatmodel.ChatEnum.EChatBgId;
import com.lens.chatmodel.ChatEnum.EChatType;
import com.lens.chatmodel.ChatEnum.EMessageType;
import com.lens.chatmodel.ChatEnum.ESureType;
import com.lens.chatmodel.R;
import com.lens.chatmodel.bean.UserBean;
import com.lens.chatmodel.bean.body.BodyEntity;
import com.lens.chatmodel.db.MucInfo;
import com.lens.chatmodel.interf.IChatRoomModel;
import com.lensim.fingerchat.commons.helper.ContextHelper;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;

/**
 * Created by LL130386 on 2018/1/18.
 */

public class ChatHelper {

    public static long SECOND = 1000;
    public static long MINUTE = SECOND * 60;
    public static long HOUR = MINUTE * 60;
    public static long DAY = HOUR * 24;

    public static boolean isGroupChat(int chatType) {
        if (chatType < 0) {
            return false;
        } else {
            if (chatType == EChatType.GROUP.ordinal()) {
                return true;
            } else {
                return false;
            }
        }
    }

    public static String getHint(EMessageType type, String text, boolean isSecret) {
        if (isSecret) {
            return "[密信]";
        } else {
            if (TextUtils.isEmpty(text)) {
                switch (type) {
                    case CONTACT:
                        return "[个人名片]";
                    case MAP:
                        return "[位置]";
                    case VOTE:
                        return "[投票]";
                    case SYSTEM:
                        return "[系统消息]";
                    case FACE:
                        return "[动态表情]";
                    default:
                        return "";
                }
            }
            switch (type) {
                case VOICE:
                    return "[语音]";
                case IMAGE:
                    return "[图片]";
                case VIDEO:
                    return "[视频]";
                case FACE:
                    return "[动态表情]";
                case CONTACT:
                    return "[个人名片]";
                case CARD:
                    return "[签到]";
                case MAP:
                    return "[位置]";
                case VOTE:
                    return "[投票]";
                case SYSTEM:
                    return "[系统消息]";
                case OA:
                    return "[OA消息]";
                case MULTIPLE:
                    return "[转发消息]";
                default:
                    return text;
            }
        }
    }


    public static String getUserNick(String nick, String userId) {
        return TextUtils.isEmpty(nick) ? userId : nick;
    }

    public static MessageType getMessageType(EMessageType type) {
        switch (type) {
            case TEXT:
                return MessageType.TEXT;
            case IMAGE:
                return MessageType.IMAGE;
            case VOICE:
                return MessageType.VOICE;
            case VIDEO:
                return MessageType.VIDEO;
            case MAP:
                return MessageType.MAP;
            case VOTE:
                return MessageType.VOTE;
            case FACE:
                return MessageType.FACE;
            case CONTACT:
                return MessageType.CONTACT;
            case CARD:
                return MessageType.CARD;
            case MULTIPLE:
                return MessageType.MULTIPLE;
            case INPUTING:
                return MessageType.INPUTING;
            case RECORDING:
                return MessageType.RECORDING;
            case OA:
                return MessageType.OA;
            case NOTICE:
                return MessageType.NOTICE;
            case ERROR:
                return MessageType.ERROR;
        }
        return MessageType.TEXT;
    }

    public static EMessageType getMessageType(MessageType type) {
        switch (type) {
            case TEXT:
                return EMessageType.TEXT;
            case IMAGE:
                return EMessageType.IMAGE;
            case VOICE:
                return EMessageType.VOICE;
            case VIDEO:
                return EMessageType.VIDEO;
            case MAP:
                return EMessageType.MAP;
            case VOTE:
                return EMessageType.VOTE;
            case FACE:
                return EMessageType.FACE;
            case CONTACT:
                return EMessageType.CONTACT;
            case CARD:
                return EMessageType.CARD;
            case MULTIPLE:
                return EMessageType.MULTIPLE;
            case INPUTING:
                return EMessageType.INPUTING;
            case RECORDING:
                return EMessageType.RECORDING;
            case OA:
                return EMessageType.OA;
            case NOTICE:
                return EMessageType.NOTICE;
            case ERROR:
                return EMessageType.ERROR;
        }
        return EMessageType.TEXT;
    }

    public static EActionType getActionType(MucAction action) {
        switch (action.getAction()) {
            case Create:
                return EActionType.CREATE;
            case Invite:
                return EActionType.INVITE;
            case Join:
                return EActionType.JOIN;
            case Kick:
                return EActionType.KICK;
            case Leave:
                return EActionType.LEAVE;
            case Destory:
                return EActionType.DESTROY;
            case ChangeRole:
                return EActionType.CHANGE_ROLE;
            case MQuery:
                return EActionType.MQUERY;
            case UpdateConfig:
                return EActionType.UPDATE_CONFIG;
            case Confirm:
                return EActionType.CONFIRM;
            case Revoke:
                return EActionType.REVOKE;
        }
        return null;
    }

    public static MucAction getActionType(EActionType action) {
        MucAction.Builder builder = MucAction.newBuilder();
        switch (action) {
            case CREATE:
                builder.setAction(MOption.Create);
            case INVITE:
                builder.setAction(MOption.Invite);
            case JOIN:
                builder.setAction(MOption.Join);
            case KICK:
                builder.setAction(MOption.Kick);
            case LEAVE:
                builder.setAction(MOption.Leave);
            case DESTROY:
                builder.setAction(MOption.Destory);
            case CHANGE_ROLE:
                builder.setAction(MOption.ChangeRole);
            case MQUERY:
                builder.setAction(MOption.MQuery);
            case UPDATE_CONFIG:
                builder.setAction(MOption.UpdateConfig);
        }
        return builder.build();
    }

    public static String getActionText(EActionType type, String userNick, String content) {
        String resultContent = "";
        switch (type) {
            case CREATE:
                resultContent = userNick + "创建了群";
                break;
            case UPDATE_CONFIG:
                resultContent = userNick + "修改了群资料";
                break;
        }
        return resultContent;
    }

    private static String getOptionalText(String text) {
        if ("".equals(text)) {
            return "";
        } else {
            return " (" + text + ")";
        }
    }

    public static String getTimeLength(int time, EMessageType type) {
        if (time <= 0) {
            return "0";
        } else {

            switch (type) {
                case VOICE:
                    int s = time;
                    return s + "\"";
                case VIDEO:
                    StringBuilder mFormatBuilder = new StringBuilder();
                    Formatter mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());
                    return mFormatter.format("%d.%d 秒", new Object[]{Integer.valueOf(time / 1000),
                        Integer.valueOf(time / 100 % 10)}).toString();
                default:
                    return "0";
            }
        }
    }

    /*
    * 获取聊天背景
    * */
    public static Drawable getChatBackGround(int backId) {
        EChatBgId chatBgId = EChatBgId.fromOrdinal(backId);
        if (chatBgId == null) {
            return ContextHelper.getDrawable(R.drawable.bg_chat_white);
        } else {
            switch (chatBgId) {
                case DEFAULT:
                    return ContextHelper.getDrawable(R.drawable.bg_chat_white);
                case FIRST:
                    return ContextHelper.getDrawable(R.drawable.background_1);
                case SECEND:
                    return ContextHelper.getDrawable(R.drawable.background_2);
                case THIRD:
                    return ContextHelper.getDrawable(R.drawable.background_3);
                case FORTH:
                    return ContextHelper.getDrawable(R.drawable.background_4);
                case FIFTH:
                    return ContextHelper.getDrawable(R.drawable.background_5);
                case SIXTH:
                    return ContextHelper.getDrawable(R.drawable.background_6);
                default:
                    return ContextHelper.getDrawable(R.drawable.bg_chat_white);

            }
        }
    }

    public static String getSecretText(EMessageType type) {
        switch (type) {
            case TEXT:
                return "文字密信";
            case VOICE:
                return "语音密信";
            case IMAGE:
                return "图片密信";
            case VIDEO:
                return "视频密信";
            case MAP:
                return "位置密信";
            default:
                return "其他密信";
        }
    }

    public static void destroySecretMsg(IChatRoomModel model) {
        if (model == null) {
            return;
        }
        BodyEntity entity = new BodyEntity(model.getBody());
        if (entity == null) {
            return;
        }
        entity.setBody("");
        entity.setSecret(true);
        model.setBody(BodyEntity.toJson(entity));
    }

    public static EMessageType getMessageTypeByString(String num) {
        switch (num) {
            case "1":
                return EMessageType.TEXT;
            case "2":
                return EMessageType.IMAGE;
            case "3":
                return EMessageType.VIDEO;
            case "4":
                return EMessageType.FACE;
            default:
                return EMessageType.TEXT;
        }
    }

    public static String getFavType(EMessageType type) {
        switch (type) {
            case TEXT:
                return "1";
            case IMAGE:
                return "2";
            case VIDEO:
                return "3";
            case FACE:
                return "4";
            default:
                return "1";
        }
    }

    /*
    * 有效时间一周
    * */
    public static boolean isTimeValid(long time) {
        long d = System.currentTimeMillis() - time;
        if (d <= 7 * DAY) {
            return true;
        }
        return false;
    }

    public static boolean isNeedConfirm(MucItem item) {
        if (item == null) {
            return false;
        }
        if (!isOwerMuc(item) && item.getNeedConfirm() == ESureType.YES.ordinal()) {
            return true;
        }
        return false;
    }

    public static boolean isOwerMuc(MucItem item) {
        if (item == null) {
            return false;
        }
        if (item.getPConfig() != null && item.getPConfig().getRole() == Role.Owner) {
            return true;
        }
        return false;
    }

    public static void setAuthenticationDrawable(UserBean userBean, ImageView imageView) {
        if (userBean == null || imageView == null) {
            return;
        }
        if (userBean.isValid()) {//已认证
            imageView.setImageResource(R.drawable.ic_authenticated);
        } else {
            if (userBean.isQuit()) {//已离职
                imageView.setImageResource(R.drawable.ic_quit);
            } else {//未认证
                imageView.setImageResource(R.drawable.ic_no_authenticate);
            }
        }
    }

    /*
    * 是否是系统账号
    * 如：小秘书
    * */
    public static boolean isSystemUser(String userId) {
        if (!TextUtils.isEmpty(userId)) {
            if (userId.equalsIgnoreCase("小秘书")) {
                return true;
            }
        }
        return false;
    }

    /*
    * 当前群成员是否是群主
    * */
    public static boolean isMucOwer(Muc.MucItem mucItem, String userId) {
        if (!TextUtils.isEmpty(mucItem.getCreator())) {
            if (mucItem.getCreator().equalsIgnoreCase(userId)) {
                return true;
            } else {
                return false;
            }
        } else {
            if (Muc.Role.Owner == mucItem.getPConfig().getRole()) {
                return true;
            } else {
                return false;
            }
        }
    }

    /*
    * 获取备注名
    * @param remark:备注名，可修改
    * @param nick:昵称，不可修改
    * @param userId:用户id，不可修改
    * */
    public static String getUserRemarkName(String remark, String nick, String userId) {
        if (!TextUtils.isEmpty(remark)) {
            return remark;
        } else if (!TextUtils.isEmpty(nick)) {
            return nick;
        } else {
            return userId;
        }
    }

    //检查是否是网络路径
    public static boolean checkHttpUrl(String url) {
        if (!TextUtils.isEmpty(url)) {
            if (url.contains("http") || url.contains("https")) {
                return true;
            }
        }
        return false;
    }
}
