package com.lens.chatmodel.bean.body;

import android.text.TextUtils;
import com.lensim.fingerchat.commons.base.BaseJsonEntity;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by LL130386 on 2017/12/22.
 * 上传图片解析数据
 */

public class VideoUploadEntity extends BaseJsonEntity {

  /*
  *
  *  {"Status":"OK","Value":
  *  {"ImageUrl":"http://10.3.9.97:8168/group1/M00/00/00/CgMJYVofyGWAD1WlAAAiuYnmSoE928.jpg","ImageSize":"240x180",
  *  "VideoUrl":"http://10.3.9.97:8168/group1/M00/00/00/CgMJYVofyGWATNwcANSIPDAg69o444.mp4"}}
  * */

    String ImageUrl;
    String ImageSize;

    String VideoUrl;
    int timeLength;


    public static VideoUploadEntity fromJson(String json) {
        if (TextUtils.isEmpty(json)) {
            return null;
        }
        try {
            JSONObject object = new JSONObject(json);
            if (object != null) {
                VideoUploadEntity entry = new VideoUploadEntity();
                if (object.has("ImageUrl") && !TextUtils.isEmpty(object.optString("ImageUrl"))) {
                    entry.setImageUrl(object.optString("ImageUrl"));
                } else {
                    entry.setImageUrl("");
                }

                if (object.has("ImageSize") && !TextUtils.isEmpty(object.optString("ImageSize"))) {
                    entry.setImageSize(object.optString("ImageSize"));
                } else {
                    entry.setImageSize("");
                }

                if (object.has("VideoUrl") && !TextUtils.isEmpty(object.optString("VideoUrl"))) {
                    entry.setVideoUrl(object.optString("VideoUrl"));
                } else {
                    entry.setVideoUrl("");
                }

                if (object.has("timeLength") && object.optInt("timeLength") > 0) {
                    entry.setTimeLength(object.optInt("timeLength"));
                } else {
                    entry.setTimeLength(0);
                }
                return entry;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;

    }

    public static String toJson(VideoUploadEntity entry) {
        if (entry != null) {
            try {
                JSONObject object = new JSONObject();
                object.put("ImageUrl", entry.getImageUrl());
                object.put("ImageSize", entry.getImageSize());
                object.put("VideoUrl", entry.getVideoUrl());
                object.put("timeLength", entry.getTimeLength());
                return object.toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    public static JSONObject toObject(VideoUploadEntity entry) {
        if (entry != null) {
            try {
                JSONObject object = new JSONObject();
                object.put("ImageUrl", entry.getImageUrl());
                object.put("ImageSize", entry.getImageSize());
                object.put("VideoUrl", entry.getVideoUrl());
                object.put("timeLength", entry.getTimeLength());
                return object;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static VideoUploadEntity createEntity(String video, String image, String size,
        int time) {
        VideoUploadEntity entity = new VideoUploadEntity();
        entity.setVideoUrl(video);
        entity.setImageUrl(image);
        entity.setImageSize(size);
        entity.setTimeLength(time);
        return entity;
    }

    public String getImageUrl() {
        return ImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        ImageUrl = imageUrl;
    }

    public String getImageSize() {
        return ImageSize;
    }

    public void setImageSize(String imageSize) {
        ImageSize = imageSize;
    }

    public String getVideoUrl() {
        return VideoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        VideoUrl = videoUrl;
    }

    public int getTimeLength() {
        return timeLength;
    }

    public void setTimeLength(int timeLength) {
        this.timeLength = timeLength;
    }
}
