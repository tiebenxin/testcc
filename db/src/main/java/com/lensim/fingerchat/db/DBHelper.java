package com.lensim.fingerchat.db;

/**
 * Created by LL130386 on 2017/12/13.
 */

public class DBHelper {

    public static final String[] chat_fields = new String[]{DBHelper.TYPE, DBHelper.TO,
        DBHelper.CONTENT, DBHelper.ID, DBHelper.TIME, DBHelper.CODE, DBHelper.CANCLE, DBHelper.FROM,
        DBHelper.SEND_TYPE, DBHelper.CHAT_TAG, DBHelper.UPLOAD_URL, DBHelper.IS_SECRET,
        DBHelper.PLAY_STATUS, DBHelper.ACTION_TYPE, DBHelper.USER_AVATAR, DBHelper.NICK};

    public static String[] roster_fields = new String[]{DBHelper.ACCOUT, DBHelper.USER_NICK,
        DBHelper.WORK_ADDRESS, DBHelper.GROUP, DBHelper.EMP_NAME, DBHelper.REMARK_NAME,
        DBHelper.SEX, DBHelper.IMAGE, DBHelper.IS_VALID, DBHelper.JOB_NAME, DBHelper.DPT_NO,
        DBHelper.DPT_NAME, DBHelper.EMP_NO, DBHelper.IS_BLOCK, DBHelper.SHORT, DBHelper.PINYIN,
        DBHelper.STATUS, DBHelper.TIME, DBHelper.HAS_READED, DBHelper.IS_STAR, DBHelper.CHAT_BG,
        DBHelper.IS_QUIT};


    public static String[] recent_fields = new String[]{DBHelper.MSG, DBHelper.NICK,
        DBHelper.GROUP_NAME, DBHelper.MSG_TYPE, DBHelper.USER_ID, DBHelper.TOP_FLAG,
        DBHelper.UNREAD_COUNT, DBHelper.NOT_DISTURB, DBHelper.TIME, DBHelper.IS_AT,
        DBHelper.CHAT_ID, DBHelper.AVATAR_URL, DBHelper.CHAT_TYPE, DBHelper.HINT, DBHelper.BG_ID};


    //RosterItem 属性值
    public final static String TABLE_ROSTER = "t_roster";

    public final static String ACCOUT = "account_";
    public final static String USER_NICK = "usernick_";
    public final static String WORK_ADDRESS = "workAddress_";
    public final static String GROUP = "group_";
    public final static String EMP_NAME = "empName_";
    public final static String REMARK_NAME = "remarkName_";
    public final static String SEX = "sex_";
    public final static String IMAGE = "image_";
    public final static String IS_VALID = "isvalid_";
    public final static String JOB_NAME = "jobname_";
    public final static String DPT_NO = "dptNo_";
    public final static String DPT_NAME = "dptName_";
    public final static String EMP_NO = "empNo_";
    public final static String IS_BLOCK = "isBlock_";
    public final static String SHORT = "short";
    public final static String PINYIN = "pinyin";
    public final static String STATUS = "status"; //好友状态，当前是好友为0，当前非好友：我发送1，我收到邀请2
    //public final static String TIME = "time_";  接受邀请的时间。long
    public final static String HAS_READED = "has_readed"; //是否已读，int 0为未读，1为已读
    public final static String NEW_STATUS = "new_status"; //是否新好友，int 0为否，1为是
    public final static String IS_STAR = "isStar_"; //是否是星标好友，int 0为否，1为是
    public final static String CHAT_BG = "chat_bg"; //聊天背景，int 0为否，1为是
    public final static String IS_QUIT = "isQuit";//是否已离职，int 0为否，1为是


    //Message 属性
    public final static String TABLE_MESSAGE = "t_private";

    public final static String TYPE = "type_";
    public final static String TO = "to_";
    public final static String CONTENT = "content_";
    public final static String ID = "id_";
    public final static String TIME = "time_";
    public final static String CODE = "code_";
    public final static String CANCLE = "cancel_";
    public final static String FROM = "from_";

    //Message 自定义属性
    public final static String SEND_TYPE = "send_type";
    public final static String CHAT_TAG = "chat_tag";//接收 or 发送
    public final static String UPLOAD_URL = "upload_url";//接收 or 发送
    public final static String IS_SECRET = "is_secret";//是否密聊
    public final static String PLAY_STATUS = "is_loaded";//是否下载了
    public final static String ACTION_TYPE = "action_type";//action
    public final static String USER_AVATAR = "avatar";//发送者头像
//    public final static String USER_NICK = "nick";//发送者昵称
//    public final static String CHAT_TYPE = "chat_type";//是否群聊


    public final static String TABLE_RECENT = "t_recent";

    //RecentMEssage 属性
    public final static String CHAT_ID = "chat_id";
    public final static String MSG = "msg";
    public final static String NICK = "nick";
    public final static String GROUP_NAME = "group_name";
    public final static String MSG_TYPE = "msg_type";
    public final static String USER_ID = "user_id";
    public final static String TOP_FLAG = "top_flg";
    public final static String UNREAD_COUNT = "unread_count";
    public final static String NOT_DISTURB = "not_disturb";
    //  public final static String TIME = "time";
    public final static String IS_AT = "is_at";
    public final static String AVATAR_URL = "avatar_url";
    public final static String CHAT_TYPE = "chat_type";
    public final static String HINT = "hint";
    public final static String BG_ID = "bg_id";

    //MucInfo 群信息
    public final static String TABLE_MUC_INFO = "t_muc_info";

    public final static String MUC_ID = "muc_id";
    public final static String MUC_NAME = "muc_name";
    public final static String SUBJECT = "subject_";
    public final static String AUTOENTER = "autoEnter_";
    public final static String MEMBERCOUNT = "memberCount_";
    public final static String ROLE = "role_";
    public final static String NOTDISTURB = "noDisturb_";
    public final static String CHATBG = "chatBg_";
    public final static String MUC_USERNICK = "mucusernick_";
    public final static String CREATION_TIME = "creationTime";//创建时间
    public final static String CREATOR = "creater";//创建者
    //MucInfo 群信息 END

    //MucMemberItem
    public final static String TABLE_PRIVATE_MCU_USER = "t_muc_user";

    public final static String GROUP_ROLE = "role_";
    public final static String GROUP_USERNAME = "username_";
    public final static String GROUP_USERNICK = "usernick_";
    public final static String GROUP_MUC_USERNICK = "mucusernick_";
    public final static String GROUP_INVITER = "inviter_";
    public final static String GROUP_AVATAR = "avatar_";
    //MucMemberItem end
}
