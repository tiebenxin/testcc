package com.lens.chatmodel.ui.group;

/**
 * Created by xhdl0002 on 2018/1/12.
 */

public class Constant {

    public static final String KEY_OPERATION = "operation";
    public static final String KEY_SELECT_USER = "selectUsers";
    //创建
    public static final int GROUP_SELECT_MODE_CREATE = 0X11;
    //添加
    public static final int GROUP_SELECT_MODE_ADD = 0X12;
    //移除
    public static final int GROUP_SELECT_MODE_REMOVE = 0X13;
    //单选
    public static final int GROUP_SELECT_MODE_CHANGE_ROLE = 0X14;


    //报告选择模式
    public static final int GROUP_SELECT_MODE_CARD = 0X16;
    //转发消息
    public static final int MODE_TRANSFOR_MSG = 0X17;

    //role 选择模式
    //人
    public static final int ROLE_USER_MODE = 0;
    //组
    public static final int ROLE_GROUP_MODE = 1;


    //分组
    public static final int MODE_GROUP_CREATE = 0X20; //创建分组
    public static final int MODE_GROUP_ADD_MEMEBER = 0X21; //分组添加成员
    public static final int MODE_GROUP_DELE_MEMEBER = 0X22; //分组删除成员

    //
    public static final int SECRETCHAT_ADD = 0X23;

}
