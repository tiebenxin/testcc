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

    public static final String UPLOAD_IMAGE_SAVE = BaseURL.BASE_URL_UPLOAD + "/DFS/ImageSave";


    public static final String SEARCH_USER_LIST = "/xdata-proxy/user/getUserInfoList";
    public static final String SEARCH_USER = "/xdata-proxy/user/getUserInfo";
    public static final String UPDATE_PASSWORD = "/xdata-proxy/user/updatePassword";
    public static final String Host = BaseURL.BASE_URL + "/";

    public static final String UPLOAD_LOG = "/xdata-proxy/v1/log";

    /**
     * 群成员添加与删除
     * 方法：GET
     * "userid", 用户名
     * "fun", 方法名：getmember
     * "username", 昵称
     * "roomname", 房间名称
     */
    public static final String URL_MucMember = "/servers/addMucmember.ashx";
    /**
     * 获取所有的群成员
     * 方法：GET
     * "userid", 用户名
     * "fun",方法名： "teamdetail"
     * "teamserno", 房间名称
     */
    public static final String URL_GetMucMembers = "/servers/getTeam.ashx";
    public static final String URL_REGISTER = "/LensWcfSrv.svc/r2";
    /**
     * 上传头像
     * 方法：POST
     * userid", 用户名
     * photoContent", 头像文件
     */
    public static final String URL_UpLoadAvater = "/servers/PostImage.aspx";
    /**
     * 获取用户个人信息
     * 方法：GET
     * "fun", 方法名: "getuser"
     * "userid",用户名
     */
    public static final String URL_GetOrUpdateUserInfo = "/servers/getUserInfo.ashx";
    /**
     * 获取所有的好友信息
     * 方法：GET
     * "fun",方法名:"myFriend"
     * "userid",用户名
     */
    public static final String URL_GetAllFriendsInfo = "/servers/getFriend.ashx";
    /**
     * 上传用户认证信息
     */
    public static final String URL_UPLOAD_IDCARD = "/servers/cerAuth.ashx";
    /**
     * 发送图片或者音频
     */
    public static final String FileUri = "/servers/postMessage.ashx";
    /**
     * 获取所有好友的朋友圈分享
     */
    public static final String FriendCircleUrl = "/servers/getPhoto.ashx";

    //http://mobile.fingerchat.cn:8686/LensWcfSrv.svc/r/{n}/{p}/{h}/{pictype}
    /**
     * 刷新查看朋友圈的时间
     */
    public static final String UpdateCircleUrl = "/servers/getPhotoView.ashx";
    /**
     * 创建收藏列表
     */
    public static final String CreateFavUrl = "/fav/CreateFav";
    /**
     * 获取收藏列表
     */
    public static final String GetFavListUrl = "/LensWcfSrv.svc/GetFav";
    /**
     * 删除收藏
     */
    public static final String DelFavUrl = "/LensWcfSrv.svc/DelFav";


    public static final String NewUpdateCircleUrl = "/LensWcfSrv.svc";
    /**
     * 分享小视频
     */
    public static final String SendVideoUrl = "/LensFS/VideoFrame";
    /**
     * 登陆SSO
     */
    public static final String URL_SSO_LOGIN = "/imsso/v2/fxtoken/login";

    /**
     * 登陆SSO,手机号
     */
    public static final String URL_SSO_LOGIN_BY_PHONE = "/imsso/v2/fxtoken/tel-login";
    /**
     * 登出__SSO
     */
    public static final String URL_SSO_LOGIN_OUT = "/imsso/v2/fxtoken/logout";

    /**
     * 实名认证
     */
    public static final String URL_USER_AUTH = "xdata-proxy/user/auth";

    /**
     * 登陆__SSO
     */
    public static final String URL_OA_TOKEN = "/imsso/v2/tokenx/getNewOAToken";

    /**
     * 手机登陆
     */
    public static final String URL_PHONE_LOGIN = "LensWcfSrv.svc/GetUsrInfPost";
    /**
     * 修改密码
     */
    public static final String URL_ChangePwd = "LensWcfSrv.svc/CUP";
    /**
     * 获取最新的朋友圈发布者
     */
    //http://10.3.7.147:8090/servers/getPhotoView.ashx?userid={用户名}&fun=getnewPhoto
    public static final String URL_NEW_CIRCLE_PUBLISHER = "servers/getPhotoView.ashx";

    public static final String URL_ACCEPT_SSO_LOGIN = "/imsso/v2/oauth2/acceptQrtcodeLogin";

    public static final String URL_QRCODE_LOGIN = "/imsso/v2/oauth2/qrtcodeLogin";
    /**
     * 删除评论
     */
    public static final String URL_DEL_COMMENT = "/LensWcfSrv.svc/MvMoments";
    /**
     * 评论
     */
    // http://10.3.7.142:8181/LensWcfSrv.svc/MomentsComment/{func}/{CreateUserid}/{CreateUsername}/{CommentUserid}/{CommentUsername}/{photoserno}/{content}/{secondid=null}/{secondname=null}
    //private static String URL_COMMENT = HTTP + IP + PORT_8181 + URL_SPLITTER + "LensWcfSrv.svc/MomentsComment/%s/%s/%s/%s/%s/%s/%s/%s/%s";
    public static final String URL_COMMENT = "/LensWcfSrv.svc/mc";
    /**
     * 签到
     */
    public static final String URL_SIGN_IN = "/xdata-proxy/v1/fxclient/empAttendance/add";
    /**
     * 签到查询
     */
    public static final String URL_GET_SIGN_IN = "/LensWcfSrv.svc/GetEmpSignIn";
    /**
     * 上传图片
     */
    public static final String URL_SIGN_IN_POST_IMAGE = "/LensFS/Image";

    /**
     * 检查用户是否被锁定
     */
    public static final String URL_ALL_COMMENTS_BY_TIME = "/LensWcfSrv.svc/GetMyComments";
    public static final String URL_OPTION_LOGGER = "/v1/kafka/applog/add";

    /**
     * 获取单条分享的所有评论
     */
    public static final String URL_ITEM = "/LensWcfSrv.svc/GetPhotoComment";

    public static final String URL_GET_FUNCTIONS = "/LensWcfSrv.svc/GetFuncInfByEmpno";
    public static final String URL_GET_HR =
        BaseURL.BASE_URL + "/LensWcfSrv.svc/GetHRCS/%s";


    /**
     * Hex
     * 会议信息录入
     * 方法：post
     */
    public static final String URL_HEX_MEETING_CREAT = "/LensWcfSrv.svc/meetingcreat";


    /**
     * Hex
     * 会议信息删除
     * 方法：post
     */
    public static final String URL_HEX_MEETING_DELETE = "/LensWcfSrv.svc/delMeeting";


    /**
     * Hex
     * 会议列表查询
     * 方法：GET
     */
    public static final String URL_HEX_GET_MEETING_LIST = "/LensWcfSrv.svc/getMeetingList";

    /**
     * Hex
     * 给已存在的会议添加联系人的接口:
     * 方法：POST
     */
    public static final String HEX_MEET_JOINTO_EXISTMEETING = "/LensWcfSrv.svc/JoinToExistMeeting";

    /**
     * 获取当前admin的token
     * 用于结束会议等操作时所需的admin权限
     */
    public static final String ADMIN_TOKEN = "/HexUser/GetT/feige";

    /**
     * 获取成员头像
     * 方法：GET
     * %s:用户名
     */
    public static String obtainAvater =
        BaseURL.BASE_URL + "/HnlensImage/Users/%s/Avatar/headimage.png";
    /**
     * 获取朋友圈主题背景图片
     * 方法：GET
     * %s:用户名
     */
    public static String obtainTheme =
        BaseURL.BASE_URL + "/HnlensImage/Users/%s/Avatar/themeimage.png";
    /**
     * 获取用户001代码权限
     */
    public static String URL_AUTHORITY_SUBJECT =
        BaseURL.BASE_URL + "/GetUserPower/GetPowerByUserIDParentID/%s";

    /**
     * 发表评论
     */
    public static String SendCircleUrl = BaseURL.BASE_URL + "/LensFS/Image";

    //http://fingerchat.cn:8181/LensWcfSrv.svc/CheckIdCard/{工号}/{身份证号}/{用户名}
    public static String URL_Identify =
        BaseURL.BASE_URL + "/LensWcfSrv.svc/CheckIdCard/%s/%s/%s";
    /**
     * 获取工作中心的图片
     */
    public static String WORK_ITEM_IMG = BaseURL.BASE_URL + "/hnlensImage/and/%s";
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
