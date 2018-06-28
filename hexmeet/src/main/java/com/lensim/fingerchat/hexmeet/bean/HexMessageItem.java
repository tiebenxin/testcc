package com.lensim.fingerchat.hexmeet.bean;


import com.lensim.fingerchat.commons.utils.StringUtils;

/**
 * Created by LL130386 on 2017/10/31.
 *
 */

public class HexMessageItem {

  /**
   * 每条消息，必然对应一个会话
   */
//  private final AbstractChat chat;
  /**
   * 消息类型
   */
  private final int messageType;

  /**
   * 分配给对方的sip
   */
  private final String hexMeetSip;

  /**
   * 分配给对方的Hex帐号
   */
  private final String hexUserCode;

  /**
   * 发送者名称
   */
  private final String name;

  /**
   * 会议室名称
   */
  private final String meetingName;

  /**
   * 会议室ID
   */
  private final String meetingID;

  private final String startTime;
  private final String duration;
  private final String confPassword;
  private final String remarks;

  /**
   * 消息id，确保消息的唯一性
   */
  private String packetId;

  public HexMessageItem(int messageType, String hexMeetSip, String hexUserCode, String name, String meetingName, String meetingID,
      String startTime, String duration, String confPassword, String remarks) {
    this.messageType = messageType;
    this.hexMeetSip = hexMeetSip;
    this.hexUserCode = hexUserCode;
    this.name = name;
    this.meetingName = StringUtils.isEmpty(meetingName) || "null".equals(meetingName) ? name : meetingName;
    this.meetingID = meetingID;
    this.startTime = startTime;
    this.duration = duration;
    this.confPassword = confPassword;
    this.remarks = remarks;
//    this.packetId = StanzaIdUtil.newStanzaId();
  }


  public int getMessageType() {
    return messageType;
  }

  public String getHexMeetSip() {
    return hexMeetSip;
  }

  public String getHexUserCode() {
    return hexUserCode;
  }

  public String getName() {
    return name;
  }

  public String getMeetingName() {
    return meetingName;
  }

  public String getMeetingID() {
    return meetingID;
  }

  public String getPacketId() {
    return packetId;
  }


  public String getStartTime() {
    return startTime;
  }

  public String getDuration() {
    return duration;
  }

  public String getConfPassword() {
    return confPassword;
  }

  public String getRemarks() {
    return remarks;
  }
}
