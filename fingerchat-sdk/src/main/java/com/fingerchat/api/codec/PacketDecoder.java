package com.fingerchat.api.codec;

import com.fingerchat.api.protocol.Packet;

import java.nio.ByteBuffer;

/**
 * Created by LY309313 on 2017/9/26.
 */

public final class PacketDecoder {



    public static Packet decode(ByteBuffer in) {
        Packet hp = decodeHeartbeat(in);
        if (hp != null) return hp;
        return decodeFrame(in);
    }

    private static Packet decodeHeartbeat(ByteBuffer in) {
        if (in.hasRemaining()) {
            in.mark();
            if (in.get() == Packet.HB_PACKET_BYTE) {
                return Packet.HB_PACKET;
            }
            in.reset();
        }
        return null;
    }

    private static Packet decodeFrame(ByteBuffer in) {
        if (in.remaining() >= Packet.HEADER_LEN) {
            in.mark();
            int bufferSize = in.remaining();
            int bodyLength = in.getInt();
            if (bufferSize >= (bodyLength + Packet.HEADER_LEN)) {
                return readPacket(in, bodyLength);
            }
            in.reset();
        }
        return null;
    }

    private static Packet readPacket(ByteBuffer in, int bodyLength) {
        byte command = in.get();
        byte flags = in.get();
        int sessionid = in.getInt();
        byte[] body = null;
        if (bodyLength > 0) {
            body = new byte[bodyLength];
            in.get(body);
        }
        Packet packet = new Packet(command);
        packet.flags = flags;
        packet.body = body;
        packet.sessionId = sessionid;
        return packet;
    }

}
