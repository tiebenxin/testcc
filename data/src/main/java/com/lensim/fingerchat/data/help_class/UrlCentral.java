package com.lensim.fingerchat.data.help_class;

/**
 * Created by LL130386 on 2017/12/21.
 * url中心类
 */

public class UrlCentral {

  private ApiMethods mApiMethods;
  public static final String HOST = "10.3.7.149";
  public final static String HTTP = "http://";
  public final static String SPLITTER = "/";
  public static final String PORT_9696 = ":9696";


  private static class ApiMethods extends MethodsHolder {

    /**
     * 查找好友
     */
    @IUrl(direct = "/LensWcfSrv.svc/SearchUsr/%s/%s/%s/%s")
    private String searchUser;


    /**
     * 上传图片
     */
    @IUrl(direct = "/DFS/Image")
    private String uploadImage;


    /**
     * 上传视频
     */
    @IUrl(direct = "/DFS/Video")
    private String uploadVideo;
  }

  public static UrlCentral create() {
    final UrlCentral urlCentral = new UrlCentral();
    return urlCentral;
  }

  private UrlCentral() {
    mApiMethods = new ApiMethods();
  }

  private String getEndpoint() {
    return HTTP + HOST + PORT_9696;
  }


  public String getSearchUser() {
    return getEndpoint() + mApiMethods.searchUser;
  }

  public String getUploadImage() {
    return getEndpoint() + mApiMethods.uploadImage;
  }

  public String getUploadVideo() {
    return getEndpoint() + mApiMethods.uploadVideo;
  }

}
