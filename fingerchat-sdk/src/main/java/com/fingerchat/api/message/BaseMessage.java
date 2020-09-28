package com.fingerchat.api.message;

import com.fingerchat.api.IMessage;
import com.fingerchat.api.codec.PacketDecoder;
import com.fingerchat.api.connection.Connection;
import com.fingerchat.api.connection.SessionContext;
import com.fingerchat.api.protocol.Packet;

import java.util.concurrent.atomic.AtomicInteger;

import sun.security.krb5.internal.PAData;

/**
 * @author LY309313
 * @create 2017/9/6
 */
public abstract class BaseMessage implements IMessage {
    public static final byte STATUS_DECODED = 1;
    public static final byte STATUS_ENCODED = 2;

    protected final Packet packet;
    protected Connection connection;
    private static final AtomicInteger SID_SEQ = new AtomicInteger();
    protected byte status = 0;

    public BaseMessage(Packet packet, Connection connection) {
        this.packet = packet;
        this.connection = connection;
    }

    public BaseMessage(Packet packet) {
        this.packet = packet;
    }

    @Override
    public void decodeBody() {
        if ((status & STATUS_DECODED) != 0) return;
        else status |= STATUS_DECODED;

        if (packet.body != null && packet.body.length > 0) {
            //1.解密
            byte[] tmp = packet.body;
            if (packet.hasFlag(Packet.FLAG_CRYPTO)) {
                if (connection.getSessionContext().cipher != null) {
                    tmp = connection.getSessionContext().cipher.decrypt(tmp);
                }
            }

            //2.解压
//            if (packet.hasFlag(Packet.FLAG_COMPRESS)) {
//                tmp = IOUtils.uncompress(tmp);
//            }

            if (tmp.length == 0) {
                throw new RuntimeException("message decode ex");
            }

            packet.body = tmp;
            decode(packet.body);
        }
    }

    @Override
    public void encodeBody() {
        if ((status & STATUS_ENCODED) != 0) return;
        else status |= STATUS_ENCODED;

        byte[] tmp = encode();
        if (tmp != null && tmp.length > 0) {
            System.out.println(PacketDecoder.class.getSimpleName() + "--发送数据--" + PacketDecoder.bytesToHex(tmp));
            //2.加密
            SessionContext context = connection.getSessionContext();
            if (context.cipher != null) {
                byte[] result = context.cipher.encrypt(tmp);
                if (result.length > 0) {
                    tmp = result;
                    packet.addFlag(Packet.FLAG_CRYPTO);
                }
            }
            packet.body = tmp;
        }
    }

    private void encodeBodyRaw() {
        if ((status & STATUS_ENCODED) != 0) return;
        else status |= STATUS_ENCODED;

        packet.body = encode();
    }

    protected abstract void decode(byte[] body);

    protected abstract byte[] encode();

    public Packet createResponse() {
        return new Packet(packet.cmd);
    }

    @Override
    public Packet getPacket() {
        return packet;
    }

    @Override
    public Connection getConnection() {
        return connection;
    }

    @Override
    public void setConnection(Connection connection) {
        this.connection = connection;
    }


    protected static int genSessionId() {
        return SID_SEQ.incrementAndGet();
    }

    public int getSessionId() {
        return packet.sessionId;
    }

    @Override
    public void send() {
        encodeBody();
        connection.send(packet);
    }

    @Override
    public void sendRaw() {
        encodeBodyRaw();
        connection.send(packet);
    }


    @Override
    public String toString() {
        return "BaseMessage{" +
            "packet=" + packet +
            ", connection=" + connection +
            '}';
    }
}
