
package com.lens.chatmodel.notification;


import java.util.Date;

public class SystemNotification {




    private Date timestamp;

    private String  message;

    public SystemNotification(Date timestamp, String  message) {
        this.timestamp = timestamp;
        this.message = message;
    }


    public Date getTimestamp() {
        return timestamp;
    }
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.timestamp = new Date();
        this.message = message;
    }

}
