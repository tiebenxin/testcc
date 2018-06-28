package com.lensim.fingerchat.hexmeet.conf;

import com.lensim.fingerchat.hexmeet.api.model.RestMeeting;

public class ConferenceStatus {

  public enum Status {
    NEW, APPROVED, ONGOING, FINISHED;
  }

  public static Status getStatus(RestMeeting meeting) {
    String confStatus = meeting.getStatus();

    if (confStatus == null) {
      return Status.NEW;
    } else if (isOngoing(confStatus)) {
      return Status.ONGOING;
    } else {
      if (confStatus.equalsIgnoreCase("FINISHED")) {
        return Status.FINISHED;
      }

      return Status.APPROVED;
    }
  }

  public static boolean isOngoing(String status) {
    return status.equalsIgnoreCase("LAUNCHING") || status.equalsIgnoreCase("ONGOING");
  }
}
