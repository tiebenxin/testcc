package com.lens.chatmodel.cache;

import com.lensim.fingerchat.commons.utils.L;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;

public class LruDiskCache implements DiskCache {
    public static final int DEFAULT_BUFFER_SIZE = 32768;
    public static final int DEFAULT_COMPRESS_QUALITY = 100;
    private static final String ERROR_ARG_NULL = " argument must be not null";
    private static final String ERROR_ARG_NEGATIVE = " argument must be positive number";
    protected DiskLruCache cache;
    private File reserveCacheDir;
    protected final FileNameGenerator fileNameGenerator;
    protected int bufferSize;


    public LruDiskCache(File cacheDir, FileNameGenerator fileNameGenerator, long cacheMaxSize) throws IOException {
        this(cacheDir, (File)null, fileNameGenerator, cacheMaxSize);
    }

    public LruDiskCache(File cacheDir, File reserveCacheDir, FileNameGenerator fileNameGenerator, long cacheMaxSize) throws IOException {
        this.bufferSize = '耀';

        if(cacheDir == null) {
            throw new IllegalArgumentException("cacheDir argument must be not null");
        } else if(cacheMaxSize < 0L) {
            throw new IllegalArgumentException("cacheMaxSize argument must be positive number");
        } else if(fileNameGenerator == null) {
            throw new IllegalArgumentException("fileNameGenerator argument must be not null");
        } else {
            if(cacheMaxSize == 0L) {
                cacheMaxSize = 9223372036854775807L;
            }

            this.reserveCacheDir = reserveCacheDir;
            this.fileNameGenerator = fileNameGenerator;
            this.initCache(cacheDir, reserveCacheDir, cacheMaxSize);
        }
    }

    private void initCache(File cacheDir, File reserveCacheDir, long cacheMaxSize) throws IOException {
        try {
            this.cache = DiskLruCache.open(cacheDir, 1, 1, cacheMaxSize);
        } catch (IOException var7) {
            L.e(var7);
            if(reserveCacheDir != null) {
                this.initCache(reserveCacheDir, (File)null, cacheMaxSize);
            }

            if(this.cache == null) {
                throw var7;
            }
        }

    }

    public File getDirectory() {
        return this.cache.getDirectory();
    }

    public File get(String imageUri) {
        DiskLruCache.Snapshot snapshot = null;

        Object var4;
        try {
            snapshot = this.cache.get(this.getKey(imageUri));
            File e = snapshot == null?null:snapshot.getFile(0);
            return e;
        } catch (IOException var8) {
            L.e(var8);
            var4 = null;
        } finally {
            if(snapshot != null) {
                snapshot.close();
            }

        }

        return (File)var4;
    }


    public boolean remove(String imageUri) {
        try {
            return this.cache.remove(this.getKey(imageUri));
        } catch (IOException var3) {
            L.e(var3);
            return false;
        }
    }

    @Override
    public boolean save(String url, byte[] data){
        boolean successfully = false;
        DiskLruCache.Editor edit = null;
        BufferedOutputStream  outputStream = null;
        try{
            edit = this.cache.edit(this.getKey(url));
            if(edit != null){
                outputStream = new BufferedOutputStream(edit.newOutputStream(0),8*1024);
                outputStream.write(data);
                successfully = true;
            }else
                successfully = false;

        }catch (IOException e){
            e.printStackTrace();
            successfully = false;
        }finally {
            IoUtils.closeSilently(outputStream);
            if(edit != null){
                try {
                    if( successfully){
                        edit.commit();
                    }else
                        edit.abort();
                }catch (IOException e){
                    e.printStackTrace();
                }
            }

        }
        return successfully;
    }

    public void close() {
        try {
            this.cache.close();
        } catch (IOException var2) {
            L.e(var2);
        }

        this.cache = null;
    }

    public void clear() {
        try {
            this.cache.delete();
        } catch (IOException var3) {
            L.e(var3);
        }

        try {
            this.initCache(this.cache.getDirectory(), this.reserveCacheDir, this.cache.getMaxSize());
        } catch (IOException var2) {
            L.e(var2);
        }

    }

    private String getKey(String imageUri) {
        return this.fileNameGenerator.generate(imageUri);
    }

    public void setBufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
    }

}