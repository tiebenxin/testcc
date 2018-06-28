package com.lensim.fingerchat.hexmeet.api.model;

public class RestErrorMessage
{
   private static final char SEPERATOR = ';';
   private int errorCode;
   private String errorInfo;

   /**
    * @return the errorCode
    */
   public int getErrorCode()
   {
      return errorCode;
   }

   /**
    * @param errorCode the errorCode to set
    */
   public void setErrorCode(int errorCode)
   {
      this.errorCode = errorCode;
   }

   /**
    * @return the errorInfo
    */
   public String getErrorInfo()
   {
      return errorInfo;
   }

   /**
    * @param errorInfo the errorInfo to set
    */
   public void setErrorInfo(String errorInfo)
   {
      this.errorInfo = errorInfo;
   }

   @Override
   public String toString()
   {
      StringBuilder sb = new StringBuilder();
      sb.append('{');
      sb.append("errorCode: ").append(errorCode).append(SEPERATOR);
      sb.append("errorInfo: ").append(errorInfo);
      sb.append('}');
      return sb.toString();
   }
}
