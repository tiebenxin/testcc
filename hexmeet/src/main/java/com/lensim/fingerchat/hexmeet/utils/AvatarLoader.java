package com.lensim.fingerchat.hexmeet.utils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;


import com.lensim.fingerchat.hexmeet.App;
import com.lensim.fingerchat.hexmeet.R;
import com.lensim.fingerchat.hexmeet.RuntimeData;
import com.lensim.fingerchat.hexmeet.api.model.RestContact;
import com.lensim.fingerchat.hexmeet.api.model.RestGroup;
import com.lensim.fingerchat.hexmeet.api.model.RestUser;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.List;

public class AvatarLoader {

  private static final String tag = "AvatarLoader";

  private static LruCache<String, Bitmap> avatarCache = new LruCache<String, Bitmap>(
      5 * 1024 * 1024);
  private static LruCache<Integer, GroupBitmap> groupAvatarCache = new LruCache<Integer, GroupBitmap>(
      5 * 1024 * 1024);

  private static DisplayImageOptions options = new DisplayImageOptions.Builder()
      .showImageOnLoading(R.drawable.icon_contact).showImageForEmptyUri(R.drawable.icon_contact)
      .showImageOnFail(R.drawable.icon_contact).cacheInMemory(true).cacheOnDisk(true)
      .considerExifParams(true).build();

  public static void load(RestContact restContact, ImageView imageView) {
    load("https://" + RuntimeData.getUcmServer() + restContact.getImageURL(), imageView, null,
        null);
  }

  public static void load(RestUser restUser, ImageView imageView) {
    load("https://" + RuntimeData.getUcmServer() + restUser.getImageURL(), imageView, null, null);
  }

  public static void load(String url, ImageView imageView) {
    load(url, imageView, null, null);
  }

  public static void load(final String url, final ImageView imageView, final Activity activity,
      final Runnable runnable) {
    if (url == null || url.endsWith("null")) {
      imageView.setImageResource(R.drawable.icon_contact);
    } else {
      Bitmap bmp = avatarCache.get(url);
      if (bmp != null) {
        imageView.setImageBitmap(bmp);
        if (activity != null && runnable != null) {
          activity.runOnUiThread(runnable);
        }
        return;
      }

      imageView.setTag(url);
      ImageLoader.getInstance().loadImage(url, options, new ImageLoadingListener() {
        @Override
        public void onLoadingCancelled(String arg0, View arg1) {
        }

        @Override
        public void onLoadingComplete(String arg0, View arg1, Bitmap arg2) {
          if (arg2 != null) {
            avatarCache.put(arg0, arg2);
            if (arg0.equalsIgnoreCase(imageView.getTag().toString())) {
              imageView.setImageBitmap(arg2);
            }
          }

          if (activity != null && runnable != null) {
            activity.runOnUiThread(runnable);
          }
        }

        @Override
        public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
        }

        @Override
        public void onLoadingStarted(String arg0, View arg1) {
        }
      });
      //ImageLoader.getInstance().displayImage(url, imageView, options, );
    }
  }

  public static Bitmap loadSync(String url) {
    Bitmap ret = null;
    try {
      ret = ImageLoader.getInstance().loadImageSync(url, new ImageSize(120, 120), options);
    } catch (Exception ex) {
      // maybe load image failed from target url
    }
    return ret;
  }

  public static void downloadSelfAvatar() {
    new Thread(new Runnable() {
      @Override
      public void run() {
        Log.d(tag, "download self avatar");
        String url = Convertor.getAvatarUrl(RuntimeData.getSelfContact());
        Bitmap bitMap = AvatarLoader.loadSync(url);
        if (bitMap != null) {
          App.getHexmeetSdkInstance().saveUserIcon(bitMap);
        }
      }
    }).start();
  }

  public static void putGroupAvatar(RestGroup group, Bitmap bitmap) {
    groupAvatarCache.put(group.getId(), new GroupBitmap(group, bitmap));
  }

  public static Bitmap getGroupAvatar(RestGroup group) {
    GroupBitmap groupBitmap = groupAvatarCache.get(group.getId());
    if (groupBitmap != null) {
      List<RestContact> cachedContacts = groupBitmap.group.getContacts();
      List<RestContact> inputContacts = group.getContacts();
      if (isContactListEqual(cachedContacts, inputContacts)) {
        return groupBitmap.bitmap;
      } else {
        groupAvatarCache.remove(group.getId());
      }
    }

    return null;
  }

  private static boolean isContactListEqual(List<RestContact> l, List<RestContact> r) {
    if (l == null || r == null) {
      return false;
    }

    if (l.size() != l.size()) {
      return false;
    }

    for (RestContact lContact : l) {
      boolean found = false;
      for (RestContact rContact : r) {
        if (lContact != null && rContact != null && lContact.getId() == rContact.getId()
            && lContact.getLastModifiedTime() == rContact.getLastModifiedTime()) {
          found = true;
        }
      }

      if (!found) {
        return false;
      }
    }

    for (RestContact rContact : r) {
      boolean found = false;
      for (RestContact lContact : l) {
        if (lContact != null && rContact != null && lContact.getId() == rContact.getId()) {
          found = true;
        }
      }

      if (!found) {
        return false;
      }
    }

    return true;
  }

  private static class GroupBitmap {

    public RestGroup group;
    public Bitmap bitmap;

    public GroupBitmap(RestGroup group, Bitmap bitmap) {
      this.group = group;
      this.bitmap = bitmap;
    }
  }
}
