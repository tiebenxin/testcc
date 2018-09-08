package com.lens.core.componet.net.exeception;

public class ApiException extends Exception {

    public int status;
    public int code;
    public String message;
    public Object object;

    public ApiException(Throwable throwable, int code) {
        super(throwable);
        this.code = code;
        if (throwable != null && throwable instanceof ServerException) {
            object = ((ServerException) throwable).object;
        }
    }

    public ApiException(String msg) {
        super(msg);
    }
}