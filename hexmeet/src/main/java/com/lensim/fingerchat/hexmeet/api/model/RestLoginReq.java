package com.lensim.fingerchat.hexmeet.api.model;

public class RestLoginReq
{
   private String account;
   private String password;
   private String deviceSN;
   private String deviceType;
   private String tempToken;
   private String language;
   
   public String getAccount()
   {
      return account;
   }
   public void setAccount(String account)
   {
      this.account = account;
   }
   public String getPassword()
   {
      return password;
   }
   public void setPassword(String password)
   {
      this.password = password;
   }
   public String getDeviceSN()
   {
      return deviceSN;
   }
   public void setDeviceSN(String deviceSN)
   {
      this.deviceSN = deviceSN;
   }
   public String getDeviceType()
   {
      return deviceType;
   }
   public void setDeviceType(String deviceType)
   {
      this.deviceType = deviceType;
   }
   public String getTempToken()
   {
      return tempToken;
   }
   public void setTempToken(String tempToken)
   {
      this.tempToken = tempToken;
   }
   public String getLanguage()
   {
      return language;
   }
   public void setLanguage(String language)
   {
      this.language = language;
   }
   @Override
   public String toString()
   {
      return "RestLoginReq [account=" + account + ", password=" + password + ", deviceSN=" + deviceSN
            + ", deviceType=" + deviceType + ", tempToken=" + tempToken + ", language=" + language + "]";
   }
   
}
