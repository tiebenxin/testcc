package com.lensim.fingerchat.commons.interf;

/**
 * Created by LL130386 on 2018/1/4.
 */

public interface IChatUser {

//    void setRoster(RosterItem roster);
//
//    RosterItem getRoster();

    void setUserId(String account);

    String getUserId();

    void setUserNick(String nick);

    String getUserNick();

    void setWorkAddress(String address);

    String getWorkAddress();

    void setGroup(String group);

    String getGroup();

    void setEmpName(String name);

    String getEmpName();

    void setRemarkName(String name);

    String getRemarkName();

    void setSex(String sex);

    String getSex();

    void setAvatarUrl(String image);

    String getAvatarUrl();

    void setValid(int isValid);

    boolean isValid();

    int getValid();

    void setJobName(String job);

    String getJobName();

    void setDptNo(String num);

    String getDptNo();

    void setDptNname(String name);

    String getDptName();

    void setEmpNo(String empNo);

    String getEmpNo();

    void setStar(int star);

    boolean isStar();

    int getStar();

    void setState(int state);

    int getState();

    void setBlock(int block);

    boolean isBlock();

    int getBlock();

    void setQuit(int quit);

    boolean isQuit();

    int getQuit();

    int getChatType();

    int getBgId();

    void setBgId(int id);

    String getFirstChar();

    String getPinYin();

    int getRelationStatus();

    long getTime();

    int hasReaded();

    int getNewStatus();


}
