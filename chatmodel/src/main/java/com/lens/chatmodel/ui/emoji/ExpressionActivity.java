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
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.fingerchat.api.message.ExcuteResultMessage;
import com.fingerchat.proto.message.Excute.ExcuteMessage;
import com.fingerchat.proto.message.Excute.ExcuteType;
import com.lens.chatmodel.R;
import com.lens.chatmodel.bean.EmoBean;
import com.lens.chatmodel.bean.body.ImageUploadEntity;
import com.lens.chatmodel.eventbus.ExcuteEvent;
import com.lens.chatmodel.im_service.FingerIM;
import com.lens.chatmodel.manager.MessageManager;
import com.lens.chatmodel.net.HttpUtils;
import com.lens.chatmodel.ui.image.MultiImageSelectorActivity;
import com.lensim.fingerchat.commons.app.AppConfig;
import com.lensim.fingerchat.commons.base.FGActivity;
import com.lensim.fingerchat.commons.global.Common;
import com.lensim.fingerchat.commons.global.CommonEnum.EUploadFileType;
import com.lensim.fingerchat.commons.helper.ContextHelper;
import com.lensim.fingerchat.commons.helper.GsonHelper;
import com.lensim.fingerchat.commons.interf.IEventProduct;
import com.lensim.fingerchat.commons.toolbar.FGToolbar;
import com.lensim.fingerchat.commons.utils.SPHelper;
import com.lensim.fingerchat.commons.utils.T;
import com.lensim.fingerchat.data.bean.ImageBean;
import com.lensim.fingerchat.data.help_class.IUploadListener;
import java.util.ArrayList;
import java.util.List;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
    private String emoJson;
    private List<EmoBean> emoBeans;


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
        toolbar.setConfirmBt(ContextHelper.getString(R.string.manage), v -> doConfirm());
    }


    @Override
    public void initData(Bundle savedInstanceState) {
        String data = SPHelper.getString(AppConfig.EX_KEY, "");
        if (emoBeans == null) {
            emoBeans = new ArrayList<>();
        } else {
            emoBeans.clear();
        }
        if (strings == null) {
            strings = new ArrayList<>();
        } else {
            strings.clear();
        }
        try {
            JSONArray array = new JSONArray(data);
            if (array != null) {
                int length = array.length();
                for (int i = 0; i < length; i++) {
                    String emoObj = array.getString(i);
                    EmoBean bean = GsonHelper.getObject(emoObj, EmoBean.class);
                    if (bean != null) {
                        emoBeans.add(bean);
                        strings.add(bean.getContent());
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        adapter = new ExAdapter(this, emoBeans);
        exList.setAdapter(adapter);
        exList.setItemAnimator(new DefaultItemAnimator());
        exList.addItemDecoration(new GridDivider(this));
    }


    public void initListener() {
        toFront.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<EmoBean> selectImages = adapter.getSelectImages();
                if (selectImages.isEmpty()) {
                    return;
                }
                int len = selectImages.size();
                List<String> frontImages = new ArrayList<>();
                StringBuilder stringBuilder = new StringBuilder();
                for (int i = 0; i < len; i++) {
                    EmoBean bean = selectImages.get(i);
                    if (!frontImages.contains(bean.getKey())) {
                        if (i == len - 1) {
                            stringBuilder.append(bean.getKey());
                        } else {
                            stringBuilder.append(bean.getKey()).append(",");

                        }
                        frontImages.add(bean.getKey());
                    }
                }
                frontEmoticon(stringBuilder.toString());

            }


        });

        exDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<EmoBean> selectImages = adapter.getSelectImages();
                if (selectImages.isEmpty()) {
                    return;
                }
                int len = selectImages.size();
                List<String> deleImages = new ArrayList<>();
                StringBuilder stringBuilder = new StringBuilder();
                for (int i = 0; i < len; i++) {
                    EmoBean bean = selectImages.get(i);
                    if (!deleImages.contains(bean.getKey())) {
                        if (i == len - 1) {
                            stringBuilder.append(bean.getKey());
                        } else {
                            stringBuilder.append(bean.getKey()).append(",");

                        }
                        deleImages.add(bean.getKey());
                    }
                }
                deleEmoticon(stringBuilder.toString());

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
        //        private List<String> images;
        private List<EmoBean> images;
        private List<EmoBean> selectImages;
        private boolean showChecked;

        ExAdapter(Context context, List<EmoBean> images) {
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
                EmoBean bean = images.get(position);
                if (bean == null) {
                    return;
                }
                ImageUploadEntity entity = bean.getValue();
                if (entity != null) {
                    Glide.with(context).load(entity.getOriginalUrl())
                        .placeholder(R.drawable.ease_default_expression)
                        .fitCenter()
                        .diskCacheStrategy(
                            DiskCacheStrategy.SOURCE).into(viewHolder.imageview);
                }

                if (showChecked) {
                    viewHolder.checkBox.setVisibility(View.VISIBLE);
//                    final String path = images.get(position);
                    final EmoBean emoBean = images.get(position);
                    if (selectImages.contains(emoBean)) {
                        viewHolder.checkBox.setImageResource(R.drawable.click_check_box);
                    } else {
                        viewHolder.checkBox.setImageResource(R.drawable.check_box);
                    }
                    viewHolder.imageview.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (selectImages.contains(emoBean)) {
                                selectImages.remove(emoBean);
                                viewHolder.checkBox.setImageResource(R.drawable.check_box);
                            } else {
                                selectImages.add(emoBean);
                                viewHolder.checkBox
                                    .setImageResource(R.drawable.click_check_box);
                            }
                        }
                    });
                } else {
                    viewHolder.checkBox.setVisibility(View.GONE);
                }
            }
        }


        public List<EmoBean> getSelectImages() {
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

        void addImage(List<EmoBean> images) {
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
                showProgress("正在上传", true);
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
                                                resetProgressText("上传失败");
                                                dismissProgressDelay(1500);
                                                return;
                                            }
                                            dismissProgress();
                                            if (strings.contains(entity.getOriginalUrl())) {
                                                T.show("已经添加为表情");
                                                return;
                                            }
                                            emoJson = ImageUploadEntity.toJson(entity);
                                            addEmoticon(emoJson);
                                        }
                                    }
                                });
                            }

                            @Override
                            public void onFailed() {
                                resetProgressText("上传失败");
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

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onEventMain(IEventProduct event) {
        if (event != null) {
            dealWithEvent(event);
        }
    }

    private void dealWithEvent(IEventProduct event) {
        if (event instanceof ExcuteEvent) {
            ExcuteResultMessage message = ((ExcuteEvent) event).getPacket();
            int code = message.message.getCode();
            if (code == Common.EMOTICON_SAVE_SUCCESS) {
                String result = message.message.getResult();
                if (!TextUtils.isEmpty(result)) {
                    saveEmoticon(result);
                }
            } else if (code == Common.EMOTICON_SAVE_ERROR) {
                T.show("表情保存失败");
            } else if (code == Common.EMOTICON_DEL_SUCCESS) {
                String result = message.message.getResult();
                if (!TextUtils.isEmpty(result)) {
                    saveEmoticon(result);
                }
            } else if (code == Common.EMOTICON_DEL_ERROR) {
                T.show("表情删除失败");
            } else if (code == Common.EMOTICON_TOFIRST_SUCCESS) {
                String result = message.message.getResult();
                if (!TextUtils.isEmpty(result)) {
                    saveEmoticon(result);
                }
            } else if (code == Common.EMOTICON_PARAM_INVALID) {
                T.show("参数错误,操作失败");
            } else if (code == Common.EMOTICON_NO_LONGIN) {
                T.show("未登录,操作失败");
            }
        }
    }

    private void addEmoticon(String json) {
        ExcuteMessage message = MessageManager.getInstance()
            .createExcuteBody(ExcuteType.EMOTICON_SAVE, json);
        if (message != null) {
            FingerIM.I.excute(message);
        }
    }

    public void deleEmoticon(String emoIds) {
        if (TextUtils.isEmpty(emoIds)) {
            return;
        }
        ExcuteMessage message = MessageManager.getInstance()
            .createExcuteBody(ExcuteType.EMOTICON_DELETE, emoIds);
        if (message != null) {
            FingerIM.I.excute(message);
        }
    }

    public void frontEmoticon(String emoIds) {
        if (TextUtils.isEmpty(emoIds)) {
            return;
        }
        ExcuteMessage message = MessageManager.getInstance()
            .createExcuteBody(ExcuteType.EMOTICON_TOFIRST, emoIds);
        if (message != null) {
            FingerIM.I.excute(message);
        }
    }

    private void saveEmoticon(String json) {
        SPHelper.remove(AppConfig.EX_KEY);
        SPHelper.saveValue(AppConfig.EX_KEY, json);
        try {
            JSONArray array = new JSONArray(json);
            if (emoBeans != null) {
                emoBeans.clear();
            } else {
                emoBeans = new ArrayList<>();
            }
            int len = array.length();
            if (len > 0) {
                for (int i = 0; i < len; i++) {
                    JSONObject object = array.getJSONObject(i);
                    EmoBean bean = GsonHelper.getObject(object.toString(), EmoBean.class);
                    if (bean != null) {
                        emoBeans.add(bean);
                    }
                }
            }
            adapter.addImage(emoBeans);
            adapter.notifyDataSetChanged();
            changed = true;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        adapter.clearSelectImages();
    }
}
