package com.lensim.fingerchat.fingerchat.model.bean;

import android.os.Parcel;
import android.os.Parcelable;
import com.lensim.fingerchat.fingerchat.ui.me.utils.DatasUtil;
import java.util.ArrayList;
import java.util.List;

/**
 * 朋友圈说说实体类
 * Created by zm on 2018/7/5.
 */
public class PhotoBean implements Parcelable {

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
    private String userName;
    private String userImage;
    private long createDatetime;
    private List<ThumbsBean> thumbsUps;
    private List<CommentBean> comments;

    public PhotoBean() {
    }

    protected PhotoBean(Parcel in) {
        photoId = in.readInt();
        photoSerno = in.readString();
        photoCreator = in.readString();
        photoFilenames = in.readString();
        photoFileNum = in.readInt();
        photoUrl = in.readString();
        photoContent = in.readString();
        imageWidth = in.readInt();
        imageHeight = in.readInt();
        imageSize = in.readInt();
        userName = in.readString();
        userImage = in.readString();
        createDatetime = in.readLong();
    }

    public static final Creator<PhotoBean> CREATOR = new Creator<PhotoBean>() {
        @Override
        public PhotoBean createFromParcel(Parcel in) {
            return new PhotoBean(in);
        }

        @Override
        public PhotoBean[] newArray(int size) {
            return new PhotoBean[size];
        }
    };

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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public long getCreateDatetime() {
        return createDatetime;
    }

    public void setCreateDatetime(long createDatetime) {
        this.createDatetime = createDatetime;
    }

    public List<ThumbsBean> getThumbsUps() {
        return thumbsUps == null ? new ArrayList<>() : thumbsUps;
    }

    public void setThumbsUps(List<ThumbsBean> thumbsUps) {
        this.thumbsUps = thumbsUps;
    }

    public List<CommentBean> getComments() {
        return comments == null ? new ArrayList<>() : comments;
    }

    public void setComments(List<CommentBean> comments) {
        this.comments = comments;
    }

    public int getType() {
        return DatasUtil.getPhotoType(photoUrl);
    }

    public String getVideoUrl() {
        return DatasUtil.getVideoUrl(photoUrl);
    }

    public List<String> getImageUrls() {
        return DatasUtil.getImageUrls(photoUrl);
    }

    public String getVideoThumbnail() {
        return DatasUtil.getVideoThumbnail(photoUrl);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(photoId);
        dest.writeString(photoSerno);
        dest.writeString(photoCreator);
        dest.writeString(photoFilenames);
        dest.writeInt(photoFileNum);
        dest.writeString(photoUrl);
        dest.writeString(photoContent);
        dest.writeInt(imageWidth);
        dest.writeInt(imageHeight);
        dest.writeInt(imageSize);
        dest.writeString(userName);
        dest.writeString(userImage);
        dest.writeLong(createDatetime);
    }

    @Override
    public String toString() {
        return "PhotoBean{" +
            "photoId=" + photoId +
            ", photoSerno='" + photoSerno + '\'' +
            ", photoCreator='" + photoCreator + '\'' +
            ", photoFilenames='" + photoFilenames + '\'' +
            ", photoFileNum=" + photoFileNum +
            ", photoUrl='" + photoUrl + '\'' +
            ", photoContent='" + photoContent + '\'' +
            ", imageWidth=" + imageWidth +
            ", imageHeight=" + imageHeight +
            ", imageSize=" + imageSize +
            ", userName='" + userName + '\'' +
            ", userImage='" + userImage + '\'' +
            ", createDatetime=" + createDatetime +
            ", thumbsUps=" + thumbsUps +
            ", comments=" + comments +
            '}';
    }
}
