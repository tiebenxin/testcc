package com.lens.chatmodel.ui.image;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.format.Formatter;
import android.util.SparseArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.lens.chatmodel.R;
import com.lensim.fingerchat.commons.base.BaseActivity;
import com.lensim.fingerchat.commons.helper.ContextHelper;
import com.lensim.fingerchat.commons.toolbar.FGToolbar;
import com.lensim.fingerchat.commons.utils.BitmapUtil;
import com.lensim.fingerchat.commons.utils.FileUtil;
import com.lensim.fingerchat.commons.utils.T;
import com.lensim.fingerchat.components.widget.ViewPagerFixed;
import com.lensim.fingerchat.data.bean.ImageBean;
import com.lensim.fingerchat.data.login.UserInfoRepository;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by LY309313 on 2017/4/1.
 */

@SuppressLint("Registered")
public class PhotoPreviewActivity extends BaseActivity implements View.OnClickListener {


    ViewPagerFixed mPhotoPreviewPager;
    ImageView mPhotoPreviewOriginImage;
    TextView mPhotoPreviewOriginSize;
    TextView mPhotoPreviewEdit;
    LinearLayout mPhotoPreviewOrigin;
    ImageView mPhotoPreviewSelectImage;
    LinearLayout mPhotoPreviewSelect;
    private ArrayList<ImageBean> images;
    private ArrayList<ImageBean> selectedImages;
    private ImageBean image;
    private long totalSize;
    private boolean useOrigin;
    private ImagePagerAdapter mPagerAdapter;
    private FGToolbar toolbar;

    @Override
    public void initView() {
        setContentView(R.layout.activity_preview_layout);
        toolbar = findViewById(R.id.mPhotoPreviewToolbar);
        initToolBar();
        mPhotoPreviewPager = findViewById(R.id.mPhotoPreviewPager);
        mPhotoPreviewOriginImage = findViewById(R.id.mPhotoPreviewOriginImage);
        mPhotoPreviewOriginSize = findViewById(R.id.mPhotoPreviewOriginSize);
        mPhotoPreviewEdit = findViewById(R.id.mPhotoPreviewEdit);
        mPhotoPreviewOrigin = findViewById(R.id.mPhotoPreviewOrigin);
        mPhotoPreviewSelectImage = findViewById(R.id.mPhotoPreviewSelectImage);
        mPhotoPreviewSelect = findViewById(R.id.mPhotoPreviewSelect);
        initListener();

    }

    public void initListener() {
        mPhotoPreviewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset,
                int positionOffsetPixels) {
                //   setCustomTitle();

            }

            @Override
            public void onPageSelected(int position) {
                if (images == null) {
                    toolbar.setTitleText((position + 1) + "/" + selectedImages.size());
                    image = selectedImages.get(position);
                    mPhotoPreviewSelectImage.setImageResource(R.drawable.click_check_box);

                } else {
                    toolbar.setTitleText((position + 1) + "/" + images.size());
                    image = images.get(position);
                    if (selectedImages.contains(image)) {
                        mPhotoPreviewSelectImage.setImageResource(R.drawable.click_check_box);
                    } else {
                        mPhotoPreviewSelectImage.setImageResource(R.drawable.check_box);
                    }
                    resetTab(ContextHelper.isVideo(image.path));
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mPhotoPreviewOrigin.setOnClickListener(this);
        mPhotoPreviewSelect.setOnClickListener(this);
        mPhotoPreviewEdit.setOnClickListener(this);
    }

    @Override
    public void initData(Bundle savedInstanceState) {

        Intent intent = getIntent();
        images = intent.getParcelableArrayListExtra(MultiImageSelectorFragment.ALL_DATA_LIST);
        selectedImages = intent
            .getParcelableArrayListExtra(MultiImageSelectorFragment.SELECTED_LIST);
        if (images == null) {
            initPreviewModel(intent);
        } else {
            initLookModel(intent);
        }

        initConfirmButton();

    }

    //预览
    private void initPreviewModel(Intent intent) {
        toolbar.setTitleText((1) + "/" + selectedImages.size());
        mPagerAdapter = new ImagePagerAdapter(getSupportFragmentManager(), selectedImages);
        mPhotoPreviewPager.setAdapter(mPagerAdapter);
        mPhotoPreviewPager.setCurrentItem(0);
        image = selectedImages.get(0);
        resetTab(ContextHelper.isVideo(image.path));
        useOrigin = false;
        mPhotoPreviewOriginImage.setImageResource(R.drawable.btn_radio_off_pressed_holo_dark);
        mPhotoPreviewSelectImage.setImageResource(R.drawable.click_check_box);
        if (selectedImages != null) {
            long size = 0;
            for (ImageBean image : selectedImages) {
                size += FileUtil.getFileSize(image.path);
            }
            totalSize = size;
            if (size > 0) {
                String fileSize = Formatter.formatFileSize(this, size);
                mPhotoPreviewOriginSize.setText("原图(" + fileSize + ")");
            }
        }
    }

    //查看
    private void initLookModel(Intent intent) {
        int index = intent.getIntExtra(MultiImageSelectorFragment.SELECTED_INDEX, 0);
        toolbar.setTitleText((index + 1) + "/" + images.size());
        mPagerAdapter = new ImagePagerAdapter(getSupportFragmentManager(), images);
        mPhotoPreviewPager.setAdapter(mPagerAdapter);
        mPhotoPreviewPager.setCurrentItem(index);
        image = images.get(index);
        resetTab(ContextHelper.isVideo(image.path));
        if (selectedImages.contains(image)) {
            mPhotoPreviewSelectImage.setImageResource(R.drawable.click_check_box);
        }
        useOrigin = false;

        mPhotoPreviewOriginImage.setImageResource(R.drawable.btn_radio_off_pressed_holo_dark);

        if (selectedImages != null) {
            long size = 0;
            for (ImageBean image : selectedImages) {
                size += FileUtil.getFileSize(image.path);
            }
            totalSize = size;
            if (size > 0) {
                String fileSize = Formatter.formatFileSize(this, size);
                mPhotoPreviewOriginSize.setText("原图(" + fileSize + ")");
            }
        }
    }

    private void initToolBar() {
        initBackButton(toolbar, true);
    }

    private void initConfirmButton() {
        //初始化确定按钮
        if (selectedImages.size() > 0) {
            toolbar.setConfirmBt("发送(" + selectedImages.size() + "/9)", new OnClickListener() {
                @Override
                public void onClick(View v) {
                    confirm();
                }
            });
        } else {
            toolbar.setConfirmBt(ContextHelper.getString(R.string.button_send));
        }
    }


    @Override
    public void onClick(View view) {

        int i = view.getId();
        if (i == R.id.mPhotoPreviewOrigin) {
            if (selectedImages.size() >= 9) {
                T.showShort(R.string.max_images);
                return;
            }
            useOrigin = !useOrigin;
            if (useOrigin) {
                mPhotoPreviewOriginImage
                    .setImageResource(R.drawable.btn_radio_on_focused_holo_light);
                if (!selectedImages.contains(image)) {
                    selectImage();
                }
            } else {
                mPhotoPreviewOriginImage
                    .setImageResource(R.drawable.btn_radio_off_pressed_holo_dark);
            }

        } else if (i == R.id.mPhotoPreviewSelect) {
            if (selectedImages.contains(image)) {
                selectedImages.remove(image);

                mPhotoPreviewSelectImage.setImageResource(R.drawable.check_box);
                totalSize -= FileUtil.getFileSize(image.path);
                if (totalSize <= 0) {
                    mPhotoPreviewOriginSize.setText("原图");
                } else {
                    String fileSize = Formatter.formatFileSize(this, totalSize);
                    mPhotoPreviewOriginSize.setText("原图(" + fileSize + ")");
                }

            } else {
                selectImage();
            }
            initConfirmButton();
            mPagerAdapter.notifyDataSetChanged();

        } else if (i == R.id.mPhotoPreviewEdit) {
            Intent intent = new Intent(this, PhotoEditActivity.class);
            intent.putExtra("edit_file_path", image.path);
            startActivityForResult(intent, 1);

        }

    }


    private SparseArray<Fragment> fragmentMap = new SparseArray<>();


    protected void confirm() {
        //把选中的文件发送出去
        // private ArrayList<Image> selectedImages;
        Intent intent = new Intent();
        intent.putExtra("need_to_send", selectedImages);
        intent.putExtra("need_origin", useOrigin);
        setResult(-2, intent);
        finish();

    }

    @Override
    public void backPressed() {
        setResult(RESULT_CANCELED);
        finish();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            image.path = data.getStringExtra("new_file_path");
            int currentItem = mPhotoPreviewPager.getCurrentItem();
            Fragment fragment = fragmentMap.get(currentItem);
            if (fragment instanceof LookUpPhototsFragment) {
                ((LookUpPhototsFragment) fragment).setImagePath(image.path);
            }

            initConfirmButton();
        }
    }


    private class ImagePagerAdapter extends FragmentPagerAdapter {

        private final List<ImageBean> images;

        public ImagePagerAdapter(FragmentManager fm, List<ImageBean> images) {
            super(fm);
            this.images = images;
        }

        @Override
        public Fragment getItem(int position) {

            Fragment fragment = fragmentMap.get(position);
            if (fragment == null) {
                fragment = LookUpPhototsFragment
                    .newInstance(images.get(position).path);
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
                fragmentMap.put(position, (Fragment) object);
            }
        }

        @Override
        public int getCount() {
            return this.images.size();
        }

    }

    public void resetTab(boolean isVidio) {
        if (isVidio) {
            mPhotoPreviewEdit.setVisibility(View.GONE);
            mPhotoPreviewOrigin.setVisibility(View.GONE);
        } else {
            mPhotoPreviewEdit.setVisibility(View.VISIBLE);
            mPhotoPreviewOrigin.setVisibility(View.VISIBLE);
        }
    }

    private void selectImage() {
        if (selectedImages.size() >= 9) {
            T.showShort(R.string.max_images);
            return;
        }
        selectedImages.add(image);
        toolbar.resetConfirmBt("发送(" + selectedImages.size() + "/9)", true);
        mPhotoPreviewSelectImage.setImageResource(R.drawable.click_check_box);
        totalSize += FileUtil.getFileSize(image.path);
        String fileSize = Formatter.formatFileSize(this, totalSize);
        mPhotoPreviewOriginSize.setText("原图(" + fileSize + ")");

    }
}
