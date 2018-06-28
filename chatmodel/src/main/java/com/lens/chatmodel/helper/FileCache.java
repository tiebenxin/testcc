package com.lens.chatmodel.helper;

import android.content.Context;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.util.Log;
import com.lens.chatmodel.cache.AESEncryptor;
import com.lens.chatmodel.cache.FileNameGenerator;
import com.lens.chatmodel.cache.LruDiskCache;
import com.lens.chatmodel.cache.Md5FileNameGenerator;
import com.lensim.fingerchat.commons.app.AppConfig;
import com.lensim.fingerchat.commons.helper.ContextHelper;
import com.lensim.fingerchat.commons.utils.FileUtil;
import com.lensim.fingerchat.commons.utils.L;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.Arrays;

/**
 * Created by LY309313 on 2016/11/7.
 */

public class FileCache {

    // public static final String CACHE_DIR = "finger_caches";
    private static final String VOICE = "cache_voice";
    public static final String VIDEO = "cache_video";
    //  private  DiskLruCache mVideoCache;
    // private  DiskLruCache mVoiceCache;
    private static final FileCache instance = new FileCache(ContextHelper.getContext());
    private static final int REVERSE_LENGTH = 1024;
    private final FileNameGenerator generator;

//   static{
//       instance = new FileCache(MyApplication.getInstance().getApplication());
//   }

    private LruDiskCache mVoiceCache;
    private LruDiskCache mVideoCache;


    private FileCache(Context context) {
        generator = new Md5FileNameGenerator();
        try {
            File voiceCacheDirs = FileUtil.getDiskCacheDirs(context, VOICE);
            if (!voiceCacheDirs.exists()) {
                voiceCacheDirs.mkdirs();
            }
            File videoCacheDirs = FileUtil.getDiskCacheDirs(context, VIDEO);
            if (!videoCacheDirs.exists()) {
                videoCacheDirs.mkdirs();
            }
            mVideoCache = new LruDiskCache(videoCacheDirs, generator, 100 * 1024 * 1024);
            mVoiceCache = new LruDiskCache(voiceCacheDirs, generator, 100 * 1024 * 1024);
            L.i("初始化disklrucache成功");
        } catch (IOException e) {
            e.printStackTrace();
            L.i("初始化disklrucache失败");
        }

    }

    public static FileCache getInstance() {
        return instance;
    }

    public synchronized boolean saveVideo(String url, byte[] data) throws IOException {
        byte[] bytes = encrypt(data);
        mVideoCache.save(url, bytes);

        return true;

    }


    public synchronized boolean saveVoice(String url, byte[] data) throws IOException {
        byte[] bytes = encryptVoice(data);
        mVoiceCache.save(url, bytes);
        return true;
    }

    private byte[] encryptVoice(byte[] data) {
        try {
            long t = System.currentTimeMillis();
            AESEncryptor.Builder builder = new AESEncryptor.Builder();
            AESEncryptor encryptor = builder.setCharset(Charset.forName("UTF-8"))
                .setDerivedKey(AppConfig.getDefaultKey(), 128)
                .setMode(AESEncryptor.MODE_CFB)
                .setPadding(AESEncryptor.PADDING_NONE)
                .build();

            byte[] bytes = encryptor.encrypt(data);
            L.i("FileCache", "加密成功用时:" + (System.currentTimeMillis() - t));
            return bytes;
        } catch (Exception e) {
            e.printStackTrace();

        }
        return null;
    }


    /**
     * 加解密
     *
     * @param strFile 源文件绝对路径
     */
    public File decryptVoice(String strFile) {
        FileChannel sourceFC = null;
        FileChannel targetFC = null;
        try {
            File sf = new File(strFile);
            File tf = File.createTempFile("temp", "voice",
                ContextHelper.getContext().getCacheDir());
            tf.deleteOnExit();
            AESEncryptor.Builder builder = new AESEncryptor.Builder();
            AESEncryptor encryptor = builder.setCharset(Charset.forName("UTF-8"))
                .setDerivedKey(AppConfig.getDefaultKey(), 128)
                .setMode(AESEncryptor.MODE_CFB)
                .setPadding(AESEncryptor.PADDING_NONE)
                .build();

            sourceFC = new RandomAccessFile(sf, "r").getChannel();
            targetFC = new RandomAccessFile(tf, "rw").getChannel();
            ByteBuffer byteData = ByteBuffer.allocate(1024);
            while (sourceFC.read(byteData) != -1) {
                // 通过通道读写交叉进行。
                // 将缓冲区准备为数据传出状态
                byteData.flip();

                byte[] byteList = new byte[byteData.remaining()];
                byteData.get(byteList, 0, byteList.length);
//此处，若不使用数组加密解密会失败，因为当byteData达不到1024个时，加密方式不同对空白字节的处理也不相同，从而导致成功与失败。
                byte[] bytes = encryptor.decrypt(byteList);
                targetFC.write(ByteBuffer.wrap(bytes));
                byteData.clear();
            }

            return tf;
        } catch (Exception e) {
            Log.d("FileCache", e.getMessage());
            return null;
        } finally {
            try {
                if (sourceFC != null) {
                    sourceFC.close();
                }
                if (targetFC != null) {
                    targetFC.close();
                }
            } catch (IOException e) {
                Log.d("FileCache", e.getMessage());
            }
        }
    }

    public File encryptVideo(String src, String target) {
        FileChannel sourceFC = null;
        FileChannel targetFC = null;
        try {
            File sf = new File(src);
            File tf = new File(target);
            AESEncryptor.Builder builder = new AESEncryptor.Builder();
            AESEncryptor encryptor = builder.setCharset(Charset.forName("UTF-8"))
                .setDerivedKey(AppConfig.getDefaultKey(), 128)
                .setMode(AESEncryptor.MODE_CFB)
                .setPadding(AESEncryptor.PADDING_NONE)
                .build();

            sourceFC = new RandomAccessFile(sf, "r").getChannel();
            targetFC = new RandomAccessFile(tf, "rw").getChannel();
            ByteBuffer byteData = ByteBuffer.allocate(1024);
            boolean encrypt = false;
            while (sourceFC.read(byteData) != -1) {
                // 通过通道读写交叉进行。
                // 将缓冲区准备为数据传出状态
                byteData.flip();

                byte[] byteList = new byte[byteData.remaining()];
                byteData.get(byteList, 0, byteList.length);
//此处，若不使用数组加密解密会失败，因为当byteData达不到1024个时，加密方式不同对空白字节的处理也不相同，从而导致成功与失败。
                if (encrypt) {
                    targetFC.write(ByteBuffer.wrap(byteList));
                } else {
                    byte[] bytes = encryptor.encrypt(byteList);
                    targetFC.write(ByteBuffer.wrap(bytes));
                    encrypt = true;
                }
                byteData.clear();
            }

            L.d("FileCache" + "-- 加密成功 two String");

            return tf;
        } catch (Exception e) {
            Log.d("FileCache", e.getMessage());
            return null;
        } finally {
            try {
                if (sourceFC != null) {
                    sourceFC.close();
                }
                if (targetFC != null) {
                    targetFC.close();
                }
            } catch (IOException e) {
                Log.d("FileCache", e.getMessage());
            }
        }
    }

    public void clear() {
        mVideoCache.clear();
        mVoiceCache.clear();
    }


    public String getVideoPath(String url) {

        File file = mVideoCache.get(url);
        if (file == null) {
            return "";
        }
        return file.getAbsolutePath();
    }

    public File getVideo(String url) {
        return mVideoCache.get(url);
    }

    public String getVoicePath(String url) {
        File file = mVoiceCache.get(url);
        if (file == null) {
            return "";
        }

        return file.getAbsolutePath();
    }

    public boolean removeVideo(String uri) {
        return mVideoCache.remove(uri);
    }

    public int getVersionName() {
        try {
            return ContextHelper.getContext().getPackageManager()
                .getPackageInfo(ContextHelper.getContext().getPackageName(),
                    0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 1;
    }


    /**
     * 加解密
     *
     * @param strFile 源文件绝对路径
     */
    public boolean encrypt(String strFile) {
        int len = REVERSE_LENGTH;
        try {
            File f = new File(strFile);

            AESEncryptor.Builder builder = new AESEncryptor.Builder();
            AESEncryptor encryptor = builder.setCharset(Charset.forName("UTF-8"))
                .setDerivedKey(AppConfig.getDefaultKey(), 128)
                .setMode(AESEncryptor.MODE_CFB)
                .setPadding(AESEncryptor.PADDING_NONE)
                .build();

            RandomAccessFile raf = new RandomAccessFile(f, "rw");
            long totalLen = raf.length();

            if (totalLen < REVERSE_LENGTH) {
                len = (int) totalLen;
            }

            FileChannel channel = raf.getChannel();
            MappedByteBuffer buffer = channel.map(
                FileChannel.MapMode.READ_WRITE, 0, REVERSE_LENGTH);
            // byte tmp;
            byte[] data = new byte[len];
            for (int i = 0; i < len; ++i) {
                byte rawByte = buffer.get(i);
                data[i] = rawByte;
                // tmp = (byte) (rawByte ^ i);
                // buffer.put(i, tmp);
            }

            byte[] bytes = encryptor.encrypt(data);
            for (int i = 0; i < len; i++) {
                buffer.put(i, bytes[i]);
            }
            buffer.force();
            buffer.clear();
            channel.close();
            raf.close();
            L.i("FileCache" + "--加密成功 one String");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean encryptVoice(String strFile) {
        try {
            File f = new File(strFile);

            AESEncryptor.Builder builder = new AESEncryptor.Builder();
            AESEncryptor encryptor = builder.setCharset(Charset.forName("UTF-8"))
                .setDerivedKey(AppConfig.getDefaultKey(), 128)
                .setMode(AESEncryptor.MODE_CFB)
                .setPadding(AESEncryptor.PADDING_NONE)
                .build();

            RandomAccessFile raf = new RandomAccessFile(f, "rw");
            long totalLen = raf.length();
            FileChannel channel = raf.getChannel();
            MappedByteBuffer buffer = channel.map(
                FileChannel.MapMode.READ_WRITE, 0, totalLen);
            // byte tmp;

            byte[] data = new byte[((int) totalLen)];

            for (int i = 0; i < totalLen; ++i) {
                byte rawByte = buffer.get(i);
                data[i] = rawByte;
            }

            byte[] bytes = encryptor.encrypt(data);
            buffer.put(bytes);
            buffer.force();
            buffer.clear();
            channel.close();
            raf.close();
            L.i("FileCache", "加密成功");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 加解密
     *
     * @param strFile 源文件绝对路径
     */
    public boolean decrypt(String strFile) {
        int len = REVERSE_LENGTH;
        try {
            File f = new File(strFile);
            AESEncryptor.Builder builder = new AESEncryptor.Builder();
            AESEncryptor encryptor = builder.setCharset(Charset.forName("UTF-8"))
                .setDerivedKey(AppConfig.getDefaultKey(), 128)
                .setMode(AESEncryptor.MODE_CFB)
                .setPadding(AESEncryptor.PADDING_NONE)
                .build();

            RandomAccessFile raf = new RandomAccessFile(f, "rw");
            long totalLen = raf.length();

            if (totalLen < REVERSE_LENGTH) {
                len = (int) totalLen;
            }

            FileChannel channel = raf.getChannel();
            MappedByteBuffer buffer = channel.map(
                FileChannel.MapMode.READ_WRITE, 0, REVERSE_LENGTH);
            // byte tmp;
            byte[] data = new byte[len];
            for (int i = 0; i < len; ++i) {
                byte rawByte = buffer.get(i);
                data[i] = rawByte;
            }

            byte[] bytes = encryptor.decrypt(data);
            for (int i = 0; i < len; i++) {
                buffer.put(i, bytes[i]);
            }

            buffer.force();
            // buffer.clear();
            channel.close();
            raf.close();

            L.i("FileCache" + "--解密成功");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    private byte[] encrypt(byte[] datas) {
        try {
            AESEncryptor.Builder builder = new AESEncryptor.Builder();
            AESEncryptor encryptor = builder.setCharset(Charset.forName("UTF-8"))
                .setDerivedKey(AppConfig.getDefaultKey(), 128)
                .setMode(AESEncryptor.MODE_CFB)
                .setPadding(AESEncryptor.PADDING_NONE)
                .build();
            byte[] data = Arrays.copyOf(datas, REVERSE_LENGTH);

            byte[] bytes = encryptor.encrypt(data);

            System.arraycopy(bytes, 0, datas, 0, REVERSE_LENGTH);
            L.i("FileCache" + "--加密成功");
            return datas;
        } catch (Exception e) {
            e.printStackTrace();

        }
        return null;

    }

    //检查是否加密了
    public static boolean checkVideoHasEncrypt(String path) {
        if (!TextUtils.isEmpty(path)) {
            if (path.contains(".mp4")) {
                String jpg = path.replace(".mp4", ".jpg");
                File file = new File(jpg);
                if (file.exists()) {
                    return true;
                }
            } else if (path.contains(".0")) {
                return true;
            } else {
                File file = FileCache.getInstance().getVideo(path);
                if (file.exists()) {
                    return true;
                }
            }
        }
        return false;
    }
}
