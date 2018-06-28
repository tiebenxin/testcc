package com.lens.chatmodel.utils;

import android.graphics.Typeface;

import com.lensim.fingerchat.commons.helper.ContextHelper;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("HardCodedStringLiteral")
public class FontHelper {

    @SuppressWarnings("HardCodedStringLiteral")
    public enum EFont {
        ROBOTO_LIGHT("fonts/roboto_light.ttf"),
        ROBOTO_REGULAR("fonts/roboto_reqular.ttf"),
        ROBOTO_MEDIUM("fonts/roboto_medium.ttf");

        private String _value;

        EFont(String value) {
            _value = value;
        }

        public String getValue() {
            return _value;
        }
    }

    private static Map<EFont, Typeface> CacheFonts;
    private final static Map<String, EFont> FontKeys;

    static {
        FontKeys = new HashMap<String, EFont>();
        FontKeys.put("RobotoLight", EFont.ROBOTO_LIGHT);
        FontKeys.put("RobotoRegular", EFont.ROBOTO_REGULAR);
        FontKeys.put("RobotoMedium", EFont.ROBOTO_MEDIUM);
    }

    public static EFont getFont(String text, EFont defaultFont) {
        EFont font = FontKeys.get(text);
        if (font == null) {
            font = defaultFont;
        }
        return font;
    }

    public static Typeface getFont(EFont font) {
        if (CacheFonts == null) {
            CacheFonts = new HashMap<EFont, Typeface>();
        }

        if (!CacheFonts.containsKey(font)) {
            try {
                CacheFonts.put(font, Typeface.createFromAsset(ContextHelper.getContext().getAssets(), font.getValue()));
            } catch (Exception exc) {
                return Typeface.DEFAULT;
            }
        }

        return CacheFonts.get(font);
    }

//    public static int prepareFontSize(float sizeInPt) {
//        float result = ((sizeInPt * 1.333333f) * My.Device.getDisplayDpi()) / 72;
//        return (int) result;
//    }
}
