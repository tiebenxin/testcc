package com.lensim.fingerchat.components.springview.container;

import android.view.View;
import com.lensim.fingerchat.components.springview.listener.DragHelper;


/**
 * @author wenqin 2017-08-09 18:00
 */

public abstract class BaseHelper implements DragHelper {

  @Override
  public int getDragLimitHeight(View rootView) {
    return 0;
  }

  @Override
  public int getDragLimitWidth(View rootView) {
    return 0;
  }

  @Override
  public int getDragMaxHeight(View rootView) {
    return 0;
  }

  @Override
  public int getDragMaxWidth(View rootView) {
    return 0;
  }

  @Override
  public int getDragSpringHeight(View rootView) {
    return 0;
  }

  @Override
  public int getDragSpringWidth(View rootView) {
    return 0;
  }
}
