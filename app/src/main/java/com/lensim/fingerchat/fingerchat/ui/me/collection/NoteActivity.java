package com.lensim.fingerchat.fingerchat.ui.me.collection;


import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.view.KeyEvent;

import com.lens.chatmodel.ui.image.MultiImageSelectorActivity;
import com.lensim.fingerchat.commons.base.BaseActivity;
import com.lensim.fingerchat.commons.map.MapPickerActivity;
import com.lensim.fingerchat.commons.map.bean.MapInfoEntity;
import com.lensim.fingerchat.commons.utils.L;
import com.lensim.fingerchat.commons.utils.NoteStringUtils;
import com.lensim.fingerchat.commons.utils.StringUtils;
import com.lensim.fingerchat.components.dialog.nifty_dialog.NiftyDialogBuilder;
import com.lensim.fingerchat.data.bean.ImageBean;
import com.lensim.fingerchat.data.me.content.CollectionManager;
import com.lensim.fingerchat.data.me.content.StoreManager;
import com.lensim.fingerchat.fingerchat.R;
import com.lensim.fingerchat.fingerchat.databinding.ActivityNoteBinding;
import com.lensim.fingerchat.fingerchat.ui.me.collection.note.NoteImageView;
import com.lensim.fingerchat.fingerchat.ui.me.collection.note.RichTextEditor;
import com.lensim.fingerchat.fingerchat.ui.work_center.sign.ClockInRecordDetailActivity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.lensim.fingerchat.commons.utils.cppencryp.SecureUtil.showToast;

/**
 * 收藏——笔记
 * <p>
 * Created by LL117394 on 2017/5/26.
 */

public class NoteActivity extends BaseActivity {

    public final static String PARAMS_CONTENT = "content";
    public final static String PARAMS_TIMESTAMP = "timeStamp";
    private final int REQUEST_NOTE_PICK_IMAGE = 346;
    public final static int REQUEST_NOTE_PICK_LOCATION = 347;
    private final int REQUEST_NOTE_PICK_FILE = 348;
    private final int REQUEST_NOTE_PICK_VOICE = 349;
    private boolean isEdited = true;
    private long contentLength;
    private ArrayList<String> pictures = new ArrayList<>();
    ActivityNoteBinding ui;
    private long favId;

    public static void openActivity(Activity context, int requestCode, String content,
                                    long favId) {
        Intent intent = new Intent(context, NoteActivity.class);
        intent.putExtra(ClockInRecordDetailActivity.PARAMS_CONTENT, content);
        intent.putExtra(PARAMS_TIMESTAMP, favId);
        context.startActivityForResult(intent, requestCode);
    }

    @Override
    public void initView() {
        ui = DataBindingUtil.setContentView(this, R.layout.activity_note);
        ui.collectionNoteToolbar.setTitleText("笔记 ");
        initBackButton(ui.collectionNoteToolbar, true);
        ui.collectionNoteToolbar.setConfirmBt("完成", v -> onReturn());
        ui.imgbtnImg.setOnClickListener(v -> getImg());
        ui.imgbtnLocation.setOnClickListener(v -> getLocation());

        String content = getIntent().getStringExtra(PARAMS_CONTENT);
        favId = getIntent().getLongExtra(PARAMS_TIMESTAMP, 0);
        if (!StringUtils.isEmpty(content)) {
            contentLength = content.length();
            showDataSync(content);
        }
    }


    public void getImg() {
        Intent intent = new Intent(NoteActivity.this, MultiImageSelectorActivity.class);
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, true);
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, 9 - pictures.size());
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, MultiImageSelectorActivity.MODE_MULTI);
        startActivityForResult(intent, REQUEST_NOTE_PICK_IMAGE);
    }

    public void getLocation() {
        MapPickerActivity.openActivity(NoteActivity.this, REQUEST_NOTE_PICK_LOCATION, null);
    }


    /**
     * 异步方式显示数据
     */
    private void showDataSync(final String html) {
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String> e) throws Exception {
                showEditData(e, html);
            }
        }).subscribeOn(Schedulers.io())//生产事件在io
            .observeOn(AndroidSchedulers.mainThread())//消费事件在UI线程
            .subscribe(new Observer<String>() {
                @Override
                public void onSubscribe(@NonNull Disposable d) {

                }

                @Override
                public void onComplete() {
                }

                @Override
                public void onError(Throwable e) {
                    showToast("解析错误：图片不存在或已损坏");
                }

                @Override
                public void onNext(String text) {
                    if (text.endsWith(NoteStringUtils.IDENTIFER_OF_IMG)) {
                        text = text.replace(NoteStringUtils.IDENTIFER_OF_IMG, "");
                        if (text.indexOf(NoteStringUtils.SPLIT_AND) != -1) {
                            ui.noteRichEditor.addImageViewAtIndex(ui.noteRichEditor.getLastIndex(), text, NoteImageView.TYPE_MAP);
                        } else {
                            ui.noteRichEditor.addImageViewAtIndex(ui.noteRichEditor.getLastIndex(), text, NoteImageView.TYPE_IMG);
                        }
                    } else {
                        ui.noteRichEditor.addEditTextAtIndex(ui.noteRichEditor.getLastIndex(), text);
                    }
                }
            });
    }

    /**
     * 显示数据
     */
    protected void showEditData(ObservableEmitter<String> subscriber, String html) {
        try {
            List<String> textList = NoteStringUtils.cutStringByImgTag(html);
            for (int i = 0; i < textList.size(); i++) {
                String text = textList.get(i);
                if (text.contains("<img")) {
                    String imagePath = NoteStringUtils.getImgSrc(text);
                    subscriber.onNext(imagePath);
                } else {
                    subscriber.onNext(text);
                }
            }
            subscriber.onComplete();
        } catch (Exception e) {
            e.printStackTrace();
            subscriber.onError(e);
        }
    }

    /**
     * 负责处理编辑数据提交等事宜，请自行实现
     */
    private String getEditData() {
        List<RichTextEditor.EditData> editList = ui.noteRichEditor.buildEditData();
        StringBuffer content = new StringBuffer();
        for (RichTextEditor.EditData itemData : editList) {
            if (itemData.inputStr != null) {
                content.append(itemData.inputStr);
            } else if (itemData.imagePath != null) {
                content.append("<img src=\"")
                    .append(itemData.imagePath)
                    .append(NoteStringUtils.IDENTIFER_OF_IMG)
                    .append("\"/>");
            }
        }
        return content.toString();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case REQUEST_NOTE_PICK_IMAGE:
                List<ImageBean> path = data.getParcelableArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
                if (path != null && path.size() > 0) {
                    for (ImageBean imageBean : path) {
                        ui.noteRichEditor.insertImage(imageBean.path, NoteImageView.TYPE_IMG);
                    }
                }
                break;
            case REQUEST_NOTE_PICK_LOCATION:
                // 1张图片
                List<String> path2 = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
                MapInfoEntity para_addressInfo = data.getParcelableExtra("position");
                if (path2 != null && path2.size() == 1) {
                    if (!StringUtils.isEmpty(path2.get(0))) {
                        ui.noteRichEditor.insertImage(para_addressInfo.toString() + NoteStringUtils.SPLIT_AND + path2.get(0), NoteImageView.TYPE_MAP);
                    }
                }
                break;
            case REQUEST_NOTE_PICK_FILE:
                break;
            case REQUEST_NOTE_PICK_VOICE:
                break;
        }
    }

    @Override
    public void backPressed() {
        onReturn();
    }

    public void onReturn() {
        String content = getEditData();
        isEdited = content.length() != contentLength;

        if (isEdited) {
            closeDialog(content);
        } else {
            Intent intent = new Intent();
            intent.putExtra("dataChanged", false);
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            onReturn();
        }
        return super.onKeyDown(keyCode, event);
    }

    public void closeDialog(final String content) {
        NiftyDialogBuilder builder = NiftyDialogBuilder.getInstance(NoteActivity.this);
        builder.withTitle("提示")
            .withMessage("是否保存本次编辑?")
            .withButton1Text("不保存")
            .withButton2Text("保存")
            .setButton1Click(v -> {
                builder.dismiss();
                saveChanges(false);
            })
            .setButton2Click(v -> {
                builder.dismiss();
                CollectionManager.getInstance().deleteByFavId(favId);
                StoreManager.getInstance().storeNote(new Date().getTime() + "", content);
                saveChanges(true);
            })
            .show();
    }

    public void saveChanges(boolean isSave) {
        Intent intent = new Intent();
        intent.putExtra("dataChanged", isSave);
        setResult(RESULT_OK, intent);
        finish();
    }

}

