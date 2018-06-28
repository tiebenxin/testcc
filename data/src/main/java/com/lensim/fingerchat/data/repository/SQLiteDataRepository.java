package com.lensim.fingerchat.data.repository;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.List;

/**
 * Created by ll147996 on 2017/12/15.
 * sqlite 数据存储管理类
 */

public class SQLiteDataRepository<T extends Serializable> implements DataSource<T> {

    @Override
    public List<T> getDatas() {
        return null;
    }


    @Override
    public T getData(@NonNull String dataId) {
        return null;
    }

    @Override
    public void saveData(@NonNull T data) {

    }


    @Override
    public void saveDatas(@NonNull List<T> datas) {

    }

    @Override
    public void updateData(@NonNull String dataId, @NonNull T data) {

    }

    @Override
    public void updateAllDatas(@NonNull List<T> datas) {

    }


    @Override
    public void deleteAllDatas() {

    }

    @Override
    public void deleteData(@NonNull String dataId) {

    }
}
