package com.lensim.fingerchat.fingerchat.api;

import com.lens.core.componet.rx.RxSchedulers;
import com.lensim.fingerchat.commons.base.BaseResponse;
import com.lensim.fingerchat.commons.base.data.BaseRequestBody;
import com.lensim.fingerchat.commons.http.FXRequestManager;
import com.lensim.fingerchat.commons.http.FXRxSubscriberHelper;
import com.lensim.fingerchat.commons.utils.CyptoUtils;
import com.lensim.fingerchat.fingerchat.model.bean.CircleDetailsInfo;
import com.lensim.fingerchat.fingerchat.model.bean.CommentResponse;
import com.lensim.fingerchat.fingerchat.model.bean.FriendCircleInfo;
import com.lensim.fingerchat.fingerchat.model.bean.UnReadCommentInfo;
import com.lensim.fingerchat.fingerchat.model.bean.UpdateBgImgResponse;
import com.lensim.fingerchat.fingerchat.model.requestbody.CommentTalkRequestBody;
import com.lensim.fingerchat.fingerchat.model.requestbody.RevertCommentRequestBody;
import com.lensim.fingerchat.fingerchat.model.requestbody.SendPhotosRequestBody;
import com.lensim.fingerchat.fingerchat.model.requestbody.ThumbsUpRequestBody;
import com.lensim.fingerchat.fingerchat.model.result.GetPhotoResult;
import io.reactivex.Observable;
import java.util.HashMap;
import java.util.Map;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * 朋友圈APi接口
 */
public class CirclesFriendsApi {

    public interface Api {

        // 获取朋友圈未读说说信息
        @GET("/xdata-proxy/CirclesOfFriends/get/unread/photo")
        Observable<GetPhotoResult> getUnreadPhoto(@Query("userId") String userId);

        // 发朋友圈
        @POST("/xdata-proxy/CirclesOfFriends/pulish/photo")
        Observable<BaseResponse> sendPhoto(@Body RequestBody requestBody);

        // 发布纯文本说说
        @POST("/xdata-proxy/CirclesOfFriends/pulish/text/talk")
        Observable<BaseResponse> pulishTalkText(@Body RequestBody requestBody);

        // 回复评论
        @POST("/xdata-proxy/CirclesOfFriends/revert/comment")
        Observable<BaseResponse<CommentResponse>> revertComment(@Body RequestBody requestBody);

        // 朋友圈点赞
        @POST("/xdata-proxy/CirclesOfFriends/thumbsUp")
        Observable<BaseResponse> thumbsUp(@Body RequestBody requestBody);

        // 取消点赞
        @POST("/xdata-proxy/CirclesOfFriends/cancel/thumbsUp")
        Observable<BaseResponse> cancelThumbsUp(@Body RequestBody requestBody);

        // 获取本人及朋友说说信息列表
        @POST("/xdata-proxy/CirclesOfFriends/get/owner/info/page/id")
        Observable<GetPhotoResult> getPhotos(@Body RequestBody requestBody);

        // 刪除說說
        @GET("/xdata-proxy/CirclesOfFriends/delete/one/photo")
        Observable<BaseResponse> deletePhoto(@Query("photoSerno") String photoSerno);

        // 更新查看朋友圈时间
       // @GET("/xdata-proxy/CirclesOfFriends/add/new/see/photo/time")
        @GET("/xdata-proxy/v1/fxclient/friend-cricle/updateSeePhotoTime")
        Observable<BaseResponse> updateLookPhotoTime(@Query("userId") String userId);

        // 评论說說
        @POST("/xdata-proxy/CirclesOfFriends/comment/talk")
        Observable<BaseResponse<CommentResponse>> commentTalk(@Body RequestBody requestBody);

        // 获取未读的评论信息 GET /v1/fxclient/friend-cricle/list/getUnreadAddImage
        @GET("/xdata-proxy/v1/fxclient/friend-cricle/list/getUnreadAddImage")
        Observable<BaseResponse<UnReadCommentInfo>> getUnreadCommentInfo(@Query("userId") String userId);

        //更换朋友圈背景图片，如果没有数据会进行插入返回
        @POST("/xdata-proxy/CirclesOfFriends/updateBackgroundImage")
        Observable<BaseResponse<UpdateBgImgResponse>> updateBackgroundImage(@Query("userId") String userId,@Query("bgImageUrl") String bgImageUrl);

        //删除某条评论
        @GET("/xdata-proxy/v1/fxclient/friend-cricle/deleteComment")
        Observable<BaseResponse> deleteComment(@Query("commentSerno") String commentSerno);

        //通过id获取朋友圈说说列表
        @GET("/xdata-proxy/CirclesOfFriends/get/infoList/by/Id")
        Observable<BaseResponse<FriendCircleInfo>> getInfoListById(@Query("userId") String userId);

        //添加或修改最新查看评论的时间 ，触发后，最新评论清0
        @POST("/xdata-proxy/v1/fxclient/friend-cricle/addSeeCommentTime")
        Observable<BaseResponse> seeCommentTime(@Query("userId") String userId);

        //根据单个说说序列号获取主体信息（包括图片和用户名）
        @POST("/xdata-proxy/v1/fxclient/friend-cricle/getSubject")
        Observable<BaseResponse<CircleDetailsInfo>> getSubject(@Query("photoSerno") String photoSerno);

    }

    private Api api;

    public CirclesFriendsApi() {
        this.api = FXRequestManager.getRequest(Api.class);
    }

    /**
     * 发布纯文本说说
     *
     * @param creatorUserId
     * @param creatorUserName
     * @param photoContent
     * @param rxSubscriberHelper
     */
    public void pulishTalkText(String creatorUserId, String creatorUserName, String photoContent,
                               FXRxSubscriberHelper<BaseResponse> rxSubscriberHelper) {
        Map<String, String> map = new HashMap<>();
        map.put("creatorUserId", creatorUserId);
        map.put("creatorUserName", creatorUserName);
        map.put("photoContent", photoContent);

        api.pulishTalkText(new BaseRequestBody<>(map).toRequestBody())
            .compose(RxSchedulers.handleResult())
            .compose(RxSchedulers.rxSchedulerHelper())
            .subscribe(rxSubscriberHelper);
    }

    /**
     * 获取未读说说信息
     *
     * @param userId
     * @param rxSubscriberHelper
     */
    public void getUnreadPhoto(String userId, FXRxSubscriberHelper<GetPhotoResult> rxSubscriberHelper) {
        api.getUnreadPhoto(userId)
            .compose(RxSchedulers.handleResult())
            .compose(RxSchedulers.rxSchedulerHelper())
            .subscribe(rxSubscriberHelper);
    }

    /**
     * 发朋友圈  hh
     *
     * @param requestBody
     * @param rxSubscriberHelper
     */
    public void sendPhoto(SendPhotosRequestBody requestBody, FXRxSubscriberHelper<BaseResponse> rxSubscriberHelper) {
        api.sendPhoto(new BaseRequestBody<>(requestBody).toRequestBody())
            .compose(RxSchedulers.handleResult())
            .compose(RxSchedulers.rxSchedulerHelper())
            .subscribe(rxSubscriberHelper);
    }

    /**
     * 点赞
     *
     * @param requestBody
     * @param rxSubscriberHelper
     */
    public void thumbsUp(ThumbsUpRequestBody requestBody, FXRxSubscriberHelper<BaseResponse> rxSubscriberHelper) {
        api.thumbsUp(new BaseRequestBody<>(requestBody).toRequestBody())
            .compose(RxSchedulers.handleResult())
            .compose(RxSchedulers.rxSchedulerHelper())
            .subscribe(rxSubscriberHelper);
    }

    /**
     * 回复评论
     *
     * @param requestBody
     * @param rxSubscriberHelper
     */
    public void revertComment(RevertCommentRequestBody requestBody, FXRxSubscriberHelper<BaseResponse<CommentResponse>> rxSubscriberHelper) {
        api.revertComment(new BaseRequestBody<>(requestBody).toRequestBody())
            .compose(RxSchedulers.handleResult())
            .compose(RxSchedulers.rxSchedulerHelper())
            .subscribe(rxSubscriberHelper);
    }

    /**
     * 取消点赞
     *
     * @param creatorUserId  发朋友圈的人的id
     * @param photoSerno   说说序列号
     * @param thumbsUserId  点赞人的id
     * @param rxSubscriberHelper
     */
    public void cancelThumbsUp(String creatorUserId, String photoSerno, String thumbsUserId,
                               FXRxSubscriberHelper<BaseResponse> rxSubscriberHelper) {
        Map<String, String> map = new HashMap<>();
        map.put("creatorUserId", creatorUserId);
        map.put("photoSerno", photoSerno);
        map.put("thumbsUserId", CyptoUtils.encrypt(thumbsUserId));

        api.cancelThumbsUp(new BaseRequestBody<>(map).toRequestBody())
            .compose(RxSchedulers.handleResult())
            .compose(RxSchedulers.rxSchedulerHelper())
            .subscribe(rxSubscriberHelper);
    }

    /**
     * 获取本人及朋友说说信息列表
     * @param userId
     * @param rxSubscriberHelper
     */
    public void getPhotos(String userId, String pageNum, FXRxSubscriberHelper<GetPhotoResult> rxSubscriberHelper) {
        Map<String, String> map = new HashMap<>();
        map.put("userId", userId);
        map.put("pageNum", pageNum);
        map.put("pageSize", "5");

        api.getPhotos(new BaseRequestBody<>(map).toRequestBody())
            .compose(RxSchedulers.handleResult())
            .compose(RxSchedulers.rxSchedulerHelper())
            .subscribe(rxSubscriberHelper);
    }

    /**
     * 刪除某條說說
     * @param photoSerno
     * @param rxSubscriberHelper
     */
    public void deletePhoto(String photoSerno, FXRxSubscriberHelper<BaseResponse> rxSubscriberHelper) {
        api.deletePhoto(photoSerno)
            .compose(RxSchedulers.handleResult())
            .compose(RxSchedulers.rxSchedulerHelper())
            .subscribe(rxSubscriberHelper);
    }

    /**
     * 更新查看朋友圈时间
     * @param userId
     * @param rxSubscriberHelper
     */
    public void updateLookPhotoTime(String userId, FXRxSubscriberHelper<BaseResponse> rxSubscriberHelper) {
        api.updateLookPhotoTime(userId)
            .compose(RxSchedulers.handleResult())
            .compose(RxSchedulers.rxSchedulerHelper())
            .subscribe(rxSubscriberHelper);
    }

    /**
     * 评论说说
     * @param requestBody
     * @param rxSubscriberHelper
     */
    public void commentTalk(CommentTalkRequestBody requestBody,FXRxSubscriberHelper<BaseResponse<CommentResponse>> rxSubscriberHelper){
        api.commentTalk(new BaseRequestBody<>(requestBody).toRequestBody())
            .compose(RxSchedulers.handleResult())
            .compose(RxSchedulers.rxSchedulerHelper())
            .subscribe(rxSubscriberHelper);
    }

    /**
     * 更换朋友圈背景图片，如果没有数据会进行插入返回
     * @param imageUrl 图片文件路径
     * @param rxSubscriberHelper
     */
    public void updateBackgroundImage(String userId,String imageUrl,FXRxSubscriberHelper<BaseResponse<UpdateBgImgResponse>> rxSubscriberHelper){

        api.updateBackgroundImage(userId,imageUrl)
            .compose(RxSchedulers.handleResult())
            .compose(RxSchedulers.rxSchedulerHelper())
            .subscribe(rxSubscriberHelper);
    }

    /**
     * 删除某条评论
     * @param commentSerno 评论序列号
     * @param rxSubscriberHelper
     */
    public void deleteComment(String commentSerno,FXRxSubscriberHelper<BaseResponse> rxSubscriberHelper){
        api.deleteComment(commentSerno)
            .compose(RxSchedulers.handleResult())
            .compose(RxSchedulers.rxSchedulerHelper())
            .subscribe(rxSubscriberHelper);
    }

    /**
     *获取未读的评论信息 包括主体信息 评论点赞
     * @param userId
     * @param rxSubscriberHelper
     */
    public void getUnreadCommentInfo(String userId ,FXRxSubscriberHelper<BaseResponse<UnReadCommentInfo>> rxSubscriberHelper){
        api.getUnreadCommentInfo(userId)
            .compose(RxSchedulers.handleResult())
            .compose(RxSchedulers.rxSchedulerHelper())
            .subscribe(rxSubscriberHelper);
    }

    /**
     * 通过id获取朋友圈说说列表
     * @param id
     * @param rxSubscriberHelper
     */
    public void getInfoListById(String id ,FXRxSubscriberHelper<BaseResponse<FriendCircleInfo>> rxSubscriberHelper){
        api.getInfoListById(id)
            .compose(RxSchedulers.handleResult())
            .compose(RxSchedulers.rxSchedulerHelper())
            .subscribe(rxSubscriberHelper);
    }

    /**
     * 添加或修改最新查看评论的时间 ，触发后，最新评论清0
     * @param userId 用户的id
     * @param rxSubscriberHelper
     */
    public void seeCommentTime(String userId,FXRxSubscriberHelper<BaseResponse> rxSubscriberHelper){
        api.seeCommentTime(userId)
            .compose(RxSchedulers.handleResult())
            .compose(RxSchedulers.rxSchedulerHelper())
            .subscribe(rxSubscriberHelper);
    }

    /**
     * 根据单个说说序列号获取主体信息（包括图片和用户名）
     * @param photoSero
     * @param rxSubscriberHelper
     */
    public void getSubject(String photoSero,FXRxSubscriberHelper<BaseResponse<CircleDetailsInfo>> rxSubscriberHelper){
        api.getSubject(photoSero)
            .compose(RxSchedulers.handleResult())
            .compose(RxSchedulers.rxSchedulerHelper())
            .subscribe(rxSubscriberHelper);
    }
}
