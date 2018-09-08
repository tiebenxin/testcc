package com.lensim.fingerchat.data.bean;

import java.util.List;

/**
 * @time 2017/11/29 8:59
 * @class describe
 */

public class GetFavoListResponse {

    /**
     * page : {"pageIndex":0,"pageSize":10,"total":1}
     * data : [{"msgId":"95b7c541-9515-4a5e-a755-856afdbbb197","msgType":1,"from":"zze","fromNickname":"轻松王","provider":"zze","creator":"wshh0007","tags":"重要","msgContent":"{\"userHeadImageStr\":\"http:\\/\\/mobile.fingerchat.cn:8686\\/group2\\/M00\\/00\\/FE\\/CgMJklqnbpqAS6wmAAEtRqD1vk4324.png\",\"signContent\":\"\",\"messageType\":\"1\",\"type\":\"0\",\"userName\":\"wshh0007\",\"recordTime\":\"2018-08-08 15:41:44\",\"content\":\"无语了\"}","creationTime":1533714107000}]
     */

    private PageBean page;
    private List<DataBean> data;

    public PageBean getPage() {
        return page;
    }

    public void setPage(PageBean page) {
        this.page = page;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class PageBean {

        /**
         * pageIndex : 0
         * pageSize : 10
         * total : 1
         */

        private int pageIndex;
        private int pageSize;
        private int total;

        public int getPageIndex() {
            return pageIndex;
        }

        public void setPageIndex(int pageIndex) {
            this.pageIndex = pageIndex;
        }

        public int getPageSize() {
            return pageSize;
        }

        public void setPageSize(int pageSize) {
            this.pageSize = pageSize;
        }

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }
    }

    public static class DataBean {

        /**
         * msgId : 95b7c541-9515-4a5e-a755-856afdbbb197
         * msgType : 1
         * from : zze
         * fromNickname : 轻松王
         * provider : zze
         * creator : wshh0007
         * tags : 重要
         * msgContent : {"userHeadImageStr":"http:\/\/mobile.fingerchat.cn:8686\/group2\/M00\/00\/FE\/CgMJklqnbpqAS6wmAAEtRqD1vk4324.png","signContent":"","messageType":"1","type":"0","userName":"wshh0007","recordTime":"2018-08-08 15:41:44","content":"无语了"}
         * creationTime : 1533714107000
         */

        private String msgId;
        private int msgType;
        private String from;
        private String fromNickname;
        private String provider;
        private String creator;
        private String tags;
        private String msgContent;
        private long creationTime;

        public String getMsgId() {
            return msgId;
        }

        public void setMsgId(String msgId) {
            this.msgId = msgId;
        }

        public int getMsgType() {
            return msgType;
        }

        public void setMsgType(int msgType) {
            this.msgType = msgType;
        }

        public String getFrom() {
            return from;
        }

        public void setFrom(String from) {
            this.from = from;
        }

        public String getFromNickname() {
            return fromNickname;
        }

        public void setFromNickname(String fromNickname) {
            this.fromNickname = fromNickname;
        }

        public String getProvider() {
            return provider;
        }

        public void setProvider(String provider) {
            this.provider = provider;
        }

        public String getCreator() {
            return creator;
        }

        public void setCreator(String creator) {
            this.creator = creator;
        }

        public String getTags() {
            return tags;
        }

        public void setTags(String tags) {
            this.tags = tags;
        }

        public String getMsgContent() {
            return msgContent;
        }

        public void setMsgContent(String msgContent) {
            this.msgContent = msgContent;
        }

        public long getCreationTime() {
            return creationTime;
        }

        public void setCreationTime(long creationTime) {
            this.creationTime = creationTime;
        }
    }
}
