package com.lensim.fingerchat.hexmeet.api.model;

import java.util.List;

public class RestMeetingReq
{
   private int id;
   private String name;
   private Long startTime;
   private Long duration;
   private Integer masterEndpointId;
   private String status;
   private List<Integer> endpointIds;
   private List<Integer> pollingEndpointIds;
   private Integer groupId;
   private List<Integer> contactIds;
   private String remarks;
   private Integer unitId;
   private Integer departmentId;
   private String contact;
   private String contactPhone;
   private Integer maxBandwidth;
   private String layout;
   private Boolean enableRecording;
   private Boolean autoRedialing;
   private String confPassword;
   private Integer numericId;
   private String recurrenceInterval;// DAILY,WEEKLY,MONTHLY
   private int recurrenceTimes;
   private boolean includeWeekend;
   private String statusInfo;
   private String confType = "VISEE";

   private Boolean messageOverlayEnabled;
   private String messageOverlayContent;
   private Integer messageOverlayDisplayDuration;
   private Integer messageOverlayTransparency;
   private Boolean messageOverlayReset;
   private String messageOverlaySpeed;
   private String messageOverlayColor;
   private String messageOverlayFontSize;
   private String messageOverlayPosition;

   public int getId()
   {
      return id;
   }
   public void setId(int id)
   {
      this.id = id;
   }
   public String getName()
   {
      return name;
   }
   public void setName(String name)
   {
      this.name = name;
   }
   public Long getStartTime()
   {
      return startTime;
   }
   public void setStartTime(Long startTime)
   {
      this.startTime = startTime;
   }
   public Long getDuration()
   {
      return duration;
   }
   public void setDuration(Long duration)
   {
      this.duration = duration;
   }
   public Integer getMasterEndpointId()
   {
      return masterEndpointId;
   }
   public void setMasterEndpointId(Integer masterEndpointId)
   {
      this.masterEndpointId = masterEndpointId;
   }
   public String getStatus()
   {
      return status;
   }
   public void setStatus(String status)
   {
      this.status = status;
   }
   public List<Integer> getEndpointIds()
   {
      return endpointIds;
   }
   public void setEndpointIds(List<Integer> endpointIds)
   {
      this.endpointIds = endpointIds;
   }
   public List<Integer> getPollingEndpointIds()
   {
      return pollingEndpointIds;
   }
   public void setPollingEndpointIds(List<Integer> pollingEndpointIds)
   {
      this.pollingEndpointIds = pollingEndpointIds;
   }
   public List<Integer> getContactIds()
   {
      return contactIds;
   }
   public void setContactIds(List<Integer> contactIds)
   {
      this.contactIds = contactIds;
   }
   public String getRemarks()
   {
      return remarks;
   }
   public void setRemarks(String remarks)
   {
      this.remarks = remarks;
   }
   public Integer getUnitId()
   {
      return unitId;
   }
   public void setUnitId(Integer unitId)
   {
      this.unitId = unitId;
   }
   public Integer getDepartmentId()
   {
      return departmentId;
   }
   public void setDepartmentId(Integer departmentId)
   {
      this.departmentId = departmentId;
   }
   public String getContact()
   {
      return contact;
   }
   public void setContact(String contact)
   {
      this.contact = contact;
   }
   public String getContactPhone()
   {
      return contactPhone;
   }
   public void setContactPhone(String contactPhone)
   {
      this.contactPhone = contactPhone;
   }
   public Integer getMaxBandwidth()
   {
      return maxBandwidth;
   }
   public void setMaxBandwidth(Integer maxBandwidth)
   {
      this.maxBandwidth = maxBandwidth;
   }
   public String getLayout()
   {
      return layout;
   }
   public void setLayout(String layout)
   {
      this.layout = layout;
   }
   public Boolean getEnableRecording()
   {
      return enableRecording;
   }
   public void setEnableRecording(Boolean enableRecording)
   {
      this.enableRecording = enableRecording;
   }
   public Boolean getAutoRedialing()
   {
      return autoRedialing;
   }
   public void setAutoRedialing(Boolean autoRedialing)
   {
      this.autoRedialing = autoRedialing;
   }
   public String getConfPassword()
   {
      return confPassword;
   }
   public void setConfPassword(String confPassword)
   {
      this.confPassword = confPassword;
   }
   public Integer getNumericId()
   {
      return numericId;
   }
   public void setNumericId(Integer numericId)
   {
      this.numericId = numericId;
   }
   public String getRecurrenceInterval()
   {
      return recurrenceInterval;
   }
   public void setRecurrenceInterval(String recurrenceInterval)
   {
      this.recurrenceInterval = recurrenceInterval;
   }
   public int getRecurrenceTimes()
   {
      return recurrenceTimes;
   }
   public void setRecurrenceTimes(int recurrenceTimes)
   {
      this.recurrenceTimes = recurrenceTimes;
   }
   public boolean isIncludeWeekend()
   {
      return includeWeekend;
   }
   public void setIncludeWeekend(boolean includeWeekend)
   {
      this.includeWeekend = includeWeekend;
   }
   public String getStatusInfo()
   {
      return statusInfo;
   }
   public void setStatusInfo(String statusInfo)
   {
      this.statusInfo = statusInfo;
   }
   public Boolean getMessageOverlayEnabled()
   {
      return messageOverlayEnabled;
   }
   public void setMessageOverlayEnabled(Boolean messageOverlayEnabled)
   {
      this.messageOverlayEnabled = messageOverlayEnabled;
   }
   public String getMessageOverlayContent()
   {
      return messageOverlayContent;
   }
   public void setMessageOverlayContent(String messageOverlayContent)
   {
      this.messageOverlayContent = messageOverlayContent;
   }
   public Integer getMessageOverlayDisplayDuration()
   {
      return messageOverlayDisplayDuration;
   }
   public void setMessageOverlayDisplayDuration(Integer messageOverlayDisplayDuration)
   {
      this.messageOverlayDisplayDuration = messageOverlayDisplayDuration;
   }
   public Integer getMessageOverlayTransparency()
   {
      return messageOverlayTransparency;
   }
   public void setMessageOverlayTransparency(Integer messageOverlayTransparency)
   {
      this.messageOverlayTransparency = messageOverlayTransparency;
   }
   public Boolean getMessageOverlayReset()
   {
      return messageOverlayReset;
   }
   public void setMessageOverlayReset(Boolean messageOverlayReset)
   {
      this.messageOverlayReset = messageOverlayReset;
   }
   public String getMessageOverlaySpeed()
   {
      return messageOverlaySpeed;
   }
   public void setMessageOverlaySpeed(String messageOverlaySpeed)
   {
      this.messageOverlaySpeed = messageOverlaySpeed;
   }
   public String getMessageOverlayColor()
   {
      return messageOverlayColor;
   }
   public void setMessageOverlayColor(String messageOverlayColor)
   {
      this.messageOverlayColor = messageOverlayColor;
   }
   public String getMessageOverlayFontSize()
   {
      return messageOverlayFontSize;
   }
   public void setMessageOverlayFontSize(String messageOverlayFontSize)
   {
      this.messageOverlayFontSize = messageOverlayFontSize;
   }
   public String getMessageOverlayPosition()
   {
      return messageOverlayPosition;
   }
   public void setMessageOverlayPosition(String messageOverlayPosition)
   {
      this.messageOverlayPosition = messageOverlayPosition;
   }
   public Integer getGroupId()
   {
      return groupId;
   }
   public void setGroupId(Integer groupId)
   {
      this.groupId = groupId;
   }
   public String getConfType()
   {
      return confType;
   }
   public void setConfType(String confType)
   {
      this.confType = confType;
   }
   @Override
   public String toString()
   {
      return "RestMeetingReq [id=" + id + ", name=" + name + ", startTime=" + startTime + ", duration="
            + duration + ", masterEndpointId=" + masterEndpointId + ", status=" + status + ", endpointIds="
            + endpointIds + ", pollingEndpointIds=" + pollingEndpointIds + ", groupId=" + groupId
            + ", contactIds=" + contactIds + ", remarks=" + remarks + ", unitId=" + unitId
            + ", departmentId=" + departmentId + ", contact=" + contact + ", contactPhone=" + contactPhone
            + ", maxBandwidth=" + maxBandwidth + ", layout=" + layout + ", enableRecording="
            + enableRecording + ", autoRedialing=" + autoRedialing + ", confPassword=" + confPassword
            + ", numericId=" + numericId + ", recurrenceInterval=" + recurrenceInterval
            + ", recurrenceTimes=" + recurrenceTimes + ", includeWeekend=" + includeWeekend + ", statusInfo="
            + statusInfo + ", confType=" + confType + ", messageOverlayEnabled=" + messageOverlayEnabled
            + ", messageOverlayContent=" + messageOverlayContent + ", messageOverlayDisplayDuration="
            + messageOverlayDisplayDuration + ", messageOverlayTransparency=" + messageOverlayTransparency
            + ", messageOverlayReset=" + messageOverlayReset + ", messageOverlaySpeed=" + messageOverlaySpeed
            + ", messageOverlayColor=" + messageOverlayColor + ", messageOverlayFontSize="
            + messageOverlayFontSize + ", messageOverlayPosition=" + messageOverlayPosition + "]";
   }
}
