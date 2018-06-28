package com.lens.chatmodel.ui.emoji;


import static com.lensim.fingerchat.commons.app.AppConfig.REQUEST_IMAGE;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.lens.chatmodel.R;
import com.lens.chatmodel.bean.body.ImageUploadEntity;
import com.lens.chatmodel.ui.image.MultiImageSelectorActivity;
import com.lensim.fingerchat.data.help_class.IUploadListener;
import com.lensim.fingerchat.commons.app.AppConfig;
import com.lensim.fingerchat.commons.base.FGActivity;
import com.lensim.fingerchat.commons.global.CommonEnum.EUploadFileType;
import com.lensim.fingerchat.commons.helper.ContextHelper;
import com.lensim.fingerchat.commons.helper.EmojiHelper;
import com.lensim.fingerchat.commons.toolbar.FGToolbar;
import com.lensim.fingerchat.commons.utils.SPHelper;
import com.lensim.fingerchat.commons.utils.StringUtils;
import com.lensim.fingerchat.commons.utils.T;
import com.lensim.fingerchat.data.bean.ImageBean;
import com.lens.chatmodel.net.HttpUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by LY309313 on 2017/6/15.
 */

public class ExpressionActivity extends FGActivity {

    RecyclerView exList;
    TextView toFront;
    TextView exDelete;
    FrameLayout exBottom;
    private ExAdapter adapter;
    private List<String> strings;
    private boolean changed;
    private FGToolbar toolbar;


    @Override
    public void initView() {
        setContentView(R.layout.activity_expression);
        exList = findViewById(R.id.ex_list);
        toFront = findViewById(R.id.toFront);
        exDelete = findViewById(R.id.exDelete);
        exBottom = findViewById(R.id.exBottom);
        toolbar = findViewById(R.id.viewTitleBar);
        initToolBar();
        GridLayoutManager manager = new GridLayoutManager(this, 4);
        exList.setLayoutManager(manager);
        initListener();

    }

    private void initToolBar() {
        toolbar.setTitleText(ContextHelper.getString(R.string.emoji_manage));
        initBackButton(toolbar, true);
        toolbar.setConfirmBt(ContextHelper.getString(R.string.manage), new OnClickListener() {
            @Override
            public void onClick(View v) {
                doConfirm();
            }
        });
    }


    @Override
    public void initData(Bundle savedInstanceState) {
        String data = SPHelper.getString(AppConfig.EX_KEY, "");
        if (!StringUtils.isEmpty(data)) {
            String[] split = data.split(";");
            strings = new ArrayList<>(Arrays.asList(split));
        } else {
            strings = new ArrayList<>();
        }

        adapter = new ExAdapter(this, strings);
        exList.setAdapter(adapter);
        exList.setItemAnimator(new DefaultItemAnimator());
        exList.addItemDecoration(new GridDivider(this));
    }

    public void initListener() {
        toFront.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<String> selectImages = adapter.getSelectImages();
                if (selectImages.isEmpty()) {
                    return;
                }
                strings.removeAll(selectImages);
                strings.addAll(0, selectImages);
                String data = EmojiHelper.listToString(strings);
                SPHelper.saveValue(AppConfig.EX_KEY, data);
                adapter.addImage(strings);
                adapter.notifyDataSetChanged();
                adapter.clearSelectImages();
                changed = true;

            }

        });

        exDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<String> selectImages = adapter.getSelectImages();
                if (selectImages.isEmpty()) {
                    return;
                }
                strings.removeAll(selectImages);
                String data = EmojiHelper.listToString(strings);
                SPHelper.saveValue(AppConfig.EX_KEY, data);
                adapter.addImage(strings);
                adapter.notifyDataSetChanged();
                adapter.clearSelectImages();
                changed = true;
            }
        });
    }


    private void doConfirm() {
        if (exBottom.getVisibility() == View.GONE) {
            exBottom.setVisibility(View.VISIBLE);
            adapter.setShowChecked(true);
            toolbar.setConfirmBt(ContextHelper.getString(R.string.action_done));
        } else {
            exBottom.setVisibility(View.GONE);
            adapter.setShowChecked(false);
            toolbar.setConfirmBt(ContextHelper.getString(R.string.manage));
        }
    }


    @Override
    public void backPressed() {
        Intent intent = new Intent();
        intent.putExtra("ex_changed", changed);
        setResult(RESULT_OK, intent);
        super.backPressed();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent();
            intent.putExtra("ex_changed", changed);
            setResult(RESULT_OK, intent);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private class ExAdapter extends RecyclerView.Adapter {

        private LayoutInflater inflater;
        private Context context;
        private List<String> images;
        private List<String> selectImages;
        private boolean showChecked;

        ExAdapter(Context context, List<String> images) {
            inflater = LayoutInflater.from(context);
            this.context = context;
            this.images = images;
            selectImages = new ArrayList<>();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = inflater.inflate(R.layout.wrap_image, parent, false);
            return new ExViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            final ExViewHolder viewHolder = (ExViewHolder) holder;
            if (position == images.size()) {
                viewHolder.checkBox.setVisibility(View.GONE);
                viewHolder.imageview.setImageResource(R.drawable.icon_addpic_unfocused);
                viewHolder.imageview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, MultiImageSelectorActivity.class);
                        intent.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, false);
                        intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, 1);
                        intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE,
                            MultiImageSelectorActivity.MODE_SINGLE);
                        startActivityForResult(intent, REQUEST_IMAGE);
                    }
                });
            } else {
                String json = images.get(position);
                ImageUploadEntity entity = ImageUploadEntity.fromJson(json);
                if (entity != null) {
                    Glide.with(context).load(entity.getOriginalUrl())
                        .placeholder(R.drawable.ease_default_expression)
                        .fitCenter()
                        .diskCacheStrategy(
                            DiskCacheStrategy.SOURCE).into(viewHolder.imageview);
                }

                if (showChecked) {
                    viewHolder.checkBox.setVisibility(View.VISIBLE);
                    final String path = images.get(position);
                    if (selectImages.contains(path)) {
                        viewHolder.checkBox.setImageResource(R.drawable.click_check_box);
                    } else {
                        viewHolder.checkBox.setImageResource(R.drawable.check_box);
                    }
                    viewHolder.imageview.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (selectImages.contains(path)) {
                                selectImages.remove(path);
                                viewHolder.checkBox.setImageResource(R.drawable.check_box);
                            } else {
                                selectImages.add(path);
                                viewHolder.checkBox.setImageResource(R.drawable.click_check_box);
                            }
                        }
                    });
                } else {
                    viewHolder.checkBox.setVisibility(View.GONE);
                }
            }
        }


        public List<String> getSelectImages() {
            return selectImages;
        }

        public void clearSelectImages() {
            if (selectImages != null) {
                selectImages.clear();
            }
        }

        @Override
        public int getItemCount() {
            return images.size() + 1;
        }

        void addImage(List<String> images) {

            this.images = images;

        }

        public void setShowChecked(boolean showChecked) {
            this.showChecked = showChecked;
            notifyDataSetChanged();
        }

        private class ExViewHolder extends RecyclerView.ViewHolder {

            private final ImageView imageview;
            private final ImageView checkBox;

            public ExViewHolder(View itemView) {
                super(itemView);
                imageview = (ImageView) itemView.findViewById(R.id.wrap_image);
                checkBox = (ImageView) itemView.findViewById(R.id.exChecked);

            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == RESULT_OK) {
                // 获取返回的图片列表
                Bundle bundle = data.getExtras();
                if (bundle == null) {
                    return;
                }
                final List<ImageBean> path = bundle
                    .getParcelableArrayList(MultiImageSelectorActivity.EXTRA_RESULT);
                //默认不使用原图
                showProgress("正在设置", true);
                for (final ImageBean bean : path) {
                    if (bean == null) {
                        return;
                    }
                    boolean isGif = ContextHelper.isGif(bean.path);
                    HttpUtils.getInstance().uploadFileProgress(bean.path,
                        isGif ? EUploadFileType.GIF : EUploadFileType.JPG, new IUploadListener() {
                            @Override
                            public void onSuccess(Object result) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (result != null && result instanceof ImageUploadEntity) {
                                            ImageUploadEntity entity = (ImageUploadEntity) result;
                                            if (entity == null) {
                                                resetProgressText("设置失败");
                                                dismissProgressDelay(1500);
                                                return;
                                            }
                                            dismissProgress();
                                            if (strings.contains(entity.getOriginalUrl())) {
                                                T.show("已经添加为表情");
                                                return;
                                            }
                                            strings.add(ImageUploadEntity.toJson(entity));
                                            String data = EmojiHelper.listToString(strings);
                                            if (!TextUtils.isEmpty(data)) {
                                                SPHelper.saveValue(AppConfig.EX_KEY, data);
                                            }
                                            adapter.addImage(strings);
                                            adapter.notifyDataSetChanged();
                                            changed = true;
                                        }
                                    }
                                });
                            }

                            @Override
                            public void onFailed() {
                                resetProgressText("设置失败");
                                dismissProgressDelay(1500);
                            }

                            @Override
                            public void onProgress(int progress) {

                            }
                        });
                }
            }
        }
    }
}
