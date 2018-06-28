package com.lensim.fingerchat.commons.utils;

import com.google.gson.Gson;
import com.lensim.fingerchat.commons.bean.BaseListBean;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * json工具JsonUtils.java类
 */
public class JsonUtils {
    public static Gson gson = new Gson();

    public static String toJson(Object obj, Class<?> clazz) {
        return gson.toJson(obj, clazz);
    }

    public static String toJson(Object obj) {
        return gson.toJson(obj);
    }

    public static <T> T fromJson(String json, Class<T> clazz) {
        return gson.fromJson(json, clazz);
    }

    public static <T> T fromJson(String json, Type objectType) {
        return gson.fromJson(json, objectType);
    }

//    /**
//     * 通用解析 无需
//     *
//     * @param json
//     * @param clazz
//     * @return
//     */
//    public static BaseNotBean formJsonNotBean(String json, Class clazz) {
//        Type objectType = type(BaseNotBean.class, clazz);
//        BaseNotBean baseBean = (BaseNotBean) JsonUtils.fromJson(json, objectType);
//        return baseBean;
//    }
//    public static BaseOutVo formJsonOutBean(String json, Class clazz) {
//        Type objectType = type(BaseOutVo.class, clazz);
//        BaseOutVo baseBean = (BaseOutVo) JsonUtils.fromJson(json, objectType);
//        return baseBean;
//    }
//
//    /**
//     * 通用解析 单个
//     *
//     * @param json
//     * @param clazz
//     * @return
//     */
//    public static BaseBean formJsonBean(String json, Class clazz) {
//        Type objectType = type(BaseBean.class, clazz);
//        BaseBean baseBean = (BaseBean) JsonUtils.fromJson(json, objectType);
//        return baseBean;
//    }
//

    /**
     * 通用解析 集合
     *
     * @param json
     * @param clazz
     * @return
     */
    public static BaseListBean formJsonListBean(String json, Class clazz) {
        Type objectType = type(BaseListBean.class, clazz);
        BaseListBean baseBean = (BaseListBean) JsonUtils.fromJson(json, objectType);
        return baseBean;
    }

    private static ParameterizedType type(final Class raw, final Type... args) {
        return new ParameterizedType() {
            public Type getRawType() {
                return raw;
            }

            public Type[] getActualTypeArguments() {
                return args;
            }

            public Type getOwnerType() {
                return null;
            }
        };
    }
}