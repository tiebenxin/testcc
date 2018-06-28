package com.lensim.fingerchat.data.response;


import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by ll147996 on 2017/12/14.
 * 接口返回实体类的基类
 */
public class ResponseArray<T> extends Response {

    /**
     * 返回结果
     */

    @SerializedName("content")
    public List<T> result;

}