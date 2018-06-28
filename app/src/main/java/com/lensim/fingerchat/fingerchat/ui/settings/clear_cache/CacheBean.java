package com.lensim.fingerchat.fingerchat.ui.settings.clear_cache;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LY309313 on 2016/12/5.
 */

public class CacheBean implements Parcelable {
    private String jid;
    private boolean checked;
    private long totalSize;
    private List<Cache> caches;

    public long getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(long totalSize) {
        this.totalSize = totalSize;
    }

    public String getJid() {
        return jid;
    }

    public void setJid(String jid) {
        this.jid = jid;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public List<Cache> getCaches() {
        return caches;
    }

    public void setCaches(List<Cache> caches) {
        this.caches = caches;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.jid);
        dest.writeByte(this.checked ? (byte) 1 : (byte) 0);
        dest.writeLong(this.totalSize);
        dest.writeList(this.caches);
    }

    public CacheBean() {
    }

    protected CacheBean(Parcel in) {
        this.jid = in.readString();
        this.checked = in.readByte() != 0;
        this.totalSize = in.readLong();
        this.caches = new ArrayList<Cache>();
        in.readList(this.caches, Cache.class.getClassLoader());
    }

    public static final Creator<CacheBean> CREATOR = new Creator<CacheBean>() {
        @Override
        public CacheBean createFromParcel(Parcel source) {
            return new CacheBean(source);
        }

        @Override
        public CacheBean[] newArray(int size) {
            return new CacheBean[size];
        }
    };
}
