package com.lens.chatmodel.ui.image;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import android.widget.TextView;
import com.lens.chatmodel.R;
import com.lensim.fingerchat.commons.helper.ContextHelper;
import com.lensim.fingerchat.commons.utils.DensityUtil;
import com.lensim.fingerchat.data.bean.ImageBean;
import com.lensim.fingerchat.commons.base.BaseActivity;
import com.lensim.fingerchat.commons.toolbar.FGToolbar;

import java.io.File;
import java.util.ArrayList;

/**
 * 多图选择
 * Created by Nereo on 2015/4/7.
 * Updated by nereo on 2016/1/19.
 */
public class MultiImageSelectorActivity extends BaseActivity implements
    MultiImageSelectorFragment.Callback {

    /**
     * 最大图片选择次数，int类型，默认9
     */
    public static final String EXTRA_SELECT_COUNT = "max_select_count";
    /**
     * 图片选择模式，默认多选
     */
    public static final String EXTRA_SELECT_MODE = "select_count_mode";
    /**
     * 是否显示相机，默认显示
     */
    public static final String EXTRA_SHOW_CAMERA = "show_camera";
    /**
     * 选择结果，返回为 ArrayList&lt;String&gt; 图片路径集合
     */
    public static final String EXTRA_RESULT = "select_result";

    public static final String EXTRA_RESULT_ORIGIN = "result_origin";
    /**
     * 默认选择集
     */
    public static final String EXTRA_DEFAULT_SELECTED_LIST = "default_list";

    /**
     * 单选
     */
    public static final int MODE_SINGLE = 0;
    /**
     * 多选
     */
    public static final int MODE_MULTI = 1;

    //  private ArrayList<String> resultList = new ArrayList<>();
    private ArrayList<ImageBean> resultBeanList = new ArrayList<>();
    //private Button mSubmitButton;
    private int mDefaultCount;

    private FGToolbar toolbar;


    @Override
    public void initView() {
        setContentView(R.layout.activity_default);
        toolbar = findViewById(R.id.img_selector_toolbar);
        Intent intent = getIntent();
        mDefaultCount = intent.getIntExtra(EXTRA_SELECT_COUNT, 9);
        int mode = intent.getIntExtra(EXTRA_SELECT_MODE, MODE_MULTI);
        boolean isShow = intent.getBooleanExtra(EXTRA_SHOW_CAMERA, true);

        Bundle bundle = new Bundle();
        bundle.putInt(MultiImageSelectorFragment.EXTRA_SELECT_COUNT, mDefaultCount);
        bundle.putInt(MultiImageSelectorFragment.EXTRA_SELECT_MODE, mode);
        bundle.putBoolean(MultiImageSelectorFragment.EXTRA_SHOW_CAMERA, isShow);
        bundle.putParcelableArrayList(MultiImageSelectorFragment.EXTRA_DEFAULT_SELECTED_LIST,
            resultBeanList);

        getSupportFragmentManager().beginTransaction()
            .add(R.id.image_grid,
                Fragment.instantiate(this, MultiImageSelectorFragment.class.getName(), bundle))
            .commit();

    }


    @Override
    public void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);
        initTitleBar();
    }

    private void initTitleBar() {
        initBackButton(toolbar, true);
        toolbar.setTitleText(ContextHelper.getString(R.string.select_image));
        toolbar.initRightView(createConfirmButton());
        toolbar.setConfirmListener(() -> confirm());
    }

    private void confirm() {
        if (resultBeanList != null && resultBeanList.size() > 0) {
            // 返回已选择的图片数据
            Intent data = new Intent();
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList(EXTRA_RESULT, resultBeanList);
            data.putExtras(bundle);
            setResult(RESULT_OK, data);
            finish();
        }
    }

    public TextView createConfirmButton() {
        TextView button = new TextView(this);
        button.setText(ContextHelper.getString(R.string.sure));
        button.setTextColor(ContextHelper.getColor(R.color.white));
        button.setBackground(ContextHelper.getDrawable(R.drawable.green_btn_selector));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT);
        int d = DensityUtil.dip2px(ContextHelper.getContext(), 1);
        params.setMargins(0, 0, d, 0);
        button.setLayoutParams(params);
        button.setPadding(d, d / 2, d, d / 2);
        button.setGravity(Gravity.CENTER);
        return button;
    }

    @Override
    public void onSingleImageSelected(ImageBean bean) {
        Intent data = new Intent();
        resultBeanList.add(bean);
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(EXTRA_RESULT, resultBeanList);
        data.putExtras(bundle);
        setResult(RESULT_OK, data);
        finish();
    }

    @Override
    public void onImageSelected(ImageBean bean) {
        if (!resultBeanList.contains(bean)) {
            resultBeanList.add(bean);
        }
    }

    @Override
    public void onImageUnselected(ImageBean bean) {
        if (resultBeanList.contains(bean)) {
            resultBeanList.remove(bean);
        }
    }

    @Override
    public void onCameraShot(ImageBean bean) {
        if (bean != null) {
            // notify system
            File imageFile = new File(bean.path);
            if (imageFile.exists()) {
                sendBroadcast(
                    new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(imageFile)));
                Intent data = new Intent();
                resultBeanList.add(bean);
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList(EXTRA_RESULT, resultBeanList);
                data.putExtras(bundle);
                setResult(RESULT_OK, data);
                finish();
            }


        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            setResult(RESULT_CANCELED);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
