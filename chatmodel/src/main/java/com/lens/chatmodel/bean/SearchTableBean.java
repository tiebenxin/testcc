package com.lens.chatmodel.bean;

import android.os.Parcel;
import android.os.Parcelable;
import com.lens.chatmodel.ChatEnum.EChatType;
import com.lens.chatmodel.ChatEnum.ESureType;
import com.lensim.fingerchat.commons.interf.IChatUser;
import com.lensim.fingerchat.commons.utils.StringUtils;

/**
 * Created by LL130386 on 2018/1/22.
 * 搜索结果类
 */

public class SearchTableBean implements Parcelable, IChatUser {

    private String empNo;
    private String empName;//真实姓名
    private int isEnabled;
    private int isValid;
    private String nickname;
    private long registerTime;
    private String userId;
    private String userImage;
    private int userLevel;//职级
    private String userMobile;
    private int userPrivileges;//权限
    private int bgId;
    private int newStatus;
    private long time;
    private int relationStatus = -1;
    private int hasReaded;
    private String empSexName;//性别

    protected SearchTableBean(Parcel in) {
        empNo = in.readString();
        empName = in.readString();
        isEnabled = in.readInt();
        isValid = in.readInt();
        nickname = in.readString();
        registerTime = in.readLong();
        userId = in.readString();
        userImage = in.readString();
        userLevel = in.readInt();
        userMobile = in.readString();
        userPrivileges = in.readInt();
        bgId = in.readInt();
        newStatus = in.readInt();
        time = in.readLong();
        relationStatus = in.readInt();
        hasReaded = in.readInt();
        empSexName = in.readString();
    }

    public static final Creator<SearchTableBean> CREATOR = new Creator<SearchTableBean>() {
        @Override
        public SearchTableBean createFromParcel(Parcel in) {
            return new SearchTableBean(in);
        }

        @Override
        public SearchTableBean[] newArray(int size) {
            return new SearchTableBean[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(empNo);
        dest.writeString(empName);
        dest.writeInt(isEnabled);
        dest.writeInt(isValid);
        dest.writeString(nickname);
        dest.writeLong(registerTime);
        dest.writeString(userId);
        dest.writeString(userImage);
        dest.writeInt(userLevel);
        dest.writeString(userMobile);
        dest.writeInt(userPrivileges);
        dest.writeInt(bgId);
        dest.writeInt(newStatus);
        dest.writeLong(time);
        dest.writeInt(relationStatus);
        dest.writeInt(hasReaded);
        dest.writeString(empSexName);
    }


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

    }

    @Override
    public String getWorkAddress() {
        return "";
    }

    @Override
    public void setGroup(String group) {

    }

    @Override
    public String getGroup() {
        return "";
    }

    @Override
    public void setEmpName(String name) {
        empName = name;
    }

    @Override
    public String getEmpName() {
        return empName;
    }

    @Override
    public void setRemarkName(String name) {

    }

    @Override
    public String getRemarkName() {
        return "";
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

    }

    @Override
    public String getJobName() {
        return "";
    }

    @Override
    public void setDptNo(String num) {

    }

    @Override
    public String getDptNo() {
        return "";
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
        this.empNo = empNo;
    }

    @Override
    public String getEmpNo() {
        return empNo;
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
        relationStatus = state;
    }

    @Override
    public int getState() {
        return relationStatus;
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
        isEnabled = quit;
    }

    @Override
    public boolean isQuit() {
        return isEnabled == ESureType.YES.ordinal();
    }

    @Override
    public int getQuit() {
        return isEnabled;
    }

    @Override
    public int getChatType() {
        return EChatType.PRIVATE.ordinal();
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
        return StringUtils.getFristChar(nickname);
    }

    @Override
    public String getPinYin() {
        return StringUtils.getFullPinYin(nickname);
    }

    public void setRelationStatus(int status) {
        relationStatus = status;
    }

    @Override
    public int getRelationStatus() {
        return relationStatus;
    }

    public void setTime(long t) {
        time = t;
    }

    @Override
    public long getTime() {
        return time;
    }

    @Override
    public int hasReaded() {
        return hasReaded;
    }

    public void setHasReaded(int readed) {
        hasReaded = readed;
    }

    public void setNewStatus(int status) {
        newStatus = status;
    }

    @Override
    public int getNewStatus() {
        return newStatus;
    }

    @Override
    public int getStar() {
        return 0;
    }
}
