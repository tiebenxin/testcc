package com.lensim.fingerchat.commons.http.interceptor;

import android.text.TextUtils;

import com.lensim.fingerchat.commons.http.TokenManager;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 所有请求的公共参数
 * <p>
 * Created by zm on 2018/4/11
 */

public class CommonInterceptor implements Interceptor {

    public CommonInterceptor() {}

    @Override
    public Response intercept(Chain chain) throws IOException {

        Request oldRequest = chain.request();

        /**
         * 公共参数
         */
        HttpUrl.Builder authorizedUrlBuilder = oldRequest.url()
                .newBuilder()
                .scheme(oldRequest.url().scheme())
                .host(oldRequest.url().host());

        /**
         * 新的请求
         */
        Request.Builder requestBuilder = oldRequest.newBuilder()
                .method(oldRequest.method(), oldRequest.body())
                .url(authorizedUrlBuilder.build());

        /**
         * 已经登陆的用户 带上Authorization token
         */
        if (!TextUtils.isEmpty(TokenManager.sToken)) {
            requestBuilder.header(TokenManager.TOKEN_KEY, TokenManager.getToken());
        }
        return chain.proceed(requestBuilder.build());
    }
}
