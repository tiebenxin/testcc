
package com.lens.chatmodel.notification;


import com.lens.chatmodel.bean.BaseEntity;
import java.util.Date;


public class MessageNotification extends BaseEntity {


    private String text;


    private Date timestamp;


    private int count;

    public MessageNotification(String account, String user, String text,
                               Date timestamp, int count) {
        super(account, user);
        this.text = text;
        this.timestamp = timestamp;
        this.count = count;
    }

    public String getText() {
        return text;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public int getCount() {
        return count;
    }

    public void addMessage(String text) {
        this.text = text;
        this.timestamp = new Date();
        this.count += 1;
    }

}
