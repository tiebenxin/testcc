package com.lens.chatmodel.ui.image;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.lens.chatmodel.R;
import com.lens.chatmodel.ui.image.ImagePagerActivity.ImageSize;
import com.lensim.fingerchat.commons.base.FGActivity;
import com.lensim.fingerchat.commons.toolbar.FGToolbar;
import com.lensim.fingerchat.commons.utils.BitmapUtil;
import com.lensim.fingerchat.commons.utils.FileUtil;
import com.lensim.fingerchat.commons.utils.TDevice;
import com.lens.chatmodel.view.photoedit.CameraSurfaceView;
import com.lens.chatmodel.view.photoedit.ColorPickView;


/**
 * Created by LY309313 on 2017/4/1.
 */

public class PhotoEditActivity extends FGActivity implements SurfaceHolder.Callback,
    OnClickListener {


    CameraSurfaceView sv;
    ImageView line;
    ImageView word;
    ImageView mosaic;
    ImageView clip;
    ColorPickView colorview;
    TextView cancel;
    LinearLayout colorviewContainer;
    LinearLayout group;
    LinearLayout mNavBar;
    FrameLayout mMain;
    private boolean hideTitle;

    private boolean hidding;

    private boolean showing;
    private FGToolbar mToolBar;


    @SuppressLint("WrongViewCast")
    @Override
    public void initView() {
        setContentView(R.layout.activity_photo_edit);
        mToolBar = findViewById(R.id.viewTitleBar);
        initToolBar();
        sv = findViewById(R.id.sv);
        line =findViewById(R.id.line);
        word = findViewById(R.id.word);
        mosaic = findViewById(R.id.mosaic);
        clip = findViewById(R.id.clip);
        colorview = findViewById(R.id.colorview);
        cancel = findViewById(R.id.cancel);
        colorviewContainer = findViewById(R.id.colorviewContainer);
        group = findViewById(R.id.group);
        mNavBar = findViewById(R.id.mNavBar);
        mMain = findViewById(R.id.main);
        mMain.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        initListener();
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        Intent intent = getIntent();
        String filePath = intent.getStringExtra("edit_file_path");

        int[] imageSize = BitmapUtil.getImageSize(filePath);

        ImageSize size;
        float screenWidth = TDevice.getScreenWidth();
        float screenHeight = TDevice.getScreenHeight();
        if (imageSize[0] < screenWidth && imageSize[1] < screenHeight) {
            size = new ImageSize(imageSize[0], imageSize[1]);
        } else if (imageSize[0] > imageSize[1]) {
            float ratio = screenWidth / imageSize[0];
            size = new ImageSize((int) screenWidth, (int) (imageSize[1] * ratio));
        } else {
            float ratio = screenHeight / imageSize[1];
            size = new ImageSize((int) (ratio * imageSize[0]), (int) screenHeight);
        }
        sv.setOriginBitmap(filePath, size);
        sv.setCanDraw(true);

    }

    private void initToolBar() {
        mToolBar.setTitleText(R.string.edit);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) mToolBar
            .getLayoutParams();
        layoutParams.topMargin = TDevice.getStatuBarHeight();
        mToolBar.setLayoutParams(layoutParams);
        initBackButton(mToolBar,true);
        mToolBar.setConfirmBt(new OnClickListener() {
            @Override
            public void onClick(View v) {
                confirm();
            }
        });

    }

    public void initListener() {
        line.setOnClickListener(this);
        word.setOnClickListener(this);
        mosaic.setOnClickListener(this);
        clip.setOnClickListener(this);
        cancel.setOnClickListener(this);
        colorview.setOnColorPickListenr(new ColorPickView.OnColorPickListenr() {
            @Override
            public void onColorPick(int color) {
                sv.setColor(color);
            }
        });
        sv.setOptionListener(new CameraSurfaceView.OnOptionListener() {


            @Override
            public void onStartDraw() {
                if (!hideTitle) {
                    hideTitle = true;
                    hideBar();
                }
            }

            @Override
            public void OnEndDraw() {
                if (hideTitle) {
                    hideTitle = false;
                    showBar();
                }
            }

            @Override
            public void ontap() {
                hideTitle = !hideTitle;
                //这里执行显示和隐藏的效果
                if (hideTitle) {
                    //隐藏标题栏和工具栏
                    hideBar();

//                    mMain.setSystemUiVisibility(View.INVISIBLE);
//                    mMain.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);

                } else {
                    showBar();
                    //AnimationUtility.showTViews(mToolbar,mNavBar);
//                    mMain.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);

                }

            }
        });
    }

    public void showBar() {
        if (showing || hidding) {
            return;
        }

        showing = true;
        showStatusBar();
        mToolBar.setVisibility(View.VISIBLE);
        mNavBar.setVisibility(View.VISIBLE);
//        final int toolbarH = mToolbar.getHeight();
//        final int navH = mNavBar.getHeight();
        final int size = (int) TDevice.dpToPixel(56);
        ValueAnimator animator = ValueAnimator.ofFloat(0, 100);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                float percent = value / 100;
                ViewGroup.LayoutParams layoutParams = mToolBar.getLayoutParams();
                layoutParams.height = (int) (size * percent);
                mToolBar.setLayoutParams(layoutParams);

                ViewGroup.LayoutParams navParams = mNavBar.getLayoutParams();
                navParams.height = (int) (2 * size * percent);
                mNavBar.setLayoutParams(navParams);

            }
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                showing = false;
            }
        });
        animator.setDuration(300);
        animator.setInterpolator(new AccelerateInterpolator());
        animator.start();

    }

    private void hideBar() {
        if (showing || hidding) {
            return;
        }
        hidding = true;

        final int size = (int) TDevice.dpToPixel(56);
        ValueAnimator animator = ValueAnimator.ofFloat(100, 0);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                float percent = value / 100;
                ViewGroup.LayoutParams layoutParams = mToolBar.getLayoutParams();
                layoutParams.height = (int) (size * percent);
                mToolBar.setLayoutParams(layoutParams);

                ViewGroup.LayoutParams navParams = mNavBar.getLayoutParams();
                navParams.height = (int) (size * 2 * percent);
                mNavBar.setLayoutParams(navParams);

            }
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                hidding = false;
                hideStatusBar();
                mToolBar.setVisibility(View.GONE);
                mNavBar.setVisibility(View.GONE);
            }
        });
        animator.setDuration(300);
        animator.setInterpolator(new AccelerateInterpolator());
        animator.start();
    }

    private void hideStatusBar() {
        WindowManager.LayoutParams attrs = getWindow().getAttributes();
        attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
        getWindow().setAttributes(attrs);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }

    private void showStatusBar() {
        WindowManager.LayoutParams attrs = getWindow().getAttributes();
        attrs.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
        getWindow().setAttributes(attrs);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // sv.setCanDraw(true);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            String result = data.getStringExtra("clip_result");
            int[] imageSize = BitmapUtil.getImageSize(result);

            ImageSize size;
            float screenWidth = TDevice.getScreenWidth();
            float screenHeight = TDevice.getScreenHeight();
            if (imageSize[0] < screenWidth && imageSize[1] < screenHeight) {
                size = new ImageSize(imageSize[0], imageSize[1]);
            } else if (imageSize[0] > imageSize[1]) {
                float ratio = screenWidth / imageSize[0];
                size = new ImageSize((int) screenWidth, (int) (imageSize[1] * ratio));
            } else {
                float ratio = screenHeight / imageSize[1];
                size = new ImageSize((int) (ratio * imageSize[0]), (int) screenHeight);
            }
            sv.setOriginBitmap(result, size);
            sv.setCanDraw(true);
        }
    }

    protected void confirm() {
        Bitmap bitmap = sv.getResultBitmap();
        if (bitmap != null) {
            showProgress("正在保存...", false);
            String filepath = FileUtil.saveToPicDir(bitmap);
            dismissProgress();
            Intent intent = new Intent();
            intent.putExtra("new_file_path", filepath);
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.line) {
            line.setImageResource(R.drawable.img_pen_sel);
            word.setImageResource(R.drawable.img_text_nor);
            mosaic.setImageResource(R.drawable.img_mosaic_nor);
            clip.setImageResource(R.drawable.img_cutting_nor);
            sv.setCurrentDraw(CameraSurfaceView.DRAW_PATH);

        } else if (i == R.id.word) {
            hideBar();
            line.setImageResource(R.drawable.img_pen_nor);
            word.setImageResource(R.drawable.img_text_sel);
            mosaic.setImageResource(R.drawable.img_mosaic_nor);
            clip.setImageResource(R.drawable.img_cutting_nor);
            sv.setCurrentDraw(CameraSurfaceView.DRAW_WORD);

        } else if (i == R.id.mosaic) {
            line.setImageResource(R.drawable.img_pen_nor);
            word.setImageResource(R.drawable.img_text_nor);
            mosaic.setImageResource(R.drawable.img_mosaic_sel);
            clip.setImageResource(R.drawable.img_cutting_nor);
            sv.setCurrentDraw(CameraSurfaceView.DRAW_MOSAIC);

        } else if (i == R.id.clip) {
            line.setImageResource(R.drawable.img_pen_nor);
            word.setImageResource(R.drawable.img_text_nor);
            mosaic.setImageResource(R.drawable.img_mosaic_nor);
            sv.setCurrentDraw(CameraSurfaceView.DRAW_NOTHING);
            Bitmap bitmap = sv.getResultBitmap();
            if (bitmap != null) {
                showProgress("稍等...", false);
                String filepath = FileUtil.saveToPicDir(bitmap);
                dismissProgress();
                Intent intent = new Intent(this, PhotoEditClipActivity.class);
                intent.putExtra("clip_file_path", filepath);
                startActivityForResult(intent, 1);
            }

        } else if (i == R.id.cancel) {
            sv.back();

        }
    }
}
