package com.lensim.fingerchat.fingerchat.ui.settings.clear_cache;

import android.os.Parcel;
import android.os.Parcelable;


import com.lensim.fingerchat.commons.utils.StringUtils;

import java.io.File;

/**
 * Created by LY309313 on 2016/12/5.
 */

public class Cache implements Parcelable {

    private String uri;

    private File content;//所有的聊天文件
    private long size; //总大小
    private long date;
    private int type;

    private boolean checked;//是否被选中清除

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public File getContent() {
        return content;
    }

    public void setContent(File content) {
        this.content = content;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    @Override
    public String toString() {
        return "Cache{" +
                "content=" + content +
                ", size=" + size +
                ", date=" + date +
                ", type=" + type +
                ", checked=" + checked +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable(this.content);
        dest.writeLong(this.size);
        dest.writeLong(this.date);
        dest.writeInt(this.type);
        dest.writeByte(this.checked ? (byte) 1 : (byte) 0);
    }

    public Cache() {
    }


    @Override
    public boolean equals(Object obj) {
        if(obj==null || !(obj instanceof Cache)){
            return false;
        }
        String path = ((Cache) obj).getUri();
        if(StringUtils.isEmpty(path) || StringUtils.isEmpty(this.uri)){
            return false;
        }
        if(path.equals(this.uri)){
            return true;
        }
        return super.equals(obj);
    }

    protected Cache(Parcel in) {
        this.content = (File) in.readSerializable();
        this.size = in.readLong();
        this.date = in.readLong();
        this.type = in.readInt();
        this.checked = in.readByte() != 0;
    }

    public static final Creator<Cache> CREATOR = new Creator<Cache>() {
        @Override
        public Cache createFromParcel(Parcel source) {
            return new Cache(source);
        }

        @Override
        public Cache[] newArray(int size) {
            return new Cache[size];
        }
    };
}
