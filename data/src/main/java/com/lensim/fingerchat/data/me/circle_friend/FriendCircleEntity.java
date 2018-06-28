package com.lensim.fingerchat.data.me.circle_friend;

import android.os.Parcel;
import android.os.Parcelable;


/**
 * Created by LY309313 on 2016/8/1.
 *
 */

public class FriendCircleEntity implements Parcelable {

    /**
     * PHO_Serno : c9e14657ca
     * PHO_CreateUserID : ly295189
     * PHO_CreateDT : 2016/7/14 16:15:47
     * PHO_ImageName : c9e14657ca_1.png;
     * PHO_ImageNUM : 1
     * PHO_ImagePath : C:\HnlensWeb\HnlensImage\Users\ly295189\photo\160714\
     * PHO_Content : 测试一下??????
     * USR_Name : 胡琼华
     * USR_UserImage : C:\HnlensWeb\HnlensImage\Users\ly295189\Avatar\headimage.png
     * Zambia : //为一个数据集合
     * Content : // 为一个数据集合
     */

    private String PHO_Serno;
    private String PHO_CreateUserID;
    private String PHO_CreateDT;
    private String PHO_ImageName;
    private String PHO_ImageNUM;
    private String PHO_ImagePath;
    private String PHO_Content;
    private String USR_Name;
    private String USR_UserImage;
    private String Zambia;
    private String Content;

    public String getPHO_Serno() {
        return PHO_Serno;
    }

    public void setPHO_Serno(String PHO_Serno) {
        this.PHO_Serno = PHO_Serno;
    }

    public String getPHO_CreateUserID() {
        return PHO_CreateUserID;
    }

    public void setPHO_CreateUserID(String PHO_CreateUserID) {
        this.PHO_CreateUserID = PHO_CreateUserID;
    }

    public String getPHO_CreateDT() {
        return PHO_CreateDT;
    }

    public void setPHO_CreateDT(String PHO_CreateDT) {
        this.PHO_CreateDT = PHO_CreateDT;
    }

    public String getPHO_ImageName() {
        return PHO_ImageName;
    }

    public void setPHO_ImageName(String PHO_ImageName) {
        this.PHO_ImageName = PHO_ImageName;
    }

    public String getPHO_ImageNUM() {
        return PHO_ImageNUM;
    }

    public void setPHO_ImageNUM(String PHO_ImageNUM) {
        this.PHO_ImageNUM = PHO_ImageNUM;
    }

    public String getPHO_ImagePath() {
        return PHO_ImagePath;
    }

    public void setPHO_ImagePath(String PHO_ImagePath) {
        this.PHO_ImagePath = PHO_ImagePath;
    }

    public String getPHO_Content() {
        return PHO_Content;
    }

    public void setPHO_Content(String PHO_Content) {
        this.PHO_Content = PHO_Content;
    }

    public String getUSR_Name() {
        return USR_Name;
    }

    public void setUSR_Name(String USR_Name) {
        this.USR_Name = USR_Name;
    }

    public String getUSR_UserImage() {
        return USR_UserImage;
    }

    public void setUSR_UserImage(String USR_UserImage) {
        this.USR_UserImage = USR_UserImage;
    }

    public String getZambia() {
        return Zambia;
    }

    public void setZambia(String Zambia) {
        this.Zambia = Zambia;
    }

    public String getContent() {
        return Content;
    }

    public void setContent(String Content) {
        this.Content = Content;
    }

    @Override
    public String toString() {
        return "FriendCircleEntity{" +
                "PHO_Serno='" + PHO_Serno + '\'' +
                ", PHO_CreateUserID='" + PHO_CreateUserID + '\'' +
                ", PHO_CreateDT='" + PHO_CreateDT + '\'' +
                ", PHO_ImageName='" + PHO_ImageName + '\'' +
                ", PHO_ImageNUM='" + PHO_ImageNUM + '\'' +
                ", PHO_ImagePath='" + PHO_ImagePath + '\'' +
                ", PHO_Content='" + PHO_Content + '\'' +
                ", USR_Name='" + USR_Name + '\'' +
                ", USR_UserImage='" + USR_UserImage + '\'' +
                ", Zambia='" + Zambia + '\'' +
                ", Content='" + Content + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.PHO_Serno);
        dest.writeString(this.PHO_CreateUserID);
        dest.writeString(this.PHO_CreateDT);
        dest.writeString(this.PHO_ImageName);
        dest.writeString(this.PHO_ImageNUM);
        dest.writeString(this.PHO_ImagePath);
        dest.writeString(this.PHO_Content);
        dest.writeString(this.USR_Name);
        dest.writeString(this.USR_UserImage);
        dest.writeString(this.Zambia);
        dest.writeString(this.Content);
    }

    public FriendCircleEntity() {
    }

    protected FriendCircleEntity(Parcel in) {
        this.PHO_Serno = in.readString();
        this.PHO_CreateUserID = in.readString();
        this.PHO_CreateDT = in.readString();
        this.PHO_ImageName = in.readString();
        this.PHO_ImageNUM = in.readString();
        this.PHO_ImagePath = in.readString();
        this.PHO_Content = in.readString();
        this.USR_Name = in.readString();
        this.USR_UserImage = in.readString();
        this.Zambia = in.readString();
        this.Content = in.readString();
    }

    public static final Creator<FriendCircleEntity> CREATOR = new Creator<FriendCircleEntity>() {
        @Override
        public FriendCircleEntity createFromParcel(Parcel source) {
            return new FriendCircleEntity(source);
        }

        @Override
        public FriendCircleEntity[] newArray(int size) {
            return new FriendCircleEntity[size];
        }
    };
}
