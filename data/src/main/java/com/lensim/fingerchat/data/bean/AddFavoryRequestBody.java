package com.lensim.fingerchat.data.bean;

/**
 * @time 2017/11/29 8:59
 * @class describe
 */

public class AddFavoryRequestBody  {

    private String creator;
    private String from;
    private String fromNickname;
    private String msgContent;
    private String msgId;
    private int msgType;
    private String provider;
    private String tags;

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getFromNickname() {
        return fromNickname;
    }

    public void setFromNickname(String fromNickname) {
        this.fromNickname = fromNickname;
    }

    public String getMsgContent() {
        return msgContent;
    }

    public void setMsgContent(String msgContent) {
        this.msgContent = msgContent;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public int getMsgType() {
        return msgType;
    }

    public void setMsgType(int msgType) {
        this.msgType = msgType;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    @Override
    public String toString() {
        return "AddFavoryRequestBody{" +
            "creator='" + creator + '\'' +
            ", from='" + from + '\'' +
            ", fromNickname='" + fromNickname + '\'' +
            ", msgContent='" + msgContent + '\'' +
            ", msgId='" + msgId + '\'' +
            ", msgType=" + msgType +
            ", provider='" + provider + '\'' +
            ", tags='" + tags + '\'' +
            '}';
    }
}
