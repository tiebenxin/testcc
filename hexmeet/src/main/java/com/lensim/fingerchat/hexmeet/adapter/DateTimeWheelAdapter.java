package com.lensim.fingerchat.hexmeet.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.lensim.fingerchat.hexmeet.R;
import com.lensim.fingerchat.hexmeet.utils.ScreenUtil;
import com.lensim.fingerchat.hexmeet.widget.wheel.adapter.AbstractWheelTextAdapter;
import java.util.Calendar;

public class DateTimeWheelAdapter extends AbstractWheelTextAdapter {

  private int count;
  private int type;
  private int year;
  private String unit;
  private int focusColor = Color.parseColor("#313131");
  private int grayColor = Color.parseColor("#919191");
  private int tyStart = ScreenUtil.dp_to_px(8);
  private int tyEnd = ScreenUtil.dp_to_px(17);
  private int byStart = ScreenUtil.dp_to_px(1);
  private int byEnd = ScreenUtil.dp_to_px(15);

  public DateTimeWheelAdapter(Context context, int count, int type, String unit) {
    super(context, R.layout.wheel_text_item, NO_RESOURCE);

    this.count = count;
    this.type = type;
    this.year = Calendar.getInstance().get(Calendar.YEAR);
    this.unit = unit;
  }

  @Override
  public View getItem(int index, View convertView, ViewGroup parent) {
    int val = index;
    switch (type) {
      case 0:
        val += year;
        break;

      case 1:
      case 2:
        val++;
        break;

      case 3:
      case 4:
      default:
    }

    if (index >= 0 && index < getItemsCount()) {
      if (convertView == null) {
        convertView = inflater.inflate(itemResourceId, parent, false);
      }

      TextView itemLabel = (TextView) convertView.findViewById(R.id.number);
      itemLabel.setText(val + unit);

      int current = wheel.getCurrentItem();
      if (current == index) {
        itemLabel.setTextColor(focusColor);
        itemLabel.setTextSize(16);
        itemLabel.getPaint().setFakeBoldText(true);
      } else {
        itemLabel.setTextColor(grayColor);
        itemLabel.setTextSize(15);
        itemLabel.getPaint().setFakeBoldText(false);
        if (current - 2 == index || current - 1 + count == index) {
          LinearGradient lg = new LinearGradient(0, tyStart, 0, tyEnd, Color.WHITE, grayColor,
              Shader.TileMode.CLAMP);
          itemLabel.getPaint().setShader(lg);
        } else if (current + 2 == index || current + 1 - count == index) {
          LinearGradient lg = new LinearGradient(0, byStart, 0, byEnd, grayColor, Color.WHITE,
              Shader.TileMode.CLAMP);
          itemLabel.getPaint().setShader(lg);
        }
      }

      return convertView;
    }

    return null;
  }

  @Override
  public int getItemsCount() {
    return count + 1;
  }

  @Override
  protected CharSequence getItemText(int index) {
    return "";
  }
}
