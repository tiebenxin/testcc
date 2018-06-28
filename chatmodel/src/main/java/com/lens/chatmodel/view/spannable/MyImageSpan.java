package com.lens.chatmodel.view.spannable;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.style.ImageSpan;

/**
 * Created by LY309313 on 2017/1/6.
 */

public class MyImageSpan extends ImageSpan {
    public MyImageSpan(Bitmap b) {
        super(b);
    }

    public MyImageSpan(Bitmap b, int verticalAlignment) {
        super(b, verticalAlignment);
    }

    public MyImageSpan(Context context, Bitmap b) {
        super(context, b);
    }

    public MyImageSpan(Context context, Bitmap b, int verticalAlignment) {
        super(context, b, verticalAlignment);
    }

    public MyImageSpan(Drawable d) {
        super(d);
    }

    public MyImageSpan(Drawable d, int verticalAlignment) {
        super(d, verticalAlignment);
    }

    public MyImageSpan(Drawable d, String source) {
        super(d, source);
    }

    public MyImageSpan(Drawable d, String source, int verticalAlignment) {
        super(d, source, verticalAlignment);
    }

    public MyImageSpan(Context context, Uri uri) {
        super(context, uri);
    }

    public MyImageSpan(Context context, Uri uri, int verticalAlignment) {
        super(context, uri, verticalAlignment);
    }

    public MyImageSpan(Context context, int resourceId) {
        super(context, resourceId);
    }

    public MyImageSpan(Context context, int resourceId, int verticalAlignment) {
        super(context, resourceId, verticalAlignment);
    }

    @Override
    public int getSize(Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {
        Drawable drawable = getDrawable();
        Rect rect = drawable.getBounds();
        if(fm!=null){
            Paint.FontMetricsInt fontMetricsInt = paint.getFontMetricsInt();
            int fontHeight = fontMetricsInt.bottom - fontMetricsInt.top;

            int drHeight = rect.bottom - rect.top;

            int top = drHeight / 2 - fontHeight/4;
            int bottom = drHeight /2 + fontHeight/4;

            fm.ascent  = -bottom;
            fm.top = -bottom;
            fm.bottom = top;
            fm.descent = top;

        }
        return rect.right;
    }

    @Override
    public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint) {
        // image to draw

        Drawable b = getDrawable();
        // font metrics of text to be replaced
        Paint.FontMetricsInt fm = paint.getFontMetricsInt();
        int transY = (y + fm.descent + y + fm.ascent) / 2
                - b.getBounds().bottom / 2;

        canvas.save();
        canvas.translate(x, transY);
        b.draw(canvas);
        canvas.restore();
    }
}
