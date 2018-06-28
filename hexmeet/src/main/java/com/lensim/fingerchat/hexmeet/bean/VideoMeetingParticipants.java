package com.lensim.fingerchat.hexmeet.bean;

public class VideoMeetingParticipants {

  /// 与会者
  /// 飞鸽 userid
  private String participants;
  /// 与会者昵称
  private String nickname;
  /// 与会者头像
  private String headPortrait;

  public VideoMeetingParticipants(String participants, String nickname, String headPortrait) {
    this.participants = participants;
    this.nickname = nickname;
    this.headPortrait = headPortrait;
  }

  public String getParticipants() {
    return participants;
  }

  public void setParticipants(String participants) {
    this.participants = participants;
  }

  public String getNickname() {
    return nickname;
  }

  public void setNickname(String nickname) {
    this.nickname = nickname;
  }

  public String getHeadPortrait() {
    return headPortrait;
  }

  public void setHeadPortrait(String headPortrait) {
    this.headPortrait = headPortrait;
  }
}