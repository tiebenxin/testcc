package com.lensim.fingerchat.hexmeet.api.model;

import java.io.Serializable;

public class RestEndpoint implements Serializable
{
   /**
    * 
    */
   private static final long serialVersionUID = 4463807144090519988L;
   private int id;
   private String type;
   private String name;
   private String ip;
   private boolean available;
   private int orgId;
   private String orgName;
   private int unitId;
   private String unitName;
   private boolean inPrimaryVS;
   private int primaryVSSortIndex;
   private boolean inSecondaryVS;
   private int secondaryVSSortIndex;
   private int sortIndex;
   private String e164;
   private String callType;
   private String callNumber;
   private String outwardType;
   private boolean master;
   private String sipUrl;
   private String deviceStatus;

   private String adminId;
   private String adminPassword;
   private String deviceName;
   private String description;
   private String serialNumber;
   private String softwareVersion;
   private int callSpeed;
   private String contact;
   private String contactEmail;
   private String contactPhone;
   private int userCapacity;
   private String imageURL;

   public String getUnitName()
   {
      return unitName;
   }

   public void setUnitName(String unitName)
   {
      this.unitName = unitName;
   }

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

   public String getName()
   {
      return name;
   }

   public void setName(String name)
   {
      this.name = name;
   }

   public String getIp()
   {
      return ip;
   }

   public void setIp(String ip)
   {
      this.ip = ip;
   }

   public boolean isAvailable()
   {
      return available;
   }

   public void setAvailable(boolean available)
   {
      this.available = available;
   }

   public int getOrgId()
   {
      return orgId;
   }

   public void setOrgId(int orgId)
   {
      this.orgId = orgId;
   }

   public String getOrgName()
   {
      return orgName;
   }

   public void setOrgName(String orgName)
   {
      this.orgName = orgName;
   }

   public int getUnitId()
   {
      return unitId;
   }

   public void setUnitId(int unitId)
   {
      this.unitId = unitId;
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

   public String getCallNumber()
   {
      return callNumber;
   }

   public void setCallNumber(String callNumber)
   {
      this.callNumber = callNumber;
   }

   public String getOutwardType()
   {
      return outwardType;
   }

   public void setOutwardType(String outwardType)
   {
      this.outwardType = outwardType;
   }

   public boolean isMaster()
   {
      return master;
   }

   public void setMaster(boolean master)
   {
      this.master = master;
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

   public String getSoftwareVersion()
   {
      return softwareVersion;
   }

   public void setSoftwareVersion(String softwareVersion)
   {
      this.softwareVersion = softwareVersion;
   }

   public int getCallSpeed()
   {
      return callSpeed;
   }

   public void setCallSpeed(int callSpeed)
   {
      this.callSpeed = callSpeed;
   }

   public String getContact()
   {
      return contact;
   }

   public void setContact(String contact)
   {
      this.contact = contact;
   }

   public String getContactEmail()
   {
      return contactEmail;
   }

   public void setContactEmail(String contactEmail)
   {
      this.contactEmail = contactEmail;
   }

   public String getContactPhone()
   {
      return contactPhone;
   }

   public void setContactPhone(String contactPhone)
   {
      this.contactPhone = contactPhone;
   }

   public int getUserCapacity()
   {
      return userCapacity;
   }

   public void setUserCapacity(int userCapacity)
   {
      this.userCapacity = userCapacity;
   }

   public String getImageURL()
   {
      return imageURL;
   }

   public void setImageURL(String imageURL)
   {
      this.imageURL = imageURL;
   }

   public boolean isInPrimaryVS()
   {
      return inPrimaryVS;
   }

   public void setInPrimaryVS(boolean inPrimaryVS)
   {
      this.inPrimaryVS = inPrimaryVS;
   }

   public int getPrimaryVSSortIndex()
   {
      return primaryVSSortIndex;
   }

   public void setPrimaryVSSortIndex(int primaryVSSortIndex)
   {
      this.primaryVSSortIndex = primaryVSSortIndex;
   }

   public boolean isInSecondaryVS()
   {
      return inSecondaryVS;
   }

   public void setInSecondaryVS(boolean inSecondaryVS)
   {
      this.inSecondaryVS = inSecondaryVS;
   }

   public int getSecondaryVSSortIndex()
   {
      return secondaryVSSortIndex;
   }

   public void setSecondaryVSSortIndex(int secondaryVSSortIndex)
   {
      this.secondaryVSSortIndex = secondaryVSSortIndex;
   }

   public int getSortIndex()
   {
      return sortIndex;
   }

   public void setSortIndex(int sortIndex)
   {
      this.sortIndex = sortIndex;
   }
}