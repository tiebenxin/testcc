package com.lens.chatmodel.bean;

import com.lensim.fingerchat.commons.base.BaseJsonEntity;
import java.util.List;

/**
 * Created by LL130386 on 2018/3/6.
 * 好友分组数据bean
 */

public class RosterGroupBean extends BaseJsonEntity {

    private String name;
    private List<UserBean> users;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<UserBean> getUsers() {
        return users;
    }

    public void setUsers(List<UserBean> users) {
        this.users = users;
    }

    public int getMemberCount() {
        if (users != null) {
            return users.size();
        }
        return 0;
    }
}
