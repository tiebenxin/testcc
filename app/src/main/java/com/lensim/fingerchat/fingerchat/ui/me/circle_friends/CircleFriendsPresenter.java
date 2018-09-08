package com.lensim.fingerchat.fingerchat.ui.me.circle_friends;


import android.support.annotation.NonNull;
import android.view.View;
import com.lens.chatmodel.bean.body.ImageUploadEntity;
import com.lens.chatmodel.net.HttpUtils;
import com.lens.core.componet.log.DLog;
import com.lensim.fingerchat.commons.app.AppConfig;
import com.lensim.fingerchat.commons.base.BaseResponse;
import com.lensim.fingerchat.commons.global.CommonEnum;
import com.lensim.fingerchat.commons.http.FXRxSubscriberHelper;
import com.lensim.fingerchat.commons.utils.CyptoUtils;
import com.lensim.fingerchat.commons.utils.StringUtils;
import com.lensim.fingerchat.data.Http;
import com.lensim.fingerchat.data.RxSchedulers;
import com.lensim.fingerchat.data.help_class.IUploadListener;
import com.lensim.fingerchat.data.login.UserInfoRepository;
import com.lensim.fingerchat.data.me.CircleItem;
import com.lensim.fingerchat.data.me.circle_friend.CommentConfig;
import com.lensim.fingerchat.data.me.circle_friend.FriendCircleEntity;
import com.lensim.fingerchat.data.repository.SPSaveHelper;
import com.lensim.fingerchat.fingerchat.api.CirclesFriendsApi;
import com.lensim.fingerchat.fingerchat.model.bean.CommentBean;
import com.lensim.fingerchat.fingerchat.model.bean.CommentResponse;
import com.lensim.fingerchat.fingerchat.model.bean.PhotoBean;
import com.lensim.fingerchat.fingerchat.model.bean.ThumbsBean;
import com.lensim.fingerchat.fingerchat.model.bean.UpdateBgImgResponse;
import com.lensim.fingerchat.fingerchat.model.requestbody.CommentTalkRequestBody;
import com.lensim.fingerchat.fingerchat.model.requestbody.RevertCommentRequestBody;
import com.lensim.fingerchat.fingerchat.model.requestbody.ThumbsUpRequestBody;
import com.lensim.fingerchat.fingerchat.model.result.GetPhotoResult;
import java.util.ArrayList;
import java.util.List;


/**
 * date on 2017/12/21
 * author ll147996
 * describe
 */


public class CircleFriendsPresenter extends CircleFirendsContract.Presenter {

    private List<CircleItem> circleItems = new ArrayList<>();
    private List<FriendCircleEntity> entities;
    private CommentConfig mCommentConfig;

    private CirclesFriendsApi circlesFriendsApi;
    private List<PhotoBean> photoBeanList = new ArrayList<>();

    public CirclesFriendsApi getCirclesFriendsApi() {
        return circlesFriendsApi == null ? new CirclesFriendsApi() : circlesFriendsApi;
    }

    @Override
    public CommentConfig getCommentConfig() {
        return mCommentConfig;
    }

    @Override
    public void setHeaderItem() {

    }

    /**
     * 发起网络请求——更新朋友圈
     */
    @Override
    public void updateFriendCircle(final int type) {
        if (type == CircleFriendsActivity.TYPE_LOADMORE) {
            loadMoreCircleData(type);
        } else {
            getCirclesFriendsApi().getPhotos(UserInfoRepository.getUserName(), "0", new FXRxSubscriberHelper<GetPhotoResult>() {
                @Override
                public void _onNext(GetPhotoResult getPhotoResult) {
                    GetPhotoResult.Data data = getPhotoResult.getContent();
                    if (data != null) {
                        photoBeanList.clear();
                        photoBeanList.addAll(data.getFxNewPhotos());
                        getMvpView().updateFriendCircle(type, data.getFxNewPhotos());
                    }
                }
            });
        }
    }


    /**
     * 发起网络请求——上拉加载
     */
    private void loadMoreCircleData(final int type) {
        getCirclesFriendsApi().getPhotos(UserInfoRepository.getUserName(), photoBeanList.size() + "", new FXRxSubscriberHelper<GetPhotoResult>() {
            @Override
            public void _onNext(GetPhotoResult getPhotoResult) {
                GetPhotoResult.Data data = getPhotoResult.getContent();
                if (data != null) {
                    photoBeanList.addAll(data.getFxNewPhotos());
                    getMvpView().updateFriendCircle(type, photoBeanList);
                }
            }
        });

    }

    /**
     * 发起网络请求——删除动态
     */
    @Override
    public void deleteCircle(final String circleId) {
        getCirclesFriendsApi().deletePhoto(circleId, new FXRxSubscriberHelper<BaseResponse>() {
            @Override
            public void _onNext(BaseResponse baseResponse) {
                updateDeleteCircle(circleId);
            }
        });
    }


    /**
     * 删除动态
     */
    private void updateDeleteCircle(String circleId) {
        if (photoBeanList == null) {
            return;
        }
        for (int i = 0; i < photoBeanList.size(); i++) {
            if (circleId.equals(photoBeanList.get(i).getPhotoSerno())) {
                photoBeanList.remove(i);
                getMvpView().updateFriendCircle(CircleFriendsActivity.TYPE_OTHERREFRESH, photoBeanList);
                return;
            }
        }
    }


    private void updateItems(@NonNull String circleId) {
        PhotoBean e = null;
        for (PhotoBean photoBean : photoBeanList) {
            if (photoBean.getPhotoSerno().equals(circleId)) {
                e = photoBean;
                break;
            }
        }
        if (e != null) {
            photoBeanList.remove(e);
        }
    }

    /**
     * 发起网络请求——点赞
     */
    @Override
    public void addFavort(PhotoBean circleItem, final int circlePosition) {
        ThumbsUpRequestBody requestBody = new ThumbsUpRequestBody.Builder()
            .photoSerno(circleItem.getPhotoSerno())
            .photoUserId(circleItem.getPhotoCreator())
            .photoUserName(circleItem.getUserName())
            .thumbsUserId(UserInfoRepository.getUserId())
            .thumbsUserName(CyptoUtils.encrypt(UserInfoRepository.getUsernick())).build();
        getCirclesFriendsApi().thumbsUp(requestBody, new FXRxSubscriberHelper<BaseResponse>() {
            @Override
            public void _onNext(BaseResponse baseResponse) {
                ThumbsBean item = new ThumbsBean();
                item.setPhotoSerno(circleItem.getPhotoSerno());
                item.setPhotoUserId(circleItem.getPhotoCreator());
                item.setPhotoUserName(circleItem.getUserName());
//                item.setThumbsTime();
                item.setThumbsUserId(UserInfoRepository.getUserId());
                item.setThumbsUserName(UserInfoRepository.getUsernick());
                item.setThumbsSerno(circleItem.getPhotoSerno());
                updateAddFavorite(circlePosition, item);
            }
        });

    }


    /**
     * 点赞
     */
    private void updateAddFavorite(int circlePosition, ThumbsBean addItem) {
        if (addItem != null && photoBeanList != null) {
            PhotoBean item = photoBeanList.get(circlePosition);
            item.getThumbsUps().add(addItem);
            getMvpView().updateFriendCircle(CircleFriendsActivity.TYPE_OTHERREFRESH, photoBeanList);
        }
    }

    /**
     * 发起网络请求——取消点赞
     */
    @Override
    public void deleteFavort(final PhotoBean item, final int circlePosition) {
        getCirclesFriendsApi().cancelThumbsUp(item.getPhotoCreator(), item.getPhotoSerno(),
            UserInfoRepository.getUserId(),
            new FXRxSubscriberHelper<BaseResponse>() {
                @Override
                public void _onNext(BaseResponse baseResponse) {
                    if ("Ok".equals(baseResponse.getMessage())) {
                        updateDeleteFavort(circlePosition, item);
                    }

                }
            });

    }

    /**
     * 取消赞
     */
    private void updateDeleteFavort(int circlePosition, PhotoBean photoBean) {
        PhotoBean item = photoBeanList.get(circlePosition);
        List<ThumbsBean> lists = item.getThumbsUps();
        for (int i = 0; i < photoBean.getThumbsUps().size(); i++) {
            if (UserInfoRepository.getUserId().equals(lists.get(i).getThumbsUserId())) {
                lists.remove(i);
                item.setThumbsUps(lists);
                photoBeanList.add(item);
                getMvpView().updateFriendCircle(CircleFriendsActivity.TYPE_OTHERREFRESH, photoBeanList);
                return;
            }
        }
    }

    /**
     * 发起网络请求——添加评论
     */
    @Override
    public void addComment(String content) {

        if (mCommentConfig.commentType == CommentConfig.Type.PUBLIC) {
            CommentTalkRequestBody requestBody = new CommentTalkRequestBody();
            requestBody.setCommentUserId(UserInfoRepository.getUserName());
            requestBody.setCommentUserName(CyptoUtils.encrypt(UserInfoRepository.getUsernick()));
            requestBody.setContent(CyptoUtils.encrypt(content));
            requestBody.setCreatorUserId(mCommentConfig.replyUserid);
            requestBody.setCreatorUserName(mCommentConfig.replyUsername);
            requestBody.setPhotoSerno(photoBeanList.get(mCommentConfig.circlePosition).getPhotoSerno());
            comment(mCommentConfig,requestBody);
        } else {
            RevertCommentRequestBody revertCommentRequestBody = new RevertCommentRequestBody.Builder()
                .commentUserId(UserInfoRepository.getUserName())
                .commentUserId2(mCommentConfig.replyUserid)
                .commentUserName(CyptoUtils.encrypt(UserInfoRepository.getUsernick()))
                .commentUserName2(CyptoUtils.encrypt(mCommentConfig.replyUsername))
                .creatorUserId(mCommentConfig.replyUserid)
                .creatorUserName(mCommentConfig.replyUsername)
                .content(CyptoUtils.encrypt(content))
                .photoSerno(photoBeanList.get(mCommentConfig.circlePosition).getPhotoSerno())
                .build();

            reComment(mCommentConfig, revertCommentRequestBody);
        }

    }

    private void comment(final CommentConfig config, CommentTalkRequestBody entity) {
        getCirclesFriendsApi().commentTalk(entity,
            new FXRxSubscriberHelper<BaseResponse<CommentResponse>>() {
                @Override
                public void _onNext(BaseResponse<CommentResponse> baseResponse) {
                    if (null != baseResponse && "Ok".equals(baseResponse.getMessage())){
                        updateAddComment(config, baseResponse.getMessage(),baseResponse.getContent().getComment());
                    }
                }
            });
    }


    private void reComment(final CommentConfig config, RevertCommentRequestBody revertCommentRequestBody) {

        getCirclesFriendsApi().revertComment(revertCommentRequestBody,
            new FXRxSubscriberHelper<BaseResponse<CommentResponse>>() {
                @Override
                public void _onNext(BaseResponse<CommentResponse> baseResponse) {
                    if (null != baseResponse && "Ok".equals(baseResponse.getMessage())){
                        updateAddComment(config, baseResponse.getMessage(), baseResponse.getContent().getComment());
                    }
                }
            });
    }

    /**
     * 添加评论
     */
    private void updateAddComment(final CommentConfig config, String msg, CommentBean entity) {

        if (config.commentType == CommentConfig.Type.PUBLIC) {
            entity.setCreatorUserid("");
            entity.setCreatorUsername("");
        }
        //entity.setPHC_Serno(msg);
        PhotoBean item = photoBeanList.get(config.circlePosition);
        if (item == null) return;
        item.getComments().add(entity);
        getMvpView().updateFriendCircle(CircleFriendsActivity.TYPE_OTHERREFRESH, photoBeanList);

    }


    /**
     * 发起网络请求——删除评论
     */
    @Override
    public void deleteComment(final int mCirclePosition, String userid, String commentSerno) {
        getCirclesFriendsApi().deleteComment(commentSerno, new FXRxSubscriberHelper<BaseResponse>() {
            @Override
            public void _onNext(BaseResponse baseResponse) {
                if ("Ok".equals(baseResponse.getMessage())){
                    updateDeleteComment(mCirclePosition, commentSerno);
                }
            }
        });
    }


    /**
     * 删除评论
     */
    private void updateDeleteComment(int circlePosition, String commentId) {

        PhotoBean item = photoBeanList.get(circlePosition);
        List<CommentBean> lists = item.getComments();
        for (int i = 0; i < lists.size(); i++) {
            if (commentId.equals(lists.get(i).getCommentSerno())) {
                lists.remove(i);
                getMvpView().updateFriendCircle(CircleFriendsActivity.TYPE_OTHERREFRESH, photoBeanList);
                return;
            }
        }
    }

    /**
     * 发起网络请求——更新朋友圈Theme
     */
    @Override
    public void uploadThemeImg(@NonNull String imagePath) {
/*//        MyApplication.getInstance()
//            .saveString(LensImUtil.getUserName() + AppConfig.CIRCLE_THEME_PATH, imagePath);
        compressionPicture();
        File file = new File(imagePath);
        L.i("文件名：" + file.getName());
        mCompositeSubscription
            .add(Http.setAvatar(file, UserInfoRepository.getUserName())
                .compose(RxSchedulers.io_main())
                .subscribe(
                    string -> {
//                    showToast("设置成功");
//                    mAdapter.setSaveAvatarTime();
//                    mAdapter.notifyItemChanged(0);
                    },
                    throwable -> {
//                    showToast("上传失败");
//                    MyApplication.getInstance()
//                        .saveString(LensImUtil.getUserName() + AppConfig.CIRCLE_THEME_PATH, "");
                    }
                ));*/

        HttpUtils.getInstance()
            .uploadFileProgress(imagePath,CommonEnum.EUploadFileType.JPG,
                new IUploadListener() {
                    @Override
                    public void onSuccess(Object result) {

                        if (result != null && result instanceof ImageUploadEntity) {
                            ImageUploadEntity entity = (ImageUploadEntity) result;
                            getCirclesFriendsApi().updateBackgroundImage(UserInfoRepository.getUserId(),entity.getOriginalUrl(),
                                new FXRxSubscriberHelper<BaseResponse<UpdateBgImgResponse>>() {
                                    @Override
                                    public void _onNext(BaseResponse<UpdateBgImgResponse> baseResponse) {
                                        SPSaveHelper.setValue(UserInfoRepository.getUserName() + AppConfig.CIRCLE_THEME_PATH,baseResponse.getContent().getRecord().getBgImageUrl());
                                    mView.updateBackgroundImg();
                                    }
                                });
                        }
                    }

                    @Override
                    public void onFailed() {

                    }

                    @Override
                    public void onProgress(int progress) {
                        DLog.d("上传进度" + progress);
                    }

                });
       /* getCirclesFriendsApi().updateBackgroundImage(imagePath,
                new FXRxSubscriberHelper<BaseResponse>() {
                    @Override
                    public void _onNext(BaseResponse baseResponse) {
                        //SPSaveHelper.setValue(UserInfoRepository.getUserName() + AppConfig.CIRCLE_THEME_PATH,"");
                    }
                });*/

    }

    /**
     * 回复评论
     */
    @Override
    public void replyComment(int circlePosition, PhotoBean circleItem, CommentBean commentItem,
                             int commentPosition) {
        CommentConfig config = new CommentConfig();
        config.id = circleItem.getPhotoId() + "";
        config.circlePosition = circlePosition;
        config.createdid = circleItem.getPhotoCreator();
        config.createdName = circleItem.getUserName();
        config.commentPosition = commentPosition;
        config.commentType = CommentConfig.Type.REPLY;
        config.replyUserid = commentItem.getCommentUserid();
        config.replyUsername =
            StringUtils.isEmpty(commentItem.getCommentUsername())
                ? commentItem.getCommentUserid() : commentItem.getCommentUsername();

        showEditTextBody(config);
    }


    /**
     * 编写评论
     */
    @Override
    public void writeCommen(PhotoBean circleItem, int circlePosition) {
        CommentConfig config = new CommentConfig();
        config.id = circleItem.getPhotoId() + "";
        config.circlePosition = circlePosition;
        config.commentType = CommentConfig.Type.PUBLIC;
        config.replyUserid = circleItem.getPhotoId() + "";
        config.replyUsername = StringUtils.isEmpty(circleItem.getUserName()) ?
            circleItem.getPhotoCreator() : circleItem.getUserName();

        showEditTextBody(config);
    }

    private void showEditTextBody(CommentConfig commentConfig) {
        mCommentConfig = commentConfig;
        getMvpView().updateEditTextBodyVisible(View.VISIBLE, commentConfig);
    }

    /**
     * 下载视频
     */
    @Override
    public void downloadVideoFile(@NonNull String fileUrl, @NonNull DownloadListener listener) {
        mCompositeSubscription
            .add(Http.downloadVideoFile(fileUrl)
                .compose(RxSchedulers.io_main())
                .subscribe(
                    responseBody -> listener.loadSuccess(responseBody.bytes()),
                    throwable -> listener.loadFailure()
                ));

    }

    public interface DownloadListener {
        void loadSuccess(byte[] bytes);

        void loadFailure();
    }


    /**
     * 压缩图片
     */
    private void compressionPicture() {

    }
}
