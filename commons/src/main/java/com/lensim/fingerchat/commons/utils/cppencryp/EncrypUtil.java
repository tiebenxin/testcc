package com.lensim.fingerchat.commons.utils.cppencryp;

import android.util.Base64;
import java.util.UUID;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * date on 2018/1/18
 * author ll147996
 * describe
 */

public class EncrypUtil {

    static {
        System.loadLibrary("encrypt-lib");
    }


    /**
     * 加密
     * @param iv 必须是16字节长度的密钥
     */
    public static String javaEncode(String str, byte[] iv) {
        if (iv == null || iv.length != 16) throw new RuntimeException("must be a 16-byte key");
        try {
            byte[] key = MD5Util.toByte(getKey());
            SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, new IvParameterSpec(iv));
            byte[] encrypted = cipher.doFinal(str.getBytes());

            return Base64.encodeToString(encrypted, Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
            return str;
        }
    }

    /**
     * 解密
     * @param iv 秘钥
     */
    public static String javaDecode(String str, byte[] iv) {
        try {
            byte[] deData = Base64.decode(str, Base64.DEFAULT);
            byte[] key = MD5Util.toByte(getKey());
            SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

            cipher.init(Cipher.DECRYPT_MODE, skeySpec, new IvParameterSpec(iv));
            byte[] oriData = cipher.doFinal(deData);
            return new String(oriData);
        } catch (Exception e) {
            e.printStackTrace();
            return str;
        }
    }


    /**
     * 调用原生方法加密
     */
    public static String nativeEncode(String str) {
        byte[] enData = SecureUtil.encryptData(str.getBytes());
        return new String(Base64.encode(enData, Base64.DEFAULT));
    }

    /**
     * 调用原生方法解密
     */
    public static String nativeDecode(String str) {
        byte[] deData = Base64.decode(str, Base64.DEFAULT);
        byte[] oriData = SecureUtil.decryptData(deData);
        return new String(oriData);
    }

    //获取随机 byte[16]
    public static byte[] getByte() {
        String uuid = UUID.randomUUID().toString().replaceAll("-", "").substring(16);
        return uuid.getBytes();
    }



    private static String getKey() {
        return "appKey" + SecureUtil.getDeviceId() + "appKey";
    }

}
