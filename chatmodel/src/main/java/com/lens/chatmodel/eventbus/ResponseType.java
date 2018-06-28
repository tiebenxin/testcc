package com.lens.chatmodel.eventbus;

import com.fingerchat.api.message.RespMessage;
import com.fingerchat.proto.message.Resp.Message;
import com.lensim.fingerchat.commons.global.Common;

/**
 * Created by LL130386 on 2017/11/21.
 */

public class ResponseType {

    public static final int LOGIN = 1;
    public static final int REGISTER = 2;
    public static final int NO_LOGIN = 3;//未登录，被挤

    public static int getType(RespMessage packet) {
        int type = -1;
        if (packet != null && packet.response != null) {
            Message msg = packet.response;
            if (msg.getCode() == Common.REG_REGISTER_OK || msg.getCode() == Common.REG_SMS_ERROR
                || msg.getCode() == Common.REG_VER_CODE_ERROR || msg.getCode() == Common.REG_SMS_OK
                || msg.getCode() == Common.ACCOUNT_DUMPLICATED) {
                type = REGISTER;
            } else if (msg.getCode() == Common.LOGIN_ACCOUNT_INEXIST
                || msg.getCode() == Common.LOGIN_FORBIDDON_LOGIN
                || msg.getCode() == Common.LOGIN_LOGIN_CONFLICT
                || msg.getCode() == Common.LOGIN_UNAUTHORIZED
                || msg.getCode() == Common.LOGIN_UNBIND_ERROR
                || msg.getCode() == Common.LOGIN_UNBIND_SUCCESS
                || msg.getCode() == Common.LOGIN_VERYFY_ERROR
                || msg.getCode() == Common.LOGIN_VERYFY_PASSED) {
                type = LOGIN;

            } else if (msg.getCode() == Common.LOGIN_UNAUTHORIZED) {
                type = NO_LOGIN;
            }
        }
        return type;
    }


}
