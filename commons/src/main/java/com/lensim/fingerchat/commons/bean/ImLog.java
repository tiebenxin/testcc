package com.lensim.fingerchat.commons.bean;

/**
 * Created by LY309313 on 2017/4/25.
 */

public class ImLog {
    /// <summary>
    /// 日志时间
    /// </summary>
    private String logTime;
    /// <summary>
    /// app版本
    /// </summary>
    private String logAppVer;
    /// <summary>
    /// 系统版本
    /// </summary>
    private String logSysVer;
    /// <summary>
    /// 制造商
    /// </summary>
    private String logDevName;
    /// <summary>
    /// 手机类型
    /// </summary>
    private String logMobileType;
    /// <summary>
    /// 日志内容. 需加密
    /// </summary>
    private String logContent;
    /// <summary>
    /// 用户名,不可为null
    /// </summary>
    private String userName;
    /// <summary>
    /// 用户工号
    /// </summary>
    private String userEmpNo;
    /// <summary>
    /// 终端ID（标示手机的UDID)
    /// </summary>
    private String udid;
    /// <summary>
    /// 目标帐号
    /// </summary>
    private String aimUser;
    /// <summary>
    /// 操作: a_1=复制文字  a_2=保存图片  a_3=截图  a_4=错误信息
    /// </summary>
    private String op;


    public String getLogTime() {
        return logTime;
    }

    public void setLogTime(String logTime) {
        this.logTime = logTime;
    }

    public String getLogAppVer() {
        return logAppVer;
    }

    public void setLogAppVer(String logAppVer) {
        this.logAppVer = logAppVer;
    }

    public String getLogSysVer() {
        return logSysVer;
    }

    public void setLogSysVer(String logSysVer) {
        this.logSysVer = logSysVer;
    }

    public String getLogDevName() {
        return logDevName;
    }

    public void setLogDevName(String logDevName) {
        this.logDevName = logDevName;
    }

    public String getLogMobileType() {
        return logMobileType;
    }

    public void setLogMobileType(String logMobileType) {
        this.logMobileType = logMobileType;
    }

    public String getLogContent() {
        return logContent;
    }

    public void setLogContent(String logContent) {
        this.logContent = logContent;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmpNo() {
        return userEmpNo;
    }

    public void setUserEmpNo(String userEmpNo) {
        this.userEmpNo = userEmpNo;
    }

    public String getUdid() {
        return udid;
    }

    public void setUdid(String udid) {
        this.udid = udid;
    }

    public String getAimUser() {
        return aimUser;
    }

    public void setAimUser(String aimUser) {
        this.aimUser = aimUser;
    }

    public String getOp() {
        return op;
    }

    public void setOp(String op) {
        this.op = op;
    }

}
