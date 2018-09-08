package com.lensim.fingerchat.fingerchat.api;

import com.lens.core.componet.rx.RxSchedulers;
import com.lensim.fingerchat.commons.base.BaseResponse;
import com.lensim.fingerchat.commons.http.FXRequestManager;
import com.lensim.fingerchat.commons.http.FXRxSubscriberHelper;
import com.lensim.fingerchat.fingerchat.model.bean.BaseRequestBody;
import com.lensim.fingerchat.fingerchat.model.result.GetVersionInfoResult;
import com.lensim.fingerchat.fingerchat.model.result.UserPrivilegesResult;

import java.util.Map;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Query;

public class SystemApi {

    public interface Api {

        // 上传错误日志
        @POST("/xdata-proxy/client/uploadLog")
        Observable<BaseResponse> uploadLogger(@Body RequestBody requestBody);

        // 获取角色权限
        @POST("/xdata-proxy/v1/db/privileges/getUserPrivileges")
        Observable<UserPrivilegesResult> getUserPrivileges(@Query("userId") String userId);

        // 获取App最新版本信息
        @POST("/xdata-proxy/v1/client/getAppVersion")
        Observable<GetVersionInfoResult> getVersionInfo(@Query("appid") String appid,
                                                        @Query("appclient") String appclient);
    }

    private Api api;

    public SystemApi() {
        api = FXRequestManager.getRequest(Api.class);
    }

    /**
     * 上传错误日志
     *
     * @param map
     * @param rxSubscriberHelper
     */
    public void uploadLogger(Map<String, String> map, FXRxSubscriberHelper<BaseResponse> rxSubscriberHelper) {
        api.uploadLogger(new BaseRequestBody<>(map).toRequestBody())
            .compose(RxSchedulers.handleResult())
            .compose(RxSchedulers.rxSchedulerHelper())
            .subscribe(rxSubscriberHelper);
    }

    /**
     * 获取资源信息
     *
     * @param userId
     * @param rxSubscriberHelper
     */
    public void getUserPrivileges(String userId, FXRxSubscriberHelper<UserPrivilegesResult> rxSubscriberHelper) {
        api.getUserPrivileges(userId)
            .compose(RxSchedulers.rxSchedulerHelper())
            .subscribe(rxSubscriberHelper);
    }

    /**
     * 获取最新版本信息
     *
     * @param rxSubscriberHelper
     */
    public void getVersionInfo(FXRxSubscriberHelper<GetVersionInfoResult> rxSubscriberHelper) {
        api.getVersionInfo("com.feige.fingerchat", "android")
            .compose(RxSchedulers.handleResult())
            .compose(RxSchedulers.rxSchedulerHelper())
            .subscribe(rxSubscriberHelper);
    }
}
