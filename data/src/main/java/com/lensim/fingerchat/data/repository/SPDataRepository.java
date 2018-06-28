package com.lensim.fingerchat.data.repository;


import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.util.Base64;
import com.lensim.fingerchat.commons.helper.ContextHelper;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;

/**
 * Created by ll147996 on 2017/12/15.
 * sp数据存储管理类
 * 存储 Object 类数据
 */

public class SPDataRepository<T extends Serializable> {

    private static final String LIST = ".list";

    public SPDataRepository() {

    }


    public T getData(Class<T> clazz) {
        String fileNema = clazz.getName();
        String key = clazz.getSimpleName();
        return getData(fileNema, key);
    }

    public List<T> getDatas(Class<T> clazz) {
        String fileNema = clazz.getName() + LIST;
        String key = clazz.getSimpleName();
        return getDatas(fileNema, key);
    }


    public void saveData(@NonNull T data) {
        String fileNema = data.getClass().getName();
        String key = data.getClass().getSimpleName();
        saveData(data, fileNema, key);
    }


    public void saveDatas(@NonNull List<T> datas, Class<T> clazz) {
        String fileNema = clazz.getName() + LIST;
        String key = clazz.getSimpleName();
        saveDatas(datas,fileNema,key);
    }

    /**
     * 获取List型数据
     * @param fileNema 文件名
     * @param key 键
     * @return List
     */
    public List<T> getDatas(String fileNema, String key) {
        SharedPreferences sharedPreferences = ContextHelper.getContext()
            .getSharedPreferences(fileNema, Context.MODE_PRIVATE);
        String string = sharedPreferences.getString(key, null);
        if (string != null) {
            List<T> object = String2List(string);
            return object;
        } else {
            return null;
        }
    }


    /**
     * 获取List型数据
     * @param fileNema 文件名
     * @param key 键
     * @return List
     */
    public T getData(String fileNema, String key) {
        SharedPreferences sharedPreferences = ContextHelper.getContext()
            .getSharedPreferences(fileNema, Context.MODE_PRIVATE);
        String string = sharedPreferences.getString(key, null);
        if (string != null) {
            T object = String2Object(string);
            return object;
        } else {
            return null;
        }
    }


    /**
     * 保存数据
     * @param fileNema 文件名
     * @param key 键
     */
    public void saveData(@NonNull T data, String fileNema, String key) {
        SharedPreferences sharedPreferences = ContextHelper.getContext()
            .getSharedPreferences(fileNema, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String string = Object2String(data);
        editor.putString(key, string);
        editor.commit();
    }



    /**
     * 保存List型数据
     * @param fileNema 文件名
     * @param key 键
     */
    public void saveDatas(@NonNull List<T> datas, String fileNema, String key) {

        SharedPreferences sharedPreferences = ContextHelper.getContext()
            .getSharedPreferences(fileNema, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String string = List2String(datas);
        editor.putString(key, string);
        editor.commit();
    }


    /**
     * writeObject 方法负责写入特定类的对象的状态，以便相应的 readObject 方法可以还原它
     * 最后，用Base64.encode将字节文件转换成Base64编码保存在String中
     * @param object 待加密的转换为String的对象
     * @return String   加密后的String
     */
    private String Object2String(T object) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = null;
        try {
            objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(object);
            String string = new String(
                Base64.encode(byteArrayOutputStream.toByteArray(), Base64.DEFAULT));
            objectOutputStream.close();
            return string;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * writeObject 方法负责写入特定类的对象的状态，以便相应的 readObject 方法可以还原它
     * 最后，用Base64.encode将字节文件转换成Base64编码保存在String中
     * @param object 待加密的转换为String的对象
     * @return String   加密后的String
     */
    private String List2String(List<T> object) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = null;
        try {
            objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(object);
            String string = new String(
                Base64.encode(byteArrayOutputStream.toByteArray(), Base64.DEFAULT));
            objectOutputStream.close();
            return string;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * 使用Base64解密String，返回Object对象
     * @param objectString 待解密的String
     * @return object      解密后的object
     */
    private T String2Object(String objectString) {
        byte[] mobileBytes = Base64.decode(objectString.getBytes(), Base64.DEFAULT);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(mobileBytes);
        ObjectInputStream objectInputStream = null;
        try {
            objectInputStream = new ObjectInputStream(byteArrayInputStream);
            T object = (T) objectInputStream.readObject();
            objectInputStream.close();
            return object;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }


    /**
     * 使用Base64解密String，返回Object对象
     * @param objectString 待解密的String
     * @return object      解密后的object
     */
    private List<T> String2List(String objectString) {
        byte[] mobileBytes = Base64.decode(objectString.getBytes(), Base64.DEFAULT);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(mobileBytes);
        ObjectInputStream objectInputStream = null;
        try {
            objectInputStream = new ObjectInputStream(byteArrayInputStream);
            List<T> object = (List<T>) objectInputStream.readObject();
            objectInputStream.close();
            return object;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
