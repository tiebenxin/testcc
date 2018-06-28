package com.fingerchat.api.util;

/**
 * Created by LY309313 on 2017/11/9.
 */

public class Objects {

    public static <T> T requireNonNull(T obj,String message){
        if(obj == null){
            throw new NullPointerException(message);
        }
        return obj;
    }
}
