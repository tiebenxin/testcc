package com.lens.chatmodel.ui.image;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.lens.chatmodel.ChatEnum.EMessageType;
import com.lens.chatmodel.ChatEnum.EPlayType;
import com.lens.chatmodel.R;
import com.lens.chatmodel.bean.body.BodyEntity;
import com.lens.chatmodel.bean.body.ImageUploadEntity;
import com.lens.chatmodel.db.ProviderChat;
import com.lens.chatmodel.helper.ChatHelper;
import com.lens.chatmodel.helper.ImageHelper;
import com.lens.chatmodel.interf.IChatRoomModel;
import com.lens.chatmodel.interf.IGetFileSizeListener;
import com.lens.chatmodel.manager.MessageManager;
import com.lens.chatmodel.net.HttpUtils;
import com.lens.chatmodel.ui.message.TransforMsgActivity;
import com.lens.chatmodel.view.photoview.PhotoViewAttacher;
import com.lens.chatmodel.view.photoview.ZoomImageView;
import com.lensim.fingerchat.commons.authority.AuthorityManager;
import com.lensim.fingerchat.commons.global.CommonEnum.ELogType;
import com.lensim.fingerchat.commons.helper.AnimationRect;
import com.lensim.fingerchat.commons.helper.ContextHelper;
import com.lensim.fingerchat.commons.utils.AnimationUtility;
import com.lensim.fingerchat.commons.utils.FileUtil;
import com.lensim.fingerchat.commons.utils.L;
import com.lensim.fingerchat.commons.utils.T;
import com.lensim.fingerchat.commons.utils.TDevice;
import com.lensim.fingerchat.commons.utils.ThreadUtils;
import com.lensim.fingerchat.commons.utils.TimeUtils;
import com.lensim.fingerchat.data.bean.LongImageBean;
import com.lensim.fingerchat.data.help_class.IDataRequestListener;
import com.lensim.fingerchat.data.login.UserInfoRepository;
import com.lensim.fingerchat.data.me.content.CollectionManager;
import com.lensim.fingerchat.data.me.content.FavJson;
import com.lensim.fingerchat.data.me.content.StoreManager;
import java.io.File;
import org.json.JSONException;
import org.json.JSONObject;

public class GeneralPictureFragment extends Fragment {


    private ZoomImageView photoView;
    public static final int ANIMATION_DURATION = 300;
    private TextView lookOrigin;
    private String mMsgId;
    private boolean animateIn;
    private long fileSize;
    private boolean compress;
    private boolean isLocal = false;
    private ImageUploadEntity entity;
    private int playStatus;
    private String collectInfo;
    private int position;

    public static GeneralPictureFragment newInstance(String path, AnimationRect rect,
        LongImageBean longImageBean,
        boolean animationIn, String collectInfo, int position) {
        GeneralPictureFragment fragment = new GeneralPictureFragment();
        Bundle bundle = new Bundle();
        bundle.putString("path", path);
        bundle.putParcelable("rect", rect);
        bundle.putParcelable("longImage", longImageBean);
        bundle.putBoolean("animationIn", animationIn);
        bundle.putString("collectInfo", collectInfo);
        bundle.putInt("position", position);
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.gallery_general_layout, container, false);

        photoView = (ZoomImageView) view.findViewById(R.id.animation);
        lookOrigin = (TextView) view.findViewById(R.id.lookOrigin);
        photoView.setOnViewTapListener(new PhotoViewAttacher.OnViewTapListener() {
            @Override
            public void onViewTap(View view, float x, float y) {
                getActivity().onBackPressed();
            }
        });


        String path = getArguments().getString("path");
        collectInfo = getArguments().getString("collectInfo");
        position = getArguments().getInt("position");
        if (path.startsWith("http://")) {
            entity = ImageUploadEntity.createEntity(path);
        } else {
            entity = ImageUploadEntity.fromJson(path);
        }
        L.i("GeneralPictureFragment", "图片地址" + path);
        animateIn = getArguments().getBoolean("animationIn");
        final AnimationRect rect = getArguments().getParcelable("rect");
        final LongImageBean longImageBean = getArguments().getParcelable("longImage");
        if (rect != null) {
            mMsgId = rect.getMsgId();
        }

        setLongClick();

        if (longImageBean != null && longImageBean.isLongImage()) {//长图不压缩
            compress = false;
        } else {
            compress = true;
        }
        playStatus = 0;
        if (!TextUtils.isEmpty(mMsgId)) {
            playStatus = ProviderChat.getPlayStatus(getActivity(), mMsgId);
        }
        if (playStatus == EPlayType.PALYED.ordinal()) {
            loadOriginImage(entity.getOriginalUrl());
        } else {
            loadImage(entity, compress);
        }

        final Runnable endAction = new Runnable() {
            @Override
            public void run() {
                Bundle bundle = getArguments();
                bundle.putBoolean("animationIn", false);
            }
        };

        photoView.getViewTreeObserver()
            .addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {

                    if (rect == null) {
                        photoView.getViewTreeObserver().removeOnPreDrawListener(this);
                        endAction.run();
                        L.d("朋友圈点击的图片尺寸为空");
                        return true;
                    }

                    final Rect startBounds = new Rect(rect.scaledBitmapRect);//开始时的尺寸
                    final Rect finalBounds = AnimationUtility
                        .getBitmapRectFromImageView(photoView);

                    if (finalBounds == null) {
                        photoView.getViewTreeObserver().removeOnPreDrawListener(this);
                        endAction.run();
                        return true;
                    }

                    float startScale = (float) finalBounds.width() / startBounds.width();
                    L.d("缩放比:" + startScale);
                    if (startScale * startBounds.height() > finalBounds.height()) {
                        startScale = (float) finalBounds.height() / startBounds.height();
                    }

                    int deltaTop = startBounds.top - finalBounds.top;
                    int deltaLeft = startBounds.left - finalBounds.left;

                    photoView.setPivotY(
                        (photoView.getHeight() - finalBounds.height()) / 2);
                    photoView.setPivotX((photoView.getWidth() - finalBounds.width()) / 2);

                    photoView.setScaleX(1 / startScale);
                    photoView.setScaleY(1 / startScale);

                    photoView.setTranslationX(deltaLeft);
                    photoView.setTranslationY(deltaTop);

                    photoView.animate().translationY(0).translationX(0)
                        .scaleY(1)
                        .scaleX(1).setDuration(ANIMATION_DURATION)
                        .setInterpolator(
                            new AccelerateDecelerateInterpolator())
                        .withEndAction(endAction);

                    AnimatorSet animationSet = new AnimatorSet();
                    animationSet.setDuration(ANIMATION_DURATION);
                    animationSet
                        .setInterpolator(new AccelerateDecelerateInterpolator());

                    animationSet.start();
                    L.d("执行结束");
                    photoView.getViewTreeObserver().removeOnPreDrawListener(this);
                    return true;
                }
            });

        return view;
    }

    private void getOriginSize() {
        if (entity == null) {
            return;
        }
        String path = entity.getOriginalUrl();
        if (TextUtils.isEmpty(path)) {
            return;
        }
        if (path.toLowerCase().startsWith("http://") || path.toLowerCase().startsWith("https://")) {
            HttpUtils.getInstance().getUrlFileSize(path, new IGetFileSizeListener() {
                @Override
                public void getSize(String size) {
                    fileSize = Long.parseLong(size);

                    ThreadUtils.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            initLookOrigin();
                        }
                    });
                }
            });

        } else {
            File f = new File(path);
            if (f.exists()) {
                fileSize = f.length();
            }
        }
    }

    private boolean checkIsLocal() {
        if (entity == null) {
            return false;
        }
        String path = entity.getOriginalUrl();
        File file = new File(path);
        if (file.exists()) {
            isLocal = true;
        } else {
            isLocal = false;
        }
        return isLocal;
    }

    public void animationExit(ObjectAnimator backgroundAnimator) {

        if (Math.abs(photoView.getScale() - 1.0f) > 0.1f) {
            photoView.setScale(1, true);
            return;
        }

        getActivity().overridePendingTransition(0, 0);
        animateClose(backgroundAnimator);
    }

    private void animateClose(ObjectAnimator backgroundAnimator) {

        AnimationRect rect = getArguments().getParcelable("rect");

        if (rect == null) {
            photoView.animate().alpha(0);
            backgroundAnimator.start();
            return;
        }

        final Rect startBounds = rect.scaledBitmapRect;
        final Rect finalBounds = AnimationUtility.getBitmapRectFromImageView(photoView);

        if (finalBounds == null) {
            photoView.animate().alpha(0);
            backgroundAnimator.start();
            return;
        }

        if (TDevice.isPortrait() != rect.isScreenPortrait) {
            photoView.animate().alpha(0);
            backgroundAnimator.start();
            return;
        }

        float startScale;
        if ((float) finalBounds.width() / finalBounds.height()
            > (float) startBounds.width() / startBounds.height()) {
            startScale = (float) startBounds.height() / finalBounds.height();
        } else {
            startScale = (float) startBounds.width() / finalBounds.width();
        }

        final float startScaleFinal = startScale;

        int deltaTop = startBounds.top - finalBounds.top;
        int deltaLeft = startBounds.left - finalBounds.left;

        photoView.setPivotY((photoView.getHeight() - finalBounds.height()) / 2);
        photoView.setPivotX((photoView.getWidth() - finalBounds.width()) / 2);

        photoView.animate().translationX(deltaLeft).translationY(deltaTop)
            .scaleY(startScaleFinal)
            .scaleX(startScaleFinal).setDuration(ANIMATION_DURATION)
            .setInterpolator(new AccelerateDecelerateInterpolator())
            .withEndAction(new Runnable() {
                @Override
                public void run() {

                    photoView.animate().alpha(0.0f).setDuration(200).withEndAction(
                        new Runnable() {
                            @Override
                            public void run() {

                            }
                        });
                }
            });

        AnimatorSet animationSet = new AnimatorSet();
        animationSet.setDuration(ANIMATION_DURATION);
        animationSet.setInterpolator(new AccelerateDecelerateInterpolator());

        animationSet.playTogether(backgroundAnimator);

        animationSet.start();
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getOriginSize();

    }

    private void initLookOrigin() {
        if (!TextUtils.isEmpty(mMsgId)) {
            if (fileSize > 0 && playStatus != EPlayType.PALYED.ordinal()) {
                lookOrigin.setVisibility(View.VISIBLE);
                lookOrigin.setText("原图(" + Formatter.formatFileSize(getActivity(), fileSize) + ")");

                lookOrigin.setOnClickListener(v -> {
                    if (entity == null) {
                        return;
                    }
                    final String text = lookOrigin.getText().toString();
                    final String originpath = entity.getOriginalUrl();
                    lookOrigin.setText("正在加载...");
                    loadOriginImage(originpath);
                });
            } else {
                lookOrigin.setVisibility(View.GONE);
            }
        } else {
            if (fileSize > 0) {
                lookOrigin.setVisibility(View.VISIBLE);
                lookOrigin.setText("原图(" + Formatter.formatFileSize(getActivity(), fileSize) + ")");

                lookOrigin.setOnClickListener(v -> {
                    if (entity == null) {
                        return;
                    }
                    final String text = lookOrigin.getText().toString();
                    final String originpath = entity.getOriginalUrl();
                    lookOrigin.setText("正在加载...");
                    loadOriginImage(originpath);
                });
            } else {
                lookOrigin.setVisibility(View.GONE);
            }
        }
    }

    private void loadOriginImage(String originUrl) {
        Glide.with(ContextHelper.getContext())
            .load(originUrl)
            .asBitmap()
            .into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap resource,
                    GlideAnimation<? super Bitmap> glideAnimation) {
                    lookOrigin.setVisibility(View.GONE);
                    photoView.setIsOrigin(true);
                    photoView.setImageBitmap(resource);
                    if (!TextUtils.isEmpty(mMsgId)) {
                        ProviderChat.updatePlayStatus(getActivity(), mMsgId,
                            EPlayType.PALYED);
                    }
                }
            });
    }


    private void loadImage(ImageUploadEntity entity, boolean compress) {
        if (entity == null) {
            return;
        }
        if (TextUtils.isEmpty(entity.getOriginalUrl())) {
            return;
        }
        if (ContextHelper.isGif(entity.getOriginalUrl())) {
            photoView.setIsGif(true);
            ImageHelper.loadGif(entity.getOriginalUrl(), photoView);
        } else {
            photoView.setIsGif(false);
            if (!animateIn) {
                if (compress && !isLocalPath(entity)) {//非长图
                    load(getUrl(entity), false);
                } else {
                    load(getUrl(entity), true);
                }
            } else {
                Bitmap bitmap = null;
                if (compress && !isLocalPath(entity)) {//需要压缩
                    load(getUrl(entity), false);
                } else {
                    load(entity.getOriginalUrl(), true);
                }
                if (bitmap == null) {
                    if (compress && !isLocalPath(entity)) {//需要压缩
                        load(getUrl(entity), false);
                    } else {
                        load(entity.getOriginalUrl(), true);
                    }
                }
            }
        }
    }

    private void load(String url, boolean isOrigin) {
        Glide.with(ContextHelper.getContext())
            .load(url)
            .asBitmap()
            .format(DecodeFormat.PREFER_ARGB_8888)
            .error(R.drawable.default_error)
            .placeholder(R.drawable.default_error)
            .into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap resource,
                    GlideAnimation<? super Bitmap> glideAnimation) {
                    if (resource != null) {
                        photoView.setIsOrigin(isOrigin);
                        photoView.setImageBitmap(resource);
                    }
                }
            });
    }

    private boolean isLocalPath(ImageUploadEntity entity) {
        boolean isLocal = false;
        if (entity != null) {
            if (TextUtils.isEmpty(entity.getThumbnailUrl())) {
                File file = new File(entity.getOriginalUrl());
                if (file != null && file.exists()) {
                    isLocal = true;
                }
            }
        }
        return isLocal;
    }

    private String getUrl(ImageUploadEntity entity) {
        if (entity != null) {
            if (!TextUtils.isEmpty(entity.getThumbnailUrl())) {
                return entity.getThumbnailUrl();
            } else {
                return entity.getOriginalUrl();
            }
        }
        return "";
    }

    private void setLongClick() {
        final String[] menus = new String[]{getString(R.string.dialog_menu_send_to_friend),
            getString(R.string.pop_menu_collect), getString(R.string.pop_menu_copy_to_local)};
        boolean isSecret = false;//密聊不能转发
        IChatRoomModel model = null;
        if (!TextUtils.isEmpty(mMsgId)) {
            model = ProviderChat.selectMsgSingle(ContextHelper.getContext(), mMsgId);
            if (model.isSecret()) {
                isSecret = true;
            }
        }
        if (isSecret) {
            return;
        }
        IChatRoomModel finalModel = model;
        photoView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                new AlertDialog.Builder(getActivity())
                    .setItems(menus, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (!AuthorityManager.getInstance().copyPicOutsize()) {
                                T.showShort(getActivity(), "没有相关权限");
                                return;
                            }
                            if (entity == null) {
                                return;
                            }
                            switch (which) {
                                case 0://发送给好友
                                    String body;
                                    boolean isSecret = false;//密聊不能转发
                                    if (finalModel != null) {
                                        if (isSecret) {
                                            body = null;
                                        } else {
                                            body = MessageManager.getInstance()
                                                .getMessageBodyJson(finalModel);
                                        }
                                    } else {
                                        BodyEntity bodyEntity = MessageManager.getInstance()
                                            .createBody(ImageUploadEntity.toJson(entity), false,
                                                EMessageType.IMAGE,
                                                UserInfoRepository.getUsernick());
                                        body = BodyEntity.toJson(bodyEntity);
                                    }
                                    if (isSecret) {
                                        T.show("密聊图片不能转发");
                                    } else {
                                        Intent intent = TransforMsgActivity
                                            .newPureIntent(getActivity(), body,
                                                EMessageType.IMAGE.value, 1, "");
                                        getActivity().startActivity(intent);
                                    }
                                    break;
                                case 1://收藏
                                    if (!TextUtils.isEmpty(collectInfo)) {
                                        FavJson store = new FavJson();
                                        String[] collectInfos = collectInfo.split("&");
                                        if (collectInfos.length < 5) {
                                            return;
                                        }
                                        JSONObject jsonObject = new JSONObject();
                                        try {
                                            jsonObject.put("OriginalSzie", "");
                                            jsonObject.put("userHeadImageStr", collectInfos[3]);
                                            jsonObject.put("OriginalUrl", entity.getOriginalUrl());
                                            jsonObject.put("signContent", "");
                                            jsonObject.put("messageType", "2");
                                            jsonObject.put("type", "0");
                                            jsonObject.put("ThumbnailUrl", "");
                                            jsonObject.put("ThumbnailSize", "");
                                            jsonObject.put("recordTime", TimeUtils.getDate());
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        store.setFavCreater(UserInfoRepository.getUserId());
                                        store.setProviderJid(collectInfos[1]);
                                        store.setProviderNick(collectInfos[2]);
                                        store.setFavContent(jsonObject.toString());
                                        store.setFavMsgId(collectInfos[0] + ":" + position);
                                        store.setFavType("2");
                                        store.setFavId(System.currentTimeMillis());
                                        StoreManager.getInstance().upload(store);
                                        StoreManager.getInstance()
                                            .storeFromGallery(mMsgId, entity.getOriginalUrl());
                                        //T.show(ContextHelper.getString(R.string.no_surport_function));
                                    } else {
                                        IChatRoomModel store = ProviderChat
                                            .selectMsgSingle(ContextHelper.getContext(), mMsgId);
                                        if (store != null) {
                                            collectMessage(store);
                                        }
                                    }
                                    break;
                                case 2://复制
                                    Drawable drawable = photoView.getDrawable();
                                    if (drawable instanceof BitmapDrawable) {
                                        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
                                        String filepath = FileUtil.saveToPicDir(bitmap);
                                        if (filepath != null) {
                                            T.showShort(getActivity(), "保存成功");
                                            String copyText = FileUtil
                                                .uploadUserOption(entity.getOriginalUrl(), "",
                                                    "a_save_pic");
                                            HttpUtils.getInstance().uploadLogger(copyText,
                                                ELogType.COPY, new IDataRequestListener() {
                                                    @Override
                                                    public void loadFailure(String reason) {
                                                        L.d("上传失败");
                                                    }

                                                    @Override
                                                    public void loadSuccess(Object object) {
                                                        L.d("上传成功");
                                                    }
                                                });
                                        } else {
                                            T.showShort(getActivity(), "保存失败");
                                        }
                                    } else {
                                        T.showShort(getActivity(), "此类型无法保存");
                                    }
                                    break;
                            }
                        }
                    }).show();
                return true;
            }
        });

    }

    private void collectMessage(IChatRoomModel message) {
        if (!CollectionManager.getInstance().checkDuplicateByID(message.getMsgId())) {
            FavJson store = new FavJson();

            if (message.isIncoming()) {
                store.setProviderJid(message.getTo());
                store.setProviderNick(message.getNick());
                if (message.isGroupChat()) {
                    store.setFavCreaterAvatar("");
                    String roomName = message.getGroupName();
                    store.setProviderNick(
                        (TextUtils.isEmpty(roomName) ? "" : roomName + "/") + message.getNick());
                } else {
                    store.setFavCreaterAvatar(UserInfoRepository.getImage());
                }
                store.setFavContent(message.getContent());
                store.setFavUrl(message.getContent());
            } else {
                if (message.getMsgType() == EMessageType.IMAGE
                    || message.getMsgType() == EMessageType.VIDEO
                    || message.getMsgType() == EMessageType.VOICE) {
                    store.setFavContent(message.getUploadUrl());
                } else {
                    if (message.getMsgType() == EMessageType.TEXT) {
                        store.setFavContent(message.getContent());
                    } else {
                        store.setFavContent(message.getBody());
                    }
                }
                store.setFavUrl(message.getUploadUrl());
                store.setProviderJid(message.getFrom());
                store.setProviderNick(message.getNick());
                store.setFavCreaterAvatar(UserInfoRepository.getImage());
                store.setProviderNick(UserInfoRepository.getUsernick());

            }
            store.setFavMsgId(message.getMsgId());
            store.setFavProvider(UserInfoRepository.getUserId());
            store.setFavType(ChatHelper.getFavType(message.getMsgType()));
            store.setFavCreater(UserInfoRepository.getUserName());
            store.setFavTime(TimeUtils.getDate());
            store.setFavDes("");
            store.setFavId(System.currentTimeMillis());
            StoreManager.getInstance().upload(store);
        } else {
            T.show("已收藏，请勿重复");
        }
    }
}
