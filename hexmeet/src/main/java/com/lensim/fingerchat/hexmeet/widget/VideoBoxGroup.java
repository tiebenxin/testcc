package com.lensim.fingerchat.hexmeet.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import com.lensim.fingerchat.hexmeet.R;
import com.lensim.fingerchat.hexmeet.utils.ScreenUtil;

@SuppressWarnings("ResourceType")
public class VideoBoxGroup extends ViewGroup implements View.OnTouchListener {

  public static final int little_box_width = ScreenUtil.dp_to_px(160);
  public static final int little_box_height = ScreenUtil.dp_to_px(90);
  public static final int little_box_offset = ScreenUtil.dp_to_px(15);
  public static final int little_boxes_gap = ScreenUtil.dp_to_px(20);

  private VideoBox localBox;
  private VideoBox remoteExpandableBox; // maximized shrunk minimized
  private VideoBox contentBox;

  private boolean isReceivingContent = false;
  private VideoBox fullScreenBox = null;

  public VideoBoxGroup(Context context) {
    super(context);
    setBackgroundResource(R.drawable.background_1024x768);
    buildDefaultCells();
  }

  public VideoBoxGroup(Context context, AttributeSet attrs) {
    super(context, attrs);
    setBackgroundResource(R.drawable.background_1024x768);
    buildDefaultCells();
  }

  private void buildDefaultCells() {
    localBox = newVideoBox(0);
    remoteExpandableBox = newVideoBox(1);
    contentBox = newVideoBox(2);
  }

  private VideoBox newVideoBox(int id) {
    VideoBox box = new VideoBox(getContext());
    box.setId(id);
    box.setBackgroundColor(0xff000000);
    addView(box);

    box.setOnTouchListener(this);

    return box;
  }

  public SurfaceView getLocalVideoSurfaceView() {
    return localBox.getSurfaceView();
  }

  public SurfaceView getRemoteVideoSurfaceView() {
    return remoteExpandableBox.getSurfaceView();
  }

  public SurfaceView getContentVideoSurfaceView() {
    return contentBox.getSurfaceView();
  }

  @Override
  public boolean onTouch(View inputView, MotionEvent event) {
//    log.info("onContentIncoming()");
    if (event.getAction() != MotionEvent.ACTION_DOWN) {
      return true;
    }

    if (null == fullScreenBox) {
      float x = event.getX();
      float y = event.getY();
      if (isLocalVisible && x > localBox.getLeft() && y > localBox.getTop() && x < localBox.getRight()
          && y < localBox.getBottom()) {
        inputView = localBox;
      } else if (isRemoteShrunkVisible && x > remoteExpandableBox.getLeft() && y > remoteExpandableBox.getTop() && x < remoteExpandableBox.getRight()
          && y < remoteExpandableBox.getBottom()) {
        inputView = remoteExpandableBox;
      } else {
        return false;
      }

      if (inputView.getId() == 0) {
        remoteExpandableBox.setVisible(View.GONE);
      }

      if (inputView.getId() == 1) {
        localBox.setVisible(View.GONE);
      }

      if (isReceivingContent) {
        contentBox.setVisible(View.GONE);
      }
    } else {
      localBox.setVisible(View.VISIBLE);
      remoteExpandableBox.setVisible(View.VISIBLE);

      if (isReceivingContent) {
        contentBox.setVisible(View.VISIBLE);
      }
    }

    VideoBox fullScreenCell = (VideoBox) inputView;
    fullScreenBox = fullScreenCell.toggleFullscreen() ? fullScreenCell : null;
    if (null != fullScreenBox) {
      bringChildToFront(fullScreenBox);
    }

    invalidate();
    reLayout();

    isDoubleClicked = false;

    return true;
  }

  public void onContentIncoming() {
//    log.info("onContentIncoming()");
    isReceivingContent = true;

    setRemoteShrunkVisible(true);
    contentBox.setVisible(View.VISIBLE);
    contentBox.resetZoom();
    remoteExpandableBox.resetZoom();
    reLayout();
  }

  public void onContentClosed() {
    if (!isReceivingContent) {
      return;
    }

//    log.info("onContentClosed()");
    isReceivingContent = false;

    setRemoteShrunkVisible(false);
    remoteExpandableBox.setVisible(View.VISIBLE);
    contentBox.setVisible(View.GONE);

    if (fullScreenBox != null && fullScreenBox.getId() == contentBox.getId()) {
      exitFullScreenState();
    }

    reLayout();
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//    log.info("onMeasure() widthMeasureSpec=" + widthMeasureSpec + " heightMeasureSpec=" + heightMeasureSpec);
    final int width = resolveSize(getSuggestedMinimumWidth(), widthMeasureSpec);
    final int height = resolveSize(getSuggestedMinimumHeight(), heightMeasureSpec);
    setMeasuredDimension(width, height);

    int childWidthSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.UNSPECIFIED);
    int childHeightSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.UNSPECIFIED);

    int childCount = getChildCount();
    for (int i = 0; i < childCount; i++) {
      getChildAt(i).measure(childWidthSpec, childHeightSpec);
    }
  }

  @Override
  protected void onLayout(boolean changed, int l, int t, int r, int b) {
//    log.info("onLayout() changed=" + changed);
    if (fullScreenBox != null && (fullScreenBox.getVisibility() == View.VISIBLE)) {
      fullScreenBox.reLayout();
    } else {
      refreshBoxes();
    }

    forceLayout();
    invalidate();
  }

  public void reLayout() {
    if (fullScreenBox != null && (fullScreenBox.getVisibility() == View.VISIBLE)) {
      fullScreenBox.reLayout();
    } else {
      refreshBoxes();
    }

    forceLayout();
    invalidate();
  }

  private void refreshBoxes() {
    int width = getWidth();
    int height = getHeight();
    int r = width - little_box_offset;
    int b = height - little_box_offset;
    int l = r - little_box_width;
    int t = b - little_box_height;

    if (isLocalVisible) {
      localBox.layout(l, t, r, b);
      localBox.invalidate();
    } else {
      localBox.layout(width - 1, 2, width, 3);
      localBox.invalidate();
    }

    if (!isReceivingContent) {
      remoteExpandableBox.layout(0, 0, width, height);
      remoteExpandableBox.invalidate();
    } else {
      if (isRemoteShrunkVisible) {
        if (isLocalVisible) {
          remoteExpandableBox.layout(l - little_box_width - little_boxes_gap, t, l - little_boxes_gap, b);
          remoteExpandableBox.invalidate();
        } else {
          remoteExpandableBox.layout(l, t, r, b);
          remoteExpandableBox.invalidate();
        }
      } else {
        remoteExpandableBox.layout(width - 1, 1, width, 2);
        remoteExpandableBox.invalidate();
      }

      contentBox.layout(0, 0, width, height);
      contentBox.invalidate();
    }
  }

  public boolean isReceivingContent() {
    return isReceivingContent;
  }

  private boolean isLocalVisible = true;

  public boolean isLocalVisible() {
    return isLocalVisible;
  }

  public void setLocalVisible(boolean visible) {
    isLocalVisible = visible;
    reLayout();
  }

  // maximized shrunk minimized
  private boolean isRemoteShrunkVisible = false;

  public boolean isRemoteShrunkVisible() {
    return isRemoteShrunkVisible;
  }

  public void setRemoteShrunkVisible(boolean visible) {
    isRemoteShrunkVisible = visible;
    reLayout();
  }

  @Override
  public boolean onTouchEvent(MotionEvent event) {
    if (isDoubleClicked) {
      isDoubleClicked = false;
      return true;
    }

    return false;
  }

  private long firstClickTime = -1;
  public boolean isDoubleClicked = false;

  @Override
  public boolean onInterceptTouchEvent(MotionEvent ev) {
    if (isDoubleClicked) {
      return false;
    }

    if (ev.getAction() == MotionEvent.ACTION_DOWN) {
      long currentClickTime = System.currentTimeMillis();
      if (currentClickTime - firstClickTime < 250) {
        isDoubleClicked = true;
        return false;
      }

      firstClickTime = currentClickTime;
    }

    return true;
  }

  public boolean isFullScreen() {
    return fullScreenBox != null;
  }

  public void exitFullScreenState() {
    if (fullScreenBox == null) {
      return;
    }

    if (localBox.getId() != fullScreenBox.getId()) {
      localBox.setVisible(View.VISIBLE);
    }

    if (remoteExpandableBox.getId() != fullScreenBox.getId()) {
      remoteExpandableBox.setVisible(View.VISIBLE);
    }

    if (isReceivingContent && contentBox.getId() != fullScreenBox.getId()) {
      contentBox.setVisible(View.VISIBLE);
    }

    fullScreenBox.toggleFullscreen();
    fullScreenBox = null;
  }

  public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
    if (fullScreenBox != null) {
      return fullScreenBox.onScroll(e1, e2, distanceX, distanceY);
    }

    if (isReceivingContent) {
      return contentBox.onScroll(e1, e2, distanceX, distanceY);
    }

    return remoteExpandableBox.onScroll(e1, e2, distanceX, distanceY);
  }

  public boolean onScale(ScaleGestureDetector detector) {
    if (fullScreenBox != null) {
      return fullScreenBox.onScale(detector);
    }

    if (isReceivingContent) {
      return contentBox.onScale(detector);
    }

    return remoteExpandableBox.onScale(detector);
  }
}
