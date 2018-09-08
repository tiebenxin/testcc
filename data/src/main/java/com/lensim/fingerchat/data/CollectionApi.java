package com.lensim.fingerchat.data;

import com.lens.core.componet.rx.RxSchedulers;
import com.lensim.fingerchat.commons.base.BaseResponse;
import com.lensim.fingerchat.commons.base.data.BaseRequestBody;
import com.lensim.fingerchat.commons.http.FXRequestManager;
import com.lensim.fingerchat.commons.http.FXRxSubscriberHelper;
import com.lensim.fingerchat.data.bean.AddFavoryRequestBody;
import com.lensim.fingerchat.data.bean.GetFavoListBody;
import com.lensim.fingerchat.data.bean.GetFavoListResponse;
import com.lensim.fingerchat.data.bean.QueryCollectionBody;
import io.reactivex.Observable;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * @time 2017/11/29 8:59
 * @class describe 收藏页相关的接口声明
 */

public class CollectionApi {
    public interface Api{
        //新增用户收藏
        @POST("/xdata-proxy/v1/fxclient/user-favorites/add")
        Observable<BaseResponse> addFavory(@Body RequestBody requestBody);

        //删除用户收藏
        @POST("/xdata-proxy/v1/fxclient/user-favorites/delete")
        Observable<BaseResponse> deleteCollection(@Query("creator") String creator,@Query("msgId") String msgId);

        //获取用户收藏
        @POST("/xdata-proxy/v1/fxclient/user-favorites/get")
        Observable<BaseResponse> getUserFavorites(@Query("creator") String creator,@Query("msgId") String msgId);

        //分页查找用户收藏
        @POST("/xdata-proxy/v1/fxclient/user-favorites/query")
        Observable<BaseResponse> favoritesQuery(@Body RequestBody requestBody);

        //修改用户收藏
        @POST("/xdata-proxy/v1/fxclient/user-favorites/update")
        Observable<BaseResponse> favoritesUpdate(@Body RequestBody requestBody);

        //获取用户收藏列表
        @POST("/xdata-proxy/v1/fxclient/user-favorites/get/list")
        Observable<BaseResponse<GetFavoListResponse>> getAllFavoList(@Query("creator") String creator,@Body RequestBody requestBody);
    }

    private Api api;
    public CollectionApi(){
        this.api = FXRequestManager.getRequest(Api.class);
    }

    /**
     * 新增收藏用户
     * @param requestBody
     * @param rxSubscriberHelper
     */
    public void addFavory(AddFavoryRequestBody requestBody,FXRxSubscriberHelper<BaseResponse> rxSubscriberHelper){
        api.addFavory(new BaseRequestBody<>(requestBody).toRequestBody())
            .compose(RxSchedulers.handleResult())
            .compose(RxSchedulers.rxSchedulerHelper())
            .subscribe(rxSubscriberHelper);

    }

    /**
     * 删除用户收藏
     * @param creator
     * @param msgId
     * @param rxSubscriberHelper
     */
    public void deleteCollection(String creator,String msgId,FXRxSubscriberHelper<BaseResponse> rxSubscriberHelper){
        api.deleteCollection(creator,msgId)
            .compose(RxSchedulers.handleResult())
            .compose(RxSchedulers.rxSchedulerHelper())
            .subscribe(rxSubscriberHelper);
    }

    /**
     * 获取用户收藏
     * @param creator
     * @param msgId
     * @param rxSubscriberHelper
     */
    public void getUserFavorites(String creator,String msgId,FXRxSubscriberHelper<BaseResponse> rxSubscriberHelper){
        api.getUserFavorites(creator,msgId)
            .compose(RxSchedulers.handleResult())
            .compose(RxSchedulers.rxSchedulerHelper())
            .subscribe(rxSubscriberHelper);
    }

    /**
     * 分页查找用户收藏
     * @param body
     * @param rxSubscriberHelper
     */
    public void queryFavo(QueryCollectionBody body,FXRxSubscriberHelper<BaseResponse> rxSubscriberHelper){
        api.favoritesQuery(new BaseRequestBody<>(body).toRequestBody())
            .compose(RxSchedulers.handleResult())
            .compose(RxSchedulers.rxSchedulerHelper())
            .subscribe(rxSubscriberHelper);
    }

    /**
     * 修改用户收藏 通过msgId和收藏者creator修改
     * @param body
     * @param rxSubscriberHelper
     */
    public void favoritesUpdate(AddFavoryRequestBody body ,FXRxSubscriberHelper<BaseResponse> rxSubscriberHelper){
        api.favoritesUpdate(new BaseRequestBody<>(body).toRequestBody())
            .compose(RxSchedulers.handleResult())
            .compose(RxSchedulers.rxSchedulerHelper())
            .subscribe(rxSubscriberHelper);
    }

    /**
     * 获取用户收藏列表
     * @param creator
     * @param body
     * @param rxSubscriberHelper
     */
    public void getAllFavoList(String creator,GetFavoListBody body,FXRxSubscriberHelper<BaseResponse<GetFavoListResponse>> rxSubscriberHelper){
        api.getAllFavoList(creator,new BaseRequestBody<>(body).toRequestBody())
            .compose(RxSchedulers.handleResult())
            .compose(RxSchedulers.rxSchedulerHelper())
            .subscribe(rxSubscriberHelper);
    }
}
