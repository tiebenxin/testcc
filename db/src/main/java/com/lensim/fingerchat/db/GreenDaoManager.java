package com.lensim.fingerchat.db;


import com.lensim.fingerchat.commons.helper.ContextHelper;
import com.lensim.fingerchat.db.greendao.DaoMaster;
import com.lensim.fingerchat.db.greendao.DaoSession;

/**
 * Created by ll147996 on 2018/1/19.
 *
 */

public class GreenDaoManager {
    private static final String DB_NAME = "fg_db";
    private static GreenDaoManager mInstance;
    private DaoMaster mDaoMaster;
    private DaoSession mDaoSession;

    private GreenDaoManager() {
        DaoMaster.DevOpenHelper devOpenHelper = new GreenDBOpenHelper(ContextHelper.getContext(), DB_NAME, null);
        mDaoMaster = new DaoMaster(devOpenHelper.getWritableDatabase());
        mDaoSession = mDaoMaster.newSession();
    }

    public DaoMaster getMaster() {
        return mDaoMaster;
    }

    public DaoSession getSession() {
        return mDaoSession;
    }

    public static GreenDaoManager getInstance() {
        if (mInstance == null) {
            mInstance = new GreenDaoManager();
        }
        return mInstance;
    }

}

