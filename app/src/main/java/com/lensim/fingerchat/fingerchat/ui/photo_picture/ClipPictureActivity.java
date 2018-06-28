package com.lensim.fingerchat.fingerchat.ui.photo_picture;


import static com.lensim.fingerchat.commons.app.AppConfig.REGISTER_USER;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.lensim.fingerchat.commons.helper.ContextHelper;
import com.lensim.fingerchat.commons.utils.DensityUtil;
import com.lensim.fingerchat.commons.base.BaseActivity;
import com.lensim.fingerchat.commons.toolbar.FGToolbar;
import com.lensim.fingerchat.components.widget.ClipView;
import com.lensim.fingerchat.commons.utils.BitmapUtil;
import com.lensim.fingerchat.commons.utils.FileUtil;
import com.lensim.fingerchat.commons.utils.L;
import com.lensim.fingerchat.commons.utils.TDevice;
import com.lensim.fingerchat.fingerchat.R;
import com.lensim.fingerchat.fingerchat.ui.login.LoginActivity;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;

/**
 * 图片裁剪
 *
 * @author LY309313
 */
public class ClipPictureActivity extends BaseActivity implements OnTouchListener {

    ImageView srcPic;
    ClipView clipview;

    private FGToolbar toolbar;
    // 这两个矩阵将用于变焦和移动图片
    Matrix matrix = new Matrix();
    Matrix savedMatrix = new Matrix();

    //三种状态
    static final int NONE = 0;
    static final int DRAG = 1;
    static final int ZOOM = 2;
    int mode = NONE;

    // 记录移动或者变焦的点位
    PointF start = new PointF();
    PointF mid = new PointF();
    float oldDist = 1f;

    @Override
    public void initView() {
        setContentView(R.layout.activity_clippicture);

        srcPic = (ImageView) this.findViewById(R.id.iv_src_pic);
        srcPic.setOnTouchListener(this);
        toolbar = findViewById(R.id.clip_toolbar);

    }


    @Override
    public void initData(Bundle savedInstanceState) {
        String srcPath = getIntent().getDataString();
        mPhotoType = getIntent().getIntExtra("photoPath", -1);
        Glide.with(this).load(new File(srcPath)).fitCenter().into(srcPic);

        initTitleBar();


    }

    private void initTitleBar() {
        initBackButton(toolbar, true);
        toolbar.setTitleText(ContextHelper.getString(R.string.clip_image));
        toolbar.initRightView(createConfirmButton());
        toolbar.setConfirmListener(() -> confirm());
    }

    public Button createConfirmButton() {
        Button button = new Button(this);
        button.setText(ContextHelper.getString(R.string.sure));
        button.setTextColor(ContextHelper.getColor(R.color.white));
        button.setBackground(ContextHelper.getDrawable(R.drawable.green_btn_selector));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT);
        int d = DensityUtil.dip2px(ContextHelper.getContext(), 1);
        params.setMargins(0, 0, d, 0);
        button.setLayoutParams(params);
        button.setPadding(d, d / 2, d, d / 2);
        button.setGravity(Gravity.CENTER);
        return button;
    }


    private final String userPath = "avatarCache";
    private final String mucPath = "mucavatarCache";
    private final String imageName = "headimage.png";

    //点击确定，将值传回界面，并保存到/scard/lensim/defavatar/headimage.png
    private void confirm() {
        Bitmap fianBitmap = getBitmap();
        Bitmap bitmap = BitmapUtil
            .specifyRatio(fianBitmap, TDevice.dpToPixel(80), TDevice.dpToPixel(80));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] bitmapByte = baos.toByteArray();
        L.d("图片大小", bitmapByte.length + "");
        ByteArrayInputStream bis = new ByteArrayInputStream(bitmapByte);
        Intent intent = new Intent();
        String path = "";
        if (mPhotoType != -1) {
            if (mPhotoType == REGISTER_USER) {
                path = FileUtil.writeFile2Cache(bis, this, userPath, imageName);
                intent.setClass(this, LoginActivity.class);
            } else {
                path = FileUtil.writeFile2Cache(bis, this, mucPath, imageName);
                //intent.setClass(this, TransforMsgActivity.class);
            }
        }
        intent.putExtra("bitmap", path);
        setResult(RESULT_OK, intent);
        finish();

    }


    //触摸事件的处理，实现多点触摸
    public boolean onTouch(View v, MotionEvent event) {
        ImageView view = (ImageView) v;
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                savedMatrix.set(matrix);
                //开始点位的设置
                start.set(event.getX(), event.getY());
                L.d("mode=DRAG");
                mode = DRAG;//刚落下时，模式为拖拽
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                //多点触摸的事件
                oldDist = spacing(event);
                L.d("oldDist=" + oldDist);
                if (oldDist > 10f) {
                    savedMatrix.set(matrix);
                    midPoint(mid, event);
                    mode = ZOOM;//如果距离大于10个像素，则模式变为变焦
                    L.d("mode=ZOOM");
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                mode = NONE;//抬起手指，触摸事件结束
                L.d("mode=NONE");
                break;
            case MotionEvent.ACTION_MOVE:
                if (mode == DRAG) {
                    // ...
                    matrix.set(savedMatrix);
                    matrix.postTranslate(event.getX() - start.x, event.getY()
                        - start.y);//如果只是拖拽，则图片的矩阵不用发生变化，直接移动图片就好
                } else if (mode == ZOOM) {
                    float newDist = spacing(event);
                    L.d("newDist=" + newDist);
                    if (newDist > 10f) {
                        matrix.set(savedMatrix);
                        float scale = newDist / oldDist;
                        matrix.postScale(scale, scale, mid.x, mid.y);
                        //如果是变焦，则变换图片的大小
                    }
                }
                break;
        }

        view.setImageMatrix(matrix);
        return true; //返回true，事件已经得到妥善处理
    }

    /**
     * 定义最初两根手指的距离
     */
    private float spacing(MotionEvent event) {

        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    /**
     * 计算两根手指的中间点位
     */
    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }

    //获取裁剪的图片
    private Bitmap getBitmap() {
        statusBarHeight = TDevice.getStatuBarHeight();
        Bitmap screenShoot = takeScreenShot();

        clipview = (ClipView) this.findViewById(R.id.clipview);
        int width = clipview.getWidth();
        int height = clipview.getHeight();
        Bitmap finalBitmap = Bitmap.createBitmap(screenShoot,
            (width - height / 2) / 2, (int) (height / 4 + TDevice.dpToPixel(56)
                + statusBarHeight), height / 2, height / 2);
        return finalBitmap;
    }

    int statusBarHeight = 0;
    int titleBarHeight = 0;
    private int mPhotoType;

    // 获取activity的截屏
    private Bitmap takeScreenShot() {
        View view = this.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        return view.getDrawingCache();
    }


}