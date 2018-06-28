package com.lensim.fingerchat.commons.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import com.lensim.fingerchat.commons.helper.ContextHelper;
import java.io.File;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

/**
 * <pre>
 *     author: Blankj
 *     blog  : http://blankj.com
 *     time  : 2016/8/1
 *     desc  : 设备相关工具类
 * </pre>
 */
public final class DeviceUtils {

  private DeviceUtils() {
    throw new UnsupportedOperationException("u can't instantiate me...");
  }

  /**
   * 判断设备是否root
   *
   * @return the boolean{@code true}: 是<br>{@code false}: 否
   */
  public static boolean isDeviceRooted() {
    String su = "su";
    String[] locations = {"/system/bin/", "/system/xbin/", "/sbin/", "/system/sd/xbin/", "/system/bin/failsafe/",
        "/data/local/xbin/", "/data/local/bin/", "/data/local/"};
    for (String location : locations) {
      if (new File(location + su).exists()) {
        return true;
      }
    }
    return false;
  }

  /**
   * 获取设备系统版本号
   *
   * @return 设备系统版本号
   */
  public static int getSDKVersion() {
    return Build.VERSION.SDK_INT;
  }


  /**
   * 获取设备AndroidID
   *
   * @return AndroidID
   */
  @SuppressLint("HardwareIds")
  public static String getAndroidID() {
    return Settings.Secure.getString(ContextHelper.getContext().getContentResolver(), Settings.Secure.ANDROID_ID);
  }

  /**
   * 获取设备MAC地址
   * <p>需添加权限 {@code <uses-permission android:name="android.permission.ACCESS_WIFI_STATE"/>}</p>
   * <p>需添加权限 {@code <uses-permission android:name="android.permission.INTERNET"/>}</p>
   *
   * @return MAC地址
   */
  public static String getMacAddress() {
    String macAddress = getMacAddressByWifiInfo();
    if (!"02:00:00:00:00:00".equals(macAddress)) {
      return macAddress;
    }
    macAddress = getMacAddressByNetworkInterface();
    if (!"02:00:00:00:00:00".equals(macAddress)) {
      return macAddress;
    }
    return "please open wifi";
  }

  /**
   * 获取设备MAC地址
   * <p>需添加权限 {@code <uses-permission android:name="android.permission.ACCESS_WIFI_STATE"/>}</p>
   *
   * @return MAC地址
   */
  @SuppressLint("HardwareIds")
  private static String getMacAddressByWifiInfo() {
    try {
      @SuppressLint("WifiManagerLeak")
      WifiManager wifi = (WifiManager) ContextHelper.getContext().getSystemService(Context.WIFI_SERVICE);
      if (wifi != null) {
        WifiInfo info = wifi.getConnectionInfo();
        if (info != null) {
          return info.getMacAddress();
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return "02:00:00:00:00:00";
  }

  /**
   * 获取设备MAC地址
   * <p>需添加权限 {@code <uses-permission android:name="android.permission.INTERNET"/>}</p>
   *
   * @return MAC地址
   */
  private static String getMacAddressByNetworkInterface() {
    try {
      List<NetworkInterface> nis = Collections.list(NetworkInterface.getNetworkInterfaces());
      for (NetworkInterface ni : nis) {
        if (!ni.getName().equalsIgnoreCase("wlan0")) {
          continue;
        }
        byte[] macBytes = ni.getHardwareAddress();
        if (macBytes != null && macBytes.length > 0) {
          StringBuilder res1 = new StringBuilder();
          for (byte b : macBytes) {
            res1.append(String.format("%02x:", b));
          }
          return res1.deleteCharAt(res1.length() - 1).toString();
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return "02:00:00:00:00:00";
  }


  /**
   * 获取设备厂商
   * <p>如Xiaomi</p>
   *
   * @return 设备厂商
   */

  public static String getManufacturer() {
    return Build.MANUFACTURER;
  }

  /**
   * 获取设备型号
   * <p>如MI2SC</p>
   *
   * @return 设备型号
   */
  public static String getModel() {
    String model = Build.MODEL;
    if (model != null) {
      model = model.trim().replaceAll("\\s*", "");
    } else {
      model = "";
    }
    return model;
  }

  /**
   * get App versionCode
   */
  public static String getVersionCode() {
    PackageManager packageManager = ContextHelper.getContext().getPackageManager();
    PackageInfo packageInfo;
    String versionCode = "";
    try {
      packageInfo = packageManager.getPackageInfo(ContextHelper.getContext().getPackageName(), 0);
      versionCode = packageInfo.versionCode + "";
    } catch (PackageManager.NameNotFoundException e) {
      e.printStackTrace();
    }
    return versionCode;
  }

  /**
   * 获取IP地址
   * <p>需添加权限 {@code <uses-permission android:name="android.permission.INTERNET"/>}</p>
   *
   * @param useIPv4 是否用IPv4
   * @return IP地址
   */
  public static String getIPAddress(final boolean useIPv4) {
    try {
      for (Enumeration<NetworkInterface> nis = NetworkInterface.getNetworkInterfaces(); nis.hasMoreElements(); ) {
        NetworkInterface ni = nis.nextElement();
        // 防止小米手机返回10.0.2.15
        if (!ni.isUp()) continue;
        for (Enumeration<InetAddress> addresses = ni.getInetAddresses(); addresses.hasMoreElements(); ) {
          InetAddress inetAddress = addresses.nextElement();
          if (!inetAddress.isLoopbackAddress()) {
            String hostAddress = inetAddress.getHostAddress();
            boolean isIPv4 = hostAddress.indexOf(':') < 0;
            if (useIPv4) {
              if (isIPv4) return hostAddress;
            } else {
              if (!isIPv4) {
                int index = hostAddress.indexOf('%');
                return index < 0 ? hostAddress.toUpperCase() : hostAddress.substring(0, index).toUpperCase();
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



  /**
   * 获取IP
   * @param context
   * @return
   */
  public static String getIp(final Context context) {
    String ip = null;
    ConnectivityManager conMan = (ConnectivityManager) context
            .getSystemService(Context.CONNECTIVITY_SERVICE);

    // mobile 3G Data Network
    android.net.NetworkInfo.State mobile = conMan.getNetworkInfo(
            ConnectivityManager.TYPE_MOBILE).getState();
    // wifi
    android.net.NetworkInfo.State wifi = conMan.getNetworkInfo(
            ConnectivityManager.TYPE_WIFI).getState();

    // 如果3G网络和wifi网络都未连接，且不是处于正在连接状态 则进入Network Setting界面 由用户配置网络连接
    if (mobile == android.net.NetworkInfo.State.CONNECTED
            || mobile == android.net.NetworkInfo.State.CONNECTING) {
      ip =  getLocalIpAddress();
    }
    if (wifi == android.net.NetworkInfo.State.CONNECTED
            || wifi == android.net.NetworkInfo.State.CONNECTING) {
      //获取wifi服务
      WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
      //判断wifi是否开启
      if (!wifiManager.isWifiEnabled()) {
        wifiManager.setWifiEnabled(true);
      }
      WifiInfo wifiInfo = wifiManager.getConnectionInfo();
      int ipAddress = wifiInfo.getIpAddress();
      ip =(ipAddress & 0xFF ) + "." +
              ((ipAddress >> 8 ) & 0xFF) + "." +
              ((ipAddress >> 16 ) & 0xFF) + "." +
              ( ipAddress >> 24 & 0xFF) ;
    }
    return ip;

  }

  /**
   *
   * @return 手机GPRS网络的IP
   */
  private static String getLocalIpAddress()
  {
    try {
      //Enumeration<NetworkInterface> en=NetworkInterface.getNetworkInterfaces();
      for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
        NetworkInterface intf = en.nextElement();
        for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
          InetAddress inetAddress = enumIpAddr.nextElement();
          if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {//获取IPv4的IP地址
            return inetAddress.getHostAddress();
          }
        }
      }
    } catch (SocketException e) {
      e.printStackTrace();
    }
    return null;
  }
}
