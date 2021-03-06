package com.lensim.fingerchat.commons.utils;

import android.util.Base64;
import com.google.common.io.BaseEncoding;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;

/**
 * 加密解密工具包
 *
 * @author Winter Lau
 * @date 2011-12-26
 */
public class CyptoUtils {

    public static String DES_KEY = "king@7cc";

//	static {
//		System.loadLibrary("dc");
//	}

    public static String DecryptDoNet(String message, String key)
        throws Exception {
        byte[] bytesrc = Base64.decode(message.getBytes(), Base64.DEFAULT);
        Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
        DESKeySpec desKeySpec = new DESKeySpec(key.getBytes("UTF-8"));
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
        IvParameterSpec iv = new IvParameterSpec(key.getBytes("UTF-8"));
        cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);
        byte[] retByte = cipher.doFinal(bytesrc);
        return new String(retByte);
    }

    // 加密
    //String key = CyptoUtils.getMD5("king@7cc").substring(0, 8).toUpperCase();
    //String key = E287AFFF
    public static String EncryptAsDoNet(String message, String key)
        throws Exception {
        Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
        DESKeySpec desKeySpec = new DESKeySpec(key.getBytes("UTF-8"));
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
        IvParameterSpec iv = new IvParameterSpec(key.getBytes("UTF-8"));
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
        byte[] encryptbyte = cipher.doFinal(message.getBytes());
        return new String(Base64.encode(encryptbyte, Base64.DEFAULT));
    }

    public static String getMD5(String value) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("MD5");
        byte[] data = digest.digest(value.getBytes());
        BigInteger integer = new BigInteger(1, data);
        return integer.toString(16);
    }

    public static String getMD5(byte[] value) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("MD5");
        byte[] data = digest.digest(value);
        BigInteger integer = new BigInteger(1, data);
        return integer.toString(16);
    }

    //	public static native String encrypt(String message);
    public static String encrypt(String message) {
        String msg = "";
        try {
            msg = EncryptAsDoNet(message, DES_KEY);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return msg;
    }

    public static byte[] desEncode(String text, String key) throws Exception {
        Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
        DESKeySpec desKeySpec = new DESKeySpec(key.getBytes("UTF-8"));
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
        IvParameterSpec iv = new IvParameterSpec(key.getBytes("UTF-8"));
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
        byte[] encryptbyte = cipher.doFinal(text.getBytes());
        return encryptbyte;
    }


    /*
    * 签到数据加密，先DES对称加密，后标准base64，最后url加密
    * */
    public static String encodeSignIn(String text) throws Exception {
        byte[] encryptbyte = desEncode(text, DES_KEY);
        String base64 = BaseEncoding.base64().encode(encryptbyte);
        return URLEncoder.encode(base64, "utf-8");
    }

    public static String desDecode(byte[] bytes) throws Exception {
        Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
        DESKeySpec desKeySpec = new DESKeySpec(DES_KEY.getBytes("UTF-8"));
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
        IvParameterSpec iv = new IvParameterSpec(DES_KEY.getBytes("UTF-8"));
        cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);
        byte[] retByte = cipher.doFinal(bytes);
        return new String(retByte);
    }

    public static String decode(String text) {
        byte[] bytes = BaseEncoding.base64().decode(text);
        try {
            return desDecode(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

}
