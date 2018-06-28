package com.lens.chatmodel.ui.message;

import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.lens.chatmodel.R;
import com.lens.chatmodel.utils.SmileUtils;
import com.lensim.fingerchat.commons.base.BaseActivity;
import com.lensim.fingerchat.commons.utils.L;
import com.lensim.fingerchat.commons.utils.StringUtils;


/**
 * Created by LY309313 on 2016/9/6.
 */

public class TextPreviewActivity extends BaseActivity implements TextureView.SurfaceTextureListener,
        MediaPlayer.OnCompletionListener,OnClickListener {

    private TextView mPreviewText;
    private TextureView mPreViewVideo;
    public static final int TEXT_PERVIEW = 1;
    public static final int VIDEO_PERVIEW = 2;
    public static final String PERVIEW_TYPE = "perview_type";
    private MediaPlayer mediaPlayer;
    private String path;
    private FrameLayout mRootView;

    @Override
    public void initView() {
        setContentView(R.layout.activity_textpreview);
        mPreviewText = (TextView) findViewById(R.id.mPreviewText);
        mPreViewVideo = (TextureView) findViewById(R.id.mPreViewVideo);
        mRootView = (FrameLayout) findViewById(R.id.mPreViewRoot);
        mPreViewVideo.setSurfaceTextureListener(this);
        mPreViewVideo.setOnClickListener(this);

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnCompletionListener(this);
    }


    @Override
    protected void onStop() {
        if(mediaPlayer.isPlaying()){
            mediaPlayer.pause();
        }
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        if(mediaPlayer!=null){
            mediaPlayer.release();
        }
        super.onDestroy();
    }

    private void prepare(Surface surface) {
        try {
            mediaPlayer.reset();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            // 设置需要播放的视频
            mediaPlayer.setDataSource(path);
            // 把视频画面输出到Surface
            mediaPlayer.setSurface(surface);
            mediaPlayer.setLooping(true);
            mediaPlayer.prepare();
            mediaPlayer.seekTo(0);
            mediaPlayer.start();
        } catch (Exception e) {
        }
    }
    @Override
    public void initData(Bundle savedInstanceState) {
        int mode = getIntent().getIntExtra(PERVIEW_TYPE, 1);
        if(mode == TEXT_PERVIEW){
            String text = getIntent().getStringExtra("text");
            if(!StringUtils.isEmpty(text)){
                mPreviewText.setVisibility(View.VISIBLE);
                mPreViewVideo.setVisibility(View.GONE);
                CharSequence smiledText = SmileUtils.getSmiledText(this, text, 0);
                mPreviewText.setText(smiledText, TextView.BufferType.SPANNABLE);
            }
        }else{
            path = getIntent().getStringExtra("path");
            L.d("需要播放的视频:" + path);
            if(!StringUtils.isEmpty(path)){
                mPreviewText.setVisibility(View.GONE);
                mPreViewVideo.setVisibility(View.VISIBLE);
            }
        }

        initListener();


    }

    public void initListener() {
        mPreviewText.setOnClickListener(this);
        mRootView.setOnClickListener(this);
    }




    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i1) {
        prepare(new Surface(surfaceTexture));
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i1) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {

    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {

    }

    @Override
    public void onClick(View v) {
        finish();
    }
}
