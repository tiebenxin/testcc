package com.lensim.fingerchat.commons.utils.compress;

/**
 * date on 2018/3/1
 * author ll147996
 * describe
 */

public interface ImageInterface<T> extends Comparable<T> {

    void setId(long id);

    String getPath();

    void setPath(String path);

    String getThumb();

    void setThumb(String thumb);
}
