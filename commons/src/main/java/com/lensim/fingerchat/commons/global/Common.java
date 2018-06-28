package com.lensim.fingerchat.commons.global;

public class Common {

    public static final int DISPATCH_ERROR = 1; //消息分发失败
    public static final int UNSUPPORTED_CMD = 2; //无法识别的命令
    public static final int PARAM_INVALID = 3; //非法参数
    public static final int REPEAT_HANDSHAKE = 4; //重复握手
    public static final int NO_HANDSHAKE = 5; //没有握手成功
    public static final int SESSION_EXPIRE = 6; //session 过期
    public static final int INVALID_DEVICE = 7; //非法设备

    // msgAck
    public static final int ERROR_NO_FRIEND = 11; //非好友关系
    public static final int ERROR_NO_EXSIT = 21; //房间不存在
    public static final int ERROR_NO_MEMBER = 22; //不是群成员
    public static final int CANCEL = 31; //cancel消息


    //注册相关
    public static final int ACCOUNT_DUMPLICATED = 100; //重复注册

    public static final int REG_SMS_ERROR = 101; //验证码发送失败
    public static final int REG_REGISTER_OK = 102; //注册成功
    public static final int REG_SMS_OK = 103; //验证码发送成功
    public static final int REG_VER_CODE_ERROR = 104; //验证失败

    //登录相关
    public static final int LOGIN_ACCOUNT_INEXIST = 200; //账号不存在
    public static final int LOGIN_VERYFY_PASSED = 201; //验证通过
    public static final int LOGIN_VERYFY_ERROR = 202; //验证失败
    public static final int LOGIN_FORBIDDON_LOGIN = 203; //禁止登陆
    public static final int LOGIN_UNBIND_SUCCESS = 204; //退出登录成功
    public static final int LOGIN_UNBIND_ERROR = 205; //退出登录失败
    public static final int LOGIN_LOGIN_CONFLICT = 206; //退出登录失败
    public static final int LOGIN_UNAUTHORIZED = 207; //未登陆

    public static final int UPDATE_SUCCESS = 208; //更新用户信息成功
    public static final int UPDATE_FAILURE = 209; //更新用户信息失败


    //聊天相关
    public static final int RECEIVED_MESSAGE = 300;

    //花名册相关
    public static final int SEND_INVITE = 400;  // 收到邀请
    public static final int SEND_OK = 401;  // 发出了邀请
    public static final int INVITE_OK = 402;  // 同意了邀请

    public static final int INVITE_DUMPLICATED = 403;  // 重复邀请
    public static final int USER_NOT_FOUND = 404;  // 用户不存在
    public static final int FORBIDDON = 405;  // 禁止邀请


    public static final int DELETE_SUCCESS = 406;  // 删除成功
    public static final int USER_NOT_IN_ROSTER = 407;  // 好友不存在
    public static final int DELETE_FAILURE = 408;  // 删除失败

    public static final int QUERY_OK = 410;  //查询成功
    public static final int QUERY_ERROR = 411;  //查询错误

    public static final int ALREADY_SUB = 412;  //已经是好友
    public static final int USER_IN_BLACKLIST = 413;  //黑名单
    public static final int ADD_SUCCESS = 414;  //添加成功
    public static final int UPDATE_INFO_SUCCESS = 415;  //更新用户信息成功
    public static final int INVITE_FAILURE = 416;  //邀请失败
    public static final int UPDATE_ROSTER_FAILURE = 417;  //修改失败

    //聊天群相关
    public static final int CREATE_OK = 500;  // 创建成功
    public static final int INVITE_TO_MUC = 501;  // 邀请成功
    public static final int KICK_OK = 502;  // 剔除成功
    public static final int LEAVE_OK = 503;  // 离开成功
    public static final int DESTORY_OK = 504;  // 销毁成功
    public static final int MUC_QUERY_OK = 505;  // 查询执行成功.  暂时废弃
    public static final int JOIN_OK = 510;  // 加入成功
    public static final int CHANGE_ROLE_OK = 511;  // 查询执行成功
    public static final int MUC_UPDATE_OK = 512;  // 群配置更新成功
    public static final int CREATE_FAILURE = 513;  //创建失败

    public static final int MUC_QUERY_All_ROOMS_OF_USER_OK = 514; // 查询用户所在的所有群成功
    public static final int MUC_QUERY_All_MEMBER_IN_ROOM_OK = 515; // 查询当前群所有群成员成功
    public static final int MUC_QUERY_OWNER_OK = 516; // //查询群主成功
    public static final int MUC_QUERY_ROOM_BY_ID_OK = 517; // //查询用户单个群
    public static final int MUC_QUERY_ADMIN_OK = 518; // 查询群管理员成功

    //错误信息
    public static final int NOT_MEMBER = 506;  // 不是群成员
    public static final int ROOM_INEXIST = 507;  // 房间不存在
    public static final int JOIN_DUMPLICATED = 508;  // 重复加入
    public static final int NEED_OWNER = 509;  // 需要群主权限
}
