package com.lensim.fingerchat.fingerchat.ui.me.circle_friends.circle_friends_multitype;


import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.lens.chatmodel.helper.FileCache;
import com.lens.chatmodel.ui.image.GalleryAnimationActivity;
import com.lens.chatmodel.ui.profile.FriendDetailActivity;
import com.lens.chatmodel.ui.video.LookUpVideoActivity;
import com.lens.chatmodel.utils.UrlUtils;
import com.lens.chatmodel.view.friendcircle.FavortListView;
import com.lensim.fingerchat.commons.app.AppConfig;
import com.lensim.fingerchat.commons.helper.AnimationRect;
import com.lensim.fingerchat.commons.utils.CyptoConvertUtils;
import com.lensim.fingerchat.commons.utils.FileUtil;
import com.lensim.fingerchat.commons.utils.L;
import com.lensim.fingerchat.commons.utils.StringUtils;
import com.lensim.fingerchat.commons.utils.T;
import com.lensim.fingerchat.commons.utils.TimeUtils;
import com.lensim.fingerchat.commons.utils.UIHelper;
import com.lensim.fingerchat.components.adapter.multitype.ItemViewBinder;
import com.lensim.fingerchat.components.dialog.nifty_dialog.NiftyDialogBuilder;
import com.lensim.fingerchat.components.popupwindow.ActionItem;
import com.lensim.fingerchat.components.popupwindow.SnsPopupWindow;
import com.lensim.fingerchat.components.widget.CircleProgress;
import com.lensim.fingerchat.components.widget.circle_friends.CollectDialog;
import com.lensim.fingerchat.components.widget.circle_friends.CommentDialog;
import com.lensim.fingerchat.components.widget.circle_friends.MultiImageView;
import com.lensim.fingerchat.data.login.UserInfoRepository;
import com.lensim.fingerchat.data.me.CircleItem;
import com.lensim.fingerchat.data.me.content.StoreManager;
import com.lensim.fingerchat.fingerchat.R;
import com.lensim.fingerchat.fingerchat.databinding.ItemCircleViewBinding;
import com.lensim.fingerchat.fingerchat.model.bean.CommentBean;
import com.lensim.fingerchat.fingerchat.model.bean.PhotoBean;
import com.lensim.fingerchat.fingerchat.model.bean.ThumbsBean;
import com.lensim.fingerchat.fingerchat.ui.me.circle_friends.CircleFirendsContract;
import com.lensim.fingerchat.fingerchat.ui.me.circle_friends.CircleFriendsPresenter.DownloadListener;
import com.lensim.fingerchat.fingerchat.ui.me.collection.type.Content;
import com.lensim.fingerchat.fingerchat.ui.me.utils.BitmapUtil;
import com.lensim.fingerchat.fingerchat.ui.me.utils.DatasUtil;
import com.lensim.fingerchat.fingerchat.ui.me.utils.SpliceUrl;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.jessyan.progressmanager.ProgressListener;
import me.jessyan.progressmanager.ProgressManager;
import me.jessyan.progressmanager.body.ProgressInfo;

/**
 * date on 2017/12/19
 * author ll147996
 * describe
 */

public class CircleViewHolder extends ItemViewBinder<PhotoBean, CircleViewHolder.ViewHolder> {

    public static final int HEADVIEW_SIZE = 1;
    private Context context;
    private CircleFirendsContract.Presenter presenter;


    public CircleViewHolder(Context context, CircleFirendsContract.Presenter presenter) {
        this.context = context;
        this.presenter = presenter;
    }

    @NonNull
    @Override
    protected ViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater,
                                            @NonNull ViewGroup parent) {
        View rootView = inflater.inflate(R.layout.item_circle_view, parent, false);
        return new ViewHolder(rootView);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, @NonNull PhotoBean item) {
        setViewHolder(holder, item);
    }


    private void setViewHolder(@NonNull ViewHolder viewHolder, @NonNull PhotoBean circleItem) {
        ItemCircleViewBinding holder = viewHolder.binding;
        UIHelper.setTextSize(14, holder.nameTv, holder.contentTv, holder.txtHide,
            holder.urlTipTv, holder.timeTv, holder.deleteBtn);


        final String content = CyptoConvertUtils.decryptString(circleItem.getPhotoContent());
        String createTime = TimeUtils.progressDate(circleItem.getCreateDatetime());
        final List<String> favortItems = DatasUtil.getFavortItems(circleItem.getThumbsUps());
        final List<CommentBean> commentsDatas = circleItem.getComments();
        boolean hasFavort = circleItem.getThumbsUps() != null && circleItem.getThumbsUps().size() > 0;
        boolean hasComment = circleItem.getComments() != null && circleItem.getComments().size() > 0;

        ImageLoader.getInstance().displayImage(
            circleItem.getUserImage(), holder.headIv, BitmapUtil.getAvatarOptions());

        holder.headIv.setOnClickListener(v -> {
            Intent intent = new Intent(context, FriendDetailActivity.class);
            intent.putExtra(AppConfig.FRIEND_NAME, circleItem.getPhotoCreator());
            context.startActivity(intent);
        });

        holder.nameTv.setText
            (StringUtils.isEmpty(circleItem.getUserName()) ? circleItem.getPhotoCreator() : circleItem.getUserName());
        holder.timeTv.setText(createTime);

        if (StringUtils.isEmpty(content)) {
            holder.contentTv.setVisibility(View.GONE);
        } else {
            holder.contentTv.setVisibility(View.VISIBLE);
            holder.contentTv.setText(UrlUtils.formatUrlString(content).toString());
        }
        holder.contentTv.post(() -> holder.txtHide.setVisibility
            (holder.contentTv.getLineCount() >= 6 ? View.VISIBLE : View.GONE));

        holder.txtHide.setOnClickListener(v -> {
            if ("全文".equals(holder.txtHide.getText())) {
                holder.contentTv.setEllipsize(null); // 展开
                holder.contentTv.setSingleLine(false);
                holder.txtHide.setText("收起");
            } else {
                holder.contentTv.setEllipsize(TextUtils.TruncateAt.END); // 收缩
                holder.contentTv.setMaxLines(6);
                holder.txtHide.setText("全文");
            }
        });

        holder.contentTv.setOnLongClickListener(v -> {
            showCollectWindow(circleItem, viewHolder, false,
                holder.contentTv.getText().toString(), Content.MSG_TYPE_TEXT);
            return true;
        });

        if (UserInfoRepository.getUserName().toLowerCase()
            .equals(circleItem.getPhotoCreator().toLowerCase())) {
            holder.deleteBtn.setVisibility(View.VISIBLE);
        } else {
            holder.deleteBtn.setVisibility(View.GONE);
        }

        //删除
        holder.deleteBtn.setOnClickListener(v -> showDleteDialog(circleItem));

        holder.favortListTv.setAdapter(viewHolder.favortListAdapter);
        holder.commentList.setAdapter(viewHolder.commentAdapter);
        viewHolder.commentAdapter.setListener((view, id) -> {
            Intent intent = new Intent(context, FriendDetailActivity.class);
            intent.putExtra(AppConfig.FRIEND_NAME, id);
            context.startActivity(intent);
        });
        //处理点赞列表
        if (hasFavort) {
            handleFavort(viewHolder, favortItems);
            holder.favortListTv.setVisibility(View.VISIBLE);
        } else {
            holder.favortListTv.setVisibility(View.GONE);
        }

        //处理评论列表
        if (hasComment) {
            holder.commentList.setOnItemClick(commentPosition -> {
                CommentBean commentItem = commentsDatas.get(commentPosition);
                if (UserInfoRepository.getUserName().equals(commentItem.getCreatorUserid())) {
                    //复制或者删除自己的评论
                    handleMyComment(viewHolder, commentItem);
                } else {
                    //回复别人的评论
                    replyComment(viewHolder, circleItem, commentItem, commentPosition);
                }
            });
            //长按进行复制或者删除
            holder.commentList.setOnItemLongClick(commentPosition -> {
                CommentBean commentItem = commentsDatas.get(commentPosition);
                longClickCopy(commentItem);
            });

            viewHolder.commentAdapter.setItems(commentsDatas);
            viewHolder.commentAdapter.notifyDataSetChanged();
            holder.commentList.setVisibility(View.VISIBLE);
        } else {
            holder.commentList.setVisibility(View.GONE);
        }

        if (hasFavort || hasComment) {
            holder.digCommentBody.setVisibility(View.VISIBLE);
        } else {
            holder.digCommentBody.setVisibility(View.GONE);
        }

        holder.linDig.setVisibility(hasFavort && hasComment ? View.VISIBLE : View.GONE);

        //处理点赞，评论弹窗 popupwindow
        holder.snsBtn.setOnClickListener(v -> showSnsPopupWindow(v, viewHolder, circleItem));

        holder.urlTipTv.setVisibility(View.GONE);

        if (CircleItem.TYPE_IMG.equals(circleItem.getType()) && holder.viewStub.getViewStub() != null) {
            addImgView(holder.viewStub.getViewStub(), viewHolder);
            // 处理图片
            handleImage(viewHolder, circleItem);
        } else if (CircleItem.TYPE_VIDEO.equals(circleItem.getType()) && holder.viewStub.getViewStub() != null) {
            L.d("这是一个视频");
            addVideoView(holder.viewStub.getViewStub(), viewHolder);
            handleVideo(viewHolder, circleItem);
        }

    }


    /**
     * 处理点赞，评论弹窗
     */
    private void showSnsPopupWindow(View view, @NonNull ViewHolder viewHolder, @NonNull PhotoBean circleItem) {
        final SnsPopupWindow snsPopupWindow = viewHolder.snsPopupWindow;
        //判断是否已点赞
        boolean isThumbsUps = false;
        for (ThumbsBean thumbsBean : circleItem.getThumbsUps()) {
            if (thumbsBean.getThumbsUserId().equals(circleItem.getPhotoCreator())) {
                isThumbsUps = true;
                return;
            }
        }
        if (!isThumbsUps) {
            snsPopupWindow.getmActionItems().get(0).mTitle = "取消";
        } else {
            snsPopupWindow.getmActionItems().get(0).mTitle = "点赞";
        }
        snsPopupWindow.update();
        snsPopupWindow.setmItemClickListener(
            new PopupItemClickListener(
                viewHolder.getAdapterPosition() - HEADVIEW_SIZE, circleItem));
        //弹出popupwindow
        snsPopupWindow.showPopupWindow(view);
    }

    /**
     * 处理视频
     */
    private void handleVideo(@NonNull ViewHolder viewHolder, @NonNull PhotoBean circleItem) {
        //如果是自己发出的视频，则先看本地是否还存在这个视频
        final String videoPath = FileCache.getInstance().getVideoPath(circleItem.getPhotoUrl());
        if (!StringUtils.isEmpty(videoPath) && FileUtil.checkFilePathExists(videoPath)) {
            L.i("视频文件用的缓存");
            handleCacheVideo(viewHolder, circleItem, videoPath);
        } else {
            viewHolder.ll_loading.setVisibility(View.VISIBLE);
            viewHolder.img_loading.setVisibility(View.GONE);
            L.i("视频文件没有缓存，开始下载");
            handleNetworkVideo(viewHolder, circleItem);
        }

        viewHolder.layoutPlayer.setOnLongClickListener(v -> {
            showCollectWindow(circleItem, viewHolder, true,
                circleItem.getPhotoUrl(), Content.MSG_TYPE_VIDEO);
            return true;
        });
    }


    /**
     * 处理本地视频
     */
    private void handleCacheVideo(@NonNull ViewHolder viewHolder, @NonNull PhotoBean circleItem,
                                  String videoPath) {
        viewHolder.videothumbnial.setVisibility(View.VISIBLE);
        viewHolder.videothumbnial.setAlpha(1f);
        Glide.with(context)
            .load(circleItem.getPhotoUrl()
                .replace(".mp4", ".jpg"))
            .asBitmap()
            .centerCrop()
            .into(viewHolder.videothumbnial);
        viewHolder.layoutPlayer.setOnClickListener(
            v -> toPlayer(viewHolder.videothumbnial, videoPath, false));
    }

    /**
     * 处理网络视频
     */
    private void handleNetworkVideo(@NonNull ViewHolder viewHolder, @NonNull PhotoBean circleItem) {
        ProgressManager.getInstance().addResponseListener(circleItem.getPhotoUrl(), new ProgressListener() {
            @Override
            public void onError(long id, Exception e) {
            }

            @Override
            public void onProgress(ProgressInfo progressInfo) {
                viewHolder.loading.setPercent(progressInfo.getPercent());
            }
        });

        presenter.downloadVideoFile(circleItem.getPhotoUrl(), new DownloadListener() {
            @Override
            public void loadSuccess(byte[] bytes) {
                boolean bool = false;
                try {
                    bool = FileCache.getInstance().saveVideo(circleItem.getPhotoUrl(), bytes);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                viewHolder.ll_loading.setVisibility(View.GONE);
                viewHolder.img_loading.setVisibility(View.VISIBLE);
                if (bool) {
                    final String videoPath = FileCache.getInstance().getVideoPath(circleItem.getPhotoUrl());
                    viewHolder.videothumbnial.setAlpha(1f);
                    Glide.with(context)
                        .load(circleItem.getPhotoUrl().replace(".mp4", ".jpg"))
                        .asBitmap()
                        .centerCrop()
                        .into(viewHolder.videothumbnial);
                    viewHolder.layoutPlayer.setOnClickListener(v ->
                        toPlayer(viewHolder.videothumbnial, videoPath, false));
                }
            }

            @Override
            public void loadFailure() {

            }
        });
    }

    /**
     * 处理图片
     */
    private void handleImage(@NonNull ViewHolder viewHolder, @NonNull PhotoBean circleItem) {
        final List<String> photos = Arrays.asList(circleItem.getPhotoUrl().split(","));
        if (photos != null && photos.size() > 0) {
            viewHolder.multiImageView.setVisibility(View.VISIBLE);
            viewHolder.multiImageView.setList(photos);
            viewHolder.multiImageView.setOnItemClickListener((view, position) -> {
                ArrayList<AnimationRect> animationRectArrayList = new ArrayList<>();
                SparseArray<ImageView> imageviews = viewHolder.multiImageView
                    .getImageviews();
                for (int i = 0; i < imageviews.size(); i++) {
                    ImageView imageView = imageviews.get(i);
                    if (imageView.getVisibility() == View.VISIBLE) {
                        AnimationRect rect = AnimationRect
                            .buildFromImageView(imageView);
                        if (rect == null) {
                            L.d("根本没有取到iamgeview的信息");
                        } else {
                            if (i < photos.size()) {
                                rect.setUri(photos.get(i));
                            }
                        }
                        animationRectArrayList.add(rect);
                    }
                }
                ArrayList<String> urls = new ArrayList<>(photos);

                StoreManager.getInstance().storeInit(circleItem.getPhotoId() + "", circleItem.getPhotoCreator(),
                    circleItem.getUserName(), circleItem.getUserImage(), Content.MSG_TYPE_PIC);

                Intent intent = GalleryAnimationActivity
                    .newIntent(SpliceUrl.getUrls(urls), null, animationRectArrayList, null, position,"");
                context.startActivity(intent);
            });

            viewHolder.multiImageView.setOnItemLongClickListener((view, position) ->
                showCollectWindow(circleItem, viewHolder, false,
                    photos.get(position), Content.MSG_TYPE_PIC)
            );
        } else {
            viewHolder.multiImageView.setVisibility(View.GONE);
        }
    }


    private void addImgView(ViewStub viewStubProxy, @NonNull ViewHolder viewHolder) {
        viewStubProxy.setLayoutResource(R.layout.viewstub_imgbody);
        View itemView = viewStubProxy.inflate();
        MultiImageView multiImageView = itemView.findViewById(R.id.multiImagView);
        if (multiImageView != null) {
            viewHolder.multiImageView = multiImageView;
        }
    }

    private void addVideoView(ViewStub viewStubProxy, @NonNull ViewHolder viewHolder) {
        viewStubProxy.setLayoutResource(R.layout.viewstub_videobody);
        View itemView = viewStubProxy.inflate();
        ImageView videothumbnial = itemView.findViewById(R.id.video_override);
        CircleProgress loading = itemView.findViewById(R.id.progress_bar);
        ImageView img_loading = itemView.findViewById(R.id.icon_play);
        LinearLayout ll_loading = itemView.findViewById(R.id.ll_loading);
        FrameLayout layoutPlayer = itemView.findViewById(R.id.container_video_play);
        if (layoutPlayer != null) {
            viewHolder.layoutPlayer = layoutPlayer;
            viewHolder.videothumbnial = videothumbnial;
            viewHolder.img_loading = img_loading;
            viewHolder.loading = loading;
            viewHolder.ll_loading = ll_loading;
        }
    }


    private void showDleteDialog(PhotoBean circleItem) {
        final NiftyDialogBuilder builder = NiftyDialogBuilder.getInstance(context);
        builder.withTitle("提示").withMessage("确定删除吗").withButton1Text("取消")
            .withButton2Text("删除")
            .setButton1Click(v1 -> builder.dismiss())
            .setButton2Click(v1 -> {
                builder.dismiss();
                presenter.deleteCircle(circleItem.getPhotoId() + "");
            }).show();
    }

    /**
     * 长按复制
     */
    private void longClickCopy(@NonNull CommentBean commentItem) {
        CommentDialog dialog = new CommentDialog(
            context, UserInfoRepository.getUserName().equals(commentItem.getCommentUserid()));
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        dialog.show();
        dialog.setCopyClickListener(() -> {
            ClipboardManager clipboard =
                (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setPrimaryClip(ClipData.newPlainText(null, commentItem.getCommentContent()));
        });
    }

    /**
     * 回复评论
     */
    private void replyComment(@NonNull ViewHolder viewHolder, @NonNull PhotoBean circleItem,
                              CommentBean commentItem, int commentPosition) {
        int circlePosition = viewHolder.getAdapterPosition() - HEADVIEW_SIZE;
        presenter.replyComment(circlePosition, circleItem, commentItem, commentPosition);
    }

    /**
     * 复制或者删除自己的评论
     */
    private void handleMyComment(@NonNull ViewHolder viewHolder, @NonNull CommentBean commentItem) {
        CommentDialog dialog = new CommentDialog(
            context, UserInfoRepository.getUserName().equals(commentItem.getCreatorUserid()));

        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        dialog.show();
        dialog.setDeleteClickListener(() ->
            presenter.deleteComment(
                viewHolder.getAdapterPosition() - HEADVIEW_SIZE,
                commentItem.getCommentUserid(), commentItem.getPhotoSerno())
        );

    }

    /**
     * 处理点赞
     */
    private void handleFavort(@NonNull ViewHolder viewHolder, @NonNull List<String> favortItems) {
        viewHolder.favortListAdapter.setSpanClickListener(position -> {
            Intent intent = new Intent(context, FriendDetailActivity.class);
            intent.putExtra(AppConfig.FRIEND_NAME,
                favortItems.get(position));
            context.startActivity(intent);
        });
        viewHolder.favortListAdapter.setitems(favortItems);
        viewHolder.favortListAdapter.notifyDataSetChanged();
    }


    private void showCollectWindow(PhotoBean circleItem, ViewHolder viewHolder, boolean isVideo, String resPath,
                                   int extraType) {
        CollectDialog dialog = new CollectDialog(context, isVideo);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
        dialog.setCollectItemClickListener(
            new PopupCollectClickListener(circleItem, resPath, viewHolder, extraType));
    }


    private void collect(PhotoBean circleItem, String path, int mType) {
        if (mType == Content.MSG_TYPE_TEXT) {
            StoreManager.getInstance()
                .storeCircleText(circleItem.getPhotoId() + "", circleItem.getPhotoCreator(),
                    circleItem.getUserName(), path, circleItem.getUserImage());
        } else {
            StoreManager.getInstance()
                .storeCircleImageVideo(circleItem.getPhotoId() + "", circleItem.getPhotoCreator(),
                    circleItem.getUserName(), path, circleItem.getUserImage(), mType);
        }
    }


    /***
     *  去播放视频
     *  @param isSilentPlay 是否静音播放
     *
     */
    private void toPlayer(ImageView imageView, String videoPath, boolean isSilentPlay) {
        AnimationRect rect = AnimationRect.buildFromImageView(imageView);
        Intent intent = LookUpVideoActivity.newIntent(context, rect, videoPath, "circle");
        intent.putExtra("isSilent", isSilentPlay);
        context.startActivity(intent);
    }


    private void toPlayer(@NonNull PhotoBean item, @NonNull ViewHolder viewHolder, boolean isSilentPlay) {
        AnimationRect rect = AnimationRect.buildFromImageView(viewHolder.videothumbnial);
        final String videoPath = FileCache.getInstance().getVideoPath(item.getPhotoUrl());
        if (!StringUtils.isEmpty(videoPath) && FileUtil.checkFilePathExists(videoPath)) {
            Intent intent = LookUpVideoActivity
                .newIntent(context, rect, videoPath, "circle");
            intent.putExtra("isSilent", isSilentPlay);
            context.startActivity(intent);
        } else {
            T.showShort(context, "播放出错");
        }
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        /**
         * 图片
         */
        MultiImageView multiImageView;
        ImageView videothumbnial, img_loading;
        FrameLayout layoutPlayer;
        LinearLayout ll_loading;
        CircleProgress loading;
        SnsPopupWindow snsPopupWindow;
        FavortListView.Adapter favortListAdapter;
        CommentAdapter commentAdapter;
        ItemCircleViewBinding binding;

        public ViewHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
            commentAdapter = new CommentAdapter(itemView.getContext());
            favortListAdapter = new FavortListView.Adapter();
            snsPopupWindow = new SnsPopupWindow(itemView.getContext());
        }
    }

    private class PopupCollectClickListener implements CollectDialog.OnItemClickListener {

        private String path;
        private ViewHolder viewHolder;
        private int mType;
        private PhotoBean item;

        private PopupCollectClickListener(PhotoBean item, String resPath, ViewHolder viewHolder, int extraType) {
            this.path = resPath;
            this.mType = extraType;
            this.viewHolder = viewHolder;
            this.item = item;
        }

        @Override
        public void onItemClick(int position, int dataPosi) {
            switch (position) {
                case 0:
                    toPlayer(item, viewHolder, true);
                    break;
                case 2:
                    collect(item, path, mType);
                    break;
            }
        }
    }


    private class PopupItemClickListener implements SnsPopupWindow.OnItemClickListener {

        //动态在列表中的位置
        private int mCirclePosition;
        private long mLasttime = 0;
        private PhotoBean mCircleItem;

        private PopupItemClickListener(int circlePosition, PhotoBean circleItem) {
            this.mCirclePosition = circlePosition;
            this.mCircleItem = circleItem;
        }

        @Override
        public void onItemClick(View view, ActionItem actionitem, int position) {
            switch (position) {
                case 0://点赞、取消点赞
                    //防止快速点击操作
                    if (System.currentTimeMillis() - mLasttime < 700) {
                        return;
                    }
                    mLasttime = System.currentTimeMillis();
                    if ("点赞".equals(actionitem.mTitle.toString())) {
                        presenter.addFavort(mCircleItem, mCirclePosition);
                    } else {//取消点赞
                        presenter.deleteFavort(mCircleItem, mCirclePosition);
                    }

                    break;
                case 1://点击评论编写
                    presenter.writeCommen(mCircleItem, mCirclePosition);
                    break;
            }
        }
    }

}
