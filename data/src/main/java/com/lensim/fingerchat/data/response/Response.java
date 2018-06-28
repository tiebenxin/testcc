package com.lensim.fingerchat.data.response;


import com.google.gson.annotations.SerializedName;

/**
 * Created by ll147996 on 2017/12/14.
 * 接口返回实体类的基类
 */

public class Response {

    public int code;

    @SerializedName("message")
    public String msg;

}
