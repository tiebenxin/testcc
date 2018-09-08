package com.lensim.fingerchat.hexmeet;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.hexmeet.sdk.HexmeetException;
import com.lensim.fingerchat.commons.app.AppConfig;
import com.lensim.fingerchat.commons.utils.StringUtils;
import com.lensim.fingerchat.data.login.UserInfoRepository;
import com.lensim.fingerchat.hexmeet.api.model.RestContact;
import com.lensim.fingerchat.hexmeet.api.model.RestUser;
import com.lensim.fingerchat.hexmeet.utils.NetworkUtil;
import com.lensim.fingerchat.hexmeet.utils.SipRegisterUtil;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class RuntimeData {

  private static SharedPreferences sp;

  private static String rcmserver = null;
  private static RestUser logUser = null;
  private static String token = null;
  private static String FGtoken = null;
  private static String deviceSN = null;
  private static String internalSipServer = null;
  private static String internalSipProtocol = null;
  private static String externalSipServer = null;
  private static String externalSipProtocol = null;
  private static String sipUserName = null;
  private static String sipAuthorizationName = null;
  private static String sipPassword = null;
  private static String sipServer = null;
  private static String sipProtocol = null;
  private static RestContact selfContact = null;
  private static boolean boundGetuiAlias = false;

  private static SharedPreferences getSp() {
    if (sp == null) {
      return sp = App.getContext().getSharedPreferences("runtime_data", Context.MODE_PRIVATE);
    }

    return sp;
  }

  private static void saveStr(String name, String value) {
    getSp().edit().putString(name, value).commit();
  }

  private static String getStr(String name) {
    return getSp().getString(name, "");
  }

  private static String getStr(String name, String defValue) {
    return getSp().getString(name, defValue);
  }

  public static void reset() {
    rcmserver = null;
    logUser = null;
    setLogUser(null);
    token = null;
    FGtoken = null;
    deviceSN = null;
    internalSipServer = null;
    internalSipProtocol = null;
    externalSipServer = null;
    externalSipProtocol = null;
    sipUserName = null;
    sipAuthorizationName = null;
    sipPassword = null;
    sipServer = null;
    sipProtocol = null;
    selfContact = null;
    setSelfContact(null);
    boundGetuiAlias = false;
    getSp().edit().clear().commit();
  }

  public static String getUcmServer() {
    return StringUtils.isNotEmpty(rcmserver) ? rcmserver : (rcmserver = getStr("rcmserver"));
  }

  public static void setRcmServer(String rcmserver) {
    RuntimeData.rcmserver = rcmserver;
    saveStr("rcmserver", rcmserver);
  }

  public static String getUsername() {
    if (logUser != null) {
      return logUser.getName();
    }

    return "";
  }

  public static RestUser getLogUser() {
    return logUser != null ? logUser : (logUser = (RestUser) readSerializable("logUser"));
  }

  public static void setLogUser(RestUser logUser) {
    RuntimeData.logUser = logUser;
    saveSerializable(logUser, "logUser");
  }

  public static String getToken() {
    return StringUtils.isNotEmpty(token) ? token : (token = getStr("token"));
  }

  public static void setToken(String token) {
    RuntimeData.token = token;
    saveStr("token", token);
  }

  public static String getFGToken() {
    return StringUtils.isNotEmpty(FGtoken) ? FGtoken : (FGtoken = getStr(getUserName()+"FGtoken"));
  }

  public static void setFGToken(String token) {
    RuntimeData.FGtoken = token;
    saveStr(getUserName()+"FGtoken", token);
  }

    private static String getUserName() {
        String username = UserInfoRepository.getUserId();
        return username == null ? "" : username.toLowerCase();
    }

  public static String getDeviceSN() {
    return StringUtils.isNotEmpty(deviceSN) ? deviceSN : (deviceSN = getStr("deviceSN"));
  }

  public static void setDeviceSN(String deviceSN) {
    RuntimeData.deviceSN = deviceSN;
    saveStr("deviceSN", deviceSN);
  }

  public static String getSipUserName() {
    return StringUtils.isNotEmpty(sipUserName) ? sipUserName : (sipUserName = getStr("sipUserName"));
  }

  public static void setSipUserName(String sipUserName) {
    RuntimeData.sipUserName = sipUserName;
    saveStr("sipUserName", sipUserName);
  }

  public static String getInternalSipServer() {
    return StringUtils.isNotEmpty(internalSipServer) ? internalSipServer
        : (internalSipServer = getStr("internalSipServer"));
  }

  public static void setInternalSipServer(String internalSipServer) {
    RuntimeData.internalSipServer = internalSipServer;
    saveStr("internalSipServer", internalSipServer);
  }

  public static String getExternalSipServer() {
    return StringUtils.isNotEmpty(externalSipServer) ? externalSipServer
        : (externalSipServer = getStr("externalSipServer"));
  }

  public static void setExternalSipServer(String externalSipServer) {
    RuntimeData.externalSipServer = externalSipServer;
    saveStr("externalSipServer", externalSipServer);
  }

  public static String getSipServer() {
    return StringUtils.isNotEmpty(sipServer) ? sipServer : (sipServer = getStr("sipServer"));
  }

  public static void setSipServer(String sipServer) {
    RuntimeData.sipServer = sipServer;
    saveStr("sipServer", sipServer);
  }

  public static String getSipProtocol() {
    return StringUtils.isNotEmpty(sipProtocol) ? sipProtocol : (sipProtocol = getStr("sipProtocol"));
  }

  public static void setSipProtocol(String sipProtocol) {
    RuntimeData.sipProtocol = sipProtocol;
    saveStr("sipProtocol", sipProtocol);
  }

  public static String getDefaultCallrate() {
    String defaultCallrate = getStr("defaultCallrate", "512 Kbps,256 Kbps");

    StringBuilder sb = new StringBuilder();
    defaultCallrate = defaultCallrate.trim();
    int len = defaultCallrate.length();
    boolean spaceInserted = false;
    for (int i = 0; i < len; i++) {
      Character c = defaultCallrate.charAt(i);
      if (Character.isDigit(c)) {
        sb.append(c);
      } else if (!spaceInserted) {
        sb.append(" ");
        spaceInserted = true;
      }
    }

    return sb.toString();
  }

  public static void setDefaultCallrate(String defaultCallrate) {
    saveStr("defaultCallrate", defaultCallrate);
  }

  public static RestContact getSelfContact() {
    return selfContact != null ? selfContact
        : (selfContact = (RestContact) readSerializable("selfContact"));
  }

  public static void setSelfContact(RestContact selfContact) {
    RuntimeData.selfContact = selfContact;
    saveSerializable(selfContact, "selfContact");
  }

  public static String getInternalSipProtocol() {
    return StringUtils.isNotEmpty(internalSipProtocol) ? internalSipProtocol
        : (internalSipProtocol = getStr("internalSipProtocol"));
  }

  public static void setInternalSipProtocol(String internalSipProtocol) {
    RuntimeData.internalSipProtocol = internalSipProtocol;
    saveStr("internalSipProtocol", internalSipProtocol);
  }

  public static String getExternalSipProtocol() {
    return StringUtils.isNotEmpty(externalSipProtocol) ? externalSipProtocol
        : (externalSipProtocol = getStr("externalSipProtocol"));
  }

  public static void setExternalSipProtocol(String externalSipProtocol) {
    RuntimeData.externalSipProtocol = externalSipProtocol;
    saveStr("externalSipProtocol", externalSipProtocol);
  }

  public static String getSipAuthorizationName() {
    return StringUtils.isNotEmpty(sipAuthorizationName) ? sipAuthorizationName
        : (sipAuthorizationName = getStr("sipAuthorizationName"));
  }

  public static void setSipAuthorizationName(String sipAuthorizationName) {
    RuntimeData.sipAuthorizationName = sipAuthorizationName;
    saveStr("sipAuthorizationName", sipAuthorizationName);
  }

  public static String getSipPassword() {
    return StringUtils.isNotEmpty(sipPassword) ? sipPassword : (sipPassword = getStr("sipPassword"));
  }

  public static void setSipPassword(String sipPassword) {
    RuntimeData.sipPassword = sipPassword;
    saveStr("sipPassword", sipPassword);
  }

  public static boolean isBoundGetuiAlias() {
    return boundGetuiAlias;
  }

  public static void setBoundGetuiAlias(boolean boundGetuiAlias) {
    RuntimeData.boundGetuiAlias = boundGetuiAlias;
  }

  private static void saveSerializable(Serializable obj, String filename) {
    ObjectOutputStream os = null;
    try {
      FileOutputStream fs = App.getContext().openFileOutput(filename, Context.MODE_PRIVATE);
      os = new ObjectOutputStream(fs);
      os.writeObject(obj);
      os.flush();
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      if (os != null) {
        try {
          os.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }

  private static Object readSerializable(String filename) {
    ObjectInputStream is = null;
    Object o = null;
    try {
      FileInputStream fs = App.getContext().openFileInput(filename);
      is = new ObjectInputStream(fs);
      o = is.readObject();
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      if (is != null) {
        try {
          is.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }

    return o;
  }

  // true : internal server configured and reachable
  public static boolean tryInternalSip() {
    if (StringUtils.isNotEmpty(RuntimeData.getInternalSipServer())
        && StringUtils.isNotEmpty(RuntimeData.getInternalSipProtocol())) {
      RuntimeData.setSipServer(RuntimeData.getInternalSipServer());
      RuntimeData.setSipProtocol(RuntimeData.getInternalSipProtocol());
      if (NetworkUtil.isPortReachable(RuntimeData.getSipServer(), 5061)) {
        if (StringUtils.isNotEmpty(RuntimeData.getSipUserName())) {
          String displayName = "";
          RestUser restUser = RuntimeData.getLogUser();
          if (restUser == null) {
          } else {
            displayName = restUser.getDisplayName();
          }
          try {
            App.getHexmeetSdkInstance().registerSip(RuntimeData.getSipUserName(),
                RuntimeData.getSipPassword(),
                RuntimeData.getSipServer(),
                displayName,
                SipRegisterUtil.getTransType(RuntimeData.getSipProtocol()));
          } catch (HexmeetException e) {
//                  showToast(App.getContext(),"sdk没有激活");
          }

          return true;
        }
      }
    }

    return false;
  }

  public static void tryExternalSip() {
    if (StringUtils.isNotEmpty(RuntimeData.getExternalSipServer())
        && StringUtils.isNotEmpty(RuntimeData.getExternalSipProtocol())) {
      RuntimeData.setSipServer(RuntimeData.getExternalSipServer());
      RuntimeData.setSipProtocol(RuntimeData.getExternalSipProtocol());
      if (StringUtils.isNotEmpty(RuntimeData.getSipUserName())) {
        String displayName = "";
        RestUser restUser = RuntimeData.getLogUser();
        if (restUser == null) {
        } else {
          displayName = restUser.getDisplayName();
        }
        try {
          App.getHexmeetSdkInstance().registerSip(RuntimeData.getSipUserName(),
              RuntimeData.getSipPassword(),
              RuntimeData.getSipServer(),
              displayName,
              SipRegisterUtil.getTransType(RuntimeData.getSipProtocol()));
        } catch (HexmeetException e) {
//               showToast(App.getContext(),"sdk没有激活");
        }
      } else {

      }
    } else {

    }
  }


  public static void registerSip() {

    new Thread() {
      @Override
      public void run() {
        if (NetworkUtil.is3GConnected(App.getContext())) {
          tryExternalSip();
        } else if (NetworkUtil.isWifiConnected(App.getContext())) {
          if (!tryInternalSip()) {
            tryExternalSip();
          }
        }
      }
    }.start();
  }

  public static void showToast(final Context context, final String text) {
    Handler handler = new Handler(Looper.getMainLooper());
    handler.post(new Runnable() {
      public void run() {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
      }
    });
  }
}
