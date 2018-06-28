package com.lensim.fingerchat.data.work_center;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by LL117394 on 2017/08/25
 */

public class SignInJsonRet implements Parcelable {

  public static final Creator<SignInJsonRet> CREATOR = new Creator<SignInJsonRet>() {
    @Override
    public SignInJsonRet createFromParcel(Parcel source) {
      return new SignInJsonRet(source);
    }

    @Override
    public SignInJsonRet[] newArray(int size) {
      return new SignInJsonRet[size];
    }
  };
  private boolean isFirstDay;
  private int mStatus;
  /***
   *暂时可乱写
   */
  private String token;
  /**
   * 唯一识别ID
   */
  private String EmpNo;
  /**
   */
  private String IMUserName;
  /**
   */
  private String SignInIP;
  /**
   * 收藏时间
   * yyyy-MM-dd HH:mm:ss
   * 2017-08-03T00:00:00
   */
  private String SignInTime;
  /**
   **/
  private String MobileType;
  /**
   */
  private String MobileName;
  /**
   */
  private String MobileVer;
  private String UUID;
  private String IMVer;
  private String LocationType;
  private String LocationData;
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


  public SignInJsonRet() {
  }

  protected SignInJsonRet(Parcel in) {
    this.isFirstDay = in.readByte() != 0;
    this.mStatus = in.readInt();
    this.token = in.readString();
    this.EmpNo = in.readString();
    this.IMUserName = in.readString();
    this.SignInIP = in.readString();
    this.SignInTime = in.readString();
    this.MobileType = in.readString();
    this.MobileName = in.readString();
    this.MobileVer = in.readString();
    this.UUID = in.readString();
    this.IMVer = in.readString();
    this.LocationType = in.readString();
    this.LocationData = in.readString();
    this.TPSignIn = in.readString();
    this.Remark = in.readString();
    this.ForReport = in.readString();
    this.ForReportNick = in.readString();
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeByte(this.isFirstDay ? (byte) 1 : (byte) 0);
    dest.writeInt(this.mStatus);
    dest.writeString(this.token);
    dest.writeString(this.EmpNo);
    dest.writeString(this.IMUserName);
    dest.writeString(this.SignInIP);
    dest.writeString(this.SignInTime);
    dest.writeString(this.MobileType);
    dest.writeString(this.MobileName);
    dest.writeString(this.MobileVer);
    dest.writeString(this.UUID);
    dest.writeString(this.IMVer);
    dest.writeString(this.LocationType);
    dest.writeString(this.LocationData);
    dest.writeString(this.TPSignIn);
    dest.writeString(this.Remark);
    dest.writeString(this.ForReport);
    dest.writeString(this.ForReportNick);
  }

  public boolean isFirstDay() {
    return isFirstDay;
  }

  public void setFirstDay(boolean firstDay) {
    isFirstDay = firstDay;
  }

  public int getmStatus() {
    return mStatus;
  }

  public void setmStatus(int mStatus) {
    this.mStatus = mStatus;
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  public String getEmpNo() {
    return EmpNo;
  }

  public void setEmpNo(String empNo) {
    EmpNo = empNo;
  }

  public String getIMUserName() {
    return IMUserName;
  }

  public void setIMUserName(String IMUserName) {
    this.IMUserName = IMUserName;
  }

  public String getSignInIP() {
    return SignInIP;
  }

  public void setSignInIP(String signInIP) {
    SignInIP = signInIP;
  }

  public String getSignInTime() {
    return SignInTime;
  }

  public void setSignInTime(String signInTime) {
    SignInTime = signInTime;
  }

  public String getMobileType() {
    return MobileType;
  }

  public void setMobileType(String mobileType) {
    MobileType = mobileType;
  }

  public String getMobileName() {
    return MobileName;
  }

  public void setMobileName(String mobileName) {
    MobileName = mobileName;
  }

  public String getMobileVer() {
    return MobileVer;
  }

  public void setMobileVer(String mobileVer) {
    MobileVer = mobileVer;
  }

  public String getUUID() {
    return UUID;
  }

  public void setUUID(String UUID) {
    this.UUID = UUID;
  }

  public String getIMVer() {
    return IMVer;
  }

  public void setIMVer(String IMVer) {
    this.IMVer = IMVer;
  }

  public String getLocationType() {
    return LocationType;
  }

  public void setLocationType(String locationType) {
    LocationType = locationType;
  }

  public String getLocationData() {
    return LocationData;
  }

  public void setLocationData(String locationData) {
    LocationData = locationData;
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
