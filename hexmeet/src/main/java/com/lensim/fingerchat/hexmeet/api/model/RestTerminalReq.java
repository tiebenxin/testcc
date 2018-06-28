package com.lensim.fingerchat.hexmeet.api.model;

public class RestTerminalReq
{
   private int id;
   private int userId;
   private String platform;
   private String deviceToken;
   private String deviceSN;
   private String deviceName;
   private String ipAddress;
   private long loginTime;
   private String language;
   private String osVersion;
   private String appVersion;
   private String brand;
   private String description;
   
   public int getId()
   {
      return id;
   }
   public void setId(int id)
   {
      this.id = id;
   }
   public int getUserId()
   {
      return userId;
   }
   public void setUserId(int userId)
   {
      this.userId = userId;
   }
   public String getPlatform()
   {
      return platform;
   }
   public void setPlatform(String platform)
   {
      this.platform = platform;
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
   public long getLoginTime()
   {
      return loginTime;
   }
   public void setLoginTime(long loginTime)
   {
      this.loginTime = loginTime;
   }
   public String getDescription()
   {
      return description;
   }
   public void setDescription(String description)
   {
      this.description = description;
   }
   public String getIpAddress()
   {
      return ipAddress;
   }
   public void setIpAddress(String ipAddress)
   {
      this.ipAddress = ipAddress;
   }
   public String getLanguage()
   {
      return language;
   }
   public void setLanguage(String language)
   {
      this.language = language;
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
   public String getBrand()
   {
      return brand;
   }
   public void setBrand(String brand)
   {
      this.brand = brand;
   }
   @Override
   public String toString()
   {
      return "RestTerminalReq [id=" + id + ", userId=" + userId + ", platform=" + platform + ", deviceToken="
            + deviceToken + ", deviceSN=" + deviceSN + ", deviceName=" + deviceName + ", ipAddress="
            + ipAddress + ", loginTime=" + loginTime + ", language=" + language + ", osVersion=" + osVersion
            + ", appVersion=" + appVersion + ", brand=" + brand + ", description=" + description + "]";
   }
   
}
