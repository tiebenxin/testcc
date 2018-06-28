package com.lens.chatmodel.bean.body;

import android.text.TextUtils;
import com.lensim.fingerchat.commons.base.BaseJsonEntity;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by LL130386 on 2017/12/22.
 * 上传图片解析数据
 */

public class ImageUploadEntity extends BaseJsonEntity{

    String OriginalUrl;
    String OriginalSzie;

    String ThumbnailUrl;
    String ThumbnailSize;


    public static ImageUploadEntity fromJson(String json) {
        if (TextUtils.isEmpty(json)) {
            return null;
        }
        try {
            JSONObject object = new JSONObject(json);
            if (object != null) {
                ImageUploadEntity entry = new ImageUploadEntity();
                if (object.has("OriginalUrl") && !TextUtils
                    .isEmpty(object.optString("OriginalUrl"))) {
                    entry.setOriginalUrl(object.optString("OriginalUrl"));
                } else {
                    entry.setOriginalUrl("");
                }
                if (object.has("OriginalSzie") && !TextUtils
                    .isEmpty(object.optString("OriginalSzie"))) {
                    entry.setOriginalSzie(object.optString("OriginalSzie"));
                } else {
                    entry.setOriginalSzie("");
                }
                if (object.has("ThumbnailUrl") && !TextUtils
                    .isEmpty(object.optString("ThumbnailUrl"))) {
                    entry.setThumbnailUrl(object.optString("ThumbnailUrl"));
                } else {
                    entry.setThumbnailUrl("");
                }
                if (object.has("ThumbnailSize") && !TextUtils
                    .isEmpty(object.optString("ThumbnailSize"))) {
                    entry.setThumbnailSize(object.optString("ThumbnailSize"));
                } else {
                    entry.setThumbnailSize("");
                }
                return entry;
            }
            return null;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

    }

    public static String toJson(ImageUploadEntity entry) {
        if (entry != null) {
            try {
                JSONObject object = new JSONObject();
                object.put("OriginalUrl", entry.getOriginalUrl());
                object.put("OriginalSzie", entry.getOriginalSize());
                object.put("ThumbnailUrl", entry.getThumbnailUrl());
                object.put("ThumbnailSize", entry.getThumbnailSize());
                return object.toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    public static JSONObject toObject(ImageUploadEntity entry) {
        if (entry != null) {
            try {
                JSONObject object = new JSONObject();
                object.put("OriginalUrl", entry.getOriginalUrl());
                object.put("OriginalSzie", entry.getOriginalSize());
                object.put("ThumbnailUrl", entry.getThumbnailUrl());
                object.put("ThumbnailSize", entry.getThumbnailSize());
                return object;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static ImageUploadEntity createEntity(String imageUrl) {
        return createEntity(imageUrl, "", "", "");
    }

    public static ImageUploadEntity createEntity(String imageUrl, String imageSize,
        String thumbnailUrl, String thumbnailSize) {
        ImageUploadEntity entity = new ImageUploadEntity();
        if (!TextUtils.isEmpty(imageUrl)) {
            entity.setOriginalUrl(imageUrl);
        } else {
            entity.setOriginalUrl("");

        }
        if (!TextUtils.isEmpty(imageSize)) {
            entity.setOriginalSzie(imageSize);
        } else {
            entity.setOriginalSzie("");
        }
        if (!TextUtils.isEmpty(thumbnailUrl)) {
            entity.setThumbnailUrl(thumbnailUrl);
        } else {
            entity.setThumbnailUrl("");

        }
        if (!TextUtils.isEmpty(thumbnailSize)) {
            entity.setThumbnailSize(thumbnailSize);
        } else {
            entity.setThumbnailSize("");

        }
        return entity;
    }

    public String getOriginalUrl() {
        return OriginalUrl;
    }

    public void setOriginalUrl(String originalUrl) {
        OriginalUrl = originalUrl;
    }

    public String getOriginalSize() {
        return OriginalSzie;
    }

    public void setOriginalSzie(String originalSzie) {
        OriginalSzie = originalSzie;
    }

    public String getThumbnailUrl() {
        return ThumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        ThumbnailUrl = thumbnailUrl;
    }

    public String getThumbnailSize() {
        return ThumbnailSize;
    }

    public void setThumbnailSize(String thumbnailSize) {
        ThumbnailSize = thumbnailSize;
    }
}
