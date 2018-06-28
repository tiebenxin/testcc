package com.lens.chatmodel.bean;

import java.util.List;

/**
 * Created by LY309313 on 2016/10/15.
 */

public class SearchUserResult {


    /**
     * "employeeNo":"LL000042",
     * "isEnabled":1,
     * "isValid":1,
     * "nickname":"秋丽",
     * "registerTime":0,
     *  "userId":"ll000042",
     *  "userImage":"C:\HnlensWeb\HnlensImage\Users\ll042\Avatar\headimage.png",
     *  "userLevel":0,
     *  "userMobile":"13667357757",
     *  "userPrivileges":0
     */

    private List<SearchTableBean> content;
    /**
     * msg : 成功获取
     * statuscode : 1
     */

    private String code;

    public List<SearchTableBean> getTable() {
        return content;
    }

    public void setTable(List<SearchTableBean> Table) {
        this.content = Table;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String status) {
        this.code = status;
    }

}
