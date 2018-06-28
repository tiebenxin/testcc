package com.lensim.fingerchat.data.work_center.identify;

import java.io.Serializable;

/**
 * date on 2018/1/15
 * author ll147996
 * describe
 */

public class UserIdentify implements Serializable {


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

    public String USR_ID;
    public String USR_PWD;
    public String USR_Name;
    public String USR_Sex;
    public String USR_Address;
    public String USR_UserImage;
    public String USR_PHONE;
    public int isValid;
    public int isEnable;
    public String jobname;
    public String TitName;
    public String jobNo;
    public String TitNo;
    public String DptNo;
    public String DptName;
    public String IdCard;
    public String EmployeeNO;
    public String EmpName;
    public int isTest;
    public int upCerImg;
    /**
     * USR_RegDate : 2016-08-20T15:49:48.44
     */

    public String USR_RegDate;

}
