package com.lensim.fingerchat.commons.utils.cppencryp;

import android.content.Context;
import android.widget.Toast;
import com.lensim.fingerchat.commons.helper.ContextHelper;


public class SecureUtil {

    static {
        System.loadLibrary("encrypt-lib");
    }

    public static byte[] encryptData(byte[] data) {
        return encryptData(ContextHelper.getContext(), data);
    }

    public static byte[] decryptData(byte[] data) {
        return decryptData(ContextHelper.getContext(), data);
    }

    public static String getSign(String data) {
        return getSign(ContextHelper.getContext(), data);
    }

    public static String getDeviceId() {
        return "deviceId";
    }

    public static String getAppVersion() {
        return "1.0";
    }

    public static String getChannel() {
        return "fg";
    }

    public static void showToast(String tips) {
        Toast.makeText(ContextHelper.getContext(), tips, Toast.LENGTH_SHORT).show();
    }

    native private static byte[] encryptData(Context context, byte[] data);
    native private static byte[] decryptData(Context context, byte[] data);
    native private static String getSign(Context context, String data);
}
