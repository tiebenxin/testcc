package com.lensim.fingerchat.fingerchat.ui.me.collection.note;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

/**
 * 这只是一个简单的ImageView，可以存放Bitmap和Path等信息
 *
 * @author xmuSistone
 */
public class NoteImageView extends AppCompatImageView {

  public static final int TYPE_IMG = 0;
  public static final int TYPE_MAP = 1;

  private int type;
  private String absolutePath;
  private Bitmap bitmap;

  public NoteImageView(Context context) {
    this(context, null);
  }

  public NoteImageView(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public NoteImageView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
  }

  public int getType() {
    return type;
  }

  public void setType(int type) {
    this.type = type;
  }

  public String getAbsolutePath() {
    return absolutePath;
  }

  public void setAbsolutePath(String absolutePath) {
    this.absolutePath = absolutePath;
  }

  public Bitmap getBitmap() {
    return bitmap;
  }

  public void setBitmap(Bitmap bitmap) {
    this.bitmap = bitmap;
  }
}
