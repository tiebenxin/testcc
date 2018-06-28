package com.lensim.fingerchat.fingerchat.ui.contacts;

import android.view.View;
import com.lensim.fingerchat.commons.helper.ContextHelper;
import com.lensim.fingerchat.data.login.UserInfoRepository;
import com.lensim.fingerchat.fingerchat.R;
import com.lensim.fingerchat.components.adapter.AbstractViewHolder;
import com.lens.chatmodel.interf.IContactListener;
import com.lensim.fingerchat.commons.interf.OnControllerClickListenter;

/**
 * Created by LL130386 on 2017/11/30.
 */

public class ControllerContactHead extends AbstractViewHolder {

    private final int NEW_FRIEND = 0;
    private final int GROUP_CHAT = 1;
    private final int GROUP_FRIEND = 2;
    private final int CUSTOMER_SERIVCE = 3;
    private final int SELF = 4;

    private ControllerContactHeadCell viewNewFriend;
    private ControllerContactHeadCell viewGroupChat;
    private ControllerContactHeadCell viewGroupFriend;
    private ControllerContactHeadCell viewSelf;
    private IContactListener mListener;
    private ControllerContactHeadCell viewCustomerService;

    public ControllerContactHead(View v, IContactListener l) {
        super(v);
        init(v);
        mListener = l;
    }

    @Override
    public void bindData(Object bean) {

    }

    private void init(View v) {
        viewNewFriend = new ControllerContactHeadCell(v.findViewById(R.id.viewNewFriend));
        viewGroupChat = new ControllerContactHeadCell(v.findViewById(R.id.viewGroupChat));
        viewGroupFriend = new ControllerContactHeadCell(v.findViewById(R.id.viewGroupFriend));
        viewCustomerService = new ControllerContactHeadCell(
            v.findViewById(R.id.viewCustomerService));

        viewSelf = new ControllerContactHeadCell(v.findViewById(R.id.viewSelf));

        viewNewFriend.setAvatar(getDrawable(NEW_FRIEND));
        viewNewFriend.setName(getString(NEW_FRIEND));
        viewGroupChat.setAvatar(getDrawable(GROUP_CHAT));
        viewGroupChat.setName(getString(GROUP_CHAT));

        viewGroupFriend.setAvatar(getDrawable(GROUP_FRIEND));
        viewGroupFriend.setName(getString(GROUP_FRIEND));

        viewCustomerService.setAvatar(getDrawable(CUSTOMER_SERIVCE));
        viewCustomerService.setName(getString(CUSTOMER_SERIVCE));

        viewSelf.setAvatar(UserInfoRepository.getImage());
        viewSelf.setName(getString(SELF));

        viewNewFriend.setOnClickListener(new OnControllerClickListenter() {
            @Override
            public void onClick() {
                if (mListener != null) {
                    mListener.clickNewFriend();
                }
            }
        });

        viewGroupChat.setOnClickListener(new OnControllerClickListenter() {
            @Override
            public void onClick() {
                if (mListener != null) {
                    mListener.clickGroups();
                }
            }
        });

        viewGroupFriend.setOnClickListener(new OnControllerClickListenter() {
            @Override
            public void onClick() {
                if (mListener != null) {
                    mListener.clickGroupFriend();
                }
            }
        });

        viewCustomerService.setOnClickListener(new OnControllerClickListenter() {
            @Override
            public void onClick() {
                if (mListener != null) {
                    mListener.clickCustomerSerivce();
                }
            }
        });

        viewSelf.setOnClickListener(new OnControllerClickListenter() {
            @Override
            public void onClick() {
                if (mListener != null) {
                    mListener.clickSelf();
                }
            }
        });

    }

    public void setSelfAvatar(String url) {
        viewSelf.setAvatar(url);
    }

    public void setUnread(int num) {
        viewNewFriend.setUnread(num);
    }

    private int getDrawable(int type) {
        switch (type) {
            case NEW_FRIEND:
                return R.drawable.ic_new_friend;
            case GROUP_CHAT:
                return R.drawable.ic_group_chat;
            case GROUP_FRIEND:
                return R.drawable.ic_create_group;
            case CUSTOMER_SERIVCE:
                return R.drawable.ic_customer_service;
            case SELF:
                return R.drawable.default_avatar;
            default:
                return R.drawable.default_avatar;
        }
    }

    private String getString(int type) {
        switch (type) {
            case NEW_FRIEND:
                return ContextHelper.getString(R.string.new_friend);
            case GROUP_CHAT:
                return ContextHelper.getString(R.string.groups);
            case GROUP_FRIEND:
                return ContextHelper.getString(R.string.group_friend);
            case CUSTOMER_SERIVCE:
                return ContextHelper.getString(R.string.customer_service);
            case SELF:
                return ContextHelper.getString(R.string.self);
            default:
                return "";
        }
    }


}
