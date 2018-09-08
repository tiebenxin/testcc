package com.lensim.fingerchat.fingerchat.api;

import android.annotation.SuppressLint;
import android.util.Log;

import com.lens.chatmodel.helper.FileCache;
import com.lensim.fingerchat.fingerchat.component.download.DownloadProgressInterceptor;
import com.lensim.fingerchat.fingerchat.component.download.DownloadProgressListener;
import com.lensim.fingerchat.commons.utils.AppHostUtil;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.http.GET;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

public class DownloadApi {

    private static final int DEFAULT_TIMEOUT = 15;

    public interface Api {

        /**
         *
         * 下载视频
         *
         * @param fileUrl 视频路径
         * @return
         */
        @Streaming
        @GET
        Observable<ResponseBody> downloadVideo(@Url String fileUrl);
    }

    private Retrofit retrofit;

    public DownloadApi(DownloadProgressListener downloadProgressListener) {
        this.retrofit = createRetrofit(downloadProgressListener);
    }

    private Retrofit createRetrofit(DownloadProgressListener downloadProgressListener) {
        DownloadProgressInterceptor interceptor = new DownloadProgressInterceptor(downloadProgressListener);
        OkHttpClient client = new OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .retryOnConnectionFailure(true)
            .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
            .build();
        Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(AppHostUtil.getHttpConnectHostApi())
            .client(client)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build();
        return retrofit;
    }

    /**
     * 下载视频
     *
     * @param url
     */
    @SuppressLint("CheckResult")
    public void downloadVideo(String url, Consumer<byte[]> subscriber) {
        retrofit.create(Api.class).downloadVideo(url)
            .subscribeOn(Schedulers.io())
            .unsubscribeOn(Schedulers.io())
            .map(ResponseBody::bytes)
            .observeOn(Schedulers.computation())
            .doOnNext(bytes -> {
                try {
                    FileCache.getInstance().saveVideo(url, bytes);
                }catch (IOException e) {
                    Log.e("downloadVideo", e.getMessage());
                }
            }).observeOn(AndroidSchedulers.mainThread()).subscribe(subscriber);
    }
}
