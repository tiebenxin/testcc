package com.lensim.fingerchat.fingerchat.component.download;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * Created by zm on 2018/6/26.
 */
public class DownloadProgressInterceptor implements Interceptor {

    private DownloadProgressListener downloadProgressListener;

    public DownloadProgressInterceptor(DownloadProgressListener downloadProgressListener) {
        this.downloadProgressListener = downloadProgressListener;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Response response = chain.proceed(chain.request());
        return response.newBuilder().body(new DownloadProgressResponseBody(response.body(), downloadProgressListener)).build();
    }
}
