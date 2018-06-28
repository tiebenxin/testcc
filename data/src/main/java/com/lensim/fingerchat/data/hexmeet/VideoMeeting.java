package com.lensim.fingerchat.data.hexmeet;

/**
 * Created by LY309313 on 2016/9/13.
 */

public class VideoMeeting {

  /**
   * MeetingId : 51 MeetingCreater : ll117394 MeetingName : user13的会议 MeetingStart : 1970-01-01T08:00:00 MeetingEnd : 2017-11-21T17:50:00 MeetingPwd : MeetingSIP : 0 MeetingRemark : MeetingParticipants :
   * [{"headPortrait":"http://mobile.fingerchat.cn:8686/HnlensImage/Users/ll117958@fingerchat.cn/Avatar/headimage.png","nickname":"赵凯","participants":""},{"headPortrait":"http://mobile.fingerchat.cn:8686/HnlensImage/Users/ll117394/Avatar/headimage.png","nickname":"陈青","participants":""}]
   * MeetingconfId : 0
   */

  private int MeetingId;//FG会议ID
  private String MeetingCreater;//视频会议发起人
  private String MeetingName;//视频会议名称
  private String MeetingStart;//会议开始时间
  private String MeetingEnd;//会议结束时间
  private String MeetingPwd;//会议密码
  private String MeetingSIP;//会议SIP
  private String MeetingRemark;//会议备注
  private String MeetingParticipants;// 与会者json list
  private String MeetingconfId;//Hex会议ID
  private String token;//上传用token

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  public int getMeetingId() {
    return MeetingId;
  }

  public void setMeetingId(int MeetingId) {
    this.MeetingId = MeetingId;
  }

  public String getMeetingCreater() {
    return MeetingCreater;
  }

  public void setMeetingCreater(String MeetingCreater) {
    this.MeetingCreater = MeetingCreater;
  }

  public String getMeetingName() {
    return MeetingName;
  }

  public void setMeetingName(String MeetingName) {
    this.MeetingName = MeetingName;
  }

  public String getMeetingStart() {
    return MeetingStart;
  }

  public void setMeetingStart(String MeetingStart) {
    this.MeetingStart = MeetingStart;
  }

  public String getMeetingEnd() {
    return MeetingEnd;
  }

  public void setMeetingEnd(String MeetingEnd) {
    this.MeetingEnd = MeetingEnd;
  }

  public String getMeetingPwd() {
    return MeetingPwd;
  }

  public void setMeetingPwd(String MeetingPwd) {
    this.MeetingPwd = MeetingPwd;
  }

  public String getMeetingSIP() {
    return MeetingSIP;
  }

  public void setMeetingSIP(String MeetingSIP) {
    this.MeetingSIP = MeetingSIP;
  }

  public String getMeetingRemark() {
    return MeetingRemark;
  }

  public void setMeetingRemark(String MeetingRemark) {
    this.MeetingRemark = MeetingRemark;
  }

  public String getMeetingParticipants() {
    return MeetingParticipants;
  }

  public void setMeetingParticipants(String MeetingParticipants) {
    this.MeetingParticipants = MeetingParticipants;
  }

  public String getMeetingconfId() {
    return MeetingconfId;
  }

  public void setMeetingconfId(String MeetingconfId) {
    this.MeetingconfId = MeetingconfId;
  }


}
