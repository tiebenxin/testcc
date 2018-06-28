package com.fingerchat.api.util;

import java.io.Closeable;

/**
 * Created by LY309313 on 2017/9/23.
 */

public final class IOUtils {


    public static void close(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Exception e) {
            }
        }
    }

}
