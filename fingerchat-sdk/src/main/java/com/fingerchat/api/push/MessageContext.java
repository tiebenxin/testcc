package com.fingerchat.api.push;

import com.fingerchat.api.ack.AckContext;

/**
 * Created by LY309313 on 2017/9/27.
 */

public final class MessageContext extends AckContext {

    public byte[] content;
    public MessageContext(byte[] content) {
        this.content = content;
    }

    public static MessageContext build(byte[] content) {
        return new MessageContext(content);
    }

    public byte[] getContent() {
        return content;
    }

    public MessageContext setContent(byte[] content) {
        this.content = content;
        return this;
    }

}
