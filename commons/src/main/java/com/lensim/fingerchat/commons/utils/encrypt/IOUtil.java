package com.lensim.fingerchat.commons.utils.encrypt;


import com.lensim.fingerchat.commons.utils.L;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;

/**
 * 专门用于处理IO操作的工具类
 *
 * @author DrkCore
 * @since 2015年9月27日15:35:27
 */
public final class IOUtil {

    private IOUtil() {
    }

	/* 静默关闭IO对象 */

    /**
     * 静默关闭输入流。
     * 如果closeable同时
     *
     * @param closeable
     */
    public static void close(Closeable closeable) {
        if (closeable == null) {
            return;
        }

        if (closeable instanceof Flushable) {
            try {//如果是可flush的，就先flush一下
                Flushable flushable = (Flushable) closeable;
                flushable.flush();
            } catch (IOException e) {
                L.e(e);
            }
        }

        try {//静默关闭
            closeable.close();
        } catch (IOException e) {
            L.e(e);
        }
    }

	/* 读操作 */

    public static String read(InputStream in, Charset charset) throws IOException {
        return new String(read(in), charset);
    }

    public static byte[] read(InputStream in) throws IOException {
        ByteArrayOutputStream byteArrOut = null;
        try {
            byteArrOut = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len;
            while ((len = in.read(buffer)) != -1) {
                byteArrOut.write(buffer, 0, len);
            }
            byteArrOut.flush();
            return byteArrOut.toByteArray();
        } finally {
            close(byteArrOut);
            close(in);
        }
    }

	/* 写操作 */

    /**
     * 默认使用系统编码
     */
    public static void write(OutputStream out, String content) throws IOException {
        write(out, content, Charset.defaultCharset());
    }

    public static void write(OutputStream out, String content, Charset charset) throws IOException {
        OutputStreamWriter writer = null;
        try {
            writer = new OutputStreamWriter(out, charset);
            writer.write(content);
            writer.flush();
        } finally {
            close(writer);
            close(out);
        }
    }

    public static void write(OutputStream out, InputStream in) throws IOException {
        // 创建临时变量准备输入
        byte[] data = new byte[1024];
        int len;
        // 将数据写入指定的文件
        try {
            while ((len = in.read(data)) != -1) {
                out.write(data, 0, len);
            }
        } finally {
            close(in);
            close(out);
        }
    }
}
