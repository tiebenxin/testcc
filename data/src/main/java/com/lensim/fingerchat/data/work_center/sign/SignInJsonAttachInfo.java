package com.lensim.fingerchat.data.work_center.sign;

/**
 * Created by LL117394 on 2017/09/08
 */
public class SignInJsonAttachInfo {

//  {"name":"赵凯","msgtype":"puchCard","secret":"0","pcardtime":"2017-09-08 13:45","pcardlocationInfo":"
// 湖南省长沙市长沙县漓湘中路,蓝思科技东144米,28.231036,113.098672","pcardremark":"测试一哈","
// pcardforreport":"Kkkkkk；刘华帅；陈青","pcardimages":"HnlensImage/Message/LensFSFiles/Image/e/69/e7/e69e7d5fb096aa4789eb5d008132ab54_b.jpg
// @HnlensImage/Message/LensFSFiles/Image/5/c3/a7/5c3a7ce42edd0c9a5c8136a4e13c5394_b.jpg
// @HnlensImage/Message/LensFSFiles/Image/7/12/d1/712d10aaeef0bfeb613e4057a1fb3a18_b.jpg"}

  private String name;
  private String msgtype;
  private String secret;
  private String pcardtime;
  private String pcardlocationInfo;
  private String pcardremark;
  private String pcardforreport;
  private String pcardimages;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getMsgtype() {
    return msgtype;
  }

  public void setMsgtype(String msgtype) {
    this.msgtype = msgtype;
  }

  public String getSecret() {
    return secret;
  }

  public void setSecret(String secret) {
    this.secret = secret;
  }

  public String getPcardtime() {
    return pcardtime;
  }

  public void setPcardtime(String pcardtime) {
    this.pcardtime = pcardtime;
  }

  public String getPcardlocationInfo() {
    return pcardlocationInfo;
  }

  public void setPcardlocationInfo(String pcardlocationInfo) {
    this.pcardlocationInfo = pcardlocationInfo;
  }

  public String getPcardremark() {
    return pcardremark;
  }

  public void setPcardremark(String pcardremark) {
    this.pcardremark = pcardremark;
  }

  public String getPcardforreport() {
    return pcardforreport;
  }

  public void setPcardforreport(String pcardforreport) {
    this.pcardforreport = pcardforreport;
  }

  public String getPcardimages() {
    return pcardimages;
  }

  public void setPcardimages(String pcardimages) {
    this.pcardimages = pcardimages;
  }
}
