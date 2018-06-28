package com.lens.chatmodel.view.photoedit;

import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.StaticLayout;

/**
 * 字
 *
 * @author Administrator
 */
public class Word {
    private float left, top;
    private Paint paint;
    private Paint paintRect;
    private String wordString;
    //绘制文字的区域
    private RectF rectF;
    private Matrix matrix;
    private StaticLayout layout;

    public float getLeft() {
        return left;
    }

    public void setLeft(float left) {
        this.left = left;
    }

    public float getTop() {
        return top;
    }

    public void setTop(float top) {
        this.top = top;
    }

    public Paint getPaint() {
        return paint;
    }

    public void setPaint(Paint paint) {
        this.paint = paint;
    }

    public String getWordString() {
        return wordString;
    }

    public void setWordString(String wordString) {
        this.wordString = wordString;
    }

    public Word(float left, float top, Paint paint, Paint paintRect, String wordString) {
        super();
        this.left = left;
        this.top = top;
        this.paint = paint;
        this.wordString = wordString;
        this.paintRect = paintRect;
    }

    public Word(float left, float top, Paint paint, Paint paintRect, String wordString, StaticLayout l) {
        super();
        this.left = left;
        this.top = top;
        this.paint = paint;
        this.wordString = wordString;
        this.paintRect = paintRect;
        layout = l;
    }

    public Word() {
        super();
        // TODO Auto-generated constructor stub
    }

    public void setRectF(RectF rectF) {
        this.rectF = rectF;
    }

    public RectF getRectF() {
        return rectF;
    }

    public Paint getPaintRect() {
        return paintRect;
    }

    public Matrix getMatrix() {
        return matrix;
    }

    public void setMatrix(Matrix matrix) {
        this.matrix = matrix;
    }

    public StaticLayout getLayout() {
        return layout;
    }

    public void setLayout(StaticLayout layout) {
        this.layout = layout;
    }
}
