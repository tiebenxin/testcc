package com.lens.chatmodel.ui.search;

import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.TextView;
import com.lens.chatmodel.ChatEnum.EResultType;
import com.lens.chatmodel.R;
import com.lens.chatmodel.bean.SearchMessageBean;
import com.lens.chatmodel.bean.UserBean;
import com.lens.chatmodel.db.MucInfo;
import com.lens.chatmodel.db.ProviderUser;
import com.lens.chatmodel.helper.ImageHelper;
import com.lens.chatmodel.manager.MessageManager;
import com.lensim.fingerchat.commons.widgt.AvatarImageView;
import com.lensim.fingerchat.commons.helper.ContextHelper;
import com.lensim.fingerchat.commons.utils.StringUtils;

/**
 * Created by LL130386 on 2018/4/27.
 * 本地搜索消息记录item
 */

public class ControllerSearchItem {

    private AvatarImageView iv_avatar;
    private TextView tv_name;
    private TextView tv_content;
    private final View viewRoot;
    private ISearchEventListener listener;

    ControllerSearchItem(View v) {
        viewRoot = v;
        initView(v);
    }


    public View getView() {
        return viewRoot;
    }

    private void initView(View v) {
        iv_avatar = v.findViewById(R.id.iv_avatar);
        tv_name = v.findViewById(R.id.tv_name);
        tv_content = v.findViewById(R.id.tv_content);
    }

    public void setData(SearchMessageBean bean, String condition, EResultType type) {
        if (bean == null) {
            return;
        }
        switch (type) {
            case CONTACT:
                String name = StringUtils.getUserNick(bean.getNick(), bean.getUserId());
                tv_name.setText(getSpan(name, condition));
                ImageHelper.loadAvatarPrivate(
                    ProviderUser.getUserAvatar(ContextHelper.getContext(), bean.getUserId()),
                    iv_avatar);
                iv_avatar.setChatType(true);
                tv_content.setVisibility(View.GONE);
                break;
            case MUC:
                String nameMuc = StringUtils.getUserNick(bean.getNick(), bean.getUserId());
                tv_name.setText(getSpan(nameMuc, condition));
                iv_avatar.setDrawText(
                    MucInfo.selectMucUserNickList(ContextHelper.getContext(), bean.getUserId()));
                tv_content.setVisibility(View.GONE);
                break;
            case RECORD:
                if (bean.isGroupChat()) {
                    String mucName = MucInfo
                        .getMucName(ContextHelper.getContext(), bean.getUserId());
                    tv_name.setText(StringUtils.getUserNick(mucName, bean.getUserId()));
                    iv_avatar.setDrawText(
                        MucInfo.selectMucUserNickList(ContextHelper.getContext(), bean.getUserId()));
                    tv_content.setText(bean.getMessage());
                } else {
                    UserBean userBean = MessageManager.getInstance()
                        .getCacheUserBean(bean.getUserId());
                    if (userBean != null) {
                        tv_name.setText(
                            StringUtils.getUserNick(userBean.getUserNick(), bean.getUserId()));
                        ImageHelper.loadAvatarPrivate(userBean.getAvatarUrl(), iv_avatar);
                    }
                    tv_content.setText(bean.getMessage());
                }
                break;
        }
        initListener(type, bean);


    }

    private void initListener(EResultType type, SearchMessageBean selectBean) {
        viewRoot.setOnClickListener(v -> listener.clickItem(type, selectBean));
    }


    private Spannable getSpan(String message, String condition) {
        if (!message.contains(condition)) {
            return new SpannableString(message);
        }
        SpannableString ss = new SpannableString(message);
        int start = message.indexOf(condition);
        int end = start + condition.length();
        ss.setSpan(new ForegroundColorSpan(Color.GREEN), start, end,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return ss;
    }


    public void setListener(ISearchEventListener listener) {
        this.listener = listener;
    }
}
