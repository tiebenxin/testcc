package com.lensim.fingerchat.data;

import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.lensim.fingerchat.commons.global.BaseURL;
import com.lensim.fingerchat.commons.global.Consts;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by LL130386 on 2018/8/17.
 */
public class HttpChannelTest {

    private final static String TEST_URL = "http://172.16.7.133:8081";

    public RetrofitService getRetrofitService() {
        Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(TEST_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create()) // 支持RxJava
            .client(getOkHttpClient()) // 打印请求参数
            .build();

        RetrofitService retrofitService = retrofit.create(RetrofitService.class);
        return retrofitService;
    }

    public OkHttpClient getOkHttpClient() {
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(Consts.DEBUG ?
            HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.NONE);

        // 定制OkHttp
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(3000, TimeUnit.SECONDS);
        builder.readTimeout(3000, TimeUnit.SECONDS);
        builder.writeTimeout(3000, TimeUnit.SECONDS);
        builder.addInterceptor(httpLoggingInterceptor);
        builder.addNetworkInterceptor(new StethoInterceptor());//打印日志
        return builder.build();
    }


}