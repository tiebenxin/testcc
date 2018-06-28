package com.lensim.fingerchat.data.me.content;

/**
 * date on 2018/3/13
 * author ll147996
 * describe
 */

public class FavJson {

    private long favId;

    /**
     * 唯一识别ID
     * 对应消息id或者朋友圈的id
     */
    private String favMsgId;
    /**
     * 对应发消息或者发朋友圈的人的jid （有可能是群，所以有下面的provider）  例子 ly309313@fingerchat.cn
     */
    private String providerJid;
    /**
     * 如果不为群聊消息，则与user一致，表示收藏内容的提供者
     * 群，则为群ID
     */
    private String favProvider;
    /**
     * 提供内容的人的昵称
     */
    private String providerNick;
    /**
     * 收藏内容
     **/
    private String favContent;
    /**
     * 内容的类型   * 1.text 文字 2.image 图片 3.video 视频 4.gif 大表情
     */
    private String favType;
    /**
     * 标签
     */
    private String favDes;
    /**
     * 收藏时间
     * yyyy-MM-dd HH:mm:ss
     * 2017-08-03T00:00:00
     */
    private String favTime;
    /**
     * 收藏者的 username
     */
    private String favCreater;
    /**
     * 收藏者的 头像
     */
    private String favCreaterAvatar;
    /**
     * getFileAddress or getText
     */
    private String favUrl;


    public FavJson() {}


    public FavJson(long favId, String favMsgId, String providerJid, String favProvider,
        String providerNick, String favContent, String favType, String favDes, String favTime,
        String favCreater, String favCreaterAvatar, String favUrl) {
        this.favId = favId;
        this.favMsgId = favMsgId;
        this.providerJid = providerJid;
        this.favProvider = favProvider;
        this.providerNick = providerNick;
        this.favContent = favContent;
        this.favType = favType;
        this.favDes = favDes;
        this.favTime = favTime;
        this.favCreater = favCreater;
        this.favCreaterAvatar = favCreaterAvatar;
        this.favUrl = favUrl;
    }

    public long getFavId() {
        return favId;
    }

    public void setFavId(long favId) {
        this.favId = favId;
    }

    public String getFavMsgId() {
        return favMsgId;
    }

    public void setFavMsgId(String favMsgId) {
        this.favMsgId = favMsgId;
    }

    public String getProviderJid() {
        return providerJid;
    }

    public void setProviderJid(String providerJid) {
        this.providerJid = providerJid;
    }

    public String getFavProvider() {
        return favProvider;
    }

    public void setFavProvider(String favProvider) {
        this.favProvider = favProvider;
    }

    public String getProviderNick() {
        return providerNick;
    }

    public void setProviderNick(String providerNick) {
        this.providerNick = providerNick;
    }

    public String getFavContent() {
        return favContent;
    }

    public void setFavContent(String favContent) {
        this.favContent = favContent;
    }

    public String getFavType() {
        return favType;
    }

    public void setFavType(String favType) {
        this.favType = favType;
    }

    public String getFavDes() {
        return favDes;
    }

    public void setFavDes(String favDes) {
        this.favDes = favDes;
    }

    public String getFavTime() {
        return favTime;
    }

    public void setFavTime(String favTime) {
        this.favTime = favTime;
    }

    public String getFavCreater() {
        return favCreater;
    }

    public void setFavCreater(String favCreater) {
        this.favCreater = favCreater;
    }

    public String getFavCreaterAvatar() {
        return favCreaterAvatar;
    }

    public void setFavCreaterAvatar(String favCreaterAvatar) {
        this.favCreaterAvatar = favCreaterAvatar;
    }

    public String getFavUrl() {
        return favUrl;
    }

    public void setFavUrl(String favUrl) {
        this.favUrl = favUrl;
    }
}
