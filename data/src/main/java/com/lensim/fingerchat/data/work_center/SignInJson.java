package com.lensim.fingerchat.data.work_center;

/**
 * Created by LL117394 on 2017/08/25
 */

public class SignInJson {

//    "token":"coolpadf2-token",
//        "empno":"AhgRM7FS2VzZrCug%2f8mkyQ%3d%3d",
//        "imuser":"AhgRM7FS2VzZrCug%2f8mkyQ%3d%3d",
//        "signIP":"BJ5KcB8kmyKj40TfKne2Mw%3d%3d",
//        "signTime":"1ZkcpgbrCMk%2bx6SH%2bjY965gXHrMkr7fk",
//        "mobiletype":"wvfjzA3Oins%3d",
//        "mobilename":"z2Q49mVkXAw%3d",
//        "mobileVer":"4.4.4",
//        "uuid":"5%2fAmBjI7j1ALqiKNILGeuQbW7KOT38McAk%2bT6UVxTOOXSmqv75irAg%3d%3d",
//        "imver":"1.6.0",
//        "locationtype":"JdvefCGyu78%3d",
//        "locationdata":"Q6R4Y2Kwt%2fd8rbt5tbLFNNN0VUaHQUveKmrnNDwKCIs09QtTWqHN2OkqZWC1G8IWyqfqfxT8HofAmqBuUKoXinkpjCdeQLUn",
//        "TPSignIn":"y4AQT93FR4XK1mL%2fmZr7vbBWt2BGGgMRU9FWknpBsO0%3d"
  /***
   *暂时可乱写
   */
  private String token;
  /**
   * 唯一识别ID
   */
  private String empno;
  /**
   */
  private String imuser;
  /**
   */
  private String signIP;
  /**
   * 收藏时间
   * yyyy-MM-dd HH:mm:ss
   * 2017-08-03T00:00:00
   */
  private String signTime;
  /**
   **/
  private String mobiletype;
  /**
   */
  private String mobilename;
  /**
   */
  private String mobileVer;
  private String uuid;
  private String imver;
  private String locationtype;
  private String locationdata;
  /***
   * /// TakePhoto 签到拍照 url
   /// 最多三张图片的URL; 用 @分隔
   * **/
  private String TPSignIn;
  private String Remark;
  /***
   *  汇报对象
   *格式:  用户名;用户名;用户名
   * */
  private String ForReport;
  /***
   * 汇报对象的昵称/备注名,用@分隔多人
   因为汇报对象可能是群
   加密 再encode
   * */
  private String ForReportNick;


  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  public String getEmpno() {
    return empno;
  }

  public void setEmpno(String empno) {
    this.empno = empno;
  }

  public String getImuser() {
    return imuser;
  }

  public void setImuser(String imuser) {
    this.imuser = imuser;
  }

  public String getSignIP() {
    return signIP;
  }

  public void setSignIP(String signIP) {
    this.signIP = signIP;
  }

  public String getSignTime() {
    return signTime;
  }

  public void setSignTime(String signTime) {
    this.signTime = signTime;
  }

  public String getMobiletype() {
    return mobiletype;
  }

  public void setMobiletype(String mobiletype) {
    this.mobiletype = mobiletype;
  }

  public String getMobilename() {
    return mobilename;
  }

  public void setMobilename(String mobilename) {
    this.mobilename = mobilename;
  }

  public String getMobileVer() {
    return mobileVer;
  }

  public void setMobileVer(String mobileVer) {
    this.mobileVer = mobileVer;
  }

  public String getUuid() {
    return uuid;
  }

  public void setUuid(String uuid) {
    this.uuid = uuid;
  }

  public String getImver() {
    return imver;
  }

  public void setImver(String imver) {
    this.imver = imver;
  }

  public String getLocationtype() {
    return locationtype;
  }

  public void setLocationtype(String locationtype) {
    this.locationtype = locationtype;
  }

  public String getLocationdata() {
    return locationdata;
  }

  public void setLocationdata(String locationdata) {
    this.locationdata = locationdata;
  }

  public String getTPSignIn() {
    return TPSignIn;
  }

  public void setTPSignIn(String TPSignIn) {
    this.TPSignIn = TPSignIn;
  }

  public String getRemark() {
    return Remark;
  }

  public void setRemark(String remark) {
    Remark = remark;
  }

  public String getForReport() {
    return ForReport;
  }

  public void setForReport(String forReport) {
    ForReport = forReport;
  }

  public String getForReportNick() {
    return ForReportNick;
  }

  public void setForReportNick(String forReportNick) {
    ForReportNick = forReportNick;
  }
}
