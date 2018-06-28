package com.lens.chatmodel.group;

/**
 * Created by xhdl0002 on 2018/1/26.
 */

public class MucRequestCode {
    private int groupListRequestCode;//群聊list
    private int uNoDisturbCode;//免打扰
    private int queryOneRoomCode;//查询群info
    private int queryRoomUserCode;//群成员

    public int getQueryRoomUserCode() {
        return queryRoomUserCode;
    }

    public int getQueryOneRoomCode() {
        return queryOneRoomCode;
    }

    public int getuNoDisturbCode() {
        return uNoDisturbCode;
    }


    public int getGroupListRequestCode() {
        return groupListRequestCode;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    private MucRequestCode(Builder builder) {
        groupListRequestCode = builder.groupListRequestCode;
        uNoDisturbCode = builder.uNoDisturbCode;
        queryOneRoomCode = builder.queryOneRoomCode;
        queryRoomUserCode = builder.queryRoomUserCode;
    }

    /**
     * Builder
     */
    public static class Builder {
        private int groupListRequestCode;
        private int uNoDisturbCode;
        private int queryOneRoomCode;
        private int queryRoomUserCode;

        public Builder groupListRequestCode(int val) {
            groupListRequestCode = val;
            return this;
        }

        public Builder uNoDisturbCode(int val) {
            uNoDisturbCode = val;
            return this;
        }

        public Builder queryOneRoomCode(int val) {
            queryOneRoomCode = val;
            return this;
        }

        public Builder queryRoomUserCode(int val) {
            queryRoomUserCode = val;
            return this;
        }

        public MucRequestCode build() {
            return new MucRequestCode(this);
        }
    }

}
