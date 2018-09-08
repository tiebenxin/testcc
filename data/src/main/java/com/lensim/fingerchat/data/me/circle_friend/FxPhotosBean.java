package com.lensim.fingerchat.data.me.circle_friend;

import java.io.Serializable;
import java.util.List;

/**
 * @time 2017/11/29 8:59
 * @class describe
 */

public class FxPhotosBean implements Serializable{
    private int photoId;
    private String photoSerno;
    private String photoCreator;
    private String photoFilenames;
    private int photoFileNum;
    private String photoUrl;
    private String photoContent;
    private int imageWidth;
    private int imageHeight;
    private int imageSize;
    private long createDatetime;
    private String userImage;
    private List<ThumbsBean> thumbsUps;
    private List<CommentBean> comments;

    public int getPhotoId() {
        return photoId;
    }

    public void setPhotoId(int photoId) {
        this.photoId = photoId;
    }

    public String getPhotoSerno() {
        return photoSerno;
    }

    public void setPhotoSerno(String photoSerno) {
        this.photoSerno = photoSerno;
    }

    public String getPhotoCreator() {
        return photoCreator;
    }

    public void setPhotoCreator(String photoCreator) {
        this.photoCreator = photoCreator;
    }

    public String getPhotoFilenames() {
        return photoFilenames;
    }

    public void setPhotoFilenames(String photoFilenames) {
        this.photoFilenames = photoFilenames;
    }

    public int getPhotoFileNum() {
        return photoFileNum;
    }

    public void setPhotoFileNum(int photoFileNum) {
        this.photoFileNum = photoFileNum;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getPhotoContent() {
        return photoContent;
    }

    public void setPhotoContent(String photoContent) {
        this.photoContent = photoContent;
    }

    public int getImageWidth() {
        return imageWidth;
    }

    public void setImageWidth(int imageWidth) {
        this.imageWidth = imageWidth;
    }

    public int getImageHeight() {
        return imageHeight;
    }

    public void setImageHeight(int imageHeight) {
        this.imageHeight = imageHeight;
    }

    public int getImageSize() {
        return imageSize;
    }

    public void setImageSize(int imageSize) {
        this.imageSize = imageSize;
    }

    public long getCreateDatetime() {
        return createDatetime;
    }

    public void setCreateDatetime(long createDatetime) {
        this.createDatetime = createDatetime;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public List<ThumbsBean> getThumbsUps() {
        return thumbsUps;
    }

    public void setThumbsUps(
        List<ThumbsBean> thumbsUps) {
        this.thumbsUps = thumbsUps;
    }

    public List<CommentBean> getComments() {
        return comments;
    }

    public void setComments(List<CommentBean> comments) {
        this.comments = comments;
    }
}
