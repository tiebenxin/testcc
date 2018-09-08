package com.lensim.fingerchat.db.me;

import android.text.TextUtils;
import com.lensim.fingerchat.db.GreenDaoManager;
import com.lensim.fingerchat.db.greendao.FavJsonDBDao;
import com.lensim.fingerchat.db.greendao.FavJsonDBDao.Properties;
import java.util.ArrayList;
import java.util.List;

/**
 * date on 2018/3/16
 * author ll147996
 * describe
 */

public class FavManager {

    private static final FavManager favManager = new FavManager();
    private FavJsonDBDao favJsonDBDao;


    private FavManager() {
        favJsonDBDao = GreenDaoManager.getInstance().getSession().getFavJsonDBDao();
    }

    public static FavManager getInstance() {
        return favManager;
    }

    public long addFavJson(FavJsonDB favJsonDB) {
//        return favJsonDBDao.insert(favJsonDB);
        return favJsonDBDao.insertOrReplace(favJsonDB);
    }

    public void addFavJsons(List<FavJsonDB> favJsons) {
//        favJsonDBDao.insertInTx(favJsons);
        favJsonDBDao.insertOrReplaceInTx(favJsons);
    }

    public void deleteByKey(long favId ) {
        favJsonDBDao.deleteByKey(favId);
    }

    public void deleteByTime(String time) {
        favJsonDBDao.getSession().getDatabase().execSQL("DELETE FROM FavJsonDB WHERE LIKE time");
    }

    public void deleteAll() {
        favJsonDBDao.deleteAll();

    }

    public void update(FavJsonDB favJsonDB) {
        favJsonDBDao.update(favJsonDB);
    }

    public FavJsonDB getFavJsonDB(long favId) {
        return favJsonDBDao.load(favId);
    }

    /**
     * 搜索关键字
     * @param key 关键字
     * @param type 文字、图片、视频等类型
     * @return List<FavJsonDB>
     */
    public List<FavJsonDB> queryContent(String key, String type) {
        List<FavJsonDB> favJsonDBS = new ArrayList<>();

        if (TextUtils.isEmpty(key)) {
            for (FavJsonDB favJsonDB : queryFavJsons()) {
                if (favJsonDB.getFavType().equals(type)) {
                    favJsonDBS.add(favJsonDB);
                }
            }
        } else {
            for (FavJsonDB favJsonDB : queryFavJsons()) {
                if (favJsonDB.getFavType().equals(type) && favJsonDB.getFavContent().contains(key)) {
                    favJsonDBS.add(favJsonDB);
                }
            }
        }
        return favJsonDBS;
    }

    public boolean checkDuplicateMsgId(String favMsgId) {
        List<FavJsonDB> list = favJsonDBDao.loadAll();
        for (FavJsonDB item : list) {
            if (favMsgId.equals(item.getFavMsgId())) {
                return true;
            }
        }
        return false;
    }

    public boolean checkDuplicateByContent(String favMsgId, String url) {
        List<FavJsonDB> list = favJsonDBDao.loadAll();
        for (FavJsonDB item : list) {
            if (favMsgId.equals(item.getFavMsgId()) && url.equals(item.getFavUrl())) {
                return true;
            }
        }
        return false;
    }


    public List<FavJsonDB> queryFavJson() {
        return favJsonDBDao.queryBuilder()
            .limit(1)
            .build()
            .list();
    }


    public List<FavJsonDB> queryFavJsons() {
        return favJsonDBDao.queryBuilder()
            .orderDesc(Properties.FavTime)
            .build()
            .list();
    }

}
