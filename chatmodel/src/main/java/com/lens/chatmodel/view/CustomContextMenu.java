package com.lens.chatmodel.view;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.lens.chatmodel.R;
import com.lens.chatmodel.interf.IChatRoomModel;
import com.lensim.fingerchat.commons.utils.TDevice;


/**
 * Created by LY309313 on 2016/12/26.
 */

public class CustomContextMenu extends FrameLayout implements View.OnClickListener {

  private OnMenuListener listener;
  private View targetView;
  private TextView menuTransmit;

  public static final int COPY = 1 << 1;
  public static final int ADD_EX = 1 << 2;
  public static final int TRANSMIT = 1 << 3;
  public static final int COLLECTION = 1 << 4;
  public static final int CANCLE = 1 << 5;
  public static final int DEL = 1 << 6;
  public static final int MORE = 1 << 7;


  private boolean canCopy;
  private boolean canAddEx;
  private boolean canTransmit;
  private boolean canCollection;
  private boolean canCancle;
  private boolean canDel;
  private boolean canMore;
  private TextView menuCopy;
  private TextView menuDel;
  private TextView menuEx;
  private TextView menuCancel;
  private TextView menuCollection;
  private TextView menuMore;
  private IChatRoomModel model;


  public CustomContextMenu(Context context, AttributeSet attrs, int defStyleAttr) {
    this(context, attrs);
  }

  public CustomContextMenu(Context context, AttributeSet attrs) {
    super(context, attrs);
    init(context, attrs);
  }

  public CustomContextMenu(Context context) {
    this(context, (AttributeSet) null);
  }

  private void init(Context context, AttributeSet attrs) {
    LayoutInflater.from(context).inflate(R.layout.context_menu, this);
    menuCopy = findViewById(R.id.menuCopy);
    menuDel = findViewById(R.id.menuDel);
    menuEx = findViewById(R.id.menuEx);
    menuCancel = findViewById(R.id.menuCancel);
    menuCollection = findViewById(R.id.menuCollection);
    menuMore = findViewById(R.id.menuMore);

    menuTransmit = findViewById(R.id.menuTransmit);
    canCopy = true;
    canDel = true;
    menuCopy.setOnClickListener(this);
    menuDel.setOnClickListener(this);
    menuEx.setOnClickListener(this);
    menuCancel.setOnClickListener(this);
    menuTransmit.setOnClickListener(this);
    menuCollection.setOnClickListener(this);
    menuMore.setOnClickListener(this);
    setOnClickListener(this);
    hide();
  }


  public void setCollectionText(String txt) {
    menuCollection.setText(txt);
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    int width = MeasureSpec.getSize(widthMeasureSpec);
    View view = getChildAt(0);
    int childW = MeasureSpec.makeMeasureSpec(width - 200, MeasureSpec.EXACTLY);
    view.measure(childW, view.getMeasuredHeight());

  }

  private Rect targetRect = new Rect();
  private Rect viewRect = new Rect();

  @Override
  protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
    super.onLayout(changed, left, top, right, bottom);
    //targetView是消息气泡
    if (targetView != null) {
      //先获取到这个视图的顶部
      //计算一下目标view顶部到屏幕顶部的距离，如果不够，就显示到下面去
      targetView.getGlobalVisibleRect(targetRect);
      System.out.println("目标view的全局可见区域是:" + targetRect.left + "/"
          + targetRect.top + "/" + targetRect.right + "/" + targetRect.bottom);

      getGlobalVisibleRect(viewRect);
      System.out.println(
          "容器目标view的全局可见区域是:" + viewRect.left + "/" + viewRect.top + "/" + viewRect.right + "/"
              + viewRect.bottom);
      View view = this.getChildAt(0);
      //如果目标高度超出一屏则显示在中间
      int dtop = targetRect.top - viewRect.top;
      int dbottom = viewRect.bottom - targetRect.bottom;
      if (dtop < TDevice.dpToPixel(50) && dbottom < TDevice.dpToPixel(50)) {
        int t = (int) ((viewRect.bottom - viewRect.top) / 2 + TDevice.dpToPixel(20));
        view.layout(100, t, 100 + view.getMeasuredWidth(), t + view.getMeasuredHeight());
      } else if (dtop < TDevice.dpToPixel(50)) {
        int t = targetView.getBottom();
        view.layout(100, (int) (t + TDevice.dpToPixel(10)), 100 + view.getMeasuredWidth(),
            (int) (t + TDevice.dpToPixel(10) + view.getMeasuredHeight()));
      } else {
        int t = targetView.getTop();
        view.layout(100, (int) (t + TDevice.dpToPixel(10) - view.getMeasuredHeight()),
            100 + view.getMeasuredWidth(), (int) (t + TDevice.dpToPixel(10)));
      }
    }
  }

  public CustomContextMenu appendCopy() {
    if (!canCopy) {
      menuCopy.setVisibility(GONE);
    } else {
      menuCopy.setVisibility(VISIBLE);
    }
    return this;
  }

  public CustomContextMenu appendTransmit() {
    if (!canTransmit) {
      menuTransmit.setVisibility(GONE);
    } else {
      menuTransmit.setVisibility(VISIBLE);
    }
    return this;
  }

  public CustomContextMenu appendAddEx() {
    if (!canAddEx) {
      menuEx.setVisibility(GONE);
    } else {
      menuEx.setVisibility(VISIBLE);
    }
    return this;
  }

  public CustomContextMenu appendCollection() {
    if (!canCollection) {
      menuCollection.setVisibility(GONE);
    } else {
      menuCollection.setVisibility(VISIBLE);
    }
    return this;
  }

  public CustomContextMenu appendCancel() {
    if (!canCancle) {
      menuCancel.setVisibility(GONE);
    } else {
      menuCancel.setVisibility(VISIBLE);
    }
    return this;
  }

  public CustomContextMenu appendMore() {
    if (!canMore) {
      menuCancel.setVisibility(GONE);
    } else {
      menuCancel.setVisibility(VISIBLE);
    }
    return this;
  }

  public CustomContextMenu appendDele() {
    if (!canDel) {
      menuDel.setVisibility(GONE);
    } else {
      menuDel.setVisibility(VISIBLE);
    }
    return this;
  }

  public void show(View view, OnMenuListener listener) {
    if (!canCopy) {
      menuCopy.setVisibility(GONE);
    } else {
      menuCopy.setVisibility(VISIBLE);
    }

    if (!canTransmit) {
      menuTransmit.setVisibility(GONE);
    } else {
      menuTransmit.setVisibility(VISIBLE);
    }

    if (!canAddEx) {
      menuEx.setVisibility(GONE);
    } else {
      menuEx.setVisibility(VISIBLE);
    }
    if (!canCollection) {
      menuCollection.setVisibility(GONE);
    } else {
      menuCollection.setVisibility(VISIBLE);
    }
    if (!canCancle) {
      menuCancel.setVisibility(GONE);
    } else {
      menuCancel.setVisibility(VISIBLE);
    }
    if (!canDel) {
      menuDel.setVisibility(GONE);
    } else {
      menuDel.setVisibility(VISIBLE);
    }
    if (!canMore) {
      menuMore.setVisibility(GONE);
    } else {
      menuMore.setVisibility(VISIBLE);
    }

    this.setVisibility(VISIBLE);
    this.targetView = view;
    this.listener = listener;
    requestLayout();
  }

  public void hide() {

    this.setVisibility(GONE);
    this.targetView = null;
    this.listener = null;
  }


  @Override
  public void onClick(View v) {
    int pos = -1;
    int i = v.getId();
    if (i == R.id.menuCopy) {
      pos = COPY;
    } else if (i == R.id.menuDel) {
      pos = DEL;
    } else if (i == R.id.menuTransmit) {
      pos = TRANSMIT;
    } else if (i == R.id.menuEx) {
      pos = ADD_EX;
    } else if (i == R.id.menuCancel) {
      pos = CANCLE;
    } else if (i == R.id.menuCollection) {
      pos = COLLECTION;
    } else if (i == R.id.menuMore) {
      pos = MORE;
    } else {
      hide();
    }

    if (pos != -1) {
      if (listener != null && model != null) {
        listener.OnSelect(model, pos);
        hide();
      }
    }
  }


  public CustomContextMenu setCanAddEx(boolean canAddEx) {
    this.canAddEx = canAddEx;
    return this;
  }

  public CustomContextMenu setCanCopy(boolean canCopy) {
    this.canCopy = canCopy;
    return this;
  }

  public CustomContextMenu setCanTransmit(boolean canTransmit) {
    this.canTransmit = canTransmit;
    return this;
  }

  public CustomContextMenu setCanCollection(boolean canCollection) {
    this.canCollection = canCollection;
    return this;
  }

  public CustomContextMenu setCanCancle(boolean canCancle) {
    this.canCancle = canCancle;
    return this;
  }

  public CustomContextMenu setCanDel(boolean canDel) {
    this.canDel = canDel;
    return this;
  }

  public CustomContextMenu setCanMore(boolean canMore) {
    this.canMore = canMore;
    return this;
  }

  public CustomContextMenu bindData(IChatRoomModel m) {
    model = m;
    return this;
  }


  public interface OnMenuListener {

    void OnSelect(IChatRoomModel model, int pos);
  }
}
