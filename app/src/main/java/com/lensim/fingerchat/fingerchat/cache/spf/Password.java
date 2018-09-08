package com.lensim.fingerchat.fingerchat.cache.spf;

import com.lens.spf.api.Spf;

/**
 * @time 2017/11/29 8:59
 * @class describe
 */

@Spf
public class Password {
    String secretchatPwd; //密聊密码
    boolean hasPwd;      // 是否设置了密聊密码 true 设置了
    boolean firstSet;   //是否是首次设置密码
    boolean type;
    boolean screenLock;  //是否开启屏幕安全锁定
    boolean printLock;   //是否开启指纹解锁
    long backgroundTime;
}
