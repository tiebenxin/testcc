package com.lensim.fingerchat.commons.utils.encrypt;

import android.support.annotation.Nullable;
import android.util.Base64;
import java.nio.charset.Charset;

/**
 * 用于编码的工具集
 *
 * @author DrkCore
 * @since 2016年2月16日10:37:08
 */
public final class EncodeUtil {

    private EncodeUtil() {
    }

    /*Base64*/

    public static String encodeBase64(byte... bytes) {
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    public static byte[] decodeBase64(String base64) {
        return Base64.decode(base64, Base64.DEFAULT);
    }

    public static String encodeBase64Str(String src) {
        return encodeBase64Str(src, null);
    }

    public static String encodeBase64Str(String src, @Nullable Charset charset) {
        charset = charset != null ? charset : Charset.defaultCharset();
        byte[] bytes = src.getBytes(charset);
        return encodeBase64(bytes);
    }

    public static String decodeBase64Str(String base64) {
        return decodeBase64Str(base64, null);
    }

    public static String decodeBase64Str(String base64, @Nullable Charset charset) {
        charset = charset != null ? charset : Charset.defaultCharset();
        byte[] bytes = decodeBase64(base64);
        return new String(bytes, charset);
    }

	/*Hex*/

    /**
     * 具体实现请参阅{@link #encodeHex(boolean, byte...)}。默认使用小写。
     *
     * @param bytes
     * @return
     */
    public static String encodeHex(byte... bytes) {
        return encodeHex(false, bytes);
    }

    /**
     * 将字节数组转为十六进制的字符串。如果一个字节的值小于两位，则补位0。 如0000 1111将会转化成“0f”。
     *
     * @param upperCase 是否需要大写
     * @param bytes
     * @return
     */
    public static String encodeHex(boolean upperCase, byte... bytes) {
        if (bytes.length == 0) {
            return "";
        }

        int len = bytes.length;
        StringBuilder builder = new StringBuilder(len * 2);

        String tmp;
        for (byte b : bytes) {
            tmp = upperCase ? Integer.toHexString(0xFF & b).toUpperCase() : Integer.toHexString(0xFF & b);
            if (tmp.length() == 1) {
                builder.append("0").append(tmp);
            } else
                builder.append(tmp);
        }
        return builder.toString();
    }

    /**
     * 将十六进制的字符串转化为字节数据。如将“0f”转化为0000 1111。无视大小写。
     *
     * @param hexStr
     * @return
     */
    public static byte[] decodeHex(String hexStr) {
        int len = hexStr.length();
        if (len == 0) {
            return null;
        }

        byte[] result = new byte[len / 2];
        for (int i = 0; i < len / 2; i++) {
            int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
            int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2), 16);
            result[i] = (byte) (high * 16 + low);
        }
        return result;
    }
}
