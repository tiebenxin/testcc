package com.lensim.fingerchat.data.login;


import com.lensim.fingerchat.commons.utils.cppencryp.EncrypUtil;
import com.lensim.fingerchat.db.login.Password;
import com.lensim.fingerchat.db.login.PasswordManager;

/**
 * date on 2018/1/20
 * author ll147996
 * describe
 */

public class PasswordRespository {


    /**
     * 清除密码
     */
    public static void cleanPassword() {
        PasswordManager.getInstance().clearPassword();
    }

    /**
     * 加密密码，秘钥
     * 存储密码，秘钥
     *
     * @param pw 用户密码
     */
    public static void setPassword(String pw) {
        storagePassword(pw);
    }

    /**
     * @return 用户密码
     */
    public static String getPassword() {
        return decode();
    }

    /**
     * 加密密码，秘钥
     * 存储密码，秘钥
     *
     * @param pw 用户密码
     */
    private static void storagePassword(String pw) {
        //随机获取一个16字节的秘钥
        byte[] iv = EncrypUtil.getByte();
        //密码、秘钥加密保存
        PasswordManager.getInstance()
            .setPassword(EncrypUtil.javaEncode(pw, iv), EncrypUtil.nativeEncode(new String(iv)));
    }

    /**
     * 解密
     *
     * @return 用户密码
     */
    private static String decode() {
        Password password = PasswordManager.getInstance().getPassword();
        if (password == null) {
            return null;
        }
        String pw = password.getPassword();
        String secretkey = password.getSecretkey();
        byte[] key = EncrypUtil.nativeDecode(secretkey).getBytes();
        if (key == null || key.length <= 0) {
            return null;
        }
        return EncrypUtil.javaDecode(pw, key);
    }
}
