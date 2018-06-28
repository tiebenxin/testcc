package com.lensim.fingerchat.fingerchat.ui.settings.clear_cache;

import android.os.Bundle;
import android.os.Environment;
import android.text.format.Formatter;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.lensim.fingerchat.commons.base.BaseActivity;
import com.lensim.fingerchat.commons.dialog.NiftyDialogBuilder;
import com.lensim.fingerchat.commons.toolbar.FGToolbar;
import com.lensim.fingerchat.commons.utils.FileUtil;
import com.lensim.fingerchat.commons.utils.T;
import com.lensim.fingerchat.fingerchat.R;

import java.io.File;


/**
 * Created by LY309313 on 2016/11/15.
 */

public class CacheOptionActivity extends BaseActivity {

    @Override
    public void initView() {
        if (isFinishing()) {
            return;
        }

        setContentView(R.layout.activity_cache_option);
        FGToolbar toolbar = findViewById(R.id.fg_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleText("存储空间");
        initBackButton(toolbar, true);
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                .add(R.id.fragment_container, new CacheOptionFragment()).commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_cache, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            backPressed();
            return true;
        }
        String fingerChat = Environment.getExternalStorageDirectory()
            + File.separator + "fingerChat";
        final File file = new File(fingerChat);

        final File cacheDir = new File(
            Environment.getExternalStorageDirectory() + "/cn.itguy.recordvideodemo/Media");
        if (!file.exists() && !cacheDir.exists()) {
            T.show("没有旧文件");
        } else {
            long fingerSize = FileUtil.getDirSize(file);
            final long videoSize = FileUtil.getDirSize(cacheDir);
            String size = Formatter.formatFileSize(this, fingerSize + videoSize);
            final NiftyDialogBuilder builder = new NiftyDialogBuilder(this);
            builder.withTitle("提示")
                .withMessage("旧文件大小:" + size)
                .withDuration(300)
                .withButton1Text("取消")
                .withButton2Text("删除")
                .setButton1Click(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        builder.dismiss();
                    }
                })
                .setButton2Click(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        builder.dismiss();
                        FileUtil.DeleteFolder(file.getAbsolutePath());
                        FileUtil.DeleteFolder(cacheDir.getAbsolutePath());
                        T.show("删除成功");
                    }
                }).show();
        }
        return super.onOptionsItemSelected(item);
    }

}
