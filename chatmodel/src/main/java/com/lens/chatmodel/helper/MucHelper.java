package com.lens.chatmodel.helper;

import android.content.Context;
import android.text.TextUtils;

import com.fingerchat.proto.message.Muc;
import com.fingerchat.proto.message.Muc.MOption;
import com.lens.chatmodel.ChatEnum;
import com.lens.chatmodel.R;
import com.lens.chatmodel.bean.body.BodyEntity;
import com.lensim.fingerchat.commons.app.AppConfig;
import com.lensim.fingerchat.commons.global.Common;

/**
 * Created by xhdl0002 on 2018/2/5.
 */

public final class MucHelper {

    public static String resultBiudleName(Muc.MucMemberItem item) {
        String userNick =
            TextUtils.isEmpty(item.getMucusernick()) ? (TextUtils.isEmpty(item.getUsernick()) ? item
                .getUsername() : item.getUsernick()) : item.getMucusernick();
        return "<user id='" + item.getUsername() + "'>" + userNick + "</user>";
    }

    public static String resultBuildName(String userId, String userNick) {
        return "<user id='" + userId + "'>" + userNick + "</user>";
    }

    /**
     * MucAtion拼接消息
     */
    public static String getActionText(Context context, Muc.MucAction action) {
        boolean isMySelf = AppConfig.INSTANCE.get(AppConfig.ACCOUT).toLowerCase()
            .equals(action.getFrom().getUsername().toLowerCase());
        String resultContent = "";
        String fromName = resultBiudleName(action.getFrom());
        switch (action.getAction().ordinal()) {
            /**
             * 群邀请确认消息 sdk增加后改为群邀请确认的 option 即可
             */
            case Muc.MOption.Create_VALUE:
                StringBuilder sbUsers = new StringBuilder();
                if (0 != action.getUsernamesCount()) {
                    sbUsers.append(" '");
                    for (int i = 0; i < action.getUsernamesCount(); i++) {
                        Muc.MucMemberItem item = action.getUsernamesList().get(i);
                        if (i < action.getUsernamesCount() - 1) {
                            sbUsers.append(resultBiudleName(item) + "，");
                        } else {
                            sbUsers.append(resultBiudleName(item));
                        }
                    }
                    sbUsers.append("' ");
                }
                resultContent = context
                    .getString(R.string.action_invite, isMySelf ? "你" : " \'" + fromName + "\' ",
                        sbUsers.toString());
                break;
            case Muc.MOption.Invite_VALUE:
                StringBuilder sbInviteUsers = new StringBuilder();
                if (0 != action.getUsernamesCount()) {
                    sbInviteUsers.append(" '");
                    for (int i = 0; i < action.getUsernamesCount(); i++) {
                        Muc.MucMemberItem item = action.getUsernamesList().get(i);
                        if (i < action.getUsernamesCount() - 1) {
                            sbInviteUsers.append(resultBiudleName(item) + "，");
                        } else {
                            sbInviteUsers.append(resultBiudleName(item));
                        }
                    }
                    sbInviteUsers.append("' ");
                }
                if (isMySelf) {
                    resultContent = context
                        .getString(R.string.action_invite,
                            isMySelf ? "你" : " \'" + fromName + "\' ",
                            sbInviteUsers.toString()) + "去'<cancel id='" + sbInviteUsers.toString()
                        + "'>撤销</cancel>\'";
                } else {
                    resultContent = context
                        .getString(R.string.action_invite,
                            isMySelf ? "你" : " \'" + fromName + "\' ", sbInviteUsers.toString());
                }
                break;
            case Muc.MOption.Join_VALUE:
                resultContent = context.getString(R.string.action_join,
                    "\'" + resultBiudleName(action.getUsernames(0)) + "'");
                break;
            case Muc.MOption.Kick_VALUE:
                StringBuilder sbKickUsers = new StringBuilder();
                if (0 != action.getUsernamesCount()) {
                    sbKickUsers.append(" '");
                    for (int i = 0; i < action.getUsernamesCount(); i++) {
                        Muc.MucMemberItem item = action.getUsernamesList().get(i);
                        if (i < action.getUsernamesCount() - 1) {
                            sbKickUsers.append(resultBiudleName(item) + "，");
                        } else {
                            sbKickUsers.append(resultBiudleName(item));
                        }
                    }
                    sbKickUsers.append("' ");
                }
                resultContent = context
                    .getString(R.string.action_kick_by, isMySelf ? "你" : " \'" + fromName + "\' ",
                        sbKickUsers.toString());
                break;
            case Muc.MOption.Leave_VALUE:
                resultContent = context.getString(R.string.action_leave, " \'" + fromName + "\' ");
                break;
            case Muc.MOption.ChangeRole_VALUE:
                resultContent = context.getString(R.string.action_changeRole,
                    " \"" + resultBiudleName(action.getUsernames(0)) + "\" ");
                break;
            case Muc.MOption.UpdateConfig_VALUE:
                switch (action.getUpdateOption().ordinal()) {
                    //群验证方式
                    case Muc.UpdateOption.UAutoEnter_VALUE:
                        resultContent =
                            (ChatEnum.ESureType.YES.value == action.getNeedConfirm()) ? context
                                .getResources().getString(R.string.action_uAutoEnter)
                                : context.getResources().getString(R.string.action_uAutoEnter_no);
                        break;
                    //群名
                    case Muc.UpdateOption.UName_VALUE:
                        resultContent = context.getString(R.string.action_uName,
                            (isMySelf ? "你" : " \'" + fromName + "\' "),
                            "'" + action.getMucname() + " \'");
                        break;
//                    //公告
//                    case Muc.UpdateOption.USubject_VALUE:
//                        resultContent = context.getString(R.string.action_uSubject, action.getSubject());
//                        break;
                }
                break;
            case MOption.Confirm_VALUE:
                StringBuilder confirmUsers = new StringBuilder();
                if (0 != action.getUsernamesCount()) {
                    confirmUsers.append(" '");
                    for (int i = 0; i < action.getUsernamesCount(); i++) {
                        Muc.MucMemberItem item = action.getUsernamesList().get(i);
                        if (i < action.getUsernamesCount() - 1) {
                            confirmUsers.append(resultBiudleName(item) + "，");
                        } else {
                            confirmUsers.append(resultBiudleName(item));
                        }
                    }
                    confirmUsers.append("' ");
                }
                resultContent = context.getString(R.string.action_confirm, " \'" + fromName + "\' ",
                    action.getUsernamesCount() + "") + "去'<validation id='" + confirmUsers
                    .toString() + "'>验证</validation>\'";
                break;
            case MOption.Revoke_VALUE:
                StringBuilder sbRevokeUsers = new StringBuilder();
                if (0 != action.getUsernamesCount()) {
                    sbRevokeUsers.append(" '");
                    for (int i = 0; i < action.getUsernamesCount(); i++) {
                        Muc.MucMemberItem item = action.getUsernamesList().get(i);
                        if (i < action.getUsernamesCount() - 1) {
                            sbRevokeUsers.append(resultBiudleName(item) + "，");
                        } else {
                            sbRevokeUsers.append(resultBiudleName(item));
                        }
                    }
                    sbRevokeUsers.append("' ");
                }
                resultContent = context
                    .getString(R.string.action_kick_by, isMySelf ? "你" : " \'" + fromName + "\' ",
                        sbRevokeUsers.toString());
                break;

        }
        if (!TextUtils.isEmpty(resultContent)) {
            BodyEntity entity = new BodyEntity();
            entity.setBody(resultContent);
            entity.setSecret(false);
            return BodyEntity.toJson(entity);
        } else {
            return resultContent;
        }
    }

    /**
     * 群的错误操作信息
     */
    public static String getMucCodeResult(int resultCode) {
        if ((Common.CREATE_OK <= resultCode && resultCode <= Common.MUC_QUERY_OK) || (
            Common.JOIN_OK <= resultCode && resultCode <= Common.MUC_UPDATE_OK)
            || (Common.MUC_QUERY_All_ROOMS_OF_USER_OK <= resultCode
            && resultCode <= Common.MUC_QUERY_ADMIN_OK)) {
            return "";
        }
        String resultStr = "";
        switch (resultCode) {
            case Common.INVITE_FAILURE:
                resultStr = "邀请失败";
                break;
            case Common.CREATE_FAILURE:
                resultStr = "创建群聊失败";
                break;
            case Common.NOT_MEMBER:
                resultStr = "不是群成员";
                break;
            case Common.ROOM_INEXIST:
                resultStr = "群聊不存在";
                break;
            case Common.NEED_OWNER:
                resultStr = "需要群主权限";
                break;
            case Common.UNSUPPORTED_CMD:
                resultStr = "无法识别的命令";
                break;
            case Common.PARAM_INVALID:
                resultStr = "非法参数";
                break;
            case Common.INVALID_DEVICE:
                resultStr = "非法设备";
                break;
            case Common.LOGIN_UNAUTHORIZED:
                resultStr = "未登陆";
                break;
        }
        return resultStr;
    }
}
