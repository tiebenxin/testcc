package com.lens.chatmodel.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by LL130386 on 2018/3/5.
 * 搜索本地消息数据bean
 */

public class SearchMessageBean implements Parcelable {

    String userId;
    String userName;
    String nick;
    String message;
    String alpha;//首字母，索引
    boolean isGroupChat;
    private int count;

    public SearchMessageBean() {
    }


    protected SearchMessageBean(Parcel in) {
        userId = in.readString();
        userName = in.readString();
        nick = in.readString();
        message = in.readString();
        alpha = in.readString();
        isGroupChat = in.readByte() != 0;
        count = in.readInt();

    }

    public static final Creator<SearchMessageBean> CREATOR = new Creator<SearchMessageBean>() {
        @Override
        public SearchMessageBean createFromParcel(Parcel in) {
            return new SearchMessageBean(in);
        }

        @Override
        public SearchMessageBean[] newArray(int size) {
            return new SearchMessageBean[size];
        }
    };

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getAlpha() {
        return alpha;
    }

    public void setAlpha(String alpha) {
        this.alpha = alpha;
    }

    public boolean isGroupChat() {
        return isGroupChat;
    }

    public void setGroupChat(boolean groupChat) {
        isGroupChat = groupChat;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userId);
        dest.writeString(userName);
        dest.writeString(nick);
        dest.writeString(message);
        dest.writeString(alpha);
        dest.writeByte((byte) (isGroupChat ? 1 : 0));
        dest.writeInt(count);
    }
}
