package com.lens.chatmodel.bean;

import android.os.Parcel;
import android.os.Parcelable;
import com.lens.chatmodel.ChatEnum.ESureType;
import com.lensim.fingerchat.commons.interf.IChatUser;

/**
 * Created by LL130386 on 2018/5/8.
 * 获取用户信息数据bean
 */

public class UserInfoBean implements IChatUser, Parcelable {

    private String company;
    private String jobName;//职称:工程师
    private String jobNo;
    private String userId;
    private String nickname;
    private String employeeNo;
    private String empSexName;
    private String userImage;
    private int isEnabled;
    private int isValid;

    private int bgId;
    private int newStatus;
    private long time;
    private int relationStatus = -1;
    private int hasReaded;
    private String remarkName;

    protected UserInfoBean(Parcel in) {
        company = in.readString();
        jobName = in.readString();
        jobNo = in.readString();
        userId = in.readString();
        nickname = in.readString();
        employeeNo = in.readString();
        empSexName = in.readString();
        userImage = in.readString();
        isEnabled = in.readInt();
        isValid = in.readInt();

        bgId = in.readInt();
        newStatus = in.readInt();
        time = in.readLong();
        relationStatus = in.readInt();
        hasReaded = in.readInt();
        remarkName = in.readString();
    }

    public static final Creator<UserInfoBean> CREATOR = new Creator<UserInfoBean>() {
        @Override
        public UserInfoBean createFromParcel(Parcel in) {
            return new UserInfoBean(in);
        }

        @Override
        public UserInfoBean[] newArray(int size) {
            return new UserInfoBean[size];
        }
    };

    @Override
    public void setUserId(String account) {
        userId = account;
    }

    @Override
    public String getUserId() {
        return userId;
    }

    @Override
    public void setUserNick(String nick) {
        nickname = nick;
    }

    @Override
    public String getUserNick() {
        return nickname;
    }

    @Override
    public void setWorkAddress(String address) {
        company = address;
    }

    @Override
    public String getWorkAddress() {
        return company;
    }

    @Override
    public void setGroup(String group) {

    }

    @Override
    public String getGroup() {
        return null;
    }

    @Override
    public void setEmpName(String name) {

    }

    @Override
    public String getEmpName() {
        return null;
    }

    @Override
    public void setRemarkName(String name) {
        remarkName = name;
    }

    @Override
    public String getRemarkName() {
        return remarkName;
    }

    @Override
    public void setSex(String sex) {
        empSexName = sex;
    }

    @Override
    public String getSex() {
        return empSexName;
    }

    @Override
    public void setAvatarUrl(String image) {
        userImage = image;
    }

    @Override
    public String getAvatarUrl() {
        return userImage;
    }

    @Override
    public void setValid(int isValid) {
        this.isValid = isValid;
    }

    @Override
    public boolean isValid() {
        return isValid == ESureType.YES.ordinal();
    }

    @Override
    public int getValid() {
        return isValid;
    }

    @Override
    public void setJobName(String job) {
        jobName = job;
    }

    @Override
    public String getJobName() {
        return jobName;
    }

    @Override
    public void setDptNo(String num) {

    }

    @Override
    public String getDptNo() {
        return null;
    }

    @Override
    public void setDptNname(String name) {

    }

    @Override
    public String getDptName() {
        return null;
    }

    @Override
    public void setEmpNo(String empNo) {

    }

    @Override
    public String getEmpNo() {
        return null;
    }

    @Override
    public void setStar(int star) {

    }

    @Override
    public boolean isStar() {
        return false;
    }

    @Override
    public void setState(int state) {

    }

    @Override
    public int getState() {
        return 0;
    }

    @Override
    public void setBlock(int block) {

    }

    @Override
    public boolean isBlock() {
        return false;
    }

    @Override
    public int getBlock() {
        return 0;
    }

    @Override
    public void setQuit(int quit) {

    }

    @Override
    public boolean isQuit() {
        return false;
    }

    @Override
    public int getQuit() {
        return 0;
    }

    @Override
    public int getChatType() {
        return 0;
    }

    @Override
    public int getBgId() {
        return bgId;
    }

    @Override
    public void setBgId(int id) {
        bgId = id;
    }

    @Override
    public String getFirstChar() {
        return null;
    }

    @Override
    public String getPinYin() {
        return null;
    }

    @Override
    public int getRelationStatus() {
        return relationStatus;
    }

    @Override
    public long getTime() {
        return 0;
    }

    @Override
    public int hasReaded() {
        return hasReaded;
    }

    @Override
    public int getNewStatus() {
        return newStatus;
    }

    @Override
    public int getStar() {
        return 0;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(company);
        dest.writeString(jobName);
        dest.writeString(jobNo);
        dest.writeString(userId);
        dest.writeString(nickname);
        dest.writeString(employeeNo);
        dest.writeString(empSexName);
        dest.writeString(userImage);
        dest.writeInt(isEnabled);
        dest.writeInt(isValid);

        dest.writeInt(bgId);
        dest.writeInt(newStatus);
        dest.writeLong(time);
        dest.writeInt(relationStatus);
        dest.writeInt(hasReaded);
        dest.writeString(remarkName);
    }

    public void setRelationStatus(int relationStatus) {
        this.relationStatus = relationStatus;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public void setNewStatus(int newStatus) {
        this.newStatus = newStatus;
    }

    public void setHasReaded(int hasReaded) {
        this.hasReaded = hasReaded;
    }
}
