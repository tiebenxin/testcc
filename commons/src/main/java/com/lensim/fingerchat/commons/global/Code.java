package com.lensim.fingerchat.commons.global;

/**
 * date on 2017/12/27
 * author ll147996
 * describe SSO系统接口 响应码列表
 */

public class Code {

    /**
     * OK
     */
    public static final int OK = 10;

    /**
     * 创建成功
     */
    public static final int CREATED = 11;

    /**
     * 操作成功
     */
    public static final int ACCEPTED = 12;

    /**
     * 错误请求
     */
    public static final int BAD_REQUEST = 20;

    /**
     * 权限受限
     */
    public static final int UNAUTHORIZED = 21;

    /**
     * 资源未找到
     */
    public static final int NOT_FOUND = 24;

    /**
     * 服务器错误
     */
    public static final int SERVER_ERROR = 30;
}
