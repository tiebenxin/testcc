package com.lens.chatmodel.bean;

import com.lensim.fingerchat.commons.base.BaseJsonEntity;
import com.lensim.fingerchat.commons.interf.IChatUser;
import java.util.List;

/**
 * Created by LL130386 on 2018/3/6.
 * 好友分组数据bean
 */

public class RosterGroupBean extends BaseJsonEntity {

    private String name;
    private List<IChatUser> users;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<IChatUser> getUsers() {
        return users;
    }

    public void setUsers(List<IChatUser> users) {
        this.users = users;
    }

    public int getMemberCount(){
        if (users != null){
            return users.size();
        }
        return 0;
    }
}
