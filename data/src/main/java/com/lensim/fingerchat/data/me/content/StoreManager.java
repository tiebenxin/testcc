package com.lensim.fingerchat.data.me.content;


import android.annotation.SuppressLint;
import android.text.TextUtils;
import com.lens.core.componet.net.exeception.ApiException;
import com.lensim.fingerchat.commons.base.BaseResponse;
import com.lensim.fingerchat.commons.global.Route;
import com.lensim.fingerchat.commons.http.FXRxSubscriberHelper;
import com.lensim.fingerchat.commons.utils.T;
import com.lensim.fingerchat.commons.utils.TimeUtils;
import com.lensim.fingerchat.data.CollectionApi;
import com.lensim.fingerchat.data.bean.AddFavoryRequestBody;
import com.lensim.fingerchat.data.login.UserInfoRepository;

/**
 * Created by LL117394 on 2017/5/19.
 *
 * @return -1出错 -2已经收藏 过 其它收藏 成功
 */
public class StoreManager {

    boolean isFromeChat = false;
    boolean misIncome;
    //朋友圈
    private String mCircleID;
    private String mUserNick;
    private String mUserName;
    private String mAvatarUrl;
    private int mType;

    //聊天
    private String mPacketID;
    private String mJID;
    private String mName;
    private String mResource;
    private String mText;
    private String mFileAddress;
    private int msgType;


    private StoreManager() {
    }

    public static StoreManager getInstance() {
        return SingletonHolder.instance;
    }


    private static class SingletonHolder {

        private final static StoreManager instance = new StoreManager();
    }

    /***
     * 收藏聊天文字、图片，视频
     *
     * 如果是别人发给我的消息，content为getText 不管什么样的消息
     *我发给别人的，分二种情况
     * **/
    public void storeMessage(FavJson json) {

    }

    /***
     * 收藏聊天文字、图片，视频
     * **/
//    public void storeMessage(AbstractChat chat, boolean isIncome, String packetID, String Jid,
//        String name, String resource, int msgType, String content) {
//
//    }


    /***
     * 收藏笔记
     * **/
    public void storeNote(String id, String content) {
        if (CollectionManager.getInstance().checkDuplicateByID(id)) {
            return;
        }
        FavJson store = new FavJson();
        store.setFavId(System.currentTimeMillis());
        store.setFavMsgId(id);
        store.setProviderJid(UserInfoRepository.getUserName());
        store.setProviderNick(UserInfoRepository.getUsernick());
        store.setFavContent(content);
        //1.text 文字 2.image 图片 3.video 视频 4.gif 大表情
        store.setFavType("88");
        store.setFavUrl(content);
        store.setFavCreaterAvatar(
            String.format(Route.obtainAvater, UserInfoRepository.getUserName()));
        store.setFavCreater(UserInfoRepository.getUserName());
        store.setFavTime(TimeUtils.getDate());
        store.setFavDes("");
        store.setFavUrl("");
        store.setFavProvider(store.getProviderJid());
        //CollectionManager.getInstance().collection(store);
        upload(store);

    }

    /***
     * 收藏朋友圈文字
     * **/
    public void storeCircleText(String id, String userName, String userNick, String text,
        String avatarUrl) {
        if (CollectionManager.getInstance().checkTextExistByID(id)) {
            return;
        }
        FavJson store = new FavJson();
        store.setFavMsgId(id);
        store.setProviderJid(userName);
        store.setFavProvider(userName);
        store.setProviderNick(userNick);
        store.setFavContent(text);
        store.setFavType("1");
        store.setFavUrl(text);
        store.setFavCreaterAvatar(
            TextUtils.isEmpty(avatarUrl) ? String.format(Route.obtainAvater, userName) : avatarUrl);
        store.setFavCreater(userName);
        store.setFavTime(TimeUtils.getDate());
        store.setFavDes("");
        store.setFavId(System.currentTimeMillis());
        upload(store);
    }


    /***
     * 收藏相册、朋友圈图片和视频
     * **/
    public void storeCircleImageVideo(String circleID, String userName, String userNick,
        String path, String avatarUrl, int mType) {
        if (TextUtils.isEmpty(path) || CollectionManager.getInstance()
            .checkDuplicateByContent(circleID, path)) {
            return;
        }
        FavJson store = new FavJson();
        store.setFavMsgId(circleID);
        store.setProviderJid(userName);
        store.setFavProvider(userName);
        store.setProviderNick(userNick);
        store.setFavContent(path);
        store.setFavType(mType + "");
        store.setFavUrl(path);
        store.setFavCreaterAvatar(
            TextUtils.isEmpty(avatarUrl) ? String.format(Route.obtainAvater, userName) : avatarUrl);
        store.setFavCreater(UserInfoRepository.getUserName());
        store.setFavTime(TimeUtils.getDate());
        store.setFavDes("");
        store.setFavId(System.currentTimeMillis());
        upload(store);

    }


    /**
     * 设置参数——朋友圈或者相册——点击后出的大图,
     */
    public void storeInit(String circleID, String userName, String userNick, String avatarUrl,
        int mType) {
        this.mCircleID = circleID;
        this.mUserNick = userNick;
        this.mUserName = userName;
        this.mAvatarUrl = avatarUrl;
        this.mType = mType;
    }

    /**
     * 设置参数——朋友圈或者相册——点击后出的大图,
     */
    public void storeInit(String userName, String userNick, String avatarUrl, int mType) {
        this.mUserNick = userNick;
        this.mUserName = userName;
        this.mAvatarUrl = avatarUrl;
        this.mType = mType;
    }

    /**
     * 使用前必须设置参数
     */
    public void storeFromGallery(String msgID, String path) {
        if (!TextUtils.isEmpty(msgID)) {
//            storeInit(true, MessageManager.getInstance().getMessageItemById(mChat, msgID));
//            storeFromChat();
        }
    }

    /**
     * 设置参数——聊天
     */
//    public void storeInit(boolean isFromeChat, MessageItem message) {
//
//    }

    /**
     * 使用前必须设置参数
     */
    public void storeFromChat() {
        String content;
        if (misIncome || msgType == 1) {
            content = mText;
        } else {
            content = mFileAddress;
        }
        if (!TextUtils.isEmpty(mPacketID) && !TextUtils.isEmpty(mJID) &&
            !TextUtils.isEmpty(mName) && !TextUtils.isEmpty(content)) {
//            storeMessage(mChat, misIncome, mPacketID, mJID, mName, mResource, msgType, content);
        }
    }

    /**
     * 上传
     */
    @SuppressLint("CheckResult")
    public void upload(FavJson store) {
        //CollectionManager.getInstance().collection(store);
        CollectionManager.getInstance().collect(store);
        AddFavoryRequestBody body = new AddFavoryRequestBody();
        body.setCreator(store.getFavCreater());
        body.setFrom(store.getProviderJid());
        body.setFromNickname(store.getProviderNick());
        body.setMsgContent(store.getFavContent());
        body.setMsgId(store.getFavMsgId());
        body.setMsgType(Integer.parseInt(store.getFavType()));
        body.setTags("");
        body.setProvider(store.getProviderJid());
        //Log.e("ttt---", body.toString());
        new CollectionApi().addFavory(body, new FXRxSubscriberHelper<BaseResponse>() {
            @Override
            public void _onNext(BaseResponse baseResponse) {
                if (null != baseResponse && "Ok".equals(baseResponse.getMessage())) {
                    T.show("收藏成功");
                } else {
                    CollectionManager.getInstance().deleteByFavId(store.getFavMsgId());
                }

            }

            @Override
            public void _onError(ApiException error) {
                super._onError(error);
                CollectionManager.getInstance().deleteByFavId(store.getFavMsgId());
            }
        });
        /*Http.createFavList(store)
            .compose(RxSchedulers.io_main())
            .subscribe(responseBody -> {
                String res = responseBody.string();
                res = convertJSONString(res);
                RetResponse ret = GsonHelper.getObject(res, RetResponse.class);
                if (ret.retCode != 1) {
                    CollectionManager.getInstance().deleteByFavId(store.getFavId());
                }
                Toast.makeText(ContextHelper.getContext(), ret.retMsg, Toast.LENGTH_SHORT).show();
            });*/
    }

    public String convertJSONString(String data_json) {
        data_json = data_json.replaceAll("\\\\r\\\\n", "");
        data_json = data_json.replaceAll("\\\\", "");//关键是这句
        data_json = data_json.replace("\"{", "{");
        data_json = data_json.replace("}\",", "},");
        data_json = data_json.replace("}\"", "}");
        return data_json;
    }
}
