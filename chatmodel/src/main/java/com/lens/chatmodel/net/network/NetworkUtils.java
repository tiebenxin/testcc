
package com.lens.chatmodel.net.network;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import com.lensim.fingerchat.commons.helper.ContextHelper;

/**
 * 网络连接工具类
 */
public class NetworkUtils {

    private static final String TAG = NetworkUtils.class.getSimpleName();

    /*
    * 是否网络可用
    * */
    public static boolean isNetAvaliale() {
        ConnectivityManager connectivityManager = (ConnectivityManager) ContextHelper.getContext()
            .getSystemService(
                Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            return true;
        } else {
            return false;
        }
    }


}
