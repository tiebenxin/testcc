package com.lens.chatmodel.bean;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by LY309313 on 2017/2/23.
 */

public class AllResult implements Parcelable {

    private int key;

    private List<SearchMessageBean> results;

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public List<SearchMessageBean> getResults() {
        return results;
    }

    public void setResults(List<SearchMessageBean> results) {
        this.results = results;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.key);
        dest.writeList(this.results);
    }

    public AllResult() {
    }

    protected AllResult(Parcel in) {
        this.key = in.readInt();
        this.results = new ArrayList<SearchMessageBean>();
        in.readList(this.results, SearchMessageBean.class.getClassLoader());
    }

    public static final Creator<AllResult> CREATOR = new Creator<AllResult>() {
        @Override
        public AllResult createFromParcel(Parcel source) {
            return new AllResult(source);
        }

        @Override
        public AllResult[] newArray(int size) {
            return new AllResult[size];
        }
    };
}
