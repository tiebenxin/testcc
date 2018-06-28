package com.lensim.fingerchat.fingerchat.ui.code;


import static com.lensim.fingerchat.commons.app.AppConfig.REQUEST_IMAGE;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.Toast;
import com.lensim.fingerchat.commons.base.BaseActivity;
import com.lensim.fingerchat.commons.toolbar.FGToolbar;
import com.lensim.fingerchat.data.bean.ImageBean;
import com.lensim.fingerchat.fingerchat.R;
import com.lens.chatmodel.ui.image.MultiImageSelectorActivity;
import com.uuzuche.lib_zxing.activity.CaptureFragment;
import com.uuzuche.lib_zxing.activity.CodeUtils;
import java.util.List;

/**
 * Created by LY309313 on 2016/9/2.
 */

public class QRCodeScanActivity extends BaseActivity {

    private FGToolbar toolbar;

    @Override
    public void initView() {
        setContentView(R.layout.activity_qrcodescan);
        toolbar = findViewById(R.id.viewTitleBar);
        initTitle();
        CaptureFragment captureFragment = new CaptureFragment();
        // 为二维码扫描界面设置定制化界面
        CodeUtils.setFragmentArgs(captureFragment, R.layout.custom_scan);

        captureFragment.setAnalyzeCallback(analyzeCallback);
        /**
         * 替换我们的扫描控件
         */
        getSupportFragmentManager().beginTransaction()
            .replace(R.id.mQRCodeScanContainer, captureFragment).commit();
    }

    private void initTitle() {
//        setToolBar(toolbar);
        toolbar.setTitleText(R.string.scan);
        initBackButton(toolbar, true);
    }


    /**
     * 二维码解析回调函数
     */
    CodeUtils.AnalyzeCallback analyzeCallback = new CodeUtils.AnalyzeCallback() {
        @Override
        public void onAnalyzeSuccess(Bitmap mBitmap, String result) {
            Intent resultIntent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putInt(CodeUtils.RESULT_TYPE, CodeUtils.RESULT_SUCCESS);
            bundle.putString(CodeUtils.RESULT_STRING, result);
            resultIntent.putExtras(bundle);
            QRCodeScanActivity.this.setResult(RESULT_OK, resultIntent);
            QRCodeScanActivity.this.finish();
        }

        @Override
        public void onAnalyzeFailed() {
            Intent resultIntent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putInt(CodeUtils.RESULT_TYPE, CodeUtils.RESULT_FAILED);
            bundle.putString(CodeUtils.RESULT_STRING, "");
            resultIntent.putExtras(bundle);
            QRCodeScanActivity.this.setResult(RESULT_OK, resultIntent);
            QRCodeScanActivity.this.finish();
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == RESULT_OK) {
                // 获取返回的图片列表
                final List<ImageBean> path = data
                    .getParcelableArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
                // 批量上传，全部上传完成
                if (path != null && path.size() > 0) {
                    ImageBean bean = path.get(0);
                    CodeUtils.analyzeBitmap(bean.path, new CodeUtils.AnalyzeCallback() {
                        @Override
                        public void onAnalyzeSuccess(Bitmap mBitmap, String result) {
//                            Toast.makeText(QRCodeScanActivity.this, "解析结果:" + result, Toast.LENGTH_LONG).show();
                            Intent resultIntent = new Intent();
                            Bundle bundle = new Bundle();
                            bundle.putInt(CodeUtils.RESULT_TYPE, CodeUtils.RESULT_SUCCESS);
                            bundle.putString(CodeUtils.RESULT_STRING, result);
                            resultIntent.putExtras(bundle);
                            QRCodeScanActivity.this.setResult(RESULT_OK, resultIntent);
                            QRCodeScanActivity.this.finish();
                        }

                        @Override
                        public void onAnalyzeFailed() {
                            Toast.makeText(QRCodeScanActivity.this, "解析二维码失败", Toast.LENGTH_SHORT)
                                .show();
                            Intent resultIntent = new Intent();
                            Bundle bundle = new Bundle();
                            bundle.putInt(CodeUtils.RESULT_TYPE, CodeUtils.RESULT_FAILED);
                            bundle.putString(CodeUtils.RESULT_STRING, "");
                            resultIntent.putExtras(bundle);
                            QRCodeScanActivity.this.setResult(RESULT_OK, resultIntent);
                            QRCodeScanActivity.this.finish();
                        }
                    });
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_scan, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_selectpic:
                Intent intent = new Intent(this, MultiImageSelectorActivity.class);
                intent.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, false);
                //intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, 9);
                intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE,
                    MultiImageSelectorActivity.MODE_SINGLE);
                startActivityForResult(intent, REQUEST_IMAGE);
                break;

        }
        return super.onOptionsItemSelected(item);
    }
}
