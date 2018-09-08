package com.lens.chatmodel.bean.body;

import com.lensim.fingerchat.commons.base.BaseJsonEntity;

/**
 * Created by LL130386 on 2018/6/13.
 */


public class PushEntity extends BaseJsonEntity {

/*    {
        "from" : "小秘书", //从小秘书推送来的
        "username" : "mytip", //从小秘书推送来的
        "body" : {
        "title" : "OA通知标题",
            "content" : "OA通知内容",
            "action_url" : "OA通知点击跳转URL",
            "img_url" : "图片地址",
            "level" : "OA通知紧急等级", //紧急，急，一般
            "status" : "OA状态"             //待审批...
    }
        "type" : "OA" //推送的OA流程通知消息  该字段规定 body 格式
    }*/

    String from;
    String username;
    String type;
    PushBody body;

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public PushBody getBody() {
        return body;
    }

    public void setBody(PushBody body) {
        this.body = body;
    }
}
