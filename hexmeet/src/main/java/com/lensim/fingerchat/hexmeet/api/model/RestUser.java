package com.lensim.fingerchat.hexmeet.api.model;

import java.io.Serializable;

public class RestUser implements Serializable
{
   private static final long serialVersionUID = 1L;
   private int id;
   private String name;
   private String password;
   private String displayName;
   private String email;
   private String telephone;
   private String cellphone;
   private String h323ConfNumber;
   private String sipConfNumber;
   private String pstn;
   private String description;
   private String callNumber;
   private String imageURL;
   private long lastModifiedTime;
   private boolean systemmanager;
   private String vmr;
   private String sipUserName;
   private String sipAuthName;
   private String sipPassword;
   private int status;
   private boolean passwordModifiedByUser = false;
   
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
   public String getDisplayName()
   {
      return displayName;
   }
   public void setDisplayName(String displayName)
   {
      this.displayName = displayName;
   }
   public String getEmail()
   {
      return email;
   }
   public void setEmail(String email)
   {
      this.email = email;
   }
   public String getTelephone()
   {
      return telephone;
   }
   public void setTelephone(String telephone)
   {
      this.telephone = telephone;
   }
   public String getCellphone()
   {
      return cellphone;
   }
   public void setCellphone(String cellphone)
   {
      this.cellphone = cellphone;
   }
   public String getH323ConfNumber()
   {
      return h323ConfNumber;
   }
   public void setH323ConfNumber(String confNumber)
   {
      h323ConfNumber = confNumber;
   }
   public String getPstn()
   {
      return pstn;
   }
   public void setPstn(String pstn)
   {
      this.pstn = pstn;
   }
   public String getDescription()
   {
      return description;
   }
   public void setDescription(String description)
   {
      this.description = description;
   }
   public String getSipConfNumber()
   {
      return sipConfNumber;
   }
   public void setSipConfNumber(String sipConfNumber)
   {
      this.sipConfNumber = sipConfNumber;
   }
   public String getCallNumber()
   {
      return callNumber;
   }
   public void setCallNumber(String callNumber)
   {
      this.callNumber = callNumber;
   }
   public String getImageURL()
   {
      return imageURL;
   }
   public void setImageURL(String imageURL)
   {
      this.imageURL = imageURL;
   }
   public long getLastModifiedTime()
   {
      return lastModifiedTime;
   }
   public void setLastModifiedTime(long lastModifiedTime)
   {
      this.lastModifiedTime = lastModifiedTime;
   }
   
   public boolean isSystemmanager()
   {
      return systemmanager;
   }
   public void setSystemmanager(boolean systemmanager)
   {
      this.systemmanager = systemmanager;
   }
   public String getPassword()
   {
      return password;
   }
   public void setPassword(String password)
   {
      this.password = password;
   }
   public String getVmr()
   {
      return vmr;
   }
   public void setVmr(String vmr)
   {
      this.vmr = vmr;
   }
   public String getSipUserName()
   {
      return sipUserName;
   }
   public void setSipUserName(String sipUserName)
   {
      this.sipUserName = sipUserName;
   }
   public String getSipAuthName()
   {
      return sipAuthName;
   }
   public void setSipAuthName(String sipAuthName)
   {
      this.sipAuthName = sipAuthName;
   }
   public String getSipPassword()
   {
      return sipPassword;
   }
   public void setSipPassword(String sipPassword)
   {
      this.sipPassword = sipPassword;
   }
   public int getStatus()
   {
      return status;
   }
   public void setStatus(int status)
   {
      this.status = status;
   }
   public boolean isPasswordModifiedByUser()
   {
      return passwordModifiedByUser;
   }
   public void setPasswordModifiedByUser(boolean passwordModifiedByUser)
   {
      this.passwordModifiedByUser = passwordModifiedByUser;
   }
   @Override
   public String toString()
   {
      return "RestUser [id=" + id + ", name=" + name + ", password=" + password + ", displayName="
            + displayName + ", email=" + email + ", telephone=" + telephone + ", cellphone=" + cellphone
            + ", h323ConfNumber=" + h323ConfNumber + ", sipConfNumber=" + sipConfNumber + ", pstn=" + pstn
            + ", description=" + description + ", callNumber=" + callNumber + ", imageURL=" + imageURL
            + ", lastModifiedTime=" + lastModifiedTime + ", systemmanager=" + systemmanager + ", vmr=" + vmr
            + ", sipUserName=" + sipUserName + ", sipAuthName=" + sipAuthName + ", sipPassword="
            + sipPassword + ", status=" + status + ", passwordModifiedByUser=" + passwordModifiedByUser + "]";
   }


}
