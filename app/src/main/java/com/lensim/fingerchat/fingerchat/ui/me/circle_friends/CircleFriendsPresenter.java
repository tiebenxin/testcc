package com.lensim.fingerchat.fingerchat.ui.me.circle_friends;


import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;

import com.lensim.fingerchat.commons.utils.L;
import com.lensim.fingerchat.commons.utils.StringUtils;
import com.lensim.fingerchat.data.Http;
import com.lensim.fingerchat.data.RxSchedulers;
import com.lensim.fingerchat.data.login.UserInfoRepository;
import com.lensim.fingerchat.data.me.CircleItem;
import com.lensim.fingerchat.data.me.ZambiaEntity;
import com.lensim.fingerchat.data.me.circle_friend.CommentConfig;
import com.lensim.fingerchat.data.me.circle_friend.ContentEntity;
import com.lensim.fingerchat.data.me.circle_friend.FriendCircleEntity;
import com.lensim.fingerchat.fingerchat.ui.me.utils.DatasUtil;

import java.io.File;
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
            mCompositeSubscription
                .add(Http.UpdateFriendCircle("getfriendphotopg", UserInfoRepository.getUserName())
                    .compose(RxSchedulers.io_main())
                    .subscribe(
                        friendCircleEntities -> {
                            entities = friendCircleEntities;
                            circleItems = DatasUtil.createCircleDatas(entities);
                            getMvpView().updateFriendCircle(type, circleItems);
                        },
                        throwable -> {
                            Log.e("updateFriendCircle", throwable.getMessage());
//                        getMvpView().updateFriendCircle(type, null);
                        }
                    ));
        }
    }


    /**
     * 发起网络请求——上拉加载
     */
    private void loadMoreCircleData(final int type) {
        mCompositeSubscription
            .add(Http
                .loadMoreCircleData((entities.size() + 4) / 5, UserInfoRepository.getUserName())
                .compose(RxSchedulers.io_main())
                .subscribe(
                    friendCircleEntities -> {
                        entities.addAll(friendCircleEntities);
                        circleItems = DatasUtil.createCircleDatas(entities);
                        getMvpView().updateFriendCircle(type, circleItems);
                    },
                    throwable -> {
//                        getMvpView().updateFriendCircle(type, null);
                    }
                ));
    }

    /**
     * 发起网络请求——删除动态
     */
    @Override
    public void deleteCircle(final String circleId) {
        mCompositeSubscription
            .add(Http.deleteCircle(circleId, UserInfoRepository.getUserName())
                .compose(RxSchedulers.io_main())
                .subscribe(
                    stringRetObjectResponse -> {
                        if (1 == stringRetObjectResponse.retCode) {
                            updateItems(circleId);
                            updateDeleteCircle(circleId);
                        }

                    },
                    throwable -> {
                    }
                ));
    }


    /**
     * 删除动态
     */
    private void updateDeleteCircle(String circleId) {
        if (circleItems == null) {
            return;
        }
        for (int i = 0; i < circleItems.size(); i++) {
            if (circleId.equals(circleItems.get(i).id)) {
                circleItems.remove(i);
                getMvpView().updateFriendCircle(CircleFriendsActivity.TYPE_OTHERREFRESH, circleItems);
                return;
            }
        }
    }


    private void updateItems(@NonNull String circleId) {
        FriendCircleEntity e = null;
        for (FriendCircleEntity entity : entities) {
            if (entity.getPHO_Serno().equals(circleId)) {
                e = entity;
                break;
            }
        }
        if (e != null) {
            entities.remove(e);
        }
    }

    /**
     * 发起网络请求——点赞
     */
    @Override
    public void addFavort(CircleItem circleItem, final int circlePosition) {
        mCompositeSubscription
            .add(Http.likeCircleFriends(circleItem)
                .compose(RxSchedulers.io_main())
                .subscribe(
                    string -> {
                        if ("OK".equals(string.string())) {
                            ZambiaEntity item = DatasUtil.createCurUserFavortItem();
                            updateAddFavorite(circlePosition, item);
                        }
                    },
                    throwable -> Log.e("throwable", throwable.getMessage())
                ));

    }


    /**
     * 点赞
     */
    private void updateAddFavorite(int circlePosition, ZambiaEntity addItem) {
        if (addItem != null && circleItems != null) {
            CircleItem item = circleItems.get(circlePosition);
            if (item.favorters == null) {
                item.favorters = new ArrayList<>();
            }
            item.favorters.add(addItem);
            getMvpView().updateFriendCircle(CircleFriendsActivity.TYPE_OTHERREFRESH, circleItems);
        }
    }

    /**
     * 发起网络请求——取消点赞
     */
    @Override
    public void deleteFavort(final CircleItem item, final int circlePosition) {
        mCompositeSubscription
            .add(Http.cancelLike(UserInfoRepository.getUserName().toLowerCase(), item.id, item.userid)
                .compose(RxSchedulers.io_main())
                .subscribe(
                    string -> {
                        if ("OK".equals(string.string())) {
                            updateDeleteFavort(circlePosition, item
                                .getCurUserFavortId(UserInfoRepository.getUserName()));
                        }
                    },
                    throwable -> Log.e("throwable", throwable.getMessage())
                ));
    }
    
    /**
     * 取消赞
     */
    private void updateDeleteFavort(int circlePosition, String favortId) {
        CircleItem item = circleItems.get(circlePosition);
        List<ZambiaEntity> lists = item.favorters;
        for (int i = 0; i < lists.size(); i++) {
            if (favortId.equals(lists.get(i).PHC_CommentUserid)) {
                lists.remove(i);
                getMvpView().updateFriendCircle(CircleFriendsActivity.TYPE_OTHERREFRESH, circleItems);
                return;
            }
        }
    }

    /**
     * 发起网络请求——添加评论
     */
    @Override
    public void addComment(String content) {

        ContentEntity entity = new ContentEntity();
        entity.setPHC_CommentUserid(UserInfoRepository.getUserName());
        entity.setPHC_CommentUsername(UserInfoRepository.getUsernick());
        entity.setPHC_Content(content);
        entity.setPHC_SecondUserid(mCommentConfig.replyUserid);
        entity.setPHC_SecondUsername(mCommentConfig.replyUsername);

        if (mCommentConfig.commentType == CommentConfig.Type.PUBLIC) {
            comment(mCommentConfig, entity);
        } else {
            reComment(mCommentConfig, entity);
        }
    }

    private void comment(final CommentConfig config, ContentEntity entity) {
        mCompositeSubscription
            .add(Http.comment(UserInfoRepository.getUserName().toLowerCase(),
                UserInfoRepository.getUsernick(), config.id, config.replyUserid,
                config.replyUsername, entity.getPHC_Content())
                .compose(RxSchedulers.io_main())
                .subscribe(
                    stringRetObjectResponse ->
                        updateAddComment(config, stringRetObjectResponse.retMsg, entity),
                    throwable -> {
                    }
                ));
    }


    private void reComment(final CommentConfig config, ContentEntity entity) {
        mCompositeSubscription
            .add(Http.reComment(config.id, config.createdid,
                config.createdName, entity.getPHC_Content(),
                config.replyUserid, config.replyUsername)
                .compose(RxSchedulers.io_main())
                .subscribe(
                    stringRetObjectResponse ->
                        updateAddComment(config, stringRetObjectResponse.retMsg, entity),
                    throwable -> {
                    }
                ));
    }

    /**
     * 添加评论
     */
    private void updateAddComment(final CommentConfig config, String msg, ContentEntity entity) {

        if (config.commentType == CommentConfig.Type.PUBLIC) {
            entity.setPHC_SecondUserid("");
            entity.setPHC_SecondUsername("");
        }

        entity.setPHC_Serno(msg);
        CircleItem item = circleItems.get(config.circlePosition);
        if (item == null) return;
        if (item.comments == null) {
            item.comments = new ArrayList<>();
        }
        item.comments.add(entity);
        getMvpView().updateFriendCircle(CircleFriendsActivity.TYPE_OTHERREFRESH, circleItems);

    }


    /**
     * 发起网络请求——删除评论
     */
    @Override
    public void deleteComment(final int mCirclePosition, String userid, String PHC_Serno) {
        mCompositeSubscription
            .add(Http.deleteComment(PHC_Serno, userid)
                .compose(RxSchedulers.io_main())
                .subscribe(
                    stringRetObjectResponse -> {
                        if (1 == stringRetObjectResponse.retCode) {
                            updateDeleteComment(mCirclePosition, PHC_Serno);
                        }
                    },
                    throwable -> {
//                    T.showShort(MyApplication.getInstance().getApplication(), "删除失败");
                    }
                ));
    }


    /**
     * 删除评论
     */
    private void updateDeleteComment(int circlePosition, String commentId) {

        CircleItem item = circleItems.get(circlePosition);
        List<ContentEntity> lists = item.comments;
        for (int i = 0; i < lists.size(); i++) {
            if (commentId.equals(lists.get(i).getPHC_Serno())) {
                lists.remove(i);
                getMvpView().updateFriendCircle(CircleFriendsActivity.TYPE_OTHERREFRESH, circleItems);
                return;
            }
        }
    }

    /**
     * 发起网络请求——更新朋友圈Theme
     */
    @Override
    public void uploadThemeImg(@NonNull String imagePath) {
//        MyApplication.getInstance()
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
                ));
    }

    /**
     * 回复评论
     */
    @Override
    public void replyComment(int circlePosition, CircleItem circleItem, ContentEntity commentItem,
                             int commentPosition) {
        CommentConfig config = new CommentConfig();
        config.id = circleItem.id;
        config.circlePosition = circlePosition;
        config.createdid = circleItem.userid;
        config.createdName = circleItem.username;
        config.commentPosition = commentPosition;
        config.commentType = CommentConfig.Type.REPLY;
        config.replyUserid = commentItem.getPHC_CommentUserid();
        config.replyUsername =
            StringUtils.isEmpty(commentItem.getPHC_CommentUsername())
                ? commentItem.getPHC_CommentUserid() : commentItem.getPHC_CommentUsername();

        showEditTextBody(config);
    }


    /**
     * 编写评论
     */
    @Override
    public void writeCommen(CircleItem circleItem, int circlePosition) {
        CommentConfig config = new CommentConfig();
        config.id = circleItem.id;
        config.circlePosition = circlePosition;
        config.commentType = CommentConfig.Type.PUBLIC;
        config.replyUserid = circleItem.userid;
        config.replyUsername = StringUtils.isEmpty(circleItem.username) ?
            circleItem.userid : circleItem.username;

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
