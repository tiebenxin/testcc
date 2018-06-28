package com.fingerchat.api.protocol;

import com.fingerchat.api.push.MessageContext;
import com.fingerchat.proto.message.Roster.RosterItem;
import com.fingerchat.proto.message.User.BindMessage;
import java.util.concurrent.Future;

/**
 * Created by LY309313 on 2017/9/23.
 */

public interface FingerProtocol {

    /**
     * 健康检查, 检测读写超时, 发送心跳
     *
     * @return true/false Client
     */
    boolean healthCheck();

    void fastConnect();

    void handshake();

    void applyVerCode(String userid, String phoneNumber);

    void register(String userId, String password, String phoneNumber, String verCode);

    void login(String userId, String password);

    void loginByPhone(String phone, String password);

    void loginError();

    void logout();

    Future<Boolean> send(Command cmd, MessageContext context);

    int sendResult(Command cmd, MessageContext context);

    void ack(String messageId);

    void getRosters();

    void addFriend(String user);

    void searchFriend(String user);

    void deleFriend(String user);

    void updateFriendInfo(String user, RosterItem item);

    void updateUserInfo(BindMessage message);


}
