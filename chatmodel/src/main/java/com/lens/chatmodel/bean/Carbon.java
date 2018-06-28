package com.lens.chatmodel.bean;

import java.util.List;

/**
 * Created by LY309313 on 2017/5/31.
 */

public class Carbon {


    /**
     * data : [{"account":"张三","content":"heh","name":"ddd","ts":542542542542},{"account":"张三","content":"heh","name":"ddd","ts":542542542542}]
     * username : dddd
     */

    private String username;
    private List<DataBean> data;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * account : 张三
         * content : heh
         * name : ddd
         * ts : 542542542542
         */

        private String account;
        private String content;
        private String name;
        private long ts;
        private String type;

        public String getAccount() {
            return account;
        }

        public void setAccount(String account) {
            this.account = account;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public long getTs() {
            return ts;
        }

        public void setTs(long ts) {
            this.ts = ts;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }
}
