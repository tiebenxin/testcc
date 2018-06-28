
package com.lens.chatmodel.notification;


import java.util.Date;

public class CircleNotification{




    private Date timestamp;

    /**
     * 消息数量
     */
    private int count;

    public CircleNotification(Date timestamp, int count) {
        this.timestamp = timestamp;
        this.count = count;
    }


    public Date getTimestamp() {
        return timestamp;
    }
    public int getCount() {
        return count;
    }
    public void setCount(int count) {
        this.timestamp = new Date();
        this.count = count;
    }

}
