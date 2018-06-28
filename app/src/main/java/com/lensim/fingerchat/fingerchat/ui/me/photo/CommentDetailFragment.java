package com.lensim.fingerchat.fingerchat.ui.me.photo;



import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lens.chatmodel.helper.FileCache;
import com.lens.chatmodel.ui.image.GalleryAnimationActivity;
import com.lens.chatmodel.ui.profile.FriendDetailActivity;
import com.lens.chatmodel.ui.video.LookUpVideoActivity;
import com.lens.chatmodel.utils.UrlUtils;
import com.lens.chatmodel.view.emoji.EmotionKeyboard;
import com.lens.chatmodel.view.friendcircle.FavortListView;
import com.lens.chatmodel.view.friendcircle.FavortListView.Adapter;
import com.lensim.fingerchat.commons.app.AppConfig;
import com.lensim.fingerchat.commons.global.Route;
import com.lensim.fingerchat.commons.helper.AnimationRect;
import com.lensim.fingerchat.commons.helper.ContextHelper;
import com.lensim.fingerchat.commons.utils.CyptoConvertUtils;
import com.lensim.fingerchat.commons.utils.FileUtil;
import com.lensim.fingerchat.commons.utils.L;
import com.lensim.fingerchat.commons.utils.StringUtils;
import com.lensim.fingerchat.commons.utils.T;
import com.lensim.fingerchat.commons.utils.TDevice;
import com.lensim.fingerchat.commons.utils.UIHelper;
import com.lensim.fingerchat.components.dialog.nifty_dialog.NiftyDialogBuilder;
import com.lensim.fingerchat.components.widget.CircleProgress;
import com.lensim.fingerchat.components.widget.circle_friends.CollectDialog;
import com.lensim.fingerchat.components.widget.circle_friends.CommentDialog;
import com.lensim.fingerchat.components.widget.circle_friends.MultiImageView;
import com.lensim.fingerchat.data.Http;
import com.lensim.fingerchat.data.RxSchedulers;
import com.lensim.fingerchat.data.login.UserInfoRepository;
import com.lensim.fingerchat.data.me.CircleItem;
import com.lensim.fingerchat.data.me.CommentEntity;
import com.lensim.fingerchat.data.me.NewComment;
import com.lensim.fingerchat.data.me.ZambiaEntity;
import com.lensim.fingerchat.data.me.circle_friend.CommentConfig;
import com.lensim.fingerchat.data.me.circle_friend.ContentEntity;
import com.lensim.fingerchat.data.me.circle_friend.FriendCircleEntity;
import com.lensim.fingerchat.data.me.content.StoreManager;
import com.lensim.fingerchat.data.repository.SPSaveHelper;
import com.lensim.fingerchat.fingerchat.R;
import com.lensim.fingerchat.fingerchat.databinding.FragmentCommentDetailBinding;
import com.lensim.fingerchat.fingerchat.ui.me.circle_friends.circle_friends_multitype.CommentAdapter;
import com.lensim.fingerchat.fingerchat.ui.me.collection.type.Content;
import com.lensim.fingerchat.fingerchat.ui.me.utils.BitmapUtil;
import com.lensim.fingerchat.fingerchat.ui.me.utils.DatasUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by LY309313 on 2016/11/3.
 *
 */

public class CommentDetailFragment extends Fragment {

    private CommentAdapter commentAdapter;
    private FavortListView.Adapter favortListAdapter;
    private CommentConfig config;
    private CircleItem circleItem;
    private View rootView;
    private int mCurrentKeyboardH;
    private int mScreenHeight;
    private int mEditTextBodyHeight;
    private int mSelectCircleItemH;
    private int mSelectCommentItemOffset;
    private boolean isDeleteCircle = false;
    private FragmentCommentDetailBinding ui;


    public static CommentDetailFragment newInstance(NewComment comment, CircleItem item) {
        CommentDetailFragment fragment = new CommentDetailFragment();
        Bundle args = new Bundle();
        args.putParcelable(CommentDetailActivity.NEW_COMEMNT, comment);
        args.putParcelable(CommentDetailActivity.CIRCLE_ITEM, item);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
        @Nullable Bundle savedInstanceState) {

        ui = DataBindingUtil.inflate(inflater, R.layout.fragment_comment_detail, container, false);
        rootView = ui.getRoot();
        EmotionKeyboard emotionKeyboard = EmotionKeyboard.with(getActivity()).bindToContent(ui.mCommentRootView);
        ui.circleInput.init(emotionKeyboard, null);
        ui.circleInput.hideExtendMenuContainer();
        ui.snsBtn.setVisibility(View.INVISIBLE);
        setViewTreeObserver();

        UIHelper.setTextSize2(14, ui.nameTv, ui.contentTv, ui.urlTipTv, ui.timeTv, ui.deleteBtn);
        initAdapter();
        initData();
        initListener();

        return rootView;
    }


    private void setViewTreeObserver() {
        final ViewTreeObserver swipeRefreshLayoutVTO = ui.mCommentRootView.getViewTreeObserver();
        swipeRefreshLayoutVTO.addOnGlobalLayoutListener(() -> {
                Rect r = new Rect();
                ui.mCommentRootView.getWindowVisibleDisplayFrame(r);
                int statusBarH = TDevice.getStatusBarHeight();//状态栏高度
                int screenH = ui.mCommentRootView.getRootView().getHeight();
                if (r.top != statusBarH) {
                    //在这个demo中r.top代表的是状态栏高度，在沉浸式状态栏时r.top＝0，通过getStatusBarHeight获取状态栏高度
                    r.top = statusBarH;
                }
                int keyboardH = screenH - (r.bottom - r.top);
                if (keyboardH == mCurrentKeyboardH) {//有变化时才处理，否则会陷入死循环
                    return;
                }
                mCurrentKeyboardH = keyboardH;
                mScreenHeight = screenH;//应用屏幕的高度
                mEditTextBodyHeight = ui.circleInput.getHeight();

                //偏移listview
                if (config != null) {
                    ui.mCommentRootView.smoothScrollBy(0, getoffSet(config));
                }
            });
    }

    private int getoffSet(CommentConfig commentConfig) {
        if (commentConfig == null) {
            return 0;
        }
        int listviewOffset = mScreenHeight - mSelectCircleItemH - mCurrentKeyboardH - mEditTextBodyHeight - ((int) TDevice.dpToPixel(56));
        if (commentConfig.commentType == CommentConfig.Type.REPLY) {
            //回复评论的情况
            listviewOffset = listviewOffset + mSelectCommentItemOffset;
        }
        L.d("测试的偏移量是多少:" + mSelectCommentItemOffset + "偏移量是多少 : " + listviewOffset);
        return -listviewOffset;
    }



    private void initAdapter() {
        commentAdapter = new CommentAdapter(getActivity());
        favortListAdapter = new Adapter();
        ui.favortListTv.setAdapter(favortListAdapter);
        ui.commentList.setAdapter(commentAdapter);
    }


    public void initListener() {
        ui.circleInput.setChatInputMenuListener(content -> {
            TDevice.hideSoftKeyboard(ui.circleInput);
            if (StringUtils.isEmpty(content)) {
                Toast.makeText(getActivity(), "评论内容不能为空...", Toast.LENGTH_SHORT).show();
                return;
            }
            if (config != null && circleItem != null) {
                if (StringUtils.isEmpty(config.createdName)) {
                    config.createdName = UserInfoRepository.getUserName();
                }
                if (StringUtils.isEmpty(config.replyUserid)) {
                    comment(UserInfoRepository.getUserName(), UserInfoRepository.getUsernick(),
                        config.id, config.createdid, config.createdName, content);
                } else {
                    reComment(config.id, config.createdid, config.createdName, content,
                        config.replyUserid, config.replyUsername);
                }

            }
        });
    }


    private void comment(String commentUserid, String commentUsername, String photoserno,
        String createUserid, String createUsername, String content) {
        Http.comment(commentUserid, commentUsername, photoserno, createUserid, createUsername, content)
            .compose(RxSchedulers.io_main())
            .subscribe(stringRetObjectResponse -> {
                if (stringRetObjectResponse.retCode == 1 && !TextUtils.isEmpty(stringRetObjectResponse.retMsg)) {
                    ContentEntity entity = new ContentEntity();
                    entity.setPHC_CommentUserid(UserInfoRepository.getUserName());
                    entity.setPHC_CommentUsername(UserInfoRepository.getUsernick());
                    entity.setPHC_Content(content);
                    entity.setPHC_Serno(stringRetObjectResponse.retMsg);
                    circleItem.comments.add(entity);
                    bindFavortAndComment(circleItem);
                }
            });
    }

    private void reComment(String photoserno, String createUserid, String createUsername,
        String content, String secondid, String secondname) {
        Http.reComment(photoserno, createUserid, createUsername, content, secondid, secondname)
            .compose(RxSchedulers.io_main())
            .subscribe(stringRetObjectResponse -> {
                if (stringRetObjectResponse.retCode == 1 && !TextUtils.isEmpty(stringRetObjectResponse.retMsg)) {
                    ContentEntity entity = new ContentEntity();
                    entity.setPHC_CommentUserid(UserInfoRepository.getUserName());
                    entity.setPHC_CommentUsername(UserInfoRepository.getUsernick());
                    entity.setPHC_Content(content);
                    entity.setPHC_SecondUserid(config.replyUserid);
                    entity.setPHC_SecondUsername(config.replyUsername);
                    entity.setPHC_Serno(stringRetObjectResponse.retMsg);
                    circleItem.comments.add(entity);
                    commentAdapter.setItems(circleItem.comments);
                    commentAdapter.notifyDataSetChanged();
                }
            });
    }



    private void initData() {
        Bundle arguments = getArguments();
        NewComment newComment = arguments.getParcelable(CommentDetailActivity.NEW_COMEMNT);
        CircleItem circleData = arguments.getParcelable(CommentDetailActivity.CIRCLE_ITEM);

        if (config == null) {
            config = new CommentConfig();
        }
        config.id = newComment.getPHO_Serno();
        config.createdid = newComment.getPHO_CreateUserID();
        config.createdName = newComment.getUSR_Name();

        if (TextUtils.isEmpty(newComment.getPHC_CommentUserid())) {
            ui.circleInput.setCirclePrimaryMenuHint("评论");
        } else {
            config.commentType = CommentConfig.Type.REPLY;
            config.replyUserid = newComment.getPHC_CommentUserid();
            config.replyUsername = newComment.getPHC_CommentUsername();
            ui.circleInput.setCirclePrimaryMenuHint("回复" + config.replyUsername + ":");
        }

        if (circleData == null) {
            String content = SPSaveHelper.getStringValue(UserInfoRepository.getUserName() + "circle_content", "");
            Gson gson = new Gson();
            List<FriendCircleEntity> entity = gson.fromJson(content, new TypeToken<List<FriendCircleEntity>>() {}.getType());
            if (entity != null && !entity.isEmpty() && entity.get(0) != null) {
                circleData = DatasUtil.createCircleData(entity.get(0));
                bindData(circleData);
            } else {
                getCircleDataFromNet(newComment);
            }
        } else {
            bindData(circleData);
        }

    }

    private void getCircleDataFromNet(NewComment newComment) {
        final CircleItem item = new CircleItem();
        item.userid = newComment.getPHO_CreateUserID();
        item.username = newComment.getUSR_Name();
        item.headUrl = newComment.getUSR_userimage();
        item.content = newComment.getPHO_Content();
        item.createTime = newComment.getPHO_CreateDT();
        item.id = newComment.getPHO_Serno();

        if (newComment.getPHO_ImageName() != null) {
            if (newComment.getPHO_ImageName().contains(".mp4")) {
                item.type = "3";// 视频
                item.videoUrl = DatasUtil.createVideoUrl(newComment.getPHO_ImageName(), newComment.getPHO_ImagePath());
            } else {
                item.type = "2";// 图片
                item.photos = DatasUtil.createPhotos(newComment.getPHO_ImageName(), newComment.getPHO_ImagePath());
            }
        }
        getPhotoItemById(item, newComment.getPHO_Serno(), UserInfoRepository.getUserName());
    }


    private void getPhotoItemById(CircleItem item, String pho_serno, String username) {
        Http.getPhotoItemById(pho_serno, username)
            .compose(RxSchedulers.io_main())
            .subscribe(stringRetObjectResponse -> {
                if (stringRetObjectResponse.retCode == 1) {
                    Gson gson = new Gson();
                    List<CommentEntity> list = gson.fromJson(stringRetObjectResponse.retData, new TypeToken<List<CommentEntity>>() {}.getType());
                    List<ZambiaEntity> zams = new ArrayList<>();
                    List<ContentEntity> comments = new ArrayList<>();

                    if (list == null) return;
                    for (CommentEntity commentEntity : list) {
                        if (commentEntity.getPHC_Zambia().equals("1")) {
                            ZambiaEntity zam = new ZambiaEntity();
                            zam.PHC_CommentUserid = commentEntity.getPHC_CommentUserid();
                            zam.PHC_CommentUsername = commentEntity.getPHC_CommentUsername();
                            zams.add(zam);
                        } else {
                            // commentCount++;
                            ContentEntity comment = new ContentEntity();
                            comment.setPHC_Serno(commentEntity.getPHC_Serno());
                            comment.setPHC_Content(commentEntity.getPHC_Content());
                            comment.setPHC_CommentUserid(commentEntity.getPHC_CommentUserid());
                            comment.setPHC_CommentUsername(commentEntity.getPHC_CommentUsername());
                            comment.setPHC_SecondUserid(commentEntity.getPHC_SecondUserid());
                            comment.setPHC_SecondUsername(commentEntity.getPHC_SecondUsername());
                            comments.add(comment);
                        }
                    }
                    item.favorters = zams;
                    item.comments = comments;
                    bindData(item);
                } else {
                    T.show(stringRetObjectResponse.retMsg);
                }
            },
            throwable -> T.show(throwable.getMessage()));
    }

    private void bindData(final CircleItem circleItem) {
        this.circleItem = circleItem;
        String name = circleItem.username;//发朋友圈人的名字
        String headImg = circleItem.headUrl;//

        if (TextUtils.isEmpty(headImg)) {
            headImg = String.format(Route.obtainAvater, circleItem.userid);
        }

        if (headImg.toLowerCase().startsWith("c:\\hnlensweb\\")) {
            headImg = headImg.replace("C:\\HnlensWeb\\", Route.Host);
            headImg = headImg.replace("\\", "/").trim();
        }
        final String content = CyptoConvertUtils.decryptString(circleItem.content);
        String createTime = circleItem.createTime;

        //String sign = BaseApplication.getString(LensImUtil.getUserName() + "sign", "");
        //Glide.with(this).load(headImg).signature(new StringSignature(sign)).placeholder(R.color.im_line_color).into(headIv);
        ImageLoader.getInstance().displayImage(headImg, ui.headIv, BitmapUtil.getAvatarOptions());
        ui.headIv.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), FriendDetailActivity.class);
            intent.putExtra(AppConfig.FRIEND_NAME, circleItem.userid);
            startActivity(intent);
        });

        ui.nameTv.setText(TextUtils.isEmpty(name) ? circleItem.userid : name);
        ui.timeTv.setText(TextUtils.isEmpty(createTime) ? "" : StringUtils.parseDbTime2(createTime));

        if (StringUtils.isEmpty(content)) {
            ui.contentTv.setVisibility(View.GONE);
        } else {
            ui.contentTv.setVisibility(View.VISIBLE);
            ui.contentTv.setText(UrlUtils.formatUrlString(CyptoConvertUtils.decryptString(content)));
            ui.contentTv.setEllipsize(null); // 展开
            ui.contentTv.setSingleLine(false);
            ui.contentTv.setOnLongClickListener(v -> {
                showCollectWindow(false, ui.contentTv.getText().toString(), circleItem,
                    Content.MSG_TYPE_TEXT);
                return true;
            });
        }

        if (!StringUtils.isEmpty(config.createdid) && UserInfoRepository.getUserName().equals(config.createdid)) {
            ui.deleteBtn.setVisibility(View.VISIBLE);
            ui.deleteBtn.setOnClickListener(v -> deleteDialog());
        } else {
            ui.deleteBtn.setVisibility(View.GONE);
        }

        bindFavortAndComment(circleItem);
        ui.urlTipTv.setVisibility(View.GONE);
        if (TextUtils.isEmpty(circleItem.type)) {
            return;
        }
        switch (circleItem.type) {
//            case TYPE_URL:// 处理链接动态的链接内容和和图片
//                String linkImg = circleItem.linkImg;
//                String linkTitle = circleItem.linkTitle;
//                Glide.with(this).load(linkImg).into(holder.urlImageIv);
//                holder.urlContentTv.setText(linkTitle);
//                holder.urlBody.setVisibility(View.VISIBLE);
//                holder.urlTipTv.setVisibility(View.VISIBLE);
//                break;
            case CircleItem.TYPE_IMG:// 处理图片
                handleImage();
                break;
            case CircleItem.TYPE_VIDEO:
                //如果是自己发出的视频，则先看本地是否还存在这个视频
                L.d("这是一个视频");
                handleVideo();
                break;
        }
    }

    /**
     * 处理图片
     */
    private void handleImage() {
        ui.viewStub.getViewStub().setLayoutResource(R.layout.viewstub_imgbody);
        ui.viewStub.getViewStub().inflate();
        final MultiImageView multiImageView = rootView.findViewById(R.id.multiImagView);
        final List<String> photos = circleItem.photos;
        if (photos == null || photos.isEmpty()) {
            multiImageView.setVisibility(View.GONE);
        } else {
            multiImageView.setVisibility(View.VISIBLE);
            multiImageView.setList(photos);
            multiImageView.setOnItemClickListener((view, position) -> {
                ArrayList<AnimationRect> animationRectArrayList = new ArrayList<>();
                SparseArray<ImageView> imageviews = multiImageView.getImageviews();
                for (int i = 0; i < imageviews.size(); i++) {
                    ImageView imageView = imageviews.get(i);
                    if (imageView.getVisibility() == View.VISIBLE) {
                        AnimationRect rect = AnimationRect.buildFromImageView(imageView);
                        if (rect == null) {
                            L.d("根本没有取到iamgeview的信息");
                        } else if (i < photos.size()){
                            rect.setUri(photos.get(i));
                        }
                        animationRectArrayList.add(rect);
                    }
                }
                ArrayList<String> urls = new ArrayList<>(photos);
                StoreManager.getInstance().storeInit(circleItem.id, circleItem.userid, circleItem.username, circleItem.headUrl, Content.MSG_TYPE_PIC);

                Intent intent = GalleryAnimationActivity
                    .newIntent(urls, null, animationRectArrayList, null, position);
                startActivity(intent);
            });

            multiImageView.setOnItemLongClickListener((view, position) ->
                showCollectWindow(false, photos.get(position), circleItem, Content.MSG_TYPE_PIC));
        }
    }

    /**
     * 处理视频
     */
    private void handleVideo() {
        ui.viewStub.getViewStub().setLayoutResource(R.layout.viewstub_videobody);
        ui.viewStub.getViewStub().inflate();

        ImageView videothumbnial = rootView.findViewById(R.id.video_override);
        LinearLayout ll_loading = rootView.findViewById(R.id.ll_loading);
        FrameLayout layoutPlayer = rootView.findViewById(R.id.container_video_play);
        CircleProgress progressBar = rootView.findViewById(R.id.progress_bar);
        ll_loading.setVisibility(View.GONE);
        String videoPathCache = FileCache.getInstance().getVideoPath(circleItem.videoUrl);

        if (!StringUtils.isEmpty(videoPathCache) && FileUtil
            .checkFilePathExists(videoPathCache)) {
            videothumbnial.setVisibility(View.VISIBLE);
            Glide.with(getActivity()).load(circleItem.videoUrl.replace(".mp4", ".jpg")).centerCrop().into(videothumbnial);
            layoutPlayer.setOnClickListener(v -> toPlayer(videothumbnial, videoPathCache));
        } else {
            ll_loading.setVisibility(View.VISIBLE);
        }

        layoutPlayer.setOnLongClickListener(v -> {
            showCollectWindow(true, circleItem.videoUrl, circleItem,
                Content.MSG_TYPE_VIDEO);
            return true;
        });
    }

    /***
     *  去播放视频
     *
     */
    private void toPlayer(ImageView imageView, String videoPath) {
        imageView.setAlpha(1f);
        AnimationRect rect = AnimationRect.buildFromImageView(imageView);
        Intent intent = LookUpVideoActivity.newIntent(getActivity(), rect, videoPath, "circle");
        intent.putExtra("type", "chat");
        startActivity(intent);
    }

    private void bindFavortAndComment(final CircleItem circleItem) {
        final String circleId = circleItem.id;//每个条目的序号
        final List<ZambiaEntity> favortDatas = circleItem.favorters;
        final List<ContentEntity> commentsDatas = circleItem.comments;
        boolean hasFavort = circleItem.favorters != null && circleItem.favorters.size() > 0;
        boolean hasComment = circleItem.comments != null && circleItem.comments.size() > 0;

        //处理点赞列表
        if (hasFavort) {
            handleFavort(favortDatas);
            ui.favortListTv.setVisibility(View.VISIBLE);
        } else {
            ui.favortListTv.setVisibility(View.GONE);
        }

        //处理评论列表
        if (hasComment) {
            ui.commentList.setOnItemClick(commentPosition -> {
                ContentEntity commentItem = commentsDatas.get(commentPosition);
                //回复别人的评论
                if (!UserInfoRepository.getUserName().equals(commentItem.getPHC_CommentUserid())) {
                    CommentConfig config = new CommentConfig();
                    config.id = circleId;
                    config.createdid = circleItem.userid;
                    config.createdName = circleItem.username;
                    config.commentPosition = commentPosition;
                    config.commentType = CommentConfig.Type.REPLY;
                    config.replyUserid = commentItem.getPHC_CommentUserid();
                    config.replyUsername = StringUtils.isEmpty(commentItem.getPHC_CommentUsername()) ? commentItem.getPHC_CommentUserid() : commentItem.getPHC_CommentUsername();
                    showEditTextBody(config);
                }
            });

            ui.commentList.setOnItemLongClick(commentPosition -> {
                ContentEntity commentItem = commentsDatas.get(commentPosition);
                CommentDialog dialog = new CommentDialog(getActivity(), UserInfoRepository.getUserName().equals(commentItem.getPHC_CommentUserid()));
                dialog.setCanceledOnTouchOutside(true);
                dialog.setCancelable(true);
                //删除
                dialog.setDeleteClickListener(() -> deleteComment(commentPosition, commentItem.getPHC_CommentUserid(), commentItem.getPHC_Serno()));
                //复制
                dialog.setCopyClickListener(() -> {
                    ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                    clipboard.setPrimaryClip(ClipData.newPlainText(null, commentItem.getPHC_Content()));
                });
                dialog.show();
            });
            commentAdapter.setItems(commentsDatas);
            commentAdapter.notifyDataSetChanged();
            ui.commentList.setVisibility(View.VISIBLE);
        } else {
            ui.commentList.setVisibility(View.GONE);
        }


        if (hasFavort || hasComment) {
            ui.digCommentBody.setVisibility(View.VISIBLE);
        } else {
            ui.digCommentBody.setVisibility(View.GONE);
        }

        ui.linDig.setVisibility(hasFavort && hasComment ? View.VISIBLE : View.GONE);
    }


    /**
     * 处理点赞
     */
    private void handleFavort(@NonNull List<ZambiaEntity> favortDatas) {
        favortListAdapter.setSpanClickListener(position -> {
            Intent intent = new Intent(getActivity(), FriendDetailActivity.class);
            intent.putExtra(AppConfig.FRIEND_NAME, favortDatas.get(position).PHC_CommentUserid);
            startActivity(intent);
        });
        List<String> favortItems = DatasUtil.getFavortItems(favortDatas);
        favortListAdapter.setitems(favortItems);
        favortListAdapter.notifyDataSetChanged();
    }

    private void showEditTextBody(CommentConfig config) {
        this.config = config;
        measureCircleItemHighAndCommentItemOffset(config);

        ui.circleInput.setCirclePrimaryMenuHint("回复" + config.replyUsername + ":");
        if (!ui.circleInput.emojiconContainerShowed() && mCurrentKeyboardH < 425) {
            TDevice.showSoftKeyboard(ui.circleInput.findViewById(R.id.et_sendmessage));
        }
    }


    private void measureCircleItemHighAndCommentItemOffset(CommentConfig commentConfig) {
        if (commentConfig == null) {
            return;
        }
        mSelectCircleItemH = ui.mItemView.getHeight();

        L.d("条目高度:" + mSelectCircleItemH);
        if (commentConfig.commentType == CommentConfig.Type.REPLY) {
            //回复评论的情况
            if (ui.commentList != null) {
                //找到要回复的评论view,计算出该view距离所属动态底部的距离
                View selectCommentItem = ui.commentList.getChildAt(commentConfig.commentPosition);
                if (selectCommentItem != null) {
                    //选择的commentItem距选择的CircleItem底部的距离
                    mSelectCommentItemOffset = 0;
                    View parentView = selectCommentItem;
                    do {
                        int subItemBottom = parentView.getBottom();
                        parentView = (View) parentView.getParent();
                        if (parentView != null) {
                            mSelectCommentItemOffset += (parentView.getHeight() - subItemBottom);
                        }
                    } while (parentView != null && parentView != ui.mItemView);
                }
            }
        }
    }


    /**
     *  发起网络请求——删除评论
     */
    private void deleteComment(int position, String userid, String PHC_Serno) {
        Http.deleteComment(PHC_Serno, userid)
            .compose(RxSchedulers.io_main())
            .subscribe(
                stringRetObjectResponse -> {
                    if (1 == stringRetObjectResponse.retCode) {
                        showMsg(position, stringRetObjectResponse.retMsg);
                    }
                }, throwable -> T.show("删除失败")

            );
    }

    private void showMsg(int position, String msg) {
        T.showShort(ContextHelper.getContext(), msg);
        commentAdapter.removeOne(position);
    }


    private void showCollectWindow(boolean isVideo, String resPath, CircleItem circleItem,
        int extraType) {
        CollectDialog dialog = new CollectDialog(getActivity(), isVideo);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
        dialog.setCollectItemClickListener(new PopupCollectClickListener(resPath, circleItem, extraType));
    }

    public void deleteDialog() {
        //删除
        final NiftyDialogBuilder builder = NiftyDialogBuilder.getInstance(getActivity());
        builder.withTitle("提示").withMessage("确定删除吗").withButton1Text("取消").withButton2Text("删除")
            .setButton1Click(v -> builder.dismiss())
            .setButton2Click(v -> {
                builder.dismiss();
                deleteRequest();
            })
            .show();
    }


    private void deleteRequest() {
        Http.deleteCircle(config.id, UserInfoRepository.getUserName())
            .compose(RxSchedulers.io_main())
            .subscribe(stringRetObjectResponse -> {
                isDeleteCircle = true;
                if (getActivity() instanceof CommentDetailActivity) {
                    ((CommentDetailActivity) getActivity()).onReturn();
                }
            },
            throwable -> {
                isDeleteCircle = true;
                T.show("删除失败");
            });
    }

    public Intent setResult() {
        Intent intent = new Intent();
        intent.putExtra("circleitem", circleItem);
        intent.putExtra("isDeleteCircle", isDeleteCircle);
        return intent;
    }

    private class PopupCollectClickListener implements CollectDialog.OnItemClickListener {

        private String path;
        private CircleItem mcircleItem;
        private int mType;


        public PopupCollectClickListener(String resPath, CircleItem circleItem, int extraType) {
            this.path = resPath;
            this.mType = extraType;
            this.mcircleItem = circleItem;
        }


        @Override
        public void onItemClick(int position, int dataPosition) {
            switch (position) {
                case 0:
                    break;
                case 2:
                    collect(path, mcircleItem, mType);
                    break;
                default:
                    break;
            }
        }
    }

    public void collect(String path, CircleItem circleItem, int mType) {
        if (mType == Content.MSG_TYPE_TEXT) {
            StoreManager.getInstance().storeCircleText(circleItem.id, circleItem.userid, circleItem.username, path, circleItem.headUrl);
        } else {
            StoreManager.getInstance().storeCircleImageVideo(circleItem.id, circleItem.userid, circleItem.username, path, circleItem.headUrl, mType);
        }
    }
}
