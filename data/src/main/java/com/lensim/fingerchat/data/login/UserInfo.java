package com.lensim.fingerchat.data.login;

import java.io.Serializable;

/**
 * date on 2018/1/4
 * author ll147996
 * describe
 */

public class UserInfo implements Serializable {

    /**
     * <pre>
     *用户名
     * </pre>
     *
     * <code>string userid = 1;</code>
     */
    private String userid = "11";

    /**
     * <pre>
     *昵称
     * </pre>
     *
     * <code>string usernick = 2;</code>
     */
    private String usernick = "22";

    /**
     * <pre>
     *以下除头像都是客户端不可变动
     * </pre>
     *
     * <code>string phoneNumber = 3;</code>
     */
    private String phoneNumber = "";

    /**
     * <pre>
     *园区--
     * </pre>
     *
     * <code>string workAddress = 4;</code>
     */
    private String workAddress = "";

    /**
     * <pre>
     *真实姓名--
     * </pre>
     *
     * <code>string empName = 5;</code>
     */
    private String empName = "";

    /**
     * <pre>
     *性别--
     * </pre>
     *
     * <code>string sex = 6;</code>
     */
    private String sex = "";

    /**
     * <pre>
     *头像
     * </pre>
     *
     * <code>string image = 7;</code>
     */
    private String image = "";

    /**
     * <pre>
     *是否认证--
     * </pre>
     *
     * <code>int32 isvalid = 8;</code>
     */
    private int isvalid = 0;

    /**
     * <pre>
     *职位--
     * </pre>
     *
     * <code>string jobname = 9;</code>
     */
    private String jobname = "";

    /**
     * <pre>
     *部门id
     * </pre>
     *
     * <code>string dptNo = 10;</code>
     */
    private String dptNo = "";

    /**
     * <pre>
     *部门
     * </pre>
     *
     * <code>string dptName = 11;</code>
     */
    private String dptName = "";

    /**
     * <pre>
     *工号
     * </pre>
     *
     * <code>string empNo = 12;</code>
     */
    private String empNo = "";

    /**
     * <pre>
     *权限，数组
     * </pre>
     *
     * <code>string right = 13;</code>
     */
    private String right = "";

    public UserInfo(){}

    public UserInfo(String userid, String usernick, String phoneNumber, String workAddress,
        String empName, String sex, String image, int isvalid, String jobname, String dptNo,
        String dptName, String empNo, String right) {
        this.userid = userid;
        this.usernick = usernick;
        this.phoneNumber = phoneNumber;
        this.workAddress = workAddress;
        this.empName = empName;
        this.sex = sex;
        this.image = image;
        this.isvalid = isvalid;
        this.jobname = jobname;
        this.dptNo = dptNo;
        this.dptName = dptName;
        this.empNo = empNo;
        this.right = right;

    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getUsernick() {
        return usernick;
    }

    public void setUsernick(String usernick) {
        this.usernick = usernick;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getWorkAddress() {
        return workAddress;
    }

    public void setWorkAddress(String workAddress) {
        this.workAddress = workAddress;
    }

    public String getEmpName() {
        return empName;
    }

    public void setEmpName(String empName) {
        this.empName = empName;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getIsvalid() {
        return isvalid;
    }

    public void setIsvalid(int isvalid) {
        this.isvalid = isvalid;
    }

    public String getJobname() {
        return jobname;
    }

    public void setJobname(String jobname) {
        this.jobname = jobname;
    }

    public String getDptNo() {
        return dptNo;
    }

    public void setDptNo(String dptNo) {
        this.dptNo = dptNo;
    }

    public String getDptName() {
        return dptName;
    }

    public void setDptName(String dptName) {
        this.dptName = dptName;
    }

    public String getEmpNo() {
        return empNo;
    }

    public void setEmpNo(String empNo) {
        this.empNo = empNo;
    }

    public String getRight() {
        return right;
    }

    public void setRight(String right) {
        this.right = right;
    }
}
