package com.lensim.fingerchat.commons.interf;

public interface IConvert<T, V> {
    V convert(T item);
}
