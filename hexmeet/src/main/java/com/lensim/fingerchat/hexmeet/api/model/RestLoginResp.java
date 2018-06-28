package com.lensim.fingerchat.hexmeet.api.model;

public class RestLoginResp
{
   private String token;
   private RestUser profile;
   private RestTerminalProfile terminalProfile;
   public String getToken()
   {
      return token;
   }
   public void setToken(String token)
   {
      this.token = token;
   }
   public RestUser getProfile()
   {
      return profile;
   }
   public void setProfile(RestUser profile)
   {
      this.profile = profile;
   }
   public RestTerminalProfile getTerminalProfile()
   {
      return terminalProfile;
   }
   public void setTerminalProfile(RestTerminalProfile terminalProfile)
   {
      this.terminalProfile = terminalProfile;
   }

   @Override
   public String toString()
   {
      return "RestLoginResp [token=" + token + ", profile=" + profile + ", terminalProfile="
            + terminalProfile + "]";
   }
}
