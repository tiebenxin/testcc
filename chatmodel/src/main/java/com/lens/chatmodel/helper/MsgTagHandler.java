package com.lens.chatmodel.helper;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.IntDef;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.Html.TagHandler;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import com.fingerchat.proto.message.Muc;
import com.lens.chatmodel.ChatEnum.ERelationStatus;
import com.lens.chatmodel.R;
import com.lens.chatmodel.bean.UserBean;
import com.lens.chatmodel.db.MucUser;
import com.lens.chatmodel.db.ProviderChat;
import com.lens.chatmodel.db.ProviderUser;
import com.lens.chatmodel.interf.IActionTagClickListener;
import com.lens.chatmodel.interf.IChatRoomModel;
import com.lensim.fingerchat.commons.helper.ContextHelper;
import com.lensim.fingerchat.commons.interf.IChatUser;
import com.lensim.fingerchat.commons.utils.StringUtils;
import com.lensim.fingerchat.commons.utils.T;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Stack;
import org.xml.sax.XMLReader;

/**
 * Created by xhdl0002 on 2018/3/27.
 * action信息解析
 */
public class MsgTagHandler implements TagHandler {

    public static final int TIME_OUT = 0;//超时。失效
    public static final int BE_FRIEND = 1;//已经是好友
    public static final int IS_VALID = 2;//可行
    private IActionTagClickListener actionListener;

    @IntDef({TIME_OUT, BE_FRIEND, IS_VALID})
    @Retention(RetentionPolicy.SOURCE)
    public @interface EInviteType {

    }

    public static final String VALIDATION = "validation";//群确认
    public static final String CANCEL = "cancel";//邀请撤销
    public static final String SEND_VERIFY = "verify";//发送验证

    private int sIndex = 0;
    private int eIndex = 0;
    //是否解析
    private boolean isBilder;
    private final Context mContext;
    private String msgId;
    /**
     * html 标签的开始下标
     */
    private Stack<Integer> startIndex;
    /**
     * html的标签的属性值
     */
    private Stack<String> propertyValue;

    public MsgTagHandler(Context context, boolean isBilder, String msgId) {
        mContext = context;
        this.msgId = msgId;
        this.isBilder = isBilder;
    }

    public MsgTagHandler(Context context, boolean isBilder, String msgId,
        IActionTagClickListener listener) {
        mContext = context;
        this.msgId = msgId;
        this.isBilder = isBilder;
        actionListener = listener;
    }

    public void handleTag(boolean opening, String tag, Editable output, XMLReader xmlReader) {
        // TODO Auto-generated method stub
        try {
            if (opening) {
                sIndex = output.length();
                if (isBilder) {
                    handlerStartTAG(tag, output, xmlReader);
                }
            } else {
                eIndex = output.length();
                if (isBilder) {
                    handlerEndTAG(tag, output);
                } else {
                    output.setSpan(
                        new ForegroundColorSpan(ContextCompat.getColor(mContext, R.color.gray_99)),
                        sIndex, eIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 处理开始的标签位
     */
    private void handlerStartTAG(String tag, Editable output, XMLReader xmlReader) {
        handlerStartUser(output, xmlReader, tag);
    }

    /**
     * 处理结尾的标签位
     */
    private void handlerEndTAG(String tag, Editable output) {
        handlerEndUser(output, tag);
    }

    private void handlerStartUser(Editable output, XMLReader xmlReader, String tag) {
        if (startIndex == null) {
            startIndex = new Stack<>();
        }
        startIndex.push(output.length());

        if (propertyValue == null) {
            propertyValue = new Stack<>();
        }
        if (tag.equalsIgnoreCase("user")) {
            propertyValue.push(getProperty(xmlReader, "id"));
        } else if (tag.equalsIgnoreCase(VALIDATION)) {
            propertyValue.push(getProperty(xmlReader, "id"));
        } else if (tag.equalsIgnoreCase(CANCEL)) {
            propertyValue.push(getProperty(xmlReader, "id"));
        } else if (tag.equalsIgnoreCase(SEND_VERIFY)) {
            propertyValue.push(getProperty(xmlReader, "id"));
        }
    }

    private void handlerEndUser(Editable output, String tag) {

        if (!isEmpty(propertyValue)) {
            try {
                String id = propertyValue.pop();
                output
                    .setSpan(new MxgsaSpan(id, output.subSequence(sIndex, eIndex).toString(), tag),
                        sIndex, eIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 集合是否为空
     */
    public static boolean isEmpty(Collection collection) {
        return collection == null || collection.isEmpty();
    }

    private class MxgsaSpan extends ClickableSpan {

        private String userId;
        private String nick;
        private String tag;

        public MxgsaSpan(String userId, String nick, String tag) {
            this.userId = userId;
            this.nick = nick;
            this.tag = tag;
        }

        @Override
        public void onClick(View widget) {
            // TODO Auto-generated method stub
            if (tag.equalsIgnoreCase("user")) {
                if (actionListener != null && !TextUtils.isEmpty(userId)) {
                    actionListener.clickUser(userId);
                }
            } else if (tag.equalsIgnoreCase(VALIDATION)) {
                int type = isInviteValid(msgId, userId);
                switch (type) {
                    case TIME_OUT:
                        T.show("邀请已失效");
                        break;
                    case BE_FRIEND:
                        T.show("邀请已确认");
                        break;
                    case IS_VALID:
                        List<String> list = StringUtils.getUserIds(userId);
                        if (actionListener != null && list != null) {
                            actionListener.clickValidation(msgId, userId);
                        }

                        break;
                }
            } else if (tag.equalsIgnoreCase(CANCEL)) {
                List<String> list = StringUtils.getUserIds(userId);
                if (actionListener != null && list != null) {
                    if (list.size() > 0) {
                        if (isCanRevoke(msgId, list.get(0))) {
                            actionListener.clickCancelInvite((ArrayList<String>) list);
                        } else {
                            T.show("对方不是群成员");
                        }
                    }
                }
            } else if (tag.equalsIgnoreCase(SEND_VERIFY)) {
                List<String> list = StringUtils.getUserIds(userId);
                if (actionListener != null && list != null) {
                    if (list.size() > 0) {
                        String user = list.get(0);
                        //TODO:本地暂不判断好友关系，因为对方删除自己成为单边好友时，对方在我的列表中仍为好友
//                        IChatUser bean = ProviderUser
//                            .selectRosterSingle(ContextHelper.getContext(), user);
//                        if (bean != null && bean.getRelationStatus() == ERelationStatus.FRIEND
//                            .ordinal()) {
//                            T.show("已是好友");
//                        } else {
//                            actionListener.sendVerify(user);
//                        }
                        actionListener.sendVerify(user);

                    }
                }
            }
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            super.updateDrawState(ds);
            if (tag.equalsIgnoreCase("user")) {
                ds.setColor(ContextCompat.getColor(mContext, R.color.msg_tag_color));
            } else if (tag.equalsIgnoreCase(VALIDATION)) {
                ds.setColor(Color.GREEN);
            } else if (tag.equalsIgnoreCase(CANCEL)) {
                ds.setColor(Color.GREEN);
            } else if (tag.equalsIgnoreCase(SEND_VERIFY)) {
                ds.setColor(Color.GREEN);
            }
            ds.setUnderlineText(true);
        }

    }

    /**
     * 利用反射获取html标签的属性值
     */
    private String getProperty(XMLReader xmlReader, String property) {
        try {
            Field elementField = xmlReader.getClass().getDeclaredField("theNewElement");
            elementField.setAccessible(true);
            Object element = elementField.get(xmlReader);
            Field attsField = element.getClass().getDeclaredField("theAtts");
            attsField.setAccessible(true);
            Object atts = attsField.get(element);
            Field dataField = atts.getClass().getDeclaredField("data");
            dataField.setAccessible(true);
            String[] data = (String[]) dataField.get(atts);
            Field lengthField = atts.getClass().getDeclaredField("length");
            lengthField.setAccessible(true);
            int len = (Integer) lengthField.get(atts);
            for (int i = 0; i < len; i++) {
                // 这边的property换成你自己的属性名就可以了
                if (property.equals(data[i * 5 + 1])) {
                    return data[i * 5 + 4];
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /*
    * 是否邀请有效
    * */
    @EInviteType
    private int isInviteValid(String msgId, String userId) {
        IChatRoomModel model = ProviderChat.selectMsgSingle(ContextHelper.getContext(), msgId);
        if (model != null) {
            if (ChatHelper.isTimeValid(model.getTime())) {
                Muc.MucMemberItem item = MucUser
                    .selectUserById(ContextHelper.getContext(), model.getTo(), userId);
                if (item == null) {//非群成员
                    return IS_VALID;
                } else {
                    return BE_FRIEND;
                }
            } else {
                return TIME_OUT;
            }
        } else {
            return TIME_OUT;
        }
    }

    /*
    * 是否能撤销
    * */
    private boolean isCanRevoke(String msgId, String userId) {
        IChatRoomModel model = ProviderChat.selectMsgSingle(ContextHelper.getContext(), msgId);
        if (model != null) {
            if (ChatHelper.isTimeValid(model.getTime())) {
                Muc.MucMemberItem item = MucUser
                    .selectUserById(ContextHelper.getContext(), model.getTo(), userId);
                if (item != null) {//是群成员
                    return true;
                }
            }
        }
        return false;
    }
}
