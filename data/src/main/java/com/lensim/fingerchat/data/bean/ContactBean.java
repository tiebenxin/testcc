package com.lensim.fingerchat.data.bean;

/**
 * Created by LL130386 on 2017/11/30.
 */

public class ContactBean {
  String name;
  String userId;
  String avatarUrl;
  String department;
  boolean isIdentify;
  String company;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public String getAvatarUrl() {
    return avatarUrl;
  }

  public void setAvatarUrl(String avatarUrl) {
    this.avatarUrl = avatarUrl;
  }

  public String getDepartment() {
    return department;
  }

  public void setDepartment(String department) {
    this.department = department;
  }

  public boolean isIdentify() {
    return isIdentify;
  }

  public void setIdentify(boolean identify) {
    isIdentify = identify;
  }

  public String getCompany() {
    return company;
  }

  public void setCompany(String company) {
    this.company = company;
  }
}
