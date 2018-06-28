package com.lensim.fingerchat.data.me;

import android.os.Parcel;
import android.os.Parcelable;
import com.lensim.fingerchat.commons.utils.CyptoConvertUtils;


/**
 * Created by LY309313 on 2016/11/1.
 */

public class NewComment implements Parcelable {


    /**
     * PHO_Serno : 5ad0506296
     * PHC_CommentUserid : ly295189
     * PHC_CommentUsername : 胡琼华
     * PHC_Zambia : 0
     * PHC_Content : 好
     * PHC_CreateDT : 2016-09-13T14:16:33.76
     * USR_userimage : C:\HnlensWeb\HnlensImage\Users\ly309313\Avatar\headimage.png
     * PHO_ImagePath : C:\HnlensWeb\HnlensImage\Users\ly309313\photo\160913\
     * PHO_ImageName : 5ad0506296_1.png;5ad0506296_2.png;5ad0506296_3.png;
     * PHO_CreateUserID : ly309313
     * USR_Name : 大暗黑天
     */

    private String PHO_Serno;
    private String PHC_CommentUserid;
    private String PHC_CommentUsername;
    private String PHC_Zambia;
    private String PHC_Content;
    private String PHC_CreateDT;
    private String USR_userimage;
    private String PHO_ImagePath;
    private String PHO_ImageName;
    private String PHO_CreateUserID;
    private String PHO_CreateDT;
    private String PHO_Content;
    private String USR_Name;
    private int PHC_ID;
    private String PHC_Serno;
    private int PHO_ID;

    public String getPHO_Serno() {
        return PHO_Serno;
    }

    public void setPHO_Serno(String PHO_Serno) {
        this.PHO_Serno = PHO_Serno;
    }

    public String getPHC_CommentUserid() {
        return PHC_CommentUserid;
    }

    public void setPHC_CommentUserid(String PHC_CommentUserid) {
        this.PHC_CommentUserid = PHC_CommentUserid;
    }

    public String getPHO_CreateDT() {
        return PHO_CreateDT;
    }

    public void setPHO_CreateDT(String PHO_CreateDT) {
        this.PHO_CreateDT = PHO_CreateDT;
    }

    public String getPHO_Content() {
        return PHO_Content;
    }

    public void setPHO_Content(String PHO_Content) {
        this.PHO_Content = PHO_Content;
    }

    public String getPHC_CommentUsername() {
        return CyptoConvertUtils.decryptString(PHC_CommentUsername);
    }

    public void setPHC_CommentUsername(String PHC_CommentUsername) {
        this.PHC_CommentUsername = PHC_CommentUsername;
    }

    public String getPHC_Zambia() {
        return PHC_Zambia;
    }

    public void setPHC_Zambia(String PHC_Zambia) {
        this.PHC_Zambia = PHC_Zambia;
    }

    public String getPHC_Content() {
        return PHC_Content;
    }

    public void setPHC_Content(String PHC_Content) {
        this.PHC_Content = PHC_Content;
    }

    public String getPHC_CreateDT() {
        return PHC_CreateDT;
    }

    public void setPHC_CreateDT(String PHC_CreateDT) {
        this.PHC_CreateDT = PHC_CreateDT;
    }

    public String getUSR_userimage() {
        return USR_userimage;
    }

    public void setUSR_userimage(String USR_userimage) {
        this.USR_userimage = USR_userimage;
    }

    public String getPHO_ImagePath() {
        return PHO_ImagePath;
    }

    public void setPHO_ImagePath(String PHO_ImagePath) {
        this.PHO_ImagePath = PHO_ImagePath;
    }

    public String getPHO_ImageName() {
        return PHO_ImageName;
    }

    public void setPHO_ImageName(String PHO_ImageName) {
        this.PHO_ImageName = PHO_ImageName;
    }

    public String getPHO_CreateUserID() {
        return PHO_CreateUserID;
    }

    public void setPHO_CreateUserID(String PHO_CreateUserID) {
        this.PHO_CreateUserID = PHO_CreateUserID;
    }

    public String getUSR_Name() {
        return USR_Name;
    }

    public void setUSR_Name(String USR_Name) {
        this.USR_Name = USR_Name;
    }

    public int getPHC_ID() {
        return PHC_ID;
    }

    public void setPHC_ID(int PHC_ID) {
        this.PHC_ID = PHC_ID;
    }

    public String getPHC_Serno() {
        return PHC_Serno;
    }

    public void setPHC_Serno(String PHC_Serno) {
        this.PHC_Serno = PHC_Serno;
    }

    public int getPHO_ID() {
        return PHO_ID;
    }

    public void setPHO_ID(int PHO_ID) {
        this.PHO_ID = PHO_ID;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.PHO_Serno);
        dest.writeString(this.PHC_CommentUserid);
        dest.writeString(this.PHC_CommentUsername);
        dest.writeString(this.PHC_Zambia);
        dest.writeString(this.PHC_Content);
        dest.writeString(this.PHC_CreateDT);
        dest.writeString(this.USR_userimage);
        dest.writeString(this.PHO_ImagePath);
        dest.writeString(this.PHO_ImageName);
        dest.writeString(this.PHO_CreateUserID);
        dest.writeString(this.PHO_CreateDT);
        dest.writeString(this.PHO_Content);
        dest.writeString(this.USR_Name);
        dest.writeInt(this.PHC_ID);
        dest.writeString(this.PHC_Serno);
        dest.writeInt(this.PHO_ID);
    }

    public NewComment() {
    }

    protected NewComment(Parcel in) {
        this.PHO_Serno = in.readString();
        this.PHC_CommentUserid = in.readString();
        this.PHC_CommentUsername = in.readString();
        this.PHC_Zambia = in.readString();
        this.PHC_Content = in.readString();
        this.PHC_CreateDT = in.readString();
        this.USR_userimage = in.readString();
        this.PHO_ImagePath = in.readString();
        this.PHO_ImageName = in.readString();
        this.PHO_CreateUserID = in.readString();
        this.PHO_CreateDT = in.readString();
        this.PHO_Content = in.readString();
        this.USR_Name = in.readString();
        this.PHC_ID = in.readInt();
        this.PHC_Serno = in.readString();
        this.PHO_ID = in.readInt();
    }

    public static final Creator<NewComment> CREATOR = new Creator<NewComment>() {
        @Override
        public NewComment createFromParcel(Parcel source) {
            return new NewComment(source);
        }

        @Override
        public NewComment[] newArray(int size) {
            return new NewComment[size];
        }
    };
}
