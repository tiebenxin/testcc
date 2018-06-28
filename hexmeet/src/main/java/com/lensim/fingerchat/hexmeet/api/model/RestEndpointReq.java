package com.lensim.fingerchat.hexmeet.api.model;

public class RestEndpointReq
{
   private int id;
   private String type;
   private String ip;
   private String systemName;
   private String deviceName;
   private String description;
   private String serialNumber;
   private String sipUrl;
   private String deviceStatus;
   private Integer unitId;
   private Integer orgId;
   private Integer callSpeed;
   private Integer userCapacity;
   private Boolean master = true;
   private String e164;
   private String callType;
   private String adminId;
   private String adminPassword;
   
   public int getId()
   {
      return id;
   }
   public void setId(int id)
   {
      this.id = id;
   }
   public String getType()
   {
      return type;
   }
   public void setType(String type)
   {
      this.type = type;
   }
   public String getIp()
   {
      return ip;
   }
   public void setIp(String ip)
   {
      this.ip = ip;
   }
   public String getSystemName()
   {
      return systemName;
   }
   public void setSystemName(String systemName)
   {
      this.systemName = systemName;
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
   public String getSerialNumber()
   {
      return serialNumber;
   }
   public void setSerialNumber(String serialNumber)
   {
      this.serialNumber = serialNumber;
   }
   public String getSipUrl()
   {
      return sipUrl;
   }
   public void setSipUrl(String sipUrl)
   {
      this.sipUrl = sipUrl;
   }
   public String getDeviceStatus()
   {
      return deviceStatus;
   }
   public void setDeviceStatus(String deviceStatus)
   {
      this.deviceStatus = deviceStatus;
   }
   public Integer getUnitId()
   {
      return unitId;
   }
   public void setUnitId(Integer unitId)
   {
      this.unitId = unitId;
   }
   public Integer getOrgId()
   {
      return orgId;
   }
   public void setOrgId(Integer orgId)
   {
      this.orgId = orgId;
   }
   public Integer getCallSpeed()
   {
      return callSpeed;
   }
   public void setCallSpeed(Integer callSpeed)
   {
      this.callSpeed = callSpeed;
   }
   public Integer getUserCapacity()
   {
      return userCapacity;
   }
   public void setUserCapacity(Integer userCapacity)
   {
      this.userCapacity = userCapacity;
   }
   public Boolean getMaster()
   {
      return master;
   }
   public void setMaster(Boolean master)
   {
      this.master = master;
   }
   public String getE164()
   {
      return e164;
   }
   public void setE164(String e164)
   {
      this.e164 = e164;
   }
   public String getCallType()
   {
      return callType;
   }
   public void setCallType(String callType)
   {
      this.callType = callType;
   }
   public String getAdminId()
   {
      return adminId;
   }
   public void setAdminId(String adminId)
   {
      this.adminId = adminId;
   }
   public String getAdminPassword()
   {
      return adminPassword;
   }
   public void setAdminPassword(String adminPassword)
   {
      this.adminPassword = adminPassword;
   }
}
