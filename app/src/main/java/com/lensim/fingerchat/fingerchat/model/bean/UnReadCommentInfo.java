package com.lensim.fingerchat.fingerchat.model.bean;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.ArrayList;
import java.util.List;

/**
 * @time 2017/11/29 8:59
 * @class describe
 */

public class UnReadCommentInfo implements Parcelable{

    private List<FxNewPhotosBean> fxNewPhotos;
    private List<UnReadCommentBean> unReadComment;
    private List<UnReadThumbsBean> unReadThumbs;

    protected UnReadCommentInfo(Parcel in) {
        this.fxNewPhotos = new ArrayList<FxNewPhotosBean>();
        this.unReadComment = new ArrayList<UnReadCommentBean>();
        this.unReadThumbs = new ArrayList<UnReadThumbsBean>();
        in.readList(this.fxNewPhotos,FxNewPhotosBean.class.getClassLoader());
        in.readList(this.unReadComment,UnReadThumbsBean.class.getClassLoader());
        in.readList(this.unReadThumbs,UnReadThumbsBean.class.getClassLoader());
    }

    public static final Creator<UnReadCommentInfo> CREATOR = new Creator<UnReadCommentInfo>() {
        @Override
        public UnReadCommentInfo createFromParcel(Parcel in) {
            return new UnReadCommentInfo(in);
        }

        @Override
        public UnReadCommentInfo[] newArray(int size) {
            return new UnReadCommentInfo[size];
        }
    };

    public List<FxNewPhotosBean> getFxNewPhotos() {
        return fxNewPhotos;
    }

    public void setFxNewPhotos(List<FxNewPhotosBean> fxNewPhotos) {
        this.fxNewPhotos = fxNewPhotos;
    }

    public List<UnReadCommentBean> getUnReadComment() {
        return unReadComment;
    }

    public void setUnReadComment(List<UnReadCommentBean> unReadComment) {
        this.unReadComment = unReadComment;
    }

    public List<UnReadThumbsBean> getUnReadThumbs() {
        return unReadThumbs;
    }

    public void setUnReadThumbs(List<UnReadThumbsBean> unReadThumbs) {
        this.unReadThumbs = unReadThumbs;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeList(this.fxNewPhotos);
        parcel.writeList(this.unReadComment);
        parcel.writeList(this.unReadThumbs);
    }

    public static class FxNewPhotosBean implements Parcelable{

        /**
         * photoId : 1411
         * photoSerno : 0a6d81c0121440aba619494b9332007d
         * photoCreator : zze
         * photoFilenames : 1532420008850.jpg,
         * photoFileNum : 2
         * photoUrl : http://mobile.fingerchat.cn:8686/group1/M00/00/3E/CgMJjFtW45mAKgp2AAE-2Jiwhqw214.jpg,http://mobile.fingerchat.cn:8686/group1/M00/00/3E/CgMJjFtW45qAQ4ZzAArZf58tgnc434.jpg
         * photoContent : 呀哈哈
         * imageWidth : 0
         * imageHeight : 0
         * imageSize : 0
         * userName : 轻松王
         * userImage : http://mobile.fingerchat.cn:8686/group2/M00/00/FE/CgMJklqnbpqAS6wmAAEtRqD1vk4324.png
         * createDatetime : 1532421019000
         * thumbsUps : [{"tId":1061,"thumbsSerno":"04c8c52341444647a544f1e244c11135","photoSerno":"0a6d81c0121440aba619494b9332007d","photoUserId":"zze","photoUserName":"轻松王","thumbsUserId":"wshh0007","thumbsUserName":"王松清","thumbsTime":1532421445000}]
         * comments : [{"commentId":1881,"commentSerno":"4a4b7c04586943c2b1ce787bff6e4f50","photoSerno":"0a6d81c0121440aba619494b9332007d","creatorUserid":"1411","creatorUsername":"轻松王","commentUserid":"wshh0007","commentUsername":"王松清","commentContent":"[偷笑][偷笑]","commentTime":1532421454000,"commentUserid2":"","commentUsername2":""},{"commentId":1891,"commentSerno":"c54a06f879584627bdae86e0ad88d9e3","photoSerno":"0a6d81c0121440aba619494b9332007d","creatorUserid":"wshh0007","creatorUsername":"王松清","commentUserid":"zze","commentUsername":"轻松王","commentContent":"[撇嘴][撇嘴]","commentTime":1532421471000,"commentUserid2":"wshh0007","commentUsername2":"王松清"}]
         */

        private int photoId;
        private String photoSerno;
        private String photoCreator;
        private String photoFilenames;
        private int photoFileNum;
        private String photoUrl;
        private String photoContent;
        private int imageWidth;
        private int imageHeight;
        private int imageSize;
        private String userName;
        private String userImage;
        private long createDatetime;
        private List<ThumbsUpsBean> thumbsUps;
        private List<CommentsBean> comments;

        protected FxNewPhotosBean(Parcel in) {
            photoId = in.readInt();
            photoSerno = in.readString();
            photoCreator = in.readString();
            photoFilenames = in.readString();
            photoFileNum = in.readInt();
            photoUrl = in.readString();
            photoContent = in.readString();
            imageWidth = in.readInt();
            imageHeight = in.readInt();
            imageSize = in.readInt();
            userName = in.readString();
            userImage = in.readString();
            createDatetime = in.readLong();
        }

        public static final Creator<FxNewPhotosBean> CREATOR = new Creator<FxNewPhotosBean>() {
            @Override
            public FxNewPhotosBean createFromParcel(Parcel in) {
                return new FxNewPhotosBean(in);
            }

            @Override
            public FxNewPhotosBean[] newArray(int size) {
                return new FxNewPhotosBean[size];
            }
        };

        public int getPhotoId() {
            return photoId;
        }

        public void setPhotoId(int photoId) {
            this.photoId = photoId;
        }

        public String getPhotoSerno() {
            return photoSerno;
        }

        public void setPhotoSerno(String photoSerno) {
            this.photoSerno = photoSerno;
        }

        public String getPhotoCreator() {
            return photoCreator;
        }

        public void setPhotoCreator(String photoCreator) {
            this.photoCreator = photoCreator;
        }

        public String getPhotoFilenames() {
            return photoFilenames;
        }

        public void setPhotoFilenames(String photoFilenames) {
            this.photoFilenames = photoFilenames;
        }

        public int getPhotoFileNum() {
            return photoFileNum;
        }

        public void setPhotoFileNum(int photoFileNum) {
            this.photoFileNum = photoFileNum;
        }

        public String getPhotoUrl() {
            return photoUrl;
        }

        public void setPhotoUrl(String photoUrl) {
            this.photoUrl = photoUrl;
        }

        public String getPhotoContent() {
            return photoContent;
        }

        public void setPhotoContent(String photoContent) {
            this.photoContent = photoContent;
        }

        public int getImageWidth() {
            return imageWidth;
        }

        public void setImageWidth(int imageWidth) {
            this.imageWidth = imageWidth;
        }

        public int getImageHeight() {
            return imageHeight;
        }

        public void setImageHeight(int imageHeight) {
            this.imageHeight = imageHeight;
        }

        public int getImageSize() {
            return imageSize;
        }

        public void setImageSize(int imageSize) {
            this.imageSize = imageSize;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getUserImage() {
            return userImage;
        }

        public void setUserImage(String userImage) {
            this.userImage = userImage;
        }

        public long getCreateDatetime() {
            return createDatetime;
        }

        public void setCreateDatetime(long createDatetime) {
            this.createDatetime = createDatetime;
        }

        public List<ThumbsUpsBean> getThumbsUps() {
            return thumbsUps;
        }

        public void setThumbsUps(List<ThumbsUpsBean> thumbsUps) {
            this.thumbsUps = thumbsUps;
        }

        public List<CommentsBean> getComments() {
            return comments;
        }

        public void setComments(List<CommentsBean> comments) {
            this.comments = comments;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeInt(photoId);
            parcel.writeString(photoSerno);
            parcel.writeString(photoCreator);
            parcel.writeString(photoFilenames);
            parcel.writeInt(photoFileNum);
            parcel.writeString(photoUrl);
            parcel.writeString(photoContent);
            parcel.writeInt(imageWidth);
            parcel.writeInt(imageHeight);
            parcel.writeInt(imageSize);
            parcel.writeString(userName);
            parcel.writeString(userImage);
            parcel.writeLong(createDatetime);
        }

        public static class ThumbsUpsBean implements Parcelable{

            /**
             * tId : 1061
             * thumbsSerno : 04c8c52341444647a544f1e244c11135
             * photoSerno : 0a6d81c0121440aba619494b9332007d
             * photoUserId : zze
             * photoUserName : 轻松王
             * thumbsUserId : wshh0007
             * thumbsUserName : 王松清
             * thumbsTime : 1532421445000
             */

            private int tId;
            private String thumbsSerno;
            private String photoSerno;
            private String photoUserId;
            private String photoUserName;
            private String thumbsUserId;
            private String thumbsUserName;
            private long thumbsTime;

            protected ThumbsUpsBean(Parcel in) {
                tId = in.readInt();
                thumbsSerno = in.readString();
                photoSerno = in.readString();
                photoUserId = in.readString();
                photoUserName = in.readString();
                thumbsUserId = in.readString();
                thumbsUserName = in.readString();
                thumbsTime = in.readLong();
            }

            public static final Creator<ThumbsUpsBean> CREATOR = new Creator<ThumbsUpsBean>() {
                @Override
                public ThumbsUpsBean createFromParcel(Parcel in) {
                    return new ThumbsUpsBean(in);
                }

                @Override
                public ThumbsUpsBean[] newArray(int size) {
                    return new ThumbsUpsBean[size];
                }
            };

            public int getTId() {
                return tId;
            }

            public void setTId(int tId) {
                this.tId = tId;
            }

            public String getThumbsSerno() {
                return thumbsSerno;
            }

            public void setThumbsSerno(String thumbsSerno) {
                this.thumbsSerno = thumbsSerno;
            }

            public String getPhotoSerno() {
                return photoSerno;
            }

            public void setPhotoSerno(String photoSerno) {
                this.photoSerno = photoSerno;
            }

            public String getPhotoUserId() {
                return photoUserId;
            }

            public void setPhotoUserId(String photoUserId) {
                this.photoUserId = photoUserId;
            }

            public String getPhotoUserName() {
                return photoUserName;
            }

            public void setPhotoUserName(String photoUserName) {
                this.photoUserName = photoUserName;
            }

            public String getThumbsUserId() {
                return thumbsUserId;
            }

            public void setThumbsUserId(String thumbsUserId) {
                this.thumbsUserId = thumbsUserId;
            }

            public String getThumbsUserName() {
                return thumbsUserName;
            }

            public void setThumbsUserName(String thumbsUserName) {
                this.thumbsUserName = thumbsUserName;
            }

            public long getThumbsTime() {
                return thumbsTime;
            }

            public void setThumbsTime(long thumbsTime) {
                this.thumbsTime = thumbsTime;
            }

            @Override
            public int describeContents() {
                return 0;
            }

            @Override
            public void writeToParcel(Parcel parcel, int i) {
                parcel.writeInt(tId);
                parcel.writeString(thumbsSerno);
                parcel.writeString(photoSerno);
                parcel.writeString(photoUserId);
                parcel.writeString(photoUserName);
                parcel.writeString(thumbsUserId);
                parcel.writeString(thumbsUserName);
                parcel.writeLong(thumbsTime);
            }
        }

        public static class CommentsBean implements Parcelable{

            /**
             * commentId : 1881
             * commentSerno : 4a4b7c04586943c2b1ce787bff6e4f50
             * photoSerno : 0a6d81c0121440aba619494b9332007d
             * creatorUserid : 1411
             * creatorUsername : 轻松王
             * commentUserid : wshh0007
             * commentUsername : 王松清
             * commentContent : [偷笑][偷笑]
             * commentTime : 1532421454000
             * commentUserid2 :
             * commentUsername2 :
             */

            private int commentId;
            private String commentSerno;
            private String photoSerno;
            private String creatorUserid;
            private String creatorUsername;
            private String commentUserid;
            private String commentUsername;
            private String commentContent;
            private long commentTime;
            private String commentUserid2;
            private String commentUsername2;

            protected CommentsBean(Parcel in) {
                commentId = in.readInt();
                commentSerno = in.readString();
                photoSerno = in.readString();
                creatorUserid = in.readString();
                creatorUsername = in.readString();
                commentUserid = in.readString();
                commentUsername = in.readString();
                commentContent = in.readString();
                commentTime = in.readLong();
                commentUserid2 = in.readString();
                commentUsername2 = in.readString();
            }

            public static final Creator<CommentsBean> CREATOR = new Creator<CommentsBean>() {
                @Override
                public CommentsBean createFromParcel(Parcel in) {
                    return new CommentsBean(in);
                }

                @Override
                public CommentsBean[] newArray(int size) {
                    return new CommentsBean[size];
                }
            };

            public int getCommentId() {
                return commentId;
            }

            public void setCommentId(int commentId) {
                this.commentId = commentId;
            }

            public String getCommentSerno() {
                return commentSerno;
            }

            public void setCommentSerno(String commentSerno) {
                this.commentSerno = commentSerno;
            }

            public String getPhotoSerno() {
                return photoSerno;
            }

            public void setPhotoSerno(String photoSerno) {
                this.photoSerno = photoSerno;
            }

            public String getCreatorUserid() {
                return creatorUserid;
            }

            public void setCreatorUserid(String creatorUserid) {
                this.creatorUserid = creatorUserid;
            }

            public String getCreatorUsername() {
                return creatorUsername;
            }

            public void setCreatorUsername(String creatorUsername) {
                this.creatorUsername = creatorUsername;
            }

            public String getCommentUserid() {
                return commentUserid;
            }

            public void setCommentUserid(String commentUserid) {
                this.commentUserid = commentUserid;
            }

            public String getCommentUsername() {
                return commentUsername;
            }

            public void setCommentUsername(String commentUsername) {
                this.commentUsername = commentUsername;
            }

            public String getCommentContent() {
                return commentContent;
            }

            public void setCommentContent(String commentContent) {
                this.commentContent = commentContent;
            }

            public long getCommentTime() {
                return commentTime;
            }

            public void setCommentTime(long commentTime) {
                this.commentTime = commentTime;
            }

            public String getCommentUserid2() {
                return commentUserid2;
            }

            public void setCommentUserid2(String commentUserid2) {
                this.commentUserid2 = commentUserid2;
            }

            public String getCommentUsername2() {
                return commentUsername2;
            }

            public void setCommentUsername2(String commentUsername2) {
                this.commentUsername2 = commentUsername2;
            }

            @Override
            public int describeContents() {
                return 0;
            }

            @Override
            public void writeToParcel(Parcel parcel, int i) {
                parcel.writeInt(commentId);
                parcel.writeString(commentSerno);
                parcel.writeString(photoSerno);
                parcel.writeString(creatorUserid);
                parcel.writeString(creatorUsername);
                parcel.writeString(commentUserid);
                parcel.writeString(commentUsername);
                parcel.writeString(commentContent);
                parcel.writeLong(commentTime);
                parcel.writeString(commentUserid2);
                parcel.writeString(commentUsername2);
            }
        }
    }

    public static class UnReadCommentBean implements Parcelable{

        /**
         * commentId : 1681
         * commentSerno : 47693247ebc84a16aef09ed32e65978d
         * photoSerno : 9d6cfab41b9f4e7eb602ddc45c404561
         * creatorUserid : zze
         * creatorUsername : 轻松王
         * commentUserid : wshh0007
         * commentUsername : E+H2h8qc58hl3Dpj2Ptj9w==
         * commentContent : jiq0Zg05kY98HMUA7TZ2JHdgzAqwsgfB
         * commentTime : 1532399079000
         * commentUserid2 :
         * commentUsername2 :
         * userImage : http://mobile.fingerchat.cn:8686/group2/M00/00/F3/CgMJklqnbjqADqNAAAAdb--4b9w187.png
         */

        private int commentId;
        private String commentSerno;
        private String photoSerno;
        private String creatorUserid;
        private String creatorUsername;
        private String commentUserid;
        private String commentUsername;
        private String commentContent;
        private long commentTime;
        private String commentUserid2;
        private String commentUsername2;
        private String userImage;

        protected UnReadCommentBean(Parcel in) {
            commentId = in.readInt();
            commentSerno = in.readString();
            photoSerno = in.readString();
            creatorUserid = in.readString();
            creatorUsername = in.readString();
            commentUserid = in.readString();
            commentUsername = in.readString();
            commentContent = in.readString();
            commentTime = in.readLong();
            commentUserid2 = in.readString();
            commentUsername2 = in.readString();
            userImage = in.readString();
        }

        public static final Creator<UnReadCommentBean> CREATOR = new Creator<UnReadCommentBean>() {
            @Override
            public UnReadCommentBean createFromParcel(Parcel in) {
                return new UnReadCommentBean(in);
            }

            @Override
            public UnReadCommentBean[] newArray(int size) {
                return new UnReadCommentBean[size];
            }
        };

        public int getCommentId() {
            return commentId;
        }

        public void setCommentId(int commentId) {
            this.commentId = commentId;
        }

        public String getCommentSerno() {
            return commentSerno;
        }

        public void setCommentSerno(String commentSerno) {
            this.commentSerno = commentSerno;
        }

        public String getPhotoSerno() {
            return photoSerno;
        }

        public void setPhotoSerno(String photoSerno) {
            this.photoSerno = photoSerno;
        }

        public String getCreatorUserid() {
            return creatorUserid;
        }

        public void setCreatorUserid(String creatorUserid) {
            this.creatorUserid = creatorUserid;
        }

        public String getCreatorUsername() {
            return creatorUsername;
        }

        public void setCreatorUsername(String creatorUsername) {
            this.creatorUsername = creatorUsername;
        }

        public String getCommentUserid() {
            return commentUserid;
        }

        public void setCommentUserid(String commentUserid) {
            this.commentUserid = commentUserid;
        }

        public String getCommentUsername() {
            return commentUsername;
        }

        public void setCommentUsername(String commentUsername) {
            this.commentUsername = commentUsername;
        }

        public String getCommentContent() {
            return commentContent;
        }

        public void setCommentContent(String commentContent) {
            this.commentContent = commentContent;
        }

        public long getCommentTime() {
            return commentTime;
        }

        public void setCommentTime(long commentTime) {
            this.commentTime = commentTime;
        }

        public String getCommentUserid2() {
            return commentUserid2;
        }

        public void setCommentUserid2(String commentUserid2) {
            this.commentUserid2 = commentUserid2;
        }

        public String getCommentUsername2() {
            return commentUsername2;
        }

        public void setCommentUsername2(String commentUsername2) {
            this.commentUsername2 = commentUsername2;
        }

        public String getUserImage() {
            return userImage;
        }

        public void setUserImage(String userImage) {
            this.userImage = userImage;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeInt(commentId);
            parcel.writeString(commentSerno);
            parcel.writeString(photoSerno);
            parcel.writeString(creatorUserid);
            parcel.writeString(creatorUsername);
            parcel.writeString(commentUserid);
            parcel.writeString(commentUsername);
            parcel.writeString(commentContent);
            parcel.writeLong(commentTime);
            parcel.writeString(commentUserid2);
            parcel.writeString(commentUsername2);
            parcel.writeString(userImage);
        }
    }

    public static class UnReadThumbsBean implements Parcelable{

        /**
         * tId : 991
         * thumbsSerno : 6154b06eaf1d499e81de0deba5ad2ef3
         * photoSerno : 9d6cfab41b9f4e7eb602ddc45c404561
         * photoUserId : zze
         * photoUserName : 轻松王
         * thumbsUserId : zze
         * thumbsUserName : QjNXycEtyRjlSiadeNEBlg==
         * thumbsTime : 1532399038000
         * userImage : http://mobile.fingerchat.cn:8686/group2/M00/00/FE/CgMJklqnbpqAS6wmAAEtRqD1vk4324.png
         */

        private int tId;
        private String thumbsSerno;
        private String photoSerno;
        private String photoUserId;
        private String photoUserName;
        private String thumbsUserId;
        private String thumbsUserName;
        private long thumbsTime;
        private String userImage;

        protected UnReadThumbsBean(Parcel in) {
            tId = in.readInt();
            thumbsSerno = in.readString();
            photoSerno = in.readString();
            photoUserId = in.readString();
            photoUserName = in.readString();
            thumbsUserId = in.readString();
            thumbsUserName = in.readString();
            thumbsTime = in.readLong();
            userImage = in.readString();
        }

        public static final Creator<UnReadThumbsBean> CREATOR = new Creator<UnReadThumbsBean>() {
            @Override
            public UnReadThumbsBean createFromParcel(Parcel in) {
                return new UnReadThumbsBean(in);
            }

            @Override
            public UnReadThumbsBean[] newArray(int size) {
                return new UnReadThumbsBean[size];
            }
        };

        public int getTId() {
            return tId;
        }

        public void setTId(int tId) {
            this.tId = tId;
        }

        public String getThumbsSerno() {
            return thumbsSerno;
        }

        public void setThumbsSerno(String thumbsSerno) {
            this.thumbsSerno = thumbsSerno;
        }

        public String getPhotoSerno() {
            return photoSerno;
        }

        public void setPhotoSerno(String photoSerno) {
            this.photoSerno = photoSerno;
        }

        public String getPhotoUserId() {
            return photoUserId;
        }

        public void setPhotoUserId(String photoUserId) {
            this.photoUserId = photoUserId;
        }

        public String getPhotoUserName() {
            return photoUserName;
        }

        public void setPhotoUserName(String photoUserName) {
            this.photoUserName = photoUserName;
        }

        public String getThumbsUserId() {
            return thumbsUserId;
        }

        public void setThumbsUserId(String thumbsUserId) {
            this.thumbsUserId = thumbsUserId;
        }

        public String getThumbsUserName() {
            return thumbsUserName;
        }

        public void setThumbsUserName(String thumbsUserName) {
            this.thumbsUserName = thumbsUserName;
        }

        public long getThumbsTime() {
            return thumbsTime;
        }

        public void setThumbsTime(long thumbsTime) {
            this.thumbsTime = thumbsTime;
        }

        public String getUserImage() {
            return userImage;
        }

        public void setUserImage(String userImage) {
            this.userImage = userImage;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeInt(tId);
            parcel.writeString(thumbsSerno);
            parcel.writeString(photoSerno);
            parcel.writeString(photoUserId);
            parcel.writeString(photoUserName);
            parcel.writeString(thumbsUserId);
            parcel.writeString(thumbsUserName);
            parcel.writeLong(thumbsTime);
            parcel.writeString(userImage);
        }
    }
}
