package com.lensim.fingerchat.data.me;


import android.os.Parcel;
import android.text.TextUtils;
import com.lensim.fingerchat.data.me.circle_friend.ContentEntity;
import java.util.List;


public class CircleItem implements android.os.Parcelable {

    public final static String TYPE_URL = "1";
    public final static String TYPE_IMG = "2";
    public final static String TYPE_VIDEO = "3";

    public String id;
    public String content;
    public String createTime;
    public String type;//1:链接  2:图片 3:视频
    public String linkImg;
    public String linkTitle;
    public List<String> photos;
    public List<ZambiaEntity> favorters;
    public List<ContentEntity> comments;
    public String username;
    public String headUrl;
    public String userid;
    public String videoUrl;
    public String videoImgUrl;

    //add by ll117394
    public int childInList;//子位置
    public int childCount;//兄弟数
    public int parentinList;//父位置
    public String imgUrl;

    public String getCurUserFavortId(String curUserId) {
        String favortid = "";
        if (!TextUtils.isEmpty(curUserId) && favorters != null) {
            for (ZambiaEntity item : favorters) {
                if (curUserId.equals(item.PHC_CommentUserid)) {
                    favortid = item.PHC_CommentUserid;
                    return favortid;
                }
            }
        }
        return favortid;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.content);
        dest.writeString(this.createTime);
        dest.writeString(this.type);
        dest.writeString(this.linkImg);
        dest.writeString(this.linkTitle);
        dest.writeStringList(this.photos);
        dest.writeTypedList(this.favorters);
        dest.writeTypedList(this.comments);
        dest.writeString(this.username);
        dest.writeString(this.headUrl);
        dest.writeString(this.userid);
        dest.writeString(this.videoUrl);
        dest.writeString(this.videoImgUrl);
        dest.writeInt(this.childInList);
        dest.writeInt(this.childCount);
        dest.writeInt(this.parentinList);
        dest.writeString(this.imgUrl);
    }

    public CircleItem() {
    }

    protected CircleItem(Parcel in) {
        this.id = in.readString();
        this.content = in.readString();
        this.createTime = in.readString();
        this.type = in.readString();
        this.linkImg = in.readString();
        this.linkTitle = in.readString();
        this.photos = in.createStringArrayList();
        this.favorters = in.createTypedArrayList(ZambiaEntity.CREATOR);
        this.comments = in.createTypedArrayList(ContentEntity.CREATOR);
        this.username = in.readString();
        this.headUrl = in.readString();
        this.userid = in.readString();
        this.videoUrl = in.readString();
        this.videoImgUrl = in.readString();
        this.childInList = in.readInt();
        this.childCount = in.readInt();
        this.parentinList = in.readInt();
        this.imgUrl = in.readString();
    }

    public static final Creator<CircleItem> CREATOR = new Creator<CircleItem>() {
        @Override
        public CircleItem createFromParcel(Parcel source) {
            return new CircleItem(source);
        }

        @Override
        public CircleItem[] newArray(int size) {
            return new CircleItem[size];
        }
    };
}
