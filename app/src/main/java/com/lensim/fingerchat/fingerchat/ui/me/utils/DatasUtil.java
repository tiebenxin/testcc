package com.lensim.fingerchat.fingerchat.ui.me.utils;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.lensim.fingerchat.commons.global.Route;
import com.lensim.fingerchat.commons.utils.StringUtils;
import com.lensim.fingerchat.data.login.UserInfoRepository;
import com.lensim.fingerchat.data.me.CircleItem;
import com.lensim.fingerchat.data.me.ZambiaEntity;
import com.lensim.fingerchat.data.me.circle_friend.ContentEntity;
import com.lensim.fingerchat.data.me.circle_friend.FriendCircleEntity;
import com.lensim.fingerchat.fingerchat.model.bean.PhotoBean;
import com.lensim.fingerchat.fingerchat.model.bean.ThumbsBean;
import com.lensim.fingerchat.fingerchat.ui.me.circle_friends.adapter.viewholder.CircleViewHolder;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;


/**
 * date on 2017/12/21
 * author ll147996
 * describe 类转化
 */
public class DatasUtil {

    public static int getPhotoType(String photoUrl) {
        int type;
        if (TextUtils.isEmpty(photoUrl)){
            type = CircleViewHolder.TYPE_TEXT; // 文字
        } else if (photoUrl.contains(".mp4")) {
            type = CircleViewHolder.TYPE_VIDEO;// 视频
        }else {
            type = CircleViewHolder.TYPE_IMAGE;// 图片
        }
        return type;
    }

    public static String getVideoUrl(String photoUrl) {
        if (TextUtils.isEmpty(photoUrl)) return "";
        String[] strings = photoUrl.split(",");
        return strings[0];
    }

    public static String getVideoThumbnail(String photoUrl) {
        if (TextUtils.isEmpty(photoUrl)) return "";
        String[] strings = photoUrl.split(",");
        if (strings.length == 1) {
            return "";
        }else {
            return strings[1];
        }
    }

    public static List<String> getImageUrls(String photoUrl) {
        List<String> list = new ArrayList<>();
        String[] imgUrls = photoUrl.split(",");
        for (String s : imgUrls) {
            list.add(s);
        }
        return list;
    }


    public static List<CircleItem> createCircleDatas(List<FriendCircleEntity> entities) {
        List<CircleItem> circleDatas = new ArrayList<>();
        for (int i = 0; i < entities.size(); i++) {
            FriendCircleEntity entity = entities.get(i);
            CircleItem item = new CircleItem();
            item.userid = entity.getPHO_CreateUserID();
            item.username = entity.getUSR_Name();
            item.content = entity.getPHO_Content();
            item.createTime = entity.getPHO_CreateDT();

            item.favorters = createFavortItemList(entity.getZambia());
            item.comments = createCommentItemList(entity.getContent());
            item.id = entity.getPHO_Serno();
            if (entity.getPHO_CreateUserID().equals(UserInfoRepository.getUserName())) {
                item.headUrl = String.format(Route.obtainAvater, UserInfoRepository.getUserName());
            } else {
                item.headUrl = String.format(Route.obtainAvater, entity.getPHO_CreateUserID());
            }

            if (entity.getPHO_ImageName().contains(".mp4")) {
                item.type = "3";// 图片

                item.videoUrl = createVideoUrl(entity.getPHO_ImageName(), entity.getPHO_ImagePath());
                circleDatas.add(item);
            } else {
                item.type = "2";// 图片
                item.photos = createPhotos(entity.getPHO_ImageName(), entity.getPHO_ImagePath());
                circleDatas.add(item);
            }
        }
        return circleDatas;
    }

    public static List<String> createPhotos(String namestr, String path) {
        if (StringUtils.isEmpty(namestr)) {
            return null;
        }
        List<String> photos = new ArrayList<String>();
        String[] names = namestr.split(";");
        for (int i = 0; i < names.length; i++) {
            String url = path.replace("C:\\HnlensWeb\\", Route.Host) + names[i];
            url = url.replace("\\", "/");
            photos.add(url);
        }
        return photos;
    }

    public static String createVideoUrl(String namestr, String path) {
        if (StringUtils.isEmpty(namestr)) {
            return null;
        }
        String url = "";
        String[] names = namestr.split(";");
        String s = path.replace("C:\\HnlensWeb\\", Route.Host) + names[0];
        url = s.replace("\\", "/");

        return url;
    }

    public static List<ZambiaEntity> createFavortItemList(String zamStr) {
        List<ZambiaEntity> items = null;

        if (!StringUtils.isEmpty(zamStr)) {
            items = new ArrayList<>();
            Gson gson = new Gson();
            try {
                JSONArray array = new JSONArray(zamStr);
                for (int i = 0; i < array.length(); i++) {
                    String s = array.getString(i);
                    ZambiaEntity zambiaEntity = gson.fromJson(s, ZambiaEntity.class);
                    items.add(zambiaEntity);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return items;
    }


    public static List<ContentEntity> createCommentItemList(String commentStr) {
        List<ContentEntity> items = null;
        if (!StringUtils.isEmpty(commentStr)) {
            items = new ArrayList<>();
            Gson gson = new Gson();
            try {
                JSONArray array = new JSONArray(commentStr);
                for (int i = 0; i < array.length(); i++) {
                    String s = array.getString(i);
                    final ContentEntity contentEntity = gson.fromJson(s, ContentEntity.class);
                    items.add(contentEntity);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return items;
    }

    public static ZambiaEntity createCurUserFavortItem() {
        ZambiaEntity item = new ZambiaEntity();
        item.PHC_CommentUserid = UserInfoRepository.getUserName();
        item.PHC_CommentUsername = UserInfoRepository.getUsernick();
        return item;
    }

    public static List<String> getFavortItems(List<ThumbsBean> zambiaEntities) {
        List<String> favortItems = new ArrayList<>();
        if (zambiaEntities == null) {
            return favortItems;
        }
        for (ThumbsBean item : zambiaEntities) {
            favortItems.add(item.getThumbsUserName());
        }
        return favortItems;
    }

}
