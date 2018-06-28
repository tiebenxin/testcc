package com.lensim.fingerchat.commons.utils.encrypt;

import android.support.annotation.StringDef;
import android.text.TextUtils;
import com.lensim.fingerchat.commons.utils.L;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.nio.charset.Charset;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * @author DrkCore
 * @since 2016年2月24日22:40:31
 */
public class AESEncryptor extends AbsEncryptor {

    private final Cipher encryptCipher;
    private final Cipher decryptCipher;

    private AESEncryptor(Charset charset, Cipher encryptCipher, Cipher decryptCipher) {
        super(charset);

        this.encryptCipher = encryptCipher;
        this.decryptCipher = decryptCipher;
    }

    @Override
    protected byte[] doEncrypt(byte[] src) throws Exception {
        return encryptCipher.doFinal(src);
    }

    @Override
    protected byte[] doDecrypt(byte[] cipherBytes) throws Exception {
        return decryptCipher.doFinal(cipherBytes);
    }

    /*Builder*/

    public static final String AES = "AES";

    public static final String MODE_CBC = "CBC";
    public static final String MODE_CFB = "CFB";
    /**
     * {@link Builder}中mode的默认值
     */
    public static final String MODE_ECB = "ECB";
    public static final String MODE_OFB = "OFB";
    public static final String MODE_PCBC = "PCBC";

    @StringDef({
            MODE_CBC,
            MODE_CFB,
            MODE_ECB,
            MODE_OFB,
            MODE_PCBC
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface Mode {
    }

    /**
     * {@link Builder}中padding的默认值
     */
    public static final String PADDING_ZERO = "ZeroBytePadding";
    public static final String PADDING_NONE = "NoPadding";
    public static final String PADDING_PKCS5 = "PKCS5Padding";
    public static final String PADDING_ISO10126 = "ISO10126Padding";

    @StringDef({
            PADDING_NONE,
            PADDING_ZERO,
            PADDING_PKCS5,
            PADDING_ISO10126
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface Padding {
    }

    /**
     * {@link AESEncryptor} 的构造者。
     */
    public static class Builder {

        private Charset charset;
        private String mode;
        private String padding;
        private byte[] key;

        public Builder setCharset(Charset charset) {
            this.charset = charset;
            return this;
        }

        public Builder setMode(@Mode String mode) {
            this.mode = mode;
            return this;
        }

        public Builder setPadding(@Padding String padding) {
            this.padding = padding;
            return this;
        }

        /**
         * 直接设置key值。
         * <p>
         * 注意，按照aes的定义这里数组的长度是有限制的。
         *
         * @param key
         * @return
         */
        public Builder setKey(byte[] key) {
            this.key = key;
            return this;
        }

        public Builder setKey(InputStream in) throws IOException {
            this.key = IOUtil.read(in);
            return this;
        }

        public Builder setKey(File key) throws IOException {
            return setKey(new FileInputStream(key));
        }

        public Builder setDerivedKeyQuietly(String password, int keyBitLen) {
            try {
                return setDerivedKey(password, keyBitLen);
            } catch (Exception e) {
                L.e(e);
                throw new IllegalStateException("无法从password中获取key值");
            }
        }

        /**
         * 使用{@link #getOrDeriveKey(String, int)}从 password 中创建key
         *
         * @param password
         * @param keyBitLen
         * @return
         */
        public Builder setDerivedKey(String password, int keyBitLen) throws InvalidKeySpecException, NoSuchAlgorithmException {
            this.key = getOrDeriveKey(password, keyBitLen);
            return this;
        }

        /**
         * 使用{@link #deriveKeyInsecurely(String, int)}将password转化为 key。
         *
         * @param password
         * @param keyBitLen
         * @return
         */
//        @Deprecated
//        public Builder setDerivedKeyInsecurely(String password, int keyBitLen) {
//            this.key = deriveKeyInsecurely(password, keyBitLen);
//            return this;
//        }
//
//        /**
//         * 使用{@link #deriveKeyDeprecated(String, String, int)}从password中创建key值
//         *
//         * @param password
//         * @param keyBitLen
//         * @return
//         */
//        @Deprecated
//        public Builder setDerivedKeyDeprecated(String password, int keyBitLen) throws NoSuchProviderException, NoSuchAlgorithmException {
//            this.key = deriveKeyDeprecated(password, AES, keyBitLen);
//            return this;
//        }

        /**
         * 调用{@link #build()}，如果发生任务异常，则抛出{@link IllegalStateException}
         *
         * @return
         */
        public AESEncryptor buildQuietly() {
            try {
                return build();
            } catch (Exception e) {
                L.e(e);
                throw new IllegalStateException("无法实例化加密类");
            }
        }

        public AESEncryptor build() throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, NoSuchPaddingException, InvalidAlgorithmParameterException {
            //参数校验
            charset = charset != null ? charset : Charset.defaultCharset();

            mode = !TextUtils.isEmpty(mode) ? mode : MODE_ECB;
            padding = !TextUtils.isEmpty(padding) ? padding : PADDING_NONE;

            SecretKey key = new SecretKeySpec(this.key, AES);
            String cipherName = AES + '/' + mode + '/' + padding;

            Cipher encryptCipher = Cipher.getInstance(cipherName);
            encryptCipher.init(Cipher.ENCRYPT_MODE, key,new IvParameterSpec(
                    new byte[encryptCipher.getBlockSize()]));

            Cipher decryptCipher = Cipher.getInstance(cipherName);
            decryptCipher.init(Cipher.DECRYPT_MODE, key,new IvParameterSpec(
                    new byte[decryptCipher.getBlockSize()]));

            return new AESEncryptor(charset, encryptCipher, decryptCipher);
        }
    }
}
