package com.lensim.fingerchat.hexmeet.api.model;

public class RestTerminalProfile
{
   private String vmr;
   private String sipUserName;
   private String sipAuthorizationName;
   private String sipPassword;

   private String internalSipServer;
   private String internalSipProtocol;
   private String externalSipServer;
   private String externalSipProtocol;

   private boolean p2pCallOverMcu = true;
   private String minimumViSeeVersion = "1.0.0";
   private int heartbeatTimeout = 30;

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
   
   public String getAuthorizationName()
   {
      return sipAuthorizationName;
   }

   public void setAuthorizationName(String sipAuthorizationName)
   {
      this.sipAuthorizationName = sipAuthorizationName;
   }

   public String getSipPassword()
   {
      return sipPassword;
   }

   public void setSipPassword(String sipPassword)
   {
      this.sipPassword = sipPassword;
   }

   public String getInternalSipServer()
   {
      return internalSipServer;
   }

   public void setInternalSipServer(String internalSipServer)
   {
      this.internalSipServer = internalSipServer;
   }

   public String getInternalSipProtocol()
   {
      return internalSipProtocol;
   }

   public void setInternalSipProtocol(String internalSipProtocol)
   {
      this.internalSipProtocol = internalSipProtocol;
   }

   public String getExternalSipServer()
   {
      return externalSipServer;
   }

   public void setExternalSipServer(String externalSipServer)
   {
      this.externalSipServer = externalSipServer;
   }

   public String getExternalSipProtocol()
   {
      return externalSipProtocol;
   }

   public void setExternalSipProtocol(String externalSipProtocol)
   {
      this.externalSipProtocol = externalSipProtocol;
   }

   public boolean isP2pCallOverMcu()
   {
      return p2pCallOverMcu;
   }

   public void setP2pCallOverMcu(boolean p2pCallOverMcu)
   {
      this.p2pCallOverMcu = p2pCallOverMcu;
   }

   public String getMinimumViSeeVersion()
   {
      return minimumViSeeVersion;
   }

   public void setMinimumViSeeVersion(String minimumViSeeVersion)
   {
      this.minimumViSeeVersion = minimumViSeeVersion;
   }

   public int getViseeHeartbeatTimeout()
   {
      return heartbeatTimeout;
   }

   public void setViseeHeartbeatTimeout(int heartbeatTimeout)
   {
      this.heartbeatTimeout = heartbeatTimeout;
   }

   @Override
   public String toString()
   {
      return "RestTerminalProfile [vmr=" + vmr + ", sipUserName=" + sipUserName + ", sipAuthorizationName="
            + sipAuthorizationName + ", sipPassword=" + sipPassword + ", internalSipServer="
            + internalSipServer + ", internalSipProtocol=" + internalSipProtocol + ", externalSipServer="
            + externalSipServer + ", externalSipProtocol=" + externalSipProtocol + ", p2pCallOverMcu="
            + p2pCallOverMcu + ", minimumViSeeVersion=" + minimumViSeeVersion + ", heartbeatTimeout="
            + heartbeatTimeout + "]";
   }
   

}
