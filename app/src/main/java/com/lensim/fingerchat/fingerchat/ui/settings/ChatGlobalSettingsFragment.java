package com.lensim.fingerchat.fingerchat.ui.settings;

import android.os.Bundle;
import com.lensim.fingerchat.commons.utils.SPHelper;
import com.lensim.fingerchat.fingerchat.R;


public class ChatGlobalSettingsFragment extends android.preference.PreferenceFragment {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int factor = SPHelper.getInt("font_size", 1);
        switch (factor) {
            case 0:
            case 1:
                addPreferencesFromResource(R.xml.preference_chat_global);
                break;
            case 2:
                addPreferencesFromResource(R.xml.preference_chat_global_normal);
                break;
            case 3:
                addPreferencesFromResource(R.xml.preference_chat_global_large);
                break;
            case 4:
                addPreferencesFromResource(R.xml.preference_chat_global_max);
                break;
            default:
                addPreferencesFromResource(R.xml.preference_chat_global_max);
                break;
        }
    }

}
