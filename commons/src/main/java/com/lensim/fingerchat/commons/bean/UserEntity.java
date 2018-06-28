package com.lensim.fingerchat.commons.bean;


/**
 * Created by LY309313 on 2016/8/24.
 */

public class UserEntity extends BaseBean{

    /**
     *
     * [{"USR_ID":"ly309313","USR_PWD":"","USR_Name":"大暗黑天","USR_Sex":"男",
     * "USR_Address":"杭州市江干区","USR_RegDate":"2016-08-20T15:49:48.44",
     * "USR_UserImage":"C:\\HnlensWeb\\HnlensImage\\Users\\ly309313\\Avatar\\headimage.png",
     * "USR_PHONE":15717498850.0,"isValid":1,"jobname":"主任工程师","TitName":"默认",
     * "jobNo":"X0000013C2","TitNo":"0000","DptNo":"LY12000000","DptName":"IT部","IdCard":"4309880625115",
     * "EmployeeNO":"LY309313","EmpName":"周志新","isTest":1}]
     * USR_ID : lyxxxx
     * USR_PWD : MTIzNDU=
     * USR_Name : 昵称
     * USR_Sex :
     * USR_Address : 星沙
     * USR_UserImage : C:\HnlensWeb\HnlensImage\Users\ly252\Avatar\headimage.png
     * USR_PHONE : 15754525623
     * isValid : 1
     * jobname : 主任工程师
     * TitName : 默认
     * jobNo : X0000013C2
     * TitNo : 0000
     * DptNo : LY12000000
     * DptName : IT部
     * IdCard : 545454525454
     * EmployeeNO : 工号
     * EmpName : 昵称
     */

    private String USR_ID;
    private String USR_PWD;
    private String USR_Name;
    private String USR_Sex;
    private String USR_Address;
    private String USR_UserImage;
    private String USR_PHONE;
    private int isValid;
    private int isEnable=1;
    private String jobname;
    private String TitName;
    private String jobNo;
    private String TitNo;
    private String DptNo;
    private String DptName;
    private String IdCard;
    private String EmployeeNO;
    private String EmpName;
    private int isTest;
    private int upCerImg;
    /**
     * USR_RegDate : 2016-08-20T15:49:48.44
     */

    private String USR_RegDate;

    public String getUSR_ID() {
        return USR_ID;
    }

    public void setUSR_ID(String USR_ID) {
        this.USR_ID = USR_ID;
    }

    public String getUSR_PWD() {
        return USR_PWD;
    }

    public void setUSR_PWD(String USR_PWD) {
        this.USR_PWD = USR_PWD;
    }

    public String getUSR_Name() {
        return USR_Name;
    }

    public void setUSR_Name(String USR_Name) {
        this.USR_Name = USR_Name;
    }

    public String getUSR_Sex() {
        return USR_Sex;
    }

    public void setUSR_Sex(String USR_Sex) {
        this.USR_Sex = USR_Sex;
    }

    public String getUSR_Address() {
        return USR_Address;
    }

    public void setUSR_Address(String USR_Address) {
        this.USR_Address = USR_Address;
    }

    public String getUSR_UserImage() {
        return USR_UserImage;
    }

    public void setUSR_UserImage(String USR_UserImage) {
        this.USR_UserImage = USR_UserImage;
    }

    public String getUSR_PHONE() {
        return USR_PHONE;
    }

    public void setUSR_PHONE(String USR_PHONE) {
        this.USR_PHONE = USR_PHONE;
    }

    public int getIsValid() {
        return isValid;
    }

    public void setIsValid(int isValid) {
        this.isValid = isValid;
    }

    public int getIsEnable() {
        return isEnable;
    }

    public void setIsEnable(int isEnable) {
        this.isEnable = isEnable;
    }

    public String getJobname() {
        return jobname;
    }

    public void setJobname(String jobname) {
        this.jobname = jobname;
    }

    public String getTitName() {
        return TitName;
    }

    public void setTitName(String TitName) {
        this.TitName = TitName;
    }

    public String getJobNo() {
        return jobNo;
    }

    public void setJobNo(String jobNo) {
        this.jobNo = jobNo;
    }

    public String getTitNo() {
        return TitNo;
    }

    public void setTitNo(String TitNo) {
        this.TitNo = TitNo;
    }

    public String getDptNo() {
        return DptNo;
    }

    public void setDptNo(String DptNo) {
        this.DptNo = DptNo;
    }

    public String getDptName() {
        return DptName;
    }

    public void setDptName(String DptName) {
        this.DptName = DptName;
    }

    public String getIdCard() {
        return IdCard;
    }

    public void setIdCard(String IdCard) {
        this.IdCard = IdCard;
    }

    public String getEmployeeNO() {
        return EmployeeNO;
    }

    public void setEmployeeNO(String EmployeeNO) {
        this.EmployeeNO = EmployeeNO;
    }

    public String getEmpName() {
        return EmpName;
    }

    public void setEmpName(String EmpName) {
        this.EmpName = EmpName;
    }

    public int getIsTest() {
        return isTest;
    }

    public void setIsTest(int isTest) {
        this.isTest = isTest;
    }

    public String getUSR_RegDate() {
        return USR_RegDate;
    }

    public void setUSR_RegDate(String USR_RegDate) {
        this.USR_RegDate = USR_RegDate;
    }

    public int getUpCerImg() {
        return upCerImg;
    }

    public void setUpCerImg(int upCerImg) {
        this.upCerImg = upCerImg;
    }
}
