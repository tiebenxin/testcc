package com.lensim.fingerchat.data.bean;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

/**
 * 图片实体
 */
public class ImageBean implements Parcelable, Comparable<ImageBean> {

  public String path;
  public String name;
  public long time;
  public String size;

  public ImageBean(String path, String name, long time,String size) {
    this.path = path;
    this.name = name;
    this.time = time;
    this.size = size;

  }

  @Override
  public boolean equals(Object o) {
    try {
      ImageBean other = (ImageBean) o;
      return this.path.equalsIgnoreCase(other.path);
    } catch (ClassCastException e) {
      e.printStackTrace();
    }
    return super.equals(o);
  }


  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(this.path);
    dest.writeString(this.name);
    dest.writeLong(this.time);
    dest.writeString(this.size);
  }

  protected ImageBean(Parcel in) {
    this.path = in.readString();
    this.name = in.readString();
    this.time = in.readLong();
    this.size = in.readString();
  }

  public static final Creator<ImageBean> CREATOR = new Creator<ImageBean>() {
    @Override
    public ImageBean createFromParcel(Parcel source) {
      return new ImageBean(source);
    }

    @Override
    public ImageBean[] newArray(int size) {
      return new ImageBean[size];
    }
  };

  @Override
  public int compareTo(@NonNull ImageBean o) {
    if (this.time > o.time) {
      return -1;
    } else if (this.time == o.time) {
      return 0;
    } else {
      return 1;
    }
  }
}
