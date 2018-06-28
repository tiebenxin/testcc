package com.lens.chatmodel.view.spannable;

import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;
import com.lens.chatmodel.R;
import com.lensim.fingerchat.commons.helper.ContextHelper;


public abstract class SpannableClickable extends ClickableSpan implements View.OnClickListener {

  private int DEFAULT_COLOR_ID = R.color.color_8290AF;
  private int textColorId = DEFAULT_COLOR_ID;

  public SpannableClickable() {

  }

  public SpannableClickable(int textColorId) {
    this.textColorId = textColorId;
  }

  @SuppressWarnings("deprecation")
  @Override
  public void updateDrawState(TextPaint ds) {
    super.updateDrawState(ds);

    int colorValue = ContextHelper.getColor(textColorId);
    ds.setColor(colorValue);
    ds.setUnderlineText(false);
    ds.clearShadowLayer();
  }
}
