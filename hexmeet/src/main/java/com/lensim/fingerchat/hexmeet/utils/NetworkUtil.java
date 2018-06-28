package com.lensim.fingerchat.hexmeet.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.hexmeet.sdk.HexmeetRegistrationState;
import com.lensim.fingerchat.hexmeet.App;
import com.lensim.fingerchat.hexmeet.R;
import com.lensim.fingerchat.hexmeet.RuntimeData;


import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class NetworkUtil {


  private static boolean ucmAlive = true;
  private static boolean oldUcmAlive = true;
  private static final int UCM_DETECTOR_DELAY_IN_SECOND = 8;
  private static ScheduledExecutorService serviceUcmDetector = null;
  private static ScheduledFuture<?> detectorTask = null;

  public static void setSdkCallRate(Context context) {
    String callrate = RuntimeData.getDefaultCallrate();
    if (callrate != null) {
      int call_rate = 128;
      if (NetworkUtil.isWifiConnected(context)) {
        call_rate = Integer.valueOf(callrate.split(" ")[0]);
      } else if (NetworkUtil.is3GConnected(context)) {
        call_rate = Integer.valueOf(callrate.split(" ")[1]);
      }
      App.getHexmeetSdkInstance().setCallRate(call_rate);
    }
  }

  public static boolean isNetConnected(Context context) {
    if (context != null) {
      ConnectivityManager cm = (ConnectivityManager) context
          .getSystemService(Context.CONNECTIVITY_SERVICE);
      if (cm != null) {
        NetworkInfo[] infos = cm.getAllNetworkInfo();
        if (infos != null) {
          for (NetworkInfo ni : infos) {
            if (ni.isConnected()) {
              return true;
            }
          }
        }
      }
    }
    return false;
  }

  public static boolean isWifiConnected(Context context) {
    if (context != null) {
      ConnectivityManager cm = (ConnectivityManager) context
          .getSystemService(Context.CONNECTIVITY_SERVICE);
      if (cm != null) {
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
          return true;
        }
      }
    }
    return false;
  }

  public static boolean is3GConnected(Context context) {
    if (context != null) {
      ConnectivityManager cm = (ConnectivityManager) context
          .getSystemService(Context.CONNECTIVITY_SERVICE);
      if (cm != null) {
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
          return true;
        }
      }
    }
    return false;
  }

  public static boolean isPortReachable(String host, int port) {
    try {
      Socket socket = new Socket();
      socket.connect(new InetSocketAddress(host, port), 2000);
      socket.close();
      return true;
    } catch (Throwable e) {
      e.printStackTrace();

      return false;
    }
  }

  public static boolean isSipServerReachable(Context context) {
    if (!App.isNetworkConnected()) {
      Utils.showToast(context, R.string.network_unconnected);
      return false;
    }

    if (SipRegisterUtil.getSipRegisterStatus() != HexmeetRegistrationState.RegistrationOk) {
      Utils.showToast(context, R.string.unregistered);
      return false;
    }

    return true;
  }

  public static boolean isUcmReachable(Context context) {
    if (!App.isNetworkConnected()) {
      Utils.showToast(context, R.string.network_unconnected);
      return false;
    }

    if (!ucmAlive) {
      Utils.showToast(context, R.string.ucm_unreachable);
      return false;
    }

    return ucmAlive;
  }

  public static boolean isUcmReachable() {
    if (!App.isNetworkConnected()) {
      return false;
    }

    if (!ucmAlive) {
      return false;
    }

    return ucmAlive;
  }

  public static void scheduleUcmDetector() {
    try {
      if (detectorTask != null) {
        detectorTask.cancel(true);
        detectorTask = null;
      }

      if (serviceUcmDetector != null) {
        serviceUcmDetector.shutdownNow();
      }

      serviceUcmDetector = Executors.newSingleThreadScheduledExecutor();
      detectorTask = serviceUcmDetector.scheduleWithFixedDelay(new Runnable() {
        @Override
        public void run() {
          if (!App.isScreenLocked() && App.isForground()) {
            ucmAlive = isPortReachable(RuntimeData.getUcmServer(), 443);
            if (!ucmAlive || ucmAlive != oldUcmAlive) {
//                            if (HexMeetListActivity.getInstance() != null) {
//                                if (ucmAlive) {
//                                    log.warn("clear warning message: ucm restore");
//                                    HexMeetListActivity.getInstance().clearNetworkStatusWarning();
//                                } else {
//                                    log.warn("display warning message: ucm unreachable");
//                                    HexMeetListActivity.getInstance().DisplayNetworkStatusIfNeeded();
//                                }
//                                oldUcmAlive = ucmAlive;
//                            }
            }
          }
        }
      }, 2, UCM_DETECTOR_DELAY_IN_SECOND, TimeUnit.SECONDS);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void shutdown() {
    if (detectorTask != null) {
      detectorTask.cancel(true);
      detectorTask = null;
    }
    if (serviceUcmDetector != null) {
      serviceUcmDetector.shutdownNow();
      serviceUcmDetector = null;
    }
    ucmAlive = true;
    oldUcmAlive = true;
  }

  /**
   * 获取IP地址
   * <p>
   * 需添加权限
   * {@code <uses-permission android:name="android.permission.INTERNET"/>}
   * </p>
   *
   * @param useIPv4 是否用IPv4
   * @return IP地址
   */
  public static String getIPAddress(boolean useIPv4) {
    try {
      for (Enumeration<NetworkInterface> nis = NetworkInterface.getNetworkInterfaces();
          nis.hasMoreElements(); ) {
        NetworkInterface ni = nis.nextElement();
        // 防止小米手机返回10.0.2.15
        if (!ni.isUp()) {
          continue;
        }
        for (Enumeration<InetAddress> addresses = ni.getInetAddresses();
            addresses.hasMoreElements(); ) {
          InetAddress inetAddress = addresses.nextElement();
          if (!inetAddress.isLoopbackAddress()) {
            String hostAddress = inetAddress.getHostAddress();
            boolean isIPv4 = hostAddress.indexOf(':') < 0;
            if (useIPv4) {
              if (isIPv4) {
                return hostAddress;
              }
            } else {
              if (!isIPv4) {
                int index = hostAddress.indexOf('%');
                return index < 0 ? hostAddress.toUpperCase()
                    : hostAddress.substring(0, index).toUpperCase();
              }
            }
          }
        }
      }
    } catch (SocketException e) {
      e.printStackTrace();
    }
    return null;
  }
}
