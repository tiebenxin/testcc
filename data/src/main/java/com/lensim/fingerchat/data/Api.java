package com.lensim.fingerchat.data;

/**
 * Created by ll147996 on 2017/12/8.
 */

public class Api {

    public static final String TAG = "API";

    //api
    public static final String IP_OLD = "mobile.fingerchat.cn";
    public static final String IP = "10.3.7.149";
    public static final String IP_SEARCH_USER = "10.3.9.133";
    public static final String PORT_9696 = ":9696";

    public final static String HTTP = "http://";
    public final static String SPLITTER = "/";
    public static final String PORT_8686 = ":8686";
    public static final String PORT_8989 = ":8989";

    public static final String BASE_URL = HTTP + IP + PORT_9696;
    public static final String BASE_URL_OLD = HTTP + IP_OLD + PORT_8686 + SPLITTER;
    public static final String BASE_URL_SEARCH = HTTP + IP_SEARCH_USER + PORT_8989;


    public static final String UPLOAD_IMAGE = BASE_URL + "/DFS/Image";
    public static final String UPLOAD_VIDEO = BASE_URL + "/DFS/Video";
    public static final String UPLOAD_VOICE = BASE_URL + "/DFS/Voice";
    public static final String SEARCH_USER = BASE_URL + "/im2user/queryByKeyword";
    public static String obtainAvater = BASE_URL_OLD + "HnlensImage/Users/%s/Avatar/headimage.png";

    /**
     * 查找好友
     */
    public static String URL_SEARCH_FRIEND =
        HTTP + IP_OLD + PORT_8686 + "/LensWcfSrv.svc/SearchUsr/%s/%s/%s/%s";

    /*
    * 投票路径
    * */
    public static String URL_VOTE =
        HTTP + IP_OLD + PORT_8686 + "/vote2/index.html?id=%s&username=%s&groupid=%s";

    public static String URL_VOTE_TO =
        HTTP + IP_OLD + PORT_8686 + "/vote2/votingdetails.html?id=%s&username=%s&voteid=%s";
}
