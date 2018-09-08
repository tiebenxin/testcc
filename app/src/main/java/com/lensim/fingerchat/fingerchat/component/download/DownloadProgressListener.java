package com.lensim.fingerchat.fingerchat.component.download;

/**
 * Created by zm on 2018/6/26.
 */
public interface DownloadProgressListener {

    void update(long bytesRead, long contentLength, boolean done);
}
