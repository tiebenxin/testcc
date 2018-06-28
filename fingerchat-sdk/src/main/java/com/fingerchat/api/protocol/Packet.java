package com.fingerchat.api.protocol;


/**
 * Created by LY309313 on 2017/8/31.
 */
public class Packet {

    public static final int HEADER_LEN = 10;//packet包头协议长度

    public static final byte FLAG_CRYPTO = 0x01;//packet包启用加密
    public final static byte HB_PACKET_BYTE = -18;
    public final static byte[] HB_PACKET_BYTES = new byte[]{HB_PACKET_BYTE};
    public final static Packet HB_PACKET = new Packet(Command.HEARTBEAT);

    public byte cmd;
    public int sessionId;
    transient public byte[] body;
    public byte flags; //特性，如是否加密，是否压缩等

   public Packet(byte cmd){
        this.cmd = cmd;
   }

   public Packet(byte cmd,int sessionId){
       this.cmd = cmd;
       this.sessionId = sessionId;
   }
    public void addFlag(byte flag) {
    this.flags |= flag;
}

    public boolean hasFlag(byte flag) {
        return (flags & flag) == flag;
    }

    public Packet(Command cmd) {
        this.cmd = cmd.cmd;
    }

    public Packet(Command cmd, int sessionId) {
        this.cmd = cmd.cmd;
        this.sessionId = sessionId;
    }

    public int getBodyLength(){
      return body == null ? 0 : body.length;
    }

}
