package com.lensim.fingerchat.hexmeet.widget;

import android.content.Context;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import com.hexmeet.sdk.HexmeetStreamType;
import com.lensim.fingerchat.hexmeet.App;

public class VideoBox extends ViewGroup {

  private SurfaceView surfaceView = null;

  private boolean isFullScreen = false;
  private int orig_l;
  private int orig_t;
  private int orig_r;
  private int orig_b;
  private float mZoomFactor = 1.f;
  private float mZoomCenterX, mZoomCenterY;

  public VideoBox(Context context) {
    super(context);

    setBackgroundColor(0);

    addView(surfaceView = App.getHexmeetSdkInstance().createVideoView(context));
    resetZoom();
  }

  public SurfaceView getSurfaceView() {
    return surfaceView;
  }

  // full screen or restore
  public void reLayout() {
    int r, l, b, t;
    if (isFullScreen) {
      View parent = (View) getParent();
      layout(0, 0, parent.getWidth() + 4, parent.getHeight() + 4);
      l = 0;
      r = parent.getWidth() + 4;
      t = 0;
      b = parent.getHeight() + 4;
    } else {
      layout(orig_l, orig_t, orig_r, orig_b);
      l = orig_l;
      r = orig_r;
      t = orig_t;
      b = orig_b;
    }

    surfaceView.layout(l, t, r, b);

    return;
  }

  @Override
  protected void onLayout(boolean changed, int l, int t, int r, int b) {
    if (l < 0 || t < 0 || r < 0 || b < 0 || l > r || t > b) {
      return;
    }

    if (!isFullScreen) {
      orig_l = l;
      orig_r = r;
      orig_t = t;
      orig_b = b;
    }

    surfaceView.layout(0, 0, r - l, b - t);
  }

  public boolean toggleFullscreen() {
    isFullScreen = !isFullScreen;
    if (isFullScreen) {
      View parent = (View) getParent();
      layout(0, 0, parent.getWidth() + 4, parent.getHeight() + 4);
    } else {
      layout(orig_l, orig_t, orig_r, orig_b);
    }

    surfaceView.setBackgroundColor(0);
    surfaceView.invalidate();
    invalidate();
    resetZoom();

    return isFullScreen;
  }

  public void setVisible(int visibility) {
    setVisibility(visibility);
    getSurfaceView().setVisibility(visibility);
  }

  public void resetZoom() {
    mZoomFactor = 1.f;
    mZoomCenterX = mZoomCenterY = 0.5f;
    applyZoom();
  }

  private boolean applyZoom() {
    HexmeetStreamType streamType = null;
    switch (getId()) {
      case 1:
        streamType = HexmeetStreamType.Video;
        break;
      case 2:
        streamType = HexmeetStreamType.Content;
        break;
      default:
        return false;
    }

    return App.getHexmeetSdkInstance().zoomVideo(mZoomFactor, mZoomCenterX, mZoomCenterY, streamType);
  }

  public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {

    if (mZoomFactor > 1) {
      // Video is zoomed, slide is used to change center of zoom
      if (distanceX > 0 && mZoomCenterX < 1) {
        mZoomCenterX += 0.01;
      } else if (distanceX < 0 && mZoomCenterX > 0) {
        mZoomCenterX -= 0.01;
      }
      if (distanceY < 0 && mZoomCenterY < 1) {
        mZoomCenterY += 0.01;
      } else if (distanceY > 0 && mZoomCenterY > 0) {
        mZoomCenterY -= 0.01;
      }

      if (mZoomCenterX > 1) {
        mZoomCenterX = 1;
      }
      if (mZoomCenterX < 0) {
        mZoomCenterX = 0;
      }
      if (mZoomCenterY > 1) {
        mZoomCenterY = 1;
      }
      if (mZoomCenterY < 0) {
        mZoomCenterY = 0;
      }

      applyZoom();
    }

    return false;
  }

  public boolean onScale(ScaleGestureDetector detector) {

    mZoomFactor *= detector.getScaleFactor();
    // Don't let the object get too small or too large.
    // Zoom to make the video fill the screen vertically
    float portraitZoomFactor = ((float) getHeight())
        / (float) ((9 * getWidth()) / 16);
    // Zoom to make the video fill the screen horizontally
    float landscapeZoomFactor = ((float) getWidth())
        / (float) ((9 * getHeight()) / 16);
    mZoomFactor = Math.max(
        0.1f,
        Math.min(mZoomFactor,
            Math.max(portraitZoomFactor, landscapeZoomFactor)));

    return applyZoom();
  }
}
