package com.lens.chatmodel.api;

import com.lens.chatmodel.bean.body.ThumbsUpRequestBody;
import com.lens.core.componet.rx.RxSchedulers;
import com.lensim.fingerchat.commons.base.BaseResponse;
import com.lensim.fingerchat.commons.base.data.BaseRequestBody;
import com.lensim.fingerchat.commons.http.FXRequestManager;
import com.lensim.fingerchat.commons.http.FXRxSubscriberHelper;
import com.lensim.fingerchat.commons.utils.CyptoUtils;
import com.lensim.fingerchat.data.me.circle_friend.FriendCircleInfo;
import io.reactivex.Observable;
import java.util.HashMap;
import java.util.Map;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * @time 2017/11/29 8:59
 * @class describe
 */

public class AlbumApi {
    public interface Api{

        // 朋友圈点赞
        @POST("/xdata-proxy/CirclesOfFriends/thumbsUp")
        Observable<BaseResponse> thumbsUp(@Body RequestBody requestBody);

        // 取消点赞
        @POST("/xdata-proxy/CirclesOfFriends/cancel/thumbsUp")
        Observable<BaseResponse> cancelThumbsUp(@Body RequestBody requestBody);//GET /v1/fxclient/friend-cricle/getSubject

        //根据单个说说序列号获取主体信息（包括图片和用户名）
        @POST("/xdata-proxy/v1/fxclient/friend-cricle/getSubject")
        Observable<BaseResponse<FriendCircleInfo>> getSubject(@Query("photoSerno") String photoSerno);

        // 刪除說說
        @GET("/xdata-proxy/CirclesOfFriends/delete/one/photo")
        Observable<BaseResponse> deletePhoto(@Query("photoSerno") String photoSerno);
    }
    private Api api;
    public AlbumApi(){
        this.api = FXRequestManager.getRequest(Api.class);
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
     * 取消点赞
     *
     * @param creatorUserId
     * @param photoSerno
     * @param thumbsUserId
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
     * 根据单个说说序列号获取主体信息（包括图片和用户名）
     * @param photoSero
     * @param rxSubscriberHelper
     */
    public void getSubject(String photoSero,FXRxSubscriberHelper<BaseResponse<FriendCircleInfo>> rxSubscriberHelper){
        api.getSubject(photoSero)
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
}
