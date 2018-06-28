package com.fingerchat.api.util;

import java.nio.ByteBuffer;

/**
 * Created by LY309313 on 2017/9/23.
 */

public final class ByteBuf {


    private ByteBuffer tmpNioBuf;

    public static ByteBuf allocate(int capacity) {
        ByteBuf buffer = new ByteBuf();
        buffer.tmpNioBuf = ByteBuffer.allocate(capacity);
        return buffer;
    }

    public static ByteBuf allocateDirect(int capacity) {
        ByteBuf buffer = new ByteBuf();
        buffer.tmpNioBuf = ByteBuffer.allocateDirect(capacity);
        return buffer;
    }

    public static ByteBuf wrap(byte[] array) {
        ByteBuf buffer = new ByteBuf();
        buffer.tmpNioBuf = ByteBuffer.wrap(array);
        return buffer;
    }

    public byte[] getArray() {
        tmpNioBuf.flip();
        byte[] array = new byte[tmpNioBuf.remaining()];
        tmpNioBuf.get(array);
        tmpNioBuf.compact();
        return array;
    }

    public ByteBuf get(byte[] array) {
        tmpNioBuf.get(array);
        return this;
    }

    public byte get() {
        return tmpNioBuf.get();
    }

    public ByteBuf put(byte b) {
        checkCapacity(1);
        tmpNioBuf.put(b);
        return this;
    }

    public short getShort() {
        return tmpNioBuf.getShort();
    }

    public ByteBuf putShort(int value) {
        checkCapacity(2);
        tmpNioBuf.putShort((short) value);
        return this;
    }

    public int getInt() {
        return tmpNioBuf.getInt();
    }

    public ByteBuf putInt(int value) {
        checkCapacity(4);
        tmpNioBuf.putInt(value);
        return this;
    }

    public long getLong() {
        return tmpNioBuf.getLong();
    }

    public ByteBuf putLong(long value) {
        checkCapacity(8);
        tmpNioBuf.putLong(value);
        return this;
    }

    public ByteBuf put(byte[] value) {
        checkCapacity(value.length);
        tmpNioBuf.put(value);
        return this;
    }

    public ByteBuf checkCapacity(int minWritableBytes) {
        int remaining = tmpNioBuf.remaining();
        if (remaining < minWritableBytes) {
            int newCapacity = newCapacity(tmpNioBuf.capacity() + minWritableBytes);
            ByteBuffer newBuffer = tmpNioBuf.isDirect() ? ByteBuffer.allocateDirect(newCapacity) : ByteBuffer.allocate(newCapacity);
            tmpNioBuf.flip();
            newBuffer.put(tmpNioBuf);
            tmpNioBuf = newBuffer;
        }
        return this;
    }

    private int newCapacity(int minNewCapacity) {
        int newCapacity = 64;
        while (newCapacity < minNewCapacity) {
            newCapacity <<= 1;
        }
        return newCapacity;
    }

    public ByteBuffer nioBuffer() {
        return tmpNioBuf;
    }

    public ByteBuf clear() {
        tmpNioBuf.clear();
        return this;
    }

    public ByteBuf flip() {
        tmpNioBuf.flip();
        return this;
    }

}
