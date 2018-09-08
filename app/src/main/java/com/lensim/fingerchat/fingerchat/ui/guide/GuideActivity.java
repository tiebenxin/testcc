package com.lensim.fingerchat.fingerchat.ui.guide;

import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import com.lensim.fingerchat.commons.base.FGActivity;
import com.lensim.fingerchat.fingerchat.R;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by LL130386 on 2018/5/14.
 */

public class GuideActivity extends FGActivity {

    int PAGER_NUM;

    private ViewPager viewPager;
    private TextView tv_botton;
    private int[] drawableIds = new int[]{R.drawable.guide_page, R.drawable.guide_page,
        R.drawable.guide_page};

    @Override
    public void initView() {
        setContentView(R.layout.activity_guide);
        viewPager = findViewById(R.id.viewpager);
        tv_botton = findViewById(R.id.tv_button);

        ImageViewPagerAdapter mAdapter = new ImageViewPagerAdapter(getImageViewList());
        viewPager.setAdapter(mAdapter);
        viewPager.addOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset,
                int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == PAGER_NUM - 1) {
                    tv_botton.setVisibility(View.VISIBLE);
                } else {
                    tv_botton.setVisibility(View.GONE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private List<View> getImageViewList() {
        List<View> list = new ArrayList<>();
        ImageView imageView;
        PAGER_NUM = drawableIds.length;
        for (int i = 0; i < PAGER_NUM; i++) {
            imageView = new ImageView(this);
            imageView.setImageResource(drawableIds[i]);
            imageView.setScaleType(ScaleType.CENTER_CROP);
            list.add(imageView);
        }
        return list;
    }
}
