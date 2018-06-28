package com.lensim.fingerchat.data;


import com.lensim.fingerchat.commons.global.Route;
import com.lensim.fingerchat.data.hexmeet.LockUser;
import com.lensim.fingerchat.data.hexmeet.RoomInfo;
import com.lensim.fingerchat.data.hexmeet.VideoMeeting;
import com.lensim.fingerchat.data.hrcs.HRCS;
import com.lensim.fingerchat.data.login.SSOToken;
import com.lensim.fingerchat.data.me.NewComment;
import com.lensim.fingerchat.data.me.circle_friend.FriendCircleEntity;
import com.lensim.fingerchat.data.response.ResponseObject;
import com.lensim.fingerchat.data.response.ret.RetArrayResponse;
import com.lensim.fingerchat.data.response.ret.RetObjectResponse;
import com.lensim.fingerchat.data.response.ret.RetResponse;
import com.lensim.fingerchat.data.work_center.OAToken;
import com.lensim.fingerchat.data.work_center.identify.UserIdentify;
import com.lensim.fingerchat.data.work_center.sign.SPListResponse;
import com.lensim.fingerchat.data.work_center.sign.SignInPicture;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import java.util.List;
import java.util.Map;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * 请求参数接口
 * Created by yangle on 2017/6/19.
 */

public interface RetrofitService {


    /**
     * 统一 动态url请求
     */
    @GET
    Observable<ResponseBody> getMethod(@NonNull @Url String url);


    @Streaming
    @GET
    Observable<ResponseBody> downloadFileWithDynamicUrlAsync(@Url String fileUrl);

    /**
     * 上传图片
     */
    @POST(Route.UPLOAD_IMAGE)
    Observable<ResponseBody> uploadImage(@Body RequestBody body);

    /**
     * 上传视频
     */
    @POST(Route.UPLOAD_VIDEO)
    Observable<ResponseBody> uploadVideo(@Body RequestBody body);

    /**
     * 上传语音
     */
    @POST(Route.UPLOAD_VOICE)
    Observable<ResponseBody> uploadVoice(@Body RequestBody body);


    /**
     * 录制视频
     */
    @POST(Route.Host + Route.SendVideoUrl)
    Flowable<ResponseBody> sendVideoAndText(@Body MultipartBody imgs);

    /**
     * 搜索好友
     */
    @POST(Route.SEARCH_USER_LIST)
    Observable<ResponseBody> searchUserList(@Body RequestBody body);

    /**
     * 获取用户信息
     * http:xxxx?userid=xx
     */
    @GET(Route.SEARCH_USER)
    Observable<ResponseBody> getUserInfo(@Query("userid") String userId);


    /**
     * 登陆_SSO
     */
    @GET(Route.SSO_HOST + Route.URL_SSO_LOGIN)
    Observable<ResponseObject<SSOToken>> SSOLogin(@Query("userId") String userId,
        @Query("password") String psw, @Query("clientType") String type);

    /**
     * 登出_SSO
     */
    @GET(Route.SSO_HOST + Route.URL_SSO_LOGIN_OUT)
    Observable<ResponseObject<SSOToken>> SSOLoginOut(@Query("fxtoken") String token);

    /**
     * 第三方授权登录
     */
    @GET(Route.SSO_HOST + Route.URL_ACCEPT_SSO_LOGIN)
    Observable<ResponseBody> acceptQRCodeLogin(@Query("token") String token,
        @Query("appId") String appId, @Query("qrcodeId") String codeId);

    /**
     * 获取OAToKen
     */
    //  @POST("http://syscpc.fingersystem.cn:8686/v1/user/getOAToken")
    @POST(Route.Host + Route.URL_GET_OA_TOKEN)
    Observable<ResponseObject<OAToken>> getOAToken(@Body RequestBody body);

    /**
     * 工作中心获取子项目
     */
    @GET(Route.Host + Route.URL_GET_FUNCTIONS + "/{empno}/{token}")
    Observable<ResponseBody> getFunctions(@Path("empno") String empno, @Path("token") String token);


    /**
     * 申请视频会议帐号
     */
    //  @GET("http://10.3.7.140:8685/HexSer/HexUser/LockUser/" + "{num}")
    @GET("http://synccenter.fingersystem.cn:8686/HexUser/LockUser/{num}")
    Observable<LockUser> applyForCameras(@Path("num") int num);


    /**
     * 获取当前admin的token
     * 用于结束会议等操作时所需的admin权限
     */
//  @GET("http://10.3.7.140:8685/HexSer/HexUser/GetT/feige")
//  @GET("http://synccenter.fingersystem.cn:8686/HexSer/HexUser/GetT/feige")
    @GET(Route.Host + Route.ADMIN_TOKEN)
    Observable<String> getTempToken();


    /**
     * 获取会议列表
     */
    @GET(
        Route.Host + Route.URL_HEX_GET_MEETING_LIST + "/{token}/{type}/{user}/{pageSize}/{pageNum}")
//  @GET("http://10.3.7.149:8181/LensWcfSrv.svc/getMeetingList/" + "/{token}/{type}/{user}/{pageSize}/{pageNum}")
    Observable<RetArrayResponse<VideoMeeting>> getHexMeetingList(@Path("token") String token,
        @Path("type") String type, @Path("user") String user, @Path("pageSize") String pageSize,
        @Path("pageNum") String pageNum);

    /**
     * 会议信息录入
     */
    @POST(Route.Host + Route.URL_HEX_MEETING_CREAT)
//  @POST("http://10.3.7.149:8181/LensWcfSrv.svc/meetingcreat")
    Observable<RetObjectResponse<String>> postHexMeeting(@Body RequestBody body);


    /**
     * 删除会议
     */
    @GET(Route.Host + Route.URL_HEX_MEETING_DELETE + "/{id}/{userid}/{token}")
//  @GET("http://10.3.7.149:8181/LensWcfSrv.svc/getMeetingList/" + "/{token}/{type}/{user}/{pageSize}/{pageNum}")
    Observable<RetObjectResponse<String>> deleteHexMeeting(@Path("id") String id,
        @Path("userid") String userid, @Path("token") String token);


    /**
     * 给已存在的会议添加联系人
     */
//  @POST("http://10.3.7.149:8181/LensWcfSrv.svc/JoinToExistMeeting")
    @POST(Route.Host + Route.HEX_MEET_JOINTO_EXISTMEETING)
    Observable<RetObjectResponse<String>> JoinToExistMeeting(@Body RequestBody body);


    /**
     * 获取聊天室所有群成员
     */
    @GET(Route.Host + Route.URL_GetMucMembers)
    Observable<List<RoomInfo>> getMucMember(@Query("fun") String fun,
        @Query("userid") String userid, @Query("teamserno") String teamserno);

    /**
     * 签到
     */
    @POST(Route.Host + Route.URL_SIGN_IN)
    Observable<RetObjectResponse<String>> signIn(@Body RequestBody body);

    /**
     * 签到查询
     */
    @GET(Route.Host + Route.URL_GET_SIGN_IN + "/{userId}/{fromDate}/{toDate}/{token}")
    Observable<RetObjectResponse<String>> getSignIn(@Path("userId") String userId,
        @Path("fromDate") String fromDate, @Path("toDate") String toDate,
        @Path("token") String token);


    /**
     * 认证
     */
    @GET
    Observable<ResponseBody> certification(@NonNull @Url String url);


    /**
     * 获取用户个人信息
     */
    @GET(Route.Host + Route.URL_GetOrUpdateUserInfo)
    Observable<List<UserIdentify>> getUserInfoByAsync(@Query("fun") String fun,
        @Query("userid") String userid);

    /**
     * 认证，上传图片
     */
    @POST(Route.Host + Route.URL_UPLOAD_IDCARD)
    Observable<RetObjectResponse<String>> sendIdcard(@Body MultipartBody imgs);


    /**
     * 签到——上传图片
     */
    @Multipart
    @POST(Route.Host + Route.URL_SIGN_IN_POST_IMAGE)
    Observable<SPListResponse<SignInPicture>> signInPostPicture(@Part MultipartBody.Part part);


    /**
     * 上传头像 ——3处
     * 1、用户修改头像
     * 2、朋友圏背景图
     * 3、注册上传头像
     */
    @POST(Route.Host + Route.URL_UpLoadAvater)
    Flowable<String> setAvatar(@Body MultipartBody imgs);


    /**
     * 获取客服
     */
    @GET
    Observable<RetArrayResponse<HRCS>> getHrNumber(@NonNull @Url String url);


    /**
     * 更新朋友圈信息
     */
    @GET(Route.Host + Route.FriendCircleUrl)
    Flowable<List<FriendCircleEntity>> UpdateFriendCircle(@Query("fun") String fun,
        @Query("userid") String userid);


    /**
     * 朋友圈——上拉刷新
     */
    @GET(Route.Host + Route.FriendCircleUrl)
//  Observable<List<FriendCircleEntity>> loadMoreCircleData(@Query("fun") String fun, @Query("userid") String userid, @Query("pagesize") String pagesize, @Query("pagenum") String pagenum);
    Flowable<List<FriendCircleEntity>> loadMoreCircleData(@QueryMap Map<String, String> options);


    /**
     * 获取最新评论的数量
     */
    //http://mobile.fingerchat.cn:8686/LensWcfSrv.svc/getnewCommentNum/ll117394
    @GET(Route.Host + Route.NewUpdateCircleUrl + "/{fun}/{username}")
    Flowable<RetObjectResponse<String>> getNewCommentCount(@Path("fun") String pho_serno,
        @Path("username") String username);


    /**
     * 删除——朋友圈
     */
    @GET(Route.Host + Route.URL_DEL_COMMENT + "/1/{cricleId}/{userName}")
    Flowable<RetObjectResponse<String>> deleteCircle(@Path("cricleId") String cricleId,
        @Path("userName") String userName);


    /**
     * 获取单条分享的所有评论
     */
    @GET(Route.Host + Route.URL_ITEM + "/{pho_serno}/{username}")
    Flowable<RetObjectResponse<String>> getPhotoItemById(@Path("pho_serno") String pho_serno,
        @Path("username") String username);


    /**
     * 点赞
     */
    @GET(Route.Host + Route.FriendCircleUrl)
    Flowable<ResponseBody> like(@QueryMap Map<String, String> options);


    /**
     * 删除——赞
     */
    @GET(Route.Host + Route.FriendCircleUrl)
    Flowable<ResponseBody> cancelLike(@Query("fun") String fun,
        @Query("CommentUserid") String commentUserid,
        @Query("photoserno") String photoserno, @Query("CreateUserid") String createUserid);


    /**
     * 朋友圈——评论
     */
    @POST(Route.Host + Route.URL_COMMENT)
    Flowable<RetObjectResponse<String>> addComment(@Body RequestBody body);


    /**
     * 删除——评论
     * 要么是/1/cba5d713e1/ll032197  , 1代表了 createuser, 即朋友圈的创建者
     * 要么是/2/cba5d713e1/ll117394 , 2代表的是 commentuser, 即 发表评论的人
     */
    @GET(Route.Host + Route.URL_DEL_COMMENT + "/2/{phc_serno}/{commentUser}")
    Flowable<RetObjectResponse<String>> deleteComment(@Path("phc_serno") String phc_serno,
        @Path("commentUser") String commentUser);


    /**
     * 分页加载评论列表
     */
    @GET(Route.Host + Route.URL_ALL_COMMENTS_BY_TIME
        + "/{userName}/{pageNum}/{pagesize}/{timeStamp}")
    Flowable<RetObjectResponse<String>> getCommentsByPage(@Path("userName") String userName,
        @Path("pageNum") String pageNum, @Path("pagesize") String pagesize,
        @Path("timeStamp") String timeStamp);


    /**
     * 新的评论列表
     */
    @GET(Route.Host + Route.UpdateCircleUrl)
    Flowable<List<NewComment>> getNewComment(@Query("fun") String fun,
        @Query("userid") String userid);


    /**
     * 更新查看评论列表时间
     */
    @GET(Route.Host + Route.UpdateCircleUrl)
    Flowable<RetObjectResponse<String>> addSeeCommentTime(@Query("fun") String fun,
        @Query("userid") String userid);

    /**
     * 相册——更新
     */
    @GET(Route.Host + Route.FriendCircleUrl)
    Flowable<List<FriendCircleEntity>> getPhotos(@Query("fun") String fun,
        @Query("userid") String userid);


    /**
     * 收藏列表——获取
     */
    @GET(Route.Host + Route.GetFavListUrl + "/{username}/{pagenum}/{pagesize}")
    Flowable<RetObjectResponse<String>> getFavList(@Path("username") String username,
        @Path("pagenum") String pagenum, @Path("pagesize") String pagesize);

    /**
     * 收藏——删除
     */
    @GET(Route.Host + Route.DelFavUrl + "/{msgId}/{username}")
    Flowable<RetResponse> removeFavItem(@Path("msgId") String msgId,
        @Path("username") String username);

    /**
     * 上传日志
     */
    @POST(Route.Host + Route.URL_OPTION_LOG)
    Flowable<ResponseBody> uploadLog(@Body RequestBody body);

    /**
     * 修改密码
     */
    @POST(Route.UPDATE_PASSWORD)
    Observable<ResponseObject> updatePassword(@Body RequestBody requestbody);

    @POST(Route.Host + Route.URL_OPTION_LOGGER)
    Observable<ResponseBody> uploadlogger(@Body RequestBody body);

    /**
     * 收藏列表——新建
     */
    @POST(Route.Host + Route.CreateFavUrl)
    Flowable<ResponseBody> createFavList(@Body RequestBody body);

    @Streaming
    @GET
    Flowable<ResponseBody> downloadVideoFile(@Url String fileUrl);

    /**
     * 认证
     */
    @GET
    Observable<ResponseBody> acceptQRCodeLogin(@NonNull @Url String url);

    /**
     * 上传附件
     */
    @POST(Route.URL_ATTACH_MESSAGES)
    Observable<ResponseBody> uploadAttackMessage(@Body RequestBody body);
}
