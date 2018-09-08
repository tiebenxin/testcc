package com.lensim.fingerchat.fingerchat.ui.me.collection.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.lensim.fingerchat.commons.helper.ContextHelper;
import com.lensim.fingerchat.commons.utils.FileUtil;
import com.lensim.fingerchat.commons.utils.JsonUtils;
import com.lensim.fingerchat.commons.utils.StringUtils;
import com.lensim.fingerchat.components.widget.CircleProgress;
import com.lensim.fingerchat.data.me.content.VideoFavContent;
import com.lensim.fingerchat.fingerchat.R;
import com.lensim.fingerchat.fingerchat.api.DownloadApi;
import com.lensim.fingerchat.fingerchat.component.download.DownloadProgressListener;
import com.lensim.fingerchat.fingerchat.ui.me.collection.type.Content;

import me.jessyan.progressmanager.ProgressListener;
import me.jessyan.progressmanager.ProgressManager;
import me.jessyan.progressmanager.body.ProgressInfo;

/**
 * date on 2018/3/14
 * author ll147996
 * describe
 */

public class SimpleVideoView implements AbsContentView {

    public static final String URL = "url";
    private FrameLayout fl;
    private ImageView videothumbnial;
    private ImageView img_loading;
    private LinearLayout ll_loading;
    private CircleProgress progressBar;
    Context mContext;
    private VideoFavContent favContent;

    public SimpleVideoView(Context ctx, Content content) {
        mContext = ctx;
        favContent = JsonUtils.fromJson(content.getText(), VideoFavContent.class);
    }

    @SuppressLint("CheckResult")
    private void setSimpleVideo() {
        if (favContent == null || TextUtils.isEmpty(favContent.getVideoUrl())) {
            return;
        }
        /*AppConfig.CIRCLE_PATH + videoName*/
        if (!StringUtils.isEmpty(favContent.getVideoUrl()) && FileUtil
            .checkFilePathExists(favContent.getVideoUrl())) {
            Glide.with(ContextHelper.getContext())
                .load(favContent.getImageUrl()).asBitmap().centerCrop()
                .into(videothumbnial);
        } else {
            ll_loading.setVisibility(View.VISIBLE);
            img_loading.setVisibility(View.GONE);

            ProgressManager.getInstance()
                .addResponseListener(favContent.getVideoUrl(), new ProgressListener() {
                    @Override
                    public void onError(long id, Exception e) {

                    }

                    @Override
                    public void onProgress(ProgressInfo progressInfo) {
                        progressBar.setPercent(progressInfo.getPercent());
                    }
                });
            DownloadProgressListener downloadProgressListener = (bytesRead, contentLength, done) -> {
                //
            };
            new DownloadApi(downloadProgressListener)
                .downloadVideo(favContent.getVideoUrl(), bytes -> {
                    progressBar.setVisibility(View.GONE);
                    img_loading.setVisibility(View.VISIBLE);
                    Glide.with(ContextHelper.getContext())
                        .load(favContent.getImageUrl())
                        .asBitmap()
                        .centerCrop()
                        .into(videothumbnial);
                });
        }
    }

    @Override
    public View getFrameLayoutView(LayoutInflater mInflater, ViewGroup parent) {
        View view = mInflater.inflate(R.layout.viewstub_videobody, parent, false);
        fl = view.findViewById(R.id.container_video_play);
        videothumbnial = view.findViewById(R.id.video_override);
        img_loading = view.findViewById(R.id.icon_play);
        ll_loading = view.findViewById(R.id.ll_loading);
        progressBar = view.findViewById(R.id.progress_bar);
        setSimpleVideo();
        return view;
    }
}
