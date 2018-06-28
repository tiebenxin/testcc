package com.lensim.fingerchat.commons.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by LY309313 on 2017/11/9.
 */

public class NetWorkUtil {

    public NetWorkUtil() {
    }

    public static boolean isNetworkAvailable(Context context) {
        boolean result = false;
        if(GetNetworkType(context) != null) {
            result = true;
        }

        return result;
    }

    public static boolean isWiFiActive(Context inContext) {
        Context context = inContext.getApplicationContext();
        ConnectivityManager connectivity = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if(info != null) {
                for(int i = 0; i < info.length; ++i) {
                    if(info[i].getTypeName().equals("WIFI") && info[i].isConnected()) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public static String GetNetworkType(Context context) {
        String result = null;
        ConnectivityManager connectivity = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivity == null) {
            result = null;
        } else {
            NetworkInfo[] info = null;

            try {
                info = connectivity.getAllNetworkInfo();
            } catch (Exception var7) {
                ;
            }

            if(info != null) {
                for(int i = 0; i < info.length; ++i) {
                    if(info[i] != null) {
                        NetworkInfo.State tem = info[i].getState();
                        if(tem == NetworkInfo.State.CONNECTED || tem == NetworkInfo.State.CONNECTING) {
                            String temp = info[i].getExtraInfo();
                            result = info[i].getTypeName() + " " + info[i].getSubtypeName() + temp;
                            break;
                        }
                    }
                }
            }
        }

        return result;
    }


}
