package com.lensim.fingerchat.commons.utils;

import android.util.Base64;
import com.google.common.io.BaseEncoding;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import org.junit.Test;

/**
 * Created by LL130386 on 2018/8/15.
 */
public class CyptoUtilsTest {

    @Test
    public void encodeSignIn() throws Exception {
        String txt = "liszt";
        byte[] des_encoded;
        try {
            des_encoded = CyptoUtils.desEncode(txt, "king@7cc");
            System.out.printf("DES加密(%s): %s\n", txt, new String(des_encoded));

            System.out.println("----------------------2----------------------------");
//            String base64_java = BaseEncoding.base64().encode(des_encoded);
//            System.out.printf("Base64_java编码(%s): %s\n", new String(des_encoded), base64_java);

            //            String url_encoded = URLEncoder.encode(base64_java, StandardCharsets.UTF_8.name());
//            System.out.printf("Url编码(%s): %s\n", base64_java, url_encoded);

            //            byte[] base64_java_decode = BaseEncoding.base64().decode(base64_java);
//            System.out
//                .printf("Base64_java解码(%s): %s\n", new String(base64_java_decode), base64_java);

            //            System.out.println("DES_java解码：" + CyptoUtils.desDecode(base64_java_decode));

            System.out.println("----------------------3----------------------------");

            String base64_an = new String(Base64.encode(des_encoded, Base64.DEFAULT));
            System.out.printf("Base64_android编码(%s): %s\n", new String(des_encoded), base64_an);

            byte[] base64_android_decode = Base64.decode(base64_an, Base64.DEFAULT);
            System.out
                .printf("Base64_android解码(%s): %s\n", new String(base64_android_decode), base64_an);

            System.out.println("DES_android 解码：" + CyptoUtils.desDecode(base64_android_decode));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}