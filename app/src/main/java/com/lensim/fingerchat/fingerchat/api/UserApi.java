package com.lensim.fingerchat.fingerchat.api;

import com.lens.core.componet.rx.RxSchedulers;
import com.lensim.fingerchat.commons.http.FXRequestManager;
import com.lensim.fingerchat.commons.http.FXRxSubscriberHelper;
import com.lensim.fingerchat.fingerchat.model.result.NewOATokenResult;
import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public class UserApi {
    public interface Api {

        // 获取OAToken
        @GET("/imsso/v2/tokenx/getNewOAToken")
        Observable<NewOATokenResult> getNewOAToken(@Query("fxToken") String fxToken);
    }

    private Api api;
    public UserApi() {
        api = FXRequestManager.getRequest(Api.class);
    }

    public void getNewOAToken(String ssoToken, FXRxSubscriberHelper<NewOATokenResult> rxSubscriberHelper) {
        api.getNewOAToken(ssoToken)
            .compose(RxSchedulers.handleResult())
            .compose(RxSchedulers.rxSchedulerHelper())
            .subscribe(rxSubscriberHelper);
    }

}
