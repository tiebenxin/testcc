package com.lensim.fingerchat.fingerchat.ui.me.circle_friends.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.signature.StringSignature;
import com.lens.chatmodel.helper.FileCache;
import com.lens.chatmodel.ui.image.GalleryAnimationActivity;
import com.lens.chatmodel.ui.image.MultiImageSelectorActivity;
import com.lens.chatmodel.ui.profile.FriendDetailActivity;
import com.lens.chatmodel.ui.video.LookUpVideoActivity;
import com.lens.chatmodel.utils.UrlUtils;
import com.lensim.fingerchat.commons.app.AppConfig;
import com.lensim.fingerchat.commons.global.Route;
import com.lensim.fingerchat.commons.helper.AnimationRect;
import com.lensim.fingerchat.commons.helper.ContextHelper;
import com.lensim.fingerchat.commons.utils.CyptoConvertUtils;
import com.lensim.fingerchat.commons.utils.FileUtil;
import com.lensim.fingerchat.commons.utils.L;
import com.lensim.fingerchat.commons.utils.StringUtils;
import com.lensim.fingerchat.commons.utils.T;
import com.lensim.fingerchat.commons.utils.TimeUtils;
import com.lensim.fingerchat.commons.utils.UIHelper;
import com.lensim.fingerchat.components.dialog.nifty_dialog.NiftyDialogBuilder;
import com.lensim.fingerchat.components.popupwindow.ActionItem;
import com.lensim.fingerchat.components.popupwindow.SnsPopupWindow;
import com.lensim.fingerchat.components.widget.circle_friends.CollectDialog;
import com.lensim.fingerchat.components.widget.circle_friends.CommentDialog;
import com.lensim.fingerchat.data.Http;
import com.lensim.fingerchat.data.RxSchedulers;
import com.lensim.fingerchat.data.login.UserInfoRepository;
import com.lensim.fingerchat.data.me.CircleItem;
import com.lensim.fingerchat.data.me.ZambiaEntity;
import com.lensim.fingerchat.data.me.circle_friend.ContentEntity;
import com.lensim.fingerchat.data.me.content.StoreManager;
import com.lensim.fingerchat.data.repository.SPSaveHelper;
import com.lensim.fingerchat.fingerchat.R;
import com.lensim.fingerchat.fingerchat.base.BaseRecycleViewAdapter;
import com.lensim.fingerchat.fingerchat.component.download.DownloadApi;
import com.lensim.fingerchat.fingerchat.component.download.progress.DownloadProgressListener;
import com.lensim.fingerchat.fingerchat.databinding.ItemCircleViewBinding;
import com.lensim.fingerchat.fingerchat.databinding.ItemHeadCircleBinding;
import com.lensim.fingerchat.fingerchat.ui.me.circle_friends.CircleFirendsContract;
import com.lensim.fingerchat.fingerchat.ui.me.circle_friends.CircleFriendsActivity;
import com.lensim.fingerchat.fingerchat.ui.me.circle_friends.CircleFriendsPresenter;
import com.lensim.fingerchat.fingerchat.ui.me.circle_friends.LookupCommentActivity;
import com.lensim.fingerchat.fingerchat.ui.me.circle_friends.adapter.viewholder.CircleViewHolder;
import com.lensim.fingerchat.fingerchat.ui.me.circle_friends.adapter.viewholder.ImageViewHolder;
import com.lensim.fingerchat.fingerchat.ui.me.circle_friends.adapter.viewholder.TextViewHolder;
import com.lensim.fingerchat.fingerchat.ui.me.circle_friends.adapter.viewholder.VideoViewHolder;
import com.lensim.fingerchat.fingerchat.ui.me.circle_friends.adapter.viewholder.HeaderViewHolder;
import com.lensim.fingerchat.fingerchat.ui.me.collection.type.Content;
import com.lensim.fingerchat.fingerchat.ui.me.utils.BitmapUtil;
import com.lensim.fingerchat.fingerchat.ui.me.utils.CircleFriendsHelper;
import com.lensim.fingerchat.fingerchat.ui.me.utils.DatasUtil;
import com.lensim.fingerchat.fingerchat.ui.me.utils.SpliceUrl;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.reactivex.functions.Consumer;
import me.jessyan.progressmanager.ProgressListener;
import me.jessyan.progressmanager.ProgressManager;
import me.jessyan.progressmanager.body.ProgressInfo;

public class CircleFriendsAdapter extends BaseRecycleViewAdapter<CircleItem, BaseRecycleViewAdapter.BaseRecycleViewHolder> {
    public static final int HEAD_VIEW_SIZE = 1;

    public final static int TYPE_HEAD = 0;

    private Context context;
    private CircleFirendsContract.Presenter presenter;
    private OnItemClickListener onItemClickListener;

    public CircleFriendsAdapter(Context context, CircleFirendsContract.Presenter presenter) {
        this.context = context;
        this.presenter = presenter;
    }

    public void setOnItemCliclkListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_HEAD;
        }

        int itemType = 0;
        CircleItem item = datas.get(position - 1);
        if (CircleItem.TYPE_IMG.equals(item.type)) {
            itemType = CircleViewHolder.TYPE_IMAGE;
        } else if (CircleItem.TYPE_VIDEO.equals(item.type)) {
            itemType = CircleViewHolder.TYPE_VIDEO;
        } else {
            itemType = CircleViewHolder.TYPE_TEXT;
        }

        return itemType;
    }

    @Override
    public BaseRecycleViewAdapter.BaseRecycleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        BaseRecycleViewAdapter.BaseRecycleViewHolder viewHolder;
        if (viewType == TYPE_HEAD) {
            View headView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_head_circle, parent, false);
            viewHolder = new HeaderViewHolder(headView);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_circle_view, parent, false);

            if (viewType == CircleViewHolder.TYPE_IMAGE) {
                viewHolder = new ImageViewHolder(view);
            } else if (viewType == CircleViewHolder.TYPE_VIDEO) {
                viewHolder = new VideoViewHolder(view);
            } else {
                viewHolder = new TextViewHolder(view);
            }
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(BaseRecycleViewAdapter.BaseRecycleViewHolder viewHolder, int position) {
        if (getItemViewType(position) == TYPE_HEAD) {
            HeaderViewHolder holder = (HeaderViewHolder) viewHolder;
            ItemHeadCircleBinding binding = holder.binding;

            String themePath = CircleFriendsHelper.getThemePath(UserInfoRepository.getUserName());
            if (!StringUtils.isEmpty(themePath)) {
                Glide.with(ContextHelper.getApplication())
                    .load(themePath)
                    .placeholder(R.drawable.pengyouquan_theme)
                    .error(R.drawable.pengyouquan_theme)
                    .signature(new StringSignature(getSaveAvatarTime()))
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .into(binding.circleTheme);
                binding.circleChangeText.setVisibility(View.GONE);
            }

            String headImg = String.format(Route.obtainAvater, UserInfoRepository.getUserName());
            ImageLoader.getInstance()
                .displayImage(headImg, binding.circleItemHead, BitmapUtil.getAvatarOptions());
            binding.circleUsername.setText(UserInfoRepository.getUsernick());

            binding.circleTheme.setOnClickListener(v -> {
                if (context instanceof CircleFriendsActivity) {
                    CircleFriendsActivity activity = (CircleFriendsActivity) context;
                    Intent photo = new Intent(activity, MultiImageSelectorActivity.class);
                    photo.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, true);
                    photo.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE,
                        MultiImageSelectorActivity.MODE_SINGLE);
                    activity.startActivityForResult(photo, AppConfig.REQUEST_SINGLE_IMAGE);
                }
            });

            binding.circleItemHead.setOnClickListener(v -> {
                Intent intent = new Intent(context, FriendDetailActivity.class);
                intent.putExtra(AppConfig.FRIEND_NAME, UserInfoRepository.getUserName());
                context.startActivity(intent);
            });

            final int newCount = SPSaveHelper
                .getIntValue(UserInfoRepository.getUserName() + LookupCommentActivity.CIRCLE_COMMENT, 0);
            if (newCount <= 0) {
                binding.llCircleNewMessage.setVisibility(View.GONE);
            } else {
                binding.llCircleNewMessage.setVisibility(View.VISIBLE);
                binding.mCircleNewMessage.setText(newCount + "条新消息");
//            String avatarUrl = LensImUtil.getAvatarPath(
//                com.lens.lensfly.smack.account.AccountManager.getInstance().getLastCircleUser());
//            Glide.with(context).load(avatarUrl).into(binding.imgNewCircleAvatar);
            }

            getNewCommentCount(binding, newCount);

            binding.llCircleNewMessage.setOnClickListener(v -> {
                v.setVisibility(View.GONE);
                ((Activity) context).startActivityForResult
                    (new Intent(context, LookupCommentActivity.class), 12);
            });
        } else {
            CircleViewHolder holder = (CircleViewHolder) viewHolder;
            ItemCircleViewBinding binding = holder.binding;
            UIHelper.setTextSize(14, binding.nameTv, binding.contentTv, binding.txtHide,
                binding.urlTipTv, binding.timeTv, binding.deleteBtn);

            CircleItem circleItem = datas.get(position - HEAD_VIEW_SIZE);
            final String content = CyptoConvertUtils.decryptString(circleItem.content);
            String createTime = TimeUtils.progressDate(circleItem.createTime);
            final List<ContentEntity> commentsDatas = circleItem.comments;
            boolean hasFavort = circleItem.favorters != null && circleItem.favorters.size() > 0;
            boolean hasComment = circleItem.comments != null && circleItem.comments.size() > 0;

            ImageLoader.getInstance().displayImage(
                circleItem.headUrl, binding.headIv, BitmapUtil.getAvatarOptions());

            binding.headIv.setOnClickListener(v -> {
                if (onItemClickListener != null) {
                    onItemClickListener.onIntemClick(v, position);
                }
                Intent intent = new Intent(context, FriendDetailActivity.class);
                intent.putExtra(AppConfig.FRIEND_NAME, circleItem.userid);
                context.startActivity(intent);
            });

            binding.item.setOnClickListener(v -> {
                if (onItemClickListener != null) {
                    onItemClickListener.onIntemClick(v, position);
                }
            });

            binding.nameTv.setText
                (StringUtils.isEmpty(circleItem.username) ? circleItem.userid : circleItem.username);
            binding.timeTv.setText(createTime);

            if (StringUtils.isEmpty(content)) {
                binding.contentTv.setVisibility(View.GONE);
            } else {
                binding.contentTv.setVisibility(View.VISIBLE);
                binding.contentTv.setText(UrlUtils.formatUrlString(content).toString());
            }
            binding.contentTv.post(() -> binding.txtHide.setVisibility
                (binding.contentTv.getLineCount() >= 6 ? View.VISIBLE : View.GONE));

            binding.txtHide.setOnClickListener(v -> {
                if ("全文".equals(binding.txtHide.getText())) {
                    binding.contentTv.setEllipsize(null); // 展开
                    binding.contentTv.setSingleLine(false);
                    binding.txtHide.setText("收起");
                } else {
                    binding.contentTv.setEllipsize(TextUtils.TruncateAt.END); // 收缩
                    binding.contentTv.setMaxLines(6);
                    binding.txtHide.setText("全文");
                }
            });

            binding.contentTv.setOnLongClickListener(v -> {
                showCollectWindow(circleItem, holder, false,
                    binding.contentTv.getText().toString(), Content.MSG_TYPE_TEXT);
                return true;
            });

            if (UserInfoRepository.getUserName().toLowerCase()
                .equals(circleItem.userid.toLowerCase())) {
                binding.deleteBtn.setVisibility(View.VISIBLE);
            } else {
                binding.deleteBtn.setVisibility(View.GONE);
            }

            //删除
            binding.deleteBtn.setOnClickListener(v -> {
                showDleteDialog(circleItem);
            });

            binding.favortListTv.setAdapter(holder.favortListAdapter);
            binding.commentList.setAdapter(holder.commentAdapter);
            holder.commentAdapter.setListener((view, id) -> {
                Intent intent = new Intent(context, FriendDetailActivity.class);
                intent.putExtra(AppConfig.FRIEND_NAME, id);
                context.startActivity(intent);
            });
            //处理点赞列表
            if (hasFavort) {
                handleFavort(holder, circleItem.favorters);
                binding.favortListTv.setVisibility(View.VISIBLE);
            } else {
                binding.favortListTv.setVisibility(View.GONE);
            }

            //处理评论列表
            if (hasComment) {
                binding.commentList.setOnItemClick(commentPosition -> {
                    ContentEntity commentItem = commentsDatas.get(commentPosition);
                    if (UserInfoRepository.getUserName().equals(commentItem.getPHC_CommentUserid())) {
                        //复制或者删除自己的评论
                        handleMyComment(holder, commentItem);
                    } else {
                        //回复别人的评论
                        replyComment(holder, circleItem, commentItem, commentPosition);
                    }
                });
                //长按进行复制或者删除
                binding.commentList.setOnItemLongClick(commentPosition -> {
                    ContentEntity commentItem = commentsDatas.get(commentPosition);
                    longClickCopy(commentItem);
                });

                holder.commentAdapter.setItems(commentsDatas);
                holder.commentAdapter.notifyDataSetChanged();
                binding.commentList.setVisibility(View.VISIBLE);
            } else {
                binding.commentList.setVisibility(View.GONE);
            }

            if (hasFavort || hasComment) {
                binding.digCommentBody.setVisibility(View.VISIBLE);
            } else {
                binding.digCommentBody.setVisibility(View.GONE);
            }

            binding.linDig.setVisibility(hasFavort && hasComment ? View.VISIBLE : View.GONE);

            //处理点赞，评论弹窗 popupwindow
            binding.snsBtn.setOnClickListener(v -> {
                showSnsPopupWindow(v, holder, circleItem);
            });

            binding.urlTipTv.setVisibility(View.GONE);

            switch (holder.viewType) {
                case CircleViewHolder.TYPE_IMAGE: // 处理图片
                    ImageViewHolder imageViewHolder = (ImageViewHolder) holder;
                    final List<String> photos = circleItem.photos;
                    if (photos != null && photos.size() > 0) {
                        imageViewHolder.multiImageView.setVisibility(View.VISIBLE);
                        imageViewHolder.multiImageView.setList(photos);
                        imageViewHolder.multiImageView.setOnItemClickListener((view, position1) -> {
                            ArrayList<AnimationRect> animationRectArrayList = new ArrayList<>();
                            SparseArray<ImageView> imageviews = imageViewHolder.multiImageView
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

                            StoreManager.getInstance().storeInit(circleItem.id, circleItem.userid,
                                circleItem.username, circleItem.headUrl, Content.MSG_TYPE_PIC);

                            Intent intent = GalleryAnimationActivity
                                .newIntent(SpliceUrl.getUrls((ArrayList<String>) photos), null, animationRectArrayList, null, position1);
                            context.startActivity(intent);
                        });

                        imageViewHolder.multiImageView.setOnItemLongClickListener((view, position12) ->
                            showCollectWindow(circleItem, imageViewHolder, false,
                                photos.get(position), Content.MSG_TYPE_PIC));
                    }else {
                        imageViewHolder.multiImageView.setVisibility(View.GONE);
                    }
                    break;
                case CircleViewHolder.TYPE_VIDEO:
                    VideoViewHolder videoViewHolder = (VideoViewHolder) holder;
                    //如果是自己发出的视频，则先看本地是否还存在这个视频
                    final String videoPath = FileCache.getInstance().getVideoPath(circleItem.videoUrl);
                    if (!StringUtils.isEmpty(videoPath) && FileUtil.checkFilePathExists(videoPath)) {
                        L.i("视频文件用的缓存");
                        handleCacheVideo(videoViewHolder, circleItem, videoPath);
                    } else {
                        videoViewHolder.ll_loading.setVisibility(View.VISIBLE);
                        videoViewHolder.img_loading.setVisibility(View.GONE);
                        L.i("视频文件没有缓存，开始下载");
                        handleNetworkVideo(videoViewHolder, circleItem);
                    }
                    videoViewHolder.layoutPlayer.setOnLongClickListener(v -> {
                        showCollectWindow(circleItem, videoViewHolder, true,
                            circleItem.videoUrl, Content.MSG_TYPE_VIDEO);
                        return true;
                    });
                    break;
            }

        }
    }

    /**
     * 处理本地视频
     */
    private void handleCacheVideo(@NonNull VideoViewHolder viewHolder, @NonNull CircleItem circleItem,
                                  String videoPath) {
        viewHolder.videothumbnial.setVisibility(View.VISIBLE);
        viewHolder.videothumbnial.setAlpha(1f);
        Glide.with(context)
            .load(circleItem.videoUrl
                .replace(".mp4", ".jpg"))
            .asBitmap()
            .centerCrop()
            .into(viewHolder.videothumbnial);
        viewHolder.layoutPlayer.setOnClickListener(v -> {
            toPlayer(viewHolder.videothumbnial, videoPath, false);
        });
    }

    /**
     * 处理网络视频
     */
    private void handleNetworkVideo(@NonNull VideoViewHolder viewHolder, @NonNull CircleItem circleItem) {
        ProgressManager.getInstance().addResponseListener(circleItem.videoUrl, new ProgressListener() {
            @Override
            public void onError(long id, Exception e) {
            }

            @Override
            public void onProgress(ProgressInfo progressInfo) {
                viewHolder.loading.setPercent(progressInfo.getPercent());
            }
        });
        DownloadProgressListener downloadProgressListener = (bytesRead, contentLength, done) -> {

        };
        new DownloadApi(downloadProgressListener).downloadVideo(circleItem.videoUrl, bytes -> {
            viewHolder.ll_loading.setVisibility(View.GONE);
            viewHolder.img_loading.setVisibility(View.VISIBLE);
            Glide.with(ContextHelper.getContext())
                .load(circleItem.videoUrl.replace(".mp4", ".jpg"))
                .centerCrop()
                .into(viewHolder.videothumbnial);
            viewHolder.layoutPlayer.setOnClickListener(v -> {
                toPlayer(viewHolder.videothumbnial, circleItem.videoUrl, false);
            });
        });
    }

    @Override
    public int getItemCount() {
        return datas.size() + 1;
    }

    private void showCollectWindow(CircleItem circleItem, CircleViewHolder viewHolder, boolean isVideo, String resPath,
                                   int extraType) {
        CollectDialog dialog = new CollectDialog(context, isVideo);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
        dialog.setCollectItemClickListener(
            new PopupCollectClickListener(circleItem, resPath, viewHolder, extraType));
    }

    /**
     * 处理点赞，评论弹窗
     */
    private void showSnsPopupWindow(View view, @NonNull CircleViewHolder viewHolder, @NonNull CircleItem circleItem) {
        final SnsPopupWindow snsPopupWindow = viewHolder.snsPopupWindow;
        //判断是否已点赞
        String curUserFavortId = circleItem.getCurUserFavortId(UserInfoRepository.getUserName());
        if (!TextUtils.isEmpty(curUserFavortId)) {
            snsPopupWindow.getmActionItems().get(0).mTitle = "取消";
        } else {
            snsPopupWindow.getmActionItems().get(0).mTitle = "点赞";
        }
        snsPopupWindow.update();
        snsPopupWindow.setmItemClickListener(
            new PopupItemClickListener(
                viewHolder.getAdapterPosition() - HEAD_VIEW_SIZE, circleItem));
        //弹出popupwindow
        snsPopupWindow.showPopupWindow(view);
    }

    private void showDleteDialog(CircleItem circleItem) {
        final NiftyDialogBuilder builder = NiftyDialogBuilder.getInstance(context);
        builder.withTitle("提示").withMessage("确定删除吗").withButton1Text("取消")
            .withButton2Text("删除")
            .setButton1Click(v1 -> builder.dismiss())
            .setButton2Click(v1 -> {
                builder.dismiss();
                presenter.deleteCircle(circleItem.id);
            }).show();
    }

    /**
     * 长按复制
     */
    private void longClickCopy(@NonNull ContentEntity commentItem) {
        CommentDialog dialog = new CommentDialog(
            context, UserInfoRepository.getUserName().equals(commentItem.getPHC_CommentUserid()));
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        dialog.show();
        dialog.setCopyClickListener(() -> {
            ClipboardManager clipboard =
                (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setPrimaryClip(ClipData.newPlainText(null, commentItem.getPHC_Content()));
        });
    }

    /**
     * 回复评论
     */
    private void replyComment(@NonNull CircleViewHolder viewHolder, @NonNull CircleItem circleItem,
                              ContentEntity commentItem, int commentPosition) {
        int circlePosition = viewHolder.getAdapterPosition() - HEAD_VIEW_SIZE;
        presenter.replyComment(circlePosition, circleItem, commentItem, commentPosition);
    }

    /**
     * 复制或者删除自己的评论
     */
    private void handleMyComment(@NonNull CircleViewHolder viewHolder, @NonNull ContentEntity commentItem) {
        CommentDialog dialog = new CommentDialog(
            context, UserInfoRepository.getUserName().equals(commentItem.getPHC_CommentUserid()));

        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        dialog.show();
        dialog.setDeleteClickListener(() ->
            presenter.deleteComment(
                viewHolder.getAdapterPosition() - HEAD_VIEW_SIZE,
                commentItem.getPHC_CommentUserid(), commentItem.getPHC_Serno())
        );

    }

    /**
     * 处理点赞
     */
    private void handleFavort(@NonNull CircleViewHolder viewHolder, @NonNull List<ZambiaEntity> favortItems) {
        viewHolder.favortListAdapter.setSpanClickListener(position -> {
            Intent intent = new Intent(context, FriendDetailActivity.class);
            intent.putExtra(AppConfig.FRIEND_NAME,
                favortItems.get(position).PHC_CommentUserid);
            context.startActivity(intent);
        });
        viewHolder.favortListAdapter.setitems(DatasUtil.getFavortItems(favortItems));
        viewHolder.favortListAdapter.notifyDataSetChanged();
    }

    /***
     *  去播放视频
     *  @param isSilentPlay 是否静音播放
     *
     */
    private void toPlayer(ImageView imageView, String videoUrl, boolean isSilentPlay) {
        AnimationRect rect = AnimationRect.buildFromImageView(imageView);
        if (!StringUtils.isEmpty(videoUrl)) {
            Intent intent = LookUpVideoActivity.newIntent(context, rect, videoUrl, "circle");
            intent.putExtra("isSilent", isSilentPlay);
            context.startActivity(intent);
        } else {
            T.showShort(context, "播放出错");
        }
    }

    private void toPlayer(@NonNull CircleItem item, @NonNull VideoViewHolder viewHolder, boolean isSilentPlay) {
        AnimationRect rect = AnimationRect.buildFromImageView(viewHolder.videothumbnial);
        if (!StringUtils.isEmpty(item.videoUrl)) {
            Intent intent = LookUpVideoActivity
                .newIntent(context, rect, item.videoUrl, "circle");
            intent.putExtra("isSilent", isSilentPlay);
            context.startActivity(intent);
        } else {
            T.showShort(context, "播放出错");
        }
    }

    private void collect(CircleItem circleItem, String path, int mType) {
        if (mType == Content.MSG_TYPE_TEXT) {
            StoreManager.getInstance()
                .storeCircleText(circleItem.id, circleItem.userid, circleItem.username, path, circleItem.headUrl);
        } else {
            StoreManager.getInstance()
                .storeCircleImageVideo(circleItem.id, circleItem.userid, circleItem.username, path,
                    circleItem.headUrl, mType);
        }
    }

    private class PopupCollectClickListener implements CollectDialog.OnItemClickListener {

        private String path;
        private CircleViewHolder viewHolder;
        private int mType;
        private CircleItem item;

        private PopupCollectClickListener(CircleItem item, String resPath, CircleViewHolder viewHolder, int extraType) {
            this.path = resPath;
            this.mType = extraType;
            this.viewHolder = viewHolder;
            this.item = item;
        }

        @Override
        public void onItemClick(int position, int dataPosi) {
            switch (position) {
                case 0:
                    toPlayer(item, (VideoViewHolder) viewHolder, true);
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
        private CircleItem mCircleItem;

        private PopupItemClickListener(int circlePosition, CircleItem circleItem) {
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

    /**
     * 获取最新评论的数量
     */
    @SuppressLint({"SetTextI18n", "CheckResult"})
    private void getNewCommentCount(ItemHeadCircleBinding binding, int newCount) {
        Http.getNewCommentCount("getnewCommentNum", UserInfoRepository.getUserName())
            .compose(RxSchedulers.io_main())
            .subscribe(
                stringRetObjectResponse -> {
                    String ret = stringRetObjectResponse.retData;
                    if (TextUtils.isEmpty(ret)) {
                        return;
                    }
                    int count = 0;
                    String userID = null;
                    try {
                        String[] retArray = ret.split("#");
                        count = Integer.parseInt(retArray[0]);
                        userID = retArray[1];
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (!TextUtils.isEmpty(userID) && count != 0 && count != newCount) {
                        binding.llCircleNewMessage.setVisibility(View.VISIBLE);
                        binding.mCircleNewMessage.setText(count + "条新消息");
                        Glide.with(context)
                            .load(String.format(Route.obtainAvater, userID))
                            .into(binding.imgNewCircleAvatar);
                    }

                },
                throwable -> Log.e(HeaderViewHolder.NEW_COMMENT_COUNT, throwable.getMessage()));
    }


    public void setSaveAvatarTime() {
        SPSaveHelper.setValue(HeaderViewHolder.MY_AVATAR_TIME, StringUtils.formatDateTime(new Date()));
    }

    public String getSaveAvatarTime() {
        if (HeaderViewHolder.DEF_VALUE.equals(SPSaveHelper.getStringValue(HeaderViewHolder.MY_AVATAR_TIME, HeaderViewHolder.DEF_VALUE))) {
            setSaveAvatarTime();
        }
        return SPSaveHelper.getStringValue(HeaderViewHolder.MY_AVATAR_TIME, HeaderViewHolder.DEF_VALUE);
    }

    public interface OnItemClickListener {
        void onIntemClick(View view, int position);
    }
}
