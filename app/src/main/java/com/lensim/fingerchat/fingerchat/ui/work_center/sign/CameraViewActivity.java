/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.lensim.fingerchat.fingerchat.ui.work_center.sign;


import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.bumptech.glide.Glide;
import com.google.android.cameraview.CameraView;
import com.lensim.fingerchat.commons.global.Route;
import com.lensim.fingerchat.commons.map.BaiduSDK;
import com.lensim.fingerchat.commons.map.service.LocationService;
import com.lensim.fingerchat.commons.utils.ImageUtil;
import com.lensim.fingerchat.commons.utils.StringUtils;
import com.lensim.fingerchat.commons.utils.TDevice;
import com.lensim.fingerchat.commons.utils.TimeUtils;
import com.lensim.fingerchat.data.login.UserInfoRepository;
import com.lensim.fingerchat.fingerchat.R;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * This demo app saves the taken picture to a constant file.
 * $ adb pull /sdcard/Android/data/com.google.android.cameraview.demo/files/Pictures/picture.jpg
 */
public class CameraViewActivity extends AppCompatActivity implements
    ActivityCompat.OnRequestPermissionsResultCallback {

    public static final String EXTRA_RESULT = "select_result";
    public static final String PARAMS_ADDRESS = "address";
    private static final int REQUEST_CAMERA_PERMISSION = 1;
    private static final String FRAGMENT_DIALOG = "dialog";
    private static final int[] FLASH_OPTIONS = {
        CameraView.FLASH_AUTO,
        CameraView.FLASH_OFF,
        CameraView.FLASH_ON,
    };
    private static final int[] FLASH_ICONS = {
        R.drawable.ic_flash_auto,
        R.drawable.ic_flash_off,
        R.drawable.ic_flash_on,
    };
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
    private boolean isLocateFinished = true;//上一界面是否定位成功
    private LocationService locationService;
    private BDLocationListener mMapListener = new BDLocationListener() {
        @Override
        public void onReceiveLocation(BDLocation location) {
            if (null != location && location.getLocType() != BDLocation.TypeServerError) {
                if (null != tv_user_location && !StringUtils.isEmpty(location.getAddrStr())) {
                    tv_user_location.setText(location.getAddrStr());
                }
            }
        }
    };
    private String originalName, imgPath;
    private int mCurrentFlash;
    private ProgressBar mProgress;
    private CameraView mCameraView;
    private TextView tv_user_name, tv_time, tv_date, tv_user_location, tv_re_take_pic, tv_use_pic;
    private ImageView cameraFlash, imgUserAvatar;
    private LinearLayout mLLFlashCamera, mLLPaintAreaBottom, mLLPaintAreaTop;
    private ImageView imgCamera;
    private Button btn_take_picture;
    private boolean isPictureTaken = false;
    private CameraView.Callback mCallback = new CameraView.Callback() {

        @Override
        public void onCameraOpened(CameraView cameraView) {
            super.onCameraOpened(cameraView);
            mCameraView.setAutoFocus(true);
        }

        @Override
        public void onCameraClosed(CameraView cameraView) {
            super.onCameraClosed(cameraView);
        }

        @Override
        public void onPictureTaken(CameraView cameraView, final byte[] data) {
            super.onPictureTaken(cameraView, data);
            Observable.create(new ObservableOnSubscribe<String>() {
                @Override
                public void subscribe(@io.reactivex.annotations.NonNull ObservableEmitter<String> e)
                    throws Exception {
                    File file = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                        getDateName());
                    imgPath = file.getAbsolutePath();
                    OutputStream os = null;
                    try {
                        os = new FileOutputStream(file);
                        os.write(data);
                        os.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    } finally {
                        if (os != null) {
                            try {
                                os.close();
                            } catch (IOException e2) {
                                e2.printStackTrace();
                            }
                            e.onNext("");
                        }
                    }
                }
            }).subscribeOn(Schedulers.io()).observeOn(
                AndroidSchedulers.mainThread()).subscribe(new Consumer<String>() {
                @Override
                public void accept(@io.reactivex.annotations.NonNull String favJsons)
                    throws Exception {
                    afterTaken();
                }
            });
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_view);

        initView();
        initListener();
        if (mCameraView != null) {
            mCameraView.addCallback(mCallback);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
        @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CAMERA_PERMISSION:
                if (permissions.length != 1 || grantResults.length != 1) {
                    throw new RuntimeException("Error on requesting camera permission.");
                }
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, R.string.camera_permission_not_granted,
                        Toast.LENGTH_SHORT).show();
                }
                // No need to start camera here; it is handled by onResume
                break;
        }
    }

    private void initView() {
        mProgress = (ProgressBar) findViewById(R.id.loading_clock);
        mCameraView = (CameraView) findViewById(R.id.camera);
        tv_user_name = (TextView) findViewById(R.id.tv_user_name_camera);
        tv_re_take_pic = (TextView) findViewById(R.id.tv_re_take_pic);
        tv_use_pic = (TextView) findViewById(R.id.tv_use_pic);
        tv_time = (TextView) findViewById(R.id.tv_time);
        tv_date = (TextView) findViewById(R.id.tv_date);
        tv_user_location = (TextView) findViewById(R.id.tv_location);
        cameraFlash = (ImageView) findViewById(R.id.camera_flash_lamp);
        imgUserAvatar = (ImageView) findViewById(R.id.img_user_avatar);
        imgCamera = (ImageView) findViewById(R.id.img_camera_switcher);
        btn_take_picture = (Button) findViewById(R.id.btn_take_picture);
        mLLFlashCamera = (LinearLayout) findViewById(R.id.ll_flash_camera);
        mLLPaintAreaBottom = (LinearLayout) findViewById(R.id.ll_paint_area_left_bottom);
        mLLPaintAreaTop = (LinearLayout) findViewById(R.id.ll_paint_area_right_top);

        tv_user_name.setText(UserInfoRepository.getInstance().getUserInfo().getUsernick());
        if (null != getIntent()) {
            if (!StringUtils.isEmpty(getIntent().getStringExtra(PARAMS_ADDRESS))) {
                tv_user_location.setText(getIntent().getStringExtra(PARAMS_ADDRESS));
            } else {
                isLocateFinished = false;
            }
        }
        tv_time.setText(TimeUtils.getTime());
        tv_date.setText(TimeUtils.getDateNoTime());

        Glide.with(this).load(
            Route.getAvatarPath(UserInfoRepository.getInstance().getUserInfo().getUserid()))
            .override(80, 80).into(imgUserAvatar);
    }

    private void initListener() {
        cameraFlash.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCameraView != null) {
                    mCurrentFlash = (mCurrentFlash + 1) % FLASH_OPTIONS.length;
                    cameraFlash.setImageResource(FLASH_ICONS[mCurrentFlash]);
                    mCameraView.setFlash(FLASH_OPTIONS[mCurrentFlash]);
                }
            }
        });
        imgCamera.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCameraView != null) {
                    int facing = mCameraView.getFacing();
                    mCameraView.setFacing(facing == CameraView.FACING_FRONT ?
                        CameraView.FACING_BACK : CameraView.FACING_FRONT);
                }
            }
        });
        btn_take_picture.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCameraView != null) {
                    mCameraView.takePicture();
                }
            }
        });
        tv_use_pic.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPictureTaken) {
                    showDataSync();
                } else {
                    finish();
                }
            }
        });
        tv_re_take_pic.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPictureTaken) {
                    reTakePicture();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!isLocateFinished) {
            locationService = BaiduSDK.getLocationService();
            locationService.registerListener(mMapListener);
            locationService.setLocationOption(locationService.getDefaultLocationClientOption());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED) {
            mCameraView.start();
        } else if (ActivityCompat
            .shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
            ConfirmationDialogFragment.newInstance(R.string.camera_permission_confirmation,
                new String[]{Manifest.permission.CAMERA},
                REQUEST_CAMERA_PERMISSION, R.string.camera_permission_not_granted)
                .show(getSupportFragmentManager(), FRAGMENT_DIALOG);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                REQUEST_CAMERA_PERMISSION);
        }
        if (!isLocateFinished) {
            locationService.start();
        }
    }

    @Override
    protected void onPause() {
        mCameraView.stop();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!isLocateFinished) {
            locationService.stop();
            locationService.unregisterListener(mMapListener);
        }
    }

    private void reTakePicture() {
        isPictureTaken = false;
        tv_use_pic.setText("   取消  ");
        tv_re_take_pic.setVisibility(View.GONE);
        mLLFlashCamera.setVisibility(View.VISIBLE);
        mLLPaintAreaBottom.setVisibility(View.VISIBLE);
        mLLPaintAreaTop.setVisibility(View.VISIBLE);
        mCameraView.start();
    }


    private void afterTaken() {
        isPictureTaken = true;
        tv_use_pic.setText("使用照片");
        tv_re_take_pic.setVisibility(View.VISIBLE);
        mLLFlashCamera.setVisibility(View.GONE);
        mLLPaintAreaBottom.setVisibility(View.GONE);
        mLLPaintAreaTop.setVisibility(View.GONE);
        mCameraView.stop();
    }

    private String getDateName() {
        return sdf.format(new Date()) + ".jpg";
    }

    /**
     * 异步方式显示数据
     */
    private void showDataSync() {
        mProgress.setVisibility(View.VISIBLE);
        Observable.create(new ObservableOnSubscribe<Bitmap>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Bitmap> e) throws Exception {
                if (StringUtils.isEmpty(imgPath)) {
                    e.onComplete();
                } else {
                    originalName = imgPath;
                    Bitmap srcBm = BitmapFactory.decodeFile(imgPath);

                    Bitmap waterBitmap = createViewBitmap(mLLPaintAreaBottom);
                    double ratioHW = (double) waterBitmap.getHeight() / waterBitmap.getWidth();
                    double ratioWidth = (double) waterBitmap.getWidth() / TDevice.getScreenWidth();
                    ratioWidth = Math.max(ratioWidth, 0.25);
                    waterBitmap = ImageUtil.scaleWithWH(waterBitmap, srcBm.getWidth() * ratioWidth,
                        srcBm.getWidth() * ratioWidth * ratioHW);
                    srcBm = ImageUtil
                        .createWaterMaskLeftBottom(CameraViewActivity.this, srcBm, waterBitmap, 20,
                            20);

                    waterBitmap = createViewBitmap(mLLPaintAreaTop);
                    ratioHW = (double) waterBitmap.getHeight() / waterBitmap.getWidth();
                    ratioWidth = (double) waterBitmap.getWidth() / TDevice.getScreenWidth();
                    ratioWidth = Math.max(ratioWidth, 0.25);
                    waterBitmap = ImageUtil.scaleWithWH(waterBitmap, srcBm.getWidth() * ratioWidth,
                        srcBm.getWidth() * ratioWidth * ratioHW);
                    srcBm = ImageUtil
                        .createWaterMaskRightTop(CameraViewActivity.this, srcBm, waterBitmap, 20,
                            20);

                    e.onNext(srcBm);
                    e.onComplete();
                }
            }
        }).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Observer<Bitmap>() {
                @Override
                public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

                }

                @Override
                public void onNext(@io.reactivex.annotations.NonNull Bitmap bitmap) {
                    imgPath = saveMyBitmap(bitmap, getDateName());
                    bitmap.recycle();
                }

                @Override
                public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                    mProgress.setVisibility(View.GONE);
                }

                @Override
                public void onComplete() {
                    mProgress.setVisibility(View.GONE);
                    if (!StringUtils.isEmpty(originalName)) {
                        File file = new File(originalName);
                        if (file.exists()) {
                            file.delete();
                        }
                    }
                    Intent data = new Intent();
                    if (!StringUtils.isEmpty(imgPath)) {
                        data.putExtra(EXTRA_RESULT, imgPath);
                    }
                    setResult(RESULT_OK, data);
                    finish();
                }
            });
    }


    @Override
    public void onBackPressed() {
        if (isPictureTaken) {
            showDataSync();
        } else {
            finish();
        }
    }


    public Bitmap createViewBitmap(View v) {
        Bitmap bitmap = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        v.draw(canvas);
        return bitmap;
    }

    public String saveMyBitmap(Bitmap mBitmap, String name) {
        File file = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), name);
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        mBitmap.compress(Bitmap.CompressFormat.JPEG, 60, fOut);
        try {
            fOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file.getAbsolutePath();
    }


    public static class ConfirmationDialogFragment extends DialogFragment {

        private static final String ARG_MESSAGE = "message";
        private static final String ARG_PERMISSIONS = "permissions";
        private static final String ARG_REQUEST_CODE = "request_code";
        private static final String ARG_NOT_GRANTED_MESSAGE = "not_granted_message";

        public static ConfirmationDialogFragment newInstance(@StringRes int message,
            String[] permissions, int requestCode, @StringRes int notGrantedMessage) {
            ConfirmationDialogFragment fragment = new ConfirmationDialogFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_MESSAGE, message);
            args.putStringArray(ARG_PERMISSIONS, permissions);
            args.putInt(ARG_REQUEST_CODE, requestCode);
            args.putInt(ARG_NOT_GRANTED_MESSAGE, notGrantedMessage);
            fragment.setArguments(args);
            return fragment;
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Bundle args = getArguments();
            return new AlertDialog.Builder(getActivity())
                .setMessage(args.getInt(ARG_MESSAGE))
                .setPositiveButton(android.R.string.ok,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String[] permissions = args.getStringArray(ARG_PERMISSIONS);
                            if (permissions == null) {
                                throw new IllegalArgumentException();
                            }
                            ActivityCompat.requestPermissions(getActivity(),
                                permissions, args.getInt(ARG_REQUEST_CODE));
                        }
                    })
                .setNegativeButton(android.R.string.cancel,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(getActivity(),
                                args.getInt(ARG_NOT_GRANTED_MESSAGE),
                                Toast.LENGTH_SHORT).show();
                        }
                    })
                .create();
        }
    }

}
