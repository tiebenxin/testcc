package com.lensim.fingerchat.data.me.content;


import android.util.Log;
import com.lensim.fingerchat.db.me.FavJsonDB;
import com.lensim.fingerchat.db.me.FavManager;
import java.util.ArrayList;
import java.util.List;


/**
 * date on 2018/3/13
 * author ll147996
 * describe
 */

public class CollectionManager {

    private final static CollectionManager instance = new CollectionManager();


    private CollectionManager() {

    }

    public static CollectionManager getInstance() {
        return instance;
    }

    /**
     * 保存收藏对象
     */
    public void collection(FavJson favJson) {
        FavManager.getInstance().addFavJson(createFavJsonDB(favJson));
    }

    /**
     * 保存收藏对象
     */
    public void collections(List<FavJson> favJsons) {
        FavManager.getInstance().addFavJsons(createFavJsonDBS(favJsons));
    }

    public void updateItemDES(long favId, String DES) {
        FavJsonDB favJsonDB = FavManager.getInstance().getFavJsonDB(favId);
        favJsonDB.setFavDes(DES);
        FavManager.getInstance().update(favJsonDB);
    }

    public String getItemDES(long favId) {
        return FavManager.getInstance().getFavJsonDB(favId).getFavDes();
    }

    /***
     * 仅针聊天，笔记
     * 以前收藏过，返回true否则false
     * */
    public boolean checkDuplicateByID(String favMsgId) {
        return FavManager.getInstance().checkDuplicateMsgId(favMsgId);
    }

    /***
     * 仅仅针对朋友圈文字
     * 以前收藏过，返回true否则false
     * */
    public boolean checkTextExistByID(String favMsgId) {
        return FavManager.getInstance().checkDuplicateMsgId(favMsgId);
    }


    /***
     * 仅仅针对朋友圈图片、视频、语音
     * 以前收藏过，返回true否则false
     * */
    public boolean checkDuplicateByContent(String favMsgId, String url) {
        return FavManager.getInstance().checkDuplicateByContent(favMsgId, url);
    }


    /***
     *   返回1则删除成功
     * */
    public void deleteItemByTime(String timeStamp) {
        FavManager.getInstance().deleteByTime(timeStamp);
    }

    public void deleteByFavId(long favId) {
        FavManager.getInstance().deleteByKey(favId);
    }

    /**
     * 搜索关键字
     * @param key 关键字
     * @param type 文字、图片、视频等类型
     * @return List<FavJson>
     */
    public List<FavJson> queryContent(String key, String type) {
        List<FavJsonDB> favJsonDBS = FavManager.getInstance().queryContent(key, type);
        return createFavJsons(favJsonDBS);
    }

    /**
     * 获取所有的收藏
     */
    public List<FavJson> getCollections() {
        return createFavJsons(FavManager.getInstance().queryFavJsons());
    }

    /**
     * 获取所有的收藏 ——分页
     */
//    public List<FavJson> getCollectionsByPage(int pageNum, String pageSize) {
//        return createFavJsons(FavManager.getInstance().queryFavJsons(pageNum, Integer.parseInt(pageSize)));
//    }

    public FavJson getLastFavJson() {
        return createFavJson(FavManager.getInstance().queryFavJson().get(0));
    }


    private FavJsonDB createFavJsonDB(FavJson favJsonDB) {
        return new FavJsonDB(favJsonDB.getFavId(), favJsonDB.getFavMsgId(), favJsonDB.getProviderJid(),
            favJsonDB.getFavProvider(), favJsonDB.getProviderNick(), favJsonDB.getFavContent(),
            favJsonDB.getFavType(), favJsonDB.getFavDes(), favJsonDB.getFavTime(),
            favJsonDB.getFavCreater(), favJsonDB.getFavCreaterAvatar(), favJsonDB.getFavUrl());
    }

    private List<FavJsonDB> createFavJsonDBS(List<FavJson> favJsonDBS) {
        List<FavJsonDB> list = new ArrayList<>();
        for (FavJson favJson : favJsonDBS) {
            Log.e("time",favJson.getFavTime());
            list.add(createFavJsonDB(favJson));
        }
        return list;
    }

    private FavJson createFavJson(FavJsonDB favJsonDB) {
        if (favJsonDB == null) return null;

        return new FavJson(favJsonDB.getFavId(), favJsonDB.getFavMsgId(), favJsonDB.getProviderJid(),
            favJsonDB.getFavProvider(), favJsonDB.getProviderNick(), favJsonDB.getFavContent(),
            favJsonDB.getFavType(), favJsonDB.getFavDes(), favJsonDB.getFavTime(),
            favJsonDB.getFavCreater(), favJsonDB.getFavCreaterAvatar(), favJsonDB.getFavUrl());
    }

    private List<FavJson> createFavJsons(List<FavJsonDB> favJsonDBS) {
        List<FavJson> list = new ArrayList<>();
        for (FavJsonDB favJson : favJsonDBS) {
            list.add(createFavJson(favJson));
        }
        return list;
    }
}
