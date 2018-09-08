package com.lensim.fingerchat.commons.utils;

import static com.lensim.fingerchat.commons.utils.CyptoUtils.DES_KEY;

import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * 加密解密工具包
 *
 * @author Winter Lau
 * @date 2011-12-26
 */
public class CyptoConvertUtils {

    public static String decryptString(String original) {
        String result = original;
        try {
            result = CyptoUtils.DecryptDoNet(original, DES_KEY);
        } catch (RuntimeException e) {
            result = original;
        } catch (Exception e) {
            result = original;
        }
        return result;
    }

    public static String decryptUrlString(String original) {
        String result = original;
        try {
            String temp = URLDecoder.decode(original, "UTF-8");
            result = CyptoUtils.DecryptDoNet(temp, DES_KEY);
            System.out.println(CyptoConvertUtils.class.getSimpleName() + ":" + result);
        } catch (RuntimeException e) {
            result = original;
        } catch (Exception e) {
            result = original;
        }
        return result;
    }

    public static String encodeUrlString(String original) {
        String result = original;
        try {
            String temp = CyptoUtils.encrypt(original);
            result = URLEncoder.encode(temp, "UTF-8");
            System.out.println(CyptoConvertUtils.class.getSimpleName() + ":" + result);
        } catch (RuntimeException e) {
            result = original;
        } catch (Exception e) {
            result = original;
        }
        return result;
    }
}
