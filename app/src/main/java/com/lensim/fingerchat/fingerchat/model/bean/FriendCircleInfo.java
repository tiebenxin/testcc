package com.lensim.fingerchat.fingerchat.model.bean;

import com.lensim.fingerchat.data.me.circle_friend.FxPhotosBean;
import java.util.List;

/**
 * @time 2017/11/29 8:59
 * @class describe
 */

public class FriendCircleInfo {

    private List<FxPhotosBean> fxNewPhotos;

    public List<FxPhotosBean> getFxNewPhotos() {
        return fxNewPhotos;
    }

    public void setFxNewPhotos(List<FxPhotosBean> fxNewPhotos) {
        this.fxNewPhotos = fxNewPhotos;
    }

}
