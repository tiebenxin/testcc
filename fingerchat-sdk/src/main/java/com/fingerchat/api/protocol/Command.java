package com.fingerchat.api.protocol;

/**
 * Created by LY309313 on 2017/8/31.
 */
public enum Command {
    HEARTBEAT(1),//心跳
    HANDSHAKE(2),//握手
    OB_VER_CODE(3),//获取验证码
    REGISTER(4),//注册
    LOGIN(5),//登陆
    LOGOUT(6),//登出
    FAST_CONNECT(7),//快速重连
    RESPONSE(8),//响应
    ROSTER_OPTION(9),//操作花名册
    ROSTER(10),//花名册
    MUC_OPTION(11),//群操作
    MUC(12),//群相关
    MUC_MEMBER(13),//群成员
    MUC_ACTION(14),//群通知
    ACK(15),//回执
    PRIVATE_CHAT(16),//单聊
    GROUP_CHAT(17),//群聊
    OFFLINE(18),//离线消息
    CONFLICT(19),//冲突
    USER_INFO(20),//用户消息
    USER_UPDATE(21),//用户更新
    MSG_ACK(22),//消息回执
    NOTIFY(23),//推送通知
    CHANGE_PASS(24), //修改密码
    GATEWAY_PUSH(25), //内部使用
    FIRST_HELLO(26), //好友成功招呼
    PACKET(27),
    EXCUTE(28), //提供后台服务处理
    READED(29), //已读消息指令
    UNKNOWN(-1);//未知标号
    public final byte cmd;

    Command(int cmd) {
        this.cmd = (byte) cmd;
    }

    public static Command toCMD(byte b){
        Command[] values = values();
        if(b > 0 && b < values.length) return values[b -1];
        return UNKNOWN;
    }
}
