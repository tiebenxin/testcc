package com.fingerchat.api.connection;

/**
 * Created by LY309313 on 2017/9/22.
 */

public interface Cipher {

    byte[] decrypt(byte[] data);

    byte[] encrypt(byte[] data);

}
