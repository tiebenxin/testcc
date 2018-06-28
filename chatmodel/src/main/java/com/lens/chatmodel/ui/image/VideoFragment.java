package com.lens.chatmodel.ui.image;

import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import com.lens.chatmodel.R;
import com.lens.chatmodel.helper.FileCache;


/**
 * date on 2018/4/9
 * author ll147996
 * describe
 */

public class VideoFragment extends Fragment implements TextureView.SurfaceTextureListener, MediaPlayer.OnCompletionListener {

    private TextureView mVideo;
    private MediaPlayer mediaPlayer;
    private String path;
    private FrameLayout mVideoRoot;
    private Surface mSurface;
    public static boolean isViewPagerSelected = false;

    public static VideoFragment newInstance(String path) {
        VideoFragment fragment = new VideoFragment();
        Bundle bundle = new Bundle();
        bundle.putString("path", path);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
        @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_video, container, false);
        mVideo = rootView.findViewById(R.id.mVideo);
        mVideoRoot = rootView.findViewById(R.id.mVideoRoot);
        mVideo.setSurfaceTextureListener(this);
        mVideoRoot.setOnClickListener(v -> ((LookUpPhotosActivity) getActivity()).onPhotoTap());
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnCompletionListener(this);
        initData();
        return rootView;
    }


    public void initData() {
        path = getArguments().getString("path");
        if (path != null && path.startsWith("http")) {
            path = FileCache.getInstance().getVideoPath(path);
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            prepare();
        } else {
            toPauseMethod();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        FileCache.getInstance().encrypt(path);
    }

    @Override
    public void onResume() {
        super.onResume();
        FileCache.getInstance().decrypt(path);
    }

    public void toPauseMethod() {
        try {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void prepare() {
        if (isViewPagerSelected && mSurface != null) {
            try {
                mediaPlayer.reset();
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                // 设置需要播放的视频
                mediaPlayer.setDataSource(path);
                // 把视频画面输出到Surface
                mediaPlayer.setSurface(mSurface);
                mediaPlayer.setLooping(true);
                mediaPlayer.prepare();
                mediaPlayer.seekTo(0);
                mediaPlayer.start();
            } catch (Exception e) {
            }
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {

    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        mSurface = new Surface(surface);
        prepare();
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    @Override
    public void onPause() {
        toPauseMethod();
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        super.onDestroyView();
    }
}
