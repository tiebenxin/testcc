package com.lensim.fingerchat.commons.map.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class LocationBean implements Parcelable {

  public static final Creator<LocationBean> CREATOR = new Creator<LocationBean>() {
    @Override
    public LocationBean createFromParcel(Parcel source) {
      return new LocationBean(source);
    }

    @Override
    public LocationBean[] newArray(int size) {
      return new LocationBean[size];
    }
  };
  private String locName;// 地名
  private String province;// 省名
  private String city;// 城市
  private String district;// 区名
  private String street;// 街道
  private String streetNum;// 街道號
  private Double latitude;// 纬度
  private Double longitude;// 经度
  private String time;
  private int locType;
  private float radius;
  // gps才有的
  private float speed;
  private int satellite;
  private float direction;
  // wifi才有的
  private String addStr;// 具体地址
  private int operationers;
  // 用户信息的
  private String userId;
  private String userName;
  private String userAvator;
  // 额外输入的详细地名
  private String detailAddInput;
  private String uid;

  public LocationBean() {
  }

  protected LocationBean(Parcel in) {
    this.locName = in.readString();
    this.province = in.readString();
    this.city = in.readString();
    this.district = in.readString();
    this.street = in.readString();
    this.streetNum = in.readString();
    this.latitude = (Double) in.readValue(Double.class.getClassLoader());
    this.longitude = (Double) in.readValue(Double.class.getClassLoader());
    this.time = in.readString();
    this.locType = in.readInt();
    this.radius = in.readFloat();
    this.speed = in.readFloat();
    this.satellite = in.readInt();
    this.direction = in.readFloat();
    this.addStr = in.readString();
    this.operationers = in.readInt();
    this.userId = in.readString();
    this.userName = in.readString();
    this.userAvator = in.readString();
    this.detailAddInput = in.readString();
    this.uid = in.readString();
  }

  public String getLocName() {
    return locName;
  }

  public void setLocName(String locName) {
    this.locName = locName;
  }

  public String getProvince() {
    return province;
  }

  public void setProvince(String province) {
    this.province = province;
  }

  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public String getDistrict() {
    return district;
  }

  public void setDistrict(String district) {
    this.district = district;
  }

  public String getStreet() {
    return street;
  }

  public void setStreet(String street) {
    this.street = street;
  }

  public String getStreetNum() {
    return streetNum;
  }

  public void setStreetNum(String streetNum) {
    this.streetNum = streetNum;
  }

  public Double getLatitude() {
    return latitude;
  }

  public void setLatitude(Double latitude) {
    this.latitude = latitude;
  }

  public Double getLongitude() {
    return longitude;
  }

  public void setLongitude(Double longitude) {
    this.longitude = longitude;
  }

  public String getTime() {
    return time;
  }

  public void setTime(String time) {
    this.time = time;
  }

  public int getLocType() {
    return locType;
  }

  public void setLocType(int locType) {
    this.locType = locType;
  }

  public float getRadius() {
    return radius;
  }

  public void setRadius(float radius) {
    this.radius = radius;
  }

  public float getSpeed() {
    return speed;
  }

  public void setSpeed(float speed) {
    this.speed = speed;
  }

  public int getSatellite() {
    return satellite;
  }

  public void setSatellite(int satellite) {
    this.satellite = satellite;
  }

  public float getDirection() {
    return direction;
  }

  public void setDirection(float direction) {
    this.direction = direction;
  }

  public String getAddStr() {
    return addStr;
  }

  public void setAddStr(String addStr) {
    this.addStr = addStr;
  }

  public int getOperationers() {
    return operationers;
  }

  public void setOperationers(int operationers) {
    this.operationers = operationers;
  }

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

  public String getUserAvator() {
    return userAvator;
  }

  public void setUserAvator(String userAvator) {
    this.userAvator = userAvator;
  }

  public String getDetailAddInput() {
    return detailAddInput;
  }

  public void setDetailAddInput(String detailAddInput) {
    this.detailAddInput = detailAddInput;
  }

  public String getUid() {
    return uid;
  }

  public void setUid(String uid) {
    this.uid = uid;
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(this.locName);
    dest.writeString(this.province);
    dest.writeString(this.city);
    dest.writeString(this.district);
    dest.writeString(this.street);
    dest.writeString(this.streetNum);
    dest.writeValue(this.latitude);
    dest.writeValue(this.longitude);
    dest.writeString(this.time);
    dest.writeInt(this.locType);
    dest.writeFloat(this.radius);
    dest.writeFloat(this.speed);
    dest.writeInt(this.satellite);
    dest.writeFloat(this.direction);
    dest.writeString(this.addStr);
    dest.writeInt(this.operationers);
    dest.writeString(this.userId);
    dest.writeString(this.userName);
    dest.writeString(this.userAvator);
    dest.writeString(this.detailAddInput);
    dest.writeString(this.uid);
  }
}
