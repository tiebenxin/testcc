package com.lens.chatmodel.ui.group;

import android.content.Intent;

import com.fingerchat.proto.message.Muc;
import com.lens.chatmodel.bean.UserBean;

/**
 * Created by xhdl0002 on 2018/1/18.
 * 单选群列表
 */

public class GroupSingerListActivity extends GroupListActivity implements
    AdapterGroupList.OnItemClick {

    @Override
    public void initView() {
        super.initView();
        //清空toolbar
        toolbar.setConfirmBt("");
        toolbar.initRightView(null);
    }

    @Override
    public void onitemClick(int positioin) {
        Muc.MucItem mucItem = adapterGroupList.getData().get(positioin);
        UserBean groupUserBean = new UserBean(mucItem.getMucid(), mucItem.getMucname(),
            Constant.ROLE_GROUP_MODE);
        Intent intent = new Intent();
        intent.putExtra("selectUser", groupUserBean);
        setResult(RESULT_OK, intent);
        finish();
    }
}
