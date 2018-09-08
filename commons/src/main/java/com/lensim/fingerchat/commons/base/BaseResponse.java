package com.lensim.fingerchat.commons.base;

import com.lens.core.componet.net.data.IRespose;

import java.io.Serializable;

public class BaseResponse<T> implements IRespose, Serializable{
    /**
     * 下线通知
     */
    public static final String OFFLINE = "";
    /**
     * 后台解锁
     */
    public static final String BACKSTAGECLERAR = "";
    /**
     * 建议更新
     */
    public static final String SUCCESS_CODE_UPGRADE = "";
    /**
     * 强制更新
     */
    public static final String SUCCESS_CODE_UPGRADE_FOUCE = "";

    private int code;
    private String message;
    T content;

    @Override
    public boolean isSuccess() {
        return code==10;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public int getIStatus() {
        return code;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getContent() {
        return content;
    }

    public void setContent(T content) {
        this.content = content;
    }
}
