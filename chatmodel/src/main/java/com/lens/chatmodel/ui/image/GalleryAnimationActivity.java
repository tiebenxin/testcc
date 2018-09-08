package com.lens.chatmodel.ui.image;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.lens.chatmodel.R;
import com.lens.chatmodel.bean.body.ImageUploadEntity;
import com.lensim.fingerchat.commons.helper.AnimationRect;
import com.lensim.fingerchat.commons.helper.ContextHelper;
import com.lensim.fingerchat.commons.utils.AnimationUtility;
import com.lensim.fingerchat.commons.utils.L;
import com.lensim.fingerchat.data.bean.LongImageBean;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GalleryAnimationActivity extends FragmentActivity {

    private static final int STATUS_BAR_HEIGHT_DP_UNIT = 25;

    private ArrayList<AnimationRect> rectList;
    private List<String> urls = new ArrayList<>();
    private ArrayList<String> msgids;

    private ViewPager pager;
    private TextView position;
    private View background;

    private int initPosition;

    private ColorDrawable backgroundColor;

    private boolean msgMode;
    private ArrayList<LongImageBean> longList;
    private String collectInfo;

    public static Intent newIntent(ArrayList<String> ThumbnailPicUrls,
        ArrayList<String> msgids,
        ArrayList<AnimationRect> rectList, ArrayList<LongImageBean> longList,
        int initPosition,String collectInfo) {
        Intent intent = new Intent(ContextHelper.getContext(),
            GalleryAnimationActivity.class);
        intent.putExtra("msg", ThumbnailPicUrls);
        intent.putExtra("msgids", msgids);
        intent.putExtra("rect", rectList);
        intent.putExtra("position", initPosition);
        intent.putExtra("isLong", longList);
        intent.putExtra("collectInfo",collectInfo);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery_animation_layout);

        rectList = getIntent().getParcelableArrayListExtra("rect");
        longList = getIntent().getParcelableArrayListExtra("isLong");
        msgids = getIntent().getStringArrayListExtra("msgids");
        collectInfo = getIntent().getStringExtra("collectInfo");
        if (msgids == null || msgids.size() <= 0) {
            msgMode = false;
        } else {
            msgMode = true;
        }
        ArrayList<String> tmp = getIntent().getStringArrayListExtra("msg");
//        ArrayList<String> tmp = msg.getThumbnailPicUrls();
        for (int i = 0; i < tmp.size(); i++) {
            if (!TextUtils.isEmpty(tmp.get(i))) {
                urls.add(tmp.get(i));
            }
        }

        boolean disableHardwareLayerType = false;

        for (String path : urls) {
            if (!TextUtils.isEmpty(path)) {
                ImageUploadEntity entity = ImageUploadEntity.fromJson(path);
                if (entity != null) {
                    if (entity.getOriginalUrl().contains(".gif")) {
                        disableHardwareLayerType = true;
                        break;
                    }
                }
            }
        }

        position = (TextView) findViewById(R.id.position);
        initPosition = getIntent().getIntExtra("position", 0);

        pager = (ViewPager) findViewById(R.id.pager);
//        pager.setOffscreenPageLimit(0);//限定预加载个数
        pager.setAdapter(new ImagePagerAdapter(getSupportFragmentManager()));

        final boolean finalDisableHardwareLayerType = disableHardwareLayerType;
        pager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                GalleryAnimationActivity.this.position.setText(String.valueOf(position + 1));
            }

            @Override
            public void onPageScrollStateChanged(int scrollState) {
                if (scrollState != ViewPager.SCROLL_STATE_IDLE && finalDisableHardwareLayerType) {
                    final int childCount = pager.getChildCount();
                    for (int i = 0; i < childCount; i++) {
                        View child = pager.getChildAt(i);
                        if (child.getLayerType() != View.LAYER_TYPE_NONE) {
                            child.setLayerType(View.LAYER_TYPE_NONE, null);
                        }
                    }
                }
            }
        });
        pager.setCurrentItem(getIntent().getIntExtra("position", 0));
        pager.setOffscreenPageLimit(1);
        pager.setPageTransformer(true, new ZoomOutPageTransformer());

        TextView sum = (TextView) findViewById(R.id.sum);
        sum.setText(String.valueOf(urls.size()));

        background = AnimationUtility.getAppContentView(this);

        if (savedInstanceState != null) {
            showBackgroundImmediately();
        }
    }

    private HashMap<Integer, ContainerFragment> fragmentMap
        = new HashMap<Integer, ContainerFragment>();

    private boolean alreadyAnimateIn = false;

    private class ImagePagerAdapter extends FragmentPagerAdapter {

        public ImagePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            ContainerFragment fragment = fragmentMap.get(position);
            if (fragment == null) {

                boolean animateIn = (initPosition == position) && !alreadyAnimateIn;
                if (urls.get(position) != null) {
                    fragment = ContainerFragment
                        .newInstance(urls.get(position),
                            msgMode ? getRect(msgids.get(position), rectList)
                                : rectList.get(position),
                            getLongBean(position), animateIn, initPosition == position,collectInfo,position);
                    alreadyAnimateIn = true;
                    fragmentMap.put(position, fragment);
                    String s = "false";
                    if (animateIn) {
                        s = "true";
                    }
                    L.i(this.getClass().getSimpleName() + "fragmentMap=" + fragmentMap.size()
                        + "  animateIn = "
                        + s);
                }

            }
            L.i(this.getClass().getSimpleName() + "fragmentMap=" + fragmentMap.size());

            return fragment;
        }

        private LongImageBean getLongBean(int position) {
            LongImageBean bean;
            if (longList == null) {
                bean = new LongImageBean();
                bean.setLongImage(false);
            } else {
                bean = msgMode ? getLongImageBean(msgids.get(position), longList)
                    : longList.get(position);
            }
            return bean;
        }

        private AnimationRect getRect(String s, ArrayList<AnimationRect> rectList) {
            if (rectList == null) {
                return null;
            }
            for (AnimationRect rect : rectList) {
                if (s.equals(rect.getMsgId())) {
                    return rect;
                }
            }
            return null;
        }

        private LongImageBean getLongImageBean(String id, ArrayList<LongImageBean> longList) {
            if (longList == null) {
                return null;
            }
            for (LongImageBean longBean : longList) {
                if (id.equals(longBean.getId())) {
                    return longBean;
                }
            }
            return null;
        }

        //when activity is recycled, ViewPager will reuse fragment by theirs name, so
        //getItem wont be called, but we need fragmentMap to animate close operation
        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            super.setPrimaryItem(container, position, object);
            if (object instanceof Fragment) {
                fragmentMap.put(position, (ContainerFragment) object);
            }
        }

        @Override
        public int getCount() {
            return urls.size();
        }
    }

    public void showBackgroundImmediately() {
        if (background.getBackground() == null) {
            backgroundColor = new ColorDrawable(Color.BLACK);
            background.setBackground(backgroundColor);
        }
    }

    public ObjectAnimator showBackgroundAnimate() {
        backgroundColor = new ColorDrawable(Color.BLACK);
        background.setBackground(backgroundColor);
        ObjectAnimator bgAnim = ObjectAnimator
            .ofInt(backgroundColor, "alpha", 0, 255);
        bgAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                background.setBackground(backgroundColor);
            }
        });
        return bgAnim;
    }

    @Override
    public void onBackPressed() {

        ContainerFragment fragment = fragmentMap.get(pager.getCurrentItem());
        if (fragment != null && fragment.canAnimateCloseActivity()) {
            backgroundColor = new ColorDrawable(Color.BLACK);
            ObjectAnimator bgAnim = ObjectAnimator.ofInt(backgroundColor, "alpha", 0);
            bgAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    background.setBackgroundDrawable(backgroundColor);
                }
            });
            bgAnim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    GalleryAnimationActivity.super.finish();
                    overridePendingTransition(-1, -1);
                }
            });
            fragment.animationExit(bgAnim);
        } else {
            super.onBackPressed();
        }
    }


    static class ZoomOutPageTransformer implements ViewPager.PageTransformer {
        private static float MIN_SCALE = 0.85f;
        private static float MIN_ALPHA = 0.5f;

        public void transformPage(View view, float position) {
            int pageWidth = view.getWidth();
            int pageHeight = view.getHeight();

            if (position < -1) { // [-Infinity,-1)
                // This page is way off-screen to the left.
                view.setAlpha(0);
            } else if (position <= 1) { // [-1,1]
                // Modify the default slide transition to shrink the page as well
                float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
                float vertMargin = pageHeight * (1 - scaleFactor) / 2;
                float horzMargin = pageWidth * (1 - scaleFactor) / 2;
                if (position < 0) {
                    view.setTranslationX(horzMargin - vertMargin / 2);
                } else {
                    view.setTranslationX(-horzMargin + vertMargin / 2);
                }

                // Scale the page down (between MIN_SCALE and 1)
                view.setScaleX(scaleFactor);
                view.setScaleY(scaleFactor);

                // Fade the page relative to its size.
                view.setAlpha(MIN_ALPHA +
                    (scaleFactor - MIN_SCALE) /
                        (1 - MIN_SCALE) * (1 - MIN_ALPHA));
            } else { // (1,+Infinity]
                // This page is way off-screen to the right.
                view.setAlpha(0);
            }
        }
    }
}
