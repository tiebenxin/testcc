package com.lensim.fingerchat.data.work_center.identify;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * date on 2018/1/12
 * author ll147996
 * describe
 */

public class CheckIdCard {

    /**
     * {"Table":[{"ErrorMsg":"没有该用户数据.请联系人事部门补充.","ResultText":""}]}
     */
    @SerializedName("Table")
    public List<TableBean> table;

    public static class TableBean {

        /**
         * ErrorMsg : 验证不通过,请检查工号和身份证号是否正确.
         * ResultText :
         */

        @SerializedName("ErrorMsg")
        public String msg;
        @SerializedName("ResultText")
        public String text;

    }
}
