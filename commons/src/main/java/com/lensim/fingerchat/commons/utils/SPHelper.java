package com.lensim.fingerchat.commons.utils;


import android.content.Context;
import android.content.SharedPreferences;
import com.lensim.fingerchat.commons.helper.ContextHelper;

/**
 * Created by LL130386 on 2017/11/14.
 */

public class SPHelper {

  public static final String DEFAULT_FILE = "fingerchat";

  public static float getFloat(String fileName, String key, float defValue) {
    SharedPreferences sharedPreferences = getPreferences(fileName);
    return sharedPreferences.getFloat(key, defValue);
  }

  public static int getInt(String fileName, String key, int defValue) {
    SharedPreferences sharedPreferences = getPreferences(fileName);
    return sharedPreferences.getInt(key, defValue);
  }

  public static boolean getBoolean(String fileName, String key, boolean defValue) {
    SharedPreferences sharedPreferences = getPreferences(fileName);
    return sharedPreferences.getBoolean(key, defValue);
  }

  public static String getString(String fileName, String key, String defValue) {
    SharedPreferences sharedPreferences = getPreferences(fileName);

    return sharedPreferences.getString(key, defValue);
  }

  public static float getFloat(String key, float defValue) {
    SharedPreferences sharedPreferences = getPreferences();
    return sharedPreferences.getFloat(key, defValue);
  }

  public static int getInt(String key, int defValue) {
    SharedPreferences sharedPreferences = getPreferences();
    return sharedPreferences.getInt(key, defValue);
  }

  public static boolean getBoolean(String key, boolean defValue) {
    SharedPreferences sharedPreferences = getPreferences();
    return sharedPreferences.getBoolean(key, defValue);
  }

  public static String getString(String key, String defValue) {
    SharedPreferences sharedPreferences = getPreferences();
    return sharedPreferences.getString(key, defValue);
  }

  public static String getString(String key) {
    SharedPreferences sharedPreferences = getPreferences();
    return sharedPreferences.getString(key, "");
  }

  public static void setValue(String fileName, String key, Object value) {
    SharedPreferences sharedPreferences = getPreferences(fileName);
    SharedPreferences.Editor editor = sharedPreferences.edit();
    if (value instanceof String) {
      editor.putString(key, (String) value);
    } else if (value instanceof Integer) {
      editor.putInt(key, (Integer) value);
    } else if (value instanceof Float) {
      editor.putFloat(key, (Float) value);
    } else if (value instanceof Boolean) {
      editor.putBoolean(key, (Boolean) value);
    } else if (value instanceof Long) {
      editor.putLong(key, (Long) value);
    }

    if (value == null) {
      editor.putString(key, "");
    }
    editor.commit();
  }

  public static void saveValue(String key, Object value) {
    SharedPreferences sharedPreferences = getPreferences();
    SharedPreferences.Editor editor = sharedPreferences.edit();
    if (value instanceof String) {
      editor.putString(key, (String) value);
    } else if (value instanceof Integer) {
      editor.putInt(key, (Integer) value);
    } else if (value instanceof Float) {
      editor.putFloat(key, (Float) value);
    } else if (value instanceof Boolean) {
      editor.putBoolean(key, (Boolean) value);
    } else if (value instanceof Long) {
      editor.putLong(key, (Long) value);
    }

    if (value == null) {
      editor.putString(key, "");
    }
    editor.commit();
  }

  public static void remove(String key){
      if (getPreferences().contains(key)) {
          getPreferences().edit().remove(key).apply();
      }
  }

  private static SharedPreferences getPreferences() {
    SharedPreferences pre = ContextHelper.getContext()
        .getSharedPreferences(DEFAULT_FILE, Context.MODE_PRIVATE);
    return pre;
  }

  private static SharedPreferences getPreferences(String file) {
    SharedPreferences pre = ContextHelper.getContext()
        .getSharedPreferences(file, Context.MODE_PRIVATE);
    return pre;
  }

}
