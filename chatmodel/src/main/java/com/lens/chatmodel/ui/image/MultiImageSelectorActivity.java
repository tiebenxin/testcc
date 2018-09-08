package com.lens.chatmodel.ui.image;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.lens.chatmodel.R;
import com.lens.chatmodel.helper.ImageHelper;
import com.lensim.fingerchat.commons.base.FGActivity;
import com.lensim.fingerchat.commons.helper.ContextHelper;
import com.lensim.fingerchat.commons.toolbar.FGToolbar;
import com.lensim.fingerchat.commons.utils.BitmapUtil;
import com.lensim.fingerchat.commons.utils.DensityUtil;
import com.lensim.fingerchat.commons.utils.FileUtil;
import com.lensim.fingerchat.commons.utils.SPHelper;
import com.lensim.fingerchat.data.bean.ImageBean;
import com.lensim.fingerchat.data.login.UserInfoRepository;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import java.io.File;
import java.util.ArrayList;

/**
 * 多图选择
 * Created by Nereo on 2015/4/7.
 * Updated by nereo on 2016/1/19.
 */
public class MultiImageSelectorActivity extends FGActivity implements
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
            //                    setResultOk();
            addWaterMark();
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
        resultBeanList.add(bean);

        addWaterMark();
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
                resultBeanList.add(bean);
//                    setResultOk();
                addWaterMark();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 101) {
            if (null != data) {
                Bundle bundle = data.getExtras();
                String path = bundle.getString("imagePath");
                File imageFile = new File(path);
                ImageBean bean = new ImageBean(path,
                    "picture_" + System.currentTimeMillis() + ".jpg", 0, "");
                bean.setEdit(true);
                if (imageFile.exists()) {
                    sendBroadcast(
                        new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(imageFile)));
                    resultBeanList.add(bean);
//                    setResultOk();
                    addWaterMark();
                }
            }
        }

    }

    private void setResultOk() {
        dismissProgress();
        Intent data = new Intent();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(EXTRA_RESULT, resultBeanList);
        data.putExtras(bundle);
        setResult(RESULT_OK, data);
        finish();
    }

    private void addWaterMark() {
        showProgress("正在处理...", false);
        if (resultBeanList != null) {
            int len = resultBeanList.size();
            if (len > 0) {
                for (int i = 0; i < len; i++) {
                    ImageBean bean = resultBeanList.get(i);
                    ImageHelper.loadBitmap(bean.path);
                    if (bean.isEdit) {
                        if (i != len - 1) {
                            continue;
                        } else {
                            setResultOk();
                            break;
                        }
                    } else {
                        String path = SPHelper.getString(SPHelper.IMAGE_FILE, bean.path, "");
                        File file = new File(path);
                        if (!TextUtils.isEmpty(path) && file.exists()) {
                            bean.path = path;
                            if (i != len - 1) {
                                continue;
                            } else {
                                setResultOk();
                                break;
                            }
                        } else {
                            doFinishGlide(bean);
                        }
                    }
                }
            }
        }
    }

    private void doFinishGlide(ImageBean bean) {
        Glide.with(ContextHelper.getContext())
            .load(bean.path)
            .asBitmap()
            .skipMemoryCache(true)
            .into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap resource,
                    GlideAnimation<? super Bitmap> glideAnimation) {
                    if (resource != null) {
                        if (resource != null) {
                            String result = FileUtil.createImagePath();
                            String code = BitmapUtil
                                .checkAndCompressBitmap(resource, result);
                            if (code.equals("1")) {
                                resource = BitmapUtil.decodeFile(result);
                                resource = BitmapUtil
                                    .createWaterBitmap(resource,
                                        UserInfoRepository.getUserId());
                                if (resource != null) {
                                    String path = FileUtil
                                        .saveToPicDir(resource, result);
                                    SPHelper.setValue(SPHelper.IMAGE_FILE,
                                        bean.path, path);//存储
                                    bean.path = path;
                                    bean.size =
                                        resource.getWidth() + "x" + resource
                                            .getHeight();
                                }
                            }
                            setResultOk();
                        }
                    }
                }
            });
    }

    private void doFinish(ImageBean bean) {
        Observable.just(bean.path)
            .map(new Function<String, Bitmap>() {
                @Override
                public Bitmap apply(String s) throws Exception {
                    Bitmap bitmap = BitmapUtil.decodeFile(s);
                    if (bitmap != null) {
                        bitmap = BitmapUtil
                            .createWaterBitmap(bitmap,
                                UserInfoRepository.getUserId());
                    }
                    return bitmap;
                }
            }).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .onErrorResumeNext(Observable.empty())
            .subscribe(new Consumer<Bitmap>() {
                @Override
                public void accept(Bitmap bitmap) throws Exception {
                    if (bitmap != null) {
                        String path = FileUtil.saveBitmap(bitmap);
                        SPHelper
                            .setValue(SPHelper.IMAGE_FILE, bean.path, path);//存储
                        bean.path = path;
                        bean.size =
                            bitmap.getWidth() + "x" + bitmap.getHeight();
                    }
                    setResultOk();
                }
            });
    }
}
