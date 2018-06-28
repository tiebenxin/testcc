package com.lens.chatmodel;


/**
 * Created by LL130386 on 2017/12/6.
 */

public class ChatEnum {

    /*
    * 聊天布局类型
    * */
    public enum EChatCellLayout {
        TEXT_RECEIVED(R.layout.chatcell_text_received),
        TEXT_SEND(R.layout.chatcell_text_send),

        IMAGE_RECEIVED(R.layout.chatcell_picture_received),
        IMAGE_SEND(R.layout.chatcell_picture_send),

        VOICE_RECEIVED(R.layout.chatcell_voice_received),
        VOICE_SEND(R.layout.chatcell_voice_sent),

        VIDEO_RECEIVED(R.layout.chatcell_video_received),
        VIDEO_SEND(R.layout.chatcell_video_sent),

        MAP_RECEIVED(R.layout.chatcell_map_received),
        MAP_SEND(R.layout.chatcell_map_send),

        VOTE_RECEIVED(R.layout.chatcell_vote_received),
        VOTE_SEND(R.layout.chatcell_vote_sent),

        EMOTICON_RECEIVED(R.layout.chatcell_picture_received),
        EMOTICON_SEND(R.layout.chatcell_picture_send),

        BUSINESS_CARD_RECEIVED(R.layout.chatcell_card_received),
        BUSINESS_CARD_SEND(R.layout.chatcell_card_send),

        NOTICE_ACTION(R.layout.chatcell_picture_received),

        WORK_LOGIN_RECEIVED(R.layout.chatcell_picture_received),
        WORK_LOGIN_SEND(R.layout.chatcell_picture_send),

        MULTI_RECEIVED(R.layout.chatcell_multi_received),
        MULTI_SEND(R.layout.chatcell_multi_sent),

        SECRET(R.layout.chatcell_secret),

        CHAT_ACTION(R.layout.chatcell_action),

        NOTICE(R.layout.chatcell_oa_received),

        OA(R.layout.chatcell_oa_received),

        SYSTEM(R.layout.chatcell_system_notice_received);


        public final int LayoutId;

        EChatCellLayout(int layoutId) {
            this.LayoutId = layoutId;
        }

        public static EChatCellLayout fromOrdinal(int ordinal) {
            EChatCellLayout result = null;
            for (EChatCellLayout item : EChatCellLayout.values()) {
                if (item.ordinal() == ordinal) {
                    result = item;
                    break;
                }
            }
            if (result == null) {
                throw new IllegalArgumentException("EChatCellLayout - fromOrdinal");
            }
            return result;
        }

        public static EChatCellLayout fromLayoutId(int layoutId) {
            EChatCellLayout result = null;
            for (EChatCellLayout item : EChatCellLayout.values()) {
                if (item.LayoutId == layoutId) {
                    result = item;
                    break;
                }
            }
            if (result == null) {
                throw new IllegalArgumentException("EChatCellLayout - fromLayoutId");
            }
            return result;
        }

        public static int size() {
            return values().length;
        }
    }


    /*
   * 消息类型
   * */
    public enum EMessageType {
        TEXT(0),
        IMAGE(1),
        VOICE(2),
        VIDEO(3),
        MAP(4),
        VOTE(5),
        FACE(6),//GIF表情
        CONTACT(7),//名片
        NOTICE(8),//取消类型(时间戳,  撤销,  投票提示)
        CARD(9),//签到打卡
        ERROR(10),
        MULTIPLE(11),//合并转发
        RECORDING(12),
        INPUTING(13),
        OA(14),//oa
        SYSTEM(15),//系统

        ACTION(2 << 5);


        public final int value;

        EMessageType(int value) {
            this.value = value;
        }

        public static EMessageType fromInt(int value) {
            EMessageType result = null;
            for (EMessageType item : values()) {
                if (item.value == value) {
                    result = item;
                    break;
                }
            }
            if (result == null) {
                result = EMessageType.TEXT;
            }
            return result;
        }
    }


    /*
   * action类型,对应MOption
   * */
    public enum EActionType {
        NONE(0),
        CREATE(1),
        INVITE(2),
        JOIN(3),
        KICK(4),
        LEAVE(5),
        CHANGE_ROLE(6),//转让群主
        DESTROY(7),
        MQUERY(8),//查询
        UPDATE_CONFIG(9),//更新
        CONFIRM(10),//群主确认
        REVOKE(11);//撤销


        public final int value;

        EActionType(int value) {
            this.value = value;
        }

        public static EActionType fromInt(int value) {
            EActionType result = null;
            for (EActionType item : values()) {
                if (item.value == value) {
                    result = item;
                    break;
                }
            }
            if (result == null) {
                result = EActionType.NONE;
            }
            return result;
        }
    }

    /*
    * 消息气泡点击和长按事件事件类型
    * */
    public enum ECellEventType {
        TEXT_CLICK(1),
        IMAGE_CLICK(2),
        VIDEO_CLICK(3),
        VOICE_CLICK(4),
        CARD_CLICK(5),
        MAP_CLICK(6),
        MULTI_CLICK(7),
        SECRET(8),
        VOTE_CLICK(9),

        RESEND_EVENT(10),
        ADD_EX(11),
        COPY(12),
        COLLECT_MSG(13),
        TRANSFER_MSG(14),
        CANCEL(15),
        AVATAR(16);


        public final int value;

        ECellEventType(int value) {
            this.value = value;
        }

        public static ECellEventType fromInt(int value) {
            ECellEventType result = null;
            for (ECellEventType item : values()) {
                if (item.value == value) {
                    result = item;
                    break;
                }
            }
            if (result == null) {
                throw new IllegalArgumentException("ECellEventType - fromInt");
            }
            return result;
        }
    }


    /*
    * 消息发送状态类型
    * */
    public enum ESendType {
        SENDING(0),
        FILE_SUCCESS(1),//文件上传成功
        MSG_SUCCESS(2),//消息发送成功
        ERROR(3);


        public final int value;

        ESendType(int value) {
            this.value = value;
        }

        public static ESendType fromInt(int value) {
            ESendType result = null;
            for (ESendType item : values()) {
                if (item.value == value) {
                    result = item;
                    break;
                }
            }
            if (result == null) {
                throw new IllegalArgumentException("ESendType - fromInt");
            }
            return result;
        }
    }

    /*
    * 聊天页面滚动事件type
    * */
    public enum ESCrollType {
        TOP(1),
        BOTTOM(2),
        CURRENT(4);


        public final int value;

        ESCrollType(int value) {
            this.value = value;
        }

        public static ESCrollType fromInt(int value) {
            ESCrollType result = null;
            for (ESCrollType item : values()) {
                if (item.value == value) {
                    result = item;
                    break;
                }
            }
            if (result == null) {
                throw new IllegalArgumentException("ESCrollType - fromInt");
            }
            return result;
        }
    }

    /*
    * 聊天角色类型
    * 1,接收者
    * 2,发送者
    * */
    public enum EChatRoleType {
        RECIPIENT(1),
        SENDER(2);


        public final int value;

        EChatRoleType(int value) {
            this.value = value;
        }

        public static EChatRoleType fromInt(int value) {
            EChatRoleType result = null;
            for (EChatRoleType item : values()) {
                if (item.value == value) {
                    result = item;
                    break;
                }
            }
            if (result == null) {
                throw new IllegalArgumentException("EChatRoleType - fromInt");
            }
            return result;
        }
    }


    /*
     * 0,false
     * 1,true
     * */
    public enum ESureType {
        NO(0),
        YES(1);


        public final int value;

        ESureType(int value) {
            this.value = value;
        }

        public static ESureType fromInt(int value) {
            ESureType result = null;
            for (ESureType item : values()) {
                if (item.value == value) {
                    result = item;
                    break;
                }
            }
            if (result == null) {
                result = ESureType.NO;
            }
            return result;
        }
    }


    /*
    * 播放状态
    * 0,未下载
    * 1,已下载，未播放
    * 2.已播放
    * */
    public enum EPlayType {
        NOT_DOWNLOADED(0),
        NOT_PALYED(1),
        PALYED(2);


        public final int value;

        EPlayType(int value) {
            this.value = value;
        }

        public static EPlayType fromInt(int value) {
            EPlayType result = null;
            for (EPlayType item : values()) {
                if (item.value == value) {
                    result = item;
                    break;
                }
            }
            if (result == null) {
                throw new IllegalArgumentException("EPlayType - fromInt");
            }
            return result;
        }
    }


    /*
   * activity 编号
   * 0,ActivityMain
   * */
    public enum EActivityNum {
        MAIN(0),
        CHAT(1),
        ATALL(2);

        public final int value;

        EActivityNum(int value) {
            this.value = value;
        }

        public static EActivityNum fromInt(int value) {
            EActivityNum result = null;
            for (EActivityNum item : values()) {
                if (item.value == value) {
                    result = item;
                    break;
                }
            }
            if (result == null) {
                throw new IllegalArgumentException("EActivityNum - fromInt");
            }
            return result;
        }
    }

    /*
   * fragment 编号
   * 0,ActivityMain
   * */
    public enum EFragmentNum {
        TAB_MESSAGE(0),
        TAB_CONTACTS(1),
        TAB_WORKCENT(2),
        TAB_ME(3),
        TAB_SETTINGS(4);

        public final int value;

        EFragmentNum(int value) {
            this.value = value;
        }

        public static EFragmentNum fromInt(int value) {
            EFragmentNum result = null;
            for (EFragmentNum item : values()) {
                if (item.value == value) {
                    result = item;
                    break;
                }
            }
            if (result == null) {
                throw new IllegalArgumentException("EFragmentNum - fromInt");
            }
            return result;
        }
    }

    /*
   * 转发类型
   * */
    public enum ETransforType {
        SINGLE_MSG(0),
        MULTI_MSG(1),
        PURE_MSG(2),
        CARD_MSG(3);

        public final int value;

        ETransforType(int value) {
            this.value = value;
        }

        public static ETransforType fromInt(int value) {
            ETransforType result = null;
            for (ETransforType item : values()) {
                if (item.value == value) {
                    result = item;
                    break;
                }
            }
            if (result == null) {
                throw new IllegalArgumentException("ETransforType - fromInt");
            }
            return result;
        }
    }

    /*
    * 转发模式
    * */
    public enum ETransforModel {
        MODE_ONE_BY_ONE(0),
        MODE_ALL(1),
        MODE_ATTACH(2);

        public final int value;

        ETransforModel(int value) {
            this.value = value;
        }

        public static ETransforModel fromInt(int value) {
            ETransforModel result = null;
            for (ETransforModel item : values()) {
                if (item.value == value) {
                    result = item;
                    break;
                }
            }
            if (result == null) {
                throw new IllegalArgumentException("ETransforModel - fromInt");
            }
            return result;
        }
    }

    /*
    * 群聊 or 私聊
    * */
    public enum EChatType {
        GROUP(0),
        PRIVATE(1);
        public final int value;

        EChatType(int value) {
            this.value = value;
        }

        public static EChatType fromInt(int value) {
            EChatType result = null;
            for (EChatType item : values()) {
                if (item.value == value) {
                    result = item;
                    break;
                }
            }
            if (result == null) {
                throw new IllegalArgumentException("EChatType - fromInt");
            }
            return result;
        }
    }


    /*
   * 合并消息布局类型
   * */
    public enum EMultiCellLayout {
        TEXT(R.layout.multi_text),

        IMAGE(R.layout.multi_image),

        VOICE(R.layout.multi_voice),

        VIDEO(R.layout.multi_video),

        MAP(R.layout.multi_map),

        VOTE(R.layout.multi_text),

        EMOTICON(R.layout.multi_image),

        BUSINESS_CARD(R.layout.multi_contact),

        WORK_LOGIN(R.layout.multi_text),

        MULTI(R.layout.multi_text);


        public final int LayoutId;

        EMultiCellLayout(int layoutId) {
            this.LayoutId = layoutId;
        }

        public static EMultiCellLayout fromOrdinal(int ordinal) {
            EMultiCellLayout result = null;
            for (EMultiCellLayout item : EMultiCellLayout.values()) {
                if (item.ordinal() == ordinal) {
                    result = item;
                    break;
                }
            }
            if (result == null) {
                result = EMultiCellLayout.TEXT;
            }
            return result;
        }

        public static EMultiCellLayout fromLayoutId(int layoutId) {
            EMultiCellLayout result = null;
            for (EMultiCellLayout item : EMultiCellLayout.values()) {
                if (item.LayoutId == layoutId) {
                    result = item;
                    break;
                }
            }
            if (result == null) {
                result = EMultiCellLayout.TEXT;
            }
            return result;
        }

        public static int size() {
            return values().length;
        }
    }


    /*
  * 聊天背景ID
  * */
    public enum EChatBgId {
        DEFAULT(0),
        FIRST(10001),
        SECEND(10002),
        THIRD(10003),
        FORTH(10004),
        FIFTH(10005),
        SIXTH(10006),
      /*  SEVENTH(10007),
        EIGHTH(10008),
        NINTH(10009),
        TENTH(10010)*/;

        public final int id;

        EChatBgId(int id) {
            this.id = id;
        }

        public static EChatBgId fromOrdinal(int ordinal) {
            EChatBgId result = null;
            for (EChatBgId item : EChatBgId.values()) {
                if (item.id == ordinal) {
                    result = item;
                    break;
                }
            }
            if (result == null) {
                result = EChatBgId.DEFAULT;
            }
            return result;
        }

        public static int size() {
            return values().length;
        }
    }

    /*
     * 通讯录关系状态
     * */
    public enum ERelationStatus {
        FRIEND(0),//是好友
        INVITE(1),//非好友，我邀请
        RECEIVE(2),//非好友，我被邀请
        SELF(3);//自己


        public final int value;

        ERelationStatus(int value) {
            this.value = value;
        }

        public static ERelationStatus fromInt(int value) {
            ERelationStatus result = null;
            for (ERelationStatus item : values()) {
                if (item.value == value) {
                    result = item;
                    break;
                }
            }
            if (result == null) {
                result = ERelationStatus.FRIEND;
            }
            return result;
        }
    }

//    @IntDef(
//        flag = true,
//        value = {ROSTER_MESSAGE, PRIVATE_MESSAGE, GROUP_MESSAGE, OFFLINE_MESSAGE, MUC_GROUP_MESSAGE,
//            MUC_MEMBER_MESSAGE, MSG_ACK_MSG, NET_STATUS})
//    @Retention(RetentionPolicy.CLASS)
//    public @interface EIMType {
//
//        int ROSTER_MESSAGE = 0;
//        int PRIVATE_MESSAGE = 1;
//        int GROUP_MESSAGE = 2;
//        int OFFLINE_MESSAGE = 3;
//        int MUC_GROUP_MESSAGE = 4;
//        int MUC_MEMBER_MESSAGE = 5;
//        int MSG_ACK_MSG = 6;
//        int NET_STATUS = 10;
//    }

    /*
     * 服务器返回消息类型
     * */
    public enum EIMType {
        ROSTER_MESSAGE(0),//好友消息
        PRIVATE_MESSAGE(1),//私聊消息
        GROUP_MESSAGE(2),//群聊消息
        OFFLINE_MESSAGE(3),//离线消息
        MUC_GROUP_MESSAGE(4),
        MUC_MEMBER_MESSAGE(5),
        MSG_ACK_MSG(6),//二次回执消息
        FG_PUSH_MESSAGE(7),//推送消息

        NET_STATUS(10);//网络状态


        public final int value;

        EIMType(int value) {
            this.value = value;
        }

        public static EIMType fromInt(int value) {
            EIMType result = null;
            for (EIMType item : values()) {
                if (item.value == value) {
                    result = item;
                    break;
                }
            }
            if (result == null) {
                throw new IllegalArgumentException("EIMType - fromInt");
            }
            return result;
        }
    }

    /*
     * 服务器返回消息类型
     * */
    public enum ENetStatus {
        SUCCESS_ON_NET(0),//网络连接成功
        SUCCESS_ON_SERVICE(1),//IM client 连接成功
        LOGIN_CONFLICTED(2),//登录冲突
        ERROR_CONNECT(3),//服务器断开链接
        ERROR_LOGIN(4),//207未登录,被服务器踢出登录
        ERROR_NET(5);//无可用网络


        public final int value;

        ENetStatus(int value) {
            this.value = value;
        }

        public static ENetStatus fromInt(int value) {
            ENetStatus result = null;
            for (ENetStatus item : values()) {
                if (item.value == value) {
                    result = item;
                    break;
                }
            }
            if (result == null) {
                throw new IllegalArgumentException("ENetStatus - fromInt");
            }
            return result;
        }
    }

    /*
     * 搜索结果类型
     * */
    public enum EResultType {
        DEFUALT(0),
        CONTACT(1),
        MUC(2),
        RECORD(3);


        public final int value;

        EResultType(int value) {
            this.value = value;
        }

        public static EResultType fromInt(int value) {
            EResultType result = null;
            for (EResultType item : values()) {
                if (item.value == value) {
                    result = item;
                    break;
                }
            }
            if (result == null) {
                return DEFUALT;
            }
            return result;
        }
    }


}
