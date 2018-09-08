package com.lensim.fingerchat.hexmeet.api;

import android.content.SharedPreferences;


import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.lensim.fingerchat.hexmeet.App;
import com.lensim.fingerchat.hexmeet.R;
import com.lensim.fingerchat.hexmeet.RuntimeData;
import com.lensim.fingerchat.hexmeet.api.model.OfflineMessage;
import com.lensim.fingerchat.hexmeet.api.model.RestAppVersionInfo;
import com.lensim.fingerchat.hexmeet.api.model.RestContact;
import com.lensim.fingerchat.hexmeet.api.model.RestContactReq;
import com.lensim.fingerchat.hexmeet.api.model.RestErrorMessage;
import com.lensim.fingerchat.hexmeet.api.model.RestFeedback;
import com.lensim.fingerchat.hexmeet.api.model.RestGroup;
import com.lensim.fingerchat.hexmeet.api.model.RestGroupContactLink;
import com.lensim.fingerchat.hexmeet.api.model.RestGroupReq;
import com.lensim.fingerchat.hexmeet.api.model.RestLoginReq;
import com.lensim.fingerchat.hexmeet.api.model.RestLoginResp;
import com.lensim.fingerchat.hexmeet.api.model.RestMeeting;
import com.lensim.fingerchat.hexmeet.api.model.RestMeetingReq;
import com.lensim.fingerchat.hexmeet.api.model.RestParticipant;
import com.lensim.fingerchat.hexmeet.api.model.RestResult;
import com.lensim.fingerchat.hexmeet.api.model.RestTerminal;
import com.lensim.fingerchat.hexmeet.api.model.RestTerminalReq;
import com.lensim.fingerchat.hexmeet.api.model.RestUser;
import com.lensim.fingerchat.hexmeet.api.model.RestUserReq;
import com.lensim.fingerchat.hexmeet.db.RestCallRow_;
import com.lensim.fingerchat.hexmeet.utils.Utils;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.nio.charset.Charset;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import okio.BufferedSource;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

  public static final String INVALID_TOKEN = "Invalid-Token";
  private static final Charset UTF8 = Charset.forName("UTF-8");
  private static Api api = null;
  private static Api beta_api = null;
  private static Gson gson = null;
  private static SharedPreferences sp = null;

  private static void buildApiClient(String server, boolean isBetaServer) {
    OkHttpClient.Builder mBuilder = new OkHttpClient.Builder();
    mBuilder.sslSocketFactory(createSSLSocketFactory(), new TrustAllManager());
    mBuilder.hostnameVerifier(new TrustAllHostnameVerifier());
//    HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
//      @Override
//      public void log(String message) {
//      }
//    });
      HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
    httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
    mBuilder.addInterceptor(httpLoggingInterceptor);
    mBuilder.addInterceptor(new Interceptor() {
      @Override
      public okhttp3.Response intercept(Chain chain)
          throws IOException {
        okhttp3.Response response = chain.proceed(chain.request());
        if (response.code() == 404 || response.code() == 503) {
          return response;
        }

        if (response.code() < 200 || response.code() > 300) {
          ResponseBody responseBody = response.body();
          BufferedSource source = responseBody.source();
          source.request(Long.MAX_VALUE);
          if (responseBody.contentLength() != 0) {

          }
        }

        return response;
      }
    });
    OkHttpClient okHttpClient = mBuilder.build();
    Retrofit retrofit = new Retrofit.Builder().client(okHttpClient)
        .baseUrl("https://" + server + "/api/rest/v2.0/")
        .addConverterFactory(GsonConverterFactory.create())
        .build();
    if (isBetaServer) {
      beta_api = retrofit.create(Api.class);
    } else {
      api = retrofit.create(Api.class);
    }
  }

  private static SSLSocketFactory createSSLSocketFactory() {
    SSLSocketFactory sSLSocketFactory = null;

    try {
      SSLContext sc = SSLContext.getInstance("SSL");
      sc.init(null, new TrustManager[]{new TrustAllManager()}, new SecureRandom());
      sSLSocketFactory = sc.getSocketFactory();
    } catch (Exception e) {
      e.printStackTrace();
    }

    return sSLSocketFactory;
  }

  private static Api getApi() {
    if (api == null) {
      buildApiClient(RuntimeData.getUcmServer(), false);
    }

    return api;
  }

  private static Api getApi(String server) {
    if (api == null) {
      buildApiClient(server, false);
    }

    return api;
  }

  private static Api getBetaApi() {
    if (beta_api == null) {

    }

    return beta_api;
  }

  public static Gson getGson() {
    if (gson == null) {
      gson = new Gson();
    }

    return gson;
  }

  public static RestErrorMessage fromErrorJson(String errorJson) {
    try {
      return ApiClient.getGson().fromJson(errorJson, RestErrorMessage.class);
    } catch (JsonSyntaxException e) {
      RestErrorMessage restError = new RestErrorMessage();
      restError.setErrorCode(-1);
      restError.setErrorInfo(errorJson);
      return restError;
    }
  }

  public static String fromErrorResponse(Response<?> response) {
    try {
      RestErrorMessage restError = ApiClient.fromErrorJson(response.errorBody().string());
      return restError != null ? restError.getErrorInfo() : "";
    } catch (IOException e) {
      e.printStackTrace();
    }

    return "";
  }

  public static SharedPreferences getSp() {
    if (sp == null) {
      sp = App.getContext().getSharedPreferences("settings", 0);
    }

    return sp;
  }

  public static void reset() {
    api = null;
    gson = null;
    sp = null;
  }

  public static long getLastModifiedTime() {
    return getSp().getLong(
        "meeting_lastModifiedTime_" + RuntimeData.getUcmServer() + "_"
            + RuntimeData.getLogUser().getName(), 0);
  }

  public static void setLastModifiedTime(long lastModifiedTime) {
    if (RuntimeData.getLogUser() != null) {
      getSp().edit().putLong(
          "meeting_lastModifiedTime_" + RuntimeData.getUcmServer() + "_"
              + RuntimeData.getLogUser().getName(), lastModifiedTime).commit();
    }
  }

  public static <T extends Callback> T getProxy(T callback) {
    InvocationHandler handler = new CallbackProxy(callback);
    T proxied = (T) Proxy.newProxyInstance(callback.getClass().getClassLoader(), callback.getClass()
        .getInterfaces(), handler);
    return proxied;
  }

  public static void login(RestLoginReq loginReq, Callback<RestLoginResp> callback) {
    if (App.isNetworkConnected()) {
      getApi().login(loginReq).enqueue(getProxy(callback));
    }
  }

  public static void getLatestAppVersion(String appType, String language, Callback<RestAppVersionInfo> callback) {
    if (App.isNetworkConnected()) {
      getApi().getLatestAppVersion(appType, language).enqueue(getProxy(callback));
    }
  }

  public static void getLatestAppVersion(String server, String appType, String language, Callback<RestAppVersionInfo> callback) {
    if (App.isNetworkConnected()) {
      getApi(server).getLatestAppVersion(appType, language).enqueue(callback);
      api = null;
    }
  }

  public static void verifyPassword(RestUserReq user, Callback<RestResult> callback) {
    if (App.isNetworkConnected()) {
      getApi().updatePassword(RuntimeData.getLogUser().getId(), RuntimeData.getToken(), "verifyPassword", user).enqueue(getProxy(callback));
    }
  }

  public static void updatePassword(RestUserReq user, Callback<RestResult> callback) {
    if (App.isNetworkConnected()) {
      getApi().updatePassword(RuntimeData.getLogUser().getId(), RuntimeData.getToken(), "changePassword", user).enqueue(getProxy(callback));
    }
  }

  public static void logout(String token, Callback<RestResult> callback) {
    if (App.isNetworkConnected()) {
      getApi().logout(token).enqueue(callback);
    }
  }

  public static void updateUser(int userId, RestUserReq restUserReq, Callback<RestUser> callback) {
    if (App.isNetworkConnected()) {
      getApi().updateUser(userId, RuntimeData.getToken(), restUserReq).enqueue(getProxy(callback));
    }
  }

  public static void registerTerminal(RestTerminalReq restTerminalReq, Callback<RestTerminal> callback) {
    if (App.isNetworkConnected()) {
      getApi().registerTerminal(RuntimeData.getToken(), restTerminalReq).enqueue(getProxy(callback));
    }
  }

  public static void getContacts(Callback<List<RestContact>> callback) {
    if (App.isNetworkConnected()) {
      getApi().getContacts(RuntimeData.getToken()).enqueue(getProxy(callback));
    }
  }

  public static void getContacts(int topAscSize, Callback<List<RestContact>> callback) {
    if (App.isNetworkConnected()) {
      getApi().getContacts(RuntimeData.getToken(), topAscSize).enqueue(getProxy(callback));
    }
  }

  public static void deleteContact(int contactId, Callback<RestResult> callback) {
    if (App.isNetworkConnected()) {
      getApi().deleteContact(contactId, RuntimeData.getToken()).enqueue(getProxy(callback));
    }
  }

  public static void getGroups(Callback<List<RestGroup>> callback) {
    if (App.isNetworkConnected()) {
      getApi().getGroups(RuntimeData.getToken()).enqueue(getProxy(callback));
    }
  }

  public static void deleteGroup(int groupId, Callback<RestResult> callback) {
    if (App.isNetworkConnected()) {
      getApi().deleteGroup(groupId, RuntimeData.getToken()).enqueue(getProxy(callback));
    }
  }

  public static void updateGroup(int groupId, RestGroupReq restGroupReq, Callback<RestGroup> callback) {
    if (App.isNetworkConnected()) {
      getApi().updateGroup(groupId, RuntimeData.getToken(), restGroupReq).enqueue(getProxy(callback));
    }
  }

  public static void getGroup(int groupId, Callback<RestGroup> callback) {
    if (App.isNetworkConnected()) {
      getApi().getGroup(groupId, RuntimeData.getToken()).enqueue(getProxy(callback));
    }
  }

  public static void addContactsToGroup(int groupId, RestGroupContactLink groupContactLink,
      Callback<RestResult> callback) {
    if (App.isNetworkConnected()) {
      getApi().addContactsToGroup(groupId, RuntimeData.getToken(), groupContactLink).enqueue(getProxy(callback));
    }
  }

  public static void deteleContactFromGroup(int groupId, int contactId, Callback<RestResult> callback) {
    if (App.isNetworkConnected()) {
      getApi().deleteContactFromGroup(groupId, contactId, RuntimeData.getToken()).enqueue(getProxy(callback));
    }
  }

  public static void getUser(int userId, Callback<RestUser> callback) {
    if (App.isNetworkConnected()) {
      getApi().getUser(RuntimeData.getToken(), userId).enqueue(getProxy(callback));
    }
  }

  public static void getUsers(String patern, Callback<List<RestUser>> callback) {
    if (App.isNetworkConnected()) {
      getApi().getUsers(RuntimeData.getToken(), patern).enqueue(getProxy(callback));
    }
  }

  public static void getUsers(String patern, int topAscSize, Callback<List<RestUser>> callback) {
    if (App.isNetworkConnected()) {
      getApi().getUsers(RuntimeData.getToken(), patern, topAscSize).enqueue(getProxy(callback));
    }
  }

  public static void getCameras(Callback<ResponseBody> callback) {
    if (App.isNetworkConnected()) {
      getApi().getCameras(RuntimeData.getToken()).enqueue(getProxy(callback));
    }
  }

  public static void addContact(RestContactReq restContactReq, Callback<RestContact> callback) {
    if (App.isNetworkConnected()) {
      getApi().addContact(RuntimeData.getToken(), restContactReq).enqueue(getProxy(callback));
    }
  }

  public static void getMeetings(Callback<List<RestMeeting>> callback, boolean isRefreshManually) {
    if (App.isNetworkConnected()) {
      long lastModify = isRefreshManually ? 0 : getLastModifiedTime();
      getApi().getMeetings(RuntimeData.getToken(), "VISEE", lastModify).enqueue(getProxy(callback));
    }
  }

  public static void getMeeting(int meetingId, Callback<RestMeeting> callback) {
    if (App.isNetworkConnected()) {
      getApi().getMeeting(meetingId, RuntimeData.getToken()).enqueue(getProxy(callback));
    }
  }

  public static void getMeeting(int meetingId, String token,Callback<RestMeeting> callback) {
    if (App.isNetworkConnected()) {
      getApi().getMeeting(meetingId, token).enqueue(getProxy(callback));
    }
  }


  public static void addMeeting(RestMeetingReq restMeetingReq, Callback<RestMeeting> callback) {
    if (App.isNetworkConnected()) {
      getApi().addMeeting(RuntimeData.getToken(), restMeetingReq).enqueue(getProxy(callback));
    }
  }

  public static void deleteMeeting(Integer meetingId, Callback<RestResult> callback) {
    if (App.isNetworkConnected()) {
      getApi().deleteMeeting(meetingId, RuntimeData.getToken()).enqueue(getProxy(callback));
    }
  }

  public static void updateMeeting(RestMeetingReq restMeetingReq, Callback<RestMeeting> callback) {
    if (App.isNetworkConnected()) {
      getApi().updateMeeting(restMeetingReq.getId(), RuntimeData.getToken(), restMeetingReq).enqueue(getProxy(callback));
    }
  }

  public static void terminateMeeting(Integer meetingId, Callback<RestResult> callback) {
    if (App.isNetworkConnected()) {
      getApi().controlMeeting(meetingId, "terminate", RuntimeData.getToken()).enqueue(getProxy(callback));
    }
  }

  public static void getParticipants(Integer meetingId, Callback<List<RestParticipant>> callback) {
    if (App.isNetworkConnected()) {
      getApi().getParticipants(meetingId, RuntimeData.getToken()).enqueue(getProxy(callback));
    }
  }

  public static void pushOfflineMessage(OfflineMessage offlineMessage, Callback<RestResult> callback) {
    if (App.isNetworkConnected()) {
      getApi().pushOfflineMessage(RuntimeData.getToken(), offlineMessage).enqueue(getProxy(callback));
    }
  }

  public static void addCallRecord(RestCallRow_ restCallRecord, Callback<RestResult> callback) {
    if (App.isNetworkConnected()) {
      getApi().addCallRecord(RuntimeData.getToken(), restCallRecord).enqueue(getProxy(callback));
    }
  }

  public static void getCallRecords(Integer sipNum, Callback<List<RestCallRow_>> callback) {
    if (App.isNetworkConnected()) {
      getApi().getCallRecords(RuntimeData.getToken(), sipNum).enqueue(getProxy(callback));
    }
  }

  public static void saveFeedback(RestFeedback feedback, Callback<RestFeedback> callback) {
    if (App.isNetworkConnected()) {
      getBetaApi().saveFeedback(feedback).enqueue(getProxy(callback));
    }
  }

  private static class TrustAllManager implements X509TrustManager {

    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType)
        throws CertificateException {
    }

    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType)
        throws CertificateException {
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
      return new X509Certificate[0];
    }
  }

  private static class TrustAllHostnameVerifier implements HostnameVerifier {

    @Override
    public boolean verify(String hostname, SSLSession session) {
      return true;
    }
  }
}

class CallbackProxy implements InvocationHandler {

  private static final long one_week = 7 * 24 * 3600000;
  private Object proxied;

  public CallbackProxy(Object t) {
    proxied = t;
  }

  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    if (method.getName().startsWith("onResponse")) {
      Response<?> response = (Response<?>) args[1];

      String androidVersion = response.headers().get("Android");
      if (androidVersion != null && Utils.getVersion().compareTo(androidVersion) < 0) {

      }

      if (response.headers().get(ApiClient.INVALID_TOKEN) != null) {
        return null;
      }
    }
    if (method.getName().startsWith("onFailure")) // connection issue
    {
      if (!App.isNetworkConnected()) {
        Utils.showToast(App.getContext(), R.string.server_unavailable);
        return null;
      }

      if (((Throwable) args[1]) != null && ((Throwable) args[1]) instanceof ConnectException) {
        Utils.showToast(App.getContext(), R.string.server_unavailable);
      }

      if (((Throwable) args[1]) != null && ((Throwable) args[1]) instanceof SocketTimeoutException) {
        Utils.showToast(App.getContext(), R.string.server_unavailable);
      }
    }

    return method.invoke(proxied, args);
  }

}
