package com.lensim.fingerchat.commons.global;

public class Common {

    public static final int DISPATCH_ERROR = 1; //消息分发失败
    public static final int UNSUPPORTED_CMD = 2; //无法识别的命令
    public static final int PARAM_INVALID = 3; //非法参数
    public static final int REPEAT_HANDSHAKE = 4; //重复握手
    public static final int NO_HANDSHAKE = 5; //没有握手成功
    public static final int SESSION_EXPIRE = 6; //session 过期
    public static final int INVALID_DEVICE = 7; //非法设备
    public static final int DECRYPT_FAIL = 8; //解密失败
    public static final int DECODE_FAIL = 9; //消息编码格式不对,解码失败

    // msgAck
    public static final int ERROR_NO_FRIEND = 11; //非好友关系
    public static final int CODE_REFUSED = 12;//拒收
    public static final int CODE_FRIEND_DISABLE = 14;//好友已经离职,私聊消息接收人如果是离职状态则消息发送失败
    public static final int ERROR_NO_EXSIT = 21; //房间不存在
    public static final int ERROR_NO_MEMBER = 22; //不是群成员
    public static final int CANCEL = 31; //cancel消息


    //注册相关
    public static final int ACCOUNT_DUMPLICATED = 100; //重复注册

    public static final int REG_SMS_ERROR = 101; //验证码发送失败
    public static final int REG_REGISTER_OK = 102; //注册成功
    public static final int REG_SMS_OK = 103; //验证码发送成功
    public static final int REG_VER_CODE_ERROR = 104; //验证失败

    public static final int USERNAME_INVALIDE = 105;//账号非法

    public static final int OBTAIN_CODE_EXIST = 106; //上次验证码未超时
    public static final int RATE_LIMIT = 107; //发送短信频率过快
    public static final int PHONE_INVALID = 108; //手机号重复或者不可用
    public static final int OBTAIN_CODE_ERROR = 109; //后台验证用户和手机号失败

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
    public static final int WITHOUT_PERMISSION = 210; //无登录权限

    public static final int PASSWORD_CHANGED = 211; //密码被修改，强制下线冲突
    public static final int PASSWORD_ERROR = 212; //密码错误 重连时跳回登录界面

    public static final int PASSWORD_LOW_INTENSITY = 214;  //密码强度不够,密码强度规则：必须包含  数字  大写字母  小写字母
    public static final int DUPLICATE_PASSWORD_IN5TIMES = 215;  //不能使用前面5次使用过的密码

    public static final int PASSWORD_DEFERRED_80DAYS = 216;  //密码即将过期
    public static final int PASSWORD_DEFERRED_90DAYS = 217;  //密码大于90天 需要客户端跳转到密码修改界面，然后禁用其他功能

    public static final int WRONG_PASSWORD_LOCKED = 249;  //登录密码错误次数超过上限，账户已经被锁定10分钟

    /**账户锁定与清除操作*/
    public static final int ACCOUNT_LOCKED = 250; //账户已经被锁定
    public static final int ACCOUNT_CELANED = 253; //账户清除


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
    public static final int UPDATE_ROSTER_SUCCESS = 415;  //更新用户信息成功
    public static final int INVITE_FAILURE = 416;  //邀请失败
    public static final int UPDATE_ROSTER_FAILURE = 417;  //修改失败
    public static final int ROSTER_GROUP_UPDATE_SUCCESS = 418; //更新好友分组成功
    public static final int ROSTER_GROUP_DELETE_SUCCESS = 419; // 分组销毁成功


    //聊天群相关
    public static final int CREATE_OK = 500;  // 创建成功
    public static final int INVITE_TO_MUC = 501;  // 邀请成功
    public static final int KICK_OK = 502;  // 剔除成功
    public static final int LEAVE_OK = 503;  // 离开成功
    public static final int DESTORY_OK = 504;  // 销毁成功
    public static final int MUC_QUERY_OK = 505;  // 查询执行成功.  暂时废弃
    public static final int NOT_MEMBER = 506;  // 不是群成员
    public static final int ROOM_INEXIST = 507;  // 房间不存在
    public static final int JOIN_DUMPLICATED = 508;  // 重复加入
    public static final int NEED_OWNER = 509;  // 需要群主权限
    public static final int JOIN_OK = 510;  // 加入成功
    public static final int CHANGE_ROLE_OK = 511;  // 查询执行成功
    public static final int MUC_UPDATE_OK = 512;  // 群配置更新成功
    public static final int CREATE_FAILURE = 513;  //创建失败

    public static final int MUC_QUERY_All_ROOMS_OF_USER_OK = 514; // 查询用户所在的所有群成功
    public static final int MUC_QUERY_All_MEMBER_IN_ROOM_OK = 515; // 查询当前群所有群成员成功
    public static final int MUC_QUERY_OWNER_OK = 516; // //查询群主成功
    public static final int MUC_QUERY_ROOM_BY_ID_OK = 517; // //查询用户单个群
    public static final int MUC_QUERY_ADMIN_OK = 518; // 查询群管理员成功

    public static final int MUC_API_UPDATE_ERROR = 520; // 更新群内容失败
    public static final int EMPTY_OWNER = 524;//群主不存在了
    public static final int MUC_KICK_ERROR = 525; // 踢用户出群失败
    public static final int MUC_LEAVE_ERROR = 526; //用户退群失败
    public static final int MUC_DESTORY_ERROR = 527; //用户退群失败
    public static final int MUC_QUERY_All_ROOMS_OF_USER_ERROR = 528; // 查询用户所在的所有群失败
    public static final int MUC_QUERY_All_MEMBER_IN_ROOM_ERROR = 529; // 查询用户所在的单个群失败
    public static final int MUC_QUERY_OWNER_ERROR = 530; // //查询群主失败
    public static final int MUC_QUERY_ROOM_BY_ID_ERROR = 531; // //查询用户单个群失败
    public static final int MUC_QUERY_ADMIN_ERROR = 532; // 查询群管理员失败
    public static final int MUC_CREART_LEAVE_FORBIDDEN = 533; // 群主不能退群

    public static final int EMOTICON_PARAM_INVALID = 701;//自定义表情接口调用参数错误（存储需要json字符串）
    public static final int EMOTICON_NO_LONGIN = 702;// 未登录时不能进行表情操作
    public static final int EMOTICON_SAVE_ERROR = 703;//存储的表情失败
    public static final int EMOTICON_SAVE_SUCCESS = 704;//存储的表情成功
    public static final int EMOTICON_QUERY_ERROR = 705;  //查找我的表情失败
    public static final int EMOTICON_QUERY_EMPTY = 706;  //服务器没有个人表情
    public static final int EMOTICON_QUERY_SUCCESS = 707;  //查询成功
    public static final int EMOTICON_DEL_ERROR = 708; // 删除表情失败
    public static final int EMOTICON_DEL_SUCCESS = 709; // 删除表情成功
    public static final int EMOTICON_TOFIRST_SUCCESS = 710; //移动表情到最前成功

}
