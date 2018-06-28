package com.lensim.fingerchat.hexmeet.api.model;

public class RestUserReq
{
   private int id;
   private String name;
   private String password;
   private String email;
   private String displayName;
   private String description;
   private String telephone;
   private String cellphone;
   private boolean systemManager;
   private String originalPass;
   private String vmr;
   private String sipUserName;
   private String sipAuthName;
   private String sipPassword;
   private int status;
   
   private String newPassword;
   
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
   public String getPassword()
   {
      return password;
   }
   public void setPassword(String password)
   {
      this.password = password;
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
   public String getDescription()
   {
      return description;
   }
   public void setDescription(String description)
   {
      this.description = description;
   }
   public String getDisplayName()
   {
      return displayName;
   }
   public void setDisplayName(String displayName)
   {
      this.displayName = displayName;
   }
   public boolean isSystemManager()
   {
      return systemManager;
   }
   public void setSystemManager(boolean systemManager)
   {
      this.systemManager = systemManager;
   }
   public String getOriginalPass()
   {
      return originalPass;
   }
   public void setOriginalPass(String originalPass)
   {
      this.originalPass = originalPass;
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
   public String getNewPassword()
   {
      return newPassword;
   }
   public void setNewPassword(String newPassword)
   {
      this.newPassword = newPassword;
   }
   public int getStatus()
   {
      return status;
   }
   public void setStatus(int status)
   {
      this.status = status;
   }
   @Override
   public String toString()
   {
      return "RestUserReq [id=" + id + ", name=" + name + ", password=" + password + ", email=" + email
            + ", displayName=" + displayName + ", description=" + description + ", telephone=" + telephone
            + ", cellphone=" + cellphone + ", systemManager=" + systemManager + ", originalPass="
            + originalPass + ", vmr=" + vmr + ", sipUserName=" + sipUserName + ", sipAuthName=" + sipAuthName
            + ", sipPassword=" + sipPassword + ", status=" + status + ", newPassword=" + newPassword + "]";
   }
   
}