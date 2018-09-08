package com.lensim.fingerchat.fingerchat.api;

import com.lens.core.componet.rx.RxSchedulers;
import com.lensim.fingerchat.commons.http.FXRequestManager;
import com.lensim.fingerchat.commons.http.FXRxSubscriberHelper;
import com.lensim.fingerchat.fingerchat.model.bean.BaseRequestBody;
import com.lensim.fingerchat.fingerchat.model.result.GetWorkCenterListResult;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * 工作中心
 */
public class WorkCenterApi {

    public interface Api {

        // 工作中心信息
        @POST("/xdata-proxy/WebApp/query")
        Observable<GetWorkCenterListResult> getWorkCenterList(@Body RequestBody requestBody);
    }

    private Api api;
    public WorkCenterApi() {
        api = FXRequestManager.getRequest(Api.class);
    }

    /**
     * 获取工作中心信息列表
     * @param rxSubscriberHelper
     */
    public void getWorkCenterList(FXRxSubscriberHelper<GetWorkCenterListResult> rxSubscriberHelper) {
        Map<String, Integer> map = new HashMap<>();
        map.put("pageIndex", 0);
        map.put("pageSize", 100);
        api.getWorkCenterList(new BaseRequestBody<>(map).toRequestBody())
            .compose(RxSchedulers.handleResult())
            .compose(RxSchedulers.rxSchedulerHelper())
            .subscribe(rxSubscriberHelper);
    }
}
