package com.lensim.fingerchat.commons.app;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import com.lensim.fingerchat.commons.interf.IConvert;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by LL130386 on 2017/11/17.
 */

public abstract class BaseConfig {
  private final SharedPreferences _preferences;

  protected BaseConfig(Context context, String name) {
    _preferences = context.getSharedPreferences(name, Context.MODE_PRIVATE);
  }

  protected <T extends Enum<T>> T get(String key, Class<T> aClass, int defaultIndex) {
    return aClass.getEnumConstants()[_preferences.getInt(key, defaultIndex)];
  }

  protected <T extends Enum<T>> void put(String key, T value) {
    final SharedPreferences.Editor edit = _preferences.edit();
    edit.putInt(key, value.ordinal());
    edit.apply();
  }

  protected int get(String key, int defValue) {
    return _preferences.getInt(key, defValue);
  }

  protected void put(String key, int value) {
    final SharedPreferences.Editor edit = _preferences.edit();
    edit.putInt(key, value);
    edit.apply();
  }

  protected long get(String key, long defValue) {
    return _preferences.getLong(key, defValue);
  }

  protected void put(String key, long value) {
    final SharedPreferences.Editor edit = _preferences.edit();
    edit.putLong(key, value);
    edit.apply();
  }

  @SuppressLint("CommitPrefEdits")
  protected void putSync(String key, long value) {
    final SharedPreferences.Editor edit = _preferences.edit();
    edit.putLong(key, value);
    edit.commit();
  }

  protected boolean get(String key, boolean defValue) {
    return _preferences.getBoolean(key, defValue);
  }

  protected void put(String key, boolean value) {
    final SharedPreferences.Editor edit = _preferences.edit();
    edit.putBoolean(key, value);
    edit.apply();
  }

  protected String get(String key, String defValue) {
    return _preferences.getString(key, defValue);
  }

  protected void put(String key, String value) {
    final SharedPreferences.Editor edit = _preferences.edit();
    edit.putString(key, value);
    edit.apply();
  }

  @SuppressLint("CommitPrefEdits")
  protected void putSync(String key, String value) {
    final SharedPreferences.Editor edit = _preferences.edit();
    edit.putString(key, value);
    edit.commit();
  }

  protected Set<String> get(String key, Set<String> defValues) {
    return _preferences.getStringSet(key, defValues);
  }

  protected void put(String key, Set<String> values) {
    final SharedPreferences.Editor edit = _preferences.edit();
    edit.putStringSet(key, values);
    edit.apply();
  }

  @SuppressLint("CommitPrefEdits")
  protected void putSync(String key, Set<String> values) {
    final SharedPreferences.Editor edit = _preferences.edit();
    edit.putStringSet(key, values);
    edit.commit();
  }

  protected void remove(String key) {
    final SharedPreferences.Editor editor = _preferences.edit();
    editor.remove(key);
    editor.apply();
  }

  protected SharedPreferences.Editor edit() {
    return _preferences.edit();
  }

  public void clear() {
    final SharedPreferences.Editor edit = _preferences.edit();
    edit.clear();
    edit.apply();
  }

  protected <T extends Object> void putArray(String key, List<T> array) {
    StringBuilder builder = new StringBuilder();
    for (int i = 0; i < array.size(); i++) {
      T item = array.get(i);
      String result = String.valueOf(item);
      if (!TextUtils.isEmpty(result) && !result.equals("null")) {
        builder.append(result);
        if (i + 1 < array.size()) {
          builder.append(",");
        }
      }
    }
    put(key, builder.toString());
  }

  protected <T extends Object> List<T> getArray(String key, IConvert<String, T> converter) {
    List<T> result = new ArrayList<T>();

    String value = get(key, "");
    if (!TextUtils.isEmpty(value)) {
      String[] values = value.split(",");
      for (String itemString : values) {
        if (!TextUtils.isEmpty(itemString)) {
          T listItem = converter.convert(itemString);
          if (listItem != null) {
            result.add(listItem);
          }
        }
      }
    }

    return result;
  }

  protected void registerOnSharedPreferenceChangeListener(SharedPreferences.OnSharedPreferenceChangeListener listener) {
    _preferences.registerOnSharedPreferenceChangeListener(listener);
  }

}
