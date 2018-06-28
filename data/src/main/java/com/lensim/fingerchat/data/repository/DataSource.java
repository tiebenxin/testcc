package com.lensim.fingerchat.data.repository;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.List;

/**
 * Created by ll147996 on 2017/12/13.
 *
 */
public interface DataSource<T extends Serializable> {


    T getData(@NonNull String dataId);

    List<T> getDatas();

    void saveData(@NonNull T data);

    void saveDatas(@NonNull List<T> datas);

    void updateData(@NonNull String dataId, @NonNull T data);

    void updateAllDatas(@NonNull List<T> datas);

    void deleteAllDatas();

    void deleteData(@NonNull String dataId);
}
