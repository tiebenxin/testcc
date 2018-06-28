package com.lens.chatmodel.ui.video;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;

import com.lens.chatmodel.R;
import com.lens.chatmodel.cache.FileManager;
import com.lens.chatmodel.helper.FileCache;
import com.lensim.fingerchat.commons.base.BaseFragment;
import com.lensim.fingerchat.commons.helper.AnimationRect;
import com.lensim.fingerchat.commons.utils.L;
import com.lensim.fingerchat.commons.utils.StringUtils;
import com.lensim.fingerchat.commons.utils.TDevice;

import java.io.File;
import java.io.FileInputStream;

/**
 * Created by LY309313 on 2016/11/9.
 */

public class LookupVideoFragment extends BaseFragment implements TextureView.SurfaceTextureListener,
    MediaPlayer.OnCompletionListener, OnClickListener {

    private static final int ANIMATION_DURATION = 300;

    Button mLookupVideoConfirm;
    TextureView mLookupVideo;
    FrameLayout mVideo;
    FrameLayout mLookupVideoHeader;
    private MediaPlayer mediaPlayer;
    private String path;
    private boolean isSilent = false;
    private String type;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
        @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_lookup_video, container, false);
    }


    public void initListener() {
        mLookupVideo.setSurfaceTextureListener(this);
        mLookupVideoConfirm.setOnClickListener(this);
        mLookupVideo.setOnClickListener(this);
        mVideo.setOnClickListener(this);
        mediaPlayer.setOnCompletionListener(this);
    }


    private void prepare(Surface surface) {
        try {
            mediaPlayer.reset();
            // 把视频画面输出到Surface
            mediaPlayer.setSurface(surface);
            OpenOrCloseVolume(isSilent);
            // 设置需要播放的视频
            File file = new File(path);
            FileInputStream fis = new FileInputStream(file);
            mediaPlayer.setDataSource(fis.getFD());
            mediaPlayer.setLooping(true);
            mediaPlayer.prepare();
            mediaPlayer.seekTo(0);
            mediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static LookupVideoFragment newInstance(String path, AnimationRect rect, boolean isSilent,
        String type) {
        LookupVideoFragment fragment = new LookupVideoFragment();
        Bundle bundle = new Bundle();
        bundle.putString("path", path);
        bundle.putBoolean("isSilent", isSilent);
        bundle.putParcelable("rect", rect);
        bundle.putString("type", type);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void initView() {
        mLookupVideoConfirm = getView().findViewById(R.id.mLookupVideoConfirm);
        mLookupVideo = getView().findViewById(R.id.mLookupVideo);
        mVideo = getView().findViewById(R.id.mVideo);
        mLookupVideoHeader = getView().findViewById(R.id.mLookupVideoHeader);

        mediaPlayer = new MediaPlayer();
        String type = getActivity().getIntent().getStringExtra("type");
        if (!StringUtils.isEmpty(type) && type.equals("chat")) {
            mLookupVideoHeader.setVisibility(View.INVISIBLE);
        } else {
            TDevice.setWindowStatusBarColor(getActivity(), R.color.primary);
        }
        initListener();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (!checkIsLocalPath(path) || checkIsCacheUrl(path)) {
            FileCache.getInstance().encrypt(path);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!checkIsLocalPath(path) || checkIsCacheUrl(path)) {
            FileCache.getInstance().decrypt(path);
        }
    }

    @Override
    public void initData() {
        path = getArguments().getString("path");
        if (path != null && path.startsWith("http")) {
            path = FileCache.getInstance().getVideoPath(path);
        }
        type = getArguments().getString("type");
        isSilent = getArguments().getBoolean("isSilent");

        final AnimationRect rect = getArguments().getParcelable("rect");

        mVideo.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                if (rect == null) {
                    mVideo.getViewTreeObserver().removeOnPreDrawListener(this);
                    return true;
                }
                final Rect startRect = new Rect(rect.scaledBitmapRect);
                final Rect finalBounds = getvisibleRect(mLookupVideo);
                float startScale = finalBounds.width() / startRect.width();
                if (startScale * startRect.height() > finalBounds.height()) {
                    startScale = finalBounds.height() / startRect.height();
                }
                int deltaY = startRect.top - finalBounds.top;
                int deltaX = startRect.left - finalBounds.left;
                mVideo.setPivotX((mVideo.getWidth() - finalBounds.width()) / 2);
                mVideo.setPivotY((mVideo.getHeight() - finalBounds.height()) / 2);
                mVideo.setScaleX(1 / startScale);
                mVideo.setScaleY(1 / startScale);

                mVideo.setTranslationX(deltaX);
                mVideo.setTranslationY(deltaY);

                mVideo.animate().translationY(0).translationX(0)
                    .scaleY(1)
                    .scaleX(1).setDuration(ANIMATION_DURATION)
                    .setInterpolator(
                        new AccelerateDecelerateInterpolator());

                AnimatorSet animationSet = new AnimatorSet();
                animationSet.setDuration(ANIMATION_DURATION);
                animationSet
                    .setInterpolator(new AccelerateDecelerateInterpolator());

                animationSet.start();
                L.d("执行结束");
                mVideo.getViewTreeObserver().removeOnPreDrawListener(this);

                return true;
            }
        });
    }

    @Override
    public void onDestroyView() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
        super.onDestroyView();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {

    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        prepare(new Surface(surface));
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

    public void animationExit(ObjectAnimator backgroundAnimator) {

        animateClose(backgroundAnimator);
    }

    private void animateClose(ObjectAnimator backgroundAnimator) {

        AnimationRect rect = getArguments().getParcelable("rect");

        if (rect == null) {
            mVideo.animate().alpha(0);
            backgroundAnimator.start();
            return;
        }

        final Rect startBounds = rect.scaledBitmapRect;
        final Rect finalBounds = getvisibleRect(mLookupVideo);

        if (TDevice.isPortrait() != rect.isScreenPortrait) {
            mVideo.animate().alpha(0);
            backgroundAnimator.start();
            return;
        }

        float startScale;
        if ((float) finalBounds.width() / finalBounds.height()
            > (float) startBounds.width() / startBounds.height()) {
            startScale = (float) startBounds.height() / finalBounds.height();
        } else {
            startScale = (float) startBounds.width() / finalBounds.width();
        }

        final float startScaleFinal = startScale;

        int deltaTop = startBounds.top - finalBounds.top;
        int deltaLeft = startBounds.left - finalBounds.left;

        mVideo.setPivotY((mVideo.getHeight() - finalBounds.height()) / 2);
        mVideo.setPivotX((mVideo.getWidth() - finalBounds.width()) / 2);

        mVideo.animate().translationX(deltaLeft).translationY(deltaTop)
            .scaleY(startScaleFinal)
            .scaleX(startScaleFinal).setDuration(ANIMATION_DURATION)
            .setInterpolator(new AccelerateDecelerateInterpolator())
            .withEndAction(new Runnable() {
                @Override
                public void run() {

                    if (null == mVideo) {
                        return;
                    }
                    mVideo.animate().alpha(0.0f).setDuration(200).withEndAction(
                        new Runnable() {
                            @Override
                            public void run() {

                            }
                        });
                }
            });

        AnimatorSet animationSet = new AnimatorSet();
        animationSet.setDuration(ANIMATION_DURATION);
        animationSet.setInterpolator(new AccelerateDecelerateInterpolator());

        animationSet.playTogether(backgroundAnimator);

        animationSet.start();
    }

    private Rect getvisibleRect(TextureView mLookupVideo) {
        Rect rect = new Rect();
        mLookupVideo.getGlobalVisibleRect(rect);
        return rect;

    }

    //开启或者关闭声音
    public void OpenOrCloseVolume(boolean isSilent) {
        if (isSilent) {
            mediaPlayer.setVolume(0, 0);
        } else {
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            AudioManager audioManager = (AudioManager) getActivity()
                .getSystemService(Service.AUDIO_SERVICE);
//      mediaPlayer.setVolume(audioManager.getStreamVolume(AudioManager.STREAM_SYSTEM),
//          audioManager.getStreamVolume(AudioManager.STREAM_SYSTEM));
        }
    }

    private boolean checkIsLocalPath(String path) {
        if (!TextUtils.isEmpty(path)) {
            File file = new File(path);
            if (file.exists() && file.length() > 0) {
                return true;
            }
        }
        return false;
    }

    public boolean checkIsCacheUrl(String path) {
        if (!TextUtils.isEmpty(path)) {
            if (path.contains(FileCache.VIDEO)) {
                return true;
            }
        }
        return false;
    }


    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.mVideo || i == R.id.mLookupVideo) {
            mLookupVideoHeader.setVisibility(View.INVISIBLE);
            getActivity().onBackPressed();

        } else if (i == R.id.mLookupVideoConfirm) {
            Intent intent = new Intent();
            intent.putExtra("path", path);
            getActivity().setResult(Activity.RESULT_OK, intent);
            getActivity().finish();

        }
    }
}
