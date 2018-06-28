package com.fingerchat.api.codec;

import com.fingerchat.api.protocol.Command;
import com.fingerchat.api.protocol.Packet;
import com.fingerchat.api.util.ByteBuf;

/**
 * Created by LY309313 on 2017/9/26.
 */

public final class PacketEncoder {

    public static void encode(Packet packet, ByteBuf out) {

        if (packet.cmd == Command.HEARTBEAT.cmd) {
            out.put(Packet.HB_PACKET_BYTE);
        } else {
            out.putInt(packet.getBodyLength());
            out.put(packet.cmd);
            out.put(packet.flags);
            out.putInt(packet.sessionId);
            if (packet.getBodyLength() > 0) {
                out.put(packet.body);
            }
        }
    }
}
