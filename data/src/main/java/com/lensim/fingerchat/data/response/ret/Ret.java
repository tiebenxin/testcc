package com.lensim.fingerchat.data.response.ret;

/**
 * date on 2018/1/24
 * author ll147996
 * describe 只做标记用
 */

public class Ret {

    /**
     * GetEmpSignInResult : {"retCode":2,"retMsg":"查询成功但无满足条件的结果数据","retData":null}
     */

    private GetEmpSignInResultBean GetEmpSignInResult;

    public GetEmpSignInResultBean getGetEmpSignInResult() {
        return GetEmpSignInResult;
    }

    public void setGetEmpSignInResult(GetEmpSignInResultBean GetEmpSignInResult) {
        this.GetEmpSignInResult = GetEmpSignInResult;
    }

    public static class GetEmpSignInResultBean {

        /**
         * retCode : 2
         * retMsg : 查询成功但无满足条件的结果数据
         * retData : null
         */

        private int retCode;
        private String retMsg;
        private Object retData;

        public int getRetCode() {
            return retCode;
        }

        public void setRetCode(int retCode) {
            this.retCode = retCode;
        }

        public String getRetMsg() {
            return retMsg;
        }

        public void setRetMsg(String retMsg) {
            this.retMsg = retMsg;
        }

        public Object getRetData() {
            return retData;
        }

        public void setRetData(Object retData) {
            this.retData = retData;
        }
    }



}
