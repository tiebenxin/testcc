package com.lensim.fingerchat.hexmeet.api.model;

public class RestParticipant
{
   private String name;
   private String sipUserName;
   private boolean videoMode;
   private int userId;
   
   public String getName()
   {
      return name;
   }
   public void setName(String name)
   {
      this.name = name;
   }
   public String getSipUserName()
   {
      return sipUserName;
   }
   public void setSipUserName(String sipUserName)
   {
      this.sipUserName = sipUserName;
   }
   public boolean isVideoMode()
   {
      return videoMode;
   }
   public void setVideoMode(boolean videoMode)
   {
      this.videoMode = videoMode;
   }
   public int getUserId()
   {
      return userId;
   }
   public void setUserId(int userId)
   {
      this.userId = userId;
   }
   @Override
   public String toString()
   {
      return "RestParticipant [name=" + name + ", sipUserName=" + sipUserName + ", videoMode=" + videoMode
            + ", userId=" + userId + "]";
   }
   
}
