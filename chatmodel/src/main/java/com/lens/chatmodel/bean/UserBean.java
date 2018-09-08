package com.lens.chatmodel.bean;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import android.text.TextUtils;

import com.fingerchat.proto.message.Roster.RosterItem;
import com.lens.chatmodel.ChatEnum.EChatType;
import com.lens.chatmodel.ChatEnum.ESureType;
import com.lensim.fingerchat.commons.interf.IChatUser;
import com.lensim.fingerchat.commons.utils.StringUtils;
import java.util.List;

/**
 * Created by LL130386 on 2018/1/4.
 * 通讯录数据封装类
 */

public class UserBean implements IChatUser, Parcelable, Comparable<UserBean> {

    private RosterItem roster;
    private String account;//userId
    private String userNick; //昵称
    private String workAddress;
    private String group;//分组
    private String empName;
    private String empNo;
    private String remarkName;//备注名
    private String sex;
    private String image;  //头像
    private String jobName;
    private String dptName;
    private String dptNo;
    private int isValid;
    private int isStar = 0;
    private int isBlock;
    private int state;
    private int isQuit;
    private String firstChar;
    private int mChatType;//单聊群来哦
    private int bgId;
    private String pinYin;
    private List<String> groups;

    //群信息
    private String mucId;
    private String mucName;

    //0人 1组
    private int type;

    private int status; //好友状态：0为好友，1为我发送了邀请，2为我收到了邀请
    private long time; //收到邀请的时间
    private int hasReaded; //是否已读, 0未读，1已读
    private int newStatus; //是否新好友, 0否，1是


    public UserBean(String mucId, String mucName, int type) {
        this.mucId = mucId;
        this.mucName = mucName;
        this.type = type;
        this.account = mucId;
    }

    public UserBean(Parcel in) {
        account = in.readString();
        userNick = in.readString();
        workAddress = in.readString();
        group = in.readString();
        empName = in.readString();
        empNo = in.readString();
        remarkName = in.readString();
        sex = in.readString();
        image = in.readString();
        jobName = in.readString();
        dptName = in.readString();
        dptNo = in.readString();
        isValid = in.readInt();
        isStar = in.readInt();
        isBlock = in.readInt();
        state = in.readInt();
        isQuit = in.readInt();
        firstChar = in.readString();
        mChatType = in.readInt();
        bgId = in.readInt();
        pinYin = in.readString();

        mucId = in.readString();
        mucName = in.readString();
        type = in.readInt();

        status = in.readInt();
        time = in.readLong();
        hasReaded = in.readInt();
        newStatus = in.readInt();

    }

    public static final Creator<UserBean> CREATOR = new Creator<UserBean>() {
        @Override
        public UserBean createFromParcel(Parcel in) {
            return new UserBean(in);
        }

        @Override
        public UserBean[] newArray(int size) {
            return new UserBean[size];
        }
    };

    public UserBean() {

    }

    public UserBean(RosterItem roster) {
        setRoster(roster);
    }

    public RosterItem getRoster() {
        if (roster == null) {
            RosterItem.Builder builder = RosterItem.newBuilder();
            roster = builder.build();
        }
        return roster;
    }

    public void setRoster(RosterItem roster) {
        this.roster = roster;
        initFields();
    }

    private void initFields() {
        if (roster == null) {
            return;
        }
        account = roster.getUsername();
        userNick = roster.getUsernick();
        workAddress = roster.getWorkAddress();
        group = roster.getGroup();
        empName = roster.getEmpName();
        empNo = roster.getEmpNo();
        remarkName = roster.getRemarkName();
        sex = roster.getSex();
        image = roster.getAvatar();
        jobName = roster.getJobname();
        dptName = roster.getDptName();
        dptNo = roster.getDptNo();
        isValid = roster.getIsvalid();
        isStar = roster.getIsStar();
        isBlock = roster.getIsBlock();
        state = roster.getState();
        isQuit = roster.getIsQuit();
        mChatType = EChatType.PRIVATE.ordinal();
        if (!TextUtils.isEmpty(remarkName)) {
            pinYin = StringUtils.getFullPinYin(remarkName);
            firstChar = StringUtils.getFristChar(remarkName);
        } else {
            if (!TextUtils.isEmpty(userNick)) {
//                remarkName = userNick;
                pinYin = StringUtils.getFullPinYin(userNick);
                firstChar = StringUtils.getFristChar(userNick);
            } else {
                pinYin = "";
                firstChar = "#";
            }
        }
        if (!TextUtils.isEmpty(roster.getChatBg())) {
            bgId = Integer.parseInt(roster.getChatBg());
        }
    }

    public void setBean(IChatUser user) {
        account = StringUtils.checkEmptyString(user.getUserId());
        userNick = StringUtils.checkEmptyString(user.getUserNick());
        workAddress = StringUtils.checkEmptyString(user.getWorkAddress());
        group = StringUtils.checkEmptyString(user.getGroup());
        empName = StringUtils.checkEmptyString(user.getEmpName());
        empNo = StringUtils.checkEmptyString(user.getEmpNo());
        remarkName = StringUtils.checkEmptyString(user.getRemarkName());
        sex = StringUtils.checkEmptyString(user.getSex());
        image = StringUtils.checkEmptyString(user.getAvatarUrl());
        jobName = StringUtils.checkEmptyString(user.getJobName());
        dptName = StringUtils.checkEmptyString(user.getDptName());
        dptNo = StringUtils.checkEmptyString(user.getDptNo());
        isValid = user.isValid() ? ESureType.YES.ordinal() : ESureType.NO.ordinal();
        isStar = user.isStar() ? ESureType.YES.ordinal() : ESureType.NO.ordinal();
        isBlock = user.isBlock() ? ESureType.YES.ordinal() : ESureType.NO.ordinal();
        state = user.getState();
        isQuit = user.isQuit() ? ESureType.YES.ordinal() : ESureType.NO.ordinal();
        mChatType = EChatType.PRIVATE.ordinal();

        if (!TextUtils.isEmpty(remarkName)) {
            pinYin = StringUtils.getFullPinYin(remarkName);
            firstChar = StringUtils.getFristChar(remarkName);
        } else {
            if (!TextUtils.isEmpty(userNick)) {
//                remarkName = userNick;
                pinYin = StringUtils.getFullPinYin(userNick);
                firstChar = StringUtils.getFristChar(userNick);
            } else {
                pinYin = "";
                firstChar = "#";
            }
        }
        bgId = user.getBgId();

    }

    public int getNewStatus() {
        return newStatus;
    }

    public void setNewStatus(int newStatus) {
        this.newStatus = newStatus;
    }

    public int getRelationStatus() {
        return status;
    }

    public void setRelationStatus(int status) {
        this.status = status;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int hasReaded() {
        return hasReaded;
    }

    public void setHasReaded(int hasReaded) {
        this.hasReaded = hasReaded;
    }

    public String getFirstChar() {
        return firstChar;
    }

    @Override
    public String getPinYin() {
        return pinYin;
    }

    public void setPinYin(String value) {
        pinYin = value;
    }

    public String getMucId() {
        return mucId;
    }

    public void setMucId(String mucId) {
        this.mucId = mucId;
    }

    public String getMucName() {
        return mucName;
    }

    public void setMucName(String mucName) {
        this.mucName = mucName;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setFirstChar(String firstChar) {
        this.firstChar = firstChar;
    }

    @Override
    public void setUserId(String account) {
        RosterItem.Builder builder = getRoster().toBuilder();
        builder.setUsername(account);
        setRoster(builder.build());
    }

    @Override
    public String getUserId() {
        return account;
    }

    @Override
    public void setUserNick(String nick) {
        RosterItem.Builder builder = getRoster().toBuilder();
        builder.setUsernick(nick);
        setRoster(builder.build());
    }

    @Override
    public String getUserNick() {
        return userNick;
    }

    @Override
    public void setWorkAddress(String address) {
        RosterItem.Builder builder = getRoster().toBuilder();
        builder.setWorkAddress(address);
        setRoster(builder.build());
    }

    @Override
    public String getWorkAddress() {
        return workAddress;
    }

    @Override
    public void setGroup(String group) {
        RosterItem.Builder builder = getRoster().toBuilder();
        builder.setGroup(group);
        setRoster(builder.build());
    }

    @Override
    public String getGroup() {
        return group;
    }

    @Override
    public void setEmpName(String name) {
        RosterItem.Builder builder = getRoster().toBuilder();
        builder.setEmpName(name);
        setRoster(builder.build());
    }

    @Override
    public String getEmpName() {
        return empName;
    }

    @Override
    public void setRemarkName(String name) {
        RosterItem.Builder builder = getRoster().toBuilder();
        builder.setRemarkName(name);
        setRoster(builder.build());
    }

    @Override
    public String getRemarkName() {
        return remarkName;
    }

    @Override
    public void setSex(String sex) {
        RosterItem.Builder builder = getRoster().toBuilder();
        builder.setSex(sex);
        setRoster(builder.build());
    }

    @Override
    public String getSex() {
        return sex;
    }

    @Override
    public void setAvatarUrl(String image) {
        RosterItem.Builder builder = getRoster().toBuilder();
        builder.setAvatar(image);
        setRoster(builder.build());
    }

    @Override
    public String getAvatarUrl() {
        return image;
    }

    @Override
    public void setValid(int isValid) {
        RosterItem.Builder builder = getRoster().toBuilder();
        builder.setIsvalid(isValid);
        setRoster(builder.build());
    }

    @Override
    public boolean isValid() {
        return isValid == ESureType.YES.ordinal();
    }

    public int getValid() {
        return isValid;
    }

    @Override
    public void setJobName(String job) {
        RosterItem.Builder builder = getRoster().toBuilder();
        builder.setJobname(job);
        setRoster(builder.build());
    }

    @Override
    public String getJobName() {
        return jobName;
    }

    @Override
    public void setDptNo(String num) {
        RosterItem.Builder builder = getRoster().toBuilder();
        builder.setDptNo(num);
        setRoster(builder.build());
    }

    @Override
    public String getDptNo() {
        return dptNo;
    }

    @Override
    public void setDptNname(String name) {
        RosterItem.Builder builder = getRoster().toBuilder();
        builder.setDptName(name);
        setRoster(builder.build());
    }

    @Override
    public String getDptName() {
        return dptName;
    }

    @Override
    public void setEmpNo(String empNo) {
        RosterItem.Builder builder = getRoster().toBuilder();
        builder.setEmpName(empNo);
        setRoster(builder.build());
    }

    @Override
    public String getEmpNo() {
        return empNo;
    }

    @Override
    public void setStar(int star) {
        RosterItem.Builder builder = getRoster().toBuilder();
        builder.setIsStar(star);
        setRoster(builder.build());
    }

    @Override
    public boolean isStar() {
        return isStar == ESureType.YES.ordinal();
    }

    @Override
    public int getStar() {
        return isStar;
    }

    @Override
    public void setState(int state) {
        RosterItem.Builder builder = getRoster().toBuilder();
        builder.setState(state);
        setRoster(builder.build());
    }

    @Override
    public int getState() {
        return state;
    }

    @Override
    public void setBlock(int block) {
        RosterItem.Builder builder = getRoster().toBuilder();
        builder.setIsBlock(block);
        setRoster(builder.build());
    }

    @Override
    public boolean isBlock() {
        return isBlock == ESureType.YES.ordinal();
    }

    @Override
    public int getBlock() {
        return isBlock;

    }

    @Override
    public void setQuit(int quit) {
        RosterItem.Builder builder = getRoster().toBuilder();
        builder.setIsQuit(quit);
        setRoster(builder.build());
    }

    @Override
    public boolean isQuit() {
        return isQuit == ESureType.YES.ordinal();
    }

    @Override
    public int getQuit() {
        return isQuit;
    }

    @Override
    public int getChatType() {
        return mChatType;
    }

    @Override
    public int getBgId() {
        return bgId;
    }

    @Override
    public void setBgId(int id) {
        bgId = id;
    }

    public void setChatType(int type) {
        mChatType = type;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(account);
        dest.writeString(userNick);
        dest.writeString(workAddress);
        dest.writeString(group);
        dest.writeString(empName);
        dest.writeString(empNo);
        dest.writeString(remarkName);
        dest.writeString(sex);
        dest.writeString(image);
        dest.writeString(jobName);
        dest.writeString(dptName);
        dest.writeString(dptNo);
        dest.writeInt(isValid);
        dest.writeInt(isStar);
        dest.writeInt(isBlock);
        dest.writeInt(state);
        dest.writeInt(isQuit);
        dest.writeString(firstChar);
        dest.writeInt(mChatType);
        dest.writeInt(bgId);
        dest.writeString(pinYin);
        dest.writeString(mucId);
        dest.writeString(mucName);
        dest.writeInt(type);

        dest.writeInt(status);
        dest.writeLong(time);
        dest.writeInt(hasReaded);
        dest.writeInt(newStatus);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof UserBean) {
            UserBean userBean = (UserBean) obj;
            return this.account.equals(userBean.account);
        }
        return super.equals(obj);
    }

    @Override
    public int compareTo(@NonNull UserBean userBean) {
        return (TextUtils.isEmpty(this.getFirstChar()) ? "#" : this.getFirstChar())
            .compareTo(userBean.getFirstChar());
    }

    public List<String> getGroups() {
        return StringUtils.getGroups(group);
    }
}
