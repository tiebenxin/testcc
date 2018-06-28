
package com.lens.chatmodel.helper;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.text.TextUtils;
import com.lens.chatmodel.R;
import com.lensim.fingerchat.commons.helper.ContextHelper;

/**
 * 管理一些通用的设置
 */
public class SettingsManager implements
    OnSharedPreferenceChangeListener {

    private static final SettingsManager instance;

    static {
        instance = new SettingsManager();
    }

    private SettingsManager() {
        getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    public static SettingsManager getInstance() {
        return instance;
    }

    private static SharedPreferences getSharedPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(ContextHelper.getContext());
    }

    private static int getInt(int key, int def) {
        String value = getString(key, def);
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return Integer.parseInt(ContextHelper.getString(def));
        }
    }

    private static boolean getBoolean(int key, boolean def) {
        return getSharedPreferences().getBoolean(ContextHelper.getString(key), def);
    }

    private static boolean getBoolean(int key, int def) {
        return getBoolean(key, ContextHelper.getResources().getBoolean(def));
    }

    private static void setBoolean(int key, boolean value) {
        Editor editor = getSharedPreferences().edit();
        editor.putBoolean(ContextHelper.getString(key), value);
        editor.commit();
    }

    private static String getString(int key, String def) {
        return getSharedPreferences().getString(ContextHelper.getString(key), def);
    }

    private static String getString(int key, int def) {
        return getString(key, ContextHelper.getString(def));
    }

    private static void setString(int key, String value) {
        Editor editor = getSharedPreferences().edit();
        editor.putString(ContextHelper.getString(key), value);
        editor.commit();
    }


    private static Uri getSound(int key, Uri defaultUri, int defaultResource) {
        String defaultValue = ContextHelper.getString(defaultResource);
        String value = getString(key, defaultValue);
        if (TextUtils.isEmpty(value)) {
            return null;
        }
        if (defaultValue.equals(value)) {
            setString(key, defaultUri.toString());
            return defaultUri;
        }
        return Uri.parse(value);
    }


    /**
     * 通知栏声音
     */
    public static Uri eventsSound() {
        return getSound(R.string.events_sound_key,
            Settings.System.DEFAULT_NOTIFICATION_URI,
            R.string.events_sound_default);
    }

    public static boolean eventsVibro() {
        return getBoolean(R.string.events_vibro_key,
            R.bool.events_vibro_default);
    }

    public static boolean eventsShowText() {
        return getBoolean(R.string.events_show_text_key,
            R.bool.events_show_text_default);
    }

    public static boolean eventsShowNotify() {
        return getBoolean(R.string.events_notify_key, R.bool.events_notify_default);
    }

    public static boolean eventsAutoEnterMuc() {
        return getBoolean(R.string.events_auto_enter_muc, R.bool.events_auto_enter_muc_default);
    }

    public static boolean eventsVisibleChat() {
        return getBoolean(R.string.events_visible_chat_key,
            R.bool.events_visible_chat_default);
    }

    public static boolean eventsFirstOnly() {
        return getBoolean(R.string.events_first_only_key,
            R.bool.events_first_only_default);
    }

    public static boolean chatsVoiceByOuter() {
        return getBoolean(R.string.chats_voice_outer_key,
            R.bool.chats_voice_outer_default);
    }

    public static boolean chatsSendByEnter() {
        return getBoolean(R.string.chats_send_by_enter_key,
            R.bool.chats_send_by_enter_default);
    }

    public static boolean chatsAttention() {
        return getBoolean(R.string.chats_attention_key,
            R.bool.chats_attention_default);
    }

    public static boolean securityCheckCertificate() {
        return getBoolean(R.string.security_check_certificate_key,
            R.bool.security_check_certificate_default);
    }

    /**
     * 接收到抖动时的声音
     */
    public static Uri chatsAttentionSound() {
        return getSound(R.string.chats_attention_sound_key,
            Settings.System.DEFAULT_RINGTONE_URI,
            R.string.chats_attention_sound_default);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
        String key) {
//        if (key.equals(SPHelper.getString(
//                R.string.events_show_text_key))) {
//            NotifyManager.getInstance().onMessageNotification();
//        } else if (key.equals(SPHelper.getString(
//                R.string.chats_attention_key))) {
//            AttentionManager.getInstance().onSettingsChanged();
//        }
    }

}