package com.lensim.fingerchat.fingerchat.model.bean;

/**
 * 朋友圈点赞实体类
 */
public class ThumbsBean {

    private Long tId;
    private String thumbsSerno;
    private String photoSerno;
    private String photoUserId;
    private String photoUserName;
    private String thumbsUserId;
    private String thumbsUserName;
    private long thumbsTime;

    public Long gettId() {
        return tId;
    }

    public void settId(Long tId) {
        this.tId = tId;
    }

    public String getThumbsSerno() {
        return thumbsSerno;
    }

    public void setThumbsSerno(String thumbsSerno) {
        this.thumbsSerno = thumbsSerno;
    }

    public String getPhotoSerno() {
        return photoSerno;
    }

    public void setPhotoSerno(String photoSerno) {
        this.photoSerno = photoSerno;
    }

    public String getPhotoUserId() {
        return photoUserId;
    }

    public void setPhotoUserId(String photoUserId) {
        this.photoUserId = photoUserId;
    }

    public String getPhotoUserName() {
        return photoUserName;
    }

    public void setPhotoUserName(String photoUserName) {
        this.photoUserName = photoUserName;
    }

    public String getThumbsUserId() {
        return thumbsUserId;
    }

    public void setThumbsUserId(String thumbsUserId) {
        this.thumbsUserId = thumbsUserId;
    }

    public String getThumbsUserName() {
        return thumbsUserName;
    }

    public void setThumbsUserName(String thumbsUserName) {
        this.thumbsUserName = thumbsUserName;
    }

    public long getThumbsTime() {
        return thumbsTime;
    }

    public void setThumbsTime(long thumbsTime) {
        this.thumbsTime = thumbsTime;
    }
}
