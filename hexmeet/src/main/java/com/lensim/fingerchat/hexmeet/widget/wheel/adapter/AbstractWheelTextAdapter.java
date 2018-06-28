//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.lensim.fingerchat.hexmeet.widget.wheel.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.lensim.fingerchat.hexmeet.widget.wheel.WheelView;


public abstract class AbstractWheelTextAdapter extends AbstractWheelAdapter {

  public static final int TEXT_VIEW_ITEM_RESOURCE = -1;
  protected static final int NO_RESOURCE = 0;
  public static final int DEFAULT_TEXT_COLOR = -15724528;
  public static final int LABEL_COLOR = -9437072;
  public static final int DEFAULT_TEXT_SIZE = 24;
  private int textColor;
  private int textSize;
  protected Context context;
  protected LayoutInflater inflater;
  protected int itemResourceId;
  protected int itemTextResourceId;
  protected int emptyItemResourceId;
  protected WheelView wheel;

  public void setWheelView(WheelView wheel) {
    this.wheel = wheel;
  }

  protected AbstractWheelTextAdapter(Context context) {
    this(context, -1);
  }

  protected AbstractWheelTextAdapter(Context context, int itemResource) {
    this(context, itemResource, 0);
  }

  protected AbstractWheelTextAdapter(Context context, int itemResource, int itemTextResource) {
    this.textColor = -15724528;
    this.textSize = 24;
    this.wheel = null;
    this.context = context;
    this.itemResourceId = itemResource;
    this.itemTextResourceId = itemTextResource;
    this.inflater = (LayoutInflater) context.getSystemService("layout_inflater");
  }

  public int getTextColor() {
    return this.textColor;
  }

  public void setTextColor(int textColor) {
    this.textColor = textColor;
  }

  public int getTextSize() {
    return this.textSize;
  }

  public void setTextSize(int textSize) {
    this.textSize = textSize;
  }

  public int getItemResource() {
    return this.itemResourceId;
  }

  public void setItemResource(int itemResourceId) {
    this.itemResourceId = itemResourceId;
  }

  public int getItemTextResource() {
    return this.itemTextResourceId;
  }

  public void setItemTextResource(int itemTextResourceId) {
    this.itemTextResourceId = itemTextResourceId;
  }

  public int getEmptyItemResource() {
    return this.emptyItemResourceId;
  }

  public void setEmptyItemResource(int emptyItemResourceId) {
    this.emptyItemResourceId = emptyItemResourceId;
  }

  protected abstract CharSequence getItemText(int var1);

  public View getItem(int index, View convertView, ViewGroup parent) {
    if (index >= 0 && index < this.getItemsCount()) {
      if (convertView == null) {
        convertView = this.getView(this.itemResourceId, parent);
      }

      TextView textView = this.getTextView(convertView, this.itemTextResourceId);
      if (textView != null) {
        CharSequence text = this.getItemText(index);
        if (text == null) {
          text = "";
        }

        textView.setText((CharSequence) text);
        if (this.wheel != null && this.wheel.getCurrentItem() == index) {
          textView.setTextColor(-16776961);
        }

        if (this.itemResourceId == -1) {
          this.configureTextView(textView);
        }
      }

      return convertView;
    } else {
      return null;
    }
  }

  public View getEmptyItem(View convertView, ViewGroup parent) {
    if (convertView == null) {
      convertView = this.getView(this.emptyItemResourceId, parent);
    }

    if (this.emptyItemResourceId == -1 && convertView instanceof TextView) {
      this.configureTextView((TextView) convertView);
    }

    return convertView;
  }

  protected void configureTextView(TextView view) {
    view.setTextColor(this.textColor);
    view.setGravity(17);
    view.setTextSize((float) this.textSize);
    view.setLines(1);
    view.setTypeface(Typeface.SANS_SERIF, 1);
  }

  private TextView getTextView(View view, int textResource) {
    TextView text = null;

    try {
      if (textResource == 0 && view instanceof TextView) {
        text = (TextView) view;
      } else if (textResource != 0) {
        text = (TextView) view.findViewById(textResource);
      }

      return text;
    } catch (ClassCastException var5) {
      Log.e("AbstractWheelAdapter", "You must supply a resource ID for a TextView");
      throw new IllegalStateException("AbstractWheelAdapter requires the resource ID to be a TextView", var5);
    }
  }

  private View getView(int resource, ViewGroup parent) {
    switch (resource) {
      case -1:
        return new TextView(this.context);
      case 0:
        return null;
      default:
        return this.inflater.inflate(resource, parent, false);
    }
  }
}
