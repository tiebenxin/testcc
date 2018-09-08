package com.lensim.fingerchat.fingerchat.model.result;

/**
 * @time 2017/11/29 8:59
 * @class describe
 */

public class CircleBgImgResponse {

    /**
     * record : {"id":31,"userId":"zze","motto":"","bgImageUrl":"http:\\/\\/mobile.fingerchat.cn:8686\\/group1\\/M00\\/00\\/25\\/CgMJjFtOuAGARlt_AAhQwNP-d00392.jpg","updateTime":1531885636858,"createTime":1531817148000}
     */

    private RecordBean record;

    public RecordBean getRecord() {
        return record;
    }

    public void setRecord(RecordBean record) {
        this.record = record;
    }

    public static class RecordBean {

        /**
         * id : 31
         * userId : zze
         * motto :
         * bgImageUrl : http:\/\/mobile.fingerchat.cn:8686\/group1\/M00\/00\/25\/CgMJjFtOuAGARlt_AAhQwNP-d00392.jpg
         * updateTime : 1531885636858
         * createTime : 1531817148000
         */

        private int id;
        private String userId;
        private String motto;
        private String bgImageUrl;
        private long updateTime;
        private long createTime;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getMotto() {
            return motto;
        }

        public void setMotto(String motto) {
            this.motto = motto;
        }

        public String getBgImageUrl() {
            return bgImageUrl;
        }

        public void setBgImageUrl(String bgImageUrl) {
            this.bgImageUrl = bgImageUrl;
        }

        public long getUpdateTime() {
            return updateTime;
        }

        public void setUpdateTime(long updateTime) {
            this.updateTime = updateTime;
        }

        public long getCreateTime() {
            return createTime;
        }

        public void setCreateTime(long createTime) {
            this.createTime = createTime;
        }
    }
}
