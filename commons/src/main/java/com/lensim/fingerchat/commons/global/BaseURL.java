package com.lensim.fingerchat.commons.global;

import com.lensim.fingerchat.commons.utils.AppHostUtil;

/**
 * Created by ll147996 on 2017/12/8.
 *
 */
public class BaseURL {
    public static final String BASE_URL = AppHostUtil.getHttpConnectHostApi();// 正式http服务器
    public static final String BASE_URL_UPLOAD = "http://mobile.fingerchat.cn:8686"; // 上传文件测试服务器
    // 服务器名称
    public static final String DEFAULT_SERVER_NAME = "fingerchat.cn"/*"localhost"*/;
    public static final String SERVER_NAME = "10.3.7.45"/*"localhost"*/;
}
