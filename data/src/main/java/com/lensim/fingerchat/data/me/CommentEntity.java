package com.lensim.fingerchat.data.me;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * date on 2018/3/30
 * author ll147996
 * describe
 */
public class CommentEntity implements Parcelable {


    /**
     * PHC_CommentUserid : ly295189
     * PHC_CommentUsername : 胡琼华
     * PHC_Content : 谢谢亲的评论！
     * PHC_SecondUserid : ly444
     * PHC_SecondUsername : 测试
     */

    private String PHC_CommentUserid;
    private String PHC_CommentUsername;
    private String PHC_Content;
    private String PHC_SecondUserid;
    private String PHC_SecondUsername;
    private String PHC_Serno;
    private String PHC_Zambia;
    private String PHO_Serno;

    public String getPHC_CommentUserid() {
        return PHC_CommentUserid;
    }

    public void setPHC_CommentUserid(String PHC_CommentUserid) {
        this.PHC_CommentUserid = PHC_CommentUserid;
    }

    public String getPHC_CommentUsername() {
        return PHC_CommentUsername;
    }

    public void setPHC_CommentUsername(String PHC_CommentUsername) {
        this.PHC_CommentUsername = PHC_CommentUsername;
    }

    public String getPHC_Content() {
        return PHC_Content;
    }

    public void setPHC_Content(String PHC_Content) {
        this.PHC_Content = PHC_Content;
    }

    public String getPHC_SecondUserid() {
        return PHC_SecondUserid;
    }

    public void setPHC_SecondUserid(String PHC_SecondUserid) {
        this.PHC_SecondUserid = PHC_SecondUserid;
    }

    public String getPHC_SecondUsername() {
        return PHC_SecondUsername;
    }

    public void setPHC_SecondUsername(String PHC_SecondUsername) {
        this.PHC_SecondUsername = PHC_SecondUsername;
    }

    public String getPHC_Zambia() {
        return PHC_Zambia;
    }

    public void setPHC_Zambia(String PHC_Zambia) {
        this.PHC_Zambia = PHC_Zambia;
    }

    public String getPHO_Serno() {
        return PHO_Serno;
    }

    public void setPHO_Serno(String PHO_Serno) {
        this.PHO_Serno = PHO_Serno;
    }

    public String getPHC_Serno() {
        return PHC_Serno;
    }

    public void setPHC_Serno(String PHC_Serno) {
        this.PHC_Serno = PHC_Serno;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.PHC_CommentUserid);
        dest.writeString(this.PHC_CommentUsername);
        dest.writeString(this.PHC_Content);
        dest.writeString(this.PHC_SecondUserid);
        dest.writeString(this.PHC_SecondUsername);
        dest.writeString(this.PHC_Serno);
        dest.writeString(this.PHC_Zambia);
        dest.writeString(this.PHO_Serno);
    }

    public CommentEntity() {
    }

    protected CommentEntity(Parcel in) {
        this.PHC_CommentUserid = in.readString();
        this.PHC_CommentUsername = in.readString();
        this.PHC_Content = in.readString();
        this.PHC_SecondUserid = in.readString();
        this.PHC_SecondUsername = in.readString();
        this.PHC_Serno = in.readString();
        this.PHC_Zambia = in.readString();
        this.PHO_Serno = in.readString();
    }

    public static final Creator<CommentEntity> CREATOR = new Creator<CommentEntity>() {
        @Override
        public CommentEntity createFromParcel(Parcel source) {
            return new CommentEntity(source);
        }

        @Override
        public CommentEntity[] newArray(int size) {
            return new CommentEntity[size];
        }
    };

}
