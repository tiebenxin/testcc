package com.lens.chatmodel.ui.video;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import com.lens.chatmodel.R;
import com.lensim.fingerchat.commons.base.BaseFragment;
import com.lensim.fingerchat.commons.helper.AnimationRect;


/**
 * Created by LL130386 on 2017/9/21.
 */

public class FragmentVideoPlay extends BaseFragment implements OnClickListener {

    private SurfaceView mSurfaceView;
    private MediaPlayer mediaPlayer;
    private String path;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
        @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_video_play, container, false);
    }

    @Override
    protected void initView() {
        mSurfaceView = getView().findViewById(R.id.surfaceView);
    }


    public static FragmentVideoPlay newInstance(String path, AnimationRect rect, boolean isSilent,
        String type) {
        FragmentVideoPlay fragment = new FragmentVideoPlay();
        Bundle bundle = new Bundle();
        bundle.putString("path", path);
        bundle.putBoolean("isSilent", isSilent);
        bundle.putParcelable("rect", rect);
        bundle.putString("type", type);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void initData() {
        path = getArguments().getString("path");
        if (mSurfaceView != null) {
            SurfaceHolder holder = mSurfaceView.getHolder();
            holder.addCallback(new Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder holder) {

                }

                @Override
                public void surfaceChanged(SurfaceHolder holder, int format, int width,
                    int height) {
                    try {
                        if (mediaPlayer == null) {
                            mediaPlayer = new MediaPlayer();
                        }
                        mediaPlayer.reset();
                        mediaPlayer.setDisplay(holder);
                        mediaPlayer.setDataSource(path);
                        mediaPlayer.setLooping(true);
                        mediaPlayer.prepare();
                        mediaPlayer.seekTo(0);
                        mediaPlayer.start();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void surfaceDestroyed(SurfaceHolder holder) {

                }
            });
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mSurfaceView.setOnClickListener(this);
    }


    @Override
    public void onStop() {
        super.onStop();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }


    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.surfaceView) {
            getActivity().finish();

        }
    }
}
