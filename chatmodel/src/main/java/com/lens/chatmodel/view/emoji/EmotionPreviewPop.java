package com.lens.chatmodel.view.emoji;

import android.content.Context;
import android.graphics.Point;
import android.util.Pair;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupWindow;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.lens.chatmodel.R;
import com.lens.chatmodel.bean.Emojicon;
import com.lensim.fingerchat.commons.helper.ContextHelper;
import com.lensim.fingerchat.commons.utils.StringUtils;
import java.util.ArrayList;
import java.util.List;

/**
 * @author LL130386 on 2018/3/22.
 */

public class EmotionPreviewPop
    implements View.OnTouchListener, AdapterView.OnItemLongClickListener {


    private final Context context;
    private final int[] gvLoc = new int[2];
    private final List<Pair<Integer, Point>> gifInfos = new ArrayList<>();
    private final List<Pair<String, Point>> gifUrls = new ArrayList<>();

    private int childWidth;
    private int childHeight;


    private GridView vCurGrid;
    private ImageView iv_img;
    private PopupWindow popupWindow;
    private boolean isLocal;

    public EmotionPreviewPop(Context context) {
        this.context = context;

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (popupWindow == null) {
            return false;
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                vCurGrid.getParent().requestDisallowInterceptTouchEvent(true);
                float rawX = event.getRawX();
                float rawY = event.getRawY();
                boolean findTarget = false;

                if (isLocal) {
                    int size = gifInfos.size();
                    for (int i = 0; i < size; i++) {
                        Pair<Integer, Point> info = gifInfos.get(i);
                        Point loc = info.second;
                        if (rawX >= loc.x && rawX <= loc.x + childWidth
                            && rawY > loc.y && rawY < loc.y + childHeight) {
                            int ids = (int) iv_img.getTag(R.id.emotion_uri);
                            if (info.first.equals(ids)) {
                                // 如果是正在播放的gif则返回
                                return false;
                            }

                            findTarget = true;
                            updatePopWindow(info.first, new int[]{info.second.x, info.second.y});
                            break;
                        }
                    }
                } else {
                    int size = gifUrls.size();
                    for (int i = 0; i < size; i++) {
                        Pair<String, Point> info = gifUrls.get(i);
                        Point loc = info.second;
                        if (rawX >= loc.x && rawX <= loc.x + childWidth
                            && rawY > loc.y && rawY < loc.y + childHeight) {
                            String ids = (String) iv_img.getTag(R.id.emotion_uri);
                            if (info.first.equals(ids)) {
                                // 如果是正在播放的gif则返回
                                return false;
                            }

                            findTarget = true;
                            updatePopWindow(info.first, new int[]{info.second.x, info.second.y});
                            break;
                        }
                    }
                }
                if (!findTarget) {
                    pausePop();
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                vCurGrid.getParent().requestDisallowInterceptTouchEvent(false);
                hidePopWindow();
                gifInfos.clear();
                gifUrls.clear();
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        Emojicon emojicon = (Emojicon) parent.getItemAtPosition(position);
        if (emojicon != null) {
            int resId = emojicon.getBigIcon();
            if (resId != 0) {
                isLocal = true;
                int[] loc = new int[2];
                view.getLocationOnScreen(loc);

                vCurGrid = (GridView) parent;
                parent.getLocationOnScreen(gvLoc);
                childWidth = view.getWidth();
                childHeight = view.getHeight();

                showPopWindow(resId, loc);

                int start = parent.getFirstVisiblePosition();
                int end = parent.getLastVisiblePosition();
                for (int i = 0; i <= end - start; i++) {
                    View v = parent.getChildAt(i);
                    if (null == v) {
                        continue;
                    }
                    v.getLocationOnScreen(loc);
                    Emojicon emoj = (Emojicon) parent.getItemAtPosition(i);
                    int rid = emoj.getBigIcon();
                    Point point = new Point(loc[0], loc[1]);
                    Pair<Integer, Point> pair = new Pair<>(rid, point);
                    gifInfos.add(pair);
                }
            } else {
                isLocal = false;
                String url = emojicon.getBigIconPath();
                int[] loc = new int[2];
                view.getLocationOnScreen(loc);

                vCurGrid = (GridView) parent;
                parent.getLocationOnScreen(gvLoc);
                childWidth = view.getWidth();
                childHeight = view.getHeight();

                showPopWindow(url, loc);

                int start = parent.getFirstVisiblePosition();
                int end = parent.getLastVisiblePosition();
                for (int i = 0; i <= end - start; i++) {
                    View v = parent.getChildAt(i);
                    if (null == v) {
                        continue;
                    }
                    v.getLocationOnScreen(loc);
                    Emojicon emoj = (Emojicon) parent.getItemAtPosition(i);
                    String rid = emoj.getBigIconPath();
                    Point point = new Point(loc[0], loc[1]);
                    Pair<String, Point> pair = new Pair<>(rid, point);
                    gifUrls.add(pair);
                }

            }
        }

        return true;
    }


    public void showPopWindow(int resId, int[] loc) {
        View view = View.inflate(context, R.layout.pop_emo_preview, null);
        popupWindow = new PopupWindow(view, 300, 300);
        popupWindow.setBackgroundDrawable(ContextHelper.getDrawable(R.drawable.shape_emoji_pre));
        iv_img = popupWindow.getContentView().findViewById(R.id.iv_img);
        Glide.with(context)
            .load(resId)
            .diskCacheStrategy(DiskCacheStrategy.SOURCE)
            .into(iv_img);
        iv_img.setTag(R.id.emotion_uri, resId);
        popupWindow.showAtLocation(vCurGrid, Gravity.NO_GRAVITY, loc[0],
            loc[1] - popupWindow.getHeight() - 30);
    }

    public void showPopWindow(String url, int[] loc) {
        View view = View.inflate(context, R.layout.pop_emo_preview, null);
        popupWindow = new PopupWindow(view, 300, 300);
        popupWindow.setBackgroundDrawable(ContextHelper.getDrawable(R.drawable.shape_emoji_pre));
        iv_img = popupWindow.getContentView().findViewById(R.id.iv_img);
        Glide.with(context)
            .load(url)
            .diskCacheStrategy(DiskCacheStrategy.SOURCE)
            .into(iv_img);
        iv_img.setTag(R.id.emotion_uri, url);
        popupWindow.showAtLocation(vCurGrid, Gravity.NO_GRAVITY, loc[0],
            loc[1] - popupWindow.getHeight() - 30);
    }

    public void updatePopWindow(int resId, int[] loc) {
        if (popupWindow != null) {
            Glide.with(context)
                .load(resId)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(iv_img);
            iv_img.setTag(R.id.emotion_uri, resId);
            popupWindow.update(loc[0],
                loc[1] - popupWindow.getHeight() - 30, -1, -1, true);
//            System.out.println("pop位置：" + loc[0] + "----" + (loc[1] - popupWindow.getHeight()));

        }
    }

    public void updatePopWindow(String url, int[] loc) {
        if (popupWindow != null) {
            Glide.with(context)
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(iv_img);
            iv_img.setTag(R.id.emotion_uri, url);
            popupWindow.update(loc[0],
                loc[1] - popupWindow.getHeight() - 30, -1, -1, true);
//            System.out.println("pop位置：" + loc[0] + "----" + (loc[1] - popupWindow.getHeight()));
        }
    }

    private void pausePop() {
        if (popupWindow != null) {
            popupWindow.dismiss();
        }
        if (iv_img != null) {
            iv_img.setTag(null);
        }
    }

    public void hidePopWindow() {
        if (popupWindow != null) {
            popupWindow.dismiss();
        }
        popupWindow = null;
    }
}