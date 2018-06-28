package com.lensim.fingerchat.hexmeet.api.model;

import java.io.Serializable;
import java.util.List;

public class RestMeeting implements Serializable {

  public static final int DEFAULT_CONF_ID = -1;
  private int id;
  private int FGMeetingId = DEFAULT_CONF_ID;//Hex会议ID
  private String name;
  private RestUser applicant;
  private long startTime;
  private long duration;
  private int masterEndpointId;
  private String masterEndpointName;
  private String layout;
  private String status;
  private boolean autoRedialing;
  private List<RestEndpoint> endpoints;
  private List<RestContact> contacts;
  private List<RestUser> users;
  private String confPassword;
  private int numericId;
  private boolean confTemplate;
  private boolean privateTemplate;
  private String statusInfo;
  private int groupId;
  private String groupName;
  private String confType;

  private String remarks;
  private int unitId;
  private String unitName;
  private int departmentId;
  private String departName;
  private String contact;
  private String contactPhone;
  private int maxBandwidth;
  private boolean enableRecording;

  private boolean messageOverlayEnabled;
  private String messageOverlayContent;
  private int messageOverlayDisplayDuration;
  private int messageOverlayTransparency;
  private String messageOverlaySpeed;
  private String messageOverlayColor;
  private String messageOverlayFontSize;
  private String messageOverlayPosition;

  private long lastModifiedTime;

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public RestUser getApplicant() {
    return applicant;
  }

  public void setApplicant(RestUser applicant) {
    this.applicant = applicant;
  }

  public long getStartTime() {
    return startTime;
  }

  public void setStartTime(long startTime) {
    this.startTime = startTime;
  }

  public long getDuration() {
    return duration;
  }

  public void setDuration(long duration) {
    this.duration = duration;
  }

  public int getMasterEndpointId() {
    return masterEndpointId;
  }

  public void setMasterEndpointId(int masterEndpointId) {
    this.masterEndpointId = masterEndpointId;
  }

  public String getMasterEndpointName() {
    return masterEndpointName;
  }

  public void setMasterEndpointName(String masterEndpointName) {
    this.masterEndpointName = masterEndpointName;
  }

  public String getLayout() {
    return layout;
  }

  public void setLayout(String layout) {
    this.layout = layout;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public boolean isAutoRedialing() {
    return autoRedialing;
  }

  public void setAutoRedialing(boolean autoRedialing) {
    this.autoRedialing = autoRedialing;
  }

  public List<RestEndpoint> getEndpoints() {
    return endpoints;
  }

  public void setEndpoints(List<RestEndpoint> endpoints) {
    this.endpoints = endpoints;
  }

  public List<RestContact> getContacts() {
    return contacts;
  }

  public void setContacts(List<RestContact> contacts) {
    this.contacts = contacts;
  }

  public List<RestUser> getUsers() {
    return users;
  }

  public void setUsers(List<RestUser> users) {
    this.users = users;
  }

  public String getRemarks() {
    return remarks;
  }

  public void setRemarks(String remarks) {
    this.remarks = remarks;
  }

  public int getUnitId() {
    return unitId;
  }

  public void setUnitId(int unitId) {
    this.unitId = unitId;
  }

  public int getDepartmentId() {
    return departmentId;
  }

  public void setDepartmentId(int departmentId) {
    this.departmentId = departmentId;
  }

  public String getContact() {
    return contact;
  }

  public void setContact(String contact) {
    this.contact = contact;
  }

  public String getContactPhone() {
    return contactPhone;
  }

  public void setContactPhone(String contactPhone) {
    this.contactPhone = contactPhone;
  }

  public int getMaxBandwidth() {
    return maxBandwidth;
  }

  public void setMaxBandwidth(int maxBandwidth) {
    this.maxBandwidth = maxBandwidth;
  }

  public boolean isEnableRecording() {
    return enableRecording;
  }

  public void setEnableRecording(boolean enableRecording) {
    this.enableRecording = enableRecording;
  }

  public boolean isMessageOverlayEnabled() {
    return messageOverlayEnabled;
  }

  public void setMessageOverlayEnabled(boolean messageOverlayEnabled) {
    this.messageOverlayEnabled = messageOverlayEnabled;
  }

  public String getMessageOverlayContent() {
    return messageOverlayContent;
  }

  public void setMessageOverlayContent(String messageOverlayContent) {
    this.messageOverlayContent = messageOverlayContent;
  }

  public int getMessageOverlayDisplayDuration() {
    return messageOverlayDisplayDuration;
  }

  public void setMessageOverlayDisplayDuration(int messageOverlayDisplayDuration) {
    this.messageOverlayDisplayDuration = messageOverlayDisplayDuration;
  }

  public int getMessageOverlayTransparency() {
    return messageOverlayTransparency;
  }

  public void setMessageOverlayTransparency(int messageOverlayTransparency) {
    this.messageOverlayTransparency = messageOverlayTransparency;
  }

  public String getMessageOverlaySpeed() {
    return messageOverlaySpeed;
  }

  public void setMessageOverlaySpeed(String messageOverlaySpeed) {
    this.messageOverlaySpeed = messageOverlaySpeed;
  }

  public String getMessageOverlayColor() {
    return messageOverlayColor;
  }

  public void setMessageOverlayColor(String messageOverlayColor) {
    this.messageOverlayColor = messageOverlayColor;
  }

  public String getMessageOverlayFontSize() {
    return messageOverlayFontSize;
  }

  public void setMessageOverlayFontSize(String messageOverlayFontSize) {
    this.messageOverlayFontSize = messageOverlayFontSize;
  }

  public String getMessageOverlayPosition() {
    return messageOverlayPosition;
  }

  public void setMessageOverlayPosition(String messageOverlayPosition) {
    this.messageOverlayPosition = messageOverlayPosition;
  }

  public String getUnitName() {
    return unitName;
  }

  public void setUnitName(String unitName) {
    this.unitName = unitName;
  }

  public String getDepartName() {
    return departName;
  }

  public void setDepartName(String departName) {
    this.departName = departName;
  }

  public String getConfPassword() {
    return confPassword;
  }

  public void setConfPassword(String confPassword) {
    this.confPassword = confPassword;
  }

  public int getNumericId() {
    return numericId;
  }

  public void setNumericId(int numericId) {
    this.numericId = numericId;
  }

  public boolean isConfTemplate() {
    return confTemplate;
  }

  public void setConfTemplate(boolean confTemplate) {
    this.confTemplate = confTemplate;
  }

  public boolean isPrivateTemplate() {
    return privateTemplate;
  }

  public void setPrivateTemplate(boolean privateTemplate) {
    this.privateTemplate = privateTemplate;
  }

  public String getStatusInfo() {
    return statusInfo;
  }

  public void setStatusInfo(String statusInfo) {
    this.statusInfo = statusInfo;
  }

  public int getGroupId() {
    return groupId;
  }

  public void setGroupId(int groupId) {
    this.groupId = groupId;
  }

  public String getGroupName() {
    return groupName;
  }

  public void setGroupName(String groupName) {
    this.groupName = groupName;
  }

  public String getConfType() {
    return confType;
  }

  public void setConfType(String confType) {
    this.confType = confType;
  }

  public long getLastModifiedTime() {
    return lastModifiedTime;
  }

  public void setLastModifiedTime(long lastModifiedTime) {
    this.lastModifiedTime = lastModifiedTime;
  }

  public int getFGMeetingId() {
    return FGMeetingId;
  }

  public void setFGMeetingId(int FGMeetingId) {
    this.FGMeetingId = FGMeetingId;
  }

  @Override
  public String toString() {
    return "RestMeeting [id=" + id + ", FGMeetingId=" + FGMeetingId + ", name=" + name + ", applicant=" + applicant + ", startTime="
        + startTime + ", duration=" + duration + ", masterEndpointId=" + masterEndpointId
        + ", masterEndpointName=" + masterEndpointName + ", layout=" + layout + ", status=" + status
        + ", autoRedialing=" + autoRedialing + ", endpoints=" + endpoints + ", contacts=" + contacts
        + ", users=" + users + ", confPassword=" + confPassword + ", numericId=" + numericId
        + ", confTemplate=" + confTemplate + ", privateTemplate=" + privateTemplate + ", statusInfo="
        + statusInfo + ", groupId=" + groupId + ", groupName=" + groupName + ", confType=" + confType
        + ", remarks=" + remarks + ", unitId=" + unitId + ", unitName=" + unitName + ", departmentId="
        + departmentId + ", departName=" + departName + ", contact=" + contact + ", contactPhone="
        + contactPhone + ", maxBandwidth=" + maxBandwidth + ", enableRecording=" + enableRecording
        + ", messageOverlayEnabled=" + messageOverlayEnabled + ", messageOverlayContent="
        + messageOverlayContent + ", messageOverlayDisplayDuration=" + messageOverlayDisplayDuration
        + ", messageOverlayTransparency=" + messageOverlayTransparency + ", messageOverlaySpeed="
        + messageOverlaySpeed + ", messageOverlayColor=" + messageOverlayColor
        + ", messageOverlayFontSize=" + messageOverlayFontSize + ", messageOverlayPosition="
        + messageOverlayPosition + ", lastModifiedTime=" + lastModifiedTime + "]";
  }


}
