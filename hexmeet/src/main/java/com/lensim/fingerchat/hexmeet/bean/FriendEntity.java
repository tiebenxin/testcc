package com.lensim.fingerchat.hexmeet.bean;

/**
 * Created by LY309313 on 2016/8/24.
 *
 */

public class FriendEntity {

    private String USR_ID;
    private String USR_Name;
    private String USR_UserImage;
    private String nick;
    private String username;
    private String jid;
    private String isEnable;
    private int isValid;

    public String getUSR_ID() {
        return USR_ID;
    }

    public void setUSR_ID(String USR_ID) {
        this.USR_ID = USR_ID;
    }

    public String getUSR_Name() {
        return USR_Name;
    }

    public void setUSR_Name(String USR_Name) {
        this.USR_Name = USR_Name;
    }

    public String getUSR_UserImage() {
        return USR_UserImage;
    }

    public void setUSR_UserImage(String USR_UserImage) {
        this.USR_UserImage = USR_UserImage;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getJid() {
        return jid;
    }

    public void setJid(String jid) {
        this.jid = jid;
    }

    public int getIsValid() {
        return isValid;
    }

    public void setIsValid(int isValid) {
        this.isValid = isValid;
    }

    public String getIsEnable() {
        return isEnable;
    }

    public void setIsEnable(String isEnable) {
        this.isEnable = isEnable;
    }
}
