package com.fingerchat.api.protocol;

import com.fingerchat.api.connection.Connection;
import com.fingerchat.api.message.BaseMessage;
import com.fingerchat.api.protocol.Command;
import com.fingerchat.api.protocol.Packet;

/**
 * Created by LY309313 on 2017/9/27.
 */

public final class Payload extends BaseMessage {
    public byte[] content;

    public Payload(Command cmd, byte[] content, Connection connection) {
        super(new Packet(cmd,genSessionId()), connection);
        this.content = content;
    }

    public Payload(Packet packet, Connection connection) {
        super(packet, connection);
    }

    @Override
    public void decode(byte[] body) {
        content = body;
    }

    @Override
    public byte[] encode() {
        return content;
    }

    @Override
    public String toString() {
        return "Payload{" +
                "content='" + content.length + '\'' +
                '}';
    }
}
