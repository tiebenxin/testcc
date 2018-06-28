package com.lensim.fingerchat.data;

/**
 * Created by LL130386 on 2018/1/12.
 */

public class ApiEnum {

    /**
     * 发送请求类型不同，所请求的服务器地址也不同
     */
    public enum ERequestType {
        DEFAULT(0),//默认主服务器
        UPLOAD(1),//上传文件服务器
        SEARCH_USER(2),//搜索用户服务器
        /**
         * 数据格式为此类型的
         * {"GetEmpSignInResult":"{\"retCode\":2,\"retMsg\":\"查询成功但无满足条件的结果数据\",\"retData\":null}"}
         */
        MGSON(3),
        SSO_LOGIN(4),//sso登录
        MAIN(5),//正式服务器
        ;

        public final int value;

        ERequestType(int value) {
            this.value = value;
        }

        public static ERequestType fromInt(int value) {
            ERequestType result = null;
            for (ERequestType item : values()) {
                if (item.value == value) {
                    result = item;
                    break;
                }
            }
            if (result == null) {
                throw new IllegalArgumentException("ERequestType - fromInt");
            }
            return result;
        }
    }


}
