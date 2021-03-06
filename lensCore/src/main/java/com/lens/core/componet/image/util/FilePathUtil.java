package com.lens.core.componet.image.util;

import android.os.Environment;

import java.io.File;

/**
 * Created by zm on 2018/4/20.
 */

public class FilePathUtil {

    private static String getBaseDir(String childDir) {
        return Environment.getExternalStorageDirectory().getPath() + "/fingerchat/" + childDir;
    }

    public static String getCacheCrop() {
        return checkAndMkdirs(getBaseDir("cache/crop/"));
    }

    public static String getCacheWeb() {
        return checkAndMkdirs(getBaseDir("cache/web/"));
    }

    public static String getCache() {
        return checkAndMkdirs(getBaseDir("cache/"));
    }

    public static String getImage() {
        return checkAndMkdirs(getBaseDir("image/"));
    }

    public static String getAdImage() {
        return checkAndMkdirs(getBaseDir("image/ad/"));
    }

    public static String getHomeDynaIcons() {
        return checkAndMkdirs(getBaseDir("image/home/icons/"));
    }

    /**
     * 检查文件夹是否存在
     *
     * @param dir
     * @return
     */
    public static String checkAndMkdirs(String dir) {
        File file = new File(dir);
        if (file.exists() == false) {
            file.mkdirs();
        }
        return dir;
    }
}
