package com.lensim.fingerchat.commons.global;

/**
 * Created by ll147996 on 2017/12/8.
 */
public class BaseURL {

    //api
    public static final String DOMAIN_NAME = "mobile.fingerchat.cn";
    public static final String DOMAIN_NAME_TEST = "oa.fingerchat.net";//测试服
    public static final String IP = "10.3.7.149";
    public static final String PORT_9696 = ":9696";
    public static final String PORT_9999 = ":9999";

    public final static String HTTP = "http://";
    public final static String PORT_8686 = ":8686";

    public final static String URL_SPLITTER = "/";

    //    public static final String BASE_URL_UPLOAD = HTTP + IP + PORT_9696; // 上传文件服务器,暂时废弃！！！！
    public static final String BASE_URL_UPLOAD = HTTP + DOMAIN_NAME_TEST + PORT_8686; // 上传文件测试服务器

    //    public static final String BASE_URL_TEST = HTTP + IP + PORT_9696;// 默认服务器
    public static final String BASE_URL_TEST = HTTP + DOMAIN_NAME_TEST + PORT_9999;// 默认测试服务器

    public static final String BASE_URL = HTTP + DOMAIN_NAME + PORT_8686;// 正式服务器

    public static final String IP_SEARCH_USER = "10.3.9.220";

    public static final String PORT_8989 = ":8989";
    public static final String PORT_8080 = ":8080";
    //        public static final String BASE_URL_SEARCH = HTTP + IP_SEARCH_USER + PORT_8989;// 搜索用户服务器
    public static final String BASE_URL_SEARCH = HTTP + IP_SEARCH_USER + PORT_8080;// 搜索用户服务器
//    public static final String BASE_URL_SEARCH = HTTP + DOMAIN_NAME_TEST + PORT_9999;// 搜索用户服务器


    // 服务器名称
    public static final String DEFAULT_SERVER_NAME = "fingerchat.cn"/*"localhost"*/;

    //  public static final String IP = "172.16.6.215";
    //  public static final String PORT_9696 = ":4357";


    // 本地测试访问地址
    public static final String LOCAL = "";

    // 远程访问地址
    public static final String REMOTE = "";


    /**
     * 根据Consts.DEBUG来判断返回地址
     */
    public static String getBaseUrlUpload() {
        return Consts.DEBUG ? LOCAL : REMOTE;
    }

}
