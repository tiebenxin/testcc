package com.lensim.fingerchat.commons.global;


/**
 * date on 2017/12/26
 * author ll147996
 * describe
 */

public class Route {

    public static final String UPLOAD_IMAGE = BaseURL.BASE_URL_UPLOAD + "/DFS/Image";
    public static final String UPLOAD_VIDEO = BaseURL.BASE_URL_UPLOAD + "/DFS/Video";
    public static final String UPLOAD_VOICE = BaseURL.BASE_URL_UPLOAD + "/DFS/Voice";
    public static final String SEARCH_USER_LIST =
        BaseURL.BASE_URL_SEARCH + "/xdata-proxy/user/getUserInfoList";
    public static final String SEARCH_USER =
        BaseURL.BASE_URL_SEARCH + "/xdata-proxy/user/getUserInfo";
    public static final String UPDATE_PASSWORD =
        "http://10.3.9.152:8080"+ "/xdata-proxy/user/updatePassword";
//    public static final String SEARCH_USER_LIST = BaseURL.BASE_URL_SEARCH + "/im2user/queryByKeyword";
//    public static final String SEARCH_USER_LIST = BaseURL.BASE_URL_SEARCH + "/v1/db/read/fingerchat/user/queryByKeyword";


    //"http://mobile.fingerchat.cn:8696/";
    public static final String Host =
        BaseURL.HTTP + BaseURL.DOMAIN_NAME + BaseURL.PORT_8686 + BaseURL.URL_SPLITTER;

    public static final String SSO_HOST =
        BaseURL.HTTP + "10.3.9.158:8080" + BaseURL.URL_SPLITTER + "/imsso/";


    /**
     * 注册飞鸽
     */
    public static final String registerFG = "LensWcfSrv.svc/csn";

    /**
     * 群成员添加与删除
     * 方法：GET
     * "userid", 用户名
     * "fun", 方法名：getmember
     * "username", 昵称
     * "roomname", 房间名称
     */
    public static final String URL_MucMember = "servers/addMucmember.ashx";
    /**
     * 获取所有的群成员
     * 方法：GET
     * "userid", 用户名
     * "fun",方法名： "teamdetail"
     * "teamserno", 房间名称
     */
    public static final String URL_GetMucMembers = "servers/getTeam.ashx";
    public static final String URL_REGISTER = "LensWcfSrv.svc/r2";
    /**
     * 上传头像
     * 方法：POST
     * userid", 用户名
     * photoContent", 头像文件
     */
    public static final String URL_UpLoadAvater = "servers/PostImage.aspx";
    /**
     * 获取用户个人信息
     * 方法：GET
     * "fun", 方法名: "getuser"
     * "userid",用户名
     */
    public static final String URL_GetOrUpdateUserInfo = "servers/getUserInfo.ashx";
    /**
     * 获取所有的好友信息
     * 方法：GET
     * "fun",方法名:"myFriend"
     * "userid",用户名
     */
    public static final String URL_GetAllFriendsInfo = "servers/getFriend.ashx";
    /**
     * 上传用户认证信息
     */
    public static final String URL_UPLOAD_IDCARD = "servers/cerAuth.ashx";
    /**
     * 发送图片或者音频
     */
    public static final String FileUri = "servers/postMessage.ashx";
    /**
     * 获取所有好友的朋友圈分享
     */
    public static final String FriendCircleUrl = "servers/getPhoto.ashx";

    //http://mobile.fingerchat.cn:8686/LensWcfSrv.svc/r/{n}/{p}/{h}/{pictype}
    /**
     * 刷新查看朋友圈的时间
     */
    public static final String UpdateCircleUrl = "servers/getPhotoView.ashx";
    /**
     * 创建收藏列表
     */
    public static final String CreateFavUrl = "fav/CreateFav";
    /**
     * 获取收藏列表
     */
    public static final String GetFavListUrl = "LensWcfSrv.svc/GetFav";
    /**
     * 删除收藏
     */
    public static final String DelFavUrl = "LensWcfSrv.svc/DelFav";


    public static final String NewUpdateCircleUrl = "LensWcfSrv.svc";
    /**
     * 分享小视频
     */
    //public static String SendVideoUrl =  "http://10.3.7.147:8085/LensFS/VideoFrame";
    public static final String SendVideoUrl = "LensFS/VideoFrame";
    /**
     * URL_GET_OA_TOKEN
     */
    public static final String URL_GET_OA_TOKEN = "v1/user/getOAToken";
    /**
     * 登陆__SSO
     */
    public static final String URL_SSO_LOGIN = "v2/fxtoken/login";
    /**
     * 登出__SSO
     */
    public static final String URL_SSO_LOGIN_OUT = "v2/fxtoken/logout";

    /**
     * 授权扫二维码登录
     */
    public static final String URL_ACCEPT_SSO_LOGIN = "v2/oauth2/acceptQrtcodeLogin";

    /**
     * 手机登陆
     */
    public static final String URL_PHONE_LOGIN = "LensWcfSrv.svc/GetUsrInfPost";
    //public static String URL_PHONE_LOGIN = HTTP + IP + ":8181" + URL_SPLITTER + "LensWcfSrv.svc/GetUsrInf/%s/%s";
    /**
     * 修改密码
     */
    public static final String URL_ChangePwd = "LensWcfSrv.svc/CUP";
    /**
     * 获取最新的朋友圈发布者
     */
    //http://10.3.7.147:8090/servers/getPhotoView.ashx?userid={用户名}&fun=getnewPhoto
    public static final String URL_NEW_CIRCLE_PUBLISHER = "servers/getPhotoView.ashx";

    /**
     * 删除评论
     */
    public static final String URL_DEL_COMMENT = "LensWcfSrv.svc/MvMoments";
    /**
     * 评论
     */
    // http://10.3.7.142:8181/LensWcfSrv.svc/MomentsComment/{func}/{CreateUserid}/{CreateUsername}/{CommentUserid}/{CommentUsername}/{photoserno}/{content}/{secondid=null}/{secondname=null}
    //private static String URL_COMMENT = HTTP + IP + PORT_8181 + URL_SPLITTER + "LensWcfSrv.svc/MomentsComment/%s/%s/%s/%s/%s/%s/%s/%s/%s";
    public static final String URL_COMMENT = "LensWcfSrv.svc/mc";
    /**
     * 签到
     */
    public static final String URL_SIGN_IN = "LensWcfSrv.svc/SignIn";
    /**
     * 签到查询
     */
    public static final String URL_GET_SIGN_IN = "LensWcfSrv.svc/GetEmpSignIn";
    /**
     * 上传图片
     */
    public static final String URL_SIGN_IN_POST_IMAGE = "LensFS/Image";

    /**
     * 检查用户是否被锁定
     */
    //http://mobile.fingerchat.cn:8686/LensWcfSrv.svc/getUserIsLock/{用户名}
    //public static String URL_CheckUserState = Host +"servers/getUserState.ashx";
    // http://mobile.fingerchat.cn:8686/LensWcfSrv.svc/GetMyComments/{用户名}/{iPageNum}/{iPagesize}/{时间戳}
    public static final String URL_ALL_COMMENTS_BY_TIME = "LensWcfSrv.svc/GetMyComments";
    //"http://mobile.fingerchat.cn:8686/LensWcfSrv.svc/imlog"
    public static final String URL_OPTION_LOG = "LensWcfSrv.svc/log";
    public static final String URL_OPTION_LOGGER = "v1/kafka/applog/add";

    /**
     * 获取单条分享的所有评论
     */
    public static final String URL_ITEM = "LensWcfSrv.svc/GetPhotoComment";

    /**
     * 更新密码
     */
    public static final String URL_UPDATE_PASSWORD = "/user/updatePassword";

    /**
     * 检查版本更新
     * 方法:GET
     */
    public static final String URL_CHECK_VERSION =
        BaseURL.HTTP + BaseURL.DOMAIN_NAME + BaseURL.PORT_8686 + BaseURL.URL_SPLITTER
            + "LensWcfSrv.svc/GetUpdateUrlJson/lensim";
    //public static String SendCircleUrl =  Host +  "servers/sendPhoto.ashx";
    //http://mobile.fingerchat.cn:8181/LensWcfSrv.svc/GetUpdateUrlJson/lensimhot
    public static final String URL_HOT_UPDATE =
        BaseURL.HTTP + BaseURL.DOMAIN_NAME + BaseURL.PORT_8686 + BaseURL.URL_SPLITTER
            + "LensWcfSrv.svc/GetUpdateUrlJson/lensimhot";
    //  public static final String URL_ITEM = HTTP + IP + PORT_8181 + URL_SPLITTER + "LensWcfSrv.svc/GetPhotoComment/%s/%s";
    //public static String SendVideoUrl =  Host +  "servers/sendVideo.ashx";
    //servers/getUserInfo.ashx?userid=xxx&fun=updatepwd&p=密码
    //
    public static final String URL_GET_FUNCTIONS = "LensWcfSrv.svc/GetFuncInfByEmpno";
    public static final String URL_GET_HR =
        BaseURL.HTTP + BaseURL.DOMAIN_NAME + BaseURL.PORT_8686 + BaseURL.URL_SPLITTER
            + "LensWcfSrv.svc/GetHRCS/%s";


    /**
     * Hex
     * 会议信息录入
     * 方法：post
     */
    public static final String URL_HEX_MEETING_CREAT = "LensWcfSrv.svc/meetingcreat";


    /**
     * Hex
     * 会议信息删除
     * 方法：post
     */
    public static final String URL_HEX_MEETING_DELETE = "LensWcfSrv.svc/delMeeting";


    /**
     * Hex
     * 会议列表查询
     * 方法：GET
     */
    public static final String URL_HEX_GET_MEETING_LIST = "LensWcfSrv.svc/getMeetingList";

    /**
     * Hex
     * 给已存在的会议添加联系人的接口:
     * 方法：POST
     */
    public static final String HEX_MEET_JOINTO_EXISTMEETING = "LensWcfSrv.svc/JoinToExistMeeting";

    /**
     * 获取当前admin的token
     * 用于结束会议等操作时所需的admin权限
     */
    public static final String ADMIN_TOKEN = "HexUser/GetT/feige";

    /**
     * 获取成员头像
     * 方法：GET
     * %s:用户名
     */
    public static String obtainAvater = Host + "HnlensImage/Users/%s/Avatar/headimage.png";
    /**
     * 获取朋友圈主题背景图片
     * 方法：GET
     * %s:用户名
     */
    public static String obtainTheme = Host + "HnlensImage/Users/%s/Avatar/themeimage.png";
    /**
     * 获取加入的所有房间名称
     * 方法：GET
     * %s:用户名
     */
    public static String URL_JoinedRooms = Host + "LensWcfSrv.svc/GetMucCreater/%s";
    /**
     * 检查用户是否被锁定
     * 方法：GET
     * "fun",方法名:"getUserIsLock"
     * "userid",userid
     */
    // public static String URL_CheckUserState = Host + "servers/getUserState.ashx";
    public static String URL_CheckUserState = Host + "LensWcfSrv.svc/getUserIsLock/%s";
    //http://fingerchat.cn:8181/LensWcfSrv.svc/GetUsrPhone/{username}
    /**
     * 获取用户001代码权限
     */
    public static String URL_AUTHORITY_SUBJECT = Host + "GetUserPower/GetPowerByUserIDParentID/%s";
    //http://fingerchat.cn:8181/LensWcfSrv.svc/CUP
    public static String URL_UPLOAD_FILE = Host + "/LensFS/Image";
    /**
     * 分页获取朋友圈分享
     */
    //http://10.3.7.147:8090/servers/getphoto.ashx?userid={username}&fun=getfriendphotopg&pagesize={分页大小}&pagenum={分页数}
    //public static String URL_FC_NUM =  Host +  "servers/getPhoto.ashx";
    //图片文件名
    public static String FileName = "hnlensImage/Message/%s/%s/%s";
    /**
     * 发表评论
     */
    //public static String SendCircleUrl = "http://10.3.7.147:8085/LensFS/Image";
    public static String SendCircleUrl = Host + "LensFS/Image";

    //http://mobile.fingerchat.cn:8181/LensWcfSrv.svc/MvMoments/{type}/{sero}/{creater}
    /**
     * 修改密码（已弃用）
     */
    public static String URL_PWD = Host + "servers/getUserInfo.ashx";
    /**
     * 获取最后一条群聊消息时间
     */
    public static String lastMucMsgTimeUrl =
        BaseURL.HTTP + BaseURL.IP + BaseURL.PORT_8686 + BaseURL.URL_SPLITTER
            + "LensWcfSrv.svc/GetMucTimeByMsgID/%s";
    //http://fingerchat.cn:8181/LensWcfSrv.svc/CheckIdCard/{工号}/{身份证号}/{用户名}
    public static String URL_Identify =
        BaseURL.HTTP + BaseURL.DOMAIN_NAME + BaseURL.PORT_8686 + BaseURL.URL_SPLITTER
            + "LensWcfSrv.svc/CheckIdCard/%s/%s/%s";
    /**
     * 用户认证
     */
    public static String IndentifyUrl =
        BaseURL.HTTP + BaseURL.IP + ":8989/LensWcfSrv.svc/%s/%s/%s/%s/%s";
    /**
     * 用户认证
     */
    public static String Indentify = BaseURL.HTTP + BaseURL.IP + ":8989/LensWcfSrv.svc/%s/%s/%s/%s";
    /**
     * 获取认证的手机号码
     */
    public static String URL_UserPhone =
        BaseURL.HTTP + BaseURL.IP + BaseURL.PORT_8686 + BaseURL.URL_SPLITTER
            + "LensWcfSrv.svc/GetUsrPhone/%s";
    /**
     * 获取工作中心的图片
     */
    public static String WORK_ITEM_IMG = Host + "hnlensImage/and/%s";
    /**
     * 查找好友
     */
    public static String URL_SEARCH_FRIEND =
        BaseURL.HTTP + BaseURL.IP + BaseURL.PORT_8686 + BaseURL.URL_SPLITTER
            + "LensWcfSrv.svc/SearchUsr/%s/%s/%s/%s";

    /**
     * 清除ios TOKEN
     */
    private static String URL_CLEAR_TOKEN =
        BaseURL.HTTP + BaseURL.IP + BaseURL.PORT_8686 + BaseURL.URL_SPLITTER
            + "LensWcfSrv.svc/SetAppIDToken/%s";
    /**
     * 验证是否为工号或者是否存在
     */
    private static String URL_IDENTY_USERNAME =
        BaseURL.HTTP + BaseURL.DOMAIN_NAME + BaseURL.PORT_8686 + BaseURL.URL_SPLITTER
            + "LensWcfSrv.svc/CheckEmpNO/%s";
    //http://mobile.fingerchat.cn:8181/LensWcfSrv.svc/upLog
    private static String URL_UPLOAD_LOG =
        BaseURL.HTTP + BaseURL.DOMAIN_NAME + BaseURL.PORT_8686 + BaseURL.URL_SPLITTER
            + "LensWcfSrv.svc/upLog";
    //http://mobile.fingerchat.cn:8686/LensWcfSrv.svc/GetMyMoments/{username}/{iPageNum}/{iPagesize}
    private static String URL_ALL_COMMENTS =
        BaseURL.HTTP + BaseURL.DOMAIN_NAME + BaseURL.PORT_8686 + BaseURL.URL_SPLITTER
            + "LensWcfSrv.svc/GetMyMoments/%s/%s/%s";
    //"http://mobile.fingerchat.cn:8686/LensWcfSrv.svc/GetMucNewName"
    private static String URL_NEW_ROOM_ID =
        BaseURL.HTTP + BaseURL.DOMAIN_NAME + BaseURL.PORT_8686 + BaseURL.URL_SPLITTER
            + "LensWcfSrv.svc/GetMucNewName";
    /**
     * 上传附件
     */
    public final static String URL_ATTACH_MESSAGES = "/Task/TaskInfo/AddTaskChatFile";

    /**
     * 获取新朋友的头像路径
     */
    public static String getAvatarPath(String username) {
        return String.format(obtainAvater, username);
    }

}
