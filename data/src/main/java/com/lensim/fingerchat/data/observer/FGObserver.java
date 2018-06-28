package com.lensim.fingerchat.data.observer;

import android.util.Log;
import com.lensim.fingerchat.commons.base.BaseObserver;
import com.lensim.fingerchat.commons.global.Code;
import com.lensim.fingerchat.data.response.Response;
import okhttp3.ResponseBody;

/**
 * date on 2018/1/12
 * author ll147996
 * describe
 */

public abstract class FGObserver<T> extends BaseObserver<T> {

    private static final String TAG = "FGObserver";

    public FGObserver() {
        super();
    }

    public FGObserver(boolean isShowProgress){
        super(isShowProgress);
    }

    @Override
    public void onNext(T response) {
        if (response instanceof Response) {
            Response res = (Response) response;
            if (Code.OK == res.code || Code.CREATED == res.code || Code.ACCEPTED == res.code) {
                onHandleSuccess(response);
            }
            if (Code.BAD_REQUEST == res.code || Code.UNAUTHORIZED == res.code
                || Code.NOT_FOUND == res.code || Code.SERVER_ERROR == res.code) {
                onHandleError(response);
            }
        } else if (response instanceof ResponseBody) {
            onHandleSuccess(response);
        }
    }

    public abstract void onHandleSuccess(T t);

    public void onHandleError(T t) {
        if (t instanceof Response) {
            Log.e(TAG, ((Response) t).msg);
        }

    }

}
