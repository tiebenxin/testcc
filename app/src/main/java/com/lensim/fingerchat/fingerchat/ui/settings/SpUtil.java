package com.lensim.fingerchat.fingerchat.ui.settings;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by LY305512 on 2018/1/2.
 */

public class SpUtil {
    private static SharedPreferences sp;
    public static void putString(Context context, String url, String json){
        if(sp==null){
            sp = context.getSharedPreferences("config",Context.MODE_PRIVATE);
        }
        sp.edit().putString(url,json).commit();
    }
    public static String getString(Context context,String url,String json){
        if(sp==null){
            sp = context.getSharedPreferences("config",Context.MODE_PRIVATE);
        }
        return sp.getString(url,json);
    }
}
