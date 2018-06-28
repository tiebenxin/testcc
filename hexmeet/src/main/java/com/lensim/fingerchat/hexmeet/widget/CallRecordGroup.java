package com.lensim.fingerchat.hexmeet.widget;

import com.lensim.fingerchat.hexmeet.db.RestCallRow_;
import java.io.Serializable;


public class CallRecordGroup implements Serializable {

  private static final long serialVersionUID = 1L;

  private int missedCallCount = 0;
  private RestCallRow_ latestCall = new RestCallRow_();

  public int getMissedCallCount() {
    return missedCallCount;
  }

  public void setMissedCallCount(int missedCallCount) {
    this.missedCallCount = missedCallCount;
  }

  public RestCallRow_ getLatestCall() {
    return latestCall;
  }

  public void setLatestCall(RestCallRow_ latestCall) {
    this.latestCall = latestCall;
  }
}
