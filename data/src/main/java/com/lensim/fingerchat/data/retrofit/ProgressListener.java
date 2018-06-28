package com.lensim.fingerchat.data.retrofit;

/**
 * Created by LY309313 on 2017/4/6.
 */

public interface ProgressListener {

    void update(long bytesRead, long contentLength, boolean done);
}
