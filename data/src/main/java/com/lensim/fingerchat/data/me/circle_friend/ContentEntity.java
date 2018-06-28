package com.lensim.fingerchat.data.me.circle_friend;

import android.os.Parcel;
import android.os.Parcelable;
import com.lensim.fingerchat.commons.utils.CyptoConvertUtils;


/**
 * Created by LY309313 on 2016/8/1.
 *
 */

public class ContentEntity implements Parcelable {


    /**
     * PHC_CommentUserid : ly295189
     * PHC_CommentUsername : 胡琼华
     * PHC_Content : 谢谢亲的评论！
     * PHC_SecondUserid : ly444
     * PHC_SecondUsername : 测试
     */

    private String PHC_CommentUserid;//用户名
    private String PHC_CommentUsername;//昵称
    private String PHC_Content;
    private String PHC_SecondUserid;
    private String PHC_SecondUsername;
    private String PHC_Serno;

    public String getPHC_Serno() {
        return PHC_Serno;
    }

    public void setPHC_Serno(String PHC_Serno) {
        this.PHC_Serno = PHC_Serno;
    }

    public String getPHC_CommentUserid() {
        return PHC_CommentUserid;
    }

    public void setPHC_CommentUserid(String PHC_CommentUserid) {
        this.PHC_CommentUserid = PHC_CommentUserid;
    }

    public String getPHC_CommentUsername() {
        return  CyptoConvertUtils.decryptString(PHC_CommentUsername);
    }

    public void setPHC_CommentUsername(String PHC_CommentUsername) {
        this.PHC_CommentUsername = PHC_CommentUsername;
    }

    public String getPHC_Content() {
        return CyptoConvertUtils.decryptString(PHC_Content);
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
        return   CyptoConvertUtils.decryptString(PHC_SecondUsername);
    }

    public void setPHC_SecondUsername(String PHC_SecondUsername) {
        this.PHC_SecondUsername = PHC_SecondUsername;
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
    }

    public ContentEntity() {
    }

    protected ContentEntity(Parcel in) {
        this.PHC_CommentUserid = in.readString();
        this.PHC_CommentUsername = in.readString();
        this.PHC_Content = in.readString();
        this.PHC_SecondUserid = in.readString();
        this.PHC_SecondUsername = in.readString();
        this.PHC_Serno = in.readString();
    }

    public static final Creator<ContentEntity> CREATOR = new Creator<ContentEntity>() {
        @Override
        public ContentEntity createFromParcel(Parcel source) {
            return new ContentEntity(source);
        }

        @Override
        public ContentEntity[] newArray(int size) {
            return new ContentEntity[size];
        }
    };
}
