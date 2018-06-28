package com.lensim.fingerchat.data.me.picture;

import com.nostra13.universalimageloader.core.decode.BaseImageDecoder;
import com.nostra13.universalimageloader.core.decode.ImageDecodingInfo;
import java.io.IOException;
import java.io.InputStream;
import javax.crypto.CipherInputStream;

/**
 * Created by LY309313 on 2017/5/12.
 */

public class CipherImageDecoder extends BaseImageDecoder {


    public CipherImageDecoder(boolean loggingEnabled) {
        super(loggingEnabled);
    }

    @Override
    protected InputStream getImageStream(ImageDecodingInfo decodingInfo) throws IOException {

        InputStream imageStream = super.getImageStream(decodingInfo);
        /**
         * 只解密网络下载的图片，其他的途径的图片不用加密所以就不用解密，加解密手段用的是Java的加解密输入流
         */
        if (decodingInfo.getOriginalImageUri().startsWith("http")) {
            CipherInputStream in = null;
            try {
                in = new CipherInputStream(imageStream, CipherImage.getInstance().getDecryptCipher());
            } catch (Exception e) {
                e.printStackTrace();
                imageStream.close();
            }
            return in;
        }
        return imageStream;
    }
}
