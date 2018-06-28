package com.lensim.fingerchat.fingerchat.ui.me.photo;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.StringSignature;
import com.example.annotation.Path;
import com.lens.chatmodel.ui.image.LookUpPhotosActivity;
import com.lens.chatmodel.ui.image.MultiImageSelectorActivity;
import com.lensim.fingerchat.commons.app.AppConfig;
import com.lensim.fingerchat.commons.base.FGActivity;
import com.lensim.fingerchat.commons.global.Route;
import com.lensim.fingerchat.commons.helper.ContextHelper;
import com.lensim.fingerchat.commons.router.ActivityPath;
import com.lensim.fingerchat.commons.utils.L;
import com.lensim.fingerchat.commons.utils.StringUtils;
import com.lensim.fingerchat.components.adapter.multitype.MultiTypeAdapter;
import com.lensim.fingerchat.data.Http;
import com.lensim.fingerchat.data.RxSchedulers;
import com.lensim.fingerchat.data.login.UserInfoRepository;
import com.lensim.fingerchat.data.me.NewComment;
import com.lensim.fingerchat.data.me.circle_friend.FriendCircleEntity;
import com.lensim.fingerchat.data.repository.SPSaveHelper;
import com.lensim.fingerchat.fingerchat.R;
import com.lensim.fingerchat.fingerchat.databinding.ActivityPhotosBinding;
import com.lensim.fingerchat.fingerchat.ui.me.circle_friends.LookupCommentActivity;
import com.lensim.fingerchat.fingerchat.ui.me.circle_friends.adapter.viewholder.HeaderViewHolder;
import com.lensim.fingerchat.fingerchat.ui.me.photo.photos_adapter_multitype.PhotosHeaderVH;
import com.lensim.fingerchat.fingerchat.ui.me.photo.photos_adapter_multitype.PhotosViewHolder;
import com.lensim.fingerchat.fingerchat.ui.me.photo.photos_adapter_multitype.TextViewHolder;
import com.lensim.fingerchat.fingerchat.ui.me.utils.CircleFriendsHelper;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by ll147996 on 2017/12/13.
 * 相册
 */
@Path(ActivityPath.PHOTOS_ACTIVITY_PATH)
public class PhotosActivity extends FGActivity {

    private ActivityPhotosBinding ui;

    public static final int PHOTOS_REQUEST_NEW_STATUS = 111;
    public static final int COMMENT_ACTIVITY_REQUEST_CODE = 191;


    private List<FriendCircleEntity> entities;
    private String username;
    private boolean isMyPhoto;
    private MultiTypeAdapter mAdapter;

    private final DisplayImageOptions options =
        new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true)
            .showImageOnFail(R.drawable.ease_default_avatar)
            .showImageOnLoading(R.drawable.ease_default_avatar)
            .bitmapConfig(Bitmap.Config.RGB_565).build();

    @Override
    public void initView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WindowManager.LayoutParams localLayoutParams = getWindow().getAttributes();
            localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                | localLayoutParams.flags);
        }

        ui = DataBindingUtil.setContentView(this, R.layout.activity_photos);
        initBackButton(ui.toolbar, true);

        ui.mLookUpAllComment.setOnClickListener((view) -> {
            Intent intent = new Intent(this, LookupCommentActivity.class);
            intent.putExtra("lookcomment_type", 2);
            startActivityForResult(intent, COMMENT_ACTIVITY_REQUEST_CODE);
        });
    }


    @Override
    public void initData(Bundle savedInstanceState) {
        username = getIntent().getStringExtra(ActivityPath.USER_ID);
        L.i("谁的相册" + username);
        if (entities == null) {
            entities = new ArrayList<>();
        }
        isMyPhoto = UserInfoRepository.getUserName().equals(username);
        //头像
        String path = String.format(Route.obtainAvater, username);
        ImageLoader.getInstance().displayImage(path, ui.fabFace, options);
        //主题背景
        String themePath = CircleFriendsHelper.getThemePath(username);
        if (!StringUtils.isEmpty(themePath)) {
            Glide.with(ContextHelper.getContext()).load(themePath)
                .signature(new StringSignature(getSaveAvatarTime())).centerCrop().into(ui.circleTheme);
            ui.circleChangeText.setVisibility(View.GONE);
        }

        if (isMyPhoto) {
            ui.mLookUpAllComment.setVisibility(View.VISIBLE);
            ui.circleUsername.setText(UserInfoRepository.getUsernick());
        } else {
            ui.mLookUpAllComment.setVisibility(View.GONE);
//            try {
//                String account = AccountManager.getInstance().getUserjid();
//                String friendName = username;
//                String user = friendName + "@" + ConnectionItem.DEFAULT_SERVER_NAME;
//                RosterContactTemp rosterContact = RosterManager.getInstance()
//                    .getRosterContact(account, user);
//                if (rosterContact != null && !TextUtils.isEmpty(rosterContact.getNick())) {
//                    circleUsername.setText(rosterContact.getNick());
//                }
//            } catch (NullPointerException e) {
//                e.printStackTrace();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
        }
        prepareAdapter(isMyPhoto);
        updateCircle();

        ui.recycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                Glide.with(ContextHelper.getContext()).resumeRequests();
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState != RecyclerView.SCROLL_STATE_IDLE) {
                    Glide.with(ContextHelper.getContext()).pauseRequests();
                    ImageLoader.getInstance().pause();
                } else {
                    ImageLoader.getInstance().resume();
                }
            }
        });
    }


    private void prepareAdapter(boolean bool) {
        LinearLayoutManager mLayouManager;
        ui.recycler.setHasFixedSize(false);
        mLayouManager = new LinearLayoutManager(PhotosActivity.this);
        mLayouManager.setOrientation(OrientationHelper.VERTICAL);
        ui.recycler.setLayoutManager(mLayouManager);
        L.d("获取的实体", entities.toString());
        PhotosHeaderVH headerViewHolder = new PhotosHeaderVH(this);
        PhotosViewHolder photosViewHolder = new PhotosViewHolder(this, bool);
        TextViewHolder textViewHolder = new TextViewHolder(this, bool);

        mAdapter = new MultiTypeAdapter();
        mAdapter.register(String.class, headerViewHolder);
        mAdapter.register(FriendCircleEntity.class, photosViewHolder);
        mAdapter.register(NewComment.class, textViewHolder);
        ui.recycler.setAdapter(mAdapter);
        mAdapter.setItems(getItems(entities));
        ui.recycler.setItemAnimator(new DefaultItemAnimator());

        photosViewHolder.setOnItemClickListener((entity, position) -> {
                Intent intent = new Intent(this, LookUpPhotosActivity.class);
                intent.putParcelableArrayListExtra(LookUpPhotosActivity.FRIEND_CIRCLE_ENTITY_LIST, (ArrayList<FriendCircleEntity>) entities);
                intent.putExtra(LookUpPhotosActivity.POSITION, position);
                startActivityForResult(intent, 0);
            }
        );
    }


    public String getSaveAvatarTime() {
        if (HeaderViewHolder.DEF_VALUE.equals(SPSaveHelper
            .getStringValue(HeaderViewHolder.MY_AVATAR_TIME, HeaderViewHolder.DEF_VALUE))) {

            SPSaveHelper
                .setValue(HeaderViewHolder.MY_AVATAR_TIME, StringUtils.formatDateTime(new Date()));
        }
        return SPSaveHelper
            .getStringValue(HeaderViewHolder.MY_AVATAR_TIME, HeaderViewHolder.DEF_VALUE);
    }


    @SuppressLint("CheckResult")
    private void updateCircle() {
        Http.getPhotos("getphoto", username)
            .compose(RxSchedulers.io_main())
            .subscribe(friendCircleEntities -> {
                    entities.addAll(friendCircleEntities);
                    mAdapter.setItems(getItems(entities));
                },
                throwable -> Log.e("updateCircle", throwable.getMessage()));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AppConfig.REQUEST_SINGLE_IMAGE) {
            if (resultCode == RESULT_OK) {
                List<String> path = data
                    .getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
                if (path != null) {
                    String imagePath = path.get(0);
                    L.d("选择了一张图片", imagePath);
                    Glide.with(this).load(new File(imagePath)).centerCrop().into(ui.circleTheme);
                    SPSaveHelper.setValue(AppConfig.CIRCLE_THEME_PATH, imagePath);
                    if (ui.circleChangeText.getVisibility() != View.GONE) {
                        ui.circleChangeText.setVisibility(View.GONE);
                    }
                }
            }
        } else if (requestCode == COMMENT_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK && entities != null) {
                if (data.getBooleanExtra("isDelete", false)) {
                    deleteCircleList(data.getStringArrayListExtra("list"));
                }
            }
        } else if (requestCode == 0) {
            if (resultCode == RESULT_OK && entities != null) {
                deleteCircle(data.getStringExtra("delete_circle_id"));
            }
        } else if (requestCode == AppConfig.REQUEST_VIDEO) {
            if (resultCode == 102) {
                String path = data.getStringExtra("videoPath");
                Intent intent = new Intent(this, VideoStatuActivity.class);
                intent.putExtra(VideoStatuActivity.PATH, path);
                startActivityForResult(intent, PHOTOS_REQUEST_NEW_STATUS);
            }
        } else if (requestCode == PHOTOS_REQUEST_NEW_STATUS) {
            if (resultCode == RESULT_OK) {
                if (!entities.isEmpty()) {
                    entities.clear();
                }
                if (username.equals(UserInfoRepository.getUserName())) {

                }
                updateCircle();
            }
        }
    }

    private void deleteCircle(String delete_circle_id) {
        if (delete_circle_id == null) {
            return;
        }
        FriendCircleEntity e = null;
        for (FriendCircleEntity entity : entities) {
            if (entity.getPHO_Serno().equals(delete_circle_id)) {
                e = entity;
                break;
            }
        }
        if (e != null) {
            entities.remove(e);
            mAdapter.setItems(getItems(entities));
//            mAdapter.notifyDataSetChanged();
        }
    }

    private void deleteCircleList(ArrayList<String> delete_circle_id) {
        if (delete_circle_id == null || delete_circle_id.isEmpty()) {
            return;
        }
        FriendCircleEntity e = null;
        for (String circleID : delete_circle_id) {
            for (FriendCircleEntity entity : entities) {
                if (entity.getPHO_Serno().equals(circleID)) {
                    e = entity;
                    break;
                }
            }
            if (e != null) {
                entities.remove(e);
            }
        }
        mAdapter.setItems(getItems(entities));
//        mAdapter.notifyDataSetChanged();
    }


    private List<Object> getItems(@NonNull List<FriendCircleEntity> entities) {
        List<Object> items = new ArrayList<>();
        if (isMyPhoto) items.add("");
        for (FriendCircleEntity item : entities) {
            if (Integer.parseInt(item.getPHO_ImageNUM()) > 0) {
                items.add(item);
            } else {
                NewComment newComment = new NewComment();
                newComment.setPHO_Serno(item.getPHO_Serno());
                newComment.setPHO_CreateUserID(item.getPHO_CreateUserID());
                newComment.setUSR_Name(item.getUSR_Name());
                newComment.setPHO_Content(item.getPHO_Content());
                newComment.setPHO_CreateDT(item.getPHO_CreateDT());
                items.add(newComment);
            }
        }
        return items;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            setResult(RESULT_OK);
            this.finish();
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }
}
