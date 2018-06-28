package com.lensim.fingerchat.data.me.picture;


import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.FileNameGenerator;
import com.nostra13.universalimageloader.utils.IoUtils;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import javax.crypto.CipherInputStream;

/**
 * Created by LY309313 on 2017/5/12.
 */

public class CipherUnlimitedDiskCache extends UnlimitedDiskCache {


    public CipherUnlimitedDiskCache(File cacheDir) {
        super(cacheDir);
    }

    public CipherUnlimitedDiskCache(File cacheDir, File reserveCacheDir) {
        super(cacheDir, reserveCacheDir);
    }

    public CipherUnlimitedDiskCache(File cacheDir, File reserveCacheDir, FileNameGenerator fileNameGenerator) {
        super(cacheDir, reserveCacheDir, fileNameGenerator);
    }

    @Override
    public boolean save(String imageUri, InputStream imageStream, IoUtils.CopyListener listener) throws IOException {
        InputStream inputStream;
        if (imageUri.startsWith("http")) {
            //只加密网络图片
            try {
                inputStream = new CipherInputStream(imageStream, CipherImage.getInstance().getEncryptCipher());
            } catch (Exception e) {
                imageStream.close();
                e.printStackTrace();
                return false;
            }
        } else {
            inputStream = imageStream;
        }
        return super.save(imageUri, inputStream, listener);
    }
}
