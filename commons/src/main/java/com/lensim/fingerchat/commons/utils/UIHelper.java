package com.lensim.fingerchat.commons.utils;


import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.lensim.fingerchat.commons.R;
import java.lang.reflect.Field;

public class UIHelper {

    public static void setCursorColor(EditText editText, int color) {
        try {
            final Field drawableResField = TextView.class.getDeclaredField("mCursorDrawableRes");
            drawableResField.setAccessible(true);
            final Drawable drawable = getDrawable(editText.getContext(),
                drawableResField.getInt(editText));
            if (drawable == null) {
                return;
            }
            drawable.setColorFilter(color, PorterDuff.Mode.SRC_IN);
            final Object drawableFieldOwner;
            final Class<?> drawableFieldClass;

            final Field editorField = TextView.class.getDeclaredField("mEditor");
            editorField.setAccessible(true);
            drawableFieldOwner = editorField.get(editText);
            drawableFieldClass = drawableFieldOwner.getClass();

            final Field drawableField = drawableFieldClass.getDeclaredField("mCursorDrawable");
            drawableField.setAccessible(true);
            drawableField.set(drawableFieldOwner, new Drawable[]{drawable, drawable});
        } catch (Exception ignored) {
        }
    }

    private static Drawable getDrawable(Context context, int id) {
        return ContextCompat.getDrawable(context, id);
    }


    public static void setTextSize(int base, int factor, TextView... textViews) {
        if (textViews == null || textViews.length <= 0) {
            return;
        }
        for (TextView view : textViews) {
            view.setTextSize(TypedValue.COMPLEX_UNIT_DIP, base + factor);
        }
    }


    public static void setTextSize(int base, TextView... textViews) {
        if (textViews == null || textViews.length <= 0) {
            return;
        }
        int factor = SPHelper.getInt("font_size", 1) * 2;
        for (TextView view : textViews) {
            view.setTextSize(TypedValue.COMPLEX_UNIT_DIP, base + factor);
        }
    }

    public static void setButtonTextSize(int base, Button... buttons) {
        if (buttons == null) {
            return;
        }
        int factor = SPHelper.getInt("font_size", 1) * 2;
        for (Button view : buttons) {
            view.setTextSize(TypedValue.COMPLEX_UNIT_DIP, base + factor);
        }

    }

    public static void setGenderImage(String gender, ImageView iv) {
        if (StringUtils.checkGender(gender)) {
            iv.setImageResource(R.drawable.man);
        } else {
            iv.setImageResource(R.drawable.girl);
        }
    }

    public static void setTextSize2(int base, TextView... textViews) {
        if (textViews == null || textViews.length <= 0) {
            return;
        }
        int factor = SPHelper.getInt("font_size", 1) * 2;
        for (TextView view : textViews) {
            view.setTextSize(TypedValue.COMPLEX_UNIT_DIP, base + factor);
        }
    }
}

