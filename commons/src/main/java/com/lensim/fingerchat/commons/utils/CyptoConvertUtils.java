package com.lensim.fingerchat.commons.utils;

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
//            result = CyptoUtils.decrypt(original);
            result = CyptoUtils.DecryptDoNet(original, "king@7cc");
        } catch (RuntimeException e) {
            result = original;
        } catch (Exception e) {
            result = original;
        }
        return result;
    }
}
