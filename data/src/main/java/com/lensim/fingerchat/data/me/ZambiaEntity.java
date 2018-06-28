package com.lensim.fingerchat.data.me;


import android.os.Parcel;

/**
 * Created by LY309313 on 2016/8/1.
 *
 */

public class ZambiaEntity implements android.os.Parcelable {

    /**
     * PHC_CommentUserid : ly444
     * PHC_CommentUsername : 测试
     */


    /**
     * 用户名
     */
    public String PHC_CommentUserid;
    /**
     * 用户昵称
     */
    public String PHC_CommentUsername;


    public ZambiaEntity() {}

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.PHC_CommentUserid);
        dest.writeString(this.PHC_CommentUsername);
    }

    protected ZambiaEntity(Parcel in) {
        this.PHC_CommentUserid = in.readString();
        this.PHC_CommentUsername = in.readString();
    }

    public static final Creator<ZambiaEntity> CREATOR = new Creator<ZambiaEntity>() {
        @Override
        public ZambiaEntity createFromParcel(Parcel source) {
            return new ZambiaEntity(source);
        }

        @Override
        public ZambiaEntity[] newArray(int size) {
            return new ZambiaEntity[size];
        }
    };
}
