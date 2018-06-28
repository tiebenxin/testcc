package com.lensim.fingerchat.data.hexmeet;

import java.io.Serializable;
import java.util.List;

/**
 * Created by LL117394 on 17/10/27
 */
public class LockUser implements Serializable {

  /**
   * Data : [{"UserCode":"user13","SIP":"1020","State":0,"IsLocked":true,"LockDate":"2017-11-20T17:29:09.3312168+08:00","LastOnlineDate":"2017-11-20T09:20:38.127","id":"2010"}]
   * ResultCode : 1
   * ErrMsg :
   */

  private int ResultCode;
  private String ErrMsg;
  private List<DataBean> Data;

  public int getResultCode() {
    return ResultCode;
  }

  public void setResultCode(int ResultCode) {
    this.ResultCode = ResultCode;
  }

  public String getErrMsg() {
    return ErrMsg;
  }

  public void setErrMsg(String ErrMsg) {
    this.ErrMsg = ErrMsg;
  }

  public List<DataBean> getData() {
    return Data;
  }

  public void setData(List<DataBean> Data) {
    this.Data = Data;
  }

  public static class DataBean implements Serializable {

    /**
     * UserCode : user13
     * SIP : 1020
     * State : 0
     * IsLocked : true
     * LockDate : 2017-11-20T17:29:09.3312168+08:00
     * LastOnlineDate : 2017-11-20T09:20:38.127
     * id : 2010
     */

    private String UserCode;
    private String SIP;
    private int State;
    private boolean IsLocked;
    private String LockDate;
    private String LastOnlineDate;
    private String id;

    public String getUserCode() {
      return UserCode;
    }

    public void setUserCode(String UserCode) {
      this.UserCode = UserCode;
    }

    public String getSIP() {
      return SIP;
    }

    public void setSIP(String SIP) {
      this.SIP = SIP;
    }

    public int getState() {
      return State;
    }

    public void setState(int State) {
      this.State = State;
    }

    public boolean isIsLocked() {
      return IsLocked;
    }

    public void setIsLocked(boolean IsLocked) {
      this.IsLocked = IsLocked;
    }

    public String getLockDate() {
      return LockDate;
    }

    public void setLockDate(String LockDate) {
      this.LockDate = LockDate;
    }

    public String getLastOnlineDate() {
      return LastOnlineDate;
    }

    public void setLastOnlineDate(String LastOnlineDate) {
      this.LastOnlineDate = LastOnlineDate;
    }

    public String getId() {
      return id;
    }

    public void setId(String id) {
      this.id = id;
    }
  }
}
