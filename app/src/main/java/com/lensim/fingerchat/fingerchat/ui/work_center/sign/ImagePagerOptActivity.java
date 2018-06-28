package com.lensim.fingerchat.fingerchat.ui.work_center.sign;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import com.lens.chatmodel.ui.image.LookUpPhototsFragment;
import com.lensim.fingerchat.commons.base.BaseActivity;
import com.lensim.fingerchat.fingerchat.R;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by yiw on 2016/1/6.
 *
 */
public class ImagePagerOptActivity extends BaseActivity {
    public static final String INTENT_IMGURLS = "imgurls";
    public static final String INTENT_POSITION = "position";
    public static final String INTENT_HAS_BUTTON= "clockin";
    public static final String INTENT_IMAGESIZE = "imagesize";

    private int startPos;
    private  boolean isFromeClockIn;
    private ArrayList<String> imgUrls;
    private ViewPager viewPager;
    private int deletePos;
    private ImageAdapter mAdapter;

    @Override
    public void initView() {
        setContentView(R.layout.activity_imagepager_opt);
        getIntentData();
//        setToolBarId(R.id.mImageOptToolbar);
//        hasBack(true);
//        if (isFromeClockIn) {
//          hasButton(false);
//        } else {
//          hasButton(true);
//        }

//        setCustomTitle((startPos + 1) + "/" + imgUrls.size());
        viewPager = (ViewPager) findViewById(R.id.pager);

        mAdapter = new ImageAdapter(getSupportFragmentManager());

        viewPager.setAdapter(mAdapter);
        viewPager.setOffscreenPageLimit(1);
        viewPager.setPageTransformer(true, new ZoomOutPageTransformer());
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                deletePos= position;
                position += 1;
//                if(mTvBaseTitle != null)
//                mTvBaseTitle.setText(position + "/" + imgUrls.size());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        viewPager.setCurrentItem(startPos);
        deletePos = startPos;
    }

//    @Override
//    protected void confirm() {
//        int currentItem = viewPager.getCurrentItem();
//        imgUrls.remove(currentItem);
//        fragmentMap.remove(currentItem);
//        if(currentItem == 0){
//            Intent intent = new Intent();
//            intent.putStringArrayListExtra("imgs",imgUrls);
//            setResult(RESULT_OK,intent);
//            finish();
//            return;
//        }
//        mTvBaseTitle.setText((currentItem + 1)+"/" + imgUrls.size());
//        mAdapter.notifyDataSetChanged();
//    }

//    @Override
//    protected void initButtonText() {
//        mBaseBtComfirm.setText("删除");
//    }


    @Override
    public void backPressed() {
        Intent intent = new Intent();
        intent.putStringArrayListExtra("imgs",imgUrls);
        setResult(RESULT_OK,intent);
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            Intent intent = new Intent();
            intent.putStringArrayListExtra("imgs",imgUrls);
            setResult(RESULT_OK,intent);
        }
        return super.onKeyDown(keyCode, event);
    }


    private void getIntentData() {
        startPos = getIntent().getIntExtra(INTENT_POSITION, 0);
        imgUrls = getIntent().getStringArrayListExtra(INTENT_IMGURLS);
        isFromeClockIn = getIntent().getBooleanExtra(INTENT_HAS_BUTTON,false);
    }


    private HashMap<Integer, LookUpPhototsFragment> fragmentMap = new HashMap<Integer, LookUpPhototsFragment>();

    private class ImageAdapter extends FragmentStatePagerAdapter {

        public ImageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            LookUpPhototsFragment fragment = fragmentMap.get(position);
            if (fragment == null) {
                fragment = LookUpPhototsFragment.newInstance(imgUrls.get(position));
                fragmentMap.put(position, fragment);
            }
            return fragment;
        }

        //when activity is recycled, ViewPager will reuse fragment by theirs name, so
        //getItem wont be called, but we need fragmentMap to animate close operation
        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            super.setPrimaryItem(container, position, object);
            if (object instanceof Fragment) {
                fragmentMap.put(position, (LookUpPhototsFragment) object);
            }
        }

        @Override
        public int getItemPosition(Object object) {
            return PagerAdapter.POSITION_NONE;
        }

        @Override
        public int getCount() {
            return imgUrls.size();
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
