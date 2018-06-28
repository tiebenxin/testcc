package com.lensim.fingerchat.hexmeet.api.model;

public class RestTerminal
{
   private int id;
   private String platform;
   private String deviceToken;
   private String deviceSN;
   private String deviceName;
   private long lastLoginTime;
   private String description;
   private String ipAddress;
   private String osVersion;
   private String appVersion;
   
   public int getId()
   {
      return id;
   }
   public void setId(int id)
   {
      this.id = id;
   }
   public String getDeviceToken()
   {
      return deviceToken;
   }
   public void setDeviceToken(String deviceToken)
   {
      this.deviceToken = deviceToken;
   }
   public String getDeviceSN()
   {
      return deviceSN;
   }
   public void setDeviceSN(String deviceSN)
   {
      this.deviceSN = deviceSN;
   }
   public String getDeviceName()
   {
      return deviceName;
   }
   public void setDeviceName(String deviceName)
   {
      this.deviceName = deviceName;
   }
   public String getDescription()
   {
      return description;
   }
   public void setDescription(String description)
   {
      this.description = description;
   }
   public String getPlatform()
   {
      return platform;
   }
   public void setPlatform(String platform)
   {
      this.platform = platform;
   }
   public long getLastLoginTime()
   {
      return lastLoginTime;
   }
   public void setLastLoginTime(long lastLoginTime)
   {
      this.lastLoginTime = lastLoginTime;
   }
   public String getIpAddress()
   {
      return ipAddress;
   }
   public void setIpAddress(String ipAddress)
   {
      this.ipAddress = ipAddress;
   }
   public String getOsVersion()
   {
      return osVersion;
   }
   public void setOsVersion(String osVersion)
   {
      this.osVersion = osVersion;
   }
   public String getAppVersion()
   {
      return appVersion;
   }
   public void setAppVersion(String appVersion)
   {
      this.appVersion = appVersion;
   }
   @Override
   public String toString()
   {
      return "RestTerminal [id=" + id + ", platform=" + platform + ", deviceToken=" + deviceToken
            + ", deviceSN=" + deviceSN + ", deviceName=" + deviceName + ", lastLoginTime=" + lastLoginTime
            + ", description=" + description + ", ipAddress=" + ipAddress + ", osVersion=" + osVersion
            + ", appVersion=" + appVersion + "]";
   }
   
}
