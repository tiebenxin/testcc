package com.lens.chatmodel.ui.group;

import com.lens.chatmodel.bean.UserBean;

/**
 * Created by xhdl0002 on 2018/1/11.
 */

public interface GroupSelectListener{
    void showSelectedView(boolean isOneSelect, boolean isRemove);
    void selectMucInfo();
    void createSecretMuc();
    void startSecretChat(UserBean userBean);
}
