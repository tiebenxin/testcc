package com.lens.chatmodel.ui.video;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.IntDef;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;

import com.fingerchat.cameralibrary.JCameraView;
import com.fingerchat.cameralibrary.listener.ErrorListener;
import com.fingerchat.cameralibrary.listener.JCameraListener;
import com.fingerchat.cameralibrary.util.DeviceUtil;
import com.fingerchat.cameralibrary.util.FileUtil;
import com.lens.chatmodel.R;
import com.lens.core.util.DateUtil;
import com.lensim.fingerchat.commons.helper.ContextHelper;
import com.lensim.fingerchat.commons.utils.BitmapUtil;
import com.lensim.fingerchat.commons.utils.DensityUtil;
import com.lensim.fingerchat.commons.utils.L;
import com.lensim.fingerchat.commons.utils.StringUtils;
import com.lensim.fingerchat.commons.utils.T;

import com.lensim.fingerchat.commons.utils.TDevice;
import com.lensim.fingerchat.data.login.UserInfoRepository;
import java.io.File;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Date;

public class CameraActivity extends AppCompatActivity {

    private static final String EXTRAS_MODEL = "model";

    public static final int BUTTON_STATE_ONLY_CAPTURE = 0x101;      //只能拍照
    public static final int BUTTON_STATE_ONLY_RECORDER = 0x102;     //只能录像
    public static final int BUTTON_STATE_BOTH = 0x103;              //两者都可以

    @IntDef({BUTTON_STATE_ONLY_CAPTURE, BUTTON_STATE_ONLY_RECORDER, BUTTON_STATE_BOTH})
    @Retention(RetentionPolicy.SOURCE)
    public @interface BtnModel {

    }

    private JCameraView jCameraView;
    /**
     * 拍摄模式（默认两者都可以）
     */
    private @BtnModel
    int mode = BUTTON_STATE_BOTH;

    public static void start(Activity activity, int requestCode) {
        start(activity, requestCode, BUTTON_STATE_BOTH);
    }

    /**
     * @param mode 拍摄模式
     */
    public static void start(Activity activity, int requestCode, @BtnModel int mode) {
        Intent intent = new Intent();
        intent.setClass(activity, CameraActivity.class);
        intent.putExtra(EXTRAS_MODEL, mode);
        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_camera);

        initBundle();
        initCameraView();
    }

    private void initBundle() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mode = bundle.getInt(EXTRAS_MODEL);
        }
    }

    /**
     * 初始化CameraView
     */
    private void initCameraView() {
        jCameraView = findViewById(R.id.jcameraview);
        //设置视频保存路径
        jCameraView.setSaveVideoPath(
            Environment.getExternalStorageDirectory().getPath() + File.separator + "FGCamera");
        jCameraView.setFeatures(mode);
        jCameraView.setTextWithAnimation("轻触拍照，按住摄像");
        jCameraView.setMediaQuality(JCameraView.MEDIA_QUALITY_MIDDLE);
        jCameraView.setErrorLisenter(new ErrorListener() {
            @Override
            public void onError() {
                //错误监听
                L.i("Camera", "camera error");
                Intent intent = new Intent();
                setResult(103, intent);
                finish();
            }

            @Override
            public void AudioPermissionError() {
                T.show("给点录音权限可以?");
            }
        });
        //JCameraView监听
        jCameraView.setJCameraLisenter(new JCameraListener() {
            @Override
            public void captureSuccess(Bitmap bitmap) {
                //获取图片bitmap
                Bitmap result = BitmapUtil
                    .createWaterBitmap(bitmap, UserInfoRepository.getUserId());
//                Bitmap result = createWaterBitmap(bitmap);
                String path = FileUtil.saveBitmap("FGCamera", result);
                Intent intent = new Intent();
                intent.putExtra("imagePath", path);
                setResult(101, intent);
                finish();
            }

            @Override
            public void recordSuccess(String url, Bitmap firstFrame) {
                String path = FileUtil.saveBitmap("FGCamera", firstFrame);
                L.i("Camera", "url = " + url + ", Bitmap = " + path);
                Intent intent = new Intent();
                // 图片路径
                intent.putExtra("framePicPath", path);
                // 视频路径
                intent.putExtra("videoPath", url);
                //录制时间
                intent.putExtra("videoDuration", jCameraView.getRecordedTime());
                //录制大小
                intent.putExtra("videoSize", new File(url).length());
                setResult(102, intent);
                finish();
            }
        });

        jCameraView.setLeftClickListener(() -> CameraActivity.this.finish());

        L.i("Camera", DeviceUtil.getDeviceModel());
    }

    @Override
    protected void onStart() {
        super.onStart();
        //全屏显示
        if (Build.VERSION.SDK_INT >= 19) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        } else {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(option);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        jCameraView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        jCameraView.onPause();
    }

    private Bitmap createWaterBitmap(Bitmap resource) {
        int w = resource.getWidth();
        int h = resource.getHeight();
        Bitmap temp = Bitmap.createBitmap(w, h, Config.ARGB_8888);
        Canvas canvas = new Canvas(temp);
        Paint paint = new Paint();
        String familyName = "宋体";
        Typeface font = Typeface.create(familyName, Typeface.NORMAL);
        paint.setColor(Color.WHITE);
        paint.setTypeface(font);
        paint.setTextSize(DensityUtil.sp2px(ContextHelper.getContext(), 6));
        canvas.drawBitmap(resource, 0, 0, paint);
        String waterText = ContextHelper.getContext()
            .getString(R.string.pic_water_content, UserInfoRepository.getUserId(),
                StringUtils.getWaterMarkTime());
        paint.setAlpha(100);
        canvas.drawText(waterText, 20, h - 20, paint);
        canvas.save(Canvas.ALL_SAVE_FLAG);
        canvas.restore();
        return temp;
    }
}
