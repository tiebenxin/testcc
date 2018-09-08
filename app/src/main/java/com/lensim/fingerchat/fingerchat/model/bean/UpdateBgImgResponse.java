package com.lensim.fingerchat.fingerchat.model.bean;

/**
 * @time 2017/11/29 8:59
 * @class describe
 */

public class UpdateBgImgResponse {

    /**
     * record : {"id":null,"userId":"wshh0007","motto":"","bgImageUrl":"http://mobile.fingerchat.cn:8686/group1/M00/01/51/CgMJjFtw6pCAVIueAAqoTigGyfg402.jpg","updateTime":1534126800021,"createTime":1534126800021}
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
         * id : null
         * userId : wshh0007
         * motto :
         * bgImageUrl : http://mobile.fingerchat.cn:8686/group1/M00/01/51/CgMJjFtw6pCAVIueAAqoTigGyfg402.jpg
         * updateTime : 1534126800021
         * createTime : 1534126800021
         */

        private Object id;
        private String userId;
        private String motto;
        private String bgImageUrl;
        private long updateTime;
        private long createTime;

        public Object getId() {
            return id;
        }

        public void setId(Object id) {
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
