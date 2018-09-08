package com.lensim.fingerchat.data.work_center;

/**
 * Created by LL117394 on 2017/08/25
 */

public class SignInJson {

    //    "deviceToken":"coolpadf2-deviceToken",
//        "employeeNo":"AhgRM7FS2VzZrCug%2f8mkyQ%3d%3d",
//        "imUser":"AhgRM7FS2VzZrCug%2f8mkyQ%3d%3d",
//        "signIp":"BJ5KcB8kmyKj40TfKne2Mw%3d%3d",
//        "signTime":"1ZkcpgbrCMk%2bx6SH%2bjY965gXHrMkr7fk",
//        "clientType":"wvfjzA3Oins%3d",
//        "clientName":"z2Q49mVkXAw%3d",
//        "clientVersion":"4.4.4",
//        "uuid":"5%2fAmBjI7j1ALqiKNILGeuQbW7KOT38McAk%2bT6UVxTOOXSmqv75irAg%3d%3d",
//        "imver":"1.6.0",
//        "locationType":"JdvefCGyu78%3d",
//        "locationData":"Q6R4Y2Kwt%2fd8rbt5tbLFNNN0VUaHQUveKmrnNDwKCIs09QtTWqHN2OkqZWC1G8IWyqfqfxT8HofAmqBuUKoXinkpjCdeQLUn",
//        "TPSignIn":"y4AQT93FR4XK1mL%2fmZr7vbBWt2BGGgMRU9FWknpBsO0%3d"

    String clientName;//客户端手机品牌
    String clientType;//客户端类型，android
    String clientVersion;//客户端版本
    String deviceToken;//客户端签名,暂时可乱写
    String employeeNo;//工号
    String imUser;//IM账号
    int isValid;//0无效，1有效
    String locationData;//定位数据
    String locationPhoto;//定位图片（最多三张图片的URL; 用 @分隔）
    String locationType;//位置类型。GPS,基站
    //    int logId;//主键
    String remark;//备注
    String reporter;//报告人(格式:  用户名;用户名;用户名)
    String reporterNickname;//报告人昵称(汇报对象的昵称/备注名,用@分隔多人,因为汇报对象可能是群,加密 再encode)
    String signIp;//签到IP地址

    /**
     * 签到时间
     * yyyy-MM-dd HH:mm:ss
     * 2017-08-03T00:00:00
     */
    private String signTime;

//    private String uuid;
//    private String imver;
//    private String TPSignIn;


    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public String getEmployeeNo() {
        return employeeNo;
    }

    public void setEmployeeNo(String employeeNo) {
        this.employeeNo = employeeNo;
    }

    public String getImUser() {
        return imUser;
    }

    public void setImUser(String imUser) {
        this.imUser = imUser;
    }

    public String getSignIp() {
        return signIp;
    }

    public void setSignIp(String signIp) {
        this.signIp = signIp;
    }

    public String getSignTime() {
        return signTime;
    }

    public void setSignTime(String signTime) {
        this.signTime = signTime;
    }

    public String getClientType() {
        return clientType;
    }

    public void setClientType(String clientType) {
        this.clientType = clientType;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getClientVersion() {
        return clientVersion;
    }

    public void setClientVersion(String clientVersion) {
        this.clientVersion = clientVersion;
    }

//    public String getUuid() {
//        return uuid;
//    }
//
//    public void setUuid(String uuid) {
//        this.uuid = uuid;
//    }
//
//    public String getImver() {
//        return imver;
//    }
//
//    public void setImver(String imver) {
//        this.imver = imver;
//    }

    public String getLocationType() {
        return locationType;
    }

    public void setLocationType(String locationType) {
        this.locationType = locationType;
    }

    public String getLocationData() {
        return locationData;
    }

    public void setLocationData(String locationData) {
        this.locationData = locationData;
    }

//    public String getTPSignIn() {
//        return TPSignIn;
//    }
//
//    public void setTPSignIn(String TPSignIn) {
//        this.TPSignIn = TPSignIn;
//    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getReporter() {
        return reporter;
    }

    public void setReporter(String reporter) {
        this.reporter = reporter;
    }

    public String getReporterNickname() {
        return reporterNickname;
    }

    public void setReporterNickname(String reporterNickname) {
        this.reporterNickname = reporterNickname;
    }

    public int getIsValid() {
        return isValid;
    }

    public void setIsValid(int isValid) {
        this.isValid = isValid;
    }

    public String getLocationPhoto() {
        return locationPhoto;
    }

    public void setLocationPhoto(String locationPhoto) {
        this.locationPhoto = locationPhoto;
    }
}
