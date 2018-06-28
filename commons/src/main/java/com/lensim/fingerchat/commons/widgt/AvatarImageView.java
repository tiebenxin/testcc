package com.lensim.fingerchat.commons.widgt;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.Style;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.text.TextUtils;
import android.util.AttributeSet;
import com.lensim.fingerchat.commons.R;
import com.lensim.fingerchat.commons.helper.ContextHelper;
import com.lensim.fingerchat.commons.utils.DensityUtil;

import java.util.List;

/**
 * Created by LL130386 on 2018/5/4.
 * 自定义头像view
 */

public class AvatarImageView extends AppCompatImageView {

    private List<String> mTextList;
    private Paint paint;
    private int textColor;
    private int textSize;
    private boolean isPrivate;

    public AvatarImageView(Context context) {
        this(context, null);
    }

    public AvatarImageView(Context context,
        @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AvatarImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.AvatarImageView);
        int DEFAULT_TEXT_SIZE = DensityUtil.sp2px(ContextHelper.getContext(), 12);
        textSize = a.getInt(R.styleable.AvatarImageView_textSize, DEFAULT_TEXT_SIZE);
        int DEFAULT_TEXT_COLOR = Color.parseColor("#FFFFFF");
        textColor = a.getColor(R.styleable.AvatarImageView_textColor, DEFAULT_TEXT_COLOR);
        a.recycle();
        setBackgroundColor(ContextHelper.getColor(R.color.custom_divider_color));//默认背景色
        if (mTextList != null) {
            mTextList.clear();
        }
    }

    /*
    * 设置是否是私聊
    * */
    public void setChatType(boolean isPrivate) {
        this.isPrivate = isPrivate;
        clearDrawText();
        drawableStateChanged();
    }

    private void clearDrawText() {
        if (mTextList != null) {
            mTextList.clear();
        }
    }

    public void setDrawText(List<String> list) {
        isPrivate = false;
        mTextList = list;
        setImageDrawable(ContextHelper.getDrawable(R.drawable.default_avatar_muc));//设置群聊头像背景
        drawableStateChanged();
    }

    public void setTextColor(int color) {
        textColor = color;
        if (paint == null) {
            paint = new Paint();
        }
        paint.setColor(textColor);
        drawableStateChanged();
    }

    public void setTextSize(int size) {
        textSize = size;
        if (paint == null) {
            paint = new Paint();
        }
        paint.setTextSize(textSize);
        drawableStateChanged();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!isPrivate) {
            if (mTextList != null && mTextList.size() > 0) {
                if (paint == null) {
                    paint = new Paint();
                    paint.setColor(textColor);
                    paint.setStrokeWidth(2);
                    paint.setStyle(Style.FILL);
                    paint.setTextSize(textSize);
                }
                drawText(paint, canvas);
            }
        }
    }

    public void drawText(Paint paint, Canvas canvas) {
        int len = mTextList.size();
        boolean isMore = false;
        if (len <= 4) {
            isMore = false;
        } else if (len > 4) {
            isMore = true;
        }
        if (!isMore) {
            for (int i = 0; i < len; i++) {
                if (i == 0) {
                    drawFirst(paint, canvas, mTextList.get(0));
                } else if (i == 1) {
                    drawSecond(paint, canvas, mTextList.get(1));
                } else if (i == 2) {
                    drawThird(paint, canvas, mTextList.get(2));
                } else if (i == 3) {
                    drawForth(paint, canvas, mTextList.get(3));
                }
            }
        } else {
            drawFirst(paint, canvas, mTextList.get(0));
            drawSecond(paint, canvas, mTextList.get(1));
            drawThird(paint, canvas, mTextList.get(2));
            drawForth(paint, canvas, mTextList.get(3));
        }
    }

    private void drawFirst(Paint paint, Canvas canvas, String text) {
        if (TextUtils.isEmpty(text)) {
            return;
        }
        text = checkString(text);
        float textWidth = paint.measureText(text);
        FontMetrics fm = paint.getFontMetrics();
        float textHeight = (float) Math.ceil(fm.descent - fm.top);
        int width = canvas.getWidth();
        int height = canvas.getHeight();
        float txtLeft = width / 4 - textWidth / 2;
        float txtTop = height / 2 - textHeight / 2;
        canvas.drawText(text, txtLeft, txtTop, paint);
    }

    private void drawSecond(Paint paint, Canvas canvas, String text) {
        if (TextUtils.isEmpty(text)) {
            return;
        }
        text = checkString(text);
        float textWidth = paint.measureText(text);
        FontMetrics fm = paint.getFontMetrics();
        float textHeight = (float) Math.ceil(fm.descent - fm.top);
        int width = canvas.getWidth();
        int height = canvas.getHeight();
        float txtLeft = width * 3 / 4 - textWidth / 2;
        float txtTop = height / 2 - textHeight / 2;
        canvas.drawText(text, txtLeft, txtTop, paint);
    }

    private void drawThird(Paint paint, Canvas canvas, String text) {
        if (TextUtils.isEmpty(text)) {
            return;
        }
        text = checkString(text);
        float textWidth = paint.measureText(text);
        FontMetrics fm = paint.getFontMetrics();
        float textHeight = (float) Math.ceil(fm.descent - fm.top);
        int width = canvas.getWidth();
        int height = canvas.getHeight();
        float txtLeft = width / 4 - textWidth / 2;
        float txtTop = height / 2 + textHeight;
        canvas.drawText(text, txtLeft, txtTop, paint);
    }

    private void drawForth(Paint paint, Canvas canvas, String text) {
        if (TextUtils.isEmpty(text)) {
            return;
        }
        text = checkString(text);
        float textWidth = paint.measureText(text);
        FontMetrics fm = paint.getFontMetrics();
        float textHeight = (float) Math.ceil(fm.descent - fm.top);
        int width = canvas.getWidth();
        int height = canvas.getHeight();
        float txtLeft = width * 3 / 4 - textWidth / 2;
        float txtTop = height / 2 + textHeight;
        canvas.drawText(text, txtLeft, txtTop, paint);
    }

    private String checkString(String text) {
        String content = null;
        if (!TextUtils.isEmpty(text)) {
            int len = text.length();
            content = text.substring(len - 1, len);
        }
        return content;
    }
}
