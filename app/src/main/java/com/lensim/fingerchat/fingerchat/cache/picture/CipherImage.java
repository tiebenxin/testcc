package com.lensim.fingerchat.fingerchat.cache.picture;

import com.lensim.fingerchat.commons.app.AppConfig;
import com.lensim.fingerchat.fingerchat.cache.encrypt.AESEncryptor;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by LY309313 on 2017/5/12.
 */

public class CipherImage {

    private static CipherImage mInstance = new Builder().build();

    public static CipherImage getInstance() {
        return mInstance;
    }

    private CipherImage(){}

    /**
     * 密钥
     */
   // private String key;

    private byte[] mKey;
    /**
     * 加密算法
     */
    private String algorithm;

    /**
     * 获取解密Cipher
     *
     * @return
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     */
    public Cipher getDecryptCipher() throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException {
       // byte[] str = key.getBytes();

        Key key = new SecretKeySpec(mKey, 0, mKey.length, algorithm);
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.DECRYPT_MODE, key);
        return cipher;
    }

    /**
     * 获取加密Cipher
     *
     * @return
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     */
    public Cipher getEncryptCipher() throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException {
       // byte[] str = key.getBytes();
        Key key = new SecretKeySpec(mKey, 0, mKey.length, algorithm);
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.ENCRYPT_MODE, key);

        return cipher;
    }


    /**
     * 构造器
     */
    public static class Builder {
        private String seed, algorithm;

        public Builder() {

            seed = AppConfig.getDefaultKey();//默认密钥
            algorithm = "AES";//默认加解密算法
        }

        public Builder cipherKey(String key) {
            this.seed = key;
            return this;
        }

        public Builder cipherAlgorithm(String algorithm) {
            this.algorithm = algorithm;
            return this;
        }

        public CipherImage build() {
            if (mInstance == null) {
                mInstance = new CipherImage();
            }
            mInstance.algorithm = algorithm;



            try {
//                SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
//                //System.out.println(new String(sr.generateSeed(16)));
//                //网上博客添加"Crypto" 后方能使用
//                // 设置一个种子,一般是用户设定的密码
//                sr.setSeed(seed.getBytes());
//                // 获得一个key生成器（AES加密模式）
//                KeyGenerator keyGen = KeyGenerator.getInstance("AES");
//                // 设置密匙长度128位
//                keyGen.init(128, sr);
//                // 获得密匙
//                SecretKey skey = keyGen.generateKey();
                // 返回密匙的byte数组供加解密使用
                mInstance.mKey = AESEncryptor.getOrDeriveKey(seed,128);

            } catch (Exception e) {
                e.printStackTrace();
            }
            //L.i("加密密钥:" + mInstance.key + "长度:" + mInstance.key.length());
            //L.i("加密方式:" + algorithm);
            return mInstance;
        }
    }
}
