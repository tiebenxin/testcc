package com.lensim.fingerchat.fingerchat.ui.me.circle_friends;



import com.lensim.fingerchat.commons.mvp.presenter.RxMvpPresenter;
import com.lensim.fingerchat.commons.mvp.view.ProcessMvpView;
import com.lensim.fingerchat.data.me.circle_friend.CommentConfig;
import com.lensim.fingerchat.fingerchat.model.bean.CommentBean;
import com.lensim.fingerchat.fingerchat.model.bean.PhotoBean;
import com.lensim.fingerchat.fingerchat.ui.me.circle_friends.CircleFriendsPresenter.DownloadListener;
import java.util.List;

/**
 * date on 2018/2/2
 * author ll147996
 * describe
 */

public interface CircleFirendsContract {

    interface View extends ProcessMvpView {

        void updateFriendCircle(int type, List<PhotoBean> items);

        void updateEditTextBodyVisible(int visibility, CommentConfig commentConfig);

        void updateEditTextBodyVisible(int visibility);

        //发朋友圈--文字信息
        void sendText();

        //发朋友圈--图片信息
        void sendImage();

        //发朋友圈--小视频
        void sendVideo();

        void updateBackgroundImg();
    }


    abstract class Presenter extends RxMvpPresenter<CircleFirendsContract.View> {

        public abstract void setHeaderItem();

        public abstract CommentConfig getCommentConfig();
        /**
         * 更新朋友圈信息
         */
        public abstract void updateFriendCircle(int type);


        /**
         * 删除动态
         */
        public abstract void deleteCircle(final String circleId);

        /**
         * 点赞
         */
        public abstract void addFavort(PhotoBean circleItem, int circlePosition);

        /**
         * 取消点赞
         */
        public abstract void deleteFavort(PhotoBean circleItem, int circlePosition);

        /**
         * 增加评论
         */
//        public abstract void addComment(CommentConfig config, ContentEntity entity);
        public abstract void addComment(String content);

        /**
         * 删除评论
         */
        public abstract void deleteComment(final int mCirclePosition, String userid, String PHC_Serno);

        /**
         * 上传Theme图片
         */
        public abstract void uploadThemeImg(String imagePath);

        /**
         * 下载视频
         */
        public abstract void downloadVideoFile(String fileUrl, DownloadListener listener);

        public abstract void replyComment(int circlePosition, PhotoBean circleItem,
                                          CommentBean commentItem, int commentPosition);

        public abstract void writeCommen(PhotoBean circleItem, int circlePosition);
    }

}
