package com.lensim.fingerchat.fingerchat.model.result;

import com.lensim.fingerchat.commons.base.BaseResponse;
import com.lensim.fingerchat.fingerchat.model.bean.PhotoBean;

import java.util.List;

/**
 * 未读说说信息响应实体类
 * Created by zm on 2018/7/5.
 */
public class GetPhotoResult extends BaseResponse<GetPhotoResult.Data> {

    public final static class Data {
        List<PhotoBean> fxNewPhotos;

        public List<PhotoBean> getFxNewPhotos() {
            return fxNewPhotos;
        }

        public void setFxNewPhotos(List<PhotoBean> fxNewPhotos) {
            this.fxNewPhotos = fxNewPhotos;
        }

        @Override
        public String toString() {
            return "Data{" +
                "fxNewPhotos=" + fxNewPhotos +
                '}';
        }
    }
}
