package com.lensim.fingerchat.fingerchat.cache;

import java.io.File;

/**
 * Created by LY309313 on 2016/11/8.
 */

public interface DiskCache {

    File get(String url);

    boolean remove(String url);

    boolean save(String url, byte[] data);

    void close();

    void clear();
}
