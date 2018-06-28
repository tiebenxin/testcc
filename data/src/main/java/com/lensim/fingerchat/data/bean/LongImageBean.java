package com.lensim.fingerchat.data.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by LL130386 on 2017/11/16.
 */

public class LongImageBean implements Parcelable {

  String id;

  boolean isLongImage;

  public LongImageBean(){

  }


  public LongImageBean(Parcel in) {
    id = in.readString();
    isLongImage = in.readByte() != 0;
  }

  public static final Creator<LongImageBean> CREATOR = new Creator<LongImageBean>() {
    @Override
    public LongImageBean createFromParcel(Parcel in) {
      return new LongImageBean(in);
    }

    @Override
    public LongImageBean[] newArray(int size) {
      return new LongImageBean[size];
    }
  };

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public boolean isLongImage() {
    return isLongImage;
  }

  public void setLongImage(boolean longImage) {
    isLongImage = longImage;
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(id);
    dest.writeByte((byte) (isLongImage ? 1 : 0));
  }
}
